package com.example.salamtek1;

import java.util.ArrayList;
import java.util.List;

class Officer {
    private String officerId, name, email, password, hubId;
    private boolean isAvailable = true;
    private List<String> assignedAccidentIds = new ArrayList<>();

    public Officer(String officerId, String name, String email, String password, String hubId) {
        this.officerId = officerId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.hubId = hubId;
    }

    public String getOfficerId() { 
        return officerId; 
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() { 
        return password;
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
