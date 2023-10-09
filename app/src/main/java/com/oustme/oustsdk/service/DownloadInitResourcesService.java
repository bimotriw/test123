package com.oustme.oustsdk.service;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;
import android.util.Log;


import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.Gson;
import com.oustme.oustsdk.BuildConfig;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.response.common.LanguageClass;
import com.oustme.oustsdk.response.common.LanguagesClasses;
import com.oustme.oustsdk.room.EntityResourseStrings;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by oust on 4/25/19.
 */

public class DownloadInitResourcesService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownloadInitResourcesService(String name) {
        super(name);
        this.orgId=name;
    }
    private String orgId;
    private float totalResources = 0;
    private float resourcesDownloaded = 0;
    private int RES_AUDIO = 1, RES_IMAGE = 2, RES_FONT = 3;
    private String TAG = "DownloadInitResourcesService";

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!OustPreferences.getAppInstallVariable("allresourcesDownloadeda")) {
            startDownloadingResourses();
        }
    }

    public void startDownloadingResourses() {
        Log.d(TAG,"start downloading resources");
        String imagePath1 = "appImages/splash/org/" + (orgId.toUpperCase()) + "/android/bgImage";
        downloadImage(orgId, imagePath1, "splashScreen", true);
        String imagePath2 = "appImages/splash/org/" + (orgId.toUpperCase()) + "/android/icon";
        downloadImage(orgId, imagePath2, "splashIcon", false);
        calculateResourceSize();
        totalResources += 1;
        OustPreferences.saveAppInstallVariable("isDownloadedImageLastTime", true);
        LanguageClass languageClass = new LanguageClass();
        languageClass.setFileName("englishStr.properties");
        languageClass.setIndex(0);
        languageClass.setLanguagePerfix("en");
        languageClass.setName("English");

        LanguagesClasses classes = new LanguagesClasses();
        List<LanguageClass> languageClasses = new ArrayList<>();
        languageClasses.add(languageClass);
        classes.setLanguageClasses(languageClasses);
        Gson gson = new Gson();
        String langStr = gson.toJson(classes);
        OustPreferences.save("alllanguage", langStr);
        downLoad(languageClass, languageClass.getFileName(), 0);
    }


    private int totalImageResourceSize = 0;
    private int totalAudioResourceSize = 0;
    private int totalFontResourceSize = 0;
    private int totalDownloadedResources = 0;
    private int downloadStartedResources = 0;
    private int totalResourceTobeDownloaded = 0;

    private void calculateResourceSize() {
        totalImageResourceSize = (getResources().getStringArray(R.array.all_images)).length;
        totalResourceTobeDownloaded += totalImageResourceSize;
        totalResources += totalImageResourceSize;

        totalAudioResourceSize = (getResources().getStringArray(R.array.sounds)).length;
        totalResourceTobeDownloaded += totalAudioResourceSize;
        totalResources += totalAudioResourceSize;

        totalFontResourceSize = (getResources().getStringArray(R.array.fonts)).length;
        totalResourceTobeDownloaded += totalFontResourceSize;
        totalResources += totalFontResourceSize;

        downloadStrategyOfResource();
    }

    private void downloadStrategyOfResource() {
        try {
            String[] audioResList = getResources().getStringArray(R.array.sounds);
            String[] imageResList = getResources().getStringArray(R.array.all_images);
            String[] fontResList = getResources().getStringArray(R.array.fonts);

            if (downloadStartedResources < totalResourceTobeDownloaded) {
                totalDownloadedResources = downloadStartedResources;
                downloadStartedResources += 10;
                if (downloadStartedResources > totalResourceTobeDownloaded) {
                    downloadStartedResources = totalResourceTobeDownloaded;
                }
                for (int i = totalDownloadedResources; i < downloadStartedResources; i++) {
                    if (i < totalImageResourceSize) {
                        downLoadAudioImagesResource(RES_IMAGE, imageResList[i]);
                    } else if (i >= totalImageResourceSize && i < totalImageResourceSize + totalAudioResourceSize) {
                        downLoadAudioImagesResource(RES_AUDIO, audioResList[i - totalImageResourceSize]);
                    } else if (i >= totalImageResourceSize + totalAudioResourceSize && i < totalResourceTobeDownloaded) {
                        downLoadFontResources(RES_FONT, fontResList[i - (totalImageResourceSize + totalAudioResourceSize)]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void downLoadAudioImagesResource(int type, final String filename) {
        String imagePath = "";
        if (type == RES_AUDIO) {
            imagePath = "AppResources/Android/All/Audios/";
        } else if (type == RES_IMAGE) {
            imagePath = "AppResources/Android/All/Images/";
        }
        try {
            final File file = new File(getFilesDir(), filename);
            if ((file != null) && (!file.exists())) {
                String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
                String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
                AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
                s3.setRegion(com.amazonaws.regions.Region.getRegion(Regions.US_WEST_1));
                TransferUtility transferUtility = new TransferUtility(s3, this);
                String resPath = imagePath + filename;
                TransferObserver transferObserver = transferUtility.download("img.oustme.com", resPath, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            showDownloadProgress();
                        } else if (state == TransferState.FAILED) {
                             Log.i("error", "error");
                            Log.i("failed", "failed");
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                         Log.i("error", "error");
                        Log.i("error", "error");
                    }
                });
            } else {
                showDownloadProgress();
            }
        } catch (Exception e) {
            
            Log.e(TAG, "downloadImage" + e.getMessage());
        }
    }

    public void downLoadFontResources(int type, final String filename) {
        String imagePath = "AppResources/Android/All/Fonts/";
        try {
            final File file = new File(getFilesDir(), filename);
            if ((file != null) && (!file.exists())) {
                String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
                String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
                AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
                s3.setRegion(com.amazonaws.regions.Region.getRegion(Regions.US_WEST_1));
                TransferUtility transferUtility = new TransferUtility(s3, this);
                String resPath = imagePath + filename;
                TransferObserver transferObserver = transferUtility.download("img.oustme.com", resPath, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            showDownloadProgress();
                            Log.i("complete", "completed");
                        } else if (state == TransferState.FAILED) {
                            Log.i("failed", "failed");
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Log.d("error", "error");
                    }
                });
            }
        } catch (Exception e) {
             Log.i("error", "error");
            Log.e(TAG, "downloadImage" + e.getMessage());
        }
    }

    public void downLoad(final LanguageClass languageClass, String fileName1, final int languageIndex) {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                //         checkLoginProcess();
                return;
            }
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(Regions.US_WEST_1));
            TransferUtility transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());
            String key = "languagePacks/mobile/" + fileName1;
            final File file = getTempFile();
            if (file != null) {
                TransferObserver transferObserver = transferUtility.download("img.oustme.com", key, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            createFileAndSave(languageClass, languageIndex, file);
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void downloadImage(final String tanentId, String imagePath, final String filename, final boolean isBackgroundImage) {
        try {
            Log.e(TAG, "downloadImage");
            final File file = new File(getFilesDir(), filename);
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(Regions.US_WEST_1));
            TransferUtility transferUtility = new TransferUtility(s3, this);
            TransferObserver transferObserver = transferUtility.download("img.oustme.com", imagePath, file);
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        //saveData(file, filename, isBackgroundImage);
                        Log.e(TAG, "doiwnload success");
                        showDownloadProgress();
                    } else if (state == TransferState.FAILED) {
                        Log.e(TAG, "doiwnload failed");
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "downloadImage" + e.getMessage());
        }
    }

    public File getTempFile() {
        File file;
        try {
            String fileName = "myname.properties";
            file = File.createTempFile(fileName, null, getCacheDir());
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return null;
    }


    public void createFileAndSave(LanguageClass languageClass, int languageIndex, File file) {
        try {
            Properties prop = new Properties();
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                prop.load(inputStream);
            }
            Map<String, String> map = new HashMap<>();
            for (Map.Entry<Object, Object> x : prop.entrySet()) {
                map.put((String) x.getKey(), (String) x.getValue());
            }
            Gson gson = new Gson();
            String hashmapStr = gson.toJson(map);
            EntityResourseStrings resourseStringsModel = new EntityResourseStrings();
            resourseStringsModel.setLanguagePerfix(languageClass.getLanguagePerfix());
            resourseStringsModel.setIndex(languageClass.getIndex());
            resourseStringsModel.setName(languageClass.getName());
            resourseStringsModel.setHashmapStr(hashmapStr);
            RoomHelper.addorUpdateResourceStringModel(resourseStringsModel);
            file.delete();
            OustStrings.init();
            showDownloadProgress();
        } catch (Exception e) {
            Log.e(TAG, "save language file" + e.getMessage());
        }
    }
    public void showDownloadProgress() {
        resourcesDownloaded++;
        if (totalResources > 0) {
            float progress = resourcesDownloaded / totalResources;
            int progress1 = (int) (progress * 100);
            if (progress1 > 100) {
                progress1 = 100;
            }
            if (resourcesDownloaded >= (totalResources)) {
                OustPreferences.saveAppInstallVariable("allresourcesDownloadeda", true);
                this.stopSelf();
            } else {
                downloadStrategyOfResource();
            }
        }else{
            this.stopSelf();
        }
    }
}
