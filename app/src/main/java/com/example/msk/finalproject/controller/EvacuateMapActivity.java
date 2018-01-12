package com.example.msk.finalproject.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.fragment.FragmentMap;
import com.example.msk.finalproject.fragment.FragmentMapPanel;

public class EvacuateMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evacuate_map);

        if (savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mapContainer, FragmentMap.newInstance())
                    .add(R.id.mapPanel, FragmentMapPanel.newInstance())
                    .commit();
        }



    }
}
