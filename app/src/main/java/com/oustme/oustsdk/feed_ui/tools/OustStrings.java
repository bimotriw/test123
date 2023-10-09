package com.oustme.oustsdk.feed_ui.tools;


import java.util.Map;

public class OustStrings {

    private static Map<String,String> strMap;

    /*public static void init(){
        try{
            String languageStr = Locale.getDefault().getLanguage();
            ResourseStringsModel resourseStringsModel=new ResourseStringsModel();
            if (languageStr.equalsIgnoreCase("en")) {
                resourseStringsModel= RealmHelper.getResourceStringModel("en");
            }
            if ((resourseStringsModel != null) && (resourseStringsModel.getHashmapStr() != null)&&(!resourseStringsModel.getHashmapStr().isEmpty())) {
                strMap=new Gson().fromJson(resourseStringsModel.getHashmapStr(), new TypeToken<HashMap<String, String>>(){}.getType());
            }

        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static String getString(String key){
        String returnStr="";
        try {
            if (strMap != null) {
                returnStr=strMap.get(key);
                if ((returnStr != null)&&(!returnStr.isEmpty())) {
                    if (returnStr.contains("\\n")) {
                        returnStr = returnStr.replace("\\n", "\n");
                    }
                }
            }
            if ((returnStr == null) || (returnStr.isEmpty())) {
                int i = OustSdkApplication.getContext().getResources().getIdentifier(key, "string", OustSdkApplication.getContext().getPackageName());
                returnStr = OustSdkApplication.getContext().getResources().getString(i);
            }
        }catch (Exception e){
            returnStr="";
        }
        return returnStr;
    }*/
}
