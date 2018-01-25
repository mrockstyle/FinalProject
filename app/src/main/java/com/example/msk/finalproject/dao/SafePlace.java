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
    private Integer userIDpath;
    private Double elevation;
    private Double userDistance;
    private String userPolyline;

    public SafePlace() {
    }

    public SafePlace(Integer safeID, String safeName, Double lat, Double lng, Integer contain) {
        this.safeID = safeID;
        this.safeName = safeName;
        this.lat = lat;
        this.lng = lng;
        this.contain = contain;
    }

    public SafePlace(Integer safeID, Integer userIDpath, Double userDistance, String userPolyline) {
        this.safeID = safeID;
        this.userIDpath = userIDpath;
        this.userDistance = userDistance;
        this.userPolyline = userPolyline;
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
