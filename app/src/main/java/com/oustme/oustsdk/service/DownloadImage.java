package com.oustme.oustsdk.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import androidx.annotation.Nullable;

import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
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

public class DownloadImage extends IntentService {
    public DownloadImage() {
        super("DownloadImage");
    }

    private final String TAG = "DownloadImage";
    private String downloadlink,fileDestination;
    private String downloadedPath="";
    private long downloaded=0;
    private File file;
    private File cacheDownloadFile;
    private Context mContext;
    public static final String ACTION_ERROR = "com.oustme.oustsdk.service.action.ERROR";
    public static final String ACTION_COMPLETE = "com.oustme.oustsdk.service.action.COMPLETE";
    public static final String ACTION_PROGRESS = "com.oustme.oustsdk.service.action.PROGRESS";

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: ");
        if(intent!=null) {
            final String fileURL = intent.getStringExtra(AppConstants.StringConstants.FILE_URL);
            final String fileDestination = intent.getStringExtra(AppConstants.StringConstants.FILE_DESTN);
            final String fileName = intent.getStringExtra(AppConstants.StringConstants.FILE_NAME);
            final boolean isOustLearn = intent.getBooleanExtra(AppConstants.StringConstants.IS_OUST_LEARN, false);

            Log.d(TAG, "Files shared :"+fileURL+" --- "+fileDestination+" --- "+fileName+" --- "+isOustLearn);
            handleDownLoad(fileURL, fileDestination, fileName, isOustLearn);
        }
    }

    private void handleDownLoad(String fileURL, String fileDestinationPath,String fileName, boolean isOustLearn) {
        Log.d(TAG, "handleDownLoad: ");
        this.downloadlink=fileURL;
        this.fileDestination=fileDestinationPath;
        this.mContext = DownloadImage.this;
        if(isOustLearn) {
            file = new File(fileDestination,"oustlearn_"+fileName);
        } else {
            file = new File(fileDestination,fileName);
        }

        if(file.exists()){
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }

        cacheDownloadFile=new File(mContext.getCacheDir()+ fileName);
        downloaded=0;

        /*try {
            if(cacheDownloadFile.isFile())
                downloaded= OustSdkTools.getFileSize(cacheDownloadFile);
            else
                downloaded=0;
            Log.d(TAG,downloaded+" <- "+cacheDownloadFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }*/

        try {
            File chacheDir= mContext.getCacheDir();
            if(!chacheDir.isDirectory())
                chacheDir.mkdirs();

            String withoutBase = OustMediaTools.removeAwsOrCDnUrl(fileURL);
            String filename2 = OustMediaTools.getMediaFileName(fileURL);
            if(withoutBase.equalsIgnoreCase(filename2) || fileURL.equalsIgnoreCase("http://di5jfel2ggs8k.cloudfront.net/")) {
                Log.d(TAG, "Base check return ");
                sendUpdate(1, "completed");
                return;
            }

            if(!cacheDownloadFile.exists()){
                cacheDownloadFile.createNewFile();
            }
            else {
                cacheDownloadFile.delete();
                cacheDownloadFile.createNewFile();
                Log.d(TAG, "FILE_DOWNLOAD_TAG: Delete cache file");
            }

            Log.d(TAG,"LINK "+downloadlink);
            URL url=new URL(downloadlink);
            HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
            if(downloaded>0)
                urlConnection.setRequestProperty("Range","byte="+downloaded);
            urlConnection.connect();
            InputStream inputStream=urlConnection.getInputStream();
            long totalSize=urlConnection.getContentLength();

            Log.d(TAG, "File size LINK --- File path LINK :"+downloadlink+" --- size:"+totalSize);
            OustPreferences.saveTimeForNotification("oustlearn_"+fileName, totalSize);

            /*if(file.exists() && file.length()>=totalSize) {
                Log.d(TAG, "File size Downloaded: "+file.length()+" --- file exit return");
                sendUpdate(1, "completed");
                //returnData= "COMPLETED";
                return;
            } else if(file.exists()){
                Log.d(TAG, "File size not matched, so deleting the previous file: "+file.getName());
                file.delete();
            }*/

            /*if(totalSize<=downloaded){
                Log.d(TAG, "handleDownLoad: download completed:");
                sendUpdate(1, "completed");
                return;
            }*/

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
                File dest;
                if(isOustLearn) {
                    dest = new File(fileDestination,"oustlearn_"+fileName);
                } else {
                    dest = new File(fileDestination,fileName);
                }

                if(OustSdkTools.fileCopy(dest, cacheDownloadFile)){
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
                    handleDownLoad(fileURL, fileDestinationPath, fileName, isOustLearn);
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                if(cacheDownloadFile.exists()) {
                    cacheDownloadFile.delete();
                }
                if(file.exists()) {
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
            //returnData="COMPLETED";


        } catch (MalformedURLException e) {
            Log.d(TAG, "handleDownLoad: MalformedURLException");
            //returnData=null;
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            sendUpdate(3, e.getLocalizedMessage());
            //this.stopSelf();
            // publishProgress(e.toString());
        } catch (IOException e) {
            Log.d(TAG, "handleDownLoad: IOException");
            //returnData=null;
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            sendUpdate(3, e.getLocalizedMessage());
            // publishProgress(e.toString());
        }

    }

    private void sendUpdate(int i, String message) {
        Log.d(TAG, "sendUpdate: message: "+message);
        Intent intent = new Intent();
        switch (i)
        {
            case 1:
                intent.setAction(ACTION_COMPLETE);
                intent.putExtra("MSG", message);
                break;
            case 2:
                intent.setAction(ACTION_PROGRESS);
                intent.putExtra("MSG", message);
                break;

            case 3:
                intent.setAction(ACTION_ERROR);
                intent.putExtra("MSG", message);
                break;
        }
        sendBroadcast(intent);
    }
}
