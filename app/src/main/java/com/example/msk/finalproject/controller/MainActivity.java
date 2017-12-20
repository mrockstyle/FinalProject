package com.example.msk.finalproject.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.fragment.FragmentEditData;
import com.example.msk.finalproject.fragment.FragmentMap;
import com.example.msk.finalproject.fragment.FragmentReport;
import com.example.msk.finalproject.fragment.FragmentWaterLevel;
import com.example.msk.finalproject.util.NotificationService;


public class MainActivity extends AppCompatActivity {

    //Variables
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;
    private String[] Menu;

    private TextView tv_Fname;
    private LinearLayout menuDrawerlayout;
    private Integer userID;
    private String Firstname;
    private String Lastname;
    private Boolean isAdmin;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(); //ทำ hamburger icon

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
                Log.i("Title","mTitle : "+mTitle);
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                Log.i("Title","mDrawTitle : "+mDrawerLayout);
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
                        .replace(R.id.mainContainer, FragmentEditData.newInstance())
                        .commit();
                    break;
                case 3 : getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, FragmentReport.newInstance())
                        .commit();
                    break;
                case 4 : signOut();
                    finish();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
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
                        .replace(R.id.mainContainer, FragmentReport.newInstance())
                        .commit();
                    break;
                case 3 : signOut();
                    finish();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    break;
            }
        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(Menu[position]);
        mDrawerLayout.closeDrawer(menuDrawerlayout);
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

}
