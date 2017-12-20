package com.example.msk.finalproject.dao;

/**
 * Created by MsK on 16/12/2017 AD.
 */

public class LocationInfo {
    private Integer locationID;
    private String locationName;
    private Double lat;
    private Double lng;

    public LocationInfo() {
    }

    public LocationInfo(Integer locationID, String locationName, Double lat, Double lng) {
        this.locationID = locationID;
        this.locationName = locationName;
        this.lat = lat;
        this.lng = lng;
    }

    public Integer getLocationID() {
        return locationID;
    }

    public String getLocationName() {
        return locationName;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

}
