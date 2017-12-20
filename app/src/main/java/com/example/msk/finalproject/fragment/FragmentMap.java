package com.example.msk.finalproject.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.msk.finalproject.R;

import com.example.msk.finalproject.controller.Constant;
import com.example.msk.finalproject.dao.LocationInfo;
import com.example.msk.finalproject.dao.SafePlace;
import com.example.msk.finalproject.dao.UserHome;
import com.example.msk.finalproject.manager.HttpManager;
import com.example.msk.finalproject.util.DataParser;
import com.example.msk.finalproject.util.GetData;
import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.PolyUtil;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;


public class FragmentMap extends Fragment implements OnMapReadyCallback {

    //Database
    private List<NameValuePair> params,params2,params3,distanceParams;

    //Obj Model
    private List<LocationInfo> locationInfoList;
    private List<SafePlace> safePlaceList;
    private UserHome userHome;
    private GetData getData;

    //GoogleMap
    private MapView mapView;
    private GoogleMap mMap;
    private double lat,lng;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager locationManager;


    //Variable
    private SharedPreferences preferences;
    private Integer userID;


    public FragmentMap() {
        super();
    }

    public static FragmentMap newInstance() {
        FragmentMap fragment = new FragmentMap();
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
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }


    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        preferences = getContext().getSharedPreferences(Constant.USER_PREF,0);
        userID = preferences.getInt(Constant.USER_ID,0);
        getData = new GetData();
        getData.readDataAndFindDistance(userID); //อ่านข้อมูล แล้วหา distance ไปลง database
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState
        CreateMap(rootView,savedInstanceState);
        readData();

    }

    private void SetCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null){
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // Show rationale and request permission.
        }
    }

    private void CreateMap(View rootView,Bundle savedInstanceState) {
        mapView = rootView.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        SetCurrentLocation();
        getDeviceLocation();
        //addMarker
        addMarker();

        params = new ArrayList<>();

        String[] directionList;

        ///
        String directionString = HttpManager.getInstance()
                .getHttpPost(Constant.URL_GOOGLE_DIRECTION,params);

        DataParser dataParser = new DataParser();
        directionList = dataParser.parseDirections(directionString);
        displayDirection(directionList);


    }

    private void readData(){
        locationInfoList = getData.getLocationInfosList();
        safePlaceList = getData.getSafePlaceList();
        userHome = getData.getUserHome();
    }

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
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.safe_zone)));
        }

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(userHome.getLat(),userHome.getLng()))
                .title(userHome.getAddressName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home_icon)));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userHome.getLat(),userHome.getLng()),15));

    }

    private void displayDirection(String[] directionList) {
        for (int i=0; i<directionList.length; i++){
            PolylineOptions options = new PolylineOptions();
            options.color(Color.BLUE);
            options.width(10);
            options.addAll(PolyUtil.decode(directionList[i]));

            mMap.addPolyline(options);
        }
    }


    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),15));
                            //Log.i("Location","CurLocation : "+location.getLatitude()+","+location.getLongitude());
                        }
                    }
                });
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
}
