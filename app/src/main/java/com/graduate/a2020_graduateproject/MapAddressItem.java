package com.graduate.a2020_graduateproject;

public class MapAddressItem {
    private String name;
    private String sub_name;
    private double latitude;

    public MapAddressItem(String name, String sub_name, double latitude, double longitude) {
        this.name = name;
        this.sub_name = sub_name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getSub_name() {
        return sub_name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private double longitude;

}
