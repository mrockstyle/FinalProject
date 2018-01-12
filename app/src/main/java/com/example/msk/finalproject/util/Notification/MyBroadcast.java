package com.example.msk.finalproject.util.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.msk.finalproject.controller.Constant;
import com.example.msk.finalproject.controller.EvacuateMapActivity;

/**
 * Created by MsK on 23/12/2017 AD.
 */

public class MyBroadcast extends BroadcastReceiver {

    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("Value","critical!!!");

        updatePref(context);//เพิ่มสถานะ user ว่าเกิด alert แล้วนะ


        Intent mIntent = new Intent(context, EvacuateMapActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //เปิด activity อพยพ
        context.startActivity(mIntent);

    }

    private void updatePref(Context context) {
        preferences = context.getSharedPreferences(Constant.USER_PREF,0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constant.IS_ALERT,true);
        editor.apply();
    }
}
