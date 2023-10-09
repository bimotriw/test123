package com.oustme.oustsdk.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustSdkTools;

import static android.view.Gravity.BOTTOM;

public class LearningDiaryFailDialog extends Dialog {
    private Context mContext;
    private boolean hasUploaded = false;
    public LearningDiaryFailDialog( Context context,boolean hasUploaded ) {
        super(context, android.R.style.Theme_Black_NoTitleBar);
        this.mContext=context;
        this.hasUploaded=hasUploaded;
    }

    public LearningDiaryFailDialog( Context context, int themeResId) {
        super(context, themeResId);
    }

    protected LearningDiaryFailDialog(Context context, boolean cancelable,DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(mContext.getResources().getDrawable(R.color.black_semi_transparent));
        setContentView(R.layout.upload_fail_success);
        setCancelable(false);

        initViews();

    }
    private ImageView imageViewClose;
    private LinearLayout mLinearLayoutRetry,mLinearLayoutOk;
    private void initViews() {
        imageViewClose = findViewById(R.id.imageViewClose);
        TextView mTextViewTitle = findViewById(R.id.textViewTitle);
        TextView mTextViewMessage = findViewById(R.id.textViewMessage);
        mLinearLayoutRetry = findViewById(R.id.linearLayoutRetry);
        mLinearLayoutOk = findViewById(R.id.linearLayoutOK);
        if (hasUploaded) {
            mTextViewTitle.setText("Upload Complete");
            mTextViewMessage.setText("Your media upload has been completed!");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 2.0f;
            params.bottomMargin = 0;
            params.gravity = BOTTOM;
            mLinearLayoutOk.setLayoutParams(params);
        } else {
            mTextViewTitle.setText("Upload Failed");
            mTextViewMessage.setText("Sorry!! your media upload has been failed");
        }
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mLinearLayoutOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dismiss();
            }
        });

        mLinearLayoutRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (OustSdkTools.checkInternetStatus()) {
//                    if (savedImageFile != null) {
//                        uploadToAWS(savedImageFile);
//                    }
//                } else {
//                    OustSdkTools.showToast(getString(R.string.no_internet_connection));
//                }
            }
        });

    }


}
