package com.oustme.oustsdk.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.Calendar;

/**
 * Created by admin on 1/29/16.
 */
public class AlarmReceiverOnBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                Log.e("AlaBoot", "intent");
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                } else {
                    pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                }

                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                // Set the alarm to start at 10:00 AM
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 4);
                calendar.set(Calendar.SECOND, 50);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 20000 * 1000, pendingIntent);

                Intent alarmIntent1 = new Intent(context, CourseNotificationReceiver.class);
                PendingIntent pendingIntent1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pendingIntent1 = PendingIntent.getService(context, 0, alarmIntent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                } else {
                    pendingIntent1 = PendingIntent.getService(context, 0, alarmIntent1, PendingIntent.FLAG_UPDATE_CURRENT);
                }
                // Set the alarm to start at 10:00 AM
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTimeInMillis(System.currentTimeMillis());
                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis() + (63 * 1000), 30 * 1000, pendingIntent1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
