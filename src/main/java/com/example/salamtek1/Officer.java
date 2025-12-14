package com.example.salamtek1;
import com.example.salamtek1.Person;
import java.util.ArrayList;

// officer extends Person (Inheritance)
class Officer extends Person {
    private String officerId;
    private String hubId;
    private boolean isAvailable = true;
    // Changed List to arraylist
    private ArrayList<String> assignedAccidentIds = new ArrayList<>();

    public Officer(String officerId, String name, String email, String password, String hubId) {
        super(name, email, password);
        this.officerId = officerId;
        this.hubId = hubId;
    }
@Override
    public String getRole() { return "Police Officer"; } // Polymorphism implementation
public String getOfficerId() {
        return officerId;
    }
    public String getHubId() {
        return hubId;
    }
    public boolean isAvailable() { 
        return isAvailable;
    }
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
    public void assignAccident(String accidentId) {
    assignedAccidentIds.add(accidentId);
    }

}
