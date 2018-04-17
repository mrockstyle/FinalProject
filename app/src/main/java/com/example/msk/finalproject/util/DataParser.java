package com.example.msk.finalproject.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by MsK on 19/12/2017 AD.
 */

public class DataParser {

    private int pathCount = 0;
    private List<String> polylineList,pathName;
    private List<Double> distanceList;

    public String parseDirections(String jsonData){

        polylineList = new ArrayList<>();
        pathName = new ArrayList<>();

        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPaths(jsonArray);
    }

    private String getPaths(JSONArray StepjsonArray) {
        String polylines = null;
        String name = null;
        for (int i = 0; i < StepjsonArray.length(); i++){
            try {
                pathCount++;
                JSONObject object = StepjsonArray.getJSONObject(i);
                name = object.getString("summary");
                polylines = object.getJSONObject("overview_polyline").getString("points");
                pathName.add(name);
                polylineList.add(polylines);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polylines;
    }

    public Double getDistance(String jsonData,int index){

        distanceList = new ArrayList<>();
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        double distance = 0;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes")
                    .getJSONObject(index)
                    .getJSONArray("legs");

            for (int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                distance = jsonObject1.getJSONObject("distance").getDouble("value");
                distanceList.add(distance);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return distance;

    }

    public int getPathCount() {
        return pathCount;
    }

    public List<String> getPolylineList() {
        return polylineList;
    }

    public List<Double> getDistanceList() {
        return distanceList;
    }

    public List<String> getPathName() {
        return pathName;
    }
}
