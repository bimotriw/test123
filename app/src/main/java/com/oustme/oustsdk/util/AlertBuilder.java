package com.oustme.oustsdk.util;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.text.Html;

import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

public class AlertBuilder {
    private AlertDialog mAlertDialog;
    private PositiveButtonClick mPositiveButtonClick;
    private NegativeButtonClick mNegativeButtonClick;
    private String toolBarColorCode;
    private String positiveMessage, negativeMessage;

    public AlertBuilder(Context mContext, String mMessage, String positiveButtonMessage, String negativeButtonMessage) {
        this.mPositiveButtonClick = new PositiveButtonClick(this);
        this.mNegativeButtonClick = new NegativeButtonClick(this);
        this.toolBarColorCode = OustPreferences.get(AppConstants.StringConstants.TOOL_BAR_COLOR_CODE);
        if(toolBarColorCode!=null && !toolBarColorCode.trim().isEmpty()) {
            this.positiveMessage = "<font color=" + this.toolBarColorCode + ">" + positiveButtonMessage + "</font>";
            this.negativeMessage = "<font color=" + this.toolBarColorCode + ">" + negativeButtonMessage + "</font>";
        }
        else {
            this.positiveMessage = positiveButtonMessage;
            this.negativeMessage = negativeButtonMessage;
        }
        //textView.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mMessage);
        builder.setPositiveButton(Html.fromHtml(positiveMessage), mPositiveButtonClick);
        builder.setNegativeButton(Html.fromHtml(negativeMessage), mNegativeButtonClick);
        this.mAlertDialog = builder.create();
        this.mAlertDialog.setCancelable(false);
       /* if(toolBarColorCode!=null && !toolBarColorCode.trim().isEmpty()) {
            this.mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor(toolBarColorCode));
            this.mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor(toolBarColorCode));
        }*/
    }
    public AlertDialog getAlertDialog()
    {
        return this.mAlertDialog;
    }
    public AlertBuilder show()
    {
        mAlertDialog.show();
        return this;
    }
    public AlertBuilder dismiss()
    {
        mAlertDialog.dismiss();
        return this;
    }
    public AlertBuilder setCanceled(boolean value){
        this.mAlertDialog.setCancelable(value);
        return this;
    }


    public class PositiveButtonClick implements DialogInterface.OnClickListener{
        private AlertBuilder mAlertBuilder;

        public PositiveButtonClick(AlertBuilder mAlertBuilder) {
            this.mAlertBuilder = mAlertBuilder;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            this.mAlertBuilder.positiveButtonClicked();
        }
    }

    public class NegativeButtonClick implements DialogInterface.OnClickListener{
        private AlertBuilder mAlertBuilder;

        public NegativeButtonClick(AlertBuilder mAlertBuilder) {
            this.mAlertBuilder = mAlertBuilder;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            this.mAlertBuilder.negativeButtonClicked();
        }
    }

    public void positiveButtonClicked(){
        dismiss();
    }
    public void negativeButtonClicked(){

    }

}
