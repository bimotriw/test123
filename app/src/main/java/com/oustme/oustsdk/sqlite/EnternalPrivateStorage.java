package com.oustme.oustsdk.sqlite;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import android.webkit.MimeTypeMap;

import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by shilpysamaddar on 10/03/17.
 */

public class EnternalPrivateStorage {
    private static final String TAG = "EnternalPrivateStorage";

    public void saveFile(String fileNameStr,String tobeSaved){
        boolean isAvailable=false;
        try {
//            String previousSavedData=readSavedData(fileNameStr);
//            if((previousSavedData!=null)&&(previousSavedData.length()>50)){
//
//            }
            isAvailable=isFileAvialable(fileNameStr);
        }catch (Exception e){}
        try {
            if (!isAvailable) {
                // File file=new File(System.getProperty("user.dir"),(fileNameStr));
                FileOutputStream fos = OustSdkApplication.getContext().openFileOutput((fileNameStr), Context.MODE_PRIVATE);
                byte[] contentInBytes = tobeSaved.getBytes();
                fos.write(contentInBytes);
                fos.close();
            }
        }catch (Exception e){}
    }

    public void saveResourseFile(String fileNameStr,String tobeSaved){
        boolean isAvailable=false;
        try {
            isAvailable=isFileAvialable(fileNameStr);
            if(isAvailable){
                deleteFile(fileNameStr);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
                // File file=new File(System.getProperty("user.dir"),(fileNameStr));
                FileOutputStream fos = OustSdkApplication.getContext().openFileOutput((fileNameStr), Context.MODE_PRIVATE);
                byte[] contentInBytes = tobeSaved.getBytes();
                fos.write(contentInBytes);
                fos.close();
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public String readSavedData (String fileNameStr) {
        StringBuffer datax = new StringBuffer();
        try {
            FileInputStream fin = OustSdkApplication.getContext().openFileInput(fileNameStr);
            if(fin!=null) {
                InputStreamReader isr = new InputStreamReader(fin);
                BufferedReader buffreader = new BufferedReader(isr);
                String readString = buffreader.readLine();
                while (readString != null) {
                    datax.append(readString);
                    readString = buffreader.readLine();
                }
                isr.close();
            }
        } catch ( Exception ioe ) {
            ioe.printStackTrace ( ) ;
        }
        return datax.toString() ;
    }
    public FileDescriptor getSavedDataPath (String fileNameStr) {
        FileDescriptor filePath=new FileDescriptor();
        try {
            FileInputStream fin = OustSdkApplication.getContext().openFileInput(fileNameStr);
            filePath=fin.getFD();
        } catch ( Exception ioe ) {
            ioe.printStackTrace ( ) ;
        }
        return filePath;
    }
    public boolean isFileAvialable (String fileNameStr) {
        StringBuffer datax = new StringBuffer();
        try {
            FileInputStream fin = OustSdkApplication.getContext().openFileInput(fileNameStr);
            InputStreamReader isr = new InputStreamReader (fin) ;
            BufferedReader buffreader = new BufferedReader (isr) ;
            String readString = buffreader.readLine ( ) ;
            if (readString != null ) {
                datax.append(readString);
            }
            isr.close ( ) ;
        } catch ( Exception ioe ) {
            ioe.printStackTrace ( ) ;
        }
        String s1=datax.toString();
        return (s1 != null) && (!s1.isEmpty());
    }

    public boolean isFileAvialableInInternalStorage (String fileName) {
        String path = OustSdkApplication.getContext().getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file.toString(), options);
        //checkIsImage(OustSdkApplication.getContext(), Uri.fromFile(file));
        String mimeType = getMimeType(Uri.fromFile(file).toString());
        if (file.exists()  && mimeType!=null  && (mimeType.contains("audio/mpeg") || mimeType.contains(".mp3") || mimeType.contains(".mp4")) )
        {
            return true;
        }
        else if (file.exists() && (options.outWidth != -1 && options.outHeight != -1))
         {
                return true;
         }
         else
             {
                if (file.exists()) {
                    file.delete();
                }
                return false;
            }

       // return file.exists();
    }

    public static boolean checkIsImage(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String type = contentResolver.getType(uri);
        Log.d(TAG, "checkIsImage file type: "+type);
        getMimeType(uri.toString());
        if (type != null) {
            return type.startsWith("image/");
        }
        else {
            return false;
        }
    }

    private static String getMimeType(String fileUrl) {
        if(fileUrl!=null) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(fileUrl);
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        else {
            return null;
        }
    }


    public void deleteFile(String fileNameStr){
        try{
            FileOutputStream fos = OustSdkApplication.getContext().openFileOutput((fileNameStr), Context.MODE_PRIVATE);
            File file=new File(System.getProperty("user.dir"),(fileNameStr));
            fos.close();
            file=new File(fileNameStr);

            if (!file.delete()) {
                Log.d("----","fileDeleted_failed"+"---"+fileNameStr);
            }else {
                Log.d("----","fileDeleted"+"---"+fileNameStr);
            }
        }catch (Exception e){
            Log.d("----","exception"+"---"+fileNameStr);
        }
    }

    public void deleteMediaFile(String fileNameStr){
        try{
            //FileOutputStream fos = OustSdkApplication.getContext().openFileOutput((fileNameStr), Context.MODE_PRIVATE);
            File file=new File(OustSdkApplication.getContext().getFilesDir(),(fileNameStr));
            if(file!=null && file.exists()) {
                if (!file.delete()) {
                    Log.d("----", "fileDeleted_failed" + "---" + fileNameStr);
                } else {
                    Log.d("----", "fileDeleted" + "---" + fileNameStr);
                }
            }
        }catch (Exception e){
            Log.d("----","exception"+"---"+fileNameStr);
        }
    }


    public void  createResourceFile(String imagename,int type,File file) {
        File folder = new File(OustSdkApplication.getContext().getFilesDir() + "/OustRes/");
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                Log.e("ERROR", "Cannot create a directory!");
            } else {
                folder.mkdir();
            }
        }
        File imgFolder=null;
        if(type==1) {
            imgFolder = new File(OustSdkApplication.getContext().getFilesDir() + "/OustRes/" + "Audio/");
            if (folder.exists()) {
                if (!imgFolder.exists()) {
                    if (!imgFolder.mkdir()) {
                        Log.e("ERROR", "Cannot create a directory!");
                    } else {
                        imgFolder.mkdir();
                    }
                }
            }
        }else if(type==2){
            imgFolder = new File(OustSdkApplication.getContext().getFilesDir() + "/OustRes/" + "Images/");
            if (folder.exists()) {
                if (!imgFolder.exists()) {
                    if (!imgFolder.mkdir()) {
                        Log.e("ERROR", "Cannot create a directory!");
                    } else {
                        imgFolder.mkdir();
                    }
                }
            }
        }else if(type==3){
            imgFolder = new File(OustSdkApplication.getContext().getFilesDir()+ "/OustRes/" + "Fonts/");
            if (folder.exists()) {
                if (!imgFolder.exists()) {
                    if (!imgFolder.mkdir()) {
                        Log.e("ERROR", "Cannot create a directory!");
                    } else {
                        imgFolder.mkdir();
                    }
                }
            }
        }

        if (folder.exists()) {
            File noMediafile = new File(folder, ".nomedia");
            if (!noMediafile.exists()) {
                try {
                    noMediafile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        }

        File fileName = new File(imgFolder, imagename);
        Log.v("FileName", "" + fileName);
        if (!fileName.exists()) {
            try {
                fileName.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }else{
        }
    }


}
