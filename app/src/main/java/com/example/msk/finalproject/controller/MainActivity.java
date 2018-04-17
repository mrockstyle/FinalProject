package com.example.msk.finalproject.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.LocationManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.dao.LocationInfo;
import com.example.msk.finalproject.dao.SafePlace;
import com.example.msk.finalproject.dao.UserHome;
import com.example.msk.finalproject.fragment.FragmentCompareAlgo;
import com.example.msk.finalproject.fragment.FragmentEditData;
import com.example.msk.finalproject.fragment.FragmentEditProfile;
import com.example.msk.finalproject.fragment.FragmentMap;
import com.example.msk.finalproject.fragment.FragmentReport;
import com.example.msk.finalproject.fragment.FragmentWaterLevel;
import com.example.msk.finalproject.fragment.FragmentWaterReport;
import com.example.msk.finalproject.manager.HttpManager;
import com.example.msk.finalproject.util.DataParser;
import com.example.msk.finalproject.util.GetData;
import com.example.msk.finalproject.util.GetData2;
import com.example.msk.finalproject.util.Notification.NotificationService;
import com.example.msk.finalproject.util.ProximityIntentReceiver;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import me.itangqi.waveloadingview.WaveLoadingView;


public class MainActivity extends AppCompatActivity {

    //Variables
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private AlertDialog.Builder ad;
    //private FlowingDrawer flowingDrawer;

    private CharSequence mTitle;
    private String[] Menu;

    private TextView tv_Fname;
    private LinearLayout menuDrawerlayout;
    private Integer userID;
    private String Firstname;
    private String Lastname;
    private Boolean isAdmin;

    private SharedPreferences preferences;

    //Location
    private LocationManager locationManager;
    private static final String PROX_ALERT_INTENT = "com.example.msk.finalproject.util.proximityintentreceiver";

    //Object
    private List<SafePlace> safePlaceList;
    private List<LocationInfo> locationInfoList;
    private UserHome userHome;
    private GetData2 getData2;
    private List<NameValuePair> params;
    private List<Double> distanceList;

    //Proximity
    private ProximityIntentReceiver proximityIntentReceiver;

    //context
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = Contextor.getInstance().getContext();

        init();
        initProximity(); // สร้างขอบเขตนับคน
        //initDistance();
        //initSafePlaceDistance();

        if (savedInstanceState == null) {
            selectItem(0);
        }

    }

    private void init() {
        getUserPref(); //อ่านข้อมูล user ที่ login ไว้
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //set ให้ actionbar กลายเป็น toolbar
        createDrawerLayout();

        startService(new Intent(this, NotificationService.class)); // เริ่ม service การแจ้งเตือนระดับน้ำ

        tv_Fname = findViewById(R.id.tv_Fname);
        showUserProfile();
        //////////////////////////////////////
        safePlaceList = new ArrayList<>();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        proximityIntentReceiver = new ProximityIntentReceiver();

    }

    private void initProximity() {
        getSafePlaceData(); //load safePlace from server

        for (int i=0;i<safePlaceList.size();i++){          ///add proximity each safePlace
            addProximity(safePlaceList.get(i).getSafeID()
                    ,i
                    ,safePlaceList.get(i).getLat()
                    ,safePlaceList.get(i).getLng());
        }
        registerProximity();
    }

    private void initDistance() {
        getUserHomeData(); // load user data form server
        distanceList = new ArrayList<>();
        //cal distance
        for (int i=0 ; i < locationInfoList.size(); i++){

            distanceList.add(SphericalUtil.computeDistanceBetween(
                    new LatLng(userHome.getLat(),userHome.getLng())
                    ,new LatLng(locationInfoList.get(i).getLat(),locationInfoList.get(i).getLng())));

        }

        Log.i("Value","distanceList = "+distanceList);
    }

    private void initSafePlaceDistance() {
        for (int i = 0; i < safePlaceList.size(); i++){
            for (int j = 0; j < safePlaceList.size(); j++){
                if (i != j){
                    String polylines;
                    Double distance;
                    params = new ArrayList<>();
                    String directionString = HttpManager
                            .getInstance().getHttpPost(Constant.URL_GOOGLE_DIRECTION+"origin="+safePlaceList.get(i).getLat()+","+safePlaceList.get(i).getLng()
                                    +"&destination="+safePlaceList.get(j).getLat()+","+safePlaceList.get(j).getLng()+"&mode=walking&key="+Constant.GOOGLE_MAP_KEY,params);

                    DataParser dataParser = new DataParser();
                    polylines = dataParser.parseDirections(directionString);
                    //distance = dataParser.getDistance(directionString);
                    //updateSafePath(safePlaceList.get(i).getSafeID(),safePlaceList.get(j).getSafeID(),distance,polylines);
                    //Log.i("Path","("+safePlaceList.get(i).getSafeID()+","+safePlaceList.get(j).getSafeID()+") = "+distance);
                }
            }
        }
    }

    private void updateSafePath(Integer safeIDs, Integer safeIDd, Double distance, String polylines) {
        params = new ArrayList<>();
        params.add(new BasicNameValuePair("safeIDstart",String.valueOf(safeIDs)));
        params.add(new BasicNameValuePair("safeIDdestination",String.valueOf(safeIDd)));
        params.add(new BasicNameValuePair("distance",String.valueOf(distance)));
        params.add(new BasicNameValuePair("polyline",String.valueOf(polylines)));

        HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_WRITE_SAFEPATH,params); //update safe path
    }

    private void updatePath(String directionList,Integer safeID,Double distance,Integer userID) {

        params = new ArrayList<>();
        params.add(new BasicNameValuePair("safeID",String.valueOf(safeID)));
        params.add(new BasicNameValuePair("user_distance",String.valueOf(distance)));
        params.add(new BasicNameValuePair("user_polyline",directionList));
        params.add(new BasicNameValuePair("userIDpath",String.valueOf(userID)));

        HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_UPDATE_USERPATH,params); //update path

    }

    private void getUserHomeData() {
        getData2 = new GetData2();
        getData2.setUserHomeData(userID);
        getData2.setLocationData();
        userHome = getData2.getUserHome();
        locationInfoList = getData2.getLocationInfosList();
    }


    @SuppressLint("MissingPermission")
    private void addProximity(Integer safeID, int i, Double lat, Double lng) {
        Intent intent = new Intent(PROX_ALERT_INTENT);
        intent.putExtra("safeID", safeID);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        locationManager.addProximityAlert(lat , lng, 150, -1, proximityIntent);
    }

    private void registerProximity() {
        IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
        registerReceiver(new ProximityIntentReceiver(), filter);
    }

    private void getSafePlaceData() {
        getData2 = new GetData2();
        getData2.setSafePlaceData();
        safePlaceList = getData2.getSafePlaceList();
    }

    private void getUserPref() {
        preferences = getSharedPreferences(Constant.USER_PREF,0);
        userID = preferences.getInt(Constant.USER_ID,0);
        Firstname = preferences.getString(Constant.USER_FNAME,null);
        Lastname = preferences.getString(Constant.USER_LNAME,null);
        isAdmin = preferences.getBoolean(Constant.IS_ADMIN,true);
    }


    private void showUserProfile() {

        tv_Fname.setText(Firstname);
        Log.i("Value","userID : "+userID);
        Log.i("Value","Firstname : "+Firstname);
        Log.i("Value","Lastname : "+Lastname);
        Log.i("Value","isAdmin : "+isAdmin);

    }


    private void createDrawerLayout() {
        mTitle = getTitle();

        if (isAdmin){
            Menu = getResources().getStringArray(R.array.admin_menu);
        }else {
            Menu = getResources().getStringArray(R.array.user_menu);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        menuDrawerlayout = findViewById(R.id.menuContainer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.listview_menu, Menu));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.drawer_open,R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                //Log.i("Title","mTitle : "+mTitle);
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                //Log.i("Title","mDrawTitle : "+mDrawerLayout);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }



    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        if (isAdmin){
            switch (position){
                case 0 : getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer,FragmentWaterLevel.newInstance())
                        .commit();
                    break;
                case 1 : getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer,FragmentMap.newInstance())
                        .commit();
                    break;
                case 2 : getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, FragmentWaterReport.newInstance())
                        .commit();
                    break;
                case 3 : getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, FragmentEditData.newInstance())
                        .commit();
                    break;

                case 4 : getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, FragmentEditProfile.newInstance())
                        .commit();
                    break;
                case 5 : getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, FragmentCompareAlgo.newInstance())
                        .commit();
                    break;
                case 6 : showDialogBox(); //sign out

                    break;
            }
        }else {
            switch (position){
                case 0 : getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer,FragmentWaterLevel.newInstance())
                        .commit();
                    break;
                case 1 : getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer,FragmentMap.newInstance())
                        .commit();
                    break;
                case 2 : getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, FragmentWaterReport.newInstance())
                        .commit();
                    break;
                case 3 : getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, FragmentEditProfile.newInstance())
                        .commit();
                    break;
                case 4 :
                    showDialogBox(); //sign out
                    break;
            }
        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(Menu[position]);
        mDrawerLayout.closeDrawer(menuDrawerlayout);
    }

    private void showDialogBox() {
        ad = new AlertDialog.Builder(this);
        ad.setTitle("Attention");
        ad.setMessage("Are you sure to sign out ?");
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                signOut();
                finish();
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            }
        });
        ad.setNegativeButton("No",null);
        ad.show();
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void signOut() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constant.USER_ID,0);
        editor.putString(Constant.USER_FNAME,null);
        editor.putString(Constant.USER_LNAME,null);
        editor.putBoolean(Constant.IS_LOGGED_IN,false);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (proximityIntentReceiver != null){
            unregisterReceiver(proximityIntentReceiver);
        }
    }
}
