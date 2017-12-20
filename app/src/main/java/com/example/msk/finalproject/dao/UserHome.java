package com.example.msk.finalproject.dao;

/**
 * Created by MsK on 19/12/2017 AD.
 */

public class UserHome {
    private String addressName;
    private Double lat;
    private Double lng;

    public UserHome(String addressName, Double lat, Double lng) {
        this.addressName = addressName;
        this.lat = lat;
        this.lng = lng;
    }

    public String getAddressName() {
        return addressName;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }
}
