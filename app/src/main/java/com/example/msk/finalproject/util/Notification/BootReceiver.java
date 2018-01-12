package com.example.msk.finalproject.util.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by MsK on 22/12/2017 AD.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Server Stop", "Ohhhh");
        context.startService(new Intent(context, NotificationService.class));
    }
}
