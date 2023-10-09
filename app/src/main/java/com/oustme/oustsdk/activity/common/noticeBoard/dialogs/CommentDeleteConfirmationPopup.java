package com.oustme.oustsdk.activity.common.noticeBoard.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.NBDeleteListener;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.PostViewData;

/**
 * Created by oust on 4/2/19.
 */

public class CommentDeleteConfirmationPopup extends Dialog {
    private Context mContext;
    private NBDeleteListener nbDeleteListener;
    private PostViewData postViewData;
    public CommentDeleteConfirmationPopup(@NonNull Context context, PostViewData postViewData,NBDeleteListener nbDeleteListener) {
        super(context, android.R.style.Theme_Black_NoTitleBar);
        this.mContext = context;
        this.postViewData = postViewData;
        this.nbDeleteListener=nbDeleteListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(mContext.getResources().getDrawable(R.color.black_semi_transparent));
        setContentView(R.layout.dialog_comment_delete);
        setCancelable(true);

        initViews();

    }

    private ImageView imageViewClose;
    private LinearLayout mLinearLayoutRetry,mLinearLayoutOk;
    private void initViews() {
        imageViewClose = findViewById(R.id.imageViewClose);
        mLinearLayoutRetry = findViewById(R.id.linearLayoutRetry);
        mLinearLayoutOk = findViewById(R.id.linearLayoutOK);
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mLinearLayoutOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nbDeleteListener.onDelete(postViewData);
                dismiss();
            }
        });

        mLinearLayoutRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

}
