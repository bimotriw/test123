package com.oustme.oustsdk.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.MyLifeCycleHandler;
import com.oustme.oustsdk.tools.OustPreferences;

import java.util.List;
import java.util.Random;


public class AlarmReceiver extends BroadcastReceiver {
    private boolean otherNotificationDisable = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            long currenttimestamp = System.currentTimeMillis() / 1000;
            otherNotificationDisable = OustPreferences.getAppInstallVariable("otherNotificationDisable");
            Long lastVisitedTime = OustPreferences.getTimeForNotification("currentOustTime");
            if (currenttimestamp > (lastVisitedTime + (172000))) {
                OustPreferences.saveTimeForNotification("currentOustTime", currenttimestamp);
                Random ran = new Random();
                int sdk = Build.VERSION.SDK_INT;
                List<String> alarmMsg = OustPreferences.getLoacalNotificationMsgs("comebackMsg");
                if (alarmMsg.size() > 0) {
                    int x = ran.nextInt(alarmMsg.size());
                    String content = alarmMsg.get(0);
                    if (sdk > Build.VERSION_CODES.JELLY_BEAN) {
                        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                        notification.setContentTitle("Oust");
                        notification.setContentText(content);
                        notification.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
                        notification.setAutoCancel(true);
                        notification.setTicker(content);
                        notification.setSmallIcon(R.drawable.app_icon);
                        //Intent resultIntent = new Intent(context, LandingActivity.class);
                        String userdata = OustPreferences.get("userdata");
                        //resultIntent.putExtra("ActiveUser", userdata);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        //stackBuilder.addNextIntent(resultIntent);
                        //stackBuilder.addNextIntentWithParentStack(resultIntent);
                        PendingIntent pIntent;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                        } else {
                            pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        }
                        notification.setContentIntent(pIntent);
                        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (isAppIsInBackground(context)) {
                            if (!otherNotificationDisable) {
                                applyCount();
                                manager.notify(0, notification.build());
                            }
                        }
                    }
                }
            }
//            Intent intent1=new Intent(OustApplication.getContext(), SendPingApiService.class);
//            OustApplication.getContext().startService(intent1);
//
//            Intent intent2 = new Intent(context, SubmitRequestsService.class);
//            OustApplication.getContext().startService(intent2);
//
//            Intent intent3 = new Intent(OustApplication.getContext(), SubmitCourseCompleteRequestService.class);
//            OustApplication.getContext().startService(intent3);
//
//            Intent intent4 = new Intent(OustApplication.getContext(), SubmitLevelCompleteRequestService.class);
//            OustApplication.getContext().startService(intent4);

        } catch (Exception e) {
        }
    }

    private boolean isAppIsInBackground(Context context) {
        return MyLifeCycleHandler.stoppedActivities == MyLifeCycleHandler.startedActivities;
    }

    private void applyCount() {
        try {
            int badgeCout = OustPreferences.getSavedInt("notificationbadgecount");
            badgeCout++;
            OustPreferences.saveintVar("notificationbadgecount", badgeCout);
        } catch (Exception e) {
        }
    }
}
