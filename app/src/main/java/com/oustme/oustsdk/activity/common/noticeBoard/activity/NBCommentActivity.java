package com.oustme.oustsdk.activity.common.noticeBoard.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.widget.Toolbar;

import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.adapters.NBReplyAdapter;
import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.NBDeleteListener;
import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.NoticeBoardCommentDeleteListener;
import com.oustme.oustsdk.activity.common.noticeBoard.data.handlers.SubmitNBPostService;
import com.oustme.oustsdk.activity.common.noticeBoard.dialogs.CommentDeleteConfirmationPopup;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.PostViewData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBReplyData;
import com.oustme.oustsdk.activity.common.noticeBoard.presenters.NBCommentPresenter;
import com.oustme.oustsdk.activity.common.noticeBoard.view.NBCommentView;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NBCommentActivity extends BaseActivity implements NBCommentView,NoticeBoardCommentDeleteListener,NBDeleteListener {

    private Toolbar toolbar;
    private RecyclerView replies_rv;
    private TextView commentTv, commented_by, titleTextView;
    private CircleImageView avatar;
    private ImageView send_imgview;
    private EditText reply_et;
    private NBCommentPresenter mPresenter;
    private NBReplyAdapter nbReplyAdapter;
    private RelativeLayout submit_reply_ll;

    @Override
    protected int getContentView() {
        OustSdkTools.setLocale(NBCommentActivity.this);
        return R.layout.activity_nbcomment;
    }

    @Override
    protected void initView() {
        avatar = findViewById(R.id.avatar);
        commentTv = findViewById(R.id.comment);
        commented_by = findViewById(R.id.commented_by);
        replies_rv = findViewById(R.id.replies_rv);
        toolbar = findViewById(R.id.tabanim_toolbar);
        reply_et = findViewById(R.id.reply_et);
        send_imgview = findViewById(R.id.send_imgview);
        submit_reply_ll=findViewById(R.id.submit_reply_ll);

        setToolbar();
    }

    private void setToolbar() {
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setTitle("");
            titleTextView = toolbar.findViewById(R.id.title);
            titleTextView.setText(""+OustStrings.getString("comment_text"));
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            int toolbarColor = 0;
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                toolbarColor = Color.parseColor(toolbarColorCode);
            } else {
                toolbarColor = context.getResources().getColor(R.color.lgreen);
            }

            PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
            GradientDrawable ld = (GradientDrawable) context.getResources().getDrawable(R.drawable.greenlite_round_textview);
            ld.setColorFilter(toolbarColor, mode);
            submit_reply_ll.setBackgroundDrawable(ld);
            toolbar.setBackgroundColor(toolbarColor);
            OustSdkTools.setImage(send_imgview, getResources().getString(R.string.send));

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initData() {
        try {
            long postId = getIntent().getLongExtra("postId", 0);
            long commentId = getIntent().getLongExtra("commentId", 0);
            long nbId = getIntent().getLongExtra("nbId", 0);
            mPresenter = new NBCommentPresenter(this, nbId, postId, commentId);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initListener() {
        submit_reply_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    reply_et.setError(null);
                    mPresenter.checkIfReply(reply_et.getText().toString());
                }catch (Exception e){
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        });
    }

    @Override
    public void onErrorFound() {
//        OustSdkTools.showToast(getResources().getString(R.string.error_message));
        finish();
    }

    @Override
    public void setCommentData(String avatarUrl, String commentedBy, String comment) {
        try {
            Picasso.get().load(avatarUrl).into(this.avatar);
            commented_by.setText(commentedBy);
            commentTv.setText(comment);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setOrUpdateAdapter(List<NBReplyData> nbReplyDataList) {
        try {
            if (nbReplyDataList == null) {
                return;
            }
            if (nbReplyAdapter == null) {
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                replies_rv.setLayoutManager(mLayoutManager);
                nbReplyAdapter = new NBReplyAdapter(this, nbReplyDataList,this);
                replies_rv.setAdapter(nbReplyAdapter);
            } else {
                nbReplyAdapter.notifyListChnage(nbReplyDataList);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void updateCommentsCount(int count) {
        try {
            if (count == 0)
                titleTextView.setText(""+OustStrings.getString("no_comments"));
            else if (count == 1)
                titleTextView.setText("(" + count + ") "+OustStrings.getString("comment_text"));
            else
                titleTextView.setText("(" + count + ") "+OustStrings.getString("comments_text"));
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void noReplyAdded() {
        reply_et.setError(getResources().getString(R.string.enter_reply_text));
    }

    @Override
    public void startApiCalls() {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitNBPostService.class);
        OustSdkApplication.getContext().startService(intent);
    }

    @Override
    public void resetReplyText() {
        Log.d(TAG, "resetReplyText: ");
        reply_et.setText("");
        //reply_et.setText(""+OustStrings.getString("enter_reply_text"));
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(reply_et.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onDeleteComment(PostViewData postViewData) {
        new CommentDeleteConfirmationPopup(NBCommentActivity.this,postViewData,this).show();
    }

    @Override
    public void onDelete(PostViewData postViewData) {
        mPresenter.deleteReplyData(postViewData);
    }

    @Override
    public void onDeleteCancel() {

    }
}
