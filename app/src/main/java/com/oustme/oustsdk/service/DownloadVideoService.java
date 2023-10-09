package com.oustme.oustsdk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;


import androidx.annotation.Nullable;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.oustme.oustsdk.fragments.common.ReadmorePopupFragment;
import com.oustme.oustsdk.fragments.courses.ModuleOverViewFragment;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.io.File;

public class DownloadVideoService extends IntentService {

    private static final String TAG = "DownloadVideoService";
    public DownloadVideoService() {
        super(DownloadVideoService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String videoFileName = intent.getStringExtra("videoFileName");
        downloadVideo(("course/media/video/" + videoFileName),videoFileName);
    }

    public void downloadVideo(String pathName,final String videoFileName){
        try {
            Log.d(TAG, "initializing downloadVideo service: ");
            String key=pathName;
            String path= Environment.getExternalStorageDirectory()+"/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files/temp"+videoFileName;
            final File file = new File(path);

            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility=new TransferUtility(s3, OustSdkApplication.getContext());
            if(file!=null) {
                TransferObserver transferObserver = transferUtility.download(AppConstants.MediaURLConstants.BUCKET_NAME, key, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            String path=Environment.getExternalStorageDirectory()+"/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files/"+videoFileName;
                            final File finalFile = new File(path);
                            file.renameTo(finalFile);
                            savePercentage(videoFileName, 100);
                        }else if(state==TransferState.FAILED||state==TransferState.CANCELED){
                            file.delete();
                            OustPreferences.clear(videoFileName);
                        }
                    }
                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        if(bytesTotal>0) {
                            float f1=(((float)bytesCurrent)/((float)bytesTotal))*100;
                            savePercentage(videoFileName,((int)f1));
                        }
                    }
                    @Override
                    public void onError(int id, Exception ex) {
                        OustPreferences.clear(videoFileName);
                        file.delete();
                    }
                });
            }else {}
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            OustPreferences.clear(videoFileName);
        }
    }

    private void savePercentage(String videoFileName,int percentage){
        try {
            OustPreferences.saveintVar(videoFileName, (percentage));
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(ModuleOverViewFragment.MyVideoDownloadReceiver.PROCESS_RESPONSE);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sendBroadcast(broadcastIntent);

            Intent broadcastIntent1 = new Intent();
            broadcastIntent1.setAction(ReadmorePopupFragment.MyReadmoreVideoDownloadReceiver.PROCESS_RESPONSE);
            broadcastIntent1.addCategory(Intent.CATEGORY_DEFAULT);
            sendBroadcast(broadcastIntent1);
        }catch (Exception e){}
    }
}
