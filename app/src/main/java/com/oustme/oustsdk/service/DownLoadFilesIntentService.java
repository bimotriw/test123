package com.oustme.oustsdk.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;


import androidx.annotation.Nullable;

import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadManager;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownLoadFilesIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_ERROR = "com.oustme.oustsdk.service.action.ERROR";
    public static final String ACTION_COMPLETE = "com.oustme.oustsdk.service.action.COMPLETE";
    public static final String ACTION_PROGRESS = "com.oustme.oustsdk.service.action.PROGRESS";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.oustme.oustsdk.service.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.oustme.oustsdk.service.extra.PARAM2";

    private static final String TAG = "DownLoadFilesIntentServ";
    private String downloadlink,fileDestination;
    public static final int ON_INIT=100,ON_ERROR=102,ON_PROGRASS=103,ON_COMPLETED=104,STATUS_DOWNLOADED=1500,STATUS_NOT_YET=1501;
    private DownloadManager.onUpdateListener onUpdateListener;
    private String downloadedPath="";
    private long downloaded=0;
    private File file;
    private String returnData=null;
    private File cacheDownloadFile;
    private Context mContext;
    private String fileName;
    private ResultReceiver mResultReceiver;
    private Bundle bundle;
    int mNoTries;
    private static DownLoadUpdateInterface mDownLoadUpdateInterface;

    public DownLoadFilesIntentService() {
        super("DownLoadFilesIntentService");
        //Log.d(TAG, "DownLoadFilesIntentService: ");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DownLoadFilesIntentService.class);
        // intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void DownLoadFilesFromIntentService(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DownLoadFilesIntentService.class);
        //intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /*@Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }*/

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            /*final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }*/
            Log.d(TAG, "onHandleIntent: ");
            mResultReceiver = intent.getParcelableExtra("receiver");
            bundle = new Bundle();
            final String fileURL = intent.getStringExtra(AppConstants.StringConstants.FILE_URL);
            final String fileDestination = intent.getStringExtra(AppConstants.StringConstants.FILE_DESTN);
            final String fileName = intent.getStringExtra(AppConstants.StringConstants.FILE_NAME);
            final boolean isOustLearn = intent.getBooleanExtra(AppConstants.StringConstants.IS_OUST_LEARN, false);
            final boolean isVideo = intent.getBooleanExtra(AppConstants.StringConstants.IS_VIDEO, false);

            mNoTries=0;
            handleDownLoad(fileURL, fileDestination, fileName, isOustLearn, isVideo);
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleDownLoad(String fileURL, String fileDestinationPath,String fileName, boolean isOustLearn, boolean isVideo) {
        if(isVideo) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    asyncDownload(fileURL, fileDestinationPath, fileName, isOustLearn, isVideo);
                    return null;
                }
            }.execute();
        }else{
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    asyncDownload(fileURL, fileDestinationPath, fileName, isOustLearn, isVideo);
                    return null;
                }
            }.execute();
        }
    }

    private void asyncDownload(String fileURL, String fileDestinationPath,String fileName, boolean isOustLearn, boolean isVideo){
        this.downloadlink=fileURL;
        this.fileDestination=fileDestinationPath;
        this.mContext = DownLoadFilesIntentService.this;
        if(isOustLearn)
        {
            file=new File(fileDestination,"oustlearn_"+fileName);
        }
        else {
            file = new File(fileDestination,fileName);
        }
        cacheDownloadFile=new File(mContext.getCacheDir()+ fileName);
        try {
            if(cacheDownloadFile.isFile())
                downloaded=OustSdkTools.getFileSize(cacheDownloadFile);
            else
                downloaded=0;
            Log.d(TAG,downloaded+" <- "+cacheDownloadFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        //fireOnUpdate(ON_INIT,"init ...");

        try {
            //File dir=new File(fileDestination);
            File chacheDir= mContext.getCacheDir();
            if(!chacheDir.isDirectory())
                chacheDir.mkdirs();

            String withoutBase = OustMediaTools.removeAwsOrCDnUrl(fileURL);
            String filename2 = OustMediaTools.getMediaFileName(fileURL);
            if(withoutBase.equalsIgnoreCase(filename2) || fileURL.equalsIgnoreCase("http://di5jfel2ggs8k.cloudfront.net/")) {
                Log.d(TAG, "Base check return ");
                sendUpdate(1, "completed");
                returnData= "COMPLETED";
                return;
            }

            Log.d(TAG,"LINK "+downloadlink);
            URL url=new URL(downloadlink);
            HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
            if(downloaded>0)
                urlConnection.setRequestProperty("Range","byte="+downloaded);
            urlConnection.connect();
            int status = urlConnection.getResponseCode();
            InputStream inputStream=urlConnection.getInputStream();
            long totalSize=urlConnection.getContentLength();

            Log.d(TAG, "File size LINK --- File path LINK :"+downloadlink+" --- size:"+totalSize);

            //String fileDestination2 = Environment.getExternalStorageDirectory() + "/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files/oustlearn_"+fileName;
            OustPreferences.saveTimeForNotification("oustlearn_"+fileName, totalSize);

            if(file.exists() && file.length()>=totalSize) {
                Log.d(TAG, "File size Downloaded: "+file.length()+" --- file exit return");
                sendUpdate(1, "completed");
                returnData= "COMPLETED";
                return;
            } else if(file.exists()){
                Log.d(TAG, "File size not matched, so deleting the previous file: "+file.getName());
                file.delete();
            }

            try {
                if (totalSize <= downloaded && cacheDownloadFile.exists()) {
                    if (OustSdkTools.fileCopy(file, cacheDownloadFile)) {
                        Log.d(TAG, "cached file Copied before download --size:"+file.length()+" --- Name:"+file.getName());
                        cacheDownloadFile.delete();
                        returnData = "COMPLETED";
                        //Log.d(TAG, "handleDownLoad: download completed:");
                        sendUpdate(1, "completed");
                        return;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            if(!cacheDownloadFile.exists()){
                cacheDownloadFile.createNewFile();
            }
            else {
                cacheDownloadFile.delete();
                cacheDownloadFile.createNewFile();
                Log.d(TAG, "FILE_DOWNLOAD_TAG: Delete cache file");
            }

            this.downloadedPath=cacheDownloadFile.getAbsolutePath();
            byte[] buffer=new byte[1024];
            int bufferLength=0;
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            FileOutputStream fileOutput=new FileOutputStream(cacheDownloadFile);

            while ((bufferLength=bis.read(buffer))>0){
                fileOutput.write(buffer,0,bufferLength);
                downloaded+=bufferLength;
                float per = ((float) downloaded/(float) totalSize)*100;
                String l = ""+(int)per;
                sendUpdate(2, l);
                if(downloaded>=totalSize){
                    break;
                }
            }
            Log.d(TAG,"File size -> DWONLOADED TO "+downloadedPath+" ("+cacheDownloadFile.length()+")");
            fileOutput.close();
            try {
                if(OustSdkTools.fileCopy(file,cacheDownloadFile)){
                    Log.d(TAG,"file Copied, delete cache");
                    cacheDownloadFile.delete();
                }
                else {
                    Log.d(TAG, "handleDownLoad: else part");
                    urlConnection.disconnect();
                    if (cacheDownloadFile.exists()) {
                        cacheDownloadFile.delete();
                    }
                    if (file.exists()) {
                        file.delete();
                    }
                    handleDownLoad(fileURL, fileDestinationPath, fileName, isOustLearn, isVideo);
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                if(cacheDownloadFile.exists())
                {
                    cacheDownloadFile.delete();
                }
                if(file.exists())
                {
                    file.delete();
                }
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            try{
                sendUpdate(1, "completed");
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            returnData="COMPLETED";

            //Commenting
            /*File dir=new File(fileDestination);
            File chacheDir= mContext.getCacheDir();
            if(!chacheDir.isDirectory())
                chacheDir.mkdirs();
            *//*if(!dir.isDirectory()){
                dir.mkdirs();
            }*//*

            String withoutBase = OustMediaTools.removeAwsOrCDnUrl(fileURL);
            String filename2 = OustMediaTools.getMediaFileName(fileURL);
            if(withoutBase.equalsIgnoreCase(filename2) || fileURL.equalsIgnoreCase(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH))
            {
                sendUpdate(1, "completed");
                returnData= "COMPLETED";
                return;
            }

            if(file.exists()) {
                Log.d(TAG,"File exist return complete");
                sendUpdate(1, "completed");
                //this.stopSelf();
                //return "COMPLETED";//file exist
            }

            //fireOnUpdate(ON_INIT, "Started");
            if(!cacheDownloadFile.exists()){
                cacheDownloadFile.createNewFile();
            }
            else {
                cacheDownloadFile.delete();
                cacheDownloadFile.createNewFile();
                Log.d(TAG, "FILE_DOWNLOAD_TAG: Already Downloading");
                // return "ALREADY DOWNLOADING";
            }

            Log.d(TAG,"LINK "+downloadlink);
            URL url=new URL(downloadlink);
            HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
            if(downloaded>0)
                urlConnection.setRequestProperty("Range","byte="+downloaded);
            urlConnection.connect();
            int status = urlConnection.getResponseCode();
            InputStream inputStream=urlConnection.getInputStream();
            long totalSize=urlConnection.getContentLength();
            if(totalSize<=downloaded){
                returnData= "COMPLETED";
                Log.d(TAG, "handleDownLoad: download completed:");
                sendUpdate(1, "completed");
                //publishProgress("File checked "+ URLUtil.guessFileName(file.getAbsolutePath(), null, null));
               //this.stopSelf();
               // return returnData;
            }
            this.downloadedPath=cacheDownloadFile.getAbsolutePath();
            byte[] buffer=new byte[1024];
            int bufferLength=0;
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            FileOutputStream fileOutput=new FileOutputStream(cacheDownloadFile);
            long d=0;
            long starttime=System.currentTimeMillis();
            while ((bufferLength=bis.read(buffer))>0){
                fileOutput.write(buffer,0,bufferLength);
                downloaded+=bufferLength;
                d+=bufferLength;
                //String l=" "+Tools.getFileName(file.getAbsolutePath())+" ( "+Tools.convertMemory(downloaded)+" / "+Tools.convertMemory(totalSize)+" )";
                //String l="  "+Tools.convertMemory(downloaded)+" / "+Tools.convertMemory(totalSize)+" ( "+getDownloadSpeed(starttime,d)+" )";
                float per = ((float) downloaded/(float) totalSize)*100;
                String l = ""+(int)per;
                //Log.d(TAG, "handleDownLoad:Progress: "+l);
                sendUpdate(2, l);
               // publishProgress(l);
                if(downloaded>=totalSize){
                    break;
                }
            }
            Log.d(TAG,"DWONLOADED TO "+downloadedPath+" ("+cacheDownloadFile.length()+")");
            fileOutput.close();
            try {
                if(OustSdkTools.fileCopy(file,cacheDownloadFile)){
                    Log.d(TAG,"file Copied, delete cache");
                    cacheDownloadFile.delete();
                    //file.renameTo(new File("oustlearn_"+file));
                }
                else {
                        Log.d(TAG, "handleDownLoad: else part");
                        urlConnection.disconnect();
                        if (cacheDownloadFile.exists()) {
                            cacheDownloadFile.delete();
                        }
                        if (file.exists()) {
                            file.delete();
                        }
                        handleDownLoad(fileURL, fileDestinationPath, fileName, isOustLearn);
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                if(cacheDownloadFile.exists())
                {
                    cacheDownloadFile.delete();
                }
                if(file.delete())
                {
                    file.delete();
                }
                //handleDownLoad(fileURL, fileDestinationPath, fileName, isOustLearn);
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            try{
                sendUpdate(1, "completed");
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            returnData="COMPLETED";*/
        } catch (MalformedURLException e) {
            Log.d(TAG, "handleDownLoad: MalformedURLException");
            returnData=null;
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            sendUpdate(3, e.getLocalizedMessage());
            //this.stopSelf();
            // publishProgress(e.toString());
        } catch (IOException e) {
            Log.d(TAG, "handleDownLoad: IOException");
            returnData=null;
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            sendUpdate(3, e.getLocalizedMessage());
            // publishProgress(e.toString());
        }
    }

    private void sendUpdate(int i, String message) {
        Intent intent = new Intent();
        switch (i)
        {
            case 1:
                if(mDownLoadUpdateInterface!=null)
                {
                    mDownLoadUpdateInterface.onDownLoadStateChanged(message, ON_COMPLETED);
                }
                intent.setAction(ACTION_COMPLETE);
                intent.putExtra("MSG", message);
                bundle.putString("MSG", message);
                break;
            case 2:
                if(mDownLoadUpdateInterface!=null)
                {
                    mDownLoadUpdateInterface.onDownLoadProgressChanged(message, message);
                }
                intent.setAction(ACTION_PROGRESS);
                intent.putExtra("MSG", message);
                bundle.putString("MSG", message);
                break;

            case 3:
                if(mDownLoadUpdateInterface!=null)
                {
                    mDownLoadUpdateInterface.onDownLoadStateChanged(message, ON_ERROR);
                }
                bundle.putString("MSG", message+""+file.toString());
                intent.setAction(ACTION_ERROR);
                intent.putExtra("MSG", message);
                break;
        }
        if(mResultReceiver!=null)
        {
            mResultReceiver.send(i, bundle);
        }
        // intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public static void setContext(DownLoadUpdateInterface downLoadUpdateInterface){
        mDownLoadUpdateInterface = downLoadUpdateInterface;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        sendUpdate(1, "completedDestroy");
        super.onDestroy();
    }

}
