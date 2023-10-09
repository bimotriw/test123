package com.oustme.oustsdk.layoutFour.newnoticeBoard.activity;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.ONE_SECOND;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.TOOL_BAR_COLOR_CODE;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.activity.NBPostCreateActivity;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.customviews.CustomSearchView;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters.NewNBAllPostAdapter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters.NewNBMembersListAdapter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewNBDeleteListener;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewNBPostClickCallBack;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers.NewNBDataHandler;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers.NewPostViewTracker;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers.NewSubmitNBPostService;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.dialogs.NewCommentDeleteConfirmationPopup;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBMemberData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBPostData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBTopicData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.presenters.NewNBTopicDetailPresenter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.view.NewNBTopicDetailView;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustShareTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;

public class NewNBTopicDetailActivity extends BaseActivity implements NewNBTopicDetailView, SearchView.OnQueryTextListener, NewNBPostClickCallBack, NewNBDeleteListener {
    String TAG = "NewNBTopicDetailActivity";
    private static final int WRITE_STORAGE_PERMISSION = 123;
    private NewNBTopicDetailPresenter mPresenter;
    private MenuItem actionSearch;
    private View searchPlate;
    private CustomSearchView newSearchView;
    private ImageView nb_topic_img;
    private RecyclerView all_posts_rv, members_list;
    private LinearLayout no_Posts_ll, mLinearLayoutProgressBar, coins_layout, members_icon;
    //    RelativeLayout mLinearLayoutProgressBar;
    private NewNBAllPostAdapter nbAllPostAdapter;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewPostViewData postViewData;
    private String toolbarColorCode;
    private FloatingActionButton mFloatingActionButton;
    private Drawable mBackgroundDrawable;
    private TextView titleTextView = null, text_title, total_users, title_posts, no_data;
    private View view;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    int PERMISSION_ALL = 1;
    ProgressBar progress_bar_nb_topic_list;
    Toolbar toolbar;
    TextView screen_name, add_post_here, coins_val, see_all;
    ImageView back_button, text_description;
    private int color;
    private int bgColor;
    GifImageView notification_gif_loader;
    NewNBTopicData nbTopicData;
    RelativeLayout members_layout;
    ArrayList<NewNBMemberData> nbMemberDataList = new ArrayList<NewNBMemberData>();
    NewNBMembersListAdapter adapterMember;
    List<NewNBPostData> postDataList1 = new ArrayList<>();
    String description = "";
    Dialog popupWindowHint;

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
        OustSdkTools.setLocale(NewNBTopicDetailActivity.this);
        return R.layout.activity_topic_details_2;
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
            if (nbAllPostAdapter != null)
                nbAllPostAdapter.closePopWindow();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initView() {
        Log.d(TAG, "initView: ");
        getColors();
        nb_topic_img = findViewById(R.id.nb_topic_img);
        text_title = findViewById(R.id.text_title);
        toolbar = findViewById(R.id.toolbar_notification_layout);
        all_posts_rv = findViewById(R.id.all_posts_rv);
        members_list = findViewById(R.id.members_list);
        no_Posts_ll = findViewById(R.id.no_Posts_ll);
        coins_layout = findViewById(R.id.coins_layout);
        total_users = findViewById(R.id.total_users);
        mLinearLayoutProgressBar = findViewById(R.id.ll_progress);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mFloatingActionButton = findViewById(R.id.floatingAction);
        progress_bar_nb_topic_list = findViewById(R.id.progress_bar_nb_topic_list);
        back_button = findViewById(R.id.back_button);
        screen_name = findViewById(R.id.screen_name);
        title_posts = findViewById(R.id.title_posts);
        add_post_here = findViewById(R.id.add_post_here);
        notification_gif_loader = findViewById(R.id.notification_gif_loader);
        coins_val = findViewById(R.id.coins_val);
        see_all = findViewById(R.id.see_all);
        members_icon = findViewById(R.id.members_icon);
        members_layout = findViewById(R.id.members_layout);
        no_data = findViewById(R.id.no_data);
        text_description = findViewById(R.id.text_description);
        notification_gif_loader.setVisibility(View.VISIBLE);
        mBackgroundDrawable = getResources().getDrawable(R.drawable.ic_add);
        mBackgroundDrawable.setColorFilter(getResources().getColor(R.color.white_presseda), PorterDuff.Mode.SRC_IN);
        mFloatingActionButton.setImageDrawable(mBackgroundDrawable);
        setToolbar();

    }

    private void setToolbar() {
        try {
            toolbar.setBackgroundColor(Color.WHITE);
            screen_name.setTextColor(color);
            screen_name.setText(getResources().getText(R.string.notice_board));
            OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);

//            setSupportActionBar(toolbar);
//            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowCustomEnabled(true);
//            getSupportActionBar().setTitle("");
//            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
//            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
//                toolbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
//            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setData(NewNBTopicData nbTopicData) {
        if (nbTopicData != null) {
//            total_users.setText("" + nbTopicData.getNo_of_members());
//            if (nbTopicData.getNbPostRewardOC()!= null)
            if (nbTopicData.getNbPostRewardOC() != 0) {
                coins_layout.setVisibility(View.VISIBLE);
                coins_val.setText("" + nbTopicData.getNbPostRewardOC());
            } else {
                coins_layout.setVisibility(View.GONE);
            }

            if (nbTopicData.getDescription() != null && !nbTopicData.getDescription().equalsIgnoreCase("null") && nbTopicData.getDescription().trim().length() > 0) {
                description = nbTopicData.getDescription();
                text_description.setVisibility(View.VISIBLE);
            } else {
                text_description.setVisibility(View.GONE);
            }
//            coins_val.setText(nbTopicData);
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
        mPresenter = new NewNBTopicDetailPresenter(this);
        mPresenter.getAllPostData();
        Log.d("NBTopicDetailed01-->", "" + mPresenter);
        callMembers();
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

    private void callMembers() {
        try {
            long nbId = getIntent().getLongExtra("nbId", 0);
            String nb_members_url = "";
            nb_members_url = "noticeBoard/getNbDistributedUsers/{nbId}";
            nb_members_url = nb_members_url.replace("{nbId}", String.valueOf(nbId));
//                nb_members_url = nb_members_url + (pageNo + 1);
            nb_members_url = HttpManager.getAbsoluteUrl(nb_members_url);

            Log.d(TAG, "getData: URL for Members0:" + nb_members_url);
            try {
                ApiCallUtils.doNetworkCall(Request.Method.GET, nb_members_url, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            try {
                                JSONArray jArray1 = (JSONArray) response.getJSONArray("userDataList");
                                for (int i = 0; i < jArray1.length(); i++) {
                                    JSONObject jsonObj = jArray1.getJSONObject(i);
                                    NewNBMemberData newNBMemberData = new NewNBMemberData();
                                    newNBMemberData.setFname(jsonObj.getString("fname"));
                                    newNBMemberData.setLname(jsonObj.getString("lname"));
                                    newNBMemberData.setStudentid(jsonObj.getString("studentid"));
                                    newNBMemberData.setDepartment(jsonObj.getString("department"));
                                    newNBMemberData.setCity(jsonObj.getString("city"));
                                    newNBMemberData.setState(jsonObj.getString("state"));
                                    newNBMemberData.setCountry(jsonObj.getString("country"));
                                    newNBMemberData.setRole(jsonObj.getString("role"));
                                    newNBMemberData.setEmail(jsonObj.getString("email"));
                                    newNBMemberData.setDepartment(jsonObj.getString("department"));
                                    newNBMemberData.setAvatar(jsonObj.getString("avatar"));

                                    nbMemberDataList.add(newNBMemberData);
                                }

                                if (jArray1.length() == 0) {
                                    members_layout.setVisibility(View.GONE);
                                }

//                                Log.d(TAG, "onRespon04" + nbMemberDataList.size());
                                if (nbMemberDataList != null) {
                                    try {
                                        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(NewNBTopicDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                        members_list.setLayoutManager(mLayoutManager1);
                                        adapterMember = new NewNBMembersListAdapter(NewNBTopicDetailActivity.this, nbMemberDataList, 2);
                                        members_list.setAdapter(adapterMember);
                                        total_users = findViewById(R.id.total_users);
                                        total_users.setText("" + nbMemberDataList.size());
                                    } catch (Exception e) {
                                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                    }
//                                Log.d(TAG, "onRespon06" + nbMemberDataList);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", "" + error);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkPermissions() {
        if (OustSdkTools.hasPermissions(NewNBTopicDetailActivity.this, PERMISSIONS)) {

        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    @Override
    protected void initListener() {
        nbTopicData = NewNBDataHandler.getInstance().getNbTopicData();

        see_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(NewNBTopicDetailActivity.this, NewNBMembersList.class);
                    intent.putExtra("nbId", nbTopicData.getId());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        });

        text_description.setOnClickListener(v -> showPopupWindowForHint(v, description));

        members_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(NewNBTopicDetailActivity.this, NewNBMembersList.class);
                    intent.putExtra("nbId", nbTopicData.getId());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        });

        add_post_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d("NBTopicDetailed01-->", " " + nbTopicData.getId());
                    Intent intent = new Intent(NewNBTopicDetailActivity.this, NewNBPostCreateActivity.class);
                    if (nbTopicData != null) {
                        intent.putExtra("NBID", nbTopicData.getId());
                        intent.putExtra("nbPostRewardOC", nbTopicData.getNbPostRewardOC());
                    }
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        });

        mFloatingActionButton.setOnClickListener(view -> {
            NewNBTopicData nbTopicData = NewNBDataHandler.getInstance().getNbTopicData();
            Log.d("NBTopicDetailed011-->", " " + nbTopicData.getId());
            Intent intent = new Intent(NewNBTopicDetailActivity.this, NBPostCreateActivity.class);
            if (nbTopicData != null) {
                intent.putExtra("NBID", nbTopicData.getId());
                intent.putExtra("nbPostRewardOC", nbTopicData.getNbPostRewardOC());
            }
            startActivity(intent);
        });
        back_button.setOnClickListener(v -> onBackPressed());
    }

    private void showPopupWindowForHint(View v, String description) {
        try {
            popupWindowHint = new Dialog(NewNBTopicDetailActivity.this, R.style.DialogTheme);
            popupWindowHint.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupWindowHint.setContentView(R.layout.pop_up_layout_hint);
            popupWindowHint.setCancelable(true);
            popupWindowHint.setCanceledOnTouchOutside(true);

            Objects.requireNonNull(popupWindowHint.getWindow()).setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindowHint.show();
            LinearLayout parent_layout = popupWindowHint.findViewById(R.id.parent_layout);
            parent_layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            TextView test2 = popupWindowHint.findViewById(R.id.titleText);
            test2.setTextSize(14);
            TextView got_it = popupWindowHint.findViewById(R.id.got_it);
            got_it.setVisibility(View.GONE);

            if (description != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    test2.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    test2.setText(Html.fromHtml(description));
                }
            }

            ImageView close = popupWindowHint.findViewById(R.id.close);
            close.setOnClickListener(view -> popupWindowHint.dismiss());

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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
            notification_gif_loader.setVisibility(View.GONE);
            mLinearLayoutProgressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showText() {
        try {
            title_posts.setVisibility(View.VISIBLE);
            add_post_here.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.alert_menu_2, menu);
            actionSearch = menu.findItem(R.id.action_search);
            Drawable searchDrawable = getResources().getDrawable(R.drawable.search);
            actionSearch.setIcon(OustResourceUtils.setDefaultDrawableColor(searchDrawable, color));

            MenuItem add = menu.findItem(R.id.action_add);
            Drawable addDrawable = getResources().getDrawable(R.drawable.ic_add_nb);
            add.setIcon(OustResourceUtils.setDefaultDrawableColor(addDrawable, color));

            //newSearchView = (CustomSearchView) MenuItemCompat.getActionView(actionSearch);
            newSearchView = (CustomSearchView) actionSearch.getActionView();

            newSearchView.setOnQueryTextListener(this);
            newSearchView.setQueryHint(getResources().getString(R.string.search_text));
            newSearchView.setVisibility(View.VISIBLE);
            newSearchView.requestFocusFromTouch();
            View searchPlate = newSearchView.findViewById(R.id.search_plate);
            searchPlate.setBackgroundColor(OustSdkTools.getColorBack(R.color.black_semi_transparent));
            showSearchIcon(true);
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
        } else if (item.getItemId() == R.id.action_add) {
            try {
                NewNBTopicData nbTopicData = NewNBDataHandler.getInstance().getNbTopicData();
                Log.d("NBTopicDetailed0-->", "" + nbTopicData.getId());
                Intent intent = new Intent(NewNBTopicDetailActivity.this, NewNBPostCreateActivity.class);
                if (nbTopicData != null) {
                    intent.putExtra("NBID", nbTopicData.getId());
                    intent.putExtra("nbPostRewardOC", nbTopicData.getNbPostRewardOC());
                }
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        } else {
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
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
        // do search here
        if (postDataList1 != null && postDataList1.size() > 0) {
            nbAllPostAdapter.getFilter().filter(s);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (postDataList1 != null && postDataList1.size() > 0) {
            nbAllPostAdapter.getFilter().filter(s);
            new Handler().postDelayed(() -> {
                if (nbAllPostAdapter.getItemCount() != 0) {
                    no_data.setVisibility(View.GONE);
                    title_posts.setVisibility(View.VISIBLE);
                    all_posts_rv.setVisibility(View.VISIBLE);
                } else {
                    no_data.setVisibility(View.VISIBLE);
                    title_posts.setVisibility(View.GONE);
                    all_posts_rv.setVisibility(View.GONE);
                }

            }, 2000);
        }
        return false;
    }

    @Override
    public void updateTopicBanner(String imageUrl) {
        try {
//            nb_topic_img.setAlpha(0.4f);
            Picasso.get().load(imageUrl).into(nb_topic_img, new Callback() {
                @Override
                public void onSuccess() {
//                    nb_topic_img.setAlpha(1.0f);
//                    ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .75f, ScaleAnimation.RELATIVE_TO_SELF, .75f);
//                    scale.setDuration(400);
//                    nb_topic_img.startAnimation(scale);
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
        toolbar.setTitle("");
        try {
            text_title.setText(title);
            OustPreferences.save("saveTitle", title);
//            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
//            f.setAccessible(true);
//            titleTextView = (TextView) f.get(toolbar);
//            titleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//            titleTextView.setFocusable(true);
//            titleTextView.setFocusableInTouchMode(true);
//            titleTextView.requestFocus();
//            titleTextView.setSingleLine(true);
//            titleTextView.setSelected(true);
////            titleTextView.setText(title);
//            titleTextView.setMarqueeRepeatLimit(-1);
        } catch (Exception e) {
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
    public void setOrUpdateAdapter(ArrayList<NewNBPostData> postDataList) {
        hideLoader();
        Log.d(TAG, "setOrUpdateAdapterSize: " + postDataList.size());
        postDataList1 = postDataList;
        if (nbAllPostAdapter == null) {
            Log.d(TAG, "setOrUpdateAdapterOld: " + postDataList.size());
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            all_posts_rv.setLayoutManager(mLayoutManager);
            nbAllPostAdapter = new NewNBAllPostAdapter(NewNBTopicDetailActivity.this, postDataList, this);
            all_posts_rv.setVisibility(View.VISIBLE);
            all_posts_rv.setAdapter(nbAllPostAdapter);

            NewPostViewTracker viewTracker = new NewPostViewTracker();
            viewTracker.setRecyclerView(all_posts_rv);
            viewTracker.setNbPostClickCallBack(NewNBTopicDetailActivity.this);
            viewTracker.startTracking();
            if (postDataList.size() == 0) {
                title_posts.setVisibility(View.GONE);
                add_post_here.setVisibility(View.VISIBLE);
            } else {
                title_posts.setVisibility(View.VISIBLE);
                add_post_here.setVisibility(View.GONE);
            }

        } else {
            try {
//                postDataList.clear();
//                LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//                all_posts_rv.setLayoutManager(mLayoutManager);
//                nbAllPostAdapter = new NewNBAllPostAdapter(NewNBTopicDetailActivity.this, postDataList, this);
//                all_posts_rv.setVisibility(View.VISIBLE);
//                all_posts_rv.setAdapter(nbAllPostAdapter);
//
//                NewPostViewTracker viewTracker = new NewPostViewTracker();
//                viewTracker.setRecyclerView(all_posts_rv);
//                viewTracker.setNbPostClickCallBack(NewNBTopicDetailActivity.this);
//                viewTracker.startTracking();

//                Log.d(TAG, "setOrUpdateAdapterUpdata: " + postDataList.size());
//                postDataList1 = null;
//                postDataList1.addAll(0, postDataList);
//                nbAllPostAdapter.notifyItemRangeInserted(0, postDataList1.size());
                nbAllPostAdapter.notifyListChnage(postDataList);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    @Override
    public void setOrUpdateAdapter2(List<NewNBPostData> postDataList) {

    }

    @Override
    public void startApiCalls() {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, NewSubmitNBPostService.class);
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
    public void onPostViewed(NewPostViewData postViewData) {
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
    public void onPostLike(NewPostViewData postViewData) {
        if (mPresenter != null) {
            mPresenter.sendPostLikeData(postViewData);
        }
    }

    @Override
    public void onPostComment(NewPostViewData postViewData) {
        if (mPresenter != null) {
            mPresenter.sendPostCommentData(postViewData);
        }
    }

    @Override
    public void onPostCommentDelete(NewPostViewData postViewData) {
        new NewCommentDeleteConfirmationPopup(NewNBTopicDetailActivity.this, postViewData, this);
    }

    @Override
    public void onPostShare(NewPostViewData postViewData, View view) {
        if (mPresenter != null) {
            shareDetails(postViewData, view);
        }
    }

    @Override
    public void onRequestPermissions(NewPostViewData postViewData, View view) {
        this.postViewData = postViewData;
        this.view = view;
        if (ContextCompat.checkSelfPermission(NewNBTopicDetailActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NewNBTopicDetailActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, WRITE_STORAGE_PERMISSION);
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
                if (add_post_here.getVisibility() == View.VISIBLE) {
                    try {
                        Intent intent = new Intent(context, NewNBTopicDetailActivity.class);
                        intent.putExtra("nbId", nbTopicData.getId());
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                } else {
                    if (nbAllPostAdapter != null) {
                        mPresenter.getAllPostData();
                    }
                    anim_timer = new Timer();
                    anim_timer.scheduleAtFixedRate(new TimerTask() {
                        public void run() {
                            Log.d(TAG, "NewNB - Inside handelr");
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
            } else {
                if (nbAllPostAdapter != null) {
                    mPresenter.getAllPostData();
                }
            }
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


    private void shareDetails(NewPostViewData postViewData, View view) {
        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            this.postViewData = postViewData;
            this.view = view;
            ActivityCompat.requestPermissions(NewNBTopicDetailActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, WRITE_STORAGE_PERMISSION);
        } else {
            OustShareTools.share(NewNBTopicDetailActivity.this, OustSdkTools.getInstance().getScreenShot(view), "");
            mPresenter.sendPostShareData(postViewData);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onDelete(NewPostViewData postViewData) {
        mPresenter.deletePostComment(postViewData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (nbAllPostAdapter != null)
            nbAllPostAdapter.closePopWindow();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nbAllPostAdapter != null)
            nbAllPostAdapter.closePopWindow();
    }

    @Override
    public void onDeleteCancel() {

    }
}
