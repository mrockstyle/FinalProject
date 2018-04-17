package com.example.msk.finalproject.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.example.msk.finalproject.util.DataParser;
import com.example.msk.finalproject.util.GetData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static android.content.Context.LOCATION_SERVICE;


public class FragmentMap extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener ,LocationListener, GoogleMap.OnMarkerClickListener {

    //Database
    private List<NameValuePair> params;

    //Obj Model
    private List<LocationInfo> locationInfoList;
    private List<SafePlace> safePlaceList,safePlaceListRefresh;
    private List<String> polylinesListTmp,polylinesList;
    private List<Integer> contain;
    private UserHome userHome;
    private GetData getData;

    //GoogleMap
    private MapView mapView;
    private GoogleMap mMap;
    private double lat,lng;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location lastLocation;
    private LocationManager locationManager;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    String TAG = "Location";
    private Marker safePlaceMarker;
    private Polyline polyline;

    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL =  3 * 60 * 1000; // 3 minutes
    private final int FASTEST_INTERVAL = 30 * 1000;  // 30 secs
    private final int REQ_PERMISSION = 999;

    //Variable
    private SharedPreferences preferences;
    private Integer userID;
    private Timer timer;


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
        getData.setLocationData();
        getData.setSafePlaceData();
        getData.setUserHomeData(userID);
        createGoogleClientApi();
    }

    private void createGoogleClientApi() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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

        if (preferences.getBoolean(Constant.IS_ALERT,true)){
            initForEvacuation();
        }

    }


    private void readData(){
        locationInfoList = getData.getLocationInfosList();
        safePlaceList = getData.getSafePlaceList();
        userHome = getData.getUserHome();
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
    public boolean onMarkerClick(Marker marker) {

        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(getContext(),
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }


        return false;
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

    ////GoogleClientAPI

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastKnownLocation();
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if ( checkPermission() ) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if ( lastLocation != null ) {
                Log.i(TAG, "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                writeLastLocation();
                startLocationUpdates();
            } else {
                Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        }
        else askPermission();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    // Start location Updates
    private void startLocationUpdates(){
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if ( checkPermission() )
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (LocationListener) this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged ["+location+"]");
        lastLocation = location;
        writeActualLocation(location);
    }

    // Write location coordinates on UI
    private void writeActualLocation(Location location) {

        Log.i(TAG,"Lat: " + location.getLatitude());
        Log.i(TAG,"Lng: " + location.getLongitude());
    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                REQ_PERMISSION
        );
    }

    // Verify user's response of the permission requested
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ) {
            case REQ_PERMISSION: {
                if ( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    // Permission granted
                    getLastKnownLocation();

                } else {
                    // Permission denied
                    permissionsDenied();
                }
                break;
            }
        }
    }

    // App cannot work without the permissions
    private void permissionsDenied() {
        Log.w(TAG, "permissionsDenied()");
    }


    ///////////////////////////////////// FOR   EVACUATION /////////////////////////////////////////////


    private void initForEvacuation() {

        //load polyline data
        polylinesListTmp = new ArrayList<>();
        polylinesList = new ArrayList<>();
        params = new ArrayList<>();
        params.add(new BasicNameValuePair("userIDpath",String.valueOf(userID)));


        JSONArray jsonArray=null;
        JSONObject jsonObject=null;
        try {
            jsonArray = new JSONArray(HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_GET_POLYLINE_BY_ID,params));
            for (int i=0; i<jsonArray.length();i++){
                jsonObject = jsonArray.getJSONObject(i);
                polylinesListTmp.add(jsonObject.getString("user_polyline"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        params = new ArrayList<>();
        JSONObject pathObj = null;
        try {
            pathObj = new JSONObject(HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_ACS_RUNNER,params));
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
        getData.setContainData(userID);
        contain = getData.getContain();
        Log.i("Value","Contain = "+contain);
        String polylineStr = null;
        Integer value = null; //ไว้เก็บ node ที่เลือก

        for (int i = 1; i < path.length()-1; i++){
            if (contain.get(Integer.parseInt(String.valueOf(path.charAt(i)))-1) > 28){ //แต่ละที่ห้ามเกิน 30 คน แต่เผื่อไว้ 28 ให้เปลี่ยนเส้นทาง
                continue;
            }else {
                value = Integer.parseInt(String.valueOf(path.charAt(i)));
                polylineStr = polylinesListTmp.get(value-1);
                break;
            }
        }

        //Log.i("Value","Polyline = "+polylineStr);


        //display polyline

        if (polylineStr != null){
            if (polyline != null){
                polyline.remove();
            }

            polyline = mMap.addPolyline(new PolylineOptions()
                    .color(Color.BLUE)
                    .width(10)
                    .startCap(new RoundCap())
                    .endCap(new RoundCap())
                    .zIndex(5)
                    .addAll(PolyUtil.decode(polylineStr)));
        }
    }




    ////////////////////////////////// SaveInstance ///////////////////////////////////////////////////////

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
