package com.example.salamtek1;

public interface Authenticatable {
    boolean authenticate(String credential, String password);
    String getIdentifier(); // Returns unique identifier (email or national ID)
}


interface Validatable {
    void validate() throws ValidationException;
}
