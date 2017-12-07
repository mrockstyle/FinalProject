package com.example.msk.finalproject.dao;

/**
 * Created by MsK on 7/12/2017 AD.
 */

public class Test {
    private Double lat;
    private Double lng;
    private String name;

    public Test(){

    }

    public Test(Double lat, Double lng, String name) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public String getName() {
        return name;
    }
}
