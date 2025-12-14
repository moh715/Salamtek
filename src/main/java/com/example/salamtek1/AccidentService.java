package com.example.salamtek1;


import java.util.ArrayList;

class AccidentService {
    private DatabaseManager db = DatabaseManager.getInstance();


    public String reportAccident(String reporterNationalId, String otherPartyNationalId,
                                 String reporterLicensePlate, String otherPartyLicensePlate,
                                 Location accidentLocation)
            throws IllegalArgumentException, ValidationException, NoOfficersAvailableException {

        // Validate inputs and throw specific exceptions
        if (!db.userExists(reporterNationalId)) {
            throw new IllegalArgumentException("User: "+  reporterNationalId+ " not found");
        }
        if (!db.userExists(otherPartyNationalId)) {
            throw new IllegalArgumentException("User: "+ otherPartyNationalId+ " not found");
        }

        // Get vehicles (will throw IllegalArgumentException if not found)
        db.getVehicleByLicensePlate(reporterLicensePlate);
        db.getVehicleByLicensePlate(otherPartyLicensePlate);

        // Create and validate accident
        Accident accident = new Accident(reporterNationalId,otherPartyNationalId,
                reporterLicensePlate, otherPartyLicensePlate, accidentLocation);
        accident.validate();

        String accidentId = db.addAccident(accident);
        accident = db.getAccident(accidentId);


        Hub nearestHub = db.findNearestHub(accidentLocation);
        ArrayList<Officer> availableOfficers = db.getAvailableOfficersAtHub(nearestHub.getHubId());
        if (availableOfficers.isEmpty()) {
            throw new NoOfficersAvailableException(nearestHub.getCityName());
        }
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

    public void completeInvestigation(String accidentId, String atFaultPartyId,
                                      ArrayList<String> damagedParts, String notes)
            throws IllegalArgumentException, ValidationException {

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
    }

    public ArrayList<Accident> getUserAccidents(String nationalId) {
        return db.getAccidentsByUser(nationalId);
    }

    public ArrayList<Accident> getOfficerAccidents(String officerId) {
        return db.getAccidentsByOfficer(officerId);
    }
}

