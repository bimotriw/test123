package com.oustme.oustsdk.reminderNotification;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import com.oustme.oustsdk.service.OustNotificationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReminderNotificationService extends Service {

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private List<String> requests;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent!=null){
            Context context = this.getApplicationContext();
            String title = intent.getStringExtra("alertTitle");
            String content = intent.getStringExtra("alertContent");
            String type = intent.getStringExtra("alertType");
            String imageUrl = intent.getStringExtra("alertImage");
            int id = intent.getIntExtra("alertId",0);

            Map<String, String> notification = new HashMap<>();
            notification.put("id", "" + id);
            notification.put("title", "REMINDER: Please complete pending task");
            notification.put("type", type);
            notification.put("imageUrl", imageUrl);
            notification.put("message",  content);
            OustNotificationHandler oustNotificationHandler = new OustNotificationHandler(notification, context);
        }


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




}

