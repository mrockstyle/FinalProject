package com.example.msk.finalproject.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.Toast;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.controller.Constant;
import com.example.msk.finalproject.dao.SafePlace;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FragmentMap extends Fragment implements OnMapReadyCallback {

    //Firebase
    private DatabaseReference mRootRef,mSafePlaceRef;
    private ValueEventListener valueEventListener;
    private SafePlace safePlace;

    //GoogleMap
    private LatLng markerLatLng;
    private MapView mapView;
    private GoogleMap mMap;
    private Marker mMarker;
    private double lat,lng;
    private FusedLocationProviderClient mFusedLocationClient;
    //private PlaceAutocompleteFragment autocompleteFragment;

    //Variable
    private List<Address> addressList;
    private int markerTag=0;
    private String placeName;


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
        addressList = new ArrayList<>();

    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState
        CreateMap(rootView,savedInstanceState);
        /*autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);*/
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
        readSafePlaceData();
        //setSearchBar();

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
                                mMarker.setTag(markerTag);
                                markerTag++; //increase tag;
                                Log.i("Value", "Data : " + mMarker.getTag()+ "," + safePlace.getSafename()+ ","+ safePlace.getLat() + "," + safePlace.getLng());
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

    public GoogleMap getmMap() {
        return mMap;
    }
}
