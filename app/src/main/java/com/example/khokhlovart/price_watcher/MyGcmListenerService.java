package com.example.khokhlovart.price_watcher;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Dom on 02.12.2017.
 */

public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("!!!!--->", "From:onCreate");
    }



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
    }


    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.e("!!!!--->", "From: 11");
        if (from.equals(MainActivity.SENDER_ID)) {
            String message = data.getString("message");
            Log.e("!!!!--->", "From: " + from);
            Log.e("!!!!--->", "Message: " + message);


            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle(getString(R.string.notification_head))
                            .setContentText(" !!! " + message.toString())
                            //.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000})
                            .setAutoCancel(true);

            Intent resultIntent = new Intent(this, MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());

        }
    }
}