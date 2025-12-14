package com.example.salamtek1;
import java.util.ArrayList;
import java.util.List;

class Hub implements Persistable {
    private String hubId;
    private String cityName;
    private Location location;  // Composition - Hub HAS-A Location
    private List<String> officerIds;

    public Hub(String hubId, String cityName, Location location) {
        this.hubId = hubId;
        this.cityName = cityName;
        this.location = location;
        this.officerIds = new ArrayList<>();
    }

    public String getHubId() { return hubId; }
    public String getCityName() { return cityName; }
    public Location getLocation() { return location; } // Return composed object
    public List<String> getOfficerIds() { return officerIds; }

    public void addOfficer(String officerId) {
        this.officerIds.add(officerId);
    }

    @Override
    public String toFileFormat() {
        return String.format("HUB|%s|%s|%s", hubId, cityName, location.toFileFormat());
    }
}
