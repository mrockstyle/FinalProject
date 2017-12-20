package com.example.msk.finalproject.util;

import com.example.msk.finalproject.controller.Constant;
import com.example.msk.finalproject.dao.LocationInfo;
import com.example.msk.finalproject.dao.SafePlace;
import com.example.msk.finalproject.dao.UserHome;
import com.example.msk.finalproject.manager.HttpManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MsK on 20/12/2017 AD.
 */

public class GetData {

    private List<LocationInfo> locationInfosList;
    private List<SafePlace> safePlaceList;
    private List<UserHome> userHomeList;

    private JSONObject locationInfoObj,safePlaceObj,homeObj;
    private JSONArray locationInfoData,safePlaceData,homeData;
    private List<NameValuePair> params,params2,params3,distanceParams;

    private UserHome userHome;
    private LocationInfo locationInfo;
    private SafePlace safePlace;


    public GetData() {
        locationInfosList = new ArrayList<>();
        safePlaceList = new ArrayList<>();
        distanceParams = new ArrayList<>();
    }

    public void readDataAndFindDistance(Integer userID){

        params = new ArrayList<>();
        params.add(new BasicNameValuePair("userID",String.valueOf(userID)));
        params2 = new ArrayList<>();
        params3 = new ArrayList<>();
        //locationMarkerOptionsList = new ArrayList<>();
        //getUserHome(params);


        try {

            homeObj = new JSONObject(HttpManager
                    .getInstance()
                    .getHttpPost(Constant.URL+Constant.URL_GET_USERS_HOME,params));

            locationInfoData = new JSONArray(HttpManager
                    .getInstance()
                    .getHttpPost(Constant.URL+Constant.URL_LOCATION_INFO,params2));

            safePlaceData = new JSONArray(HttpManager
                    .getInstance()
                    .getHttpPost(Constant.URL+Constant.URL_SAFEPLACE,params3));

            for (int i=0; i<locationInfoData.length(); i++){

                locationInfoObj = locationInfoData.getJSONObject(i);

                //Log.i("Value","locationInfoObj : "+locationInfoObj);
                locationInfo = new LocationInfo(locationInfoObj.getInt("locationID")
                        ,locationInfoObj.getString("location_name")
                        ,locationInfoObj.getDouble("lat")
                        ,locationInfoObj.getDouble("lng"));

                LatLng locationLatLng = new LatLng(locationInfo.getLat(), locationInfo.getLng());

                locationInfosList.add(locationInfo);

                userHome = new UserHome(
                        homeObj.getString("addressName")
                        ,homeObj.getDouble("lat")
                        ,homeObj.getDouble("lng"));

                LatLng userLatLng = new LatLng(userHome.getLat(),userHome.getLng());
                Double distanceUser = SphericalUtil.computeDistanceBetween(locationLatLng,userLatLng);
                writeDataUserLocation(userID,distanceUser);
                //Log.i("Value","locationID : "+locationInfo.getLocationID());

                for (int j=0; j<safePlaceData.length(); j++){
                    safePlaceObj = safePlaceData.getJSONObject(j);
                    safePlace = new SafePlace(safePlaceObj.getInt("safeID")
                            ,safePlaceObj.getString("name")
                            ,safePlaceObj.getDouble("lat")
                            ,safePlaceObj.getDouble("lng")
                            ,safePlaceObj.getInt("contain"));
                    safePlaceList.add(safePlace);
                    LatLng safeLatLng = new LatLng(safePlace.getLat(),safePlace.getLng());
                    Double distance = SphericalUtil.computeDistanceBetween(locationLatLng,safeLatLng);

                    writeDataLocationSafe(distance);

                    //Log.i("Value","safeID : "+safePlace.getSafeID());
                    //Log.i("Value","("+locationInfo.getLocationID()+","+safePlace.getSafeID()+") = "+distance);
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void writeDataUserLocation(Integer userID,Double distance) {
        distanceParams.add(new BasicNameValuePair("userID",String.valueOf(userID)));
        distanceParams.add(new BasicNameValuePair("locationID",String.valueOf(locationInfo.getLocationID())));
        distanceParams.add(new BasicNameValuePair("distance",String.valueOf(distance)));

        HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_SEND_DISTANCE_USER,distanceParams);
    }

    private void writeDataLocationSafe(Double distance) {
        distanceParams.add(new BasicNameValuePair("locationID",String.valueOf(locationInfo.getLocationID())));
        distanceParams.add(new BasicNameValuePair("safeID",String.valueOf(safePlace.getSafeID())));
        distanceParams.add(new BasicNameValuePair("distance",String.valueOf(distance)));

        HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_SEND_DISTANCE,distanceParams);
    }



    public List<LocationInfo> getLocationInfosList() {
        return locationInfosList;
    }

    public List<SafePlace> getSafePlaceList() {
        return safePlaceList;
    }

    public UserHome getUserHome() {
        return userHome;
    }



}
