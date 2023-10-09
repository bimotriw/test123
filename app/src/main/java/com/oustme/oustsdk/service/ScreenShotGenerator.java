package com.oustme.oustsdk.service;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.oustme.oustsdk.interfaces.common.BitmapCreateListener;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.ShowPopup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by oust on 2/12/19.
 */

public class ScreenShotGenerator extends AsyncTask<String,String,String>{

    private View view;
    private BitmapCreateListener bitmapCreateListener;

    public ScreenShotGenerator(View view , BitmapCreateListener bitmapCreateListener) {
        this.view = view;
        this.bitmapCreateListener=bitmapCreateListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        bitmapCreateListener.onBitmapSaved(s);
    }

    @Override
    protected String doInBackground(String... strings) {
        String filePath=null;
        try {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                    view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            String path = Environment.getExternalStorageDirectory().toString();
            OutputStream fOut = null;
            String tenant=OustPreferences.get("tanentid");
            if(tenant==null || tenant.isEmpty()){
                tenant="Oust";
            }
            File file = new File(path, "MY"+tenant.toUpperCase()+"PAYOUT" + ".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
            try {
                fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush(); // Not really required
                fOut.close();
                filePath=file.getAbsolutePath();
                MediaStore.Images.Media.insertImage(OustSdkApplication.getContext().getContentResolver(), filePath, file.getName(), file.getName());
            } catch (FileNotFoundException e) {
                bitmapCreateListener.onNoBitmapFound();
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            } catch (IOException e) {
                bitmapCreateListener.onNoBitmapFound();
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        }catch (Exception e){
            bitmapCreateListener.onNoBitmapFound();
        }

        return filePath;
    }
}
