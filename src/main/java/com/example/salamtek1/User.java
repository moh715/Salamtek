package com.example.salamtek1;

import com.example.salamtek1.Person;
import com.example.salamtek1.SalamtekException;

import java.util.ArrayList;

class User extends Person {
    private String nationalId;
    private String phoneNumber;
    private ArrayList<String> accidentIds = new ArrayList<>();

    public User(String nationalId, String name, String phoneNumber, String email, String password) throws SalamtekException {
        super(name, email, password);
        if (nationalId.length() != 14) {
            throw new SalamtekException("National ID must be exactly 14 digits.");
        }

        this.nationalId = nationalId;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getRole() {
        return "Citizen";
    } // Polymorphism implementation

    public String getNationalId() {
        return nationalId;
    }
    public String getPhoneNumber() {
        return phoneNumber; 
    }
    public ArrayList<String> getAccidentIds() {
        return accidentIds;
    }
    public void addAccidentId(String accidentId) {
        accidentIds.add(accidentId);
    }
}
