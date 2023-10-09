package com.oustme.oustsdk.layoutFour.newnoticeBoard.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters.NewNBReplyAdapter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewNBDeleteListener;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewNoticeBoardCommentDeleteListener;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.dialogs.NewCommentDeleteConfirmationPopup;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBReplyData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.presenters.NewNBCommentPresenter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.view.NewNBCommentView;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

public class NewNBCommentActivity extends BaseActivity implements NewNBCommentView, NewNoticeBoardCommentDeleteListener, NewNBDeleteListener {

    private Toolbar toolbar;
    private RecyclerView replies_rv;
    private TextView commentTv, commented_by, titleTextView, posted_on_tv, designation, userRole;
    private CircleImageView avatar, author_img;
    private ImageView send_imgview;
    private EditText reply_et;
    private NewNBCommentPresenter mPresenter;
    private NewNBReplyAdapter nbReplyAdapter;
    private RelativeLayout submit_reply_ll;
    ImageView back_button;
    private int color;
    private int bgColor;
    BitmapDrawable bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image));
    BitmapDrawable bd_loading = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading));

    @Override
    protected int getContentView() {
        OustSdkTools.setLocale(NewNBCommentActivity.this);
        return R.layout.activity_nbcomment2;
    }

    @Override
    protected void initView() {
        try {
            getColors();
            avatar = findViewById(R.id.avatar);
            commentTv = findViewById(R.id.comment);
            commented_by = findViewById(R.id.commented_by);
            replies_rv = findViewById(R.id.replies_rv);
            toolbar = findViewById(R.id.toolbar_notification_layout);
            reply_et = findViewById(R.id.reply_et);
            send_imgview = findViewById(R.id.send_imgview);
            submit_reply_ll = findViewById(R.id.submit_reply_ll);
            back_button = findViewById(R.id.back_button);
            posted_on_tv = findViewById(R.id.posted_on_tv);
            designation = findViewById(R.id.designation);
            userRole = findViewById(R.id.userRole);
            author_img = findViewById(R.id.author_img);

            reply_et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 0) {
                        send_imgview.setColorFilter(color);
                    } else {
                        send_imgview.setColorFilter(OustSdkApplication.getContext().getResources().getColor(R.color.common_grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            setToolbar();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setToolbar() {
        try {

            toolbar.setBackgroundColor(Color.WHITE);
//            screen_name.setTextColor(color);
            OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
            toolbar.setTitle("");
            toolbar.setElevation(0f);
            setSupportActionBar(toolbar);

//            setSupportActionBar(toolbar);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowCustomEnabled(true);
//            getSupportActionBar().setTitle("");
//            titleTextView = toolbar.findViewById(R.id.title);
//            titleTextView.setText(""+OustStrings.getString("comment_text"));
//            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
//            int toolbarColor = 0;
//            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
//                toolbarColor = Color.parseColor(toolbarColorCode);
//            } else {
//                toolbarColor = context.getResources().getColor(R.color.lgreen);
//            }
//
//            PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
//            GradientDrawable ld = (GradientDrawable) context.getResources().getDrawable(R.drawable.greenlite_round_textview);
//            ld.setColorFilter(toolbarColor, mode);
//            submit_reply_ll.setBackgroundDrawable(ld);
//            toolbar.setBackgroundColor(toolbarColor);
//            OustSdkTools.setImage(send_imgview, getResources().getString(R.string.send));

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getColors() {
        try {
            if (OustPreferences.getAppInstallVariable("isLayout4")) {
                color = OustResourceUtils.getColors();
                bgColor = OustResourceUtils.getToolBarBgColor();
            } else {
                bgColor = OustResourceUtils.getColors();
                color = OustResourceUtils.getToolBarBgColor();

            }
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
            mPresenter = new NewNBCommentPresenter(this, nbId, postId, commentId);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initListener() {
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        submit_reply_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    reply_et.setError(null);
                    mPresenter.checkIfReply(reply_et.getText().toString());
                } catch (Exception e) {
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
    public void setCommentData(String avatarUrl, String commentedBy, String comment, Long commentOn, String designation1, String userRole1) {
        try {
            if (avatarUrl != null) {
                Picasso.get().load(avatarUrl)
                        .placeholder(bd_loading).error(bd).into(author_img);
            }
            commented_by.setText(WordUtils.capitalize(commentedBy));
            commentTv.setText(comment);
            posted_on_tv.setText(OustSdkTools.getDate("" + commentOn));
            if (designation1 != null && !designation1.equalsIgnoreCase("null")) {
                designation.setText(designation1 + "   ");
            }
            userRole.setText(userRole1);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setOrUpdateAdapter(List<NewNBReplyData> nbReplyDataList) {
        try {
            if (nbReplyDataList == null) {
                return;
            }
            if (nbReplyAdapter == null) {
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                replies_rv.setLayoutManager(mLayoutManager);
                nbReplyAdapter = new NewNBReplyAdapter(this, nbReplyDataList, this);
                replies_rv.setAdapter(nbReplyAdapter);
            } else {
                nbReplyAdapter.notifyListChnage(nbReplyDataList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void updateCommentsCount(int count) {
        try {
//            if (count == 0)
//                titleTextView.setText("" + OustStrings.getString("no_comments"));
//            else if (count == 1)
//                titleTextView.setText("(" + count + ") " + OustStrings.getString("comment_text"));
//            else
//                titleTextView.setText("(" + count + ") " + OustStrings.getString("comments_text"));
        } catch (Exception e) {
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
    public void onDeleteComment(NewPostViewData postViewData) {
        new NewCommentDeleteConfirmationPopup(NewNBCommentActivity.this, postViewData, this).show();
    }

    @Override
    public void onDelete(NewPostViewData postViewData) {
        mPresenter.deleteReplyData(postViewData);
    }

    @Override
    public void onDeleteCancel() {

    }
}
