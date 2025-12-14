package com.example.salamtek1;

import java.util.ArrayList;

class User extends Person implements Validatable {
    private String nationalId;        // Private = strong encapsulation
    private String phoneNumber;
    private ArrayList<String> accidentIds; // Composition - User HAS-A List

    public User(String nationalId, String name, String phoneNumber, String email, String password) {
        super(name, email, password); // Call parent constructor
        this.nationalId = nationalId;
        this.phoneNumber = phoneNumber;
        this.accidentIds = new ArrayList<>();
    }

    // Implementing abstract method from Person (Polymorphism)
    @Override
    public String getRole() {
        return "USER";
    }

    // Implementing interface method (Polymorphism)
    @Override
    public String getIdentifier() {
        return nationalId;
    }

    // Method overriding - specialized authentication for users (Polymorphism)
    @Override
    public boolean authenticate(String credential, String password) {
        // Users authenticate with national ID, not email
        return this.nationalId.equals(credential) && this.password.equals(password);
    }

    // Input Validation - ensures data integrity
    @Override
    public void validate() throws ValidationException {
        if (nationalId == null || nationalId.length() != 14) {
            throw new ValidationException("National ID must be exactly 14 digits");
        }
        if (!nationalId.matches("\\d+")) {
            throw new ValidationException("National ID must contain only digits");
        }
        if (phoneNumber == null || !phoneNumber.matches("^01[0-9]{9}$")) {
            throw new ValidationException("Phone number must be 11 digits starting with 01");
        }
        if (email == null || !email.contains("@")) {
            throw new ValidationException("Invalid email format");
        }
        if (password == null || password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }
    }

    // File I/O - convert to string format for saving
    @Override
    public String toFileFormat() {
        return String.format("USER|%s|%s|%s|%s|%s",
                nationalId, name, phoneNumber, email, password);
    }

    // Encapsulation - controlled access to private fields
    public String getNationalId() { return nationalId; }
    public String getPhoneNumber() { return phoneNumber; }
    public ArrayList<String> getAccidentIds() { return new ArrayList<>(accidentIds); } // Return copy for safety
    public void addAccidentId(String accidentId) { this.accidentIds.add(accidentId); }
}
