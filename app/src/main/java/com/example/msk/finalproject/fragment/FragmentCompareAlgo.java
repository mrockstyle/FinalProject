package com.example.msk.finalproject.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.controller.Constant;
import com.example.msk.finalproject.dao.LocationInfo;
import com.example.msk.finalproject.dao.SafePlace;
import com.example.msk.finalproject.dao.UserHome;
import com.example.msk.finalproject.manager.HttpManager;
import com.example.msk.finalproject.util.DataParser;
import com.example.msk.finalproject.util.GetData;
import com.example.msk.finalproject.util.GetData2;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class FragmentCompareAlgo extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener {


    //Google maps
    private MapView mapView;
    private GoogleMap mMap;
    private LatLng latLng;
    private Marker marker;
    private Polyline polyline;
    private List<Polyline> polylineList;

    //Variable
    private SharedPreferences preferences;
    private GetData2 getData2;
    private UserHome userHome;
    private List<SafePlace> safePlaceList;
    private List<LocationInfo> locationInfoList;
    private List<String> polylinesListTmp;
    private List<Integer> contain;
    private Integer userID;
    private List<NameValuePair> params;
    private List<Double> distanceList,pathDistanceList;
    private Integer pathTag;
    private List<Integer> pathTagList;

    private Double waterFlowValue,waterTime;
    private Integer waterTimeInt,waterTimeSec,countTimer;
    private Boolean isDisplayPolyline,isACS;
    private Integer placeID = 0,placeIDtmp = 0; //ไว้เก็บ node ที่เลือก

    //JSON
    private JSONObject waterFlowJson;

    //Timer
    Timer timer,timerForWater;

    //Views
    private TextView tvTime,tvPath,tvPathDetail;
    private Button btnSpath,btnAcs;
    private ProgressDialog progressDialog;


    public FragmentCompareAlgo() {
        super();
    }

    public static FragmentCompareAlgo newInstance() {
        FragmentCompareAlgo fragment = new FragmentCompareAlgo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_compare_algo, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here

        preferences = getContext().getSharedPreferences(Constant.USER_PREF,0);
        userID = preferences.getInt(Constant.USER_ID,0);
        progressDialog = new ProgressDialog(getContext());

        getData2 = new GetData2();
        getData2.setUserHomeData(userID);
        getData2.setSafePlaceData();
        getData2.setLocationData();
        readData(); //read for update path

        //Log.i("Value","Evacuate from : "+preferences.getInt(Constant.LOCATION_ID,0));

    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState

        createMap(rootView,savedInstanceState);
        tvTime = rootView.findViewById(R.id.tv_time);
        tvPath = rootView.findViewById(R.id.tv_path);
        tvPathDetail = rootView.findViewById(R.id.tv_path_detail);
        //readData();
        pathTagList = new ArrayList<>();

    }

    private void initShortestPath() {
        params = new ArrayList<>();
        params.add(new BasicNameValuePair("userID",String.valueOf(userID)));

        String result = HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_WATERFIANL_GET_SHORTEST_PATH,params);

        JSONObject jsonObject = null;
        JSONArray jsonArray = null;

        String path = "";
        polylinesListTmp = new ArrayList<>();
        pathDistanceList = new ArrayList<>(); //เก็บระยะทางของแต่ละ path

        try {
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++){
                jsonObject = jsonArray.getJSONObject(i);
                int safeID = jsonObject.getInt("safeID");
                path = path + String.valueOf(safeID); //เอา safeID มาต่อกันเป็น string ให้เหมือน acs
                polylinesListTmp.add(URLDecoder.decode(jsonObject.getString("polyline")));
                pathDistanceList.add(jsonObject.getDouble("distance"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        refreshPath();

    }


    private void getPathFromGoogle() {

        params = new ArrayList<>();
        params.add(new BasicNameValuePair("userID",String.valueOf(userID)));

        String statusID = null;
        String result = HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_WATERFINAL_DELETE_PATH,params);
        try {
            JSONObject jsonObject = new JSONObject(result);
            statusID = jsonObject.getString("StatusID");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (statusID != null){
            if (statusID.equals("0")){
                Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
            }else {

                for (int i = 0; i < safePlaceList.size(); i++){

                    List<String> path,pathName;
                    String directionList;
                    Double distance;
                    params = new ArrayList<>();
                    String directionString = HttpManager
                            .getInstance().getHttpPost(Constant.URL_GOOGLE_DIRECTION+"origin="+userHome.getLat()+","+userHome.getLng()
                                    +"&destination="+safePlaceList.get(i).getLat()+","+safePlaceList.get(i).getLng()+"&alternatives=true&key="+Constant.GOOGLE_MAP_KEY,params);

                    DataParser dataParser = new DataParser();
                    directionList = dataParser.parseDirections(directionString);
                    path = dataParser.getPolylineList();
                    pathName = dataParser.getPathName();

                    for (int j = 0; j < path.size(); j++){
                        distance = dataParser.getDistance(directionString,j);
                        //Log.i("Value",safePlaceList.get(i).getSafeName()+" add path "+(j+1)+" "+pathName.get(j)+", distance = "+distance);
                        //Log.i("Value","polyline = "+path.get(j));
                        updatePath(pathName.get(j),safePlaceList.get(i).getSafeID(),distance,userID,path.get(j));
                    }

                }
            }
        }

    }

    private void updatePath(String pathName,Integer safeID,Double distance,Integer userID,String polyline) {

        params = new ArrayList<>();
        params.add(new BasicNameValuePair("safeID",String.valueOf(safeID)));
        params.add(new BasicNameValuePair("distance",String.valueOf(distance)));
        params.add(new BasicNameValuePair("polyline",polyline));
        params.add(new BasicNameValuePair("userID",String.valueOf(userID)));
        params.add(new BasicNameValuePair("pathName",pathName));

        HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_WATERFINAL_UPDATE_PATH,params); //update path

    }

    private void readData() {
        userHome = getData2.getUserHome();
        safePlaceList = getData2.getSafePlaceList();
        locationInfoList = getData2.getLocationInfosList();
    }


    private void createMap(View rootView, Bundle savedInstanceState) {
        mapView = rootView.findViewById(R.id.mapAlgo);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        addMarker();
        getWaterFlowData();
        initDistance(); //หาระยะห่างระหว่าง user กับ sensor
        calTime(); //คำนวณน้ำ
        getPathFromGoogle();

        if (timer != null){
            timer.cancel();
        }
        isDisplayPolyline = false;
        initPath();

    }

    private void calTime() {

        waterTime = (distanceList.get(preferences.getInt(Constant.LOCATION_ID,0)-1) / waterFlowValue) * 60; //จะได้ time นาที
        waterTimeInt = waterTime.intValue();
        waterTimeSec = waterTimeInt*60;
        countTimer = 60;

        //countdown เวลาที่น้ำจะมาถึง
        timerForWater = new Timer();
        timerForWater.schedule(new TimerTask() {
            @Override
            public void run() {
                timerTickForWater();
            }
        },0,1000);

    }

    private void timerTickForWater() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timerCountDown();
                tvTime.setText(waterTimeInt+" นาที");
            }
        });

    }

    private void timerCountDown() {

        if (waterTimeSec == 0){
            timerForWater.cancel();
        }

        if (countTimer == 0){
            countTimer = 60;
            waterTimeInt--;
        }

        //Log.i("TimeValue","sec = "+countTimer);

        tvTime.setText(waterTimeInt+" นาที");

        waterTimeSec--;
        countTimer--;

    }

    private void getWaterFlowData() {
        waterFlowValue = null;
        params = new ArrayList<>();
        params.add(new BasicNameValuePair("locationID",String.valueOf(preferences.getInt(Constant.LOCATION_ID,0))));

        String result = HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_WATERFINAL_GET_WATER_FLOW,params);

        try {
            waterFlowJson = new JSONObject(result);
            waterFlowValue = waterFlowJson.getDouble("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*try {
            waterFlowJson = new JSONObject(HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_WATERFINAL_GET_WATER_FLOW,params));

            waterFlowValue = waterFlowJson.getDouble("data");

        } catch (JSONException e) {
            e.printStackTrace();
        }*/

    }

    private void initDistance() {
        distanceList = new ArrayList<>();
        //cal distance
        for (int i=0 ; i < locationInfoList.size(); i++){

            distanceList.add(SphericalUtil.computeDistanceBetween(
                    new LatLng(userHome.getLat(),userHome.getLng())
                    ,new LatLng(locationInfoList.get(i).getLat(),locationInfoList.get(i).getLng())) / 1000); //แปลงเป็นกิโลเมตร
        }

        Log.i("Value","distanceList = "+distanceList);
    }

    private void initPath() {

        refreshPath(); //เลือกเส้นทาง

    }

    private void refreshPath() {

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerTick();
            }
        },0,5000);

    }

    private void timerTick() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        });
    }

    private void refresh() {
        JSONObject pathObj = null;
        params = new ArrayList<>();
        params.add(new BasicNameValuePair("userID",String.valueOf(userID)));
        try {
            pathObj = new JSONObject(HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_WATERFINAL_ACS_RUNNER,params));
            placeID = Integer.parseInt(pathObj.getString("path"));
            if (placeIDtmp != placeID){
                isDisplayPolyline = false;
            }else {
                isDisplayPolyline = true;
            }
            placeIDtmp = placeID;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isACS){ //ถ้าปุ่มที่กดเป็น ACS
            if (!isDisplayPolyline){
                getPolylineJSON(placeID); //ดึงค่าจาก DB
                displayPolyline();

                tvPath.setText("เส้นทางที่ 1");
                tvPathDetail.setText(String.valueOf(pathDistanceList.get(0) / 1000) + " กิโลเมตร"); //แสดงเป็นกิโลเมตร

                isDisplayPolyline = true;
            }
        }else {
            if (!isDisplayPolyline){
                displayShortestPathPolyline(placeID);

                tvPath.setText("เส้นทางที่สั้นที่สุด");
                tvPathDetail.setText(String.valueOf(pathDistanceList.get(placeID-1) / 1000) + " กิโลเมตร");

                isDisplayPolyline = true;
            }
        }

    }

    private void displayShortestPathPolyline(int placeID) {
        if (polylineList != null){
            for (Polyline line : polylineList){
                line.remove();
            }
            polylineList.clear();
        }
        polylineList = new ArrayList<>();

        polylineList.add(mMap.addPolyline(new PolylineOptions()
                .color(Color.BLUE)
                .width(10)
                .startCap(new RoundCap())
                .endCap(new RoundCap())
                .zIndex(5)
                .addAll(PolyUtil.decode(polylinesListTmp.get(placeID-1)))
        ));

    }

    private void displayPolyline() {
        if (polylineList != null){
            for (Polyline line : polylineList){
                line.remove();
            }
            polylineList.clear();
        }
        polylineList = new ArrayList<>();
        pathTag = polylinesListTmp.size()-1; //กำหนด Tag ของเส้นทาง ใช้เพื่ิอบอกว่าเส้นไหนเป็นเส้นหลัก เส้นรอง


        for (int i = 0; i < polylinesListTmp.size(); i++){

            pathTagList.add(pathTag);

            if (i == (polylinesListTmp.size()-1)){ //เส้นหลัก
                polylineList.add(mMap.addPolyline(new PolylineOptions()
                        .color(Color.BLUE)
                        .width(10)
                        .startCap(new RoundCap())
                        .endCap(new RoundCap())
                        .zIndex(5)
                        .clickable(true)
                        .addAll(PolyUtil.decode(polylinesListTmp.get(i)))));
                polylineList.get(i).setTag(pathTag); //path หลัก
            }else {
                polylineList.add(mMap.addPolyline(new PolylineOptions()
                        .color(R.color.gray_path)
                        .width(10)
                        .startCap(new RoundCap())
                        .endCap(new RoundCap())
                        .zIndex(5)
                        .clickable(true)
                        .addAll(PolyUtil.decode(polylinesListTmp.get(i)))));
                polylineList.get(i).setTag(pathTag); //path อื่นๆ
                pathTag--;
            }

        }

        Collections.sort(pathTagList); //เรียง tag จากน้อยไปมาก
        mMap.setOnPolylineClickListener(this);
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

        Integer path = 0;

        if (polyline.getTag() != null) {
            path = Integer.parseInt(polyline.getTag().toString());
        }

        for (int i = 0 ; i < polylineList.size(); i++){
            polylineList.get(i).setColor(R.color.gray_path);
        }

        polyline.setColor(Color.BLUE);

        tvPath.setText("เส้นทางที่ "+(path+1));
        tvPathDetail.setText(String.valueOf(pathDistanceList.get(path) / 1000) + " กิโลเมตร"); //แสดงเป็นกิโลเมตร

    }

    private void getPolylineJSON(Integer placeID) {

        polylinesListTmp = new ArrayList<>(); //เก็บ polyline แต่ละ path ที่ได้จาก database
        pathDistanceList = new ArrayList<>(); //เก็บระยะทางของแต่ละ path

        params = new ArrayList<>();
        params.add(new BasicNameValuePair("userID",String.valueOf(userID)));
        params.add(new BasicNameValuePair("safeID",String.valueOf(placeID)));

        JSONArray jsonArray=null;
        JSONObject jsonObject=null;
        try {
            jsonArray = new JSONArray(HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_WATERFINAL_GET_POLYLINE,params));
            for (int i=0; i<jsonArray.length();i++){
                jsonObject = jsonArray.getJSONObject(i);
                polylinesListTmp.add(URLDecoder.decode(jsonObject.getString("polyline")));
                pathDistanceList.add(jsonObject.getDouble("distance"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.reverse(polylinesListTmp); //กลับค่า list เส้นทาง เพื่อไม่ให้ display ทับเส้นหลัก
    }

    @SuppressLint("MissingPermission")
    private void addMarker() {
        for (int i =0; i<locationInfoList.size();i++){
            //show info จุดที่ alert
            if ((preferences.getInt(Constant.LOCATION_ID,0)-1) == i){
                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(locationInfoList.get(i).getLat(),locationInfoList.get(i).getLng()))
                        .title(locationInfoList.get(i).getLocationName())
                        .snippet("น้ำท่วมที่จุดนี้!!")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.sensor_icon)));
                marker.showInfoWindow();
            }else {
                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(locationInfoList.get(i).getLat(),locationInfoList.get(i).getLng()))
                        .title(locationInfoList.get(i).getLocationName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.sensor_icon)));
            }

        }

        for (int i =0;i<safePlaceList.size();i++){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(safePlaceList.get(i).getLat(),safePlaceList.get(i).getLng()))
                    .title(safePlaceList.get(i).getSafeName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.safe_place))
                    .snippet(String.valueOf(safePlaceList.get(i).getContain())));
        }

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(userHome.getLat(),userHome.getLng()))
                .title(userHome.getAddressName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home)));

        //mMap.setOnMarkerClickListener(this);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userHome.getLat(),userHome.getLng()),15));

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance (Fragment level's variables) State here
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance (Fragment level's variables) State here
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null){
            timer.cancel();
        }

        if (timerForWater != null){
            timerForWater.cancel();
        }
    }
}
