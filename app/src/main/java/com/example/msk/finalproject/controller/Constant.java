package com.example.msk.finalproject.controller;


/**
 * Created by MsK on 4/12/2017 AD.
 */

public class Constant {
    public static Boolean IS_SEARCHED = false;
    public static Double Lat_FROM_SEARCH,Lng_FROM_SEARCH;
    public static Integer isOpen = 0;

    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 1609; // 1 mile, 1.6 km


    //URL
    public static final String URL = "http://192.168.1.9/water/";
    public static final String URL_LOGIN = "checklogin.php";
    public static final String URL_REGISTER = "register.php";
    public static final String URL_UPDATE_RESOURCE = "update_resource.php";
    public static final String URL_LOCATION_INFO = "location4map.php";
    public static final String URL_SAFEPLACE = "safeplace4map.php";
    public static final String URL_GET_USERS_HOME = "getUsersHome.php";
    public static final String URL_WATER_LEVEL_INFO = "waterlvinfo_data.php";
    public static final String URL_DROP_LIST = "droplist.php";
    public static final String URL_GET_CONTAIN = "getContainInSafePlace.php";
    public static final String URL_UPDATE_CONTAIN = "updateContain.php";
    public static final String URL_GET_HEIGHT = "getHeight.php";
    public static final String URL_UPDATE_USERPATH = "setUserPath.php";
    public static final String URL_WRITE_SAFEPATH = "setSafePath.php";
    public static final String URL_ACS_RUNNER = "acsRunner.php";
    public static final String URL_GET_POLYLINE_BY_ID = "getPolylineByID.php";
    public static final String URL_GET_CONTAIN_BY_USERID = "getContainUserPath.php";
    public static final String URL_UPDATE_USER_ADDRESS = "updateUserAddress.php";

    //URL for Final Project
    public static final String URL_WATERFINAL_LOCATION_INFO = "waterfinal_location4map.php";
    public static final String URL_WATERFINAL_GET_USERS_HOME = "waterfinal_getUsersHome.php";
    public static final String URL_WATERFINAL_SAFEPLACE = "waterfinal_safeplace4map.php";
    public static final String URL_WATERFINAL_ACS_RUNNER = "waterfinal_acsRunnerPy.php";
    public static final String URL_WATERFINAL_GET_POLYLINE = "waterfinal_getPolyline.php";
    public static final String URL_WATERFINAL_GET_CONTAIN = "waterfinal_getContain.php";
    public static final String URL_WATERFINAL_GET_WATER_FLOW = "waterfinal_getLastestWaterFlowData.php";
    public static final String URL_WATERFINAL_DELETE_PATH = "waterfinal_deletePathByID.php";
    public static final String URL_WATERFINAL_UPDATE_PATH = "waterfinal_updatePath.php";
    public static final String URL_WATERFIANL_GET_SHORTEST_PATH = "waterfinal_getShortestPath.php";


    //URL Google
    public static final String GOOGLE_MAP_KEY = "AIzaSyAjK7thv7v8tTXNfVEFN8KLX1W3BTksqGM";
    public static final String URL_GOOGLE_DISTANCE = "https://maps.googleapis.com/maps/api/distancematrix/json?";
    public static final String DISTANCE_TEST = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=13.740938,100.794866&destinations=13.727626,100.772356&key=AIzaSyAjK7thv7v8tTXNfVEFN8KLX1W3BTksqGM";
    public static final String URL_GOOGLE_ELEVATION = "https://maps.googleapis.com/maps/api/elevation/json?";
    public static final String URL_GOOGLE_DIRECTION = "https://maps.googleapis.com/maps/api/directions/json?";
    public static final String URL_GOOGLE_GEOCODING = "https://maps.googleapis.com/maps/api/geocode/json?address=";


    //User Preference
    public static final String USER_PREF = "userPref";
    //pref key
    public static final String USER_ID = "userID";
    public static final String USER_FNAME = "firstname";
    public static final String USER_LNAME = "lastname";
    public static final String IS_ADMIN = "isAdmin";
    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String IS_ALERT = "isAlert";
    public static final String IS_ENTERED = "isEnter";
    public static final String LOCATION_ID = "locationID"; // location ID ที่ alert
    public static final String IS_FIRST_TIME = "isFirstTime";
}
