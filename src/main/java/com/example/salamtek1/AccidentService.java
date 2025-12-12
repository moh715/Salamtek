package com.example.salamtek1;
import com.example.salamtek1.DatabaseManager;
import java.util.ArrayList;

class AccidentService {
    private DatabaseManager db = DatabaseManager.getInstance();
public String reportAccident(String reporterNationalId, String otherPartyNationalId,
                                 String reporterLicensePlate, String otherPartyLicensePlate,
                                 Location accidentLocation) throws SalamtekException {
    
        if (!db.userExists(reporterNationalId)) throw new SalamtekException("Reporter not found");
           if (!db.userExists(otherPartyNationalId)) throw new SalamtekException("Other party not found");
        if (db.getVehicleByLicensePlate(reporterLicensePlate) == null)
            throw new SalamtekException("Reporter's vehicle not found");
        if (db.getVehicleByLicensePlate(otherPartyLicensePlate) == null)
            throw new SalamtekException("Other party's vehicle not found");

        Accident accident = new Accident((String) null, reporterNationalId, otherPartyNationalId,
                reporterLicensePlate, otherPartyLicensePlate, accidentLocation);
        String accidentId = db.addAccident(accident);
        accident = db.getAccident(accidentId);

        Hub nearestHub = db.findNearestHub(accidentLocation);
        if (nearestHub == null) throw new SalamtekException("No hubs available");

        // changed list to arrayList
        ArrayList<Officer> availableOfficers = db.getAvailableOfficersAtHub(nearestHub.getHubId());
        if (availableOfficers.isEmpty()) throw new SalamtekException("No officers available");
Officer officer = availableOfficers.get(0);
        officer.setAvailable(false);
        officer.assignAccident(accidentId);
        accident.setAssignedOfficerId(officer.getOfficerId());
        accident.setStatus(AccidentStatus.OFFICER_ASSIGNED);
        db.updateAccident(accident);
        db.getUser(reporterNationalId).addAccidentId(accidentId);
        db.getUser(otherPartyNationalId).addAccidentId(accidentId);

        return accidentId;
    }

    // changed parameter to arrayList
    public void completeInvestigation(String accidentId, String atFaultPartyId,
                                      ArrayList<String> damagedParts, String notes) {
        Accident accident = db.getAccident(accidentId);

String damagedVehiclePlate, victimNationalId;
        if (atFaultPartyId.equals(accident.getReporterNationalId())) {
        damagedVehiclePlate = accident.getOtherPartyLicensePlate();
            victimNationalId = accident.getOtherPartyNationalId();
        } else {
            damagedVehiclePlate = accident.getReporterLicensePlate();
            victimNationalId = accident.getReporterNationalId();
        }

        Vehicle vehicle = db.getVehicleByLicensePlate(damagedVehiclePlate);
        double totalCost = 0.0;

        for (String partName : damagedParts) {
            VehiclePart part = vehicle.getParts().get(partName);
            if (part != null) {
                totalCost += part.getPrice();
                accident.addDamagedPart(partName);
            }
        }
accident.setAtFaultPartyId(atFaultPartyId);
        accident.setTotalCost(totalCost);
        accident.setNotes(notes);
        accident.setStatus(AccidentStatus.COMPLETED);
        db.updateAccident(accident);
db.getOfficer(accident.getAssignedOfficerId()).setAvailable(true);
     new PaymentService().createPayment(accidentId, atFaultPartyId, victimNationalId, totalCost);
FileHandler.saveReportToFile(accident);
    }

    // Changed return type to arrayList
public ArrayList<Accident> getUserAccidents(String nationalId) {
        return db.getAccidentsByUser(nationalId);
    }

    // Changed return type to ArrayList
    public ArrayList<Accident> getOfficerAccidents(String officerId) {
        return db.getAccidentsByOfficer(officerId);
    }
}
