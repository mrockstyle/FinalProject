package com.example.msk.finalproject.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.fragment.FragmentLogIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences(Constant.USER_PREF,0);


        if (savedInstanceState == null){

            if (preferences.getBoolean(Constant.IS_LOGGED_IN,false)){
                //if user signed in
                //Change activity
                finish();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.loginContainer, FragmentLogIn.newInstance())
                        .commit();
            }

        }

    }
}
