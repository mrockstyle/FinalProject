package com.example.msk.finalproject.util;



import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import com.example.msk.finalproject.controller.Constant;

import com.example.msk.finalproject.manager.HttpManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ProximityIntentReceiver extends BroadcastReceiver {
    private JSONObject jsonObject;
    private List<NameValuePair> params;
    private SharedPreferences preferences;
    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        Boolean entering = intent.getBooleanExtra(key, false);
        Integer safeID = intent.getIntExtra("safeID",0);
        Integer contain;
        preferences = context.getSharedPreferences(Constant.USER_PREF,0);

        contain = getContainData(safeID);
        Log.i("Value","safeID : "+safeID+",Contain = "+contain);


        if (entering && !preferences.getBoolean(Constant.IS_ENTERED,true)) {
            contain++;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Constant.IS_ENTERED,true);   //update pref ว่า user ได้เข้าพื้นที่ปลอดภัยแล้ว
            editor.apply();

            Toast.makeText(context,"คุณได้เข้าสู่พื้นที่ปลอดภัย",Toast.LENGTH_LONG).show();

        } else if (!entering){
            contain--;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Constant.IS_ENTERED,false);
            editor.apply();
            Toast.makeText(context,"คุณได้ออกจากพื้นที่ปลอดภัย",Toast.LENGTH_LONG).show();
        } else if (entering && preferences.getBoolean(Constant.IS_ENTERED,true)){
            Toast.makeText(context,"คุณอยู่ในพื้นที่ปลอดภัย",Toast.LENGTH_LONG).show();
        }

        updateContainData(safeID,contain);
    }

    private void updateContainData(Integer safeID,Integer contain) {
        params = new ArrayList<>();
        params.add(new BasicNameValuePair("safeID",String.valueOf(safeID)));
        params.add(new BasicNameValuePair("contain",String.valueOf(contain)));

        HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_UPDATE_CONTAIN,params);
    }

    private Integer getContainData(Integer safeID) {
        params = new ArrayList<>();
        params.add(new BasicNameValuePair("safeID",String.valueOf(safeID)));

        Integer contain = 0;

        try {
            jsonObject = new JSONObject(HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_GET_CONTAIN,params));
            contain = jsonObject.getInt("contain");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contain;
    }
}

