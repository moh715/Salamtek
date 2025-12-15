package com.example.salamtek1;

import java.time.LocalDateTime;
import java.util.HashMap;


class Vehicle implements Persistable, Validatable {
    private String vehicleId;
    private String make;
    private String model;
    private int year;
    private HashMap<String, VehiclePart> parts; // Composition - Vehicle HAS-A collection of parts

    public Vehicle(String vehicleId, String make, String model, int year) {
        this.vehicleId = vehicleId;
        this.make = make;
        this.model = model;
        this.year = year;
        this.parts = new HashMap<>();
    }

    public void addPart(String partName, double price) {
        parts.put(partName, new VehiclePart(partName, price));
    }

    @Override
    public void validate() throws ValidationException {
        if (make.trim().isEmpty()) {
            throw new ValidationException("Vehicle make cannot be empty");
        }
        if (year < 1900 || year > LocalDateTime.now().getYear() + 1) {
            throw new ValidationException("Invalid vehicle year");
        }
    }

    public String getVehicleId() { return vehicleId; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public HashMap<String, VehiclePart> getParts() { return parts; }

    public String getFullName() {
        return make + " " + model + " " + year;
    }

    @Override
    public String toFileFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("VEHICLE|%s|%s|%s|%d", vehicleId, make, model, year));
        for (VehiclePart part : parts.values()) {
            sb.append("|").append(part.toFileFormat());
        }
        return sb.toString();
    }
}
