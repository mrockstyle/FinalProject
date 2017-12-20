package com.example.msk.finalproject.dao;

/**
 * Created by MsK on 16/12/2017 AD.
 */

public class SafePlace {
    private Integer safeID;
    private String safeName;
    private Double lat;
    private Double lng;
    private Integer contain;

    public SafePlace(Integer safeID, String safeName, Double lat, Double lng, Integer contain) {
        this.safeID = safeID;
        this.safeName = safeName;
        this.lat = lat;
        this.lng = lng;
        this.contain = contain;
    }

    public Integer getSafeID() {
        return safeID;
    }

    public String getSafeName() {
        return safeName;
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

}
