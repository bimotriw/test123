package com.oustme.oustsdk.tools;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import com.oustme.oustsdk.R;


/**
 * Created by ROHIT on 07-07-2016.
 */
public class ShowPopup {
    private static ShowPopup mInstance = null;

    public Dialog progressDialog;

    private ShowPopup() {
    }

    public static ShowPopup getInstance() {
        if (mInstance == null) {
            mInstance = new ShowPopup();
        }
        return mInstance;
    }


    public void showProgressBar(Context context) {
        try {
            if (context != null) {
                progressDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.popup_bg);
                progressDialog.getWindow().setBackgroundDrawableResource(R.color.popupBackGround);
                progressDialog.setContentView(R.layout.custom_progress_dialog);
                if (progressDialog != null)
                    progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void showProgressBar(Context context, String message) {
        try {
            if (context != null) {
                progressDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.popup_bg);
                progressDialog.getWindow().setBackgroundDrawableResource(R.color.popupBackGround);
                progressDialog.setContentView(R.layout.custom_progress_dialog);
                TextView textView = progressDialog.findViewById(R.id.tv_progress);
                textView.setText("" + message);
                if (progressDialog != null)
                    progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void dismissProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        OustPreferences.saveAppInstallVariable("CLICK", false);
                    }
                });
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}

