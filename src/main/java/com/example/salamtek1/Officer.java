package com.example.salamtek1;
import com.example.salamtek1.Person;
import java.util.ArrayList;

// officer extends Person (Inheritance)
class Officer extends Person implements Validatable {
    private String officerId;
    private String hubId;
    private boolean isAvailable;
    private ArrayList<String> assignedAccidentIds;

    public Officer(String officerId, String name, String email, String password, String hubId) {
        super(name, email, password);
        this.officerId = officerId;
        this.hubId = hubId;
        this.isAvailable = true;
        this.assignedAccidentIds = new ArrayList<>();
    }

    // Polymorphism - different implementation of abstract method
    @Override
    public String getRole() {
        return "OFFICER";
    }

    @Override
    public String getIdentifier() {
        return email; // Officers use email, Users use national ID
    }

    // Method overriding - officers authenticate with email
    @Override
    public boolean authenticate(String credential, String password) {
        return this.email.equals(credential) && this.password.equals(password);
    }

    @Override
    public void validate() throws ValidationException {
        if (officerId == null || officerId.trim().isEmpty()) {
            throw new ValidationException("Officer ID cannot be empty");
        }
        if (email == null || !email.endsWith("@police.gov.eg")) {
            throw new ValidationException("Officer email must be from police.gov.eg domain");
        }
        if (hubId == null || hubId.trim().isEmpty()) {
            throw new ValidationException("Officer must be assigned to a hub");
        }
    }

    @Override
    public String toFileFormat() {
        return String.format("OFFICER|%s|%s|%s|%s|%s|%b",
                officerId, name, email, password, hubId, isAvailable);
    }

    public String getOfficerId() { return officerId; }
    public String getHubId() { return hubId; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { this.isAvailable = available; }
    public ArrayList<String> getAssignedAccidentIds() { return assignedAccidentIds; }
    public void assignAccident(String accidentId) { this.assignedAccidentIds.add(accidentId); }
}
