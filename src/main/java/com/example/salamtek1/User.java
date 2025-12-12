package com.example.salamtek1;

import java.util.ArrayList;
import java.util.List;

class User {
    private String nationalId, name, phoneNumber, email, password;
    private List<String> accidentIds = new ArrayList<>();

    public User(String nationalId, String name, String phoneNumber, String email, String password) {
        this.nationalId = nationalId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
    }

    public String getNationalId() { return nationalId; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public List<String> getAccidentIds() { return accidentIds; }
    public void addAccidentId(String accidentId) { accidentIds.add(accidentId); }
}
