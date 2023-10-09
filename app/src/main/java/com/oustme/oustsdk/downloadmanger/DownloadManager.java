package com.oustme.oustsdk.downloadmanger;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import android.webkit.URLUtil;

import com.oustme.oustsdk.activity.rough.PopUpActivity;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownloadManager extends AsyncTask<String,String,String> {
    private static final String TAG = "DownloadManager";
    private String downloadlink,fileDestination;
    public static final int ON_INIT=100,ON_ERROR=102,ON_PROGRASS=103,ON_COMPLETED=104,STATUS_DOWNLOADED=1500,STATUS_NOT_YET=1501;
    private onUpdateListener onUpdateListener;
    private String downloadedPath="";
    private long downloaded=0;
    private File file;
    private String returnData=null;
    private File cacheDownloadFile;
    private Context mContext;
    private String fileName;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public DownloadManager(Context mContext, String fileURL, String fileDestinationPath, boolean isOustLearn){
        Log.d(TAG, "DownloadManager: "+fileURL+" filedistaniation:"+fileDestinationPath);
        this.downloadlink=fileURL;
        this.fileDestination=fileDestinationPath;
        this.mContext = OustSdkApplication.getContext();
        file=new File(fileDestination, URLUtil.guessFileName(downloadlink, null, null));
        cacheDownloadFile=new File(mContext.getCacheDir()+ URLUtil.guessFileName(downloadlink, null, null));
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
        fireOnUpdate(ON_INIT,"init ...");
    }

    public DownloadManager(Context mContext, String fileURL, String fileDestinationPath,String fileName, boolean isOustLearn){
        Log.d(TAG, "DownloadManager: "+fileURL+" filedistaniation:"+fileDestinationPath+"fileName:"+fileName);
        this.downloadlink=fileURL;
        this.fileDestination=fileDestinationPath;
        this.mContext = mContext;
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
        fireOnUpdate(ON_INIT,"init ...");
    }


    @Override
    protected String doInBackground(String... params) {
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
                return "COMPLETED";//file exist
            }

            fireOnUpdate(ON_INIT, "Started");
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
                publishProgress("File checked "+URLUtil.guessFileName(file.getAbsolutePath(), null, null));
                return returnData;
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
                publishProgress(l);
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
            publishProgress(e.toString());
        } catch (IOException e) {
            returnData=null;
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            publishProgress(e.toString());
        }

        return returnData;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        fireOnUpdate(ON_PROGRASS,values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s!=null){
            fireOnUpdate(ON_COMPLETED,downloadedPath);
        }else{
            fireOnUpdate(ON_ERROR,"Download failed");
        }
    }

    public interface onUpdateListener{
        void onUpdate(int code,String message);
    }
    public void setOnUpdateListener(onUpdateListener onUpdateListener){
        this.onUpdateListener=onUpdateListener;
    }
    private void fireOnUpdate(int code,String message){
        if(onUpdateListener!=null)
            onUpdateListener.onUpdate(code,message);
    }

    private String getDownloadSpeed(long starttime,float totalDownloaded) {
        long elapsedTime = System.currentTimeMillis() - starttime;
        //byte :
        float speed=1000f * totalDownloaded / elapsedTime;
        return convert(speed);
    }
    private String convert(float value){
        long kb=1024
                ,mb=kb*1024
                ,gb=mb*1024;

        if(value<kb){
            String speed=(value+"");
            speed=speed.substring(0,speed.indexOf('.')+2);
            return speed+" B/s";
        }else if(value<mb){
            value=value/kb;
            String speed=(value+"");
            speed=speed.substring(0,speed.indexOf('.'));
            return (speed)+" KB/s";
        }else if(value<gb){
            value=(value/mb);
            String speed=(value+"");
            speed=speed.substring(0,speed.indexOf('.'));
            return speed+" MB/s";
        }
        return "";
    }
}
