package com.oustme.oustsdk.tools;

import android.os.Environment;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by shilpysamaddar on 08/03/17.
 */

public class OustCommonUtils {
    private static final String TAG = "OustAndroid:" + OustCommonUtils.class.getName();

    static public String getMD5EncodedString(String encryptString) throws NoSuchAlgorithmException {
        String encodedString = "";
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(encryptString.getBytes());

        byte[] byteData = md.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }


        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            String hex = Integer.toHexString(0xff & byteData[i]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        encodedString = hexString.toString();
        return encodedString;
    }

    public static String secondsToString(int pTime) {
        final int min = pTime / 60;
        final int sec = pTime - (min * 60);

        final String strMin = placeZeroIfNeeded(min);
        final String strSec = placeZeroIfNeeded(sec);
        return String.format("%s:%s", strMin, strSec);
    }

    static private String placeZeroIfNeeded(int number) {
        return (number >= 10) ? Integer.toString(number) : String.format("0%s", number);
    }
    public static String getFontsPath(){
        String path =Environment.getExternalStorageDirectory().getPath()+ "/Oust/Fonts";
        return path;
    }
    public static String getImagesPath(){
        String path =Environment.getExternalStorageDirectory().getPath()+ "/Oust/Images";
        return path;
    }
    public static File getAudioFile(String filename) {
        File tempMp3=null;
//        DTOResourceData resourceDataModel = RealmHelper.getResourceDataModel(filename);
//        if (resourceDataModel != null) {
//            String audStr = resourceDataModel.getFile();
//            if ((audStr != null) && (!audStr.isEmpty())) {
//                byte[] audBytes = Base64.decode(audStr, 0);
//                try {
//                    tempMp3 = File.createTempFile(filename, null);
//                    FileOutputStream fos = new FileOutputStream(tempMp3);
//                    fos.write(audBytes);
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        try{
            tempMp3=new File(OustSdkApplication.getContext().getFilesDir(),filename);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return tempMp3;
    }
}
