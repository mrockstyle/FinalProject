package com.example.msk.finalproject.fragment;


import android.app.ProgressDialog;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.controller.Constant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentWaterReport extends Fragment {
    TabHost mTabHost;
    final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    public static final String url = Constant.URL+Constant.URL_WATER_LEVEL_INFO;
    public static final String url2 = Constant.URL+Constant.URL_DROP_LIST;
    ArrayList<String> arrayname_report_day = new ArrayList<String>();
    ArrayList<String> arrayname_report_month = new ArrayList<String>();
    ArrayList<String> arrayname_report_year = new ArrayList<String>();
    private  Spinner span_location_report, span_day_one, span_day_two,span_location_report_two,span_month_one,span_month_two,span_location_report_three,span_year_one;
    private int led,led_day;
    private JSONArray data;
    private JSONObject test ,real;

    private String month_name []= new String[]{"","มกราคม","กุมภาพันธ์","มีนาคม","เมษายน","พฤษภาคม","มิถุนายน","กรกฏาคม","สิงหาคม","กันยายน","ตุลาคม","พฤศจิกายน","ธันวาคม"};
    private View rootView;

    private ProgressDialog progressDialog;

    private ListView listView_day;
    private ListView listView_month;
    private ListView listView_year;


    public FragmentWaterReport() {
        super();
    }

    public static FragmentWaterReport newInstance() {
        FragmentWaterReport fragment = new FragmentWaterReport();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        rootView =inflater.inflate(R.layout.fragment_water__report, container, false);

        mTabHost = (TabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mTabHost.addTab(mTabHost.newTabSpec("tab_test1").setIndicator("DAY").setContent(R.id.Tab_Report_day));
        mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator("MONTH").setContent(R.id.Tab_Report_month));
        mTabHost.addTab(mTabHost.newTabSpec("tab_test3").setIndicator("YEAR").setContent(R.id.Tab_Report_year));
        mTabHost.setCurrentTab(0);



        listView_day =(ListView)rootView.findViewById(R.id.listView_report_day);
        span_location_report =(Spinner)rootView.findViewById(R.id.spinner_location_day);
        span_day_one =(Spinner)rootView.findViewById(R.id.spinner_day);
        span_day_two=(Spinner)rootView.findViewById(R.id.spinner_day_two);

        listView_month =(ListView)rootView.findViewById(R.id.listView_report_month);
        span_location_report_two =(Spinner)rootView.findViewById(R.id.spinner_location_month);
        span_month_one=(Spinner)rootView.findViewById(R.id.spinner_month_one);
        span_month_two=(Spinner)rootView.findViewById(R.id.spinner_month_two);

        listView_year=(ListView)rootView.findViewById(R.id.listView_report_year);
        span_location_report_three=(Spinner)rootView.findViewById(R.id.spinner_location_year);
        span_year_one=(Spinner)rootView.findViewById(R.id.spinner_year);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        dropdown();
        data_span_one();


        dropdown_two();
        data_span_two();

        dropdown_three();
        data_span_three();



        return rootView;
    }


    public   void dropdown(){




        List<NameValuePair> dropdownlist = new ArrayList<NameValuePair>();

        try {
            JSONArray data = new JSONArray(getJSON(url2, dropdownlist));


            for(int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);


                    arrayname_report_day.add(c.getString("location_name"));

            }

            Toast.makeText(getActivity(), "Load dropdown success", Toast.LENGTH_SHORT).show();



        } catch (JSONException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
            Toast.makeText(getActivity(), "ข้อมูลJson ผิดพลาด", Toast.LENGTH_SHORT).show();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, arrayname_report_day);
        span_location_report.setAdapter(adapter);






    }


    public   void dropdown_two(){

        List<NameValuePair> dropdownlist_twoI = new ArrayList<NameValuePair>();

        try {
            JSONArray data = new JSONArray(getJSON(url2, dropdownlist_twoI));


            for(int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);




               arrayname_report_month.add(c.getString("location_name"));


            }






        } catch (JSONException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
            Toast.makeText(getActivity(), "ข้อมูลJson ผิดพลาด", Toast.LENGTH_SHORT).show();
        }





        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, arrayname_report_month);
        span_location_report_two.setAdapter(adapter);


    }
    public   void dropdown_three(){

        List<NameValuePair> dropdownlist = new ArrayList<NameValuePair>();

        try {
            JSONArray data = new JSONArray(getJSON(url2, dropdownlist));


            for(int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);




                arrayname_report_year.add(c.getString("location_name"));


            }






        } catch (JSONException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
            Toast.makeText(getActivity(), "ข้อมูลJson ผิดพลาด", Toast.LENGTH_SHORT).show();
        }





        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, arrayname_report_year);
        span_location_report_three.setAdapter(adapter);


    }





    private void data_span_one(){


        //spin location


        span_location_report.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                               Object item = parent.getItemAtPosition(position);

                                                                ArrayList<String> arrayname_report_day_span_day_one = new ArrayList<String>();


                                                                String label = item.toString();


                                                               final int data_span_one[];
                                                               final  String X_date[];
                                                               final String X_time[];
                                                               final String X_state[];
                                                               final int Critical [] =new int [6];

//
                                                               int pos = 0;
//

//                                                               //โหลดข้อมูล
                                                               List<NameValuePair> params_two = new ArrayList<NameValuePair>();
                                                               try {
//
//
                                                                   JSONArray data_day = new JSONArray(getJSON(url, params_two));
//
                                                                   for (int i = 0; i < data_day.length(); i++) {
                                                                       JSONObject test_day = data_day.getJSONObject(i);

                                                                       if ((label.equalsIgnoreCase(test_day.getString("location_name")))&&(test_day.getInt("sensorID")==01)) {
                                                                           pos = pos + 1;
//
                                                                       }
                                                                   }
//
//
//                                                                   //เก็บช่องใส่array
                                                                   data_span_one = new int[pos + 1];
                                                                   X_date = new String[pos + 1];
                                                                   X_time = new String[pos + 1];
                                                                   X_state = new String[pos + 1];
//
//
                                                                   led_day = 1;

                                                                   for (int k = 0; k < data_day.length(); k++) {
                                                                       JSONObject re_day = data_day.getJSONObject(k);

                                                                       if ((label.equalsIgnoreCase(re_day.getString("location_name")))&&(re_day.getInt("sensorID")==01)) {

                                                                           data_span_one[led_day] = re_day.getInt("data");

                                                                           Critical[3]=re_day.getInt("normal_point");
                                                                           Critical[4]=re_day.getInt("alert_point");
                                                                           Critical[5]=re_day.getInt("critical_point");


                                                                           DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                           String time = re_day.getString("datetime");
                                                                           Date date = df.parse(time);
                                                                           Calendar v = Calendar.getInstance();
                                                                           v.setTime(date);


                                                                           int month = v.get(Calendar.MONTH) + 1;
                                                                           int year = v.get(Calendar.YEAR) + 543;


                                                                           X_date[led_day] = String.valueOf(v.get(Calendar.DATE)) + "-" + month + "-" + year;
                                                                           X_time[led_day] = String.valueOf(v.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(v.get(Calendar.MINUTE)) + ":" + String.valueOf(v.get(Calendar.SECOND));


//                                                                           arrayname_report_day_span_day_one.add(String.valueOf(v.get(Calendar.DATE)) + "-" + month + "-" + year);



                                                                           led_day = led_day+1;
                                                                       }







                                                                   }





//
//
//
                                                                   for(int i=1;i< X_date.length;i++){


                                                                       if(arrayname_report_day_span_day_one.isEmpty()){
                                                                           arrayname_report_day_span_day_one.add(X_date[i]);
                                                                       }

                                                                       if(i>=2){
                                                                           if(!((X_date[i-1]).equalsIgnoreCase(X_date[i]))){
                                                                               arrayname_report_day_span_day_one.add(X_date[i]);
                                                                           }

                                                                       }

                                                                   }
//
//
//                                                                   //วันช่องแรก
                                                                   ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
                                                                           android.R.layout.simple_dropdown_item_1line, arrayname_report_day_span_day_one);
                                                                   span_day_one.setAdapter(adapter2);








                                                                   if (pos == 0) {

                                                                       listView_day.removeAllViewsInLayout();
                                                                       span_day_one.removeAllViewsInLayout();
                                                                       span_day_two.removeAllViewsInLayout();



                                                                   } else {


                                                                       span_day_one.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                                                                                  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                                                                                      Object item2 = parent.getItemAtPosition(position);
                                                                                                                      final String label_day_one = item2.toString();


                                                                                                                      ArrayList<String> arrayname_report_day_span_day_two = new ArrayList<String>();
                                                                                                                      ArrayAdapter<String> adapter3;
                                                                                                                     int  pos = 0;
                                                                                                                      boolean found = false;


                                                                                                                      //วันช่องสอง
                                                                                                                      for (int k = 1; k < X_date.length; k++) {

                                                                                                                          if ((label_day_one.equalsIgnoreCase(X_date[k])) && found == false) {
                                                                                                                              pos = k;
                                                                                                                              found = true;


                                                                                                                          }

                                                                                                                      }

                                                                                                                      if (!(pos == 0)) {

                                                                                                                          for (int l = pos; l < X_date.length && found == true; l++) {

                                                                                                                              if(arrayname_report_day_span_day_two.isEmpty()){
                                                                                                                                  arrayname_report_day_span_day_two.add(X_date[l]);
                                                                                                                              }

                                                                                                                              if(l>=pos+1){

                                                                                                                                  if(!((X_date[l-1]).equalsIgnoreCase(X_date[l]))){
                                                                                                                                      arrayname_report_day_span_day_two.add(X_date[l]);
                                                                                                                                  }
                                                                                                                              }



                                                                                                                          }

                                                                                                                          adapter3 = new ArrayAdapter<String>(getActivity(),
                                                                                                                                  android.R.layout.simple_dropdown_item_1line, arrayname_report_day_span_day_two);
                                                                                                                          span_day_two.setAdapter(adapter3);


                                                                                                                          span_day_two.setSelection(arrayname_report_day_span_day_two.size() - 1);


                                                                                                                      }else{

                                                                                                                      }


                                                                                                                      //ช่องspin ที่3 ข้อมูลใส่ตาราง
                                                                                                                      span_day_two.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                                                                                                                                 public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                                                     Object item3 = parent.getItemAtPosition(position);
                                                                                                                                                                     String label_day_two = item3.toString();




                                                                                                                                                                     ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
                                                                                                                                                                     HashMap<String, String> map;

                                                                                                                                                                     int index = 0;
//

                                                                                                                                                                     int pos_two = 0;
                                                                                                                                                                     int pos_three = 0;

                                                                                                                                                                     boolean found_two = false;
                                                                                                                                                                     boolean found_three = false;


                                                                                                                                                                     for (int n = 1; n < X_date.length; n++) {

                                                                                                                                                                         if ((X_date[n].equalsIgnoreCase(label_day_one)) && found_two == false) {
                                                                                                                                                                             pos_two = n;
                                                                                                                                                                             found_two = true;

                                                                                                                                                                         }
                                                                                                                                                                     }

                                                                                                                                                                     for (int i = 1; i < X_date.length; i++) {

                                                                                                                                                                         if (X_date[i].equalsIgnoreCase(label_day_two))  {

                                                                                                                                                                             pos_three = i;



                                                                                                                                                                         }

                                                                                                                                                                     }

                                                                                                                                                                     if (!((pos_two == 0) && (pos_three == 0))) {


                                                                                                                                                                         map = new HashMap<String, String>();
                                                                                                                                                                         map.put("Date", "วันที่");
                                                                                                                                                                         map.put("Time", "เวลา");
                                                                                                                                                                         map.put("Data", "ข้อมูล");
                                                                                                                                                                         MyArrList.add(map);


                                                                                                                                                                         for (int m = pos_two; m <= pos_three; m++) {

                                                                                                                                                                             map = new HashMap<String, String>();

                                                                                                                                                                             map.put("Date", X_date[m]);
                                                                                                                                                                             map.put("Time", X_time[m]);


                                                                                                                                                                              if(data_span_one[m]> Critical[4]) {
                                                                                                                                                                                 map.put("Data", "+"+String.valueOf(data_span_one[m]));
                                                                                                                                                                             }
                                                                                                                                                                             else if(data_span_one[m]< Critical[3]) {
                                                                                                                                                                                 map.put("Data", "-"+String.valueOf(data_span_one[m]));
                                                                                                                                                                             }else if(data_span_one[m]== Critical[3]){
                                                                                                                                                                                  map.put("Data", " "+String.valueOf(data_span_one[m]));
                                                                                                                                                                              }else {
                                                                                                                                                                                  map.put("Data", " "+String.valueOf(data_span_one[m]));
                                                                                                                                                                              }




                                                                                                                                                                             MyArrList.add(map);

//

                                                                                                                                                                         }


                                                                                                                                                                         SimpleAdapter simpleAdapterData;
                                                                                                                                                                         simpleAdapterData = new SimpleAdapter(getActivity(), MyArrList, R.layout.acivity_column2,
                                                                                                                                                                                 new String[]{"Date", "Time", "Data"}, new int[]{R.id.article_1, R.id.article_2, R.id.article_3});

                                                                                                                                                                         listView_day.setAdapter(simpleAdapterData);

//

                                                                                                                                                                     } else {
                                                                                                                                                                         MyArrList.clear();


                                                                                                                                                                         map = new HashMap<String, String>();
                                                                                                                                                                         map.put("Date", "วันที่");
                                                                                                                                                                         map.put("Time", "เวลา");
                                                                                                                                                                         map.put("Data", "ข้อมูล");
                                                                                                                                                                         MyArrList.add(map);


                                                                                                                                                                         for (int m = pos_two; m <= pos_three; m++) {

                                                                                                                                                                             map = new HashMap<String, String>();

                                                                                                                                                                             map.put("Date", X_date[m]);
                                                                                                                                                                             map.put("Time", X_time[m]);
                                                                                                                                                                             map.put("Data", String.valueOf(data_span_one[m]));
                                                                                                                                                                             MyArrList.add(map);

//


                                                                                                                                                                         }




                                                                                                                                                                         SimpleAdapter simpleAdapterData;
                                                                                                                                                                         simpleAdapterData = new SimpleAdapter(getActivity(), MyArrList, R.layout.acivity_column2,
                                                                                                                                                                                 new String[]{"Date", "Time", "Data"}, new int[]{R.id.article_1, R.id.article_2, R.id.article_3});

                                                                                                                                                                         listView_day.setAdapter(simpleAdapterData);



//

                                                                                                                                                                     }


                                                                                                                                                                 }


                                                                                                                                                                 public void onNothingSelected(AdapterView<?> parent) {


                                                                                                                                                                 }
                                                                                                                                                             }
                                                                                                                      );


                                                                                                                  }


                                                                                                                  public void onNothingSelected(AdapterView<?> parent) {


                                                                                                                  }
                                                                                                              }
                                                                       );


                                                                   }


                                                               } catch (JSONException e) {
                                                                   // TODO Auto-generated catch block
                                                                   e.printStackTrace();
//                                                   Toast.makeText(getActivity(), "ข้อมูลJson ผิดพลาด", Toast.LENGTH_SHORT).show();
                                                               } catch (ParseException e) {
                                                                   e.printStackTrace();
                                                               }



                                                           }

                                                           public void onNothingSelected(AdapterView<?> parent) {

                                                           }


                                                       }
        );



    }
    private void data_span_three(){

        span_location_report_three.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                                 public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                     Object item = parent.getItemAtPosition(position);

                                                                     final ArrayList<Integer> arrayname_report_year = new ArrayList<Integer>();

                                                                     ArrayAdapter<Integer> adapter;
                                                                     final String label_year = item.toString();
                                                                     Boolean found = false;


                                                                     final int data_span_one[], index_span_one[];
                                                                     final String X_date[];
                                                                     final String X_time[];
                                                                     final String X_location[];
                                                                     final int X_month[];
                                                                     final int X_year[];
                                                                     final int Critical [] =new int [6];



                                                                     int pos2 = 0;


                                                                     //โหลดข้อมูล
                                                                     List<NameValuePair> params = new ArrayList<NameValuePair>();
                                                                     try {


                                                                         data = new JSONArray(getJSON(url, params));

                                                                         for (int i = 0; i < data.length(); i++) {
                                                                             test = data.getJSONObject(i);

                                                                             if ((label_year.equalsIgnoreCase(test.getString("location_name")))&&(test.getInt("sensorID")==01)) {


                                                                                 pos2 = pos2 + 1;


                                                                             }
                                                                         }


                                                                         //เก็บช่องใส่array
                                                                         data_span_one = new int[pos2 + 1];
                                                                         X_date = new String[pos2 + 1];
                                                                         X_time = new String[pos2 + 1];
                                                                         X_location = new String[pos2 + 1];
                                                                         X_month = new int[pos2 + 1];
                                                                         X_year = new int[pos2 + 1];


                                                                         led = 1;


                                                                         for (int j = 0; j < data.length(); j++) {

                                                                             real = data.getJSONObject(j);


                                                                             if ((label_year.equalsIgnoreCase(real.getString("location_name")))&&(real.getInt("sensorID")==01)) {

                                                                                 found = true;
                                                                                 data_span_one[led] = Integer.parseInt(real.getString("data"));
                                                                                 X_location[led] = real.getString("location_name");


                                                                                 //เปลี่ยนวันที่ันที่
                                                                                 DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                                 String time = real.getString("datetime");
                                                                                 Date date = df.parse(time);
                                                                                 Calendar v = Calendar.getInstance();
                                                                                 v.setTime(date);

                                                                                 String day = String.valueOf(v.get(Calendar.DATE));
                                                                                 int month = v.get(Calendar.MONTH) + 1;
                                                                                 int year = v.get(Calendar.YEAR) + 543;

                                                                                 X_date[led] = String.valueOf(v.get(Calendar.DATE)) + "-" + month + "-" + year;
                                                                                 X_month[led] = v.get(Calendar.MONTH) + 1;
                                                                                 X_year[led] = v.get(Calendar.YEAR) + 543;
                                                                                 X_time[led] = String.valueOf(v.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(v.get(Calendar.MINUTE)) + ":" + String.valueOf(v.get(Calendar.SECOND));

                                                                                 Critical[3]=real.getInt("normal_point");
                                                                                 Critical[4]=real.getInt("alert_point");
                                                                                 Critical[5]=real.getInt("critical_point");

                                                                                 led = led + 1;
                                                                             }


                                                                         }

                                                                         //ใส่ปีนีในReport_รายปี
                                                                         for (int i = 1; i < X_date.length; i++) {


                                                                             if (arrayname_report_year.isEmpty()) {
                                                                                 arrayname_report_year.add((X_year[i]));
                                                                             }

                                                                             if (i >= 2) {

                                                                                 if (!(X_year[i - 1] == X_year[i])) {

                                                                                     arrayname_report_year.add((X_year[i]));
                                                                                 }


                                                                             }

                                                                         }


                                                                         adapter = new ArrayAdapter<Integer>(getActivity(),
                                                                                 android.R.layout.simple_dropdown_item_1line, arrayname_report_year);
                                                                         span_year_one.setAdapter(adapter);


                                                                         if (pos2 == 0) {
                                                                             span_year_one.removeAllViewsInLayout();
                                                                             listView_year.removeAllViewsInLayout();

                                                                         }


                                                                         span_year_one.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                                                                                     public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                                                                                         Object item2 = parent.getItemAtPosition(position);
                                                                                                                         final String label_year = item2.toString();

                                                                                                                         ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
                                                                                                                         HashMap<String, String> map;


                                                                                                                         map = new HashMap<String, String>();
                                                                                                                         map.put("Date", "วันที่");
                                                                                                                         map.put("Time", "เวลา");
                                                                                                                         map.put("Data", "ข้อมูล (ซม.)");
                                                                                                                         MyArrList.add(map);


                                                                                                                         for (int n = 1; n < X_date.length; n++) {

                                                                                                                             if (Integer.valueOf(label_year) == X_year[n]) {
                                                                                                                                 map = new HashMap<String, String>();
                                                                                                                                 map.put("Date", X_date[n]);
                                                                                                                                 map.put("Time", X_time[n]);

                                                                                                                                 if(data_span_one[n]> Critical[4]) {
                                                                                                                                     map.put("Data", "+"+String.valueOf(data_span_one[n]));
                                                                                                                                 }
                                                                                                                                 else if(data_span_one[n]< Critical[3]) {
                                                                                                                                     map.put("Data", "-"+String.valueOf(data_span_one[n]));
                                                                                                                                 }else if(data_span_one[n]== Critical[3]){
                                                                                                                                     map.put("Data", " "+String.valueOf(data_span_one[n]));
                                                                                                                                 }else {
                                                                                                                                     map.put("Data", " "+String.valueOf(data_span_one[n]));
                                                                                                                                 }



                                                                                                                                 MyArrList.add(map);


                                                                                                                             }


                                                                                                                         }


                                                                                                                         SimpleAdapter simpleAdapterData;
                                                                                                                         simpleAdapterData = new SimpleAdapter(getActivity(), MyArrList, R.layout.acivity_column2,
                                                                                                                                 new String[]{"Date", "Time", "Data"}, new int[]{R.id.article_1, R.id.article_2, R.id.article_3});

                                                                                                                         listView_year.setAdapter(simpleAdapterData);


                                                                                                                     }

                                                                                                                     public void onNothingSelected(AdapterView<?> parent) {


                                                                                                                     }
                                                                                                                 }
                                                                         );


                                                                     } catch (JSONException e) {
                                                                         // TODO Auto-generated catch block
                                                                         e.printStackTrace();
//                                                   Toast.makeText(getActivity(), "ข้อมูลJson ผิดพลาด", Toast.LENGTH_SHORT).show();
                                                                     } catch (ParseException e) {
                                                                         e.printStackTrace();
                                                                     }


                                                                 }

                                                                 public void onNothingSelected(AdapterView<?> parent) {

                                                                 }


                                                             }
        );



    }
    private void data_span_two(){

        span_location_report_two.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                   Object item = parent.getItemAtPosition(position);

                                                                   final ArrayList<String> arrayname_report_month_span_month_one = new ArrayList<String>();


                                                                   String label_month = item.toString();
                                                                   Boolean found = false;


                                                                   final int data_span_one[], index_span_one[];
                                                                   final String X_date[];
                                                                   final String X_time[];
                                                                   final String X_location[];
                                                                   final int X_month[];
                                                                   final int X_year[];
                                                                   final int Critical [] =new int [6];


                                                                   int pos2 = 0;


                                                                   //โหลดข้อมูล
                                                                   List<NameValuePair> params = new ArrayList<NameValuePair>();
                                                                   try {


                                                                       data = new JSONArray(getJSON(url, params));

                                                                       for (int i = 0; i < data.length(); i++) {
                                                                           test = data.getJSONObject(i);

                                                                           if ((label_month.equalsIgnoreCase(test.getString("location_name")))&&(test.getInt("sensorID")==01)) {


                                                                               pos2 = pos2 + 1;


                                                                           }
                                                                       }


                                                                       //เก็บช่องใส่array
                                                                       data_span_one = new int[pos2 + 1];
                                                                       X_date = new String[pos2 + 1];
                                                                       X_time = new String[pos2 + 1];
                                                                       X_location = new String[pos2 + 1];
                                                                       X_month = new int[pos2 + 1];
                                                                       X_year = new int[pos2 + 1];


                                                                       led = 1;


                                                                       for (int j = 0; j < data.length(); j++) {

                                                                           real = data.getJSONObject(j);


                                                                           if ((label_month.equalsIgnoreCase(real.getString("location_name")))&&(real.getInt("sensorID")==01)) {

                                                                               found = true;
                                                                               data_span_one[led] = Integer.parseInt(real.getString("data"));
                                                                               X_location[led] = real.getString("location_name");


                                                                               //เปลี่ยนวันที่ันที่
                                                                               DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                               String time = real.getString("datetime");
                                                                               Date date = df.parse(time);
                                                                               Calendar v = Calendar.getInstance();
                                                                               v.setTime(date);

                                                                               String day = String.valueOf(v.get(Calendar.DATE));
                                                                               int month = v.get(Calendar.MONTH) + 1;
                                                                               int year = v.get(Calendar.YEAR) + 543;

                                                                               X_date[led] = String.valueOf(v.get(Calendar.DATE)) + "-" + month + "-" + year;
                                                                               X_month[led] = v.get(Calendar.MONTH) + 1;
                                                                               X_year[led] = v.get(Calendar.YEAR) + 543;
                                                                               X_time[led] = String.valueOf(v.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(v.get(Calendar.MINUTE)) + ":" + String.valueOf(v.get(Calendar.SECOND));



                                                                               Critical[3]=real.getInt("normal_point");
                                                                               Critical[4]=real.getInt("alert_point");
                                                                               Critical[5]=real.getInt("critical_point");

                                                                               led = led + 1;
                                                                           }


                                                                       }

                                                                       //ใส่เดือนีในReport_รายเดือน
                                                                       for (int i = 1; i < month_name.length; i++) {
                                                                           arrayname_report_month_span_month_one.add(month_name[i]);

                                                                       }


                                                                       //วันช่องแรก
                                                                       ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
                                                                               android.R.layout.simple_dropdown_item_1line, arrayname_report_month_span_month_one);
                                                                       span_month_one.setAdapter(adapter2);


                                                                       if (pos2 == 0) {
                                                                           span_month_one.removeAllViewsInLayout();
                                                                           span_month_two.removeAllViewsInLayout();
                                                                           listView_month.removeAllViewsInLayout();

                                                                       }


                                                                       span_month_one.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                                                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                                                                                        Object item2 = parent.getItemAtPosition(position);
                                                                                                                        final String label_month_one = item2.toString();


                                                                                                                        ArrayList<Integer> arrayname_report_day_span_day_two = new ArrayList<Integer>();


                                                                                                                        ArrayAdapter<Integer> adapter3;


                                                                                                                        //วันช่องสอง

                                                                                                                        //18/02/2559

//

                                                                                                                        //ใส่ปีในReport_รายเดือน
                                                                                                                        for (int i = 1; i < X_date.length; i++) {


                                                                                                                            if (arrayname_report_day_span_day_two.isEmpty()) {
                                                                                                                                arrayname_report_day_span_day_two.add((X_year[i]));
                                                                                                                            }

                                                                                                                            if (i >= 2) {

                                                                                                                                if (!(X_year[i - 1] == X_year[i])) {

                                                                                                                                    arrayname_report_day_span_day_two.add((X_year[i]));
                                                                                                                                }


                                                                                                                            }

                                                                                                                        }


                                                                                                                        adapter3 = new ArrayAdapter<Integer>(getActivity(),
                                                                                                                                android.R.layout.simple_dropdown_item_1line, arrayname_report_day_span_day_two);
                                                                                                                        span_month_two.setAdapter(adapter3);


                                                                                                                        //ช่องspin ที่3 ข้อมูลใส่ตาราง
                                                                                                                        span_month_two.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                                                                                                                                     public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                                                         Object item3 = parent.getItemAtPosition(position);
                                                                                                                                                                         String label_month_two = item3.toString();


                                                                                                                                                                         ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
                                                                                                                                                                         HashMap<String, String> map;


                                                                                                                                                                         map = new HashMap<String, String>();
                                                                                                                                                                         map.put("Date", "วันที่");
                                                                                                                                                                         map.put("Time", "เวลา");
                                                                                                                                                                         map.put("Data", "ข้อมูล (ซม.)");
                                                                                                                                                                         MyArrList.add(map);


                                                                                                                                                                         for (int i = 1; i <= 12; i++) {

                                                                                                                                                                             if (label_month_one.equalsIgnoreCase(month_name[i])) {


                                                                                                                                                                                 for (int n = 1; n < X_date.length; n++) {

                                                                                                                                                                                     if ((X_month[n] == i) && (Integer.valueOf(label_month_two) == X_year[n])) {
                                                                                                                                                                                         map = new HashMap<String, String>();
                                                                                                                                                                                         map = new HashMap<String, String>();
                                                                                                                                                                                         map.put("Date", X_date[n]);
                                                                                                                                                                                         map.put("Time", X_time[n]);
                                                                                                                                                                                         final int Critical [] =new int [6];

                                                                                                                                                                                         if(data_span_one[n]> Critical[4]) {
                                                                                                                                                                                             map.put("Data", "+"+String.valueOf(data_span_one[n]));
                                                                                                                                                                                         }
                                                                                                                                                                                         else if(data_span_one[n]< Critical[3]) {
                                                                                                                                                                                             map.put("Data", "-"+String.valueOf(data_span_one[n]));
                                                                                                                                                                                         }else if(data_span_one[n]== Critical[3]){
                                                                                                                                                                                             map.put("Data", " "+String.valueOf(data_span_one[n]));
                                                                                                                                                                                         }else {
                                                                                                                                                                                             map.put("Data", " "+String.valueOf(data_span_one[n]));
                                                                                                                                                                                         }


                                                                                                                                                                                         MyArrList.add(map);


                                                                                                                                                                                     }


                                                                                                                                                                                 }


                                                                                                                                                                             }


                                                                                                                                                                         }


                                                                                                                                                                         SimpleAdapter simpleAdapterData;
                                                                                                                                                                         simpleAdapterData = new SimpleAdapter(getActivity(), MyArrList, R.layout.acivity_column2,
                                                                                                                                                                                 new String[]{"Date", "Time", "Data"}, new int[]{R.id.article_1, R.id.article_2, R.id.article_3});

                                                                                                                                                                         listView_month.setAdapter(simpleAdapterData);


                                                                                                                                                                     }


                                                                                                                                                                     public void onNothingSelected(AdapterView<?> parent) {


                                                                                                                                                                     }
                                                                                                                                                                 }
                                                                                                                        );


                                                                                                                    }


                                                                                                                    public void onNothingSelected(AdapterView<?> parent) {


                                                                                                                    }
                                                                                                                }
                                                                       );


                                                                   } catch (JSONException e) {
                                                                       // TODO Auto-generated catch block
                                                                       e.printStackTrace();
//                                                   Toast.makeText(getActivity(), "ข้อมูลJson ผิดพลาด", Toast.LENGTH_SHORT).show();
                                                                   } catch (ParseException e) {
                                                                       e.printStackTrace();
                                                                   }


                                                               }

                                                               public void onNothingSelected(AdapterView<?> parent) {

                                                               }


                                                           }
        );



    }







    public void progress_start(){

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("loadddd");
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.show();
    }
    public void progress_end(){
        progressDialog.dismiss();
    }


    public String getJSON(String url,List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader_buffer = new BufferedReader
                        (new InputStreamReader(content));

                String line;
                while ((line = reader_buffer.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download file..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "คุณยังไม่เชื่อมต่ออินเทอร์เน็ต" ,Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "คุณยังไม่เชื่อมต่ออินเทอร์เน็ต" ,Toast.LENGTH_SHORT).show();
        }
        return str.toString();

    }}

