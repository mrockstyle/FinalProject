package com.example.msk.finalproject.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by MsK on 19/12/2017 AD.
 */

public class DataParser {
    public String parseDirections(String jsonData){
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
        for (int i = 0; i < StepjsonArray.length(); i++){
            try {
                JSONObject object = StepjsonArray.getJSONObject(i);
                polylines = object.getJSONObject("overview_polyline").getString("points");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polylines;
    }

    public Double getDistance(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        double distance = 0;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs");

            for (int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                distance = jsonObject1.getJSONObject("distance").getDouble("value");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return distance;

    }
}
