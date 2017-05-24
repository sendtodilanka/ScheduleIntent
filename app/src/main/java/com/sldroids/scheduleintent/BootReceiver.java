package com.sldroids.scheduleintent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Copyright(c) SLDroid Creations (Pvt) Ltd.  All Rights Reserved.
 * This software is the proprietary information of SLDroid Creations (Pvt) Ltd.
 * .
 * Created by dilanka on 5/9/17.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            new AlarmReceiver().setAlarm(context);
        }
    }
}
