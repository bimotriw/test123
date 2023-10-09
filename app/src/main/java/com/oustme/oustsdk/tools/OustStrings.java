package com.oustme.oustsdk.tools;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oustme.oustsdk.response.common.LanguageClass;
import com.oustme.oustsdk.response.common.LanguagesClasses;
import com.oustme.oustsdk.room.EntityResourseStrings;
import com.oustme.oustsdk.room.RoomHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by shilpysamaddar on 07/03/17.
 */

public class OustStrings {
    public static Map<String, String> strMap;

    public static void init() {
        try {
            String laungeStr = Locale.getDefault().getLanguage();
            String s1 = OustPreferences.get("alllanguage");
            Gson gson = new Gson();
            EntityResourseStrings resourseStringsModel = new EntityResourseStrings();
            if ((s1 != null)) {
                LanguagesClasses classes = gson.fromJson(s1, LanguagesClasses.class);
                List<LanguageClass> languageClasses = classes.getLanguageClasses();
                LanguageClass currentSelectedLanguage = new LanguageClass();
                for (int i = 0; i < languageClasses.size(); i++) {
                    if (laungeStr.equalsIgnoreCase(languageClasses.get(i).getLanguagePerfix())) {
                        currentSelectedLanguage = languageClasses.get(i);
                    }
                }
                if (laungeStr.equalsIgnoreCase(currentSelectedLanguage.getLanguagePerfix())) {
                    resourseStringsModel = RoomHelper.getResourceStringModel(currentSelectedLanguage.getLanguagePerfix());
                }
            } else {
                if (laungeStr.equalsIgnoreCase("en")) {
                    resourseStringsModel = RoomHelper.getResourceStringModel("en");
                }
            }
            if ((resourseStringsModel != null) && (resourseStringsModel.getHashmapStr() != null) && (!resourseStringsModel.getHashmapStr().isEmpty())) {
                strMap = new Gson().fromJson(resourseStringsModel.getHashmapStr(), new TypeToken<HashMap<String, String>>() {
                }.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static String getString(String key) {
        String returnStr = "";
        try {
            if (strMap != null) {
                returnStr = strMap.get(key);
                if ((returnStr != null) && (!returnStr.isEmpty())) {
                    if (returnStr.contains("\\n")) {
                        returnStr = returnStr.replace("\\n", "\n");
                    }
                }
            }
            if ((returnStr == null) || (returnStr.isEmpty())) {
                int i = OustSdkApplication.getContext().getResources().getIdentifier(key, "string", OustSdkApplication.getContext().getPackageName());
                returnStr = OustSdkApplication.getContext().getResources().getString(i);
            }
        } catch (Exception e) {
            returnStr = "";
        }
        return returnStr;
    }

    public static String getMainString(String key) {
        String returnStr = "";
        try {
            int i = OustSdkApplication.getContext().getResources().getIdentifier(key, "string", "com.oustme.oustapp");
            returnStr = OustSdkApplication.getContext().getResources().getString(i);
            if (returnStr == null) {
                returnStr = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return returnStr;
    }

    public static List<String> getStrList(String key) {
        List<String> strList = new ArrayList<>();
        String str;
        try {
            if (strMap != null) {
                str = strMap.get(key);
                if ((str != null) && (!str.isEmpty())) {
                    str = str.replace("[", "");
                    str = str.replace("]", "");
                    strList = new ArrayList<>(Arrays.asList(str.split(",")));
                }
            }
            if ((strList.size() == 0)) {
                int i = OustSdkApplication.getContext().getResources().getIdentifier(key, "array", "com.oustme.oustapp");
                strList = Arrays.asList(OustSdkApplication.getContext().getResources().getStringArray(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return strList;
    }
}
