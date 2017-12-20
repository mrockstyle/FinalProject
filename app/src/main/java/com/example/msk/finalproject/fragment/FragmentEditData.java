package com.example.msk.finalproject.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.controller.Constant;
import com.example.msk.finalproject.manager.HttpManager;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FragmentEditData extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    //Database
    private Integer userID;
    private Integer locationID;
    private List<NameValuePair> params;
    private AlertDialog.Builder ad;


    //Variables
    private Spinner spinner_location;
    private ArrayAdapter<CharSequence> adapter;
    private EditText edt_height,edt_normal,edt_monitor,edt_alert,edt_critical;
    private Button btn_save;
    private String LocationKey;
    private ProgressDialog progressDialog;

    //Pref
    private SharedPreferences preferences;

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
        preferences = getContext().getSharedPreferences(Constant.USER_PREF,0);
        //get userID
        userID = preferences.getInt(Constant.USER_ID,0);
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState

        spinner_location = rootView.findViewById(R.id.spinner_location);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_location, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_location.setAdapter(adapter);
        spinner_location.setOnItemSelectedListener(this);

        edt_height = rootView.findViewById(R.id.edt_height);
        edt_normal = rootView.findViewById(R.id.edt_normal);
        edt_monitor = rootView.findViewById(R.id.edt_monitor);
        edt_alert = rootView.findViewById(R.id.edt_alert);
        edt_critical = rootView.findViewById(R.id.edt_critical);
        btn_save = rootView.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);

        progressDialog = new ProgressDialog(getContext());

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_save){
            progressDialog.setMessage("Saving Data");
            progressDialog.show();
            writeLocationInfoData();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0){
            locationID = 1;
        }else{
            locationID = 2;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void writeLocationInfoData(){

        ad  = new AlertDialog.Builder(getActivity());
        createValuePair();

        Log.i("Value","Pair : "+params);

        String resultServer = HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_UPDATE_RESOURCE,params);

        /*** Default Value ***/
        String strStatusID = "0";
        String strError = "Unknow Status!";
        JSONObject c;

        try {
            c = new JSONObject(resultServer);
            strStatusID = c.getString("StatusID");
            strError = c.getString("Error");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialog.dismiss();

        // Prepare Save Data
        if (strStatusID.equals("0")) {
            ad.setMessage(strError);
            ad.show();
        } else {
            Toast.makeText(getActivity(), "Save Data Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void createValuePair() {
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("locationID",String.valueOf(locationID)));
        params.add(new BasicNameValuePair("height", edt_height.getText().toString()));
        params.add(new BasicNameValuePair("normal_point", edt_normal.getText().toString()));
        params.add(new BasicNameValuePair("monitor_point",edt_monitor.getText().toString()));
        params.add(new BasicNameValuePair("alert_point",edt_alert.getText().toString()));
        params.add(new BasicNameValuePair("critical_point", edt_critical.getText().toString()));
        params.add(new BasicNameValuePair("userID", String.valueOf(userID)));
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
