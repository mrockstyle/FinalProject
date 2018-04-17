package com.example.msk.finalproject.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.controller.Constant;
import com.example.msk.finalproject.controller.MainActivity;
import com.example.msk.finalproject.manager.HttpManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class FragmentEditProfile extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    //Google maps
    private MapView mapView;
    private GoogleMap mMap;
    private LatLng latLng;
    private Marker marker;

    //Variables
    private EditText edtAddress;
    private Button btnFind,btnSave;
    private TextView tvLocation;
    private MarkerOptions markerOptions;
    private List<NameValuePair> params;
    private Double lat=null,lng=null;
    private String placeAddress = null;
    private AlertDialog.Builder ad;
    private ProgressDialog progressDialog;
    private Integer userID;

    private SharedPreferences preferences;


    public FragmentEditProfile() {
        super();
    }

    public static FragmentEditProfile newInstance() {
        FragmentEditProfile fragment = new FragmentEditProfile();
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
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        progressDialog = new ProgressDialog(getContext());
        preferences = getContext().getSharedPreferences(Constant.USER_PREF,0);
        userID = preferences.getInt(Constant.USER_ID,0);
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState
        createMap(rootView,savedInstanceState);

        edtAddress = rootView.findViewById(R.id.edt_address);
        btnFind = rootView.findViewById(R.id.btn_find);
        btnSave = rootView.findViewById(R.id.btn_saveAddress);
        tvLocation = rootView.findViewById(R.id.tv_location);

        btnFind.setOnClickListener(this);
        btnSave.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_find){
            String location = edtAddress.getText().toString();
            String locationEncode = null;

            if (!location.equals("")) {

                try {
                    locationEncode = URLEncoder.encode(location,"utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                getJSONfromGoogle(locationEncode);
                if (marker != null){
                    marker.remove();
                }
                addMarker(location);


            }
        }
        else if (view.getId() == R.id.btn_saveAddress){
            showDialogBox();
        }

    }

    private void showDialogBox() {
        ad = new AlertDialog.Builder(getContext());
        ad.setTitle("Attention");
        ad.setMessage("Are you sure to save");
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateDatabase();
                // ถ้า log in ครั้งแรก save เสร็จให้ไปที่หน้า Main
                if (preferences.getBoolean(Constant.IS_FIRST_TIME,true)){
                    getActivity().finish();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        ad.setNegativeButton("No",null);
        ad.show();
    }

    private void updateDatabase() {
        params = new ArrayList<>();
        params.add(new BasicNameValuePair("userID",String.valueOf(userID)));
        params.add(new BasicNameValuePair("isFirstTime",String.valueOf(0)));
        params.add(new BasicNameValuePair("lat",String.valueOf(lat)));
        params.add(new BasicNameValuePair("lng",String.valueOf(lng)));
        params.add(new BasicNameValuePair("addressName",edtAddress.getText().toString()));
        progressDialog.setMessage("Saving...");
        progressDialog.show();

        JSONObject resultObj = null;
        int status = 0;

        try {
            String result = HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_UPDATE_USER_ADDRESS,params); // update address
            Log.i("value","result = "+result);
            resultObj = new JSONObject(result);
            status = resultObj.getInt("StatusID");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (status != 0){
            Toast.makeText(getContext(),"Update Completed",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(),"Failed to update",Toast.LENGTH_SHORT).show();
        }

        progressDialog.dismiss();
    }

    private void addMarker(String location) {
        marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat,lng))
                    .title(location)
                    .snippet(placeAddress)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.home)));
        marker.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),15));
    }

    private void getJSONfromGoogle(String location) {
        params = new ArrayList<>();
        JSONArray jsonArray=null;
        try {
            JSONObject jsonObject = new JSONObject(HttpManager.getInstance().getHttpPost(Constant.URL_GOOGLE_GEOCODING+location+"&"+Constant.GOOGLE_MAP_KEY,params));
            jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0 ; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                placeAddress = object.getString("formatted_address");
                lat = object.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                lng = object.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            }

            if (lat !=null && lng != null){
                tvLocation.setText(lat+","+lng);
            }else{
                Toast.makeText(getContext(),"No Location Founded",Toast.LENGTH_SHORT).show();
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createMap(View rootView, Bundle savedInstanceState) {
        mapView = rootView.findViewById(R.id.mapProfile);
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
