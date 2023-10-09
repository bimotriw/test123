package com.oustme.oustsdk.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import android.webkit.URLUtil;

import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadManager;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

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
public class DownLoadIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_ERROR = "com.oustme.oustsdk.service.action.ERROR";
    public static final String ACTION_COMPLETE = "com.oustme.oustsdk.service.action.COMPLETE";
    public static final String ACTION_PROGRESS = "com.oustme.oustsdk.service.action.PROGRESS";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.oustme.oustsdk.service.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.oustme.oustsdk.service.extra.PARAM2";


    private static final String TAG = "DownLoadIntentService";
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
    private static DownLoadUpdateInterface mDownLoadUpdateInterface;
    private io.sentry.Sentry Sentry;

    public DownLoadIntentService() {
        super("DownLoadIntentService");
        Log.d(TAG, "DownLoadIntentService: ");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DownLoadIntentService.class);
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
        Intent intent = new Intent(context, DownLoadIntentService.class);
        //intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

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
            final String fileURL = intent.getStringExtra(AppConstants.StringConstants.FILE_URL);
            final String fileDestination = intent.getStringExtra(AppConstants.StringConstants.FILE_DESTN);
            final String fileName = intent.getStringExtra(AppConstants.StringConstants.FILE_NAME);
            final boolean isOustLearn = intent.getBooleanExtra(AppConstants.StringConstants.IS_OUST_LEARN, false);
            handleDownLoad(fileURL, fileDestination, fileName, isOustLearn);
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleDownLoad(String fileURL, String fileDestinationPath,String fileName, boolean isOustLearn) {
        this.downloadlink=fileURL;
        this.fileDestination=fileDestinationPath;
        this.mContext = DownLoadIntentService.this;
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
            Log.d("FILE_DOWNLOAD_TAG_p",downloaded+" <- "+cacheDownloadFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        //fireOnUpdate(ON_INIT,"init ...");

        try {
            File dir=new File(fileDestination);
            File chacheDir= mContext.getCacheDir();
            if(!chacheDir.isDirectory())
                chacheDir.mkdirs();
            /*if(!dir.isDirectory()){
                dir.mkdirs();
            }*/

            if(file.exists()) {
                Log.d("FILE_DOWNLOAD_TAG","File exist return complete");
                sendUpdate(1, "completed");
                this.stopSelf();
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

            Log.d("FILE_DOWNLOAD_TAG","LINK "+downloadlink);
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
               this.stopSelf();
               // return returnData;
            }
            this.downloadedPath=cacheDownloadFile.getAbsolutePath();
            byte[] buffer=new byte[1024];
            int bufferLength=0;
            FileOutputStream fileOutput=new FileOutputStream(cacheDownloadFile);
            long d=0;
            long starttime=System.currentTimeMillis();
            while ((bufferLength=inputStream.read(buffer))>0){
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
            Log.d("FILE_DOWNLOAD_TAG","DWONLOADED TO "+downloadedPath+" ("+cacheDownloadFile.length()+")");
            fileOutput.close();
            try {
                if(OustSdkTools.fileCopy(file,cacheDownloadFile)){
                    Log.d("FILE_DOWNLOAD_TAG","file Copied, delete cache");
                    cacheDownloadFile.delete();
                    //file.renameTo(new File("oustlearn_"+file));
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            returnData="COMPLETED";
        } catch (MalformedURLException e) {
            returnData=null;
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            sendUpdate(3, e.getLocalizedMessage());
            this.stopSelf();
           // publishProgress(e.toString());
        } catch (IOException e) {
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
                break;
            case 2:
                if(mDownLoadUpdateInterface!=null)
                {
                    mDownLoadUpdateInterface.onDownLoadProgressChanged(message, message);
                }
                intent.setAction(ACTION_PROGRESS);
                intent.putExtra("MSG", message);
                break;

            case 3:
                if(mDownLoadUpdateInterface!=null)
                {
                    mDownLoadUpdateInterface.onDownLoadStateChanged(message, ON_ERROR);
                }
                intent.setAction(ACTION_ERROR);
                intent.putExtra("MSG", message);
                break;
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
}
