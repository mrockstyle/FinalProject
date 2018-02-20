package com.example.msk.finalproject.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.controller.Constant;
import com.example.msk.finalproject.dao.LocationInfo;
import com.example.msk.finalproject.dao.SafePlace;
import com.example.msk.finalproject.dao.UserHome;
import com.example.msk.finalproject.manager.HttpManager;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

    //Timer
    Timer timer;


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

        getData2 = new GetData2();
        getData2.setUserHomeData(userID);
        getData2.setSafePlaceData();
        getData2.setLocationData();

    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState

        createMap(rootView,savedInstanceState);
        readData();

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
        initPath();

    }

    private void initPath() {

        params = new ArrayList<>();
        JSONObject pathObj = null;
        try {
            pathObj = new JSONObject(HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_WATERFINAL_ACS_RUNNER,params));
            String path = pathObj.getString("path");

            refreshContainData(path); //เลือกเส้นทาง

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void refreshContainData(String path) {

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerTick(path);
            }
        },0,3000);

    }

    private void timerTick(String path) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refresh(path);
            }
        });
    }

    private void refresh(String path) {
        getData2.setContainData();
        contain = getData2.getContain();
        Log.i("Value","Contain = "+contain);
        String polylineStr = null;
        Integer placeID = null; //ไว้เก็บ node ที่เลือก

        for (int i = 1; i < path.length()-1; i++){
            if (contain.get(Integer.parseInt(String.valueOf(path.charAt(i)))-1) > 28){ //แต่ละที่ห้ามเกิน 30 คน แต่เผื่อไว้ 28 ให้เปลี่ยนเส้นทาง
                continue;
            }else {
                placeID = Integer.parseInt(String.valueOf(path.charAt(i)));

                getPolylineJSON(placeID); //ดึงค่าจาก DB

                break; //ถ้าเจอทางที่ไปได้แล้วให้ break
            }
        }

        Log.i("Value","PolylineList = "+polylinesListTmp);


        //display polyline

        if (polylineList != null){
            for (Polyline line : polylineList){
                line.remove();
            }
            polylineList.clear();
        }

        polylineList = new ArrayList<>();

        for (int i = 0; i < polylinesListTmp.size(); i++){


            if (i == 0){
                polylineList.add(mMap.addPolyline(new PolylineOptions()
                        .color(Color.BLUE)
                        .width(10)
                        .startCap(new RoundCap())
                        .endCap(new RoundCap())
                        .zIndex(5)
                        .clickable(true)
                        .addAll(PolyUtil.decode(polylinesListTmp.get(i)))));
            }else {
                polylineList.add(mMap.addPolyline(new PolylineOptions()
                        .color(Color.GRAY)
                        .width(10)
                        .startCap(new RoundCap())
                        .endCap(new RoundCap())
                        .zIndex(5)
                        .clickable(true)
                        .addAll(PolyUtil.decode(polylinesListTmp.get(i)))));
            }

        }

        mMap.setOnPolylineClickListener(this);


    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        Toast.makeText(getContext(),"Path Click",Toast.LENGTH_SHORT).show();
        //TODO :: handle Click

    }

    private void getPolylineJSON(Integer placeID) {

        polylinesListTmp = new ArrayList<>();

        params = new ArrayList<>();
        params.add(new BasicNameValuePair("userID",String.valueOf(userID)));
        params.add(new BasicNameValuePair("safeID",String.valueOf(placeID)));

        JSONArray jsonArray=null;
        JSONObject jsonObject=null;
        try {
            jsonArray = new JSONArray(HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_WATERFINAL_GET_POLYLINE,params));
            for (int i=0; i<jsonArray.length();i++){
                jsonObject = jsonArray.getJSONObject(i);
                polylinesListTmp.add(jsonObject.getString("polyline"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    private void addMarker() {
        for (int i =0; i<locationInfoList.size();i++){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(locationInfoList.get(i).getLat(),locationInfoList.get(i).getLng()))
                    .title(locationInfoList.get(i).getLocationName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.sensor_icon)));
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
    }
}
