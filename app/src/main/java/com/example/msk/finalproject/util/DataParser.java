package com.example.msk.finalproject.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by MsK on 19/12/2017 AD.
 */

public class DataParser {
    public String[] parseDirections(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPaths(jsonArray);
    }

    private String[] getPaths(JSONArray StepjsonArray) {
        String[] polylines = new String[StepjsonArray.length()];
        for (int i = 0; i < StepjsonArray.length(); i++){
            try {
                polylines[i] = getPath(StepjsonArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polylines;
    }

    private String getPath(JSONObject pathJSON) {

        String polyline = null;
        try {
            polyline = pathJSON.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }
}
