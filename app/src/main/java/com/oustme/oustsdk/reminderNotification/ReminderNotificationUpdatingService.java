package com.oustme.oustsdk.reminderNotification;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class ReminderNotificationUpdatingService extends Service {

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    ArrayList<ContentReminderNotification> contentReminderNotificationArrayList;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //    Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        contentReminderNotificationArrayList = OustStaticVariableHandling.getInstance().getContentReminderNotificationArrayList();

        if(contentReminderNotificationArrayList!=null&&contentReminderNotificationArrayList.size()!=0){
            for (ContentReminderNotification contentReminderNotification:
                 contentReminderNotificationArrayList) {

                sendNotificationCount(contentReminderNotification);

            }
        }

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        // Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();

    }



    private void sendNotificationCount(ContentReminderNotification contentReminderNotification) {

        try {
            if(contentReminderNotification!=null){

                String send_feed_request = getResources().getString(R.string.content_notification_count);
                Gson gson = new GsonBuilder().create();
                send_feed_request = HttpManager.getAbsoluteUrl(send_feed_request);
                ReminderMainObject reminderMainObject = new ReminderMainObject();
                reminderMainObject.setContentReminderNotification(contentReminderNotification);
                String jsonParams = gson.toJson(reminderMainObject);

                ApiCallUtils.doNetworkCall(Request.Method.POST, send_feed_request, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.optBoolean("success")) {
                            Log.e("ReminderNotification","Success notification count");
                            if(contentReminderNotificationArrayList!=null&&contentReminderNotificationArrayList.size()!=0){
                                if(contentReminderNotificationArrayList.contains(contentReminderNotification)){
                                    contentReminderNotificationArrayList.remove(contentReminderNotification);
                                    OustStaticVariableHandling.getInstance().setContentReminderNotificationArrayList(contentReminderNotificationArrayList);
                                }
                            }
                        }

                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("ReminderNotification","Error notification count");
                    }
                });


            }


        } catch (Exception e) {
            Log.e("sendNotificationCount", "ERROR: ", e);
        }
    }



}

