package com.oustme.oustsdk.activity.common.noticeBoard.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.MenuItemCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.adapters.NBAllPostAdapter;
import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.NBDeleteListener;
import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.NBPostClickCallBack;
import com.oustme.oustsdk.activity.common.noticeBoard.data.handlers.NBDataHandler;
import com.oustme.oustsdk.activity.common.noticeBoard.data.handlers.PostViewTracker;
import com.oustme.oustsdk.activity.common.noticeBoard.data.handlers.SubmitNBPostService;
import com.oustme.oustsdk.activity.common.noticeBoard.dialogs.CommentDeleteConfirmationPopup;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.PostViewData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBPostData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBTopicData;
import com.oustme.oustsdk.activity.common.noticeBoard.presenters.NBTopicDetailPresenter;
import com.oustme.oustsdk.activity.common.noticeBoard.view.NBTopicDetailView;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.customviews.CustomSearchView;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustShareTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FIFTEEN_SECOND;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.ONE_SECOND;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.TOOL_BAR_COLOR_CODE;


public class NBTopicDetailActivity extends BaseActivity implements NBTopicDetailView, SearchView.OnQueryTextListener, NBPostClickCallBack, NBDeleteListener {
    String TAG = "NBTopicDetailActivity";
    private static final int WRITE_STORAGE_PERMISSION = 123;
    private NBTopicDetailPresenter mPresenter;
    private Toolbar mToolbar;
    private MenuItem actionSearch;
    private View searchPlate;
    private CustomSearchView newSearchView;
    private ImageView nb_topic_img;
    private RecyclerView all_posts_rv;
    private LinearLayout no_Posts_ll, mLinearLayoutProgressBar;
    private NBAllPostAdapter nbAllPostAdapter;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PostViewData postViewData;
    private String toolbarColorCode;
    private FloatingActionButton mFloatingActionButton;
    private Drawable mBackgroundDrawable;
    private TextView titleTextView = null;
    private View view;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    int PERMISSION_ALL = 1;

    @Override
    protected int getContentView() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
        OustSdkTools.setLocale(NBTopicDetailActivity.this);
        return R.layout.activity_topic_details;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            Log.d(TAG, "onStop: ");
            if (anim_timer != null) {
                anim_timer.cancel();
                anim_timer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initView() {
        Log.d(TAG, "initView: ");
        nb_topic_img = findViewById(R.id.nb_topic_img);
        mToolbar = findViewById(R.id.toolbar);
        all_posts_rv = findViewById(R.id.all_posts_rv);
        no_Posts_ll = findViewById(R.id.no_Posts_ll);
        mLinearLayoutProgressBar = findViewById(R.id.ll_progress);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mFloatingActionButton = findViewById(R.id.floatingAction);
        mBackgroundDrawable = getResources().getDrawable(R.drawable.ic_add);
        mBackgroundDrawable.setColorFilter(getResources().getColor(R.color.white_presseda), PorterDuff.Mode.SRC_IN);
        mFloatingActionButton.setImageDrawable(mBackgroundDrawable);
        setToolbar();

    }

    private void setToolbar() {
        try {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setTitle("");
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                mToolbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initData() {
        mPresenter = new NBTopicDetailPresenter(this);
        mPresenter.getAllPostData();
        checkPermissions();
        toolbarColorCode = OustPreferences.get(TOOL_BAR_COLOR_CODE);
        try {
            if (toolbarColorCode != null) {
                mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(toolbarColorCode)));
            } else {
                mFloatingActionButton.setBackgroundColor(getResources().getColor(R.color.lgreen));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            if (toolbarColorCode != null) {
                int color = Color.parseColor(toolbarColorCode);
                if (mFloatingActionButton.getBackground() != null) {
                    Log.e(TAG, "initData:-->  " + mFloatingActionButton.getBackground());
                    DrawableCompat.setTintList(DrawableCompat.wrap(mFloatingActionButton.getBackground()), ColorStateList.valueOf(color));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkPermissions() {
        if (OustSdkTools.hasPermissions(NBTopicDetailActivity.this, PERMISSIONS)) {

        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    @Override
    protected void initListener() {
        mFloatingActionButton.setOnClickListener(view -> {
            NBTopicData nbTopicData = NBDataHandler.getInstance().getNbTopicData();
            Intent intent = new Intent(NBTopicDetailActivity.this, NBPostCreateActivity.class);
            if (nbTopicData != null) {
                intent.putExtra("NBID", nbTopicData.getId());
                intent.putExtra("nbPostRewardOC", nbTopicData.getNbPostRewardOC());
            }
            startActivity(intent);
        });
    }

    @Override
    public void createLoader() {
        try {
            mLinearLayoutProgressBar.setVisibility(View.VISIBLE);
            if (R.color.Orange != 0 && R.color.LiteGreen != 0) {
                swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void hideLoader() {
        try {
            mLinearLayoutProgressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.alert_menu, menu);
            actionSearch = menu.findItem(R.id.action_search);

            //newSearchView = (CustomSearchView) MenuItemCompat.getActionView(actionSearch);
            newSearchView = (CustomSearchView) actionSearch.getActionView();

            newSearchView.setOnQueryTextListener(this);
            newSearchView.setQueryHint(getResources().getString(R.string.search_text));
            newSearchView.setVisibility(View.VISIBLE);
            newSearchView.requestFocusFromTouch();
            View searchPlate = newSearchView.findViewById(R.id.search_plate);
            searchPlate.setBackgroundColor(OustSdkTools.getColorBack(R.color.black_semi_transparent));
            showSearchIcon(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_search) {
            actionSearch.setVisible(true);
            searchPlate = newSearchView.findViewById(R.id.search_plate);
            searchPlate.setBackgroundColor(OustSdkTools.getColorBack(R.color.black_semi_transparent));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showSearchIcon(boolean visible) {
        try {
            if (visible) {
                actionSearch.setVisible(true);
            } else {
                actionSearch.setVisible(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void updateTopicBanner(String imageUrl) {
        try {
            Picasso.get().load(imageUrl).into(nb_topic_img, new Callback() {
                @Override
                public void onSuccess() {
                    ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .75f, ScaleAnimation.RELATIVE_TO_SELF, .75f);
                    scale.setDuration(400);
                    nb_topic_img.startAnimation(scale);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void setToolbarText(String title) {
        mToolbar.setTitle(title);
        try {
            Field f = mToolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (TextView) f.get(mToolbar);
            titleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            titleTextView.setFocusable(true);
            titleTextView.setFocusableInTouchMode(true);
            titleTextView.requestFocus();
            titleTextView.setSingleLine(true);
            titleTextView.setSelected(true);
            titleTextView.setText(title);
            titleTextView.setMarqueeRepeatLimit(-1);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void OnErrorOccured(String error) {
        Log.e(TAG, "OnErrorOccured: " + error);
        OustSdkTools.showToast(error);
        no_Posts_ll.setVisibility(View.VISIBLE);
        all_posts_rv.setVisibility(View.GONE);
        hideLoader();
    }

    @Override
    public void setOrUpdateAdapter(List<NBPostData> postDataList) {
        hideLoader();
        Log.d(TAG, "setOrUpdateAdapter: " + postDataList.size());
        if (nbAllPostAdapter == null) {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            all_posts_rv.setLayoutManager(mLayoutManager);
            nbAllPostAdapter = new NBAllPostAdapter(NBTopicDetailActivity.this, postDataList, this);
            all_posts_rv.setVisibility(View.VISIBLE);
            all_posts_rv.setAdapter(nbAllPostAdapter);

            PostViewTracker viewTracker = new PostViewTracker();
            viewTracker.setRecyclerView(all_posts_rv);
            viewTracker.setNbPostClickCallBack(NBTopicDetailActivity.this);
            viewTracker.startTracking();

        } else {
            nbAllPostAdapter.notifyListChnage(postDataList);
        }
    }

    @Override
    public void startApiCalls() {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitNBPostService.class);
        OustSdkApplication.getContext().startService(intent);
    }

    @Override
    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPostViewed(PostViewData postViewData) {
        if (mPresenter != null) {
            mPresenter.addViewPostData(postViewData);
        }
    }

    @Override
    public void onPostViewed(int position) {
        if (nbAllPostAdapter != null) {
            nbAllPostAdapter.onNBPostViewedInScroll(position);
        }
    }

    @Override
    public void onPostLike(PostViewData postViewData) {
        if (mPresenter != null) {
            mPresenter.sendPostLikeData(postViewData);
        }
    }

    @Override
    public void onPostComment(PostViewData postViewData) {
        if (mPresenter != null) {
            mPresenter.sendPostCommentData(postViewData);
        }
    }

    @Override
    public void onPostCommentDelete(PostViewData postViewData) {
        new CommentDeleteConfirmationPopup(NBTopicDetailActivity.this, postViewData, this);
    }

    @Override
    public void onPostShare(PostViewData postViewData, View view) {
        if (mPresenter != null) {
            shareDetails(postViewData, view);
        }
    }

    @Override
    public void onRequestPermissions(PostViewData postViewData, View view) {
        this.postViewData = postViewData;
        this.view = view;
        if (ContextCompat.checkSelfPermission(NBTopicDetailActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NBTopicDetailActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, WRITE_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            switch (requestCode) {
                case WRITE_STORAGE_PERMISSION: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        shareDetails(postViewData, view);
                    } else {
                        OustSdkTools.showToast("Please provide permissions to get going !");
                    }
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    boolean isWentToCreatePost = false;
    private Timer anim_timer;

    private Handler anim_Handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            try {
                Log.d(TAG, "handleMessage: ");
                anim_timer.cancel();
                anim_timer = null;
                mPresenter.getAllPostData();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (OustStaticVariableHandling.getInstance().isNewPostcreated()) {
                OustStaticVariableHandling.getInstance().setNewPostcreated(false);
                anim_timer = new Timer();
                anim_timer.scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        Log.d(TAG, "NB - Inside handelr");
                        try {
                            if (OustStaticVariableHandling.getInstance().isNbStateChanged()) {
                                OustStaticVariableHandling.getInstance().setNbStateChanged(false);
                                Message message = anim_Handler.obtainMessage(1, "Start");
                                message.sendToTarget();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                }, 0, ONE_SECOND);
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (nbAllPostAdapter != null) {
                        mPresenter.getAllPostData();
                    }
                }
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*private void getUserNBTopicData() {
        Log.e(TAG, "inside getUserCourses() ");
        try {
            List<NBTopicData> nbTopicDataArrayList = new ArrayList<>();
            final String message1 = "/landingPage/" + activeUser.getStudentKey() + "/noticeBoard";
            ValueEventListener myListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        Log.d(TAG, "onDataChange: getUserNBTopicData");
                        int n=0;
                        if (dataSnapshot.getValue() != null) {
                            Object o1 = dataSnapshot.getValue();
                            if (o1.getClass().equals(ArrayList.class)) {
                                List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                                for (int i = 0; i < learningList.size(); i++) {
                                    Map<String, Object> lpMap = (Map<String, Object>) learningList.get(i);
                                    if (lpMap != null) {
                                        NBTopicData nbTopicData = new NBTopicData();
                                        nbTopicData.init(lpMap);
                                        n++;
                                        nbTopicDataArrayList.add(nbTopicData);
                                        //updateNBDataMap(nbTopicData);
                                    }
                                }
                            } else if (o1.getClass().equals(HashMap.class)) {
                                Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                for (String nBKey : lpMainMap.keySet()) {
                                    Map<String, Object> lpMap = (Map<String, Object>) lpMainMap.get(nBKey);
                                    if (lpMap != null) {
                                        NBTopicData nbTopicData = new NBTopicData();
                                        nbTopicData.init(lpMap);
                                        n++;
                                        nbTopicDataArrayList.add(nbTopicData);
                                        //updateNBDataMap(nbTopicData);
                                    }
                                }
                            }

                            //getAllNBTopicData();
                            *//*for(NBTopicData nbTopicData:nbTopicDataArrayList) {
                                try {
                                    int m=0;
                                    final String message = "/noticeBoard/noticeBoard" + nbTopicData.getId();
                                    ValueEventListener myListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            try {
                                                Log.d(TAG, "onDataChange: getAllNBTopicData");
                                                if (dataSnapshot.getValue() != null) {
                                                    Object o1 = dataSnapshot.getValue();
                                                    Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                                    if (lpMainMap.containsKey("nbId")) {
                                                        //nbTopicDataArrayList.add(nbTopicData);
                                                        m++;
                                                    }

                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            Log.d(TAG, "onCancelled: " + message);
                                        }
                                    };
                                    DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
                                    Query query = gameHistoryRef.orderByChild("addedOn");
                                    query.keepSynced(true);
                                    query.addValueEventListener(myListener);
                                    OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myListener, message));
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                            }*//*

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d(TAG, "onCancelled: "+message1);
                }
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message1);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(myListener);

            //courseFirebaseRefClass = new FirebaseRefClass(myassessmentListener, message);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myListener, message1));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/


    private void shareDetails(PostViewData postViewData, View view) {
        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            this.postViewData = postViewData;
            this.view = view;
            ActivityCompat.requestPermissions(NBTopicDetailActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, WRITE_STORAGE_PERMISSION);
        } else {
            OustShareTools.share(NBTopicDetailActivity.this, OustSdkTools.getInstance().getScreenShot(view), "");
            mPresenter.sendPostShareData(postViewData);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onDelete(PostViewData postViewData) {
        mPresenter.deletePostComment(postViewData);
    }

    @Override
    public void onDeleteCancel() {

    }
}
