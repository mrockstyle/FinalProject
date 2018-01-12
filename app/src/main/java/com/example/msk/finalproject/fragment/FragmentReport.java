package com.example.msk.finalproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.controller.Constant;
import com.example.msk.finalproject.dao.SafePlace;
import com.example.msk.finalproject.dao.UserHome;
import com.example.msk.finalproject.manager.HttpManager;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class FragmentReport extends Fragment {

    private TextView tvContain,tvContain2;
    private Timer timer;
    private SafePlace safePlace;
    private List<SafePlace> safePlaceList;
    private List<UserHome> userHomeList;

    private JSONObject locationInfoObj,safePlaceObj,homeObj;
    private JSONArray locationInfoData,safePlaceData,homeData;
    private List<NameValuePair> params,params2,params3,distanceParams;

    public FragmentReport() {
        super();
    }

    public static FragmentReport newInstance() {
        FragmentReport fragment = new FragmentReport();
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
        View rootView = inflater.inflate(R.layout.fragement_report, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        safePlace = new SafePlace();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState
        tvContain = rootView.findViewById(R.id.tv_contain);
        tvContain2 = rootView.findViewById(R.id.tv_contain2);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerTick();
            }
        },0,1000);


    }

    private void timerTick() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                readData();
            }
        });
    }

    private void readData() {
        params = new ArrayList<>();
        safePlaceList = new ArrayList<>();
        try {
            safePlaceData = new JSONArray(HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_SAFEPLACE,params));

            for (int j=0; j<safePlaceData.length(); j++) {
                safePlaceObj = safePlaceData.getJSONObject(j);
                safePlace = new SafePlace(safePlaceObj.getInt("safeID")
                        , safePlaceObj.getString("name")
                        , safePlaceObj.getDouble("lat")
                        , safePlaceObj.getDouble("lng")
                        , safePlaceObj.getInt("contain"));
                safePlaceList.add(safePlace);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvContain.setText("contain = "+safePlaceList.get(0).getContain());
        tvContain2.setText("contain = "+safePlaceList.get(1).getContain());

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
        timer.cancel();
    }
}
