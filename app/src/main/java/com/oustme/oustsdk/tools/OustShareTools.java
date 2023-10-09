package com.oustme.oustsdk.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.RelativeLayout;

import com.oustme.oustsdk.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.BranchShortLinkBuilder;

public class OustShareTools {


     public static void shareUsingIntent(Activity activity, String branchLink) {
         String oustShareMessage= OustStrings.getString("cpmmonsharetext");
          Intent shareIntent = new Intent(Intent.ACTION_SEND);
          shareIntent.putExtra(Intent.EXTRA_TEXT, oustShareMessage + "\n" + branchLink);
          shareIntent.setType("text/plain");
          shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
          activity.startActivity(Intent.createChooser(shareIntent, "Share via"));

     }

    public static void shareScreenUsingIntent(Activity activity, String branchLink, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "Title", null);
        Uri imageUri = Uri.parse(path);

        String oustShareMessage= OustStrings.getString("cpmmonsharetext");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, branchLink);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, oustShareMessage );
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/jpeg,text/plain");

        activity.startActivity(Intent.createChooser(shareIntent, "Share via"));

    }




    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    public static String FACEBOOK_URL = "https://www.facebook.com/oustlabs";
    public static String FACEBOOK_PAGE_ID = "oustlabs";

    //method to get the right URL to use in the intent
    public static String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    public static void shareScreenAndBranchIo(final Activity activity, RelativeLayout layout, String msg, String assessmentId){
        if(ContextCompat.checkSelfPermission(OustSdkApplication.getContext(), "android.permission.WRITE_EXTERNAL_STORAGE")!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 123);
        }else {
            if (msg == null) {
                msg = OustStrings.getString("cpmmonsharetext");
            }
                final View resultView = layout;
                final String msg1=msg;
                String uniqueId = UUID.randomUUID().toString();

                BranchShortLinkBuilder shortUrlBuilder= OustBranchTools.getShortUrlBuilder(activity, "Tag1", "Tag2", "Facebook", "Share",
                        "", "", "", "",
                        "", "", "", "", "", "", uniqueId, "OustMe",
                        "Study Smarter Rank Higher", "http://oustme.com/images/home/logo.png", assessmentId);
                // WHAT'S APP
                try {
                    shortUrlBuilder.generateShortUrl(new Branch.BranchLinkCreateListener() {
                        @Override
                        public void onLinkCreate(String url, BranchError error) {
                            if (error != null) {

                            } else {
                                OustShareTools.share(activity, OustSdkTools.getInstance().getScreenShot(resultView),msg1 + " " + url);
                            }
                        }
                    });
                } catch (Exception ex) {
                    OustSdkTools.showToast(ex.getMessage());
                }
            }
    }

    public static void share(Context cx, Bitmap bitmap, String msg){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(cx.getContentResolver(), bitmap, "Title", null);
        Uri imageUri = Uri.parse(path);

        String oustShareMessage= OustStrings.getString("cpmmonsharetext");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if(!msg.equalsIgnoreCase(" "))
        shareIntent.putExtra(Intent.EXTRA_TEXT, msg);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, oustShareMessage );
        //shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("image/jpeg,text/plain");
        cx.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public static Uri getSharedUri(Context cx, Bitmap bitmap, String msg){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String path = MediaStore.Images.Media.insertImage(cx.getContentResolver(), bitmap, "Title", null);
        Uri imageUri = Uri.parse(path);
        return imageUri;
    }

    public static void shareScreenOnWhatsApp(Activity activity,RelativeLayout layout,String msg,String assessmentId){
        try {
            if(ContextCompat.checkSelfPermission(OustSdkApplication.getContext(), "android.permission.WRITE_EXTERNAL_STORAGE")!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 123);
            }else {
                if (msg == null) {
                    msg = OustStrings.getString("cpmmonsharetext");
                }
                boolean isAppInstalled = OustSdkTools.getInstance().isAppInstalled(activity.getResources().getString(R.string.whatsapp_Package));
                if (isAppInstalled) {
                    final View resultView = layout;
                    final String msg1=msg;
                    String uniqueId = UUID.randomUUID().toString();
                    BranchShortLinkBuilder shortUrlBuilder= OustBranchTools.getShortUrlBuilder(activity, "Tag1", "Tag2", "Facebook", "Share",
                            "", "", "", "",
                            "", "", "", "", "", "", uniqueId, "OustMe",
                            "Study Smarter Rank Higher", "http://oustme.com/images/home/logo.png", assessmentId);
                    // WHAT'S APP
                    try {
                        shortUrlBuilder.generateShortUrl(new Branch.BranchLinkCreateListener() {
                            @Override
                            public void onLinkCreate(String url, BranchError error) {
                                if (error != null) {
                                } else {
                                    OustShareTools.shareEventOnWhatsApp(OustSdkTools.getInstance().getScreenShot(resultView), msg1 + " " + url);
                                }
                            }
                        });
                    } catch (Exception ex) {
                        OustSdkTools.showToast(ex.getMessage());
                    }
                } else {
                    OustSdkTools.showToast("whatsapp not installed");
                }
            }
        }catch (Exception e){

        }
    }
    public static void shareScreenInWhatsApp(Bitmap bitmap, String branchLink) {
        try {
            String oustShareMessage = "Hi There, Let's play and win on Oust. The new way to study smarter and rank higher.";
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(OustSdkApplication.getContext().getContentResolver(), bitmap, "Title", null);
            // Uri imageUri = Uri.parse(path);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
            f.createNewFile();
            new FileOutputStream(f).write(bytes.toByteArray());
            Uri imageUri=Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, oustShareMessage + "\n" + branchLink);
            intent.setType("text/plain");

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent.setType("image/jpeg");
            intent.setPackage("com.whatsapp");
            OustSdkApplication.getContext().startActivity(intent);
        }catch (Exception e){
            OustSdkTools.showToast("Unable to share_text on whatsapp");
        }
    }

    public static void shareEventOnWhatsApp(Bitmap bitmap,String msg){
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //String path = MediaStore.Images.Media.insertImage(OustSdkApplication.getContext().getContentResolver(), bitmap, "Title", null);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
            f.createNewFile();
            new FileOutputStream(f).write(bytes.toByteArray());
            Uri imageUri=Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, msg + "\n" + "");
            intent.setType("text/plain");

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent.setType("image/jpeg");
            intent.setPackage("com.whatsapp");
            OustSdkApplication.getContext().startActivity(intent);
        }catch (Exception e){
            OustSdkTools.showToast("Unable to share_text on whatsapp");
        }
    }


}
