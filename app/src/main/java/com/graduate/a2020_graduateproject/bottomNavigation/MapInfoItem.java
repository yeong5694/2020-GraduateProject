package com.graduate.a2020_graduateproject.bottomNavigation;

public class MapInfoItem {

    private String key;
    private String index;
    private String latitude;
    private String longitude;
    private String name;

    public MapInfoItem(String key, String index, String latitude, String longitude, String name){
        this.key = key;
        this.index = index;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }


    public String getKey(){
        return key;
    }
    public String getIndex(){
        return index;
    }
    public String getLatitude(){
        return latitude;
    }
    public String getLongitude(){
        return longitude;
    }
    public String getName(){
        return name;
    }


}
