package com.example.msk.finalproject.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.fragment.FragmentCompareAlgo;
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
                    .add(R.id.mapContainer, FragmentCompareAlgo.newInstance())
                    .add(R.id.mapPanel, FragmentMapPanel.newInstance())
                    .commit();
        }

    }
}
