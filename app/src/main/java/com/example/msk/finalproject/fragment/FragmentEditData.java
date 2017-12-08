package com.example.msk.finalproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.controller.Constant;
import com.example.msk.finalproject.dao.SafePlace;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FragmentEditData extends Fragment implements View.OnClickListener {

    private DatabaseReference mRootRef,mSafePlaceRef;
    private SafePlace safePlace;


    private EditText edt_name;
    private TextView tv_lat,tv_lng;
    private Button btn_save;


    private Double lat,lng;

    public FragmentEditData() {
        super();
    }

    public static FragmentEditData newInstance() {
        FragmentEditData fragment = new FragmentEditData();
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
        View rootView = inflater.inflate(R.layout.fragment_edit_data, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mSafePlaceRef = mRootRef.child("safeplace");
        lat = Constant.Lat_FROM_SEARCH;
        lng = Constant.Lng_FROM_SEARCH;
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState

        edt_name = rootView.findViewById(R.id.edt_placeName);
        tv_lat = rootView.findViewById(R.id.tv_lat);
        tv_lng = rootView.findViewById(R.id.tv_lng);
        btn_save = rootView.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_save){
            writeSafePlaceData();
        }
    }

    private void writeSafePlaceData() {
        safePlace = new SafePlace(edt_name.getText().toString(),lat,lng,0,"");
        mSafePlaceRef.push().setValue(safePlace);
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
