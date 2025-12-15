package com.example.salamtek1;
 import java.time.LocalDateTime;
  import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.*;



enum AccidentStatus { REPORTED, OFFICER_ASSIGNED, UNDER_INVESTIGATION, COMPLETED, PAID }

// Implements Reportable interface
class Accident implements Persistable, Validatable {
    private String accidentId;
    private String reporterNationalId, otherPartyNationalId;
    private String reporterLicensePlate, otherPartyLicensePlate;
    private Location accidentLocation;  // Composition - Accident HAS-A Location
    private LocalDateTime reportTime;
    private AccidentStatus status;
    private String assignedOfficerId;
    private String atFaultPartyId;
    private List<String> damagedParts;
    private double totalCost;
    private String notes;
    private static int idCounter = 1;

    public Accident(String accidentId, String reporterNationalId, String otherPartyNationalId,
                    String reporterLicensePlate, String otherPartyLicensePlate, Location accidentLocation) {
        this.accidentId = accidentId;
        this.reporterNationalId = reporterNationalId;
        this.otherPartyNationalId = otherPartyNationalId;
        this.reporterLicensePlate = reporterLicensePlate;
        this.otherPartyLicensePlate = otherPartyLicensePlate;
        this.accidentLocation = accidentLocation;
        this.reportTime = LocalDateTime.now();
        this.status = AccidentStatus.REPORTED;
        this.damagedParts = new ArrayList<>();
        this.totalCost = 0.0;
        this.notes = "";
    }

    public Accident(String reporterNationalId, String otherPartyNationalId,
                    String reporterLicensePlate, String otherPartyLicensePlate, Location accidentLocation) {
        this("ACC" + String.format("%04d",idCounter++), reporterNationalId, otherPartyNationalId,
                reporterLicensePlate, otherPartyLicensePlate, accidentLocation);
    }

    @Override
    public void validate() throws ValidationException {
        if (reporterNationalId == null || reporterNationalId.equals(otherPartyNationalId)) {
            throw new ValidationException("Reporter and other party must be different people");
        }
        if (reporterLicensePlate == null || reporterLicensePlate.equals(otherPartyLicensePlate)) {
            throw new ValidationException("Both vehicles must have different license plates");
        }
    }

    // Getters with encapsulation
    public String getAccidentId() { return accidentId; }
    public String getReporterNationalId() { return reporterNationalId; }
    public String getOtherPartyNationalId() { return otherPartyNationalId; }
    public String getReporterLicensePlate() { return reporterLicensePlate; }
    public String getOtherPartyLicensePlate() { return otherPartyLicensePlate; }
    public Location getAccidentLocation() { return accidentLocation; }
    public LocalDateTime getReportTime() { return reportTime; }
    public AccidentStatus getStatus() { return status; }
    public String getAssignedOfficerId() { return assignedOfficerId; }
    public String getAtFaultPartyId() { return atFaultPartyId; }
    public List<String> getDamagedParts() { return damagedParts; }
    public double getTotalCost() { return totalCost; }
    public String getNotes() { return notes; }

    // Setters with encapsulation
    public void setStatus(AccidentStatus status) { this.status = status; }
    public void setAssignedOfficerId(String officerId) { this.assignedOfficerId = officerId; }
    public void setAtFaultPartyId(String atFaultPartyId) { this.atFaultPartyId = atFaultPartyId; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public void setNotes(String notes) { this.notes = notes; }
    public void addDamagedPart(String partName) { this.damagedParts.add(partName); }

    public String getFormattedReportTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return reportTime.format(formatter);
    }

    @Override
    public String toFileFormat() {
        return String.format("ACCIDENT|%s|%s|%s|%s|%s|%s|%s|%s",
                accidentId, reporterNationalId, otherPartyNationalId,
                reporterLicensePlate, otherPartyLicensePlate,
                accidentLocation.toFileFormat(), status, totalCost);
    }
    public String generateReportDetails(DatabaseManager db) {
        StringBuilder report = new StringBuilder();
        report.append("Accident ID: ").append(accidentId).append("\n");
        report.append("Report Date: ").append(getFormattedReportTime()).append("\n");
        report.append("Status: ").append(status).append("\n");
        report.append("Location: ").append(accidentLocation).append("\n\n");

        try {
            User reporter = db.getUser(reporterNationalId);
            User otherParty = db.getUser(otherPartyNationalId);

            report.append("Party 1 (Reporter): ").append(reporter.getName()).append("\n");
            report.append("  National ID: ").append(reporterNationalId).append("\n");
            report.append("  License Plate: ").append(reporterLicensePlate).append("\n\n");

            report.append("Party 2: ").append(otherParty.getName()).append("\n");
            report.append("  National ID: ").append(otherPartyNationalId).append("\n");
            report.append("  License Plate: ").append(otherPartyLicensePlate).append("\n\n");

            if (assignedOfficerId != null) {
                Officer officer = db.getOfficer(assignedOfficerId);
                report.append("Assigned Officer: ").append(officer.getName()).append("\n");
            }

            if (status == AccidentStatus.COMPLETED || status == AccidentStatus.PAID) {
                report.append("\n--- Investigation Results ---\n");
                String atFaultName;
                if(atFaultPartyId.equals(reporterNationalId)) {
                    atFaultName = reporter.getName();
                }
                else {
                    atFaultName = otherParty.getName();
                }
                report.append("At Fault: ").append(atFaultName).append("\n");
                report.append("Total Cost: ").append(String.format("%.2f EGP", totalCost)).append("\n");

                if (!damagedParts.isEmpty()) {
                    report.append("Damaged Parts: ").append(String.join(", ", damagedParts)).append("\n");
                }

                if (!notes.isEmpty()) {
                    report.append("Officer's Notes: ").append(notes).append("\n");
                }
            }
        } catch (IllegalArgumentException e) {
            report.append("Error generating full report: ").append(e.getMessage()).append("\n");
        }

        return report.toString();
    }
}
