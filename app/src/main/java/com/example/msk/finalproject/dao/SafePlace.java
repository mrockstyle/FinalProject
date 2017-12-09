package com.example.msk.finalproject.dao;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by MsK on 6/12/2017 AD.
 */

@IgnoreExtraProperties
public class SafePlace {
    private String place_name;
    private Double place_lat;
    private Double place_lng;
    private Integer place_contain;

    public SafePlace(){

    }

    public SafePlace(String place_name, Double place_lat, Double place_lng, Integer place_contain) {
        this.place_name = place_name;
        this.place_lat = place_lat;
        this.place_lng = place_lng;
        this.place_contain = place_contain;
    }

    public String getPlace_name() {
        return place_name;
    }

    public Double getPlace_lat() {
        return place_lat;
    }

    public Double getPlace_lng() {
        return place_lng;
    }

    public Integer getPlace_contain() {
        return place_contain;
    }
}
