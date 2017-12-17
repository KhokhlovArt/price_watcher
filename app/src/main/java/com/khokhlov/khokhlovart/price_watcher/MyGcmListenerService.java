package com.khokhlov.khokhlovart.price_watcher;

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
    public static int notifivation_count = 0;
    public static final int MAX_NOTIFICATION = 100;

    @Override
    public void onCreate() {
        super.onCreate();
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

        if (from.equals(MainActivity.SENDER_ID)) {
            String message = data.getString("message");
            String title   = data.getString("title");
            App apl = (App) getApplication();
            String notif_options = apl.getPreferences(apl.OPTIONS_NOTIFICATION);
            if (notif_options.equals("true")) {
                sendNotification(title, message);
            }
        }
    }

    void sendNotification(String titel, String text)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle((titel == null) ? "Изменения" : titel)
                        .setContentText((text == null) ? "Новые изменения" : text)
                        .setNumber(notifivation_count)
                        //.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000})
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder.mNumber = notifivation_count;
        mNotificationManager.notify(notifivation_count, mBuilder.build());
        notifivation_count++;
        notifivation_count = notifivation_count > MAX_NOTIFICATION ? 0 : notifivation_count;

    }

}