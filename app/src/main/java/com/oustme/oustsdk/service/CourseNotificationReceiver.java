package com.oustme.oustsdk.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;


import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.FFContest.ContestNotificationMessage;
import com.oustme.oustsdk.layoutFour.LandingActivity;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.MyLifeCycleHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;

/**
 * Created by admin on 27/08/17.
 */

public class CourseNotificationReceiver extends BroadcastReceiver {
    private boolean allNotificationDisable = false;
    private boolean sendPushNotification = true;
    String CHANNEL_ID = "default";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            OustSdkApplication.setmContext(context);
            allNotificationDisable = OustPreferences.getAppInstallVariable("allNotificationDisable");
            sendPushNotification = OustPreferences.getAppInstallVariable("sendPushNotifications");
            if (!allNotificationDisable && sendPushNotification) {
                String userdata = OustPreferences.get("userdata");
                if (userdata != null) {
                    ActiveUser activeUser = OustSdkTools.getActiveUserData(userdata);
                    if (activeUser.getStudentKey() != null) {
                        //check for contest notification
                        String contestNotificationMessage = OustPreferences.get("contestnotification_message");
                        boolean showingContestNotification = true;
                        if ((contestNotificationMessage != null) && (!contestNotificationMessage.isEmpty())) {
                            Gson gson = new Gson();
                            ContestNotificationMessage contestNotificationMessage1 = gson.fromJson(contestNotificationMessage, ContestNotificationMessage.class);
                            long current_time = System.currentTimeMillis();
                            long timeToStartContest = ((contestNotificationMessage1.getStartTime() - current_time) / 1000);
                            Log.e("F3CNotification", "timeToStartContest " + timeToStartContest + " - current_time " + current_time + " - startTime " + contestNotificationMessage1.getStartTime());
                            if (timeToStartContest > 59) {
                                if (timeToStartContest < 86400) {//lesser than 24 hour
                                    if (timeToStartContest <= contestNotificationMessage1.getLastMinuteTime()) {//last minute
                                        String title = contestNotificationMessage1.getLastMinuteMessage();
                                        if ((title != null) && (!title.isEmpty())) {
                                            title = title.replace("<TIME>", ("" + (timeToStartContest / 60)));
                                            if (contestNotificationMessage1.isRegistered()) {
                                                sendContestNotification(contestNotificationMessage1.getPlayBanner(), title, contestNotificationMessage1.getContestID(), context, contestNotificationMessage1.getContestName());
                                            } else {
                                                sendContestNotification(contestNotificationMessage1.getJoinBanner(), title, contestNotificationMessage1.getContestID(), context, contestNotificationMessage1.getContestName());
                                            }
                                            contestNotificationMessage1.setLastNotificationShownTime((System.currentTimeMillis() / 1000));
                                            String contestnotification_message = gson.toJson(contestNotificationMessage1);
                                            OustPreferences.save("contestnotification_message", contestnotification_message);
                                        } else {
                                            showingContestNotification = false;
                                        }
                                    } else if (timeToStartContest < 3600) {//lesser than 1 hour
                                        Log.e("F3CNotification", "current_time " + (current_time / 1000) + " - lastNotification " + contestNotificationMessage1.getLastNotificationShownTime() + " - lessHourTime " + contestNotificationMessage1.getLesshourTime());
                                        if (((current_time / 1000) - contestNotificationMessage1.getLastNotificationShownTime()) > (3600 / contestNotificationMessage1.getLesshourTime())) {
                                            String title = contestNotificationMessage1.getLesshourMessage();
                                            if ((title != null) && (!title.isEmpty())) {
                                                title = title.replace("<TIME>", ("" + (timeToStartContest / 60)));
                                                if (contestNotificationMessage1.isRegistered()) {
                                                    sendContestNotification(contestNotificationMessage1.getPlayBanner(), title, contestNotificationMessage1.getContestID(), context, contestNotificationMessage1.getContestName());
                                                } else {
                                                    sendContestNotification(contestNotificationMessage1.getJoinBanner(), title, contestNotificationMessage1.getContestID(), context, contestNotificationMessage1.getContestName());
                                                }
                                                contestNotificationMessage1.setLastNotificationShownTime((System.currentTimeMillis() / 1000));
                                                String contestnotification_message = gson.toJson(contestNotificationMessage1);
                                                OustPreferences.save("contestnotification_message", contestnotification_message);
                                            } else {
                                                showingContestNotification = false;
                                            }
                                        } else {
                                            showingContestNotification = false;
                                        }
                                    } else {//greater than hour
                                        if (((current_time / 1000) - contestNotificationMessage1.getLastNotificationShownTime()) > (86400 / contestNotificationMessage1.getGreatehourTime())) {
                                            String title = contestNotificationMessage1.getGreatehourMessage();
                                            if ((title != null) && (!title.isEmpty())) {
                                                title = title.replace("<TIME>", ("" + (timeToStartContest / 3600)));
                                                if (contestNotificationMessage1.isRegistered()) {
                                                    sendContestNotification(contestNotificationMessage1.getPlayBanner(), title, contestNotificationMessage1.getContestID(), context, contestNotificationMessage1.getContestName());
                                                } else {
                                                    sendContestNotification(contestNotificationMessage1.getJoinBanner(), title, contestNotificationMessage1.getContestID(), context, contestNotificationMessage1.getContestName());
                                                }
                                                contestNotificationMessage1.setLastNotificationShownTime((System.currentTimeMillis() / 1000));
                                                String contestnotification_message = gson.toJson(contestNotificationMessage1);
                                                OustPreferences.save("contestnotification_message", contestnotification_message);
                                            } else {
                                                showingContestNotification = false;
                                            }
                                        } else {
                                            showingContestNotification = false;
                                        }
                                    }
                                } else {//greater than 24 hour
                                    Log.e("ConNot", "" + (System.currentTimeMillis() / 1000) + " - " + contestNotificationMessage1.getLastNotificationShownTime() + " - " + contestNotificationMessage1.getGreater24Time());
                                    if (((current_time / 1000) - contestNotificationMessage1.getLastNotificationShownTime()) > (86400 / contestNotificationMessage1.getGreater24Time())) {
                                        String title = contestNotificationMessage1.getGreater24Message();
                                        if ((title != null) && (!title.isEmpty())) {
                                            title = title.replace("<TIME>", ("" + (timeToStartContest / 86400)));
                                            if (contestNotificationMessage1.isRegistered()) {
                                                sendContestNotification(contestNotificationMessage1.getPlayBanner(), title, contestNotificationMessage1.getContestID(), context, contestNotificationMessage1.getContestName());
                                            } else {
                                                sendContestNotification(contestNotificationMessage1.getJoinBanner(), title, contestNotificationMessage1.getContestID(), context, contestNotificationMessage1.getContestName());
                                            }
                                            contestNotificationMessage1.setLastNotificationShownTime((System.currentTimeMillis() / 1000));
                                            String contestnotification_message = gson.toJson(contestNotificationMessage1);
                                            OustPreferences.save("contestnotification_message", contestnotification_message);
                                        } else {
                                            showingContestNotification = false;
                                        }
                                    } else {
                                        showingContestNotification = false;
                                    }
                                }
                            } else {
                                Log.e("--------", "time" + contestNotificationMessage1.getLeaderboardNotificationTime());
                                if ((timeToStartContest + 30) > 0) {
                                    //Log.e("--------","timeToStartContest");
                                    String title = contestNotificationMessage1.getContestStartMessage();
                                    if ((title != null) && (!title.isEmpty())) {
                                        if (contestNotificationMessage1.isRegistered()) {
                                            sendContestNotification(contestNotificationMessage1.getPlayBanner(), title, contestNotificationMessage1.getContestID(), context, contestNotificationMessage1.getContestName());
                                        } else {
                                            sendContestNotification(contestNotificationMessage1.getJoinBanner(), title, contestNotificationMessage1.getContestID(), context, contestNotificationMessage1.getContestName());
                                        }
                                    } else {
                                        showingContestNotification = false;
                                    }
                                } else if (((timeToStartContest + contestNotificationMessage1.getLeaderboardNotificationTime()) < 0) &&
                                        ((timeToStartContest + contestNotificationMessage1.getLeaderboardNotificationTime()) > -200)) {
                                    String title = contestNotificationMessage1.getLBReadyMessage();
                                    if (title == null) {
                                        title = "Leaderboard is ready. Check your score_text!";
                                    }
                                    if ((title != null) && (!title.isEmpty()) && (!contestNotificationMessage1.isLBNotificationShown())) {
                                        contestNotificationMessage1.setLBNotificationShown(true);
                                        sendContestNotification(contestNotificationMessage1.getRrBanner(), title, contestNotificationMessage1.getContestID(), context, contestNotificationMessage1.getContestName());
                                        String contestnotification_message = gson.toJson(contestNotificationMessage1);
                                        OustPreferences.save("contestnotification_message", contestnotification_message);
                                    } else {
                                        showingContestNotification = false;
                                    }
                                } else {
                                    showingContestNotification = false;
                                }
                            }
                        } else {
                            showingContestNotification = false;
                        }
                        OustPreferences.clear("coursenotification_messages");
                        if (!OustPreferences.getAppInstallVariable("dailyNotificationDisabled")) {
                            //if not contest send course notification
                            if (!showingContestNotification) {
                                //check for course notification
                                long currentTimestamp = System.currentTimeMillis() / 1000;
                                Calendar now = Calendar.getInstance();

                                int hourOfDay = now.get(Calendar.HOUR_OF_DAY); // Get hour in 24 hour format
                                //check if time is between 9am to 9pm
                                if ((hourOfDay > 9) && (hourOfDay < 20)) {
                                    long notificationInterval = 43200;
                                    if (OustPreferences.getTimeForNotification("notificationInterval") > 0) {
                                        notificationInterval = (OustPreferences.getTimeForNotification("notificationInterval") * 3600);
                                    }
                                    if (OustPreferences.getTimeForNotification("lastNotificationTime") > 0) {
                                        if ((currentTimestamp - (OustPreferences.getTimeForNotification("lastNotificationTime"))) > notificationInterval) {
                                            OustPreferences.saveTimeForNotification("lastNotificationTime", currentTimestamp);
                                            String notificationContent = "<notificationContent> are not completed";
                                            String notificationTitle = "OUST";
                                            if ((OustPreferences.get("notificationContent") != null) && (!OustPreferences.get("notificationContent").isEmpty())) {
                                                notificationContent = OustPreferences.get("notificationContent");
                                            }
                                            if ((OustPreferences.get("notificationTitle") != null) && (!OustPreferences.get("notificationTitle").isEmpty())) {
                                                notificationTitle = OustPreferences.get("notificationTitle");
                                            }
                                            if ((OustPreferences.getSavedInt("coursePendingCount") > 0) || (OustPreferences.getSavedInt("assessmentPendingCount") > 0)) {
                                                String message = "";
                                                boolean conatinCourse = false;
                                                if (OustPreferences.getSavedInt("coursePendingCount") > 0) {
                                                    conatinCourse = true;
                                                    message = "" + OustPreferences.getSavedInt("coursePendingCount") + " Course(s)";
                                                }
                                                if (OustPreferences.getSavedInt("assessmentPendingCount") > 0) {
                                                    if (conatinCourse) {
                                                        message = message + " & " + OustPreferences.getSavedInt("assessmentPendingCount") + " Assessment(s)";
                                                    } else {
                                                        message = message + "" + OustPreferences.getSavedInt("assessmentPendingCount") + " Assessment(s)";
                                                    }
                                                }
                                                notificationContent = notificationContent.replace("<notificationContent>", message);
                                                showNotification(notificationTitle, notificationContent, context, "", false);
                                            }
                                        }
                                    } else {
                                        OustPreferences.saveTimeForNotification("lastNotificationTime", currentTimestamp);
                                    }

                                    if (OustPreferences.getTimeForNotification("cplNotificationFreq") > 0) {
                                        long cplnotificationInterval = 43200;
                                        if (OustPreferences.getTimeForNotification("cplNotificationFreq") > 0) {
                                            cplnotificationInterval = (OustPreferences.getTimeForNotification("cplNotificationFreq") * 3600);
                                        }
                                        if ((currentTimestamp - (OustPreferences.getTimeForNotification("cplLastNotificationTime"))) > cplnotificationInterval) {
                                            OustPreferences.saveTimeForNotification("cplLastNotificationTime", currentTimestamp);
                                            showCplNotification("Oust", "Please continue to cpl", context, "", false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showCplNotification(String title, String content, Context context, String s2, boolean b) {
        try {
            int sdk = Build.VERSION.SDK_INT;
            if (sdk > Build.VERSION_CODES.JELLY_BEAN) {
                NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                notification.setContentTitle(title);
                notification.setContentText(content);
                notification.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
                notification.setAutoCancel(true);
                notification.setTicker(content);
                notification.setSmallIcon(R.drawable.app_icon);
                Intent resultIntent = new Intent(context, LandingActivity.class);
                final String userdata = OustPreferences.get("userdata");
                if ((userdata != null) && (!userdata.isEmpty())) {
                    if ((OustPreferences.get("tanentid") != null) && (!OustPreferences.get("tanentid").isEmpty())) {
                    } else {
                        return;
                    }
                } else {
                    return;
                }
                resultIntent.putExtra("ActiveUser", userdata);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntentWithParentStack(resultIntent);
                PendingIntent pIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                } else {
                    pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                }
                notification.setContentIntent(pIntent);
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (isAppIsInBackground(context)) {
                    manager.notify(0, notification.build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public boolean isTimeBetweenTwoTime(String initialTime, String finalTime, String currentTime) throws ParseException {
        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        if (initialTime.matches(reg) && finalTime.matches(reg) && currentTime.matches(reg)) {
            boolean valid = false;
            //Start Time
            java.util.Date inTime = new SimpleDateFormat("HH:mm:ss").parse(initialTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(inTime);

            //Current Time
            java.util.Date checkTime = new SimpleDateFormat("HH:mm:ss").parse(currentTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(checkTime);

            //End Time
            java.util.Date finTime = new SimpleDateFormat("HH:mm:ss").parse(finalTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(finTime);

            if (finalTime.compareTo(initialTime) < 0) {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            java.util.Date actualTime = calendar3.getTime();
            if ((actualTime.after(calendar1.getTime()) || actualTime.compareTo(calendar1.getTime()) == 0)
                    && actualTime.before(calendar2.getTime())) {
                valid = true;
            }
            return valid;
        } else {
            throw new IllegalArgumentException("Not a valid time, expecting HH:MM:SS format");
        }

    }


    private void showNotification(String title, String content, Context context, String id, boolean isContest) {
        try {
            int sdk = Build.VERSION.SDK_INT;
            if (sdk > Build.VERSION_CODES.JELLY_BEAN) {
                NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                notification.setContentTitle(title);
                notification.setContentText(content);
                notification.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
                notification.setAutoCancel(true);
                notification.setTicker(content);
                notification.setSmallIcon(R.drawable.app_icon);
                Intent resultIntent = new Intent(context, LandingActivity.class);
                final String userdata = OustPreferences.get("userdata");
                if ((userdata != null) && (!userdata.isEmpty())) {
                    if ((OustPreferences.get("tanentid") != null) && (!OustPreferences.get("tanentid").isEmpty())) {
                    } else {
                        return;
                    }
                } else {
                    return;
                }
                resultIntent.putExtra("ActiveUser", userdata);
                if (isContest) {
                    resultIntent.putExtra("contestId", id);
                }
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                //stackBuilder.addNextIntent(resultIntent);
                stackBuilder.addNextIntentWithParentStack(resultIntent);
                PendingIntent pIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                } else {
                    pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                }
                notification.setContentIntent(pIntent);
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (isAppIsInBackground(context)) {
                    manager.notify(0, notification.build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isAppIsInBackground(Context context) {
        return MyLifeCycleHandler.stoppedActivities == MyLifeCycleHandler.startedActivities;
    }


    private void sendContestNotification(final String urlStr, final String titleStr, final long contestId, final Context context, String contestName) {
        String manufacturer = "Xiaomi";
        /*if(manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            showNotification(titleStr,contestName,context,(""+contestId),true);
        }else {*/
        Intent resultIntent;
        final String userdata = OustPreferences.get("userdata");
        if ((userdata != null) && (!userdata.isEmpty())) {
            if ((OustPreferences.get("tanentid") != null) && (!OustPreferences.get("tanentid").isEmpty())) {
                resultIntent = new Intent(context, LandingActivity.class);
                resultIntent.putExtra("ActiveUser", userdata);
                resultIntent.putExtra("contestId", ("" + contestId));
                downloadImage(urlStr, titleStr, ("" + contestId), context, resultIntent);
            } else {
                return;
            }
        } else {
            return;
        }
        //}
    }

    private void downloadImage(final String urlStr, final String titleStr, final String contestId, final Context context, final Intent resultIntent) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream in = connection.getInputStream();
                    final Bitmap myBitmap = BitmapFactory.decodeStream(in);
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            sendPictureNotification(myBitmap, titleStr, context, resultIntent);
                        }
                    };
                    mainHandler.post(myRunnable);
                } catch (Exception e) {
                }
            }
        });
    }

    private void sendPictureNotification(Bitmap result, String titleStr, final Context context, final Intent resultIntent) {
        try {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "OUST",
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
                manager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID);
            notification.setVisibility(VISIBILITY_PUBLIC);
            notification.setAutoCancel(true);
            //notification.setTicker("");
            notification.setSmallIcon(R.drawable.app_icon);
            notification.setContentText(titleStr);
            notification.setContentTitle(titleStr);
            notification.setPriority(Notification.PRIORITY_HIGH);
            if (result != null) {
                notification.setLargeIcon(result);
            } else {
                Bitmap bitmap_image = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon);
                notification.setLargeIcon(bitmap_image);
            }

            PendingIntent pIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            } else {
                pIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            notification.setContentIntent(pIntent);

            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText(titleStr);
            notification.setStyle(bigTextStyle);

            manager.notify(0, notification.build());

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isAppIsInBackground() {
        return MyLifeCycleHandler.stoppedActivities == MyLifeCycleHandler.startedActivities;
    }
}
