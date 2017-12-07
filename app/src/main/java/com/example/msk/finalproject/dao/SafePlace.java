package com.example.msk.finalproject.dao;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by MsK on 6/12/2017 AD.
 */

@IgnoreExtraProperties
public class SafePlace {
    private String safename;
    private Double lat;
    private Double lng;
    private Integer contain;
    private String userID;

    public SafePlace(){

    }

    public SafePlace(String safename, Double lat, Double lng, Integer contain, String userID) {
        this.safename = safename;
        this.lat = lat;
        this.lng = lng;
        this.contain = contain;
        this.userID = userID;
    }

    public String getSafename() {
        return safename;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public Integer getContain() {
        return contain;
    }

    public String getUserID() {
        return userID;
    }
}
