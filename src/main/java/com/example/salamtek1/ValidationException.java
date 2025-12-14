package com.example.salamtek1;

class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}

class NoOfficersAvailableException extends Exception {
    public NoOfficersAvailableException(String hubName) {
        super("No officers available at hub: " + hubName);
    }
}

class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
