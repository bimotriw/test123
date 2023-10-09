package com.oustme.oustsdk.downloadmanger.newimpl;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;


import androidx.annotation.Nullable;

import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.DialogActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.oustme.oustsdk.util.DialogActivity.DILOG_FORCE_CLOSE_ACTION;
import static com.oustme.oustsdk.util.DialogActivity.DILOG_RETRY_ACTION;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownloadFilesIntentService extends Service {

    private static final String ACTION_DOWNLOAD = "com.oustme.oustsdk.downloadmanger.newimpl.action.DOWNLOAD";
    private static final String EXTRA_FILEURLS = "com.oustme.oustsdk.downloadmanger.newimpl.extra.FILEURLS";
    private static final String EXTRA_ISOUSTLEARN = "com.oustme.oustsdk.downloadmanger.newimpl.extra.ISOUSTLEARN";
    private static final String EXTRA_SHOWRETRY = "com.oustme.oustsdk.downloadmanger.newimpl.extra.SHOWRETRY";
    private static final String EXTRA_FILENAMES = "com.oustme.oustsdk.downloadmanger.newimpl.extra.FILENAMES";

    public static final String ACTION_ERROR = "com.oustme.oustsdk.service.action.ERROR";
    public static final String ACTION_COMPLETE = "com.oustme.oustsdk.service.action.COMPLETE";
    public static final String ACTION_PROGRESS = "com.oustme.oustsdk.service.action.PROGRESS";

    private static final String TAG = "DownloadFilesService";
    private static final int RETRY_COUNT = 3;
    private static final String EXTRA_RECEIVER = "receiver";

    private ResultReceiver mResultReceiver;
    private ArrayList<String> fileUrlList;
    private ArrayList<String> fileNameList;
    private ArrayList<String> fileUrlRetryList;
    private String fileDestination;
    private boolean isOustLearn;
    private boolean showRetry;
    private File cacheDownloadFile;
    private long downloaded = 0;
    private String downloadedPath = "";
    private int downloadCount;
    private int downloadedCount;
    private int downloadFailCount;
    private int downloadPointer;
    private int retryCounter = 0;
    private DialogRertyreceiver dialogRertyreceiver;


    public static void startFileDownload(Context context, ArrayList<String> fileUrlList, ArrayList<String> fileNameList, boolean isOustLearn, boolean showRetry, ResultReceiver receiver) {
        Intent intent = new Intent(context, DownloadFilesIntentService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putStringArrayListExtra(EXTRA_FILEURLS, fileUrlList);
        intent.putStringArrayListExtra(EXTRA_FILENAMES, fileNameList);
        intent.putExtra(EXTRA_ISOUSTLEARN, isOustLearn);
        intent.putExtra(EXTRA_SHOWRETRY, showRetry);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        context.startService(intent);

    }

    @Override
    public void onDestroy() {
        if (dialogRertyreceiver != null)
            this.unregisterReceiver(dialogRertyreceiver);
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                mResultReceiver = intent.getParcelableExtra(EXTRA_RECEIVER);
                fileUrlList = new ArrayList<>();
                ArrayList<String> list = intent.getStringArrayListExtra(EXTRA_FILEURLS);
                fileUrlList.addAll(list);
                fileNameList = new ArrayList<>();
                ArrayList<String> list1 = intent.getStringArrayListExtra(EXTRA_FILENAMES);
                fileNameList.addAll(list1);
                downloadCount = fileUrlList.size();
                isOustLearn = intent.getBooleanExtra(EXTRA_ISOUSTLEARN, false);
                showRetry = intent.getBooleanExtra(EXTRA_SHOWRETRY, true);
                downloadPointer = 0;
                downloadFailCount = 0;
                handleDownload();
            }
        }

        return Service.START_STICKY;
    }

    /*@Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                mResultReceiver = intent.getParcelableExtra(EXTRA_RECEIVER);
                fileUrlList = new ArrayList<>();
                ArrayList<String> list = intent.getStringArrayListExtra(EXTRA_FILEURLS);
                fileUrlList.addAll(list);
                fileNameList = new ArrayList<>();
                ArrayList<String> list1 = intent.getStringArrayListExtra(EXTRA_FILENAMES);
                fileNameList.addAll(list1);
                downloadCount = fileUrlList.size();
                isOustLearn = intent.getBooleanExtra(EXTRA_ISOUSTLEARN, false);
                handleDownload();
            }
        }
    }*/

    private void handleDownload() {
        fileDestination = this.getFilesDir() + "/";

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                if (fileUrlList != null && fileUrlList.size() > 0) {
                    fileUrlRetryList = new ArrayList<>();

                    while (downloadPointer < fileUrlList.size() && downloadFailCount <= 0) {
                        downLoadfile(fileUrlList.get(downloadPointer), fileNameList.get(downloadPointer));
                        downloadPointer++;
                    }

                    Log.d(TAG, "Success : " + downloadedCount + "Failure : " + downloadFailCount);
                    if (mResultReceiver != null) {
                        Log.d(TAG, "doInBackground: ");
                        Bundle bundle = new Bundle();
                        bundle.putString("MSG", "Success");
                        mResultReceiver.send(4, bundle);
                    }
                }
                return null;
            }
        }.execute();

            /*if (fileUrlRetryList.size() > 0 && retryCounter < RETRY_COUNT) {
                fileUrlList.clear();
                fileUrlList.addAll(fileUrlRetryList);
                retryCounter++;
                handleDownload();
            } else {
                sendUpdate(1, "completed");
            }*/

            /*for (String fileUrl : fileUrlList) {
                downLoadfile(fileUrl);
            }*/
    }


    private void downLoadfile(String fileUrl, String fileName) {
        File file;
        if (isOustLearn) {
            file = new File(fileDestination, "oustlearn_" + fileName);
        } else {
            file = new File(fileDestination, fileName);
        }
        cacheDownloadFile = new File(this.getCacheDir() + fileName);
        try {
            if (cacheDownloadFile.isFile())
                downloaded = OustSdkTools.getFileSize(cacheDownloadFile);
            else
                downloaded = 0;
            Log.d(TAG, downloaded + " <- " + cacheDownloadFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            //File dir=new File(fileDestination);
            File chacheDir = this.getCacheDir();
            if (!chacheDir.isDirectory())
                chacheDir.mkdirs();

            String withoutBase = OustMediaTools.removeAwsOrCDnUrl(fileUrl);
            String filename2 = OustMediaTools.getMediaFileName(fileUrl);
            if (withoutBase.equalsIgnoreCase(filename2) || fileUrl.equalsIgnoreCase("http://di5jfel2ggs8k.cloudfront.net/")) {
                Log.d(TAG, "Base check return ");
                downloadedCount++;
                sendUpdate(2, "progress");
                if (downloadedCount + downloadFailCount >= downloadCount) {
                    sendUpdate(1, "" + 100);
                }
                return;
            }

            Log.d(TAG, "LINK " + fileUrl);
            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if (downloaded > 0)
                urlConnection.setRequestProperty("Range", "byte=" + downloaded);
            urlConnection.connect();
            int status = urlConnection.getResponseCode();
            InputStream inputStream = urlConnection.getInputStream();
            long totalSize = urlConnection.getContentLength();

            Log.d(TAG, "File size LINK --- File path LINK :" + fileUrl + " --- size:" + totalSize);

            //String fileDestination2 = Environment.getExternalStorageDirectory() + "/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files/oustlearn_"+fileName;
            OustPreferences.saveTimeForNotification("oustlearn_" + fileName, totalSize);

            if (file.exists() && file.length() >= totalSize) {
                Log.d(TAG, "File size Downloaded: " + file.length() + " --- file exit return");
                downloadedCount++;
                sendUpdate(2, "progress");

                if (downloadedCount + downloadFailCount >= downloadCount) {
                    sendUpdate(1, "" + 100);
                }
                return;
            } else if (file.exists()) {
                Log.d(TAG, "File size not matched, so deleting the previous file: " + file.getName());
                file.delete();
            }

            try {
                if (totalSize <= downloaded && cacheDownloadFile.exists()) {
                    if (OustSdkTools.fileCopy(file, cacheDownloadFile)) {
                        Log.d(TAG, "cached file Copied before download --size:" + file.length() + " --- Name:" + file.getName());
                        cacheDownloadFile.delete();
                        downloadedCount++;
                        sendUpdate(2, "progress");

                        if (downloadedCount + downloadFailCount >= downloadCount) {
                            sendUpdate(1, "" + 100);
                        }
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            if (!cacheDownloadFile.exists()) {
                cacheDownloadFile.createNewFile();
            } else {
                cacheDownloadFile.delete();
                cacheDownloadFile.createNewFile();
                Log.d(TAG, "FILE_DOWNLOAD_TAG: Delete cache file");
            }

            this.downloadedPath = cacheDownloadFile.getAbsolutePath();
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            FileOutputStream fileOutput = new FileOutputStream(cacheDownloadFile);

            while ((bufferLength = bis.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloaded += bufferLength;
                float per = ((float) downloaded / (float) totalSize) * 100;
                String l = "" + (int) per;
                if (downloaded >= totalSize) {
                    break;
                }
            }
            if (downloaded >= totalSize) {
                downloadedCount++;
                sendUpdate(2, "" + downloadedCount);
                if (downloadedCount + downloadFailCount >= downloadCount) {
                    sendUpdate(1, "" + 100);
                }
            }
            Log.d(TAG, "File size -> DWONLOADED TO " + downloadedPath + " (" + cacheDownloadFile.length() + ")");
            fileOutput.close();
            try {
                if (OustSdkTools.fileCopy(file, cacheDownloadFile)) {
                    Log.d(TAG, "file Copied, delete cache");
                    cacheDownloadFile.delete();
                } else {
                    Log.d(TAG, "handleDownLoad: else part");
                    urlConnection.disconnect();
                    if (cacheDownloadFile.exists()) {
                        cacheDownloadFile.delete();
                    }
                    if (file.exists()) {
                        file.delete();
                    }
//                    downLoadfile(fileUrl);
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                if (cacheDownloadFile.exists()) {
                    cacheDownloadFile.delete();
                }
                if (file.exists()) {
                    file.delete();
                }
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            /*try {
                downloadedCount++;
                sendUpdate(2, "progress");
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }*/
        } catch (MalformedURLException e) {
            Log.d(TAG, "handleDownLoad: MalformedURLException");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            downloadFailCount++;
            fileUrlRetryList.add(fileUrl);
            checkProgressAndProceed();
//            sendUpdate(3, e.getLocalizedMessage());
            //this.stopSelf();
            // publishProgress(e.toString());
        } catch (IOException e) {
            Log.d(TAG, "handleDownLoad: IOException");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            downloadFailCount++;
            fileUrlRetryList.add(fileUrl);
            checkProgressAndProceed();
//            sendUpdate(3, e.getLocalizedMessage());
            // publishProgress(e.toString());
        }
    }

    void checkProgressAndProceed() {
        sendUpdate(3, String.valueOf((downloadedCount * 100) / downloadCount));
        openDialogRerty();
    }

    private void sendUpdate(int i, String message) {
        try {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            switch (i) {
                case 1:
                    intent.setAction(ACTION_COMPLETE);
                    intent.putExtra("MSG", message);
                    bundle.putString("MSG", message);
                    stopSelf();
                    break;
                case 2:
                    intent.setAction(ACTION_PROGRESS);
                    bundle.putString("MSG", String.valueOf((downloadedCount * 100) / downloadCount));
                    break;
                case 3:
                    intent.setAction(ACTION_ERROR);
                    intent.putExtra("MSG", message);
                    break;
            }
            if (mResultReceiver != null) {
                mResultReceiver.send(i, bundle);
            }
            // intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void openDialogRerty() {
        try {
            if (showRetry) {
                if (dialogRertyreceiver == null) {
                    dialogRertyreceiver = new DialogRertyreceiver();
                    IntentFilter filter = new IntentFilter();
                    filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
                    filter.addAction(DILOG_RETRY_ACTION); // SMS
                    this.registerReceiver(dialogRertyreceiver, filter);
                }
                Intent popup = new Intent(this, DialogActivity.class);
                popup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(popup);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    class DialogRertyreceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), DILOG_RETRY_ACTION)) {
                downloadFailCount = 0;
                downloadPointer--;
                handleDownload();
            } else if (TextUtils.equals(intent.getAction(), DILOG_FORCE_CLOSE_ACTION)) {
                stopSelf();
            }
//        Toast.makeText(DownloadFilesIntentService.this, "call receiced", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
        if (dialogRertyreceiver != null)
            this.unregisterReceiver(dialogRertyreceiver);
        Log.d(TAG, "onTaskRemoved");
        super.onTaskRemoved(rootIntent);

    }
}
