package com.example.msk.finalproject.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.dao.SafePlace;
import com.example.msk.finalproject.dao.Test;
import com.example.msk.finalproject.dao.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback {

    private DatabaseReference mRootRef,mSafePlaceRef,mTestRef;


    private MapView mapView;
    private GoogleMap mMap;
    private Marker mMarker;
    private LocationManager locationManager;
    private double lat,lng;
    private FusedLocationProviderClient mFusedLocationClient;

    private ValueEventListener valueEventListener;
    private ChildEventListener childEventListener;

    private TextView tvrtTest;


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

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mSafePlaceRef = mRootRef.child("safeplace");


    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState
        CreateMap(rootView,savedInstanceState);
        tvrtTest = rootView.findViewById(R.id.tv_rtTest);
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
        addMarker();

    }

    private void addMarker() {
        readSafePlaceData();
    }


    private void readSafePlaceData() {

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    key = ds.getKey();
                    Log.i("Value", "Key : " + key);

                    valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            SafePlace safePlace = dataSnapshot.getValue(SafePlace.class);

                            if (safePlace == null) {
                                Toast.makeText(getContext(), "Error: could not fetch data.", Toast.LENGTH_LONG).show();
                            } else {
                                LatLng latLng = new LatLng(safePlace.getLat(),safePlace.getLng());
                                mMarker = mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(safePlace.getSafename()));
                                mMarker.setTag(0);


                                tvrtTest.setText(safePlace.getSafename());
                                Log.i("Value", "Data : " + safePlace.getSafename()+ ","+ safePlace.getLat() + "," + safePlace.getLng());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    mSafePlaceRef.child(key).addValueEventListener(valueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mSafePlaceRef.addValueEventListener(valueEventListener);

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
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),15));
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

    @Override
    public void onStop() {
        super.onStop();
        if (mSafePlaceRef != null){
            mSafePlaceRef.removeEventListener(valueEventListener);
        }
    }
}
