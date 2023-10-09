package com.oustme.oustsdk.activity.courses.bulletinboardquestion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.util.Log;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.DataSnapshot;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.courses.BulletinQuesAdapter;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.customviews.CustomSearchView;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
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
 * Created by shilpysamaddar on 03/08/17.
 */

public class BulletinBoardQuestionActivity extends BaseActivity implements BulletinBoardQuestionView, SearchView.OnQueryTextListener, View.OnClickListener {

    private MenuItem actionSearch;
    public CustomSearchView newSearchView;
    private SwipeRefreshLayout swipe_refresh_layout;
    private Toolbar toolbar;
    private RecyclerView bulletin_question_recyclerview;
    private AppBarLayout bulletin_appbar;
    private BulletinQuesAdapter bulletinQuesAdapter;
    private String courseId, courseName;
    private ImageView userImage;
    private ActiveUser activeUser;
    private String question;
    private RelativeLayout noquestion_layout;
    private TextView ask_ques_hint;
    private TextView user_name;
    private FirebaseRefClass firebaseRefClass;
    private BulletinBoardData bulletinBoardQuestionData;
    private List<BulletinBoardData> questionsList;
    private boolean isCplBulletin = false;
    private BulletinBoardQuestionPresenter mPresenter;

    public BulletinBoardQuestionActivity() {
    }

    @Override
    protected int getContentView() {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return R.layout.activity_bulletin_board;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (mPresenter != null && courseId != null && !courseId.isEmpty())
                Log.d(TAG, "onResume: courseId:" + courseId);
            mPresenter.getBulletinQuesFromFirebase(courseId, isCplBulletin);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        try {
            String searchText = "";
            if (newText.isEmpty()) {
                searchText = "";

            } else {
                searchText = newText;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_search) {
            actionSearch.setVisible(true);
            View searchPlate = newSearchView.findViewById(R.id.search_plate);
            searchPlate.setBackgroundColor(OustSdkTools.getColorBack(R.color.black_semi_transparent));
        } else {
            super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void initView() {
        try {
            OustSdkTools.setLocale(BulletinBoardQuestionActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        bulletin_appbar = findViewById(R.id.bulletin_appbar);
        toolbar = findViewById(R.id.tabanim_toolbar);
        bulletin_question_recyclerview = findViewById(R.id.bulletin_question_recyclerview);
        userImage = findViewById(R.id.userImage);
        LinearLayout ask_ques_layout = findViewById(R.id.ask_ques_layout);
        user_name = findViewById(R.id.user_name);
        ask_ques_hint = findViewById(R.id.ask_ques_hint);
        noquestion_layout = findViewById(R.id.noquestion_layout);
        ask_ques_layout.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        try {
            activeUser = OustAppState.getInstance().getActiveUser();
            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(getApplicationContext());
            }
            Intent intent = getIntent();
            courseId = intent.getStringExtra("courseId");
            courseName = intent.getStringExtra("courseName");

            if (courseId == null || courseId.length() == 0) {
                isCplBulletin = true;
                courseId = intent.getStringExtra("cplId");
                courseName = intent.getStringExtra("cplName");
            }
            Log.d(TAG, "initData: " + courseId);
            setUserAvatar(userImage);
            setToolBarColor();
            setInitialData();
            createLoader();
            mPresenter = new BulletinBoardQuestionPresenter(BulletinBoardQuestionActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initListener() {

    }

    private void createLoader() {
        try {
            swipe_refresh_layout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
//               creates loader
            swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mPresenter.getBulletinQuesFromFirebase(courseId, isCplBulletin);
                }
            });
//            show loader
            swipe_refresh_layout.post(() -> {
                if ((questionsList != null) && (questionsList.size() > 0)) {
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
    public void setToolBarColor() {
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            TextView titleTextView = toolbar.findViewById(R.id.title);
//            titleTextView.setText("("+questionsList.size()+") "+getResources().getString(R.string.discussion_board"));

            if (questionsList != null) {
                if (questionsList.size() == 0) {
                    titleTextView.setText(getResources().getString(R.string.discussion_board));
                } else if (questionsList.size() == 1) {
                    titleTextView.setText("(1) " + getResources().getString(R.string.discussion_board));
                } else {
                    titleTextView.setText("(" + questionsList.size() + ") " + getResources().getString(R.string.discussion_board));
                }
            } else {
                titleTextView.setText(getResources().getString(R.string.discussion_board));
            }

            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                bulletin_appbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setInitialData() {
        ask_ques_hint.setText(getResources().getString(R.string.start_conversation));
        user_name.setText(activeUser.getUserDisplayName());
    }

    private void setUserAvatar(ImageView image) {
        try {
            if ((activeUser.getAvatar() != null) && (!activeUser.getAvatar().isEmpty())) {
                if (activeUser.getAvatar().startsWith("http")) {
                    Picasso.get().load(activeUser.getAvatar())
                            .placeholder(OustSdkTools.getImageDrawable(getResources().getString(R.string.profile_image_loading))).error(OustSdkTools.getImageDrawable(getResources().getString(R.string.maleavatar)))
                            .into(image);
                } else {
                    Picasso.get().load(OustSdkApplication.getContext().getString(R.string.oust_user_avatar_link) + activeUser.getAvatar())
                            .placeholder(OustSdkTools.getImageDrawable(getResources().getString(R.string.profile_image_loading))).error(OustSdkTools.getImageDrawable(getResources().getString(R.string.maleavatar)))
                            .into(image);
                }
            } else {
                Picasso.get().load(getResources().getString(R.string.maleavatar))
                        .placeholder(OustSdkTools.getImageDrawable(getResources().getString(R.string.profile_image_loading))).error(OustSdkTools.getImageDrawable(getResources().getString(R.string.maleavatar)))
                        .into(image);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ask_ques_layout) {
            try {
                showAddQuesPopup();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    private void sendDataToFireBase() {
        long millis = new Date().getTime();
        mPresenter.updateLastUpdatedTime(millis);
        BulletinBoardData bulletinBoardData = new BulletinBoardData();
        bulletinBoardData.setUserId(activeUser.getStudentid());
        bulletinBoardData.setUserKey(Long.parseLong(activeUser.getStudentKey()));
        if (!isCplBulletin) {
            bulletinBoardData.setCourseName(courseName);
            bulletinBoardData.setCourseId(Long.parseLong(courseId));
        } else {
            bulletinBoardData.setCplName(courseName);
            bulletinBoardData.setCplId(Long.parseLong(courseId));
        }
        bulletinBoardData.setQuestion(question);
        bulletinBoardData.setDevicePlatform("android");
        bulletinBoardData.setAddedOnDate(millis);
        bulletinBoardData.setUserDisplayName(activeUser.getUserDisplayName());
        bulletinBoardData.setUserAvatar(activeUser.getAvatar());
        bulletinBoardData.setNumComments(0);
        Log.d(TAG, "sendDataToFireBase: " + bulletinBoardData.getCourseId());
        mPresenter.setDataToFirebase(bulletinBoardData, isCplBulletin);
    }

    int count = 0;
    int size = 0;

    @Override
    public void updateQuestionDataFromFB(DataSnapshot dataSnapshot, String queskey) {
        try {
            count++;
            if (null != dataSnapshot.getValue()) {
                if (!isCplBulletin) {
                    long millis = new Date().getTime();
                    mPresenter.updateLastUpdatedTime(millis);
                }
                final Map<String, Object> questionData = (Map<String, Object>) dataSnapshot.getValue();
                if (questionData != null) {
                    bulletinBoardQuestionData = new BulletinBoardData();
                    bulletinBoardQuestionData.setQuesKey(queskey);
                    final Map<String, Object> questionDataMap = questionData;
                    if (questionDataMap.get("addedOnDate") != null) {
                        bulletinBoardQuestionData.setAddedOnDate((long) questionDataMap.get("addedOnDate"));
                    }
                    if (questionDataMap.get("courseId") != null) {
                        bulletinBoardQuestionData.setCourseId(OustSdkTools.convertToLong(questionDataMap.get("courseId")));
                    }
                    if (questionDataMap.get("courseName") != null) {
                        bulletinBoardQuestionData.setCourseName((String) questionDataMap.get("courseName"));
                    }
                    if (questionDataMap.get("cplId") != null) {
                        bulletinBoardQuestionData.setCplId(OustSdkTools.convertToLong(questionDataMap.get("courseId")));
                    }
                    if (questionDataMap.get("cplName") != null) {
                        bulletinBoardQuestionData.setCplName((String) questionDataMap.get("courseName"));
                    }
                    if (questionDataMap.get("numComments") != null) {
                        bulletinBoardQuestionData.setNumComments((long) questionDataMap.get("numComments"));
                    }
                    if (questionDataMap.get("question") != null) {
                        bulletinBoardQuestionData.setQuestion((String) questionDataMap.get("question"));
                    }
                    if (questionDataMap.get("userAvatar") != null) {
                        bulletinBoardQuestionData.setUserAvatar((String) questionDataMap.get("userAvatar"));
                    }
                    if (questionDataMap.get("userDisplayName") != null) {
                        bulletinBoardQuestionData.setUserDisplayName((String) questionDataMap.get("userDisplayName"));
                    }
                    if (questionDataMap.get("userId") != null) {
                        bulletinBoardQuestionData.setUserId((String) questionDataMap.get("userId"));
                    }
                    if (questionDataMap.get("userKey") != null) {
                        bulletinBoardQuestionData.setUserKey(OustSdkTools.convertToLong(questionDataMap.get("userKey")));
                    }
                    questionsList.add(bulletinBoardQuestionData);
                }
            }
            if ((size > 0) && (count == size)) {
                if ((questionsList != null) && (questionsList.size() > 0)) {
                    setToolBarColor();
                    setAdapter(questionsList);
                    count = 0;
                    size = 0;
                } else {
                    setToolBarColor();
                    setAdapter(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            if ((size > 0) && (count == size - 1)) {
                if ((questionsList != null) && (questionsList.size() > 0)) {
                    setToolBarColor();
                    setAdapter(questionsList);
                    count = 0;
                    size = 0;
                } else {
                    setToolBarColor();
                    setAdapter(null);
                }
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            } else {
                setToolBarColor();
                setAdapter(null);
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firebaseRefClass != null) {
            OustFirebaseTools.getRootRef().child(firebaseRefClass.getFirebasePath()).removeEventListener(firebaseRefClass.getEventListener());
        }
    }

    private void setAdapter(List<BulletinBoardData> bulletinBoardQuestionDatas) {
        final Handler handler = new Handler();
        if ((bulletinBoardQuestionDatas != null) && (bulletinBoardQuestionDatas.size() > 0)) {
            Collections.sort(bulletinBoardQuestionDatas, courseListSortRev);
            if (bulletinQuesAdapter != null) {
                bulletinQuesAdapter.notifyDataChange(bulletinBoardQuestionDatas);
                swipe_refresh_layout.setRefreshing(false);
            } else {
                bulletin_question_recyclerview.setVisibility(View.VISIBLE);
                noquestion_layout.setVisibility(View.GONE);
                bulletinQuesAdapter = new BulletinQuesAdapter(BulletinBoardQuestionActivity.this, bulletinBoardQuestionDatas);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(OustSdkApplication.getContext(), LinearLayoutManager.VERTICAL, false);
                bulletinQuesAdapter.setCplBulletin(isCplBulletin);
                bulletin_question_recyclerview.setLayoutManager(mLayoutManager);
                bulletin_question_recyclerview.setItemAnimator(new DefaultItemAnimator());
                bulletin_question_recyclerview.setAdapter(bulletinQuesAdapter);
                handler.postDelayed(() -> swipe_refresh_layout.setRefreshing(false), 500);

            }
        } else {
            handler.postDelayed(() -> swipe_refresh_layout.setRefreshing(false), 500);
        }
    }

    //    ================================================================================
//    Sorting in Reverse Order
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
//    ===============================================================================================
//    add question popup

    private PopupWindow add_ques_popup;

    private void showAddQuesPopup() {
        View popUpView = getLayoutInflater().inflate(R.layout.bulletin_addques_layout, null);
        add_ques_popup = OustSdkTools.createFullScreenPopUp(popUpView);
//        add_ques_popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        add_ques_popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        final EditText ques_edittext = popUpView.findViewById(R.id.ques_edittext);
//        RelativeLayout ok_layout= (RelativeLayout) popUpView.findViewById(R.id.ok_layout);
        ImageView ok_layout_iv = popUpView.findViewById(R.id.ok_layout_iv);
        OustSdkTools.setImage(ok_layout_iv, getResources().getString(R.string.send));
        TextView ques_popup_heading = popUpView.findViewById(R.id.ques_popup_heading);
//        TextView oktext= (TextView) popUpView.findViewById(R.id.oktext);
        ImageView userImage = popUpView.findViewById(R.id.q_userImage);
//        RelativeLayout line= (RelativeLayout) popUpView.findViewById(R.id.line);
        setUserAvatar(userImage);
        TextView user_name = popUpView.findViewById(R.id.q_user_name);
        user_name.setText(activeUser.getUserDisplayName());
        ques_popup_heading.setText(getResources().getString(R.string.compose).toUpperCase());
        RelativeLayout ques_popup_main_layout = popUpView.findViewById(R.id.ques_popup_main_layout);
        if (OustPreferences.get("toolbarColorCode") != null) {
            int color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
            GradientDrawable drawable = (GradientDrawable) getApplicationContext().getResources().getDrawable(R.drawable.bulletin_ques_background);
            drawable.setStroke(3, color);
            OustSdkTools.setLayoutBackgroudDrawable(ques_popup_main_layout, drawable);
            ok_layout_iv.setColorFilter(color);
//            line.setBackgroundColor(color);
        }
        ques_edittext.setOnLongClickListener(view -> true);


//        setCustomButtonBackground(ok_layout);
//        ok_layout.setFocusable(true);
//        oktext.setText(getResources().getString(R.string.ok_btn"));
        ImageView popup_closebtn = popUpView.findViewById(R.id.close_image);
        ques_edittext.setHint(getResources().getString(R.string.start_conversation));

//        ques_edittext.setOnClickListener(v -> add_ques_popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE));

        ok_layout_iv.setOnClickListener(view -> {
            if (ques_edittext.getText() != null) {
                question = ques_edittext.getText().toString().trim();
                if (!question.isEmpty()) {
                    sendDataToFireBase();
                    hideKeyboard(ques_edittext);
                }
            }
            if ((add_ques_popup.isShowing())) {
                hideKeyboard(ques_edittext);
                add_ques_popup.dismiss();
            }
        });
        popup_closebtn.setOnClickListener(v -> {
            if ((add_ques_popup.isShowing())) {
                hideKeyboard(ques_edittext);
                add_ques_popup.dismiss();
            }
        });
        popUpView.setFocusableInTouchMode(true);
        popUpView.requestFocus();
        popUpView.setOnKeyListener((v, keyCode, event) -> event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK);
    }

    private void setCustomButtonBackground(RelativeLayout button) {
        try {
            if (OustPreferences.get("toolbarColorCode") != null) {
                PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
                final LayerDrawable ld = (LayerDrawable) getApplicationContext().getResources().getDrawable(R.drawable.disclaimer_button_rounded_corer);
                final GradientDrawable d1 = (GradientDrawable) ld.findDrawableByLayerId(R.id.custom_roundedcorner);
                int color;

                color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                Log.e("AIRTEL", "THIS IS preference color" + color);
                d1.setColorFilter(color, mode);
                OustSdkTools.setLayoutBackgroudDrawable(button, ld);
            } else {
                OustSdkTools.setLayoutBackgroud(button, R.drawable.disclaimer_button_rounded_corer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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

    @Override
    public void updateQuestionFromFireBase(DataSnapshot dataSnapshot) {
        try {
            if (null != dataSnapshot.getValue()) {
                final Map<String, Object> allquestionMap = (Map<String, Object>) dataSnapshot.getValue();
                if (allquestionMap != null) {
                    questionsList = new ArrayList<>();
                    size = allquestionMap.size();
                    for (String queskey : allquestionMap.keySet()) {
                        mPresenter.getQuestionsDataFromFirebase(queskey);
                    }
                }
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
