package com.example.msk.finalproject.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.controller.Constant;
import com.example.msk.finalproject.manager.HttpManager;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentWaterLevel extends Fragment implements AdapterView.OnItemSelectedListener {

    private ArrayList<String> worldlist;
    private ArrayList<String> arrayname;

    //JSON
    private JSONArray data;
    private JSONObject c;

    //Variables
    private String label;
    private Integer led;
    private Integer pos_time =0;

    //View Variables
    private Spinner span;
    private TextView tvdataNow,tvNormal,tvAlert,tvCritical,tvDate,tvMonitor;


    //Timer
    private Timer time;

    public FragmentWaterLevel() {
        super();
    }

    public static FragmentWaterLevel newInstance() {
        FragmentWaterLevel fragment = new FragmentWaterLevel();
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
        View rootView = inflater.inflate(R.layout.fragment_water_level, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        worldlist = new ArrayList<>();
        arrayname = new ArrayList<>();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState

        span = rootView.findViewById(R.id.spinner);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        initDropdown();
        span.setOnItemSelectedListener(this);

        tvdataNow = rootView.findViewById(R.id.text_datanow);
        tvNormal = rootView.findViewById(R.id.text_datanomal);
        tvAlert = rootView.findViewById(R.id.text_datacrione);
        tvCritical = rootView.findViewById(R.id.text_datacritwo);
        tvDate = rootView.findViewById(R.id.textdata_table);
        tvMonitor = rootView.findViewById(R.id.text_datalow);

        //data_table();

    }

    private void initDropdown() {
        List<NameValuePair> dropdownlist = new ArrayList<NameValuePair>();
        try {
            JSONArray data = new JSONArray(HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_DROP_LIST,dropdownlist));


            for(int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);
                arrayname.add(c.getString("location_name"));

            }

            Toast.makeText(getActivity(), "Load dropdown success", Toast.LENGTH_SHORT).show();



        } catch (JSONException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
            Toast.makeText(getActivity(), "ข้อมูลJson ผิดพลาด", Toast.LENGTH_SHORT).show();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, arrayname);
        span.setAdapter(adapter);
    }

    //////////On Dropdown item Click
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

        Object item = adapterView.getItemAtPosition(position);

        label = item.toString();

        realtime_data();


        time = new Timer();

        time.schedule(new TimerTask() {
            @Override
            public void run() {
                timerTick();
            }


        }, 0, 1000);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    ////////////////////////////////



    private void timerTick() {
        Log.d("data", "timerTick");

        /*while (true) {
            try {
                Thread.sleep(1000*60);

            }catch (InterruptedException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Default Signature Fail", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            if(getActivity() == null)
                return;



            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    realtime_data_two();
                }
            });
        }*/

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                realtime_data_two();
            }
        });

    }


    private void realtime_data() {

        final String X_date[];
        final String X_time[];
        final int X_data[];
        final int X_index [];

        final String [] data_nomal = new String[4+1];

        int pos =1;



        Boolean found = false;

        List<NameValuePair> params_table = new ArrayList<NameValuePair>();
        try {

            data = new JSONArray(HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_WATER_LEVEL_INFO, params_table));

            final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;


            for (int i = 0; i < data.length(); i++) {
                JSONObject test = data.getJSONObject(i);

                if ((label.equalsIgnoreCase(test.getString("location_name")))&&(test.getInt("sensorID")==01)) {

                    found = true;
                    pos = pos + 1;


                }
            }


            X_date = new String[pos + 1];
            X_data = new int[pos+1];
            X_index= new int[pos+1];



            for (int i = 0; i < data.length(); i++) {
                c = data.getJSONObject(i);

                if (((label.equalsIgnoreCase(c.getString("location_name"))) && !found)&&(c.getInt("sensorID")==01)) {
                    found = true;
                }

            }

            led =1;

            for (int j = 0; j < data.length(); j++) {
                c = data.getJSONObject(j);


                String aa = c.getString("location_name");

                if (found) {


                    if ((label.equalsIgnoreCase(aa))&&(c.getInt("sensorID")==01)) {


                        if((c.getInt("normal_point"))!=0){
                            data_nomal[1] = c.getString("normal_point");
                            tvNormal.setText(c.getString("normal_point"));
                        }else{
                            tvNormal.setText("XX");
                            data_nomal[1]="0";
                        }


                        if((c.getInt("monitor_point"))!=0){
                            data_nomal[2] = c.getString("monitor_point");
                            tvMonitor.setText(c.getString("monitor_point"));
                        }else{
                            tvMonitor.setText("XX");
                            data_nomal[2]="0";
                        }



                        if((c.getInt("alert_point"))!=0){
                            data_nomal[3] = c.getString("alert_point");
                            tvAlert.setText(c.getString("alert_point"));
                        }else{
                            tvAlert.setText("XX");
                            data_nomal[3]="0";
                        }

                        if((c.getInt("critical_point"))!=0){
                            data_nomal[4] = c.getString("critical_point");
                            tvCritical.setText(c.getString("critical_point"));
                        }else{
                            tvCritical.setText("XX");
                            data_nomal[4]="0";
                        }


                        tvdataNow.setText(c.getString("data"));






                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String time = c.getString("datetime");
                        Date date = df.parse(time);
                        Calendar v = Calendar.getInstance();
                        v.setTime(date);



                        int month = v.get(Calendar.MONTH) + 1;
                        int year = v.get(Calendar.YEAR) + 543;
                        String date_time =String.valueOf(v.get(Calendar.DATE)) + "-" + month + "-" + year +" | "+String.valueOf(v.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(v.get(Calendar.MINUTE)) + ":" + String.valueOf(v.get(Calendar.SECOND));

                        X_data[led] =Integer.parseInt(c.getString("data"));
                        X_date[led] =date_time;
                        X_index[led] = led ;

                        led=led+1;


                        tvDate.setText(date_time);


                        if (Integer.parseInt(c.getString("data")) >= Integer.parseInt(c.getString("critical_point"))) {
                            tvCritical.setTextColor(getResources().getColor(R.color.color_red));


                        } else if (Integer.parseInt(c.getString("data")) >= Integer.parseInt(c.getString("alert_point"))) {
                            tvAlert.setTextColor(getResources().getColor(R.color.color_orange));
                        } else if (Integer.parseInt(c.getString("data")) >= Integer.parseInt(c.getString("monitor_point"))) {

                            tvMonitor.setTextColor(getResources().getColor(R.color.color_green));
                        }else  if(Integer.parseInt(c.getString("data")) >= Integer.parseInt(c.getString("normal_point"))){
                            tvNormal.setTextColor(getResources().getColor(R.color.color_green));
                        }else {
                            tvdataNow.setTextColor(Color.BLACK);
                        }

                    }


                }else {

                    tvdataNow.setText("XX");
                    tvNormal.setText("XX");
                    tvMonitor.setText("XX");
                    tvAlert.setText("XX");
                    tvCritical.setText("XX");
                    tvDate.setText("XX-XX-XX | XX:XX:XX");
                    tvdataNow.setTextColor(Color.BLACK);

                }


            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
            Toast.makeText(getActivity(), "ข้อมูลJson ผิดพลาด", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        pos_time=1;
    }

    public void realtime_data_two() {


        Log.d("data", "realtime_data_two");


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        List<NameValuePair> params_table = new ArrayList<NameValuePair>();
        try {
            data = new JSONArray(HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_WATER_LEVEL_INFO, params_table));

            for (int j = 0; j < data.length(); j++) {
                c = data.getJSONObject(j);


                String aa = c.getString("location_name");


                if ((label.equalsIgnoreCase(aa))&&(c.getInt("sensorID")==01)) {

                    tvdataNow.setText(c.getString("data"));


                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = c.getString("datetime");
                    Date date = df.parse(time);
                    Calendar v = Calendar.getInstance();
                    v.setTime(date);

                    int month = v.get(Calendar.MONTH) + 1;
                    int year = v.get(Calendar.YEAR) + 543;
                    String date_time =String.valueOf(v.get(Calendar.DATE)) + "-" + month + "-" + year +" | "+String.valueOf(v.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(v.get(Calendar.MINUTE)) + ":" + String.valueOf(v.get(Calendar.SECOND));


                    tvDate.setText(date_time);

                    if (Integer.parseInt(c.getString("data")) >= Integer.parseInt(c.getString("critical_point"))) {
                        tvdataNow.setTextColor(getResources().getColor(R.color.color_red));

                    } else if (Integer.parseInt(c.getString("data")) >= Integer.parseInt(c.getString("alert_point"))) {
                        tvdataNow.setTextColor(getResources().getColor(R.color.color_orange));

                    } else if (Integer.parseInt(c.getString("data")) >= Integer.parseInt(c.getString("normal_point"))) {

                        tvdataNow.setTextColor(getResources().getColor(R.color.color_green));
                    } else {
                        tvdataNow.setTextColor(Color.BLACK);
                    }


                }


            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
            Toast.makeText(getActivity(), "ข้อมูลJson ผิดพลาด", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        pos_time=1;



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Value","OnDestroy");
        time.cancel();
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
