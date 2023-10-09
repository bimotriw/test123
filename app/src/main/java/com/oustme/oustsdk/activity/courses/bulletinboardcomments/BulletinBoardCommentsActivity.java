package com.oustme.oustsdk.activity.courses.bulletinboardcomments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.DataSnapshot;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.courses.BulletinCommentsAdapter;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.course.BulletinBoardData;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by shilpysamaddar on 07/08/17.
 */

public class BulletinBoardCommentsActivity extends BaseActivity implements View.OnClickListener, BulletinBoardCommentsView {
    private ActiveUser activeUser;
    private Toolbar toolbar;
    private AppBarLayout bulletin_appbar;
    private RecyclerView bulletin_comments_recyclerview;
    private EditText add_comments;
    private LinearLayout submit_layout;
    private ScrollView nocomment_layout;
    private Intent intent;
    private FirebaseRefClass firebaseRefClass;
    private SwipeRefreshLayout swipe_refresh_layout;
    private String quesKey, courseName, user_comment, questionData, userDisplayName, userAvatar;
    private long courseId;
    private BulletinBoardData bulletinBoardCommentsData;
    private List<BulletinBoardData> commentsList;
    private BulletinCommentsAdapter bulletinCommentsAdapter;
    private RelativeLayout comments;
    private ImageView user_image, send_imgview;
    private TextView nocomments_text, question, user_display_name;
    private boolean isCplBulletin = false;
    private BulletinBoardCommentsPresenter mPresenter;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected int getContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_bulletinboard_comments;
    }

    @Override
    protected void initView() {
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        bulletin_appbar = findViewById(R.id.bulletin_comments_appbar);
        toolbar = findViewById(R.id.tabanim_toolbar);
        comments = findViewById(R.id.comments);
        bulletin_comments_recyclerview = findViewById(R.id.bulletin_comments_recyclerview);
        add_comments = findViewById(R.id.add_comments);
        submit_layout = findViewById(R.id.submit_layout);
        question = findViewById(R.id.question);
        nocomment_layout = findViewById(R.id.nocomment_layout);
        nocomments_text = findViewById(R.id.nocomments_text);
        user_image = findViewById(R.id.user_image);
        user_display_name = findViewById(R.id.user_display_name);
        send_imgview = findViewById(R.id.send_imgview);

        submit_layout.setOnClickListener(this);

        OustSdkTools.setImage(send_imgview, getResources().getString(R.string.send));
    }

    @Override
    protected void initData() {
        activeUser = OustAppState.getInstance().getActiveUser();
        intent = getIntent();
        questionData = intent.getStringExtra("question");
        quesKey = intent.getStringExtra("questionKey");
        courseId = intent.getLongExtra("courseId", 0);
        courseName = intent.getStringExtra("courseName");
        if (courseName == null || courseName.length() == 0) {
            isCplBulletin = true;
            courseId = intent.getLongExtra("cplId", 0);
            courseName = intent.getStringExtra("cplName");
        }
        userDisplayName = intent.getStringExtra("userDisplayName");
        userAvatar = intent.getStringExtra("userAvatar");
        setToolBarColor();
        createLoader();
        mPresenter = new BulletinBoardCommentsPresenter(BulletinBoardCommentsActivity.this);
        mPresenter.getCommentsFromFirebase(quesKey);
//        OustGATools.getInstance().reportPageViewToGoogle(BulletinBoardCommentsActivity.this, "BulletinBoard Comments Page");

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void setToolBarColor() {
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            TextView titleTextView = toolbar.findViewById(R.id.title);
            if (commentsList != null) {
                if (commentsList.size() == 0) {
                    titleTextView.setText("(0) " + getResources().getString(R.string.comment_text));
                } else if (commentsList.size() == 1) {
                    titleTextView.setText("(1) " + getResources().getString(R.string.comment_text));
                } else {
                    titleTextView.setText("(" + commentsList.size() + ") " + getResources().getString(R.string.comments_text));
                }
            } else {
                titleTextView.setText("" + getResources().getString(R.string.no_comments));
            }
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                bulletin_appbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
                send_imgview.setColorFilter(Color.parseColor(toolbarColorCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onError() {
        OustSdkTools.showToast(getString(R.string.something_went_wrong));
    }

    @Override
    public void showLoader() {

    }

    @Override
    public void hideLoader() {

    }

    private void createLoader() {
        try {
            swipe_refresh_layout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
//               creates loader
            swipe_refresh_layout.setOnRefreshListener(() -> mPresenter.getCommentsFromFirebase(quesKey));
//            show loader
            swipe_refresh_layout.post(() -> {
                if ((commentsList != null) && (commentsList.size() > 0)) {
                } else {
                    swipe_refresh_layout.setRefreshing(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void updateCommentFromFireBase(DataSnapshot dataSnapshot) {
        try {
            if (null != dataSnapshot.getValue()) {
                final Map<String, Object> allCommentsMap = (Map<String, Object>) dataSnapshot.getValue();
                if (allCommentsMap != null) {
                    commentsList = new ArrayList<>();
                    for (String queskey : allCommentsMap.keySet()) {
                        Object commentDataObject = allCommentsMap.get(queskey);
                        final Map<String, Object> commentsDataMap = (Map<String, Object>) commentDataObject;
                        if (commentsDataMap != null) {
                            {
                                bulletinBoardCommentsData = new BulletinBoardData();
                                if (commentsDataMap.get("addedOnDate") != null) {
                                    bulletinBoardCommentsData.setAddedOnDate((long) commentsDataMap.get("addedOnDate"));
                                }
                                if (commentsDataMap.get("courseId") != null) {
                                    bulletinBoardCommentsData.setCourseId(OustSdkTools.convertToLong(commentsDataMap.get("courseId")));
                                }
                                if (commentsDataMap.get("courseName") != null) {
                                    bulletinBoardCommentsData.setCourseName((String) commentsDataMap.get("courseName"));
                                }
                                if (commentsDataMap.get("cplId") != null) {
                                    bulletinBoardCommentsData.setCplId(OustSdkTools.convertToLong(commentsDataMap.get("cplId")));
                                }
                                if (commentsDataMap.get("cplName") != null) {
                                    bulletinBoardCommentsData.setCplName((String) commentsDataMap.get("cplName"));
                                }
                                if (commentsDataMap.get("comment") != null) {
                                    bulletinBoardCommentsData.setComment((String) commentsDataMap.get("comment"));
                                }
                                if (commentsDataMap.get("userAvatar") != null) {
                                    bulletinBoardCommentsData.setUserAvatar((String) commentsDataMap.get("userAvatar"));
                                }
                                if (commentsDataMap.get("userDisplayName") != null) {
                                    bulletinBoardCommentsData.setUserDisplayName((String) commentsDataMap.get("userDisplayName"));
                                }
                                if (commentsDataMap.get("userId") != null) {
                                    bulletinBoardCommentsData.setUserId((String) commentsDataMap.get("userId"));
                                }
                                if (commentsDataMap.get("userKey") != null) {
                                    bulletinBoardCommentsData.setUserKey(OustSdkTools.convertToLong(commentsDataMap.get("userKey")));
                                }
                                commentsList.add(bulletinBoardCommentsData);

                            }
                        }
                    }
                }
                setToolBarColor();
                setAdapter(commentsList);
            } else {
                setToolBarColor();
                setAdapter(null);
            }
        } catch (Exception e) {
            setToolBarColor();
            setAdapter(null);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setAdapter(List<BulletinBoardData> commentsList) {
        final Handler handler = new Handler();
        if ((commentsList != null) && (commentsList.size() > 0)) {
            Collections.sort(commentsList, courseListSortRev);
            if (bulletinCommentsAdapter != null) {
                bulletinCommentsAdapter.notifyDataChange(commentsList);
                swipe_refresh_layout.setRefreshing(false);
            } else {
                comments.setVisibility(View.VISIBLE);
                bulletin_comments_recyclerview.setVisibility(View.VISIBLE);
                nocomment_layout.setVisibility(View.GONE);
                bulletinCommentsAdapter = new BulletinCommentsAdapter(commentsList, questionData, userDisplayName, userAvatar, BulletinBoardCommentsActivity.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(OustSdkApplication.getContext(), LinearLayoutManager.VERTICAL, false);
                bulletin_comments_recyclerview.setLayoutManager(mLayoutManager);
                bulletin_comments_recyclerview.setItemAnimator(new DefaultItemAnimator());
                bulletin_comments_recyclerview.setAdapter(bulletinCommentsAdapter);
                handler.postDelayed(() -> swipe_refresh_layout.setRefreshing(false), 1000);
            }
        } else {
            nocomment_layout.setVisibility(View.VISIBLE);
            nocomments_text.setVisibility(View.VISIBLE);
            bulletin_comments_recyclerview.setVisibility(View.GONE);
            question.setText(questionData);
            nocomments_text.setText(getResources().getString(R.string.no_comments));
            user_display_name.setText(userDisplayName);
            setUserAvatar();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipe_refresh_layout.setRefreshing(false);
                }
            }, 1000);
        }
    }

    private void setUserAvatar() {
        if ((userAvatar != null) && (!userAvatar.isEmpty())) {
            if (userAvatar.startsWith("http")) {
                Picasso.get().load(userAvatar)
                        .placeholder(OustSdkTools.getImageDrawable(getResources().getString(R.string.profile_image_loading))).error(OustSdkTools.getImageDrawable(getResources().getString(R.string.maleavatar)))
                        .into(user_image);
            } else {
                Picasso.get().load(OustSdkApplication.getContext().getString(R.string.oust_user_avatar_link) + userAvatar)
                        .placeholder(OustSdkTools.getImageDrawable(getResources().getString(R.string.profile_image_loading))).error(OustSdkTools.getImageDrawable(getResources().getString(R.string.maleavatar)))
                        .into(user_image);
            }
        } else {
            Picasso.get().load(getResources().getString(R.string.maleavatar))
                    .placeholder(OustSdkTools.getImageDrawable(getResources().getString(R.string.profile_image_loading))).error(OustSdkTools.getImageDrawable(getResources().getString(R.string.maleavatar)))
                    .into(user_image);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.submit_layout) {
            if (add_comments.getText() != null) {
                user_comment = add_comments.getText().toString();
                if (!user_comment.isEmpty()) {
                    setDataToFirebase(user_comment);
                    mPresenter.getCommentsFromFirebase(quesKey);
                }
                add_comments.setText(null);
                add_comments.setHint("" + getResources().getString(R.string.type_here));
                mPresenter.getCommentsFromFirebase(quesKey);
                hideKeyboard(add_comments);
            }
        }
    }

    public void hideKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setDataToFirebase(String user_comment) {
        BulletinBoardData bulletinBoardData = new BulletinBoardData();
        bulletinBoardData.setAddedOnDate(new Date().getTime());
        if (!isCplBulletin) {
            bulletinBoardData.setCourseName(courseName);
            bulletinBoardData.setCourseId(courseId);
        } else {
            bulletinBoardData.setCplName(courseName);
            bulletinBoardData.setCplId(courseId);
        }
        bulletinBoardData.setQuesKey(quesKey);
        bulletinBoardData.setUserDisplayName(activeUser.getUserDisplayName());
        bulletinBoardData.setComment(user_comment);
        bulletinBoardData.setUserId(activeUser.getStudentid());
        bulletinBoardData.setUserKey(Long.parseLong(activeUser.getStudentKey()));
        bulletinBoardData.setUserAvatar(activeUser.getAvatar());
        bulletinBoardData.setDevicePlatform("Android");
        mPresenter.setCommentToFB(bulletinBoardData);
    }

    public Comparator<BulletinBoardData> courseListSortRev = new Comparator<BulletinBoardData>() {
        public int compare(BulletinBoardData s1, BulletinBoardData s2) {
            if (s1.getAddedOnDate() > s2.getAddedOnDate()) {
                return -1;
            } else if (s1.getAddedOnDate() < s2.getAddedOnDate()) {
                return 1;
            }
            return 0;
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BulletinBoardCommentsActivity.this.overridePendingTransition(R.anim.enter, R.anim.exit);
        BulletinBoardCommentsActivity.this.finish();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
