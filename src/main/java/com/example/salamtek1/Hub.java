package com.example.salamtek1;

import java.util.ArrayList;
import java.util.List;

class Hub {
    private String hubId, cityName;
    private Location location;
    private List<String> officerIds = new ArrayList<>();

    public Hub(String hubId, String cityName, Location location) {
        this.hubId = hubId;
        this.cityName = cityName;
        this.location = location;
    }

    public String getHubId() { return hubId; }
    public String getCityName() { return cityName; }
    public Location getLocation() { return location; }
    public void addOfficer(String officerId) { officerIds.add(officerId); }
}

