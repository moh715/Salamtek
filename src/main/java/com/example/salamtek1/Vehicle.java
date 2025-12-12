package com.example.salamtek1;

import java.util.HashMap;


class Vehicle {
    private String vehicleId, make, model;
    private int year;
    private HashMap<String, VehiclePart> parts = new HashMap<>();



    public Vehicle(String vehicleId, String make, String model, int year) {
        this.vehicleId = vehicleId;
        this.make = make;
        this.model = model;
        this.year = year;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void addPart(String partName, double price) {
        parts.put(partName, new VehiclePart(partName, price));
    }

    public HashMap<String, VehiclePart> getParts() { return parts; }
    public String getFullName() { return make + " " + model + " " + year; }
}