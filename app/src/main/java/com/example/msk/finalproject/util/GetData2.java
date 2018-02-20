package com.example.msk.finalproject.util;

import android.content.Context;

import com.example.msk.finalproject.controller.Constant;
import com.example.msk.finalproject.dao.LocationInfo;
import com.example.msk.finalproject.dao.SafePlace;
import com.example.msk.finalproject.dao.UserHome;
import com.example.msk.finalproject.manager.HttpManager;
import com.google.android.gms.maps.model.LatLng;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

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

public class GetData2 {

    private List<LocationInfo> locationInfosList;
    private List<SafePlace> safePlaceList;
    private List<UserHome> userHomeList;
    private List<Integer> contain;

    private JSONObject locationInfoObj,safePlaceObj,homeObj,containObj;
    private JSONArray locationInfoData,safePlaceData,homeData,containArr;
    private List<NameValuePair> params,params2,params3,distanceParams;

    private UserHome userHome;
    private LocationInfo locationInfo;
    private SafePlace safePlace;

    private Integer counter=0;

    private Context mContext;


    public GetData2() {
        //distanceParams = new ArrayList<>();
        mContext = Contextor.getInstance().getContext();
    }


    public void setLocationData(){
        params2 = new ArrayList<>();
        locationInfosList = new ArrayList<>();

        try {

            locationInfoData = new JSONArray(HttpManager
                    .getInstance()
                    .getHttpPost(Constant.URL+Constant.URL_WATERFINAL_LOCATION_INFO,params2));

            for (int i=0; i<locationInfoData.length(); i++){

                locationInfoObj = locationInfoData.getJSONObject(i);

                //Log.i("Value","locationInfoObj : "+locationInfoObj);
                locationInfo = new LocationInfo(locationInfoObj.getInt("locationID")
                        ,locationInfoObj.getString("location_name")
                        ,locationInfoObj.getDouble("lat")
                        ,locationInfoObj.getDouble("lng"));

                LatLng locationLatLng = new LatLng(locationInfo.getLat(), locationInfo.getLng());

                locationInfosList.add(locationInfo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setSafePlaceData(){
        params3 = new ArrayList<>();
        safePlaceList = new ArrayList<>();

        try {
            safePlaceData = new JSONArray(HttpManager
                    .getInstance()
                    .getHttpPost(Constant.URL+Constant.URL_WATERFINAL_SAFEPLACE,params3));


            for (int j=0; j<safePlaceData.length(); j++) {
                safePlaceObj = safePlaceData.getJSONObject(j);
                safePlace = new SafePlace(safePlaceObj.getInt("safeID")
                        , safePlaceObj.getString("name")
                        , safePlaceObj.getDouble("lat")
                        , safePlaceObj.getDouble("lng")
                        , safePlaceObj.getInt("contain"));

                safePlaceList.add(safePlace);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setUserHomeData(Integer userID){

        params = new ArrayList<>();
        params.add(new BasicNameValuePair("userID",String.valueOf(userID)));

        try {

            homeObj = new JSONObject(HttpManager
                    .getInstance()
                    .getHttpPost(Constant.URL+Constant.URL_WATERFINAL_GET_USERS_HOME,params));


            userHome = new UserHome(
                    homeObj.getString("addressName")
                    ,homeObj.getDouble("lat")
                    ,homeObj.getDouble("lng"));

            LatLng userLatLng = new LatLng(userHome.getLat(),userHome.getLng());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setContainData(){
        params = new ArrayList<>();

        contain = new ArrayList<>();


        try {
            containArr = new JSONArray(HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_WATERFINAL_GET_CONTAIN,params));
            //Log.i("Value","Contain = "+containArr);

            for (int i = 0; i < containArr.length(); i++){
                containObj = containArr.getJSONObject(i);

                contain.add(containObj.getInt("contain"));

                //Log.i("Value","Contain = "+containObj.getInt("contain"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public List<Integer> getContain() {
        return contain;
    }
}
