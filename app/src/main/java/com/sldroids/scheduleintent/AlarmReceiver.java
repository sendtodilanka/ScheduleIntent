package com.sldroids.scheduleintent;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Copyright(c) SLDroid Creations (Pvt) Ltd.  All Rights Reserved.
 * This software is the proprietary information of SLDroid Creations (Pvt) Ltd.
 * .
 * Created by dilanka on 5/9/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        notifyThis(context, "Schedule Intent", "Alarm is received");
        context.startService(new Intent(context, DownloadService.class));

        wl.release();
    }

    public void setAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), 1000 * 60 * 2, pi);

        Log.d(TAG, "++ Set Alarm ++");
    }

    public void notifyThis(Context context, String title, String message) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("{your tiny message}")
                .setContentTitle(title)
                .setContentText(message)
                .setContentInfo("INFO");

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, b.build());
    }
}
