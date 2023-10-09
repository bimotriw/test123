package com.oustme.oustsdk.service.login;

import android.app.Activity;
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
import com.google.gson.Gson;
import com.oustme.oustsdk.BuildConfig;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.response.common.LanguageClass;
import com.oustme.oustsdk.response.common.LanguagesClasses;
import com.oustme.oustsdk.room.EntityResourceCollection;
import com.oustme.oustsdk.room.EntityResourseStrings;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.ResourceCollectionModel;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by oust on 4/30/19.
 */

public class DownloadInitService {
    private static DownloadInitListener downloadInitListener;
    private Context context;
    private String orgId="";

    /*private float totalResources = 0;
    private int totalImageResourceSize = 0;
    private int totalAudioResourceSize = 0;
    private int totalFontResourceSize = 0;
    private int totalDownloadedResources = 0;
    private int downloadStartedResources = 0;
    private int totalResourceTobeDownloaded = 0;
    private float resourcesDownloaded = 0;*/

    private float totalResources;
    private int totalImageResourceSize;
    private int totalAudioResourceSize;
    private int totalFontResourceSize;
    private int totalDownloadedResources;
    private int downloadStartedResources;
    private int totalResourceTobeDownloaded;
    private float resourcesDownloaded;
    private float resourcesToBeDownloaded;

    private int RES_AUDIO = 1, RES_IMAGE = 2, RES_FONT = 3;

    public static void setListener(DownloadInitListener downloadInitListener1){
        Log.d("DownloadListener", "setListener: ");
        downloadInitListener = downloadInitListener1;
    }

    public DownloadInitService(Activity activity, String orgId, DownloadInitListener downloadInitListener) {
        DownloadInitService.downloadInitListener = downloadInitListener;
        this.context = activity;
        this.orgId = orgId;
        totalResources = 0;
        totalImageResourceSize = 0;
        totalAudioResourceSize = 0;
        totalFontResourceSize = 0;
        totalDownloadedResources = 0;
        downloadStartedResources = 0;
        totalResourceTobeDownloaded = 0;
        resourcesDownloaded = 0;
        resourcesToBeDownloaded = 0;

        OustStaticVariableHandling.getInstance().setDownloadingResources(true);
        downloadAllResourses();
    }

    public DownloadInitService(Context context, String orgId, DownloadInitListener downloadInitListener) {
        DownloadInitService.downloadInitListener = downloadInitListener;
        this.context = context;
        this.orgId = orgId;

        totalResources = 0;
        totalImageResourceSize = 0;
        totalAudioResourceSize = 0;
        totalFontResourceSize = 0;
        totalDownloadedResources = 0;
        downloadStartedResources = 0;
        totalResourceTobeDownloaded = 0;
        resourcesDownloaded = 0;
        resourcesToBeDownloaded = 0;

        OustStaticVariableHandling.getInstance().setDownloadingResources(true);
        downloadAllResourses();
    }

    private void downloadAllResourses() {
        if (RoomHelper.getResourceClassCount() == 0) {
            addResourcesToRealm();
        }
        if (!OustPreferences.getAppInstallVariable("allresourcesDownloadeda")) {
            startDownloadingResourses();
        } else {
            OustStaticVariableHandling.getInstance().setDownloadingResources(false);
            downloadInitListener.onDownloadComplete();
        }
    }

    private void startDownloadingResourses() {
        OustPreferences.saveAppInstallVariable("isDownloadedImageLastTime", true);
        startDownloadLanguage();
        //calculateResourceSize();
        //totalResources += 1;

    }

    private void startDownloadLanguage(){
        /*String imagePath1 = "appImages/splash/org/" + (orgId.toUpperCase()) + "/android/bgImage";
        downloadImage(imagePath1, "splashScreen");
        String imagePath2 = "appImages/splash/org/" + (orgId.toUpperCase()) + "/android/icon";
        downloadImage(imagePath2, "splashIcon");*/

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

    private void addResourcesToRealm() {
        EntityResourceCollection audioCollModel = new EntityResourceCollection();
        audioCollModel.setId(1);
        audioCollModel.setTimeStamp(0);
        RoomHelper.addorUpdateResourceCollectionModel(audioCollModel);

        EntityResourceCollection imageCollModel = new EntityResourceCollection();
        imageCollModel.setId(2);
        imageCollModel.setTimeStamp(0);
        RoomHelper.addorUpdateResourceCollectionModel(imageCollModel);

        EntityResourceCollection fontCollModel = new EntityResourceCollection();
        audioCollModel.setId(3);
        audioCollModel.setTimeStamp(0);
        RoomHelper.addorUpdateResourceCollectionModel(fontCollModel);
    }
    private void calculateResourceSize() {
        totalResources = 0;
        totalImageResourceSize = (context.getResources().getStringArray(R.array.all_images)).length;
        totalResourceTobeDownloaded += totalImageResourceSize;
        totalResources += totalImageResourceSize;

        totalAudioResourceSize = (context.getResources().getStringArray(R.array.sounds)).length;
        totalResourceTobeDownloaded += totalAudioResourceSize;
        totalResources += totalAudioResourceSize;

        totalFontResourceSize = (context.getResources().getStringArray(R.array.fonts)).length;
        totalResourceTobeDownloaded += totalFontResourceSize;
        totalResources += totalFontResourceSize;

        //downloadStrategyOfResource();
        newDownloadResource();
    }

    private void newDownloadResource(){
        try{
            String[] audioResList = context.getResources().getStringArray(R.array.sounds);
            String[] imageResList = context.getResources().getStringArray(R.array.all_images);
            String[] fontResList = context.getResources().getStringArray(R.array.fonts);
            for(int i=0;i<imageResList.length;i++){
                loadInitResFromResources(imageResList[i]);
            }
            for(int i=0;i<audioResList.length;i++){
                loadInitResFromResources(audioResList[i]);
            }
            for(int i=0;i<fontResList.length;i++){
                loadInitResFromResources(fontResList[i]);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*private void downloadStrategyOfResource() {
        try {
            //Log.d("downloadStrategyOfRes", "DStartedRes:"+downloadStartedResources+" -- totalResDownloaded:"+totalResourceTobeDownloaded);
            if (downloadStartedResources < totalResourceTobeDownloaded) {
                String[] audioResList = context.getResources().getStringArray(R.array.sounds);
                String[] imageResList = context.getResources().getStringArray(R.array.all_images);
                String[] fontResList = context.getResources().getStringArray(R.array.fonts);

                totalDownloadedResources = downloadStartedResources;
                downloadStartedResources += 10;
                resourcesToBeDownloaded +=10;
                if (downloadStartedResources > totalResourceTobeDownloaded) {
                    downloadStartedResources = totalResourceTobeDownloaded;
                }
                for (int i = totalDownloadedResources; i < downloadStartedResources; i++) {
                    if (i < totalImageResourceSize) {
                        loadInitResFromResources(imageResList[i]);
                        //downLoadAudioImagesResource(RES_IMAGE, imageResList[i]);

                    } else if (i >= totalImageResourceSize && i < totalImageResourceSize + totalAudioResourceSize) {
                        loadInitResFromResources(audioResList[i - totalImageResourceSize]);
                        //downLoadAudioImagesResource(RES_AUDIO, audioResList[i - totalImageResourceSize]);

                    } else if (i >= totalImageResourceSize + totalAudioResourceSize && i < totalResourceTobeDownloaded) {
                        loadInitResFromResources(fontResList[i - (totalImageResourceSize + totalAudioResourceSize)]);
                        //downLoadFontResources(RES_FONT, fontResList[i - (totalImageResourceSize + totalAudioResourceSize)]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    private void loadInitResFromResources(String filename){
        try {
            final File file = new File(context.getFilesDir(), filename);
            String rawFilename = filename.split("\\.")[0];
            if ((file != null) && (!file.exists())) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    //com.oustme.oustsdk
                    int resId = context.getResources().getIdentifier("raw/"+rawFilename, null, context.getPackageName());
                    Log.d("InitRes", "loadInitResFromResources: "+rawFilename+" -- resid:"+resId);
                    in =  context.getResources().openRawResource(resId);
                    out = new FileOutputStream(file);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch(Exception e) {
                    Log.e("InitRes", "Failed to copy asset file: " + filename, e);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        showDownloadProgress();
        Log.e("InitRes", "copied raw file: " + filename);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        try {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void downLoad(final LanguageClass languageClass, String fileName1, final int languageIndex) {
        try {
            if (!OustSdkTools.checkInternetStatus(context)) {
                calculateResourceSize();
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
                        //downloadResurceFailed();
                        calculateResourceSize();
                        ex.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            //downloadResurceFailed();
            calculateResourceSize();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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
            //showDownloadProgress();

        } catch (Exception e) {
            downloadInitListener.onDownloadFailed();
        }
        calculateResourceSize();
    }

    /*private void downloadResurceFailed() {
        OustStaticVariableHandling.getInstance().setDownloadingResources(false);
        if(downloadInitListener!=null){
            downloadInitListener.onDownloadFailed();
        }
    }*/

    public File getTempFile() {
        File file;
        try {
            String fileName = "myname.properties";
            file = File.createTempFile(fileName, null, context.getCacheDir());
            return file;
        } catch (Exception e) {
            downloadInitListener.onDownloadFailed();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return null;
    }

    public void showDownloadProgress() {
        resourcesDownloaded++;
        if (totalResources > 0) {
            float progress = resourcesDownloaded / totalResources;
            int progress1 = (int) (progress * 100);
            if (progress1 > 100) {
                progress1 = 100;
            }
            Log.d("DownloadListener","progress:"+progress+" -- resorcesDownloaded:"+resourcesDownloaded+" -- totalResource:"+totalResources);
            downloadInitListener.onProgressChanged(progress1);
            if (resourcesDownloaded >= (totalResources)) {
                OustPreferences.saveAppInstallVariable("allresourcesDownloadeda", true);
                OustStaticVariableHandling.getInstance().setDownloadingResources(false);
                downloadInitListener.onDownloadComplete();
            } /*else {
                if(resourcesDownloaded>=resourcesToBeDownloaded){
                    downloadStrategyOfResource();
                }
            }*/
        } else {
            OustStaticVariableHandling.getInstance().setDownloadingResources(false);
            downloadInitListener.onDownloadComplete();
        }
    }

}
