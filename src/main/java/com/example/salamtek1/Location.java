package com.example.salamtek1;

class Location implements javax.xml.stream.Location {
    private double latitude, longitude;
     public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public double distanceTo(Location other) {
        final int R = 6371;
        double lat1 = Math.toRadians(latitude), lat2 = Math.toRadians(other.latitude);
        double dLat = Math.toRadians(other.latitude - latitude);
        double dLon = Math.toRadians(other.longitude - longitude);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon/2) * Math.sin(dLon/2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }
     @Override
    public String toString() { return String.format("(%.4f, %.4f)", latitude, longitude); }

    @Override
    public int getLineNumber() {
    return 0;
    }

    @Override
    public int getColumnNumber() {
    return 0;
    }
@Override
    public int getCharacterOffset() {
        return 0;
    }

    @Override
    public String getPublicId() {
    return "";
    }

 @Override
    public String getSystemId() {
    return "";
    }
}
