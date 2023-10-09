package com.oustme.oustsdk.service;

import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;
import static kotlin.random.RandomKt.Random;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.NewLandingActivity;
import com.oustme.oustsdk.layoutFour.LandingActivity;
import com.oustme.oustsdk.reminderNotification.ContentReminderNotification;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.NotificationData;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class OustNotificationHandler {
    public String CHANNEL_ID = "default";
    public Intent resultIntent;

    Context context;
    String manufacturer = "Xiaomi";
    String userdata = "";
    String title = "";
    String gcmMessage = "";

    public OustNotificationHandler(Bundle bundle, Context context) {
        Log.e("Notification", " Notification recieved");
        this.context = context;
        OustSdkApplication.setmContext(context);
        boolean allNotificationDisabled = OustPreferences.getAppInstallVariable("allNotificationDisabled");
        if (!allNotificationDisabled) {
            Log.e("Notification", " allNotificationDisabled false");
            String gcmMessage, title, gcmType, imgUrl;
            // The getMessageType() intent parameter must be the intent you received
            // in your BroadcastReceiver.
            gcmMessage = bundle.getString("message");
            gcmType = bundle.getString("type");
            Log.e("Notification", "  gcmType" + gcmType);
            imgUrl = bundle.getString("imageUrl");
            title = bundle.getString("title");
            String id = bundle.getString("id");
            String manufacturer = "Xiaomi";
            if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
                imgUrl = null;
            }
            sendNotification(title, gcmMessage, gcmType, imgUrl, id, 0, "");
        }
    }

    public OustNotificationHandler(Map<String, String> dataMap, Context context) {
        this.context = context;
        OustSdkApplication.setmContext(context);
        NotificationData notificationData = new NotificationData();

        notificationData.setGcmMessage(dataMap.get("message"));
        Log.e("---------", "message" + dataMap.get("id"));
        Log.e("---------", "message" + dataMap.get("type"));
        Log.e("---------", "message" + dataMap.get("imageUrl"));

        if (dataMap.get("imageUrl") != null) {
            notificationData.setImgUrl(dataMap.get("imageUrl"));
        }
        if (dataMap.get("id") != null) {
            notificationData.setId(dataMap.get("id"));
        }
        if (dataMap.get("requestCode") != null) {
            notificationData.setRequestCode(Integer.parseInt(Objects.requireNonNull(dataMap.get("requestCode"))));
        }
        if (dataMap.get("title") != null) {
            notificationData.setTitle(dataMap.get("title"));
        }
        if (dataMap.get("type") != null) {
            notificationData.setGcmType(dataMap.get("type"));
        }
        if (dataMap.get("noticeBoardNotificationData") != null) {
            notificationData.setNoticeBoardNotificationData(dataMap.get("noticeBoardNotificationData"));
        }
        if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            notificationData.setImgUrl(null);
        }
        sendNotification(notificationData.getTitle(), notificationData.getGcmMessage(), notificationData.getGcmType(), notificationData.getImgUrl(), notificationData.getId(), notificationData.getRequestCode(), notificationData.getNoticeBoardNotificationData());

    }


    private void sendNotification(String title, String gcmMessage, String gcmType, String imgUrl, String id, int requestCode, String noticeBoardNotificationData) {
        Log.e("Notification", "  inside sendNotification()- title  " + title);
        Log.e("Notification", "  inside sendNotification()- gcmMessage  " + gcmMessage);
        Log.e("Notification", "  inside sendNotification()- gcmType  " + gcmType);
        Log.e("Notification", "  inside sendNotification()- imgUrl  " + imgUrl);
        Log.e("Notification", "  inside sendNotification()- id  " + id);
        Log.e("Notification", "  inside sendNotification()- requestCode  " + requestCode);
        Log.e("Notification", "  inside sendNotification()- noticeBoardNotificationData  " + noticeBoardNotificationData);

        try {
            userdata = OustPreferences.get("userdata");
//            Log.e("Notification", "  inside sendNotification()- userdata  " + userdata);
            ArrayList<ContentReminderNotification> contentReminderNotificationArrayList = OustStaticVariableHandling.getInstance().getContentReminderNotificationArrayList();
            if (contentReminderNotificationArrayList == null) {
                contentReminderNotificationArrayList = new ArrayList<>();
            }
            this.title = title;
            this.gcmMessage = gcmMessage;

            boolean isLayout4 = OustPreferences.getAppInstallVariable("isLayout4");

            //String isLoggedIn = OustPreferences.get("isLoggedIn");
            if ((userdata != null) && (!userdata.isEmpty())) {
                ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
                if (gcmType != null) {
                    Log.e("Notification", "  inside sendNotification() && gcmType != null && " + GCMType.FEED_DISTRIBUTE.name());

                    if (isLayout4) {
                        resultIntent = new Intent(context, LandingActivity.class);
                    } else {
                        resultIntent = new Intent(context, NewLandingActivity.class);
                    }
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    if (gcmType.equalsIgnoreCase(GCMType.NOTICEBOARD_REPLY.name()) || gcmType.equalsIgnoreCase(GCMType.NOTICEBOARD_COMMENT.name())) {
                        int nbId = 0, postId = 0, commentId = 0, replyId = 0;
                        if (noticeBoardNotificationData != null && !noticeBoardNotificationData.isEmpty()) {
                            JSONObject jsonObject = new JSONObject(noticeBoardNotificationData);
                            nbId = jsonObject.optInt("nbId");
                            postId = jsonObject.optInt("postId");
                            commentId = jsonObject.optInt("commentId");
                            replyId = jsonObject.optInt("replyId");
                        }
                        Log.e("Notification ", " Replay & Comment--> " + "postId " + postId + " commentId " + commentId + " nbId " + nbId + " replyId " + replyId);

                        resultIntent.putExtra("nbId", nbId);
                        resultIntent.putExtra("postId", postId);
                        resultIntent.putExtra("commentId", commentId);
                        resultIntent.putExtra("replyId", replyId);

                    } else if (gcmType.equalsIgnoreCase(GCMType.NOTICEBOARD_POST.name())) {
                        int nbId = 0, postId = 0, commentId = 0, replyId = 0;
                        if (noticeBoardNotificationData != null && !noticeBoardNotificationData.isEmpty()) {
                            JSONObject jsonObject = new JSONObject(noticeBoardNotificationData);
                            nbId = jsonObject.optInt("nbId");
                            postId = jsonObject.optInt("postId");
                            commentId = jsonObject.optInt("commentId");
                            replyId = jsonObject.optInt("replyId");
                        }
                        Log.e("Notification Post-->", "postId " + postId + " commentId " + commentId + " nbId " + nbId + " replyId " + replyId);

                        resultIntent.putExtra("nbId", nbId);
                        resultIntent.putExtra("postId", postId);
                        resultIntent.putExtra("commentId", commentId);
                        resultIntent.putExtra("replyId", replyId);

                    } else if (gcmType.equalsIgnoreCase(GCMType.ASSESSMENT_NOTIFICATION.name())) {
                        if ((id != null) && (!id.isEmpty())) {
                            resultIntent.putExtra("assessmentId", id);
                        }
                    } else if (gcmType.equalsIgnoreCase(GCMType.ASSESSMENT_DISTRIBUTE.name())) {
                        if ((id != null) && (!id.isEmpty())) {
                            resultIntent.putExtra("assessmentId", id);
                        }
                    } else if (gcmType.equalsIgnoreCase(GCMType.ASSESSMENT_REMINDER.name())) {
                        if ((id != null) && (!id.isEmpty())) {

                            resultIntent.putExtra("assessmentId", id);

                            ContentReminderNotification contentReminderNotification = new ContentReminderNotification();
                            if (activeUser != null) {
                                contentReminderNotification.setUserId(activeUser.getStudentid());
                                contentReminderNotification.setContentType("assessment");
                                contentReminderNotification.setReminderDateTime(System.currentTimeMillis());
                                contentReminderNotification.setDevicePlatform("Android");
                                String contentid = id;
                                if (contentid.toUpperCase().contains("ASSESSMENT")) {
                                    contentid = contentid.toUpperCase().replace("ASSESSMENT", "");
                                }
                                contentReminderNotification.setContentId(Long.parseLong(contentid));
                                contentReminderNotificationArrayList.add(contentReminderNotification);
                                OustStaticVariableHandling.getInstance().setContentReminderNotificationArrayList(contentReminderNotificationArrayList);
                            }

                        }
                    } else if (gcmType.equalsIgnoreCase(GCMType.COURSE_DISTRIBUTE.name())) {
                        if ((id != null) && (!id.isEmpty())) {
                            resultIntent.putExtra("courseId", id);
                        }
                    } else if (gcmType.equalsIgnoreCase(GCMType.COURSE_REMINDER.name())) {
                        if ((id != null) && (!id.isEmpty())) {
                            resultIntent.putExtra("courseId", id);
                            ContentReminderNotification contentReminderNotification = new ContentReminderNotification();
                            if (activeUser != null) {
                                contentReminderNotification.setUserId(activeUser.getStudentid());
                                contentReminderNotification.setContentType("course");
                                contentReminderNotification.setReminderDateTime(System.currentTimeMillis());
                                contentReminderNotification.setDevicePlatform("Android");
                                String contentid = id;
                                if (contentid.toUpperCase().contains("COURSE")) {
                                    contentid = contentid.toUpperCase().replace("COURSE", "");
                                }
                                contentReminderNotification.setContentId(Long.parseLong(contentid));
                                contentReminderNotificationArrayList.add(contentReminderNotification);
                                OustStaticVariableHandling.getInstance().setContentReminderNotificationArrayList(contentReminderNotificationArrayList);
                            }
                        }
                    } else if (gcmType.equalsIgnoreCase(GCMType.COLLECTION_DISTRIBUTE.name())) {
                        if ((id != null) && (!id.isEmpty())) {
                            resultIntent.putExtra("collectionId", id);
                        }
                    } else if (gcmType.equalsIgnoreCase(GCMType.FEED_DISTRIBUTE.name())) {
                        Log.e("Notification", "  inside sendNotification() and FEED build");
                        resultIntent.putExtra("isFeedDistributed", true);
                        resultIntent.putExtra("feedId", id);
                    } else if (gcmType.equalsIgnoreCase(GCMType.NOTICE_BOARD_DISTRIBUTION.name())) {
                        Log.e("Notification", "  inside sendNotification() and NOTICE_BOARD_DISTRIBUTION build");
                        if ((id != null) && (!id.isEmpty())) {
                            resultIntent.putExtra("noticeBoardId", id);
                        }
                    } else if (gcmType.equalsIgnoreCase(GCMType.CATALOGUE_DISTRIBUTED.name())) {
                        if ((id != null) && (!id.isEmpty())) {
                            resultIntent.putExtra("catalogue_id", id);
                        }
                    } else if (gcmType.equalsIgnoreCase(GCMType.CPL_DISTRIBUTE.name())) {
                        if ((id != null) && (!id.isEmpty())) {
                            resultIntent.putExtra("cpl_id", id);
                        }
                    } else if (gcmType.equalsIgnoreCase(GCMType.ML_CPL_DISTRIBUTE.name())) {
                        if ((id != null) && (!id.isEmpty())) {
                            resultIntent.putExtra("ml_cpl_id", id);
                        }
                    } else if (gcmType.equalsIgnoreCase(GCMType.MEETING_DISTRIBUTED.name())) {
                        if ((id != null) && (!id.isEmpty())) {
                            resultIntent.putExtra("meetingId", id);
                        }
                    } else if (gcmType.equalsIgnoreCase(GCMType.PUBLIC_NOTIFICATION.name())) {
                        if ((id != null) && (!id.isEmpty())) {
                            resultIntent.putExtra("fastest_finger_first_contest_id", id);
                        }
                    }

                    if (resultIntent != null) {
                        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    }

                    if (imgUrl != null && !imgUrl.isEmpty()) {
                        if (OustSdkTools.checkInternetStatus()) {
                            downloadImage(imgUrl, title, gcmMessage);
                        } else {
                            normalNotification();
                        }
                    } else {
                        normalNotification();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("WrongConstant")
    private void normalNotification() {
        int convertMillSec = Random(System.currentTimeMillis()).nextInt(1000);
        PendingIntent resultPendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultPendingIntent = PendingIntent.getActivity(context, convertMillSec, resultIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            resultPendingIntent = PendingIntent.getActivity(context, convertMillSec, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "OUST", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            channel.setShowBadge(true);
            mNotificationManager.createNotificationChannel(channel);
        }

        Bitmap bitmap_image = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon);

        mNotifyBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        mNotifyBuilder.setContentTitle(title);
        mNotifyBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
        mNotifyBuilder.setPriority(Notification.PRIORITY_HIGH);
        mNotifyBuilder.setSmallIcon(R.drawable.small_app_icon);
        mNotifyBuilder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
        mNotifyBuilder.setLargeIcon(bitmap_image);

        // Set pending intent
        mNotifyBuilder.setContentIntent(resultPendingIntent);
        mNotifyBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(gcmMessage));

        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mNotifyBuilder.setDefaults(defaults);
        // Set the content for Notification
        mNotifyBuilder.setContentText(gcmMessage);
        // Set autocancel
        mNotifyBuilder.setAutoCancel(true);
        mNotificationManager.notify(convertMillSec, mNotifyBuilder.build());
    }

    private void downloadImage(final String urlStr, final String titleStr, final String contentStr) {
        AsyncTask.execute(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream in = connection.getInputStream();
                final Bitmap myBitmap = BitmapFactory.decodeStream(in);
                Handler mainHandler = new Handler(context.getMainLooper());
                Runnable myRunnable = () -> sendPictureNotification(myBitmap, titleStr, contentStr);
                mainHandler.post(myRunnable);
            } catch (Exception e) {
                normalNotification();
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        });
    }

    private void sendPictureNotification(Bitmap result, String titleStr, String contentStr) {
        try {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "OUST", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
                channel.setShowBadge(true);
                manager.createNotificationChannel(channel);
            }
            int convertMillSec = Random(System.currentTimeMillis()).nextInt(1000);
            PendingIntent pIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pIntent = PendingIntent.getActivity(context, convertMillSec, resultIntent, PendingIntent.FLAG_IMMUTABLE);
            } else {
                pIntent = PendingIntent.getActivity(context, convertMillSec, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.notification_collapsed);
            notificationLayout.setTextViewText(R.id.collapsed_notification_title, titleStr);
            notificationLayout.setTextViewText(R.id.collapsed_notification_info, contentStr);
            notificationLayout.setImageViewBitmap(R.id.collapsed_notification_image, result);

            RemoteViews notificationLayoutExpanded = new RemoteViews(context.getPackageName(), R.layout.notification_expanded);
            notificationLayoutExpanded.setTextViewText(R.id.expanded_notification_title, titleStr);
            notificationLayoutExpanded.setTextViewText(R.id.expanded_notification_info, contentStr);
            notificationLayoutExpanded.setImageViewBitmap(R.id.image_view_expanded, result);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID);
            notification.setVisibility(VISIBILITY_PUBLIC);
            notification.setAutoCancel(true);
            notification.setPriority(Notification.PRIORITY_HIGH);
            notification.setSmallIcon(R.drawable.small_app_icon);
            notification.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
            notification.setContentIntent(pIntent);
            notification.setStyle(new NotificationCompat.DecoratedCustomViewStyle()).setCustomContentView(notificationLayout).setCustomBigContentView(notificationLayoutExpanded);
            // Set Vibrate, Sound and Light
            int defaults = 0;
            defaults = defaults | Notification.DEFAULT_LIGHTS;
            defaults = defaults | Notification.DEFAULT_VIBRATE;
            defaults = defaults | Notification.DEFAULT_SOUND;

            notification.setDefaults(defaults);
            manager.notify(convertMillSec, notification.build());

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}