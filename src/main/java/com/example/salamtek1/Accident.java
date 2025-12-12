package com.example.salamtek1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

enum AccidentStatus { REPORTED, OFFICER_ASSIGNED, UNDER_INVESTIGATION, COMPLETED, PAID }

class Accident {
    private String accidentId, reporterNationalId, otherPartyNationalId;
    private String reporterLicensePlate, otherPartyLicensePlate;
    private Location accidentLocation;
    private LocalDateTime reportTime = LocalDateTime.now();
    private AccidentStatus status = AccidentStatus.REPORTED;
    private String assignedOfficerId, atFaultPartyId, notes = "";
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

    public String getAccidentId() { return accidentId; }
    public String getReporterNationalId() { return reporterNationalId; }
    public String getOtherPartyNationalId() { return otherPartyNationalId; }
    public String getReporterLicensePlate() { return reporterLicensePlate; }
    public String getOtherPartyLicensePlate() { return otherPartyLicensePlate; }
    public Location getAccidentLocation() { return accidentLocation; }
    public AccidentStatus getStatus() { return status; }
    public String getAssignedOfficerId() { return assignedOfficerId; }
    public String getAtFaultPartyId() { return atFaultPartyId; }
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