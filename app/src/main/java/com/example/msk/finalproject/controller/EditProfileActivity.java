package com.example.msk.finalproject.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.fragment.FragmentEditProfile;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.editProfContainer, FragmentEditProfile.newInstance())
                    .commit();
        }

    }
}
