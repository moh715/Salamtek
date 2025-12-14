package com.example.salamtek1;



class Location implements Persistable {
    private double latitude;
    private double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }


    public double distanceTo(Location other) {
        final int EARTH_RADIUS = 6371; // kilometers

        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLat = Math.toRadians(other.latitude - this.latitude);
        double deltaLon = Math.toRadians(other.longitude - this.longitude);

        // Haversine formula calculation
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    @Override
    public String toFileFormat() {
        return String.format("%.6f,%.6f", latitude, longitude);
    }
    public static Location fromFileFormat(String data) {
        String[] parts = data.split(",");
        return new Location(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
    }
    @Override
    public String toString() {
        return String.format("(%.4f, %.4f)", latitude, longitude);
    }
}