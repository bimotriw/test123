package com.oustme.oustsdk.layoutFour.components.directMessages;


import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.layoutFour.components.directMessages.adapter.DirectMessageListAdapter;
import com.oustme.oustsdk.layoutFour.data.response.directMessageResponse.InboxDataResponse;
import com.oustme.oustsdk.layoutFour.data.response.directMessageResponse.UserMessageList;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class DirectMessageActivity extends AppCompatActivity implements DirectMessageListAdapter.SelectInBoxPosition {

    private ImageView backButton;
    private TextView screenName;
    private RecyclerView inBoxRecycleView;
    private View no_data_layout;
    private ImageView no_image;
    private TextView no_data_content;

    //Branding loader
    private RelativeLayout branding_mani_layout;
    //End

    int color;
    int bgColor;
    ActiveUser activeUser;
    private ArrayList<UserMessageList> userMessageLists = new ArrayList<>();
    private DirectMessageListAdapter inBoxListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.activity_direct_message);

        intView();
        initData();
    }

    private void intView() {
        Toolbar inboxToolbar = findViewById(R.id.toolbar_inbox_layout);
        backButton = findViewById(R.id.back_button);
        screenName = findViewById(R.id.screen_name);
        inBoxRecycleView = findViewById(R.id.inbox_recycler_view);
        no_data_layout = findViewById(R.id.inbox_no_data_layout);
        no_image = no_data_layout.findViewById(R.id.no_image);
        no_data_content = no_data_layout.findViewById(R.id.no_data_content);
        //Branding loader
        branding_mani_layout = findViewById(R.id.branding_main_layout);
        ImageView branding_bg = branding_mani_layout.findViewById(R.id.branding_bg);
        ImageView branding_icon = branding_mani_layout.findViewById(R.id.brand_loader);
        //End

        getColors();

        inboxToolbar.setBackgroundColor(bgColor);
        screenName.setTextColor(color);
        OustResourceUtils.setDefaultDrawableColor(backButton.getDrawable(), color);
        inboxToolbar.setTitle("");
        screenName.setText(getResources().getString(R.string.inbox));
        setSupportActionBar(inboxToolbar);

        try {
            activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            String tenantId = OustPreferences.get("tanentid");

            if (tenantId != null && !tenantId.isEmpty()) {
                File brandingBg = new File(OustSdkApplication.getContext().getFilesDir(),
                        ("oustlearn_" + tenantId.toUpperCase().trim() + "splashScreen"));

                if (brandingBg.exists() && OustPreferences.getAppInstallVariable(IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED)) {
                    Picasso.get().load(brandingBg).into(branding_bg);
                } else {
                    String tenantBgImage = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/bgImage";
                    Picasso.get().load(tenantBgImage).into(branding_bg);
                }

                File brandingLoader = new File(OustSdkApplication.getContext().getFilesDir(), ("oustlearn_" + tenantId.toUpperCase().trim() + "splashIcon"));
                if (brandingLoader.exists()) {
                    Picasso.get().load(brandingLoader).into(branding_icon);
                } else {
                    String tenantIcon = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/icon";
                    Picasso.get().load(tenantIcon).error(getResources().getDrawable(R.drawable.app_icon)).into(branding_icon);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void initData() {
        try {
            activeUser = OustAppState.getInstance().getActiveUser();

            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(DirectMessageActivity.this);
            }
            OustSdkTools.setLocale(DirectMessageActivity.this);

            backButton.setOnClickListener(v -> onBackPressed());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getData() {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                String user_id;
                if (activeUser != null) {
                    user_id = activeUser.getStudentid();
                } else {
                    ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
                    user_id = activeUser.getStudentid();
                }

                String getInBoxData = OustSdkApplication.getContext().getResources().getString(R.string.inBoxGetApi);
                getInBoxData = getInBoxData.replace("{userId}", user_id);

                getInBoxData = HttpManager.getAbsoluteUrl(getInBoxData);
                Log.d("TAG", "getInBoxData:--> " + getInBoxData);

                ApiCallUtils.doNetworkCall(Request.Method.GET, getInBoxData, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            InboxDataResponse inBoxData;
                            if (response.getBoolean("success")) {
                                Gson gson = new Gson();
                                inBoxData = gson.fromJson(response.toString(), InboxDataResponse.class);
                                if (inBoxData.getUserMessageList() != null && inBoxData.getUserMessageList().size() > 0) {
                                    if (inBoxData.getUserMessageList().size() > 0) {
                                        userMessageLists = inBoxData.getUserMessageList();
                                        for (int i = 0; i < inBoxData.getUserMessageList().size(); i++) {
                                            userMessageLists.set(i, inBoxData.getUserMessageList().get(i));
                                        }
//                                        Collections.reverse(userMessageLists);
                                        screenName.setText(getResources().getText(R.string.inbox) + " (" + userMessageLists.size() + ")");
                                        setInBoxData();
                                    }
                                    hideBrandingLoader();
                                } else {
                                    noDataFound();
                                    hideBrandingLoader();
                                }
                            } else {
                                hideBrandingLoader();
                                noDataFound();
                            }
                        } catch (Exception e) {
                            hideBrandingLoader();
                            noDataFound();
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideBrandingLoader();
                        noDataFound();
                    }
                });
            } else {
                hideBrandingLoader();
                OustSdkTools.showToast(getResources().getString(R.string.no_internet_connection));
            }
        } catch (Exception e) {
            hideBrandingLoader();
            noDataFound();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setInBoxData() {
        try {
            no_data_layout.setVisibility(View.GONE);

            inBoxListAdapter = new DirectMessageListAdapter();
            inBoxListAdapter.setInBoxListAdapter(userMessageLists, getApplicationContext(), userMessageLists1 -> {
                if (userMessageLists1.size() == 0) {
                    noSearchDataFound();
                } else {
                    no_data_layout.setVisibility(View.GONE);
                }
            });
            inBoxRecycleView.setLayoutManager(new LinearLayoutManager(this));
            inBoxRecycleView.setItemAnimator(new DefaultItemAnimator());
            inBoxRecycleView.setAdapter(inBoxListAdapter);
        } catch (Exception e) {
            hideBrandingLoader();
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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.leaderboard_menu, menu);

        MenuItem itemFilter = menu.findItem(R.id.action_filter);
        Drawable filterDrawable = getResources().getDrawable(R.drawable.ic_filter);
        itemFilter.setIcon(OustResourceUtils.setDefaultDrawableColor(filterDrawable, color));
        itemFilter.setVisible(false);

        MenuItem itemSort = menu.findItem(R.id.action_sort);
        Drawable sortDrawable = getResources().getDrawable(R.drawable.ic_sort);
        itemSort.setIcon(OustResourceUtils.setDefaultDrawableColor(sortDrawable, color));
        itemSort.setVisible(false);

        MenuItem itemSearch = menu.findItem(R.id.action_search);
        itemSearch.setVisible(true);

        try {
            SearchManager manager = (SearchManager) getApplicationContext().getSystemService(Context.SEARCH_SERVICE);
            SearchView search = (SearchView) itemSearch.getActionView();
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            EditText searchEditText = search.findViewById(R.id.search_src_text);
            searchEditText.setTextColor(getResources().getColor(R.color.primary_text));
            searchEditText.setHintTextColor(getResources().getColor(R.color.unselected_text));

            ImageView searchIcon = search.findViewById(R.id.search_button);
            searchIcon.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.search_oust), color));

            search.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    screenName.setVisibility(View.GONE);
                    itemFilter.setVisible(false);
                }
            });

            search.setOnCloseListener(() -> {
                screenName.setVisibility(View.VISIBLE);
                itemFilter.setVisible(false);
                return false;
            });

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (userMessageLists.size() > 0 && inBoxListAdapter != null) {
                        inBoxListAdapter.getFilter().filter(query);
                    } else {
                        noDataFound();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (userMessageLists.size() > 0 && inBoxListAdapter != null) {
                        inBoxListAdapter.getFilter().filter(newText);
                    } else {
                        noDataFound();
                    }
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    @Override
    public void selectInBoxPosition(UserMessageList userMessageList) {
        try {
            if (OustSdkTools.checkInternetStatus() && userMessageList != null) {
                if (!userMessageList.getRead()) {
                    String user_id;
                    if (activeUser != null) {
                        user_id = activeUser.getStudentid();
                    } else {
                        ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
                        user_id = activeUser.getStudentid();
                    }

                    String getInBoxData = OustSdkApplication.getContext().getResources().getString(R.string.inBoxUpdateReadApi);
                    getInBoxData = getInBoxData.replace("{userMessageId}", String.valueOf(userMessageList.getUserMessageId()));
                    getInBoxData = getInBoxData.replace("{userId}", user_id);

                    getInBoxData = HttpManager.getAbsoluteUrl(getInBoxData);
                    Log.d("TAG", "selectInBoxPosition:--> " + getInBoxData);

                    ApiCallUtils.doNetworkCall(Request.Method.PUT, getInBoxData, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("success")) {
                                    Log.d("TAG", "onResponse:--> Read view updated ");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            OustSdkTools.showToast(getResources().getString(R.string.no_internet_connection));
                        }
                    });
                } else {
                    Log.d("TAG", "selectInBoxPosition: This Message already viewed--> " + userMessageList.getUserMessageId());
                }
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.no_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void redirectToMessageDetailScreen(UserMessageList userMessageList) {
        try {
            if (OustSdkTools.checkInternetStatus() && userMessageList != null) {
                Gson gson = new Gson();
                Intent intent = new Intent(OustSdkApplication.getContext(), DirectMessageDetailActivity.class);
                intent.putExtra("userMessageList", gson.toJson(userMessageList));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.no_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hideBrandingLoader() {
        try {
            branding_mani_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void noDataFound() {
        try {
            no_data_layout.setVisibility(View.VISIBLE);
            no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_feed));
            no_data_content.setText(getResources().getString(R.string.no_data_found));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void noSearchDataFound() {
        try {
            no_data_layout.setVisibility(View.VISIBLE);
            no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_catalogue));
            no_data_content.setText(getResources().getString(R.string.no_search_found));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            getData();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
