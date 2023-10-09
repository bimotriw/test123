package com.oustme.oustsdk.reminderNotification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.notification.model.NotificationResponse;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.service.OustNotificationHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReminderNotificationManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("alertTitle");
        String content = intent.getStringExtra("alertContent");
        String type = intent.getStringExtra("alertType");
        String imageUrl = intent.getStringExtra("alertImage");
        int id = intent.getIntExtra("alertId", 0);
        int requestCode = intent.getIntExtra("requestCode", 0);
        String time = intent.getStringExtra("time");

        try {
            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        Log.e("TAG", "alertTitle--> " + title + " id--> " + id);

        NotificationResponse notificationResponse = new NotificationResponse();
        notificationResponse.setTitle("REMINDER: Please complete pending task");
        notificationResponse.setMessage(content);
        notificationResponse.setImageUrl(imageUrl);
        notificationResponse.setContentId(String.valueOf(id));
        notificationResponse.setType(type);
        notificationResponse.setUpdateTime(time);
        notificationResponse.setKey("0");
        notificationResponse.setFireBase(false);
        notificationResponse.setRead(true);

        if (id != 0) {
            if (!RoomHelper.getNotificationByContentId(String.valueOf(id))) {
                saveNotificationRoomDb(notificationResponse);
            }
        }

        Map<String, String> notification = new HashMap<>();
        notification.put("id", "" + id);
        notification.put("title", "REMINDER: Please complete pending task");
        notification.put("type", type);
        notification.put("imageUrl", imageUrl);
        notification.put("message", content);
        notification.put("requestCode", "" + requestCode);
        Log.e("Notification", "onReceive: -- Reminder Notification Manager -id-> " + id + "  Type---> " + type);

        ArrayList<CommonLandingData> pendingListDataCommonLanding;
        String pendingListData = OustPreferences.get("savePendingListData");
        if (pendingListData != null && !pendingListData.isEmpty()) {
            pendingListDataCommonLanding = new Gson().fromJson(pendingListData, new TypeToken<List<CommonLandingData>>() {
            }.getType());
            if (pendingListDataCommonLanding != null && pendingListDataCommonLanding.size() > 0) {
                String moduleId = "";
                if (type != null && type.equalsIgnoreCase("COURSE_REMINDER")) {
                    moduleId = "COURSE" + id;
                } else if (type != null && type.equalsIgnoreCase("ASSESSMENT_REMINDER")) {
                    moduleId = "ASSESSMENT" + id;
                }
                boolean dataFound = false;
                for (CommonLandingData landingData : pendingListDataCommonLanding) {
                    if (landingData.getId() != null && !landingData.getId().isEmpty() && !moduleId.isEmpty() && landingData.getId().equalsIgnoreCase(moduleId)) {
                        dataFound = true;
                        break;
                    }
                }
                if (dataFound) {
                    OustNotificationHandler oustNotificationHandler = new OustNotificationHandler(notification, context);
                } else {
                    Log.e("TAG", "onReceive: --> remove Reminder Notification Manager--> -id-> " + id + "  Type---> " + type );
                    removeReminderNotification(requestCode);
                }
            } else {
                Log.e("TAG", "onReceive: --> remove Reminder Notification Manager--> -id-> " + id + "  Type---> " + type );
                removeReminderNotification(requestCode);
            }
        }
    }

    private void removeReminderNotification(int requestCode) {
        try {
            AlarmManager alarmMgr = (AlarmManager) OustSdkApplication.getContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(OustSdkApplication.getContext(), ReminderNotificationManager.class);
            PendingIntent pendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_MUTABLE);
            } else {
                pendingIntent = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE);
            }

            boolean isAlready = (pendingIntent != null);
            if (isAlready) {
                PendingIntent pendingIntentCancel;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pendingIntentCancel = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
                } else {
                    pendingIntentCancel = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                }
                assert alarmMgr != null;
                alarmMgr.cancel(pendingIntentCancel);
                pendingIntentCancel.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void saveNotificationRoomDb(NotificationResponse notificationData) {
        List<NotificationResponse> notificationResponses = new ArrayList<>();
        notificationResponses.add(notificationData);
        notificationResponses.size();
        RoomHelper.addNotificationData(notificationResponses);
    }
}
