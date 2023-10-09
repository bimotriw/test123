package com.oustme.oustsdk.data.handlers.impl;

import android.content.Context;
import android.util.Log;


import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.oustme.oustsdk.BuildConfig;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;

/**
 * Created by oust on 1/15/19.
 */

public abstract class MediaDataDownloader {
    private static final String TAG = "MediaDataDownloader";
    private Context mContext;
    private Map<String, String> addedFiles;
    private int retryCount;

    public MediaDataDownloader(Context mContext) {
        this.mContext = mContext;
        addedFiles = new HashMap<>();
    }
    public void initDownload(String mediaPath){
        if (mediaPath != null && !mediaPath.isEmpty()) {
            mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
            String fileName = OustMediaTools.getMediaFileName(mediaPath);
            download(fileName,mediaPath.replace(fileName, ""), mediaPath);
        }
    }

    public void download(String fileName, String folderName, final String mediaPath) {
        String path = mContext.getFilesDir()+"/oustlearn_"+fileName;
        final File file = new File(path);
        if (!file.exists()) {
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = TransferUtility.builder().s3Client(s3).context(mContext).build();
            String resPath = folderName + fileName;
            Log.d(TAG, "download:respath: "+resPath+" file:"+file.toString());
            TransferObserver transferObserver = transferUtility.download(AppConstants.MediaURLConstants.BUCKET_NAME, resPath, file);
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        downloadComplete();
                        addedFiles.remove(id+"");
                    } else if (state == TransferState.FAILED) {
                        if(file!=null){
                            file.delete();
                        }
                        if(retryCount<=3)
                        {
                            Log.d(TAG, "retryCount: "+retryCount);
                            retryDownload(addedFiles.get(id+""));
                            addedFiles.remove(id+"");
                            retryCount++;
                        }
                        else {
                            //downFailed("Downloading " + state.name());
                            Log.i("failed", "module data failed");
                            Log.d(TAG, "onStateChanged: failed ID:" + id);
                            downloadComplete();
                        }
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.d(TAG, "onError: exception from aws: fileName:"+addedFiles.get(id+"")+" ID:"+id);
                        //downFailed("Failed to Download Resources");
                       // downloadComplete();
                    if(file!=null)
                    {
                        file.delete();
                    }
                    downloadFromCDN(mediaPath, "");
                    ex.printStackTrace();
                }
            });
            addedFiles.put(transferObserver.getId()+"", mediaPath);
            Log.d(TAG, "download: tranfsr fileID:"+transferObserver.getId()+"\n fileName:"+fileName);
        } else {
            downloadComplete();
        }
    }

    public abstract void downloadComplete();
    public abstract void downFailed(String message);

    private void retryDownload(String mediaPath)
    {
        Log.d(TAG, "retryDownload: "+mediaPath);
        if (mediaPath != null && !mediaPath.isEmpty()) {
            mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
            String fileName = OustMediaTools.getMediaFileName(mediaPath);
            download(fileName,mediaPath.replace(fileName, ""), mediaPath);
        }
    }

    private void downloadFromCDN(String mediaPath, String destn)
    {
        DownloadFiles downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {

            }
            @Override
            public void onDownLoadError(String message, int errorCode) {
                downFailed(message);
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                if(code==_COMPLETED)
                {
                    downloadComplete();
                }
            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });
        downloadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH+mediaPath,  OustSdkApplication.getContext().getFilesDir()+"/", OustMediaTools.getMediaFileName(mediaPath), true, false);
    }

}
