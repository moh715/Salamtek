package com.example.salamtek1;
 import com.example.salamtek1.Reportable;
import javax.xml.stream.Location;
 import java.time.LocalDateTime;
  import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.*;



enum AccidentStatus { REPORTED, OFFICER_ASSIGNED, UNDER_INVESTIGATION, COMPLETED, PAID }

// Implements Reportable interface
class Accident implements Reportable {
    private String accidentId, reporterNationalId, otherPartyNationalId;
    private String reporterLicensePlate, otherPartyLicensePlate;
    private Location accidentLocation;
    private LocalDateTime reportTime = LocalDateTime.now();
    private AccidentStatus status = AccidentStatus.REPORTED;
    private String assignedOfficerId, atFaultPartyId, notes = "";
    // Changed List to ArrayList
    private ArrayList<String> damagedParts = new ArrayList<>();
    private double totalCost = 0.0;

    public Accident(String accidentId, String reporterNationalId, String otherPartyNationalId,
                    String reporterLicensePlate, String otherPartyLicensePlate, Location accidentLocation) {
        this.accidentId = accidentId;
        this.reporterNationalId = reporterNationalId;
          this.otherPartyNationalId = otherPartyNationalId;
        this.reporterLicensePlate = reporterLicensePlate;
        this.otherPartyLicensePlate = otherPartyLicensePlate;
        this.accidentLocation = accidentLocation;
    }

    // Interface Implementation for generating report text
    @Override
    public String generateReportDetails() {
return "Accident ID: " + accidentId + "\n" +
        "Date: " + getFormattedReportTime() + "\n" +
      "Status: " + status + "\n" +
       "Total Cost: " + totalCost + " EGP\n" +
    "Fault Party: " + atFaultPartyId;
}

       public String getAccidentId() { return accidentId; }
public String getReporterNationalId() { return reporterNationalId; }
   public String getOtherPartyNationalId() { return otherPartyNationalId; }
      public String getReporterLicensePlate() { return reporterLicensePlate; }
     public String getOtherPartyLicensePlate() { return otherPartyLicensePlate; }
    public Location getAccidentLocation() { return accidentLocation; }
    public AccidentStatus getStatus() { return status; }
    public String getAssignedOfficerId() { return assignedOfficerId; }
public String getAtFaultPartyId() { return atFaultPartyId; }
    // changed return type to arrayList
    public ArrayList<String> getDamagedParts() { return damagedParts; }
    public double getTotalCost() { return totalCost; }
    public String getNotes() { return notes; }

    public void setStatus(AccidentStatus status) { this.status = status; }
     public void setAssignedOfficerId(String officerId) { this.assignedOfficerId = officerId; }
     public void setAtFaultPartyId(String atFaultPartyId) { this.atFaultPartyId = atFaultPartyId; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
 public void setNotes(String notes) { this.notes = notes; }
    public void addDamagedPart(String partName) { damagedParts.add(partName); }

    public String getFormattedReportTime() {
        return reportTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
