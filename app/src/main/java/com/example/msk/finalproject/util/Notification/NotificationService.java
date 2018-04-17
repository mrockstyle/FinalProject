package com.example.msk.finalproject.util.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.controller.Constant;
import com.example.msk.finalproject.controller.MainActivity;
import com.example.msk.finalproject.manager.HttpManager;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MsK on 20/12/2017 AD.
 */

public class NotificationService extends Service {

    JSONArray data;
    JSONObject c;
    private  int length_one,length_two=0;
    private int count =0;
    private NotificationCompat.Builder  notification , notification_two;
    private Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    private Timer mtimer;
    private Boolean available = true,isCountDown = false;
    private Boolean available2 = true,isCountDown2 = false;
    private Context mContext;
    private Integer locationID;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mContext = Contextor.getInstance().getContext();

        mtimer =new Timer();
        mtimer.schedule(timerTask,0,1000);


        super.onCreate();
    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            notification_data_water();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

        }catch (Exception e){
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private  void notification_data_water(){

        boolean  check_one=false;
        boolean  check_two=false;

        ArrayList<String> location_notifi =new ArrayList<String>();
        ArrayList<String> location_notifi_two =new ArrayList<String>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        List<NameValuePair> params_table = new ArrayList<NameValuePair>();
        try {
            data = new JSONArray(HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_WATER_LEVEL_INFO, params_table)); //GetJSON

            length_one =data.length();

            //check data.length first time
            if(length_two==0){
                length_two=data.length();
            }


            if(length_one >length_two) {

                length_two = data.length();


                for (int j = 1; j < data.length(); j++) {

                    c = data.getJSONObject(j);
                    JSONObject e = data.getJSONObject(j - 1);



                    //check data now
                    if (!(c.getString("location_name").equalsIgnoreCase(e.getString("location_name")))) {


                        if (((e.getInt("data")) >= (e.getInt("critical_point")))&&(e.getInt("sensorID")==01)) {


                            location_notifi.add(e.getString("location_name"));


                            count = j;
                            check_one = true;


                        } else if (((e.getInt("data")) >= (e.getInt("alert_point")))&&(e.getInt("sensorID")==01)) {

                            location_notifi_two.add(e.getString("location_name"));

                            count = j;
                            check_two = true;
                        }
                    }
                }

                //check data last
                int len =data.length()-1;

                JSONObject le = data.getJSONObject(len);

                if (((le.getInt("data")) >= ((le.getInt("critical_point"))))&&(le.getInt("sensorID")==01)) {

                    location_notifi.add(le.getString("location_name"));

                    locationID = le.getInt("locationID");

                    check_one = true;


                } else if (((le.getInt("data")) >= (le.getInt("alert_point")))&&(le.getInt("sensorID")==01)) {

                    location_notifi_two.add(le.getString("location_name"));

                    locationID = le.getInt("locationID");

                    check_two = true;
                }
            }

            //Log.i("Data","check_one : "+check_one);
            //Log.i("Data","check_two : "+check_two);

        } catch (JSONException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();

        }


        if(check_one && available){


            notification =
                    new NotificationCompat.Builder(this); // this is context
            notification.setSmallIcon(R.drawable.logo_water);
            notification.setContentTitle("Water Level Monitoring ");
            notification.setContentText(" (" + location_notifi.size() + ")  Location  " + "" + " \n ระดับน้ำถึง ระดับวิกฤต  แล้วครับ :)");
            notification.setSound(uri);


            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle("ระดับน้ำถึง ระดับวิกฤต");
            inboxStyle.setSummaryText(location_notifi.size() + " Location                         [--คลิกเพื่อดูข้อมูล--]");



            for (int i=0; i < location_notifi.size(); i++) {
                inboxStyle.addLine("Location : " + location_notifi.get(i));
            }

            notification.setStyle(inboxStyle);


            Intent result =new Intent(this,MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(result);
            PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

            notification.setContentIntent(resultPendingIntent);


            NotificationManager notificationManager_two =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager_two.notify(1000, notification.build());

            Intent intent = new Intent();
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setAction("action");
            intent.putExtra("key","from notification");
            intent.putExtra("locationID",locationID);
            sendBroadcast(intent);

            available = false;

            if (!available && !isCountDown){
                CountDown();
                isCountDown = true;
            }

        }

        if(check_two && available2){



            notification_two =
                    new NotificationCompat.Builder(this); // this is context


            notification_two.setSmallIcon(R.drawable.logo_water);
            notification_two.setContentTitle("Water Level Monitoring ");
            notification_two.setContentText("  (" + location_notifi_two.size() + ") Location  \n ระดับน้ำถึง ระดับเตือนภัย  แล้วครับ :)");
            notification_two.setSound(uri);
            NotificationCompat.InboxStyle inboxStyle_two = new NotificationCompat.InboxStyle();

            inboxStyle_two.setBigContentTitle("ระดับน้ำถึง ระดับเตือนภัย ");
            inboxStyle_two.setSummaryText(location_notifi_two.size() +  " Location                     [--คลิกเพื่อดูข้อมูล--]");


            for (int i=0; i < location_notifi_two.size(); i++) {
                inboxStyle_two.addLine("Location : " + location_notifi_two.get(i));
            }
            notification_two.setStyle(inboxStyle_two);


            Intent result =new Intent(this,MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(result);
            PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

            notification_two.setContentIntent(resultPendingIntent);




            NotificationManager notificationManager_two =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager_two.notify(1001, notification_two.build());

            available2 = false;

            if (!available2 && !isCountDown2){
                CountDown2();
                isCountDown2 = true;
            }

        }



    }

    private void CountDown() {

        Timer timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            int counter = 30;
            @Override
            public void run() {
                counter--;
                Log.i("Counter","Count = "+counter);
                if (counter == 0){
                    Log.i("Counter","Notification Available !!");
                    available = true;
                    isCountDown = false;
                    timer1.cancel();
                }
            }
        },0,1000);

    }

    private void CountDown2() {

        Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            int counter = 30;
            @Override
            public void run() {
                counter--;
                Log.i("Counter","Count2 = "+counter);
                if (counter == 0){
                    Log.i("Counter","Notification2 Available !!");
                    available2 = true;
                    isCountDown2 = false;
                    timer2.cancel();
                }
            }
        },0,1000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.i("Data","OnDestroy");
        mtimer.cancel();
        timerTask.cancel();
    }
}
