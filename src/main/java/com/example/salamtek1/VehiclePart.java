package com.example.salamtek1;

class VehiclePart {
    private String partName;
    private double price;

    public VehiclePart(String partName, double price) {
        this.partName = partName;
        this.price = price;
    }

    public String getPartName() { 
        return partName;
    }
    public double getPrice() { 
        return price;
    }

    @Override
    public String toString() {
        return partName + ": " + price + " EGP";
    }
}
