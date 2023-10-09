package com.oustme.oustsdk.downloadmanger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.util.Log;


import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.oustme.oustsdk.BuildConfig;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.service.DownLoadFilesIntentService;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amazonaws.mobileconnectors.s3.transferutility.TransferState.CANCELED;
import static com.amazonaws.mobileconnectors.s3.transferutility.TransferState.COMPLETED;
import static com.amazonaws.mobileconnectors.s3.transferutility.TransferState.FAILED;
import static com.amazonaws.mobileconnectors.s3.transferutility.TransferState.IN_PROGRESS;
import static com.amazonaws.mobileconnectors.s3.transferutility.TransferState.PART_COMPLETED;
import static com.amazonaws.mobileconnectors.s3.transferutility.TransferState.PAUSED;
import static com.amazonaws.mobileconnectors.s3.transferutility.TransferState.RESUMED_WAITING;
import static com.amazonaws.mobileconnectors.s3.transferutility.TransferState.WAITING;
import static com.amazonaws.mobileconnectors.s3.transferutility.TransferState.WAITING_FOR_NETWORK;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;

public class DownloadFiles {
    private static final String TAG = "DownloadFiles";
    private DownLoadUpdateInterface mDownLoadUpdateInterface;
    private TransferUtility transferUtility;
    private Context mContext;
    private List<String> mListS3Ids;
    private Map<String, String> specialRequest;
    private DTOCourseCard courseCardClass;
    private boolean introCard;
    public static int _WAITING = 201, _IN_PROGRESS = 202, _PAUSED = 203, _RESUMED_WAITING = 204, _COMPLETED = 205, _CANCELED = 206, _FAILED = 207, NO_NETWORK = 208, PARTIAL = 209;

    public DownloadFiles(Context mContext, DownLoadUpdateInterface downLoadUpdateInterface) {
        this.mDownLoadUpdateInterface = downLoadUpdateInterface;
        this.mContext = mContext;
        mListS3Ids = new ArrayList<>();
        String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
        String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
        s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
        transferUtility = new TransferUtility(s3, this.mContext);
        specialRequest = new HashMap<>();
    }

    public void startDownLoad(final String downloadLink, final String fileDestinationPath, final String fileName, final boolean isOustLearn, final boolean isVideo) {
        sendToDownloadService(OustSdkApplication.getContext(), downloadLink, fileDestinationPath, fileName, isOustLearn, isVideo);

    }

    private void sendToDownloadService(Context context, String downloadPath, String destination, String fileName, boolean isOustLearn, boolean isVideo) {
        try {
            Intent intent = new Intent(OustSdkApplication.getContext(), DownLoadFilesIntentService.class);
            intent.putExtra(AppConstants.StringConstants.IS_OUST_LEARN, isOustLearn);
            intent.putExtra(AppConstants.StringConstants.FILE_NAME, fileName);
            intent.putExtra(AppConstants.StringConstants.FILE_URL, downloadPath);
            intent.putExtra(AppConstants.StringConstants.FILE_DESTN, destination);
            intent.putExtra(AppConstants.StringConstants.IS_VIDEO, isVideo);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private MyFileDownLoadReceiver myFileDownLoadReceiver;

    private void setReceiver() {
        myFileDownLoadReceiver = new MyFileDownLoadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_COMPLETE);
        intentFilter.addAction(ACTION_ERROR);
        intentFilter.addAction(ACTION_PROGRESS);
        if (OustSdkApplication.getContext() != null) {
            OustSdkApplication.getContext().registerReceiver(myFileDownLoadReceiver, intentFilter);
        }

        //LocalBroadcastManager.getInstance(OustSdkApplication.getContext()).registerReceiver(myFileDownLoadReceiver, intentFilter);
    }

    private class MyFileDownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction() != null) {
                    try {
                        if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {
                            mDownLoadUpdateInterface.onDownLoadProgressChanged("Progress", intent.getStringExtra("MSG"));
                            //setDownloadingPercentage(Integer.valueOf(intent.getStringExtra("MSG")), "");
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                            mDownLoadUpdateInterface.onDownLoadStateChanged("", _COMPLETED);
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
                            mDownLoadUpdateInterface.onDownLoadError(intent.getStringExtra("MSG"), _FAILED);

                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }
        }
    }

    public void startDownLoadGif(final String pathToSave, final String bucketName, final String fileName, final boolean isGif, final boolean isAWS) {
        try {
            TransferObserver transferObserver = transferUtility.download(bucketName, fileName, new File(pathToSave), new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == WAITING)
                        mDownLoadUpdateInterface.onDownLoadStateChanged("waiting", _WAITING);
                    else if (state == IN_PROGRESS) {
                        mDownLoadUpdateInterface.onDownLoadStateChanged("in progress", _IN_PROGRESS);
                    } else if (state == PAUSED)
                        mDownLoadUpdateInterface.onDownLoadStateChanged("Paused", _PAUSED);
                    else if (state == RESUMED_WAITING)
                        mDownLoadUpdateInterface.onDownLoadStateChanged("resumed waiting", _RESUMED_WAITING);
                    else if (state == COMPLETED) {
                        try {
                            if (specialRequest.size() > 0 && specialRequest.get("" + id) != null) {
                                specialRequest.remove("" + id);
                                mDownLoadUpdateInterface.onDownLoadStateChangedWithId("Completed", _COMPLETED, "" + id);
                            } else {
                                mDownLoadUpdateInterface.onDownLoadStateChanged("completed", _COMPLETED);
                                Log.d(TAG, "onStateChanged: removing" + id);
                                mListS3Ids.remove(id + "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    } else if (state == CANCELED)
                        mDownLoadUpdateInterface.onDownLoadError("Download canceled", _CANCELED);
                    else if (state == FAILED)
                        mDownLoadUpdateInterface.onDownLoadError("Download failed", _FAILED);
                    else if (state == WAITING_FOR_NETWORK)
                        mDownLoadUpdateInterface.onDownLoadError("no network to download", NO_NETWORK);
                    else if (state == PART_COMPLETED)
                        mDownLoadUpdateInterface.onDownLoadStateChanged("Partial", PARTIAL);
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentage = ((float) bytesCurrent / (float) bytesTotal * 100);
                    mDownLoadUpdateInterface.onDownLoadProgressChanged("Progress", "" + (int) percentage);
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.d(TAG, "onError: failed Id:" + id + " pathName:" + pathToSave + " filename:" + fileName);
                    mDownLoadUpdateInterface.onDownLoadError(ex.getLocalizedMessage(), _FAILED);
                    ex.printStackTrace();
                }
            });
            if (isGif) {
                Log.d(TAG, "startDownLoad: adding Special:" + transferObserver.getId());
                mDownLoadUpdateInterface.onAddedToQueue(transferObserver.getId() + "");
                specialRequest.put("" + transferObserver.getId(), "" + transferObserver.getId());
            } else {
                Log.d(TAG, "startDownLoad: adding:" + transferObserver.getId());
                mListS3Ids.add(transferObserver.getId() + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}
