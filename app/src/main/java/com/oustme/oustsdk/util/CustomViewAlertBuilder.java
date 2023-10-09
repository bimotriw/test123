package com.oustme.oustsdk.util;

import android.content.Context;
import android.os.Build;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.customviews.HeavyCustomTextView;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class CustomViewAlertBuilder {
    private AlertDialog mAlertDialog;
    private PositiveButtonClick mPositiveButtonClick;
    private NegativeButtonClick mNegativeButtonClick;
    private String toolBarColorCode;
    private String positiveMessage, negativeMessage;
    private View dialogView;

    public CustomViewAlertBuilder(Context mContext, String mMessage, String positiveButtonMessage, String negativeButtonMessage) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.new_alert_popup, null);

        this.mPositiveButtonClick = new PositiveButtonClick(this);
        this.mNegativeButtonClick = new NegativeButtonClick(this);
        this.toolBarColorCode = OustPreferences.get(AppConstants.StringConstants.TOOL_BAR_COLOR_CODE);
        /*if(toolBarColorCode!=null && !toolBarColorCode.trim().isEmpty()) {
            this.positiveMessage = "<font color=" + this.toolBarColorCode + ">" + positiveButtonMessage + "</font>";
            this.negativeMessage = "<font color=" + this.toolBarColorCode + ">" + negativeButtonMessage + "</font>";
        }
        else {
            this.positiveMessage = positiveButtonMessage;
            this.negativeMessage = negativeButtonMessage;
        }*/
        this.positiveMessage = positiveButtonMessage;
        this.negativeMessage = negativeButtonMessage;

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        CustomTextView buttonNo = dialogView.findViewById(R.id.buttonNo);
        CustomTextView buttonYes = dialogView.findViewById(R.id.buttonYes);
        CustomTextView message = dialogView.findViewById(R.id.alertMessage);
        buttonNo.setText(negativeMessage);
        buttonYes.setText(positiveMessage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            message.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }
        message.setText(mMessage);
        builder.setView(dialogView);
        buttonNo.setOnClickListener(mNegativeButtonClick);
        buttonYes.setOnClickListener(mPositiveButtonClick);

       /* builder.setPositiveButton(Html.fromHtml(positiveMessage), mPositiveButtonClick);
        builder.setNegativeButton(Html.fromHtml(negativeMessage), mNegativeButtonClick);*/
        this.mAlertDialog = builder.create();
        this.mAlertDialog.setCancelable(false);
       /* if(toolBarColorCode!=null && !toolBarColorCode.trim().isEmpty()) {
            this.mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor(toolBarColorCode));
            this.mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor(toolBarColorCode));
        }*/
    }
    public CustomViewAlertBuilder(Context mContext, String mMessage, String titleText, String positiveButtonMessage, String negativeButtonMessage) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.new_alert_popup, null);

        this.mPositiveButtonClick = new PositiveButtonClick(this);
        this.mNegativeButtonClick = new NegativeButtonClick(this);
        this.toolBarColorCode = OustPreferences.get(AppConstants.StringConstants.TOOL_BAR_COLOR_CODE);
        /*if(toolBarColorCode!=null && !toolBarColorCode.trim().isEmpty()) {
            this.positiveMessage = "<font color=" + this.toolBarColorCode + ">" + positiveButtonMessage + "</font>";
            this.negativeMessage = "<font color=" + this.toolBarColorCode + ">" + negativeButtonMessage + "</font>";
        }
        else {
            this.positiveMessage = positiveButtonMessage;
            this.negativeMessage = negativeButtonMessage;
        }*/
        this.positiveMessage = positiveButtonMessage;
        this.negativeMessage = negativeButtonMessage;

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //builder.setMessage(mMessage);
        CustomTextView buttonNo = dialogView.findViewById(R.id.buttonNo);
        CustomTextView buttonYes = dialogView.findViewById(R.id.buttonYes);
        CustomTextView message = dialogView.findViewById(R.id.alertMessage);
        HeavyCustomTextView title = dialogView.findViewById(R.id.alertTitle);

        buttonNo.setText(negativeMessage);
        buttonYes.setText(positiveMessage);
        message.setText(mMessage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            message.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }
        title.setText(titleText);
        title.setVisibility(View.VISIBLE);
        builder.setView(dialogView);

        buttonNo.setOnClickListener(mNegativeButtonClick);
        buttonYes.setOnClickListener(mPositiveButtonClick);

       /* builder.setPositiveButton(Html.fromHtml(positiveMessage), mPositiveButtonClick);
        builder.setNegativeButton(Html.fromHtml(negativeMessage), mNegativeButtonClick);*/
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
    public CustomViewAlertBuilder show()
    {
        mAlertDialog.show();
        return this;
    }
    public CustomViewAlertBuilder dismiss()
    {
        mAlertDialog.dismiss();
        return this;
    }
    public CustomViewAlertBuilder setCanceled(boolean value){
        this.mAlertDialog.setCancelable(value);
        return this;
    }


    public class PositiveButtonClick implements View.OnClickListener {
        private CustomViewAlertBuilder mAlertBuilder;

        public PositiveButtonClick(CustomViewAlertBuilder mAlertBuilder) {
            this.mAlertBuilder = mAlertBuilder;
        }

        @Override
        public void onClick(View v) {
            this.mAlertBuilder.positiveButtonClicked();
        }
    }

    public class NegativeButtonClick implements View.OnClickListener {
        private CustomViewAlertBuilder mAlertBuilder;

        public NegativeButtonClick(CustomViewAlertBuilder mAlertBuilder) {
            this.mAlertBuilder = mAlertBuilder;
        }

        @Override
        public void onClick(View v) {
            this.mAlertBuilder.negativeButtonClicked();
        }
    }

    public void positiveButtonClicked(){
        dismiss();
    }
    public void negativeButtonClicked(){

    }

}
