package com.oustme.oustsdk.tools;

import android.util.Log;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.ResourceNotifier;
import com.oustme.oustsdk.response.common.LanguageClass;
import com.oustme.oustsdk.response.common.LanguagesClasses;
import com.oustme.oustsdk.room.EntityResourceCollection;
import com.oustme.oustsdk.room.EntityResourseStrings;
import com.oustme.oustsdk.room.RoomHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by oust on 9/8/17.
 */

public class ResourceUpdateNotifierClass {
    private ResourceNotifier resourceNotifier;
    private List<LanguageClass> updateLanguageClasses;
    public void setResourceNotifier(ResourceNotifier resourceNotifier){
        this.resourceNotifier=resourceNotifier;
    }
    public void setupdateLanguageClasses(List<LanguageClass> updateLanguageClasses){
        this.updateLanguageClasses=updateLanguageClasses;
    }
    public  void getUpdateResoursesData(){
        String msg="system/appResourses/android";
        ValueEventListener languageListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    if(snapshot!=null) {
                        LanguagesClasses classes = new LanguagesClasses();
                        Map<String, Object> mainMap = (Map<String, Object>) snapshot.getValue();
                        if(mainMap.get("languages")!=null) {
                            List<LanguageClass> list = new ArrayList<>();
                            HashMap<String, Object> languageData = (HashMap<String, Object>) mainMap.get("languages");
                            classes.setLastTimestamp((long) languageData.get("updateTS"));
                            for (String langKey : languageData.keySet()) {
                                if (!langKey.equalsIgnoreCase("updateTS")) {
                                    Map<String, Object> languageMap1 = (Map<String, Object>) languageData.get(langKey);
                                    //Map<String, Object> languageSubMap = (Map<String, Object>) languageMap1.get("mobile");
                                    LanguageClass languageClass = new LanguageClass();
                                    languageClass.setLanguagePerfix(langKey);
                                    languageClass.setName((String) languageMap1.get("displayName"));
                                    languageClass.setIndex((int) ((long) languageMap1.get("index")));
                                    languageClass.setFileName((String) languageMap1.get("keyName"));
                                    if ((languageMap1.get("languageUpdatedTS")) != null) {
                                        Object o1 = languageMap1.get("languageUpdatedTS");
                                        if (o1.getClass().equals(String.class)) {
                                            long n1 = Long.parseLong((String) o1);
                                            languageClass.setTimeStamp(n1);
                                        } else if (o1.getClass().equals(Long.class)) {
                                            long n1 = ((long) o1);
                                            languageClass.setTimeStamp(n1);
                                        }
                                    }
                                    list.add(languageClass);
                                }
                            }
                            classes.setLanguageClasses(list);
                            checkForLannguageUpdate(classes);
                        } else {
                            classes = new LanguagesClasses();
                            LanguageClass languageClass = new LanguageClass();
                            languageClass.setIndex(1);
                            languageClass.setName("English");
                            languageClass.setLanguagePerfix("en");
                            languageClass.setFileName("englishStr.properties");
                            List<LanguageClass> languageClasses = new ArrayList<>();
                            languageClasses.add(languageClass);
                            classes.setLanguageClasses(languageClasses);
                            Gson gson = new Gson();
                            String langStr = gson.toJson(classes);
                            OustPreferences.save("alllanguage", langStr);
                        }
                        if(mainMap.get("audioData")!=null){
                            HashMap<String, Object> audioData = (HashMap<String, Object>) mainMap.get("audioData");
                            Log.i("tag", audioData.toString());
                            long updateTime = (long) audioData.get("updateTime");
                            HashMap<String, String> soundCollection = (HashMap<String, String>) audioData.get("data");
                            EntityResourceCollection resCollectData = RoomHelper.getResourceCollectionModel(1);
                            if (resCollectData.getTimeStamp() < updateTime) {
                                RoomHelper.updateResourceCollectionModel(resCollectData, soundCollection.toString(), updateTime);
                                OustPreferences.saveAppInstallVariable("isAudioUpdated", true);
                            }
                        }
                        if(mainMap.get("imageData")!=null){
                            HashMap<String, Object> audioData = (HashMap<String, Object>) mainMap.get("imageData");
                            Log.i("tag", audioData.toString());
                            long updateTime = (long) audioData.get("updateTime");
                            HashMap<String, String> soundCollection = (HashMap<String, String>) audioData.get("data");
                            EntityResourceCollection resCollectData = RoomHelper.getResourceCollectionModel(2);
                            if (resCollectData.getTimeStamp() < updateTime) {
                                RoomHelper.updateResourceCollectionModel(resCollectData, soundCollection.toString(), updateTime);
                                OustPreferences.saveAppInstallVariable("isImageUpdated", true);
                            }
                        }
                        if(mainMap.get("fontData")!=null){
                            HashMap<String, Object> audioData = (HashMap<String, Object>) mainMap.get("fontData");
                            Log.i("tag", audioData.toString());
                            long updateTime = (long) audioData.get("updateTime");
                            HashMap<String, String> soundCollection = (HashMap<String, String>) audioData.get("data");
                            EntityResourceCollection resCollectData = RoomHelper.getResourceCollectionModel(3);
                            if (resCollectData.getTimeStamp() < updateTime) {
                                RoomHelper.updateResourceCollectionModel(resCollectData, soundCollection.toString(), updateTime);
                                OustPreferences.saveAppInstallVariable("isfontUpdated", true);
                            }
                        }
                        if(mainMap.get("splashData")!=null){
                            Map<String,Object> splashDataMap=(Map<String,Object>)mainMap.get("splashData");
                            long updateChecksum=(long) splashDataMap.get("updateChecksum");
                            if(updateChecksum>0){
                                String s1=OustPreferences.get("lastSpalshUpdateTime");
                                Log.e("time_update","updateTime"+s1);
                                if ((OustPreferences.get("lastSpalshUpdateTime")==null)||
                                        (((OustPreferences.get("lastSpalshUpdateTime")!=null)&&((OustPreferences.get("lastSpalshUpdateTime").isEmpty()))))||
                                        ((OustPreferences.get("lastSpalshUpdateTime")!=null)&&(!(""+updateChecksum).equalsIgnoreCase(OustPreferences.get("lastSpalshUpdateTime"))))) {
                                    OustPreferences.save("lastSpalshUpdateTime",(""+updateChecksum));
                                    if(OustPreferences.getAppInstallVariable("isDownloadedImageLastTime")){
                                        OustPreferences.clear("isDownloadedImageLastTime");
                                    }else {
                                        OustPreferences.saveAppInstallVariable("isSplashUpdate",true);
                                    }
                                }
                            }
                        }
                    }
                }catch (Exception e){

                }
                resourceNotifier.resourceUpdateOver();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        };
        OustFirebaseTools.getRootRef().child(msg).keepSynced(true);
        OustFirebaseTools.getRootRef().child(msg).addValueEventListener(languageListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(languageListener, msg));


        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    boolean connected = dataSnapshot.getValue(Boolean.class);
                    if (!connected) {
                        resourceNotifier.resourceUpdateOver();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                resourceNotifier.resourceUpdateOver();
            }
        };
        OustFirebaseTools.getRootRef().child(".info/connected");
        OustFirebaseTools.getRootRef().child(".info/connected").addListenerForSingleValueEvent(listener);
    }

    private void checkForLannguageUpdate(LanguagesClasses classes){
        try {
            String s1 = OustPreferences.get("alllanguage");
            List<LanguageClass> allUpdateClasses = new ArrayList<>();
            Gson gson = new Gson();
            if((s1!=null)&&(!s1.isEmpty())) {
                LanguagesClasses languagesClasses = gson.fromJson(s1, LanguagesClasses.class);
                if((languagesClasses!=null)&&(languagesClasses.getLastTimestamp()>0)) {
                    if (languagesClasses.getLastTimestamp() != classes.getLastTimestamp()) {
                        for (int i = 0; i < languagesClasses.getLanguageClasses().size(); i++) {
                            if (languagesClasses.getLanguageClasses().get(i).getTimeStamp() != classes.getLanguageClasses().get(i).getTimeStamp()) {
                                allUpdateClasses.add(classes.getLanguageClasses().get(i));
                            }
                        }
                        String langStr = gson.toJson(classes);
                        OustPreferences.save("alllanguage", langStr);
                    }
                }else {
                    String langStr = gson.toJson(classes);
                    OustPreferences.save("alllanguage", langStr);
                }
            }else {
                String langStr = gson.toJson(classes);
                OustPreferences.save("alllanguage", langStr);
            }

            String s2 = OustPreferences.get("updatelanguage");
            LanguagesClasses updateLanguages = new LanguagesClasses();
            if((s2!=null)&&(!s2.isEmpty())) {
                updateLanguages = gson.fromJson(s2, LanguagesClasses.class);
                if(updateLanguages==null){
                    updateLanguages.setLanguageClasses(new ArrayList<LanguageClass>());
                }else if(updateLanguages.getLanguageClasses()==null){
                    updateLanguages.setLanguageClasses(new ArrayList<LanguageClass>());
                }
            }else {
                updateLanguages.setLanguageClasses(new ArrayList<LanguageClass>());
            }
            for (int i = 0; i < allUpdateClasses.size(); i++) {
                EntityResourseStrings resourseStringsModel= RoomHelper.getResourceStringModel(allUpdateClasses.get(i).getLanguagePerfix());
                if((resourseStringsModel!=null)&&(resourseStringsModel.getHashmapStr()!=null)){
                    updateLanguages.getLanguageClasses().add(allUpdateClasses.get(i));
                }
            }
            if (updateLanguages.getLanguageClasses().size() > 0) {
                updateLanguageClasses.addAll(updateLanguages.getLanguageClasses());
                String langStr1 = gson.toJson(updateLanguages);
                OustPreferences.save("updatelanguage", langStr1);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


}
