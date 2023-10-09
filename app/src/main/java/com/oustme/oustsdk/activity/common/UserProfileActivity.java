package com.oustme.oustsdk.activity.common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.common.UpNewTabFragmentPagerAdapter;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.response.common.FriendProfileResponceAssessmentRow;
import com.oustme.oustsdk.response.common.FriendProfileResponceData;
import com.oustme.oustsdk.response.common.FriendProfileResponceRow;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.filters.BlurBuilder;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "UserProfileActivity";
    private RelativeLayout avatarLayout, detailsLayout, up_close, loader_layout;
    private ImageView user_profile_Avatar, close_imageview, banner_img;
    private TextView up_username_Txt, up_rank_Txt, up_xp_Txt, up_certifct_Txt, rank_text, coins_text, certificate_text;
    private TabLayout tabLayout;
    private ViewPager newViewPager;
    private ProgressBar main_loader;
    private LinearLayout userrank_layout, userxp_layout;
    private AppBarLayout up_appbar;
    private List<FriendProfileResponceRow> friendProfileResponceRowList;

    private UpNewTabFragmentPagerAdapter newTabFragmentPagerAdapter;
    private ActiveUser activeUser;
    private int totalCertificateCount = 0;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_profile);
        initViews();
        initLanding();
//        OustGATools.getInstance().reportPageViewToGoogle(UserProfileActivity.this, "User Profile Page");
    }

    private void initViews() {
        //main_upLayout= (RelativeLayout) findViewById(R.id.main_upLayout);
        //avatarLayout= (RelativeLayout) findViewById(R.id.avatarLayout);
        try {
            OustSdkTools.setLocale(UserProfileActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        banner_img = findViewById(R.id.banner_img);
        user_profile_Avatar = findViewById(R.id.user_profile_Avatar);
        //detailsLayout= (RelativeLayout) findViewById(R.id.detailsLayout);
        up_close = findViewById(R.id.up_close);
        up_username_Txt = findViewById(R.id.up_username_Txt);
        up_rank_Txt = findViewById(R.id.up_rank_Txt);
        up_certifct_Txt = findViewById(R.id.up_certifct_Txt);
        tabLayout = findViewById(R.id.up_tabanim_tabs);
        newViewPager = findViewById(R.id.up_tabanim_viewpager);
        loader_layout = findViewById(R.id.loader_layout);
        main_loader = findViewById(R.id.main_loader);
        userrank_layout = findViewById(R.id.userrank_layout);
        userxp_layout = findViewById(R.id.userxp_layout);
        up_xp_Txt = findViewById(R.id.up_xp_Txt);
        up_appbar = findViewById(R.id.up_appbar);
        close_imageview = findViewById(R.id.close_imageview);
        rank_text = findViewById(R.id.rank_text);
        coins_text = findViewById(R.id.coins_text);
        certificate_text = findViewById(R.id.certificate_text);

        setStrings();
        OustSdkTools.setImage(close_imageview, getResources().getString(R.string.closesymbol));
        up_close.setOnClickListener(this);
    }

    private void setStrings() {
        rank_text.setText(OustStrings.getString("rank"));
        //coins_text.setText(OustStrings.getString("coins_text"));
        coins_text.setText(OustStrings.getString("score_text"));
        certificate_text.setText(OustStrings.getString("certificates_text"));
    }

    public void initLanding() {
        try {
            activeUser = OustAppState.getInstance().getActiveUser();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                HttpManager.setBaseUrl();
                OustFirebaseTools.initFirebase();
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            }
            setToolBarColor();
            setStartingLoader();
            Intent CallingIntent = getIntent();
            String avatar = CallingIntent.getStringExtra("avatar");
            String xp = CallingIntent.getStringExtra("xp");
            String rank = CallingIntent.getStringExtra("rank");
            String name = CallingIntent.getStringExtra("name");
            setUserData(avatar, name, rank, xp);
            String studentId = CallingIntent.getStringExtra("studentId");
            getAnalyticsData(studentId);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setToolBarColor() {
        try {
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                up_appbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setStartingLoader() {
        try {
            Animation rotateAnim = AnimationUtils.loadAnimation(UserProfileActivity.this, R.anim.rotate_anim);
            main_loader.startAnimation(rotateAnim);
            loader_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            loader_layout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void hideOustLoginLoader() {
        try {
            loader_layout.setVisibility(View.GONE);
            main_loader.clearAnimation();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void setUserData(String avatar, String name, String rank, String xp) {
        try {
            up_username_Txt.setText(name);
            if ((rank != null) && (!rank.isEmpty())) {
                userrank_layout.setVisibility(View.VISIBLE);
                up_rank_Txt.setText("" + rank);
            }
            if ((xp != null) && (!xp.isEmpty())) {
                userxp_layout.setVisibility(View.VISIBLE);
                up_xp_Txt.setText("" + xp);
            }
            if ((avatar != null) && (!avatar.isEmpty())) {
                BitmapDrawable bd = OustSdkTools.getImageDrawable(getResources().getString(R.string.profile_image));
                BitmapDrawable bd_loading = OustSdkTools.getImageDrawable(getResources().getString(R.string.profile_image_loading));
                if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                    Picasso.get()
                            .load(avatar)
                            .placeholder(bd_loading).error(bd)
                            .into(user_profile_Avatar);
                    Picasso.get()
                            .load(avatar)
                            .placeholder(bd_loading).error(bd)
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    setBlurBg(bitmap);
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                } else {
                    Picasso.get()
                            .load(avatar)
                            .placeholder(bd_loading).error(bd).networkPolicy(NetworkPolicy.OFFLINE)
                            .into(user_profile_Avatar);
                    Picasso.get()
                            .load(avatar)
                            .placeholder(bd_loading).error(bd).networkPolicy(NetworkPolicy.OFFLINE)
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    setBlurBg(bitmap);
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getAnalyticsData(String studentId) {
        try {
            String getPointsUrl = OustSdkApplication.getContext().getResources().getString(R.string.friendprofilefetch_url);
            getPointsUrl = getPointsUrl.replace("{studentId}", studentId);
            try {
                getPointsUrl = HttpManager.getAbsoluteUrl(getPointsUrl);

                ApiCallUtils.doNetworkCall(Request.Method.GET, getPointsUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Gson gson = new Gson();
//                        FriendProfileResponceData friendProfileResponceData=gson.fromJson(response.toString(),FriendProfileResponceData.class);
//                        gotResponceData(friendProfileResponceData);
                        extractData(response);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        gotResponceData(null);
                    }
                });


                /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, getPointsUrl, OustSdkTools.getRequestObject(null), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Gson gson = new Gson();
//                        FriendProfileResponceData friendProfileResponceData=gson.fromJson(response.toString(),FriendProfileResponceData.class);
//                        gotResponceData(friendProfileResponceData);
                        extractData(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        gotResponceData(null);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        try {
                            params.put("api-key", OustPreferences.get("api_key"));
                            params.put("org-id", OustPreferences.get("tanentid"));
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                        return params;
                    }
                };
                jsonObjReq.setShouldCache(false);
                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
                OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void extractData(JSONObject response) {
        Log.d(TAG, "extractData: " + response.toString());
        friendProfileResponceRowList = new ArrayList<>();
        if (response != null) {
            if (response.optBoolean("success")) {
                JSONArray courseArray = response.optJSONArray("courses");
                if (courseArray != null) {
                    for (int i = 0; i < courseArray.length(); i++) {
                        JSONObject j = courseArray.optJSONObject(i);
                        if (j != null) {
                            FriendProfileResponceRow friendProfileResponceRow = new FriendProfileResponceRow();
                            friendProfileResponceRow.setType("COURSE");
                            friendProfileResponceRow.setBgImg(j.optString("bgImg"));
                            friendProfileResponceRow.setIcon(j.optString("icon"));
                            friendProfileResponceRow.setRowId(j.optInt("courseId"));
                            friendProfileResponceRow.setRowName(j.optString("courseName"));
                            if (j.optString("userCompletionTime") != null && !j.optString("userCompletionTime").isEmpty() && !j.optString("userCompletionTime").equals("null")) {
                                totalCertificateCount++;
                                friendProfileResponceRow.setUserCompletionTime(j.optString("userCompletionTime"));
                            } else {
                                friendProfileResponceRow.setUserCompletionTime("0");
                            }
                            friendProfileResponceRowList.add(friendProfileResponceRow);
                        }
                    }
                }
                JSONArray assessmentArray = response.optJSONArray("assessments");
                if (assessmentArray != null) {
                    for (int i = 0; i < assessmentArray.length(); i++) {
                        JSONObject j = assessmentArray.optJSONObject(i);
                        if (j != null) {
                            FriendProfileResponceRow friendProfileResponceRow = new FriendProfileResponceRow();
                            friendProfileResponceRow.setType("ASSESSMENT");
                            friendProfileResponceRow.setBanner(j.optString("banner"));
                            friendProfileResponceRow.setIcon(j.optString("icon"));
                            friendProfileResponceRow.setRowId(j.optInt("assessmentId"));
                            friendProfileResponceRow.setRowName(j.optString("name"));
                            friendProfileResponceRow.setLogo(j.optString("logo"));
                            if (j.optString("userCompletionTime") != null && !j.optString("userCompletionTime").isEmpty() && !j.optString("userCompletionTime").equals("null")) {
                                totalCertificateCount++;
                                friendProfileResponceRow.setUserCompletionTime(j.optString("userCompletionTime"));
                            } else {
                                friendProfileResponceRow.setUserCompletionTime("0");
                            }
                            friendProfileResponceRowList.add(friendProfileResponceRow);
                        }
                    }
                }

            } else {
                if ((response.optString("error") != null) && (!response.optString("error").isEmpty())) {
                    OustSdkTools.showToast(response.optString("error"));
                }
            }
        } else {
            OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));
        }
        hideOustLoginLoader();
        showTab();
    }


    public void gotResponceData(FriendProfileResponceData friendProfileResponceData) {
        try {
            if (friendProfileResponceData != null) {
                if (!friendProfileResponceData.isSuccess()) {
                    if ((friendProfileResponceData.getError() != null) && (!friendProfileResponceData.getError().isEmpty())) {
                        OustSdkTools.showToast(friendProfileResponceData.getError());
                    }
                }
            } else {
                OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));
            }
            hideOustLoginLoader();
            //showTab(friendProfileResponceData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showTab() {
        try {
            up_certifct_Txt.setText("" + totalCertificateCount);
            tabLayout.addTab(tabLayout.newTab().setCustomView(getTabIndicator(OustStrings.getString("courses_text"), 0)));
            //tabLayout.addTab(tabLayout.newTab().setCustomView(getTabIndicator(OustStrings.getString("challenges_text"),1)));
            Collections.sort(friendProfileResponceRowList, courseListSortRev);
            //Collections.sort(friendProfileResponceData.getAssessments(), assessmentListRevSort);
            newTabFragmentPagerAdapter = new UpNewTabFragmentPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), friendProfileResponceRowList);
            newViewPager.setAdapter(newTabFragmentPagerAdapter);
            newViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    newViewPager.setCurrentItem(tab.getPosition(), true);
                    ((TextView) tabLayout.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tabText)).setTextColor(OustSdkTools.getColorBack(R.color.Gray));
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    ((TextView) tabLayout.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tabText)).setTextColor(OustSdkTools.getColorBack(R.color.DarkGray));
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public Comparator<FriendProfileResponceRow> courseListSortRev = new Comparator<FriendProfileResponceRow>() {
        public int compare(FriendProfileResponceRow s1, FriendProfileResponceRow s2) {

            if (s1.getUserCompletionTime() != null && !s1.getUserCompletionTime().isEmpty() && s2.getUserCompletionTime() != null && !s2.getUserCompletionTime().isEmpty()) {
                return s2.getUserCompletionTime().compareTo(s1.getUserCompletionTime());
            }
            return -1;
        }
    };
    public Comparator<FriendProfileResponceAssessmentRow> assessmentListRevSort = new Comparator<FriendProfileResponceAssessmentRow>() {
        public int compare(FriendProfileResponceAssessmentRow s1, FriendProfileResponceAssessmentRow s2) {
            if (s1.getUserCompletionTime() != null && s2.getUserCompletionTime() != null) {
                String source1 = s1.getUserCompletionTime().toUpperCase();
                String source2 = s2.getUserCompletionTime().toUpperCase();
                return source2.compareTo(source1);
            } else {
                return 0;
            }
        }
    };

    private View getTabIndicator(String title, int no) {
        View view = LayoutInflater.from(this).inflate(R.layout.newlanding_tab, null);
        TextView tv = view.findViewById(R.id.tabText);
        if (no == 0) {
            tv.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
        } else {
            tv.setTextColor(OustSdkTools.getColorBack(R.color.DarkGray));
        }
        tv.setText(title);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.up_close) {
            UserProfileActivity.this.finish();
        }
    }

    public void setBlurBg(Bitmap bitmap) {
        try {
            Bitmap blurBitmap = null;
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int bitmapWidth = bitmap.getWidth();
            int height = getResources().getDimensionPixelSize(R.dimen.oustlayout_dimen180);
            int availHeight = height * bitmapWidth / scrWidth;
            if (availHeight < bitmap.getHeight()) {
                if (bitmapWidth > scrWidth) {
                    Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, scrWidth, availHeight, null, false);
                    blurBitmap = BlurBuilder.blur(UserProfileActivity.this, newBitmap, scrWidth, height);
                } else {
                    Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, availHeight, null, false);
                    blurBitmap = BlurBuilder.blur(UserProfileActivity.this, newBitmap, scrWidth, height);
                }
            } else {
                blurBitmap = BlurBuilder.blur(UserProfileActivity.this, bitmap, scrWidth, height);
            }
            banner_img.setImageBitmap(blurBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}
