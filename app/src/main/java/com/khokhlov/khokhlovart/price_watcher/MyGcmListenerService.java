package com.khokhlov.khokhlovart.price_watcher;

import android.app.AlarmManager;
import android.app.Notification;
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
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Dom on 02.12.2017.
 */

public class MyGcmListenerService extends FirebaseMessagingService {
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


    //public void onMessageReceived(String from, Bundle data) {
    @Override
    public void onMessageReceived(RemoteMessage message){
        String from = message.getFrom();
        Map data = message.getData();

        if (from.equals(MainActivity.SENDER_ID)) {
            String mess = data.get("message").toString();
            String title   = data.get("title").toString();

            App apl = (App) getApplication();
            String notificationOptions = apl.getPreferences(apl.OPTIONS_NOTIFICATION);
            if (notificationOptions.equals("true")) {

                sendNotification(title, mess);
                String id = data.get("priceId").toString(); // Сохраняем в пямяти какие элементы надо будет подсветить
                if (id != null) {
                    String lightItems = apl.getPreferences(apl.IS_CHANGE_ITEM);
                    apl.setPreferences(apl.IS_CHANGE_ITEM, lightItems + "," + id.toString());
                }
            }
        }
    }

    void sendNotification(String titel, String text)
    {
        titel = notifivation_count != 0 ? "+" + (notifivation_count + 1) + " " + getApplicationContext().getResources().getString(R.string.notification_head_group) : titel;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle((titel == null) ? "Changes" : titel)
                        .setContentText((text == null) ? "New changes" : text)
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
        //mNotificationManager.notify(notifivation_count, mBuilder.build()); //что бы не группировать нотификации а показывать каждую отдельно
        mNotificationManager.notify(0, mBuilder.build());
        notifivation_count++;
        notifivation_count = notifivation_count > MAX_NOTIFICATION ? 0 : notifivation_count;

    }

}