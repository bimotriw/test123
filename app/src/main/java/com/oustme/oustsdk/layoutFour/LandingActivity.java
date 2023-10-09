package com.oustme.oustsdk.layoutFour;

import static com.oustme.oustsdk.tools.OustSdkTools.removeAllReminderNotification;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_LANG_SELECTED;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.ORG_ID_USER_ID_APP_TUTORIAL_VIEWED;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk._utils.BaseActivity;
import com.oustme.oustsdk.activity.common.CplIntroActivity;
import com.oustme.oustsdk.activity.common.FormFillingActivity;
import com.oustme.oustsdk.activity.common.apptutorial.AppTutorialActivity;
import com.oustme.oustsdk.activity.common.languageSelector.LanguageSelectionActivity;
import com.oustme.oustsdk.activity.common.noticeBoard.activity.NBCommentActivity;
import com.oustme.oustsdk.activity.common.noticeBoard.activity.NBPostDetailsActivity;
import com.oustme.oustsdk.activity.common.noticeBoard.data.handlers.SubmitNBPostService;
import com.oustme.oustsdk.activity.common.todoactivity.CitySelection;
import com.oustme.oustsdk.calendar_ui.ui.CalendarScreen;
import com.oustme.oustsdk.customviews.CustomSearchView;
import com.oustme.oustsdk.feed_ui.services.FeedUpdatingServices;
import com.oustme.oustsdk.feed_ui.ui.ImageFeedDetailScreen;
import com.oustme.oustsdk.feed_ui.ui.PublicVideoFeedCardScreen;
import com.oustme.oustsdk.feed_ui.ui.VideoCardDetailScreen;
import com.oustme.oustsdk.firebase.common.CplCollectionData;
import com.oustme.oustsdk.firebase.common.FirebaseDataTools;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.layoutFour.components.directMessages.DirectMessageActivity;
import com.oustme.oustsdk.layoutFour.components.leaderBoard.CommonLeaderBoardActivity;
import com.oustme.oustsdk.layoutFour.components.userOverView.ActiveUserViewModel;
import com.oustme.oustsdk.layoutFour.data.Navigation;
import com.oustme.oustsdk.layoutFour.data.ToolbarItem;
import com.oustme.oustsdk.layoutFour.data.ToolbarModel;
import com.oustme.oustsdk.layoutFour.navigationFragments.AccountFragment;
import com.oustme.oustsdk.layoutFour.navigationFragments.CatalogueFragment;
import com.oustme.oustsdk.layoutFour.navigationFragments.CloseSearchIcon;
import com.oustme.oustsdk.layoutFour.navigationFragments.HomeNavFragment;
import com.oustme.oustsdk.layoutFour.navigationFragments.LearningFragment;
import com.oustme.oustsdk.layoutFour.navigationFragments.NewNoticeBoardFragment;
import com.oustme.oustsdk.layoutFour.navigationFragments.NoticeBoardFragment;
import com.oustme.oustsdk.model.orgSettings.AppTutorial;
import com.oustme.oustsdk.model.response.common.AppTutorialDataModel;
import com.oustme.oustsdk.model.response.common.SpecialFeedModel;
import com.oustme.oustsdk.notification.NotificationActivity;
import com.oustme.oustsdk.notification.model.NotificationResponse;
import com.oustme.oustsdk.request.ClickedFeedData;
import com.oustme.oustsdk.request.ClickedFeedRequestData;
import com.oustme.oustsdk.request.SignOutRequest;
import com.oustme.oustsdk.request.ViewedFeedData;
import com.oustme.oustsdk.response.common.BranchIoResponce;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCardColorScheme;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.room.dto.DTOUserFeedDetails;
import com.oustme.oustsdk.service.FeedBackService;
import com.oustme.oustsdk.service.GCMType;
import com.oustme.oustsdk.service.SubmitCourseCompleteService;
import com.oustme.oustsdk.service.SubmitLevelCompleteService;
import com.oustme.oustsdk.service.SubmitRequestsService;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.BranchTools;
import com.oustme.oustsdk.tools.CplDataHandler;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustLogDetailHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import io.branch.referral.Branch;

public class LandingActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, CloseSearchIcon {

    //View
    Toolbar toolbar;
    ImageView brand_logo;
    TextView tenant_name;
    RelativeLayout content_layout;
    BottomNavigationView navigation;
    Menu navigationMenu;
    MenuItem itemSearch;
    MenuItem itemNotification;
    MenuItem itemCalendar;
    MenuItem itemLeaderBoard;
    MenuItem itemInBox;
    MenuItem itemHelp;

    TextView directMessageCount;
    ImageView direMessageImageView;
    RelativeLayout directMessageLayout;
    ConstraintLayout direct_message_main_layout;
    FrameLayout landingActivity_lay;

    //Model
    LandingLayoutViewModel landingLayoutViewModel;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    //End

    //variable
    int color;
    int bgColor;
    int selectedPos = -1;

    int lastNavPosition = -1;
    boolean doubleBackToExitPressedOnce;
    boolean alertEnable;
    boolean calendarEnable;
    boolean leaderBoardEnable;
    boolean searchEnable;
    boolean directMessageEnable;
    boolean helpEnable;
    static boolean active = false;

    int alertShowAlways;
    int calendarShowAlways;
    int leaderBoardShowAlways;
    int searchShowAlways;
    int directMessageShowAlways;
    int helpShowAlways;

    private ActiveUser activeUser;
    private boolean isAppTutorialShown;
    private String tempIsAppTutorialShown;
    private String tenantName;
    private boolean isAppTutorialShownOnlyLogin;
    private boolean isChildCplDistributed;
    private ArrayList<AppTutorialDataModel> appTutorialDataModelArrayList;

    FragmentManager fm = getSupportFragmentManager();
    HomeNavFragment homeNavFragment = new HomeNavFragment();
    LearningFragment learningFragment = new LearningFragment();
    CatalogueFragment catalogueFragment = new CatalogueFragment();
    NoticeBoardFragment noticeBoardFragment = new NoticeBoardFragment();
    NewNoticeBoardFragment newNoticeBoardFragment = new NewNoticeBoardFragment();
    ArrayList<CplCollectionData> listCplCollectionData = new ArrayList<>();
    ArrayList<CplCollectionData> inProgressCplCollectionData = new ArrayList<>();
    ArrayList<CplCollectionData> completedCplCollectionData = new ArrayList<>();
    ArrayList<CplCollectionData> cplCollectionDataNotStarted = new ArrayList<>();
    AccountFragment accountFragment = new AccountFragment();
    Fragment activeFragment;
    List<Navigation> bottomNavigationList;
    List<Navigation> profileNavigationList;
    boolean showNewNoticeboardUI = false;
    List<ToolbarItem> menuList;
    CplCollectionData cplCollectionData;
    DTOUserFeedDetails dtoUserFeedDetails = new DTOUserFeedDetails();
    boolean isLanguageScreenEnabled, isCityScreenEnabled;
    private boolean cplShown;
    private boolean isValidateUserFirstTime = true;
    private boolean isMultipleCpl = false;
    private boolean readMultilingualCplFromFirebase = false;
    private PopupWindow mPopupWindow;
    int scrWidth = 0;
    int scrHeight = 0;

    boolean isBranchLink = false;
    String branchLink = null;
    String referringParamsString = null;

    ArrayList<ClickedFeedData> clickedFeedDataArrayList = new ArrayList<>();
    HashMap<String, ViewedFeedData> viewedFeedDataMap = new HashMap<>();
    private HashMap<String, ViewedFeedData> alreadyAddedFeedDataMap;
    boolean isDeepLink = false;

    @Override
    protected int getContentView() {
        return R.layout.activity_landing;
    }

    @Override
    protected void initView() {
        try {
            OustStaticVariableHandling.getInstance().setCheckCPL(false);
            isDeepLink = false;
            toolbar = findViewById(R.id.toolbar);
            brand_logo = findViewById(R.id.brand_logo);
            tenant_name = findViewById(R.id.tenant_name);
            content_layout = findViewById(R.id.content_layout);
            landingActivity_lay = findViewById(R.id.landingActivity_maniLayout);
            navigation = findViewById(R.id.bottomNavigationView);

            //Branding loader
            branding_mani_layout = findViewById(R.id.branding_main_layout);
            branding_bg = findViewById(R.id.branding_bg);
            branding_icon = findViewById(R.id.brand_loader);
            branding_percentage = findViewById(R.id.percentage_text);
            //End

            boolean clearedCache = OustPreferences.getAppInstallVariable("clearAppCache");
            if (!clearedCache) {
                deleteCache();
            }
            navigationMenu = navigation.getMenu();

            String tenantId = OustPreferences.get("tanentid");

            if (tenantId != null && !tenantId.isEmpty()) {
                File brandingBg = new File(OustSdkApplication.getContext().getFilesDir(),
                        ("oustlearn_" + tenantId.toUpperCase().trim() + "splashScreen"));

                if (brandingBg.exists() && OustPreferences.getAppInstallVariable(IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED)) {
                    Picasso.get().load(brandingBg).into(branding_bg);
//                    Glide.with(this).load(brandingBg).diskCacheStrategy(DiskCacheStrategy.DATA).into(branding_bg);
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

            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(LandingActivity.this);
            }
            OustSdkTools.setLocale(LandingActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void deleteCache() {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean deleteDir(File dir) {
        OustPreferences.saveAppInstallVariable("clearAppCache", true);
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    protected void initData() {
        try {
            OustPreferences.saveAppInstallVariable("isAppInstalled", true);
            OustPreferences.saveAppInstallVariable("isLayout4", true);
            OustStaticVariableHandling.getInstance().setAppActive(true);
            OustPreferences.saveAppInstallVariable("isLearningLoaded", false);
            OustStaticVariableHandling.getInstance().setContestLive(false);
            OustPreferences.saveAppInstallVariable("checkCatalogueInBottomNav", false);
            OustPreferences.saveAppInstallVariable("checkLearningInBottomNav", false);
            OustPreferences.save("viewFeedLIst", new Gson().toJson(viewedFeedDataMap));
            OustPreferences.save("clickFeedList", new Gson().toJson(clickedFeedDataArrayList));

            getConstants();
            if (OustAppState.getInstance().getActiveUser() != null) {
                String activeUserGet = OustPreferences.get("userdata");
                OustAppState.getInstance().setActiveUser(OustSdkTools.getActiveUserData(activeUserGet));
            }
            OustSdkTools.sendPingApi();
            startOustServices();
            try {
                FirebaseDataTools firebaseDataTools = new FirebaseDataTools();
                firebaseDataTools.iniitFirebaseListener();
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            branding_mani_layout.setVisibility(View.VISIBLE);
            checkWhetherComingFormNotification();
            checkForValidUser();
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                isBranchLink = bundle.getBoolean("isBranchLink", false);
                branchLink = bundle.getString("branchLink");
                referringParamsString = bundle.getString("referringParamsString");
            }
            activeUser = OustAppState.getInstance().getActiveUser();
            if (OustPreferences.get("userdata") != null && !OustPreferences.get("userdata").isEmpty()) {
                activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            }

            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
            // each of the below mentioned fields are optional
            HashMap<String, Object> profileUpdate = new HashMap<>();
            profileUpdate.put("Name", activeUser.getUserDisplayName());    // String
            profileUpdate.put("Identity", activeUser.getStudentid());      // String or number
            profileUpdate.put("Email", activeUser.getEmail()); // Email address of the user
            profileUpdate.put("Phone", activeUser.getUserMobile());   // Phone (with the country code, starting with +)
            profileUpdate.put("Gender", activeUser.getUserGender());             // Can be either M or F
            profileUpdate.put("Photo", activeUser.getAvatar());

            Log.d(TAG, "initData: CleverTap profile data" + profileUpdate);
            if (clevertapDefaultInstance != null) {
                Log.d(TAG, "initData: CleverTap instance is not null");
                clevertapDefaultInstance.onUserLogin(profileUpdate);
                clevertapDefaultInstance.pushProfile(profileUpdate);
            } else {
                Log.d(TAG, "initData: CleverTap instance is null");
            }

            isMultipleCpl = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_MULTIPLE_CPL);
            Log.e(TAG, "initData:--> " + isMultipleCpl);
            initBranch();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initListener() {
        try {
            /*ActiveUserViewModel activeUserViewModel = new ViewModelProvider(this).get(ActiveUserViewModel.class);
            activeUserViewModel.init();
            activeUserViewModel.getmActiveUser().observe(this, activeUserModel -> OustAppState.getInstance().setActiveUser(OustSdkTools.getActiveUserData(OustPreferences.get("userdata"))));*/
            runOnUiThread(this::fetchLandingLayout);
            fetchNotificationCount();
            fetchDirectMessageCount();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            removeAllReminderNotification();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            if (menuList != null && menuList.size() != 0) {

                for (int i = 0; i < menuList.size(); i++) {
                    ToolbarItem menuItem = menuList.get(i);
                    if (menuItem != null && menuItem.getAction() != null && !menuItem.getAction().isEmpty()) {
                        switch (menuItem.getAction()) {
                            case "notification":
                                Drawable notificationDrawable = getResources().getDrawable(R.drawable.ic_landing_notification);
                                if (menuItem.getName() != null && !menuItem.getName().isEmpty()) {
                                    menu.add(Menu.NONE, i, menuItem.getSequence(), menuItem.getName()).setIcon(OustResourceUtils.setDefaultDrawableColor(notificationDrawable, color)).setShowAsAction(getAlertShowAlways());
                                } else {
                                    menu.add(Menu.NONE, i, menuItem.getSequence(), getResources().getString(R.string.notifications)).setIcon(OustResourceUtils.setDefaultDrawableColor(notificationDrawable, color)).setShowAsAction(getAlertShowAlways());
                                }

                                itemNotification = menu.findItem(i);
                                itemNotification.setVisible(isAlertEnable());
                                break;

                            case "calendar":
                                Drawable calendarDrawable = getResources().getDrawable(R.drawable.ic_calendar_icon);
                                if (menuItem.getName() != null && !menuItem.getName().isEmpty()) {
                                    menu.add(Menu.NONE, i, menuItem.getSequence(), menuItem.getName()).setIcon(OustResourceUtils.setDefaultDrawableColor(calendarDrawable, color)).setShowAsAction(getCalendarShowAlways());
                                } else {
                                    menu.add(Menu.NONE, i, menuItem.getSequence(), getResources().getString(R.string.calendar_text)).setIcon(OustResourceUtils.setDefaultDrawableColor(calendarDrawable, color)).setShowAsAction(getCalendarShowAlways());
                                }

                                itemCalendar = menu.findItem(i);
                                itemCalendar.setVisible(isCalendarEnable());
                                break;

                            case "leaderBoard":
                                Drawable leaderBoardDrawable = getResources().getDrawable(R.drawable.ic_landing_leader_board);
                                if (menuItem.getName() != null && !menuItem.getName().isEmpty()) {
                                    menu.add(Menu.NONE, i, menuItem.getSequence(), menuItem.getName()).setIcon(OustResourceUtils.setDefaultDrawableColor(leaderBoardDrawable, color)).setShowAsAction(getLeaderBoardShowAlways());
                                } else {
                                    menu.add(Menu.NONE, i, menuItem.getSequence(), getResources().getString(R.string.leader_board_title)).setIcon(OustResourceUtils.setDefaultDrawableColor(leaderBoardDrawable, color)).setShowAsAction(getLeaderBoardShowAlways());
                                }

                                itemLeaderBoard = menu.findItem(i);
                                itemLeaderBoard.setVisible(isLeaderBoardEnable());
                                break;

                            case "help":
                                Drawable helpDrawable = getResources().getDrawable(R.drawable.help_support);
                                if (menuItem.getName() != null && !menuItem.getName().isEmpty()) {
                                    menu.add(Menu.NONE, i, menuItem.getSequence(), menuItem.getName()).setIcon(OustResourceUtils.setDefaultDrawableColor(helpDrawable, color)).setShowAsAction(getHelpShowAlways());
                                } else {
                                    menu.add(Menu.NONE, i, menuItem.getSequence(), getResources().getString(R.string.help_support)).setIcon(OustResourceUtils.setDefaultDrawableColor(helpDrawable, color)).setShowAsAction(getHelpShowAlways());
                                }

                                itemHelp = menu.findItem(i);
                                itemHelp.setVisible(isHelpEnable());
                                break;

                            case "inbox":
                                Drawable inBoxDrawable = getResources().getDrawable(R.drawable.ic_in_box_icon);
                                if (menuItem.getName() != null && !menuItem.getName().isEmpty()) {
                                    menu.add(Menu.NONE, i, menuItem.getSequence(), menuItem.getName()).setIcon(OustResourceUtils.setDefaultDrawableColor(inBoxDrawable, color)).setShowAsAction(getDirectMessageShowAlways());
                                } else {
                                    menu.add(Menu.NONE, i, menuItem.getSequence(), getResources().getString(R.string.inbox)).setIcon(OustResourceUtils.setDefaultDrawableColor(inBoxDrawable, color)).setShowAsAction(getDirectMessageShowAlways());
                                }

                                itemInBox = menu.findItem(i);
                                itemInBox.setVisible(isDirectMessageEnable());
                                MenuItemCompat.setActionView(itemInBox, R.layout.direct_message_update_count);
                                View view = MenuItemCompat.getActionView(itemInBox);
                                direct_message_main_layout = view.findViewById(R.id.direct_message_main_layout);
                                directMessageLayout = view.findViewById(R.id.inbox_count);
                                directMessageCount = view.findViewById(R.id.txt_inBox_count);
                                direMessageImageView = view.findViewById(R.id.imageView4);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    direMessageImageView.setBackgroundTintList(ColorStateList.valueOf(color));
                                }
                                direct_message_main_layout.setOnClickListener(v -> onDirectMessage());
                                break;
                            case "search":
                                Drawable searchDrawable = getResources().getDrawable(R.drawable.ic_landing_search);
                                if (menuItem.getName() != null && !menuItem.getName().isEmpty()) {
                                    menu.add(Menu.NONE, i, menuItem.getSequence(), menuItem.getName()).setIcon(OustResourceUtils.setDefaultDrawableColor(searchDrawable, color)).setShowAsAction(getSearchShowAlways());
                                } else {
                                    menu.add(Menu.NONE, i, menuItem.getSequence(), getResources().getString(R.string.search_menu_text)).setIcon(OustResourceUtils.setDefaultDrawableColor(searchDrawable, color)).setShowAsAction(getSearchShowAlways());
                                }
                                itemSearch = menu.findItem(i);
                                CustomSearchView searchView = new CustomSearchView(LandingActivity.this);
                                itemSearch.setActionView(searchView);
                                itemSearch.setShowAsAction(getSearchShowAlways() | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                                itemSearch.setVisible(isSearchEnable());
                                try {
                                    SearchManager manager = (SearchManager) getApplicationContext().getSystemService(Context.SEARCH_SERVICE);
                                    SearchView search = (SearchView) itemSearch.getActionView();
                                    search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

                                    EditText searchEditText = search.findViewById(R.id.search_src_text);
                                    searchEditText.setTextColor(getResources().getColor(R.color.primary_text));
                                    searchEditText.setHintTextColor(getResources().getColor(R.color.unselected_text));

                                    search.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
                                        if (hasFocus) {
                                            try {
                                                if (itemNotification != null) {
                                                    itemNotification.setVisible(false);
                                                }
                                                if (itemCalendar != null) {
                                                    itemCalendar.setVisible(false);
                                                }
                                                if (itemLeaderBoard != null) {
                                                    itemLeaderBoard.setVisible(false);
                                                }
                                                if (itemInBox != null) {
                                                    itemInBox.setVisible(false);
                                                }
                                                if (itemHelp != null) {
                                                    itemHelp.setVisible(false);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                OustSdkTools.sendSentryException(e);
                                            }
                                        }
                                    });

                                    search.setOnCloseListener(() -> {
                                        try {
                                            if (itemNotification != null) {
                                                itemNotification.setVisible(alertEnable);
                                            }
                                            if (itemCalendar != null) {
                                                itemCalendar.setVisible(calendarEnable);
                                            }
                                            if (itemLeaderBoard != null) {
                                                itemLeaderBoard.setVisible(leaderBoardEnable);
                                            }
                                            if (itemInBox != null) {
                                                fetchDirectMessageCount();
                                                itemInBox.setVisible(directMessageEnable);
                                            }
                                            if (itemHelp != null) {
                                                itemHelp.setVisible(isHelpEnable());
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            OustSdkTools.sendSentryException(e);
                                        }
                                        return false;
                                    });

                                    itemSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                                        @Override
                                        public boolean onMenuItemActionExpand(MenuItem item) {
                                            Log.e(TAG, "onMenuItemActionExpand");
                                            return true;
                                        }

                                        @Override
                                        public boolean onMenuItemActionCollapse(MenuItem item) {
                                            Log.e(TAG, "onMenuItemActionCollapse");
                                            try {
                                                if (itemNotification != null) {
                                                    itemNotification.setVisible(alertEnable);
                                                }
                                                if (itemCalendar != null) {
                                                    itemCalendar.setVisible(calendarEnable);
                                                }
                                                if (itemLeaderBoard != null) {
                                                    itemLeaderBoard.setVisible(leaderBoardEnable);
                                                }
                                                if (itemInBox != null) {
                                                    fetchDirectMessageCount();
                                                    itemInBox.setVisible(directMessageEnable);
                                                }
                                                if (itemHelp != null) {
                                                    itemHelp.setVisible(isHelpEnable());
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                OustSdkTools.sendSentryException(e);
                                            }
                                            invalidateOptionsMenu();
//                                            onSearch("");
                                            return true;
                                        }
                                    });

                                    search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                        @Override
                                        public boolean onQueryTextSubmit(String query) {
                                            onSearch(query);
                                            return false;
                                        }

                                        @Override
                                        public boolean onQueryTextChange(String newText) {
                                            onSearch(newText);
                                            return false;
                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                }
                                break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (menuList != null && menuList.size() != 0 && itemId < menuList.size()) {

            ToolbarItem menuItem = menuList.get(itemId);
            switch (menuItem.getAction()) {
                case "notification":
                    Drawable notificationDrawable = getResources().getDrawable(R.drawable.ic_landing_notification);
                    itemNotification.setIcon(OustResourceUtils.setDefaultDrawableColor(notificationDrawable, color));
                    onNotification();
                    break;
                case "calendar":
                    onCalendar();
                    break;
                case "leaderBoard":
                    onLeaderBoard();
                    break;
                case "inbox":
                    onDirectMessage();
                    break;
                case "help":
                    onHelpSupport();
                    break;
            }
        }
        return true;
    }

    void getConstants() {
        try {
            AppConstants.MediaURLConstants.init();
            AppConstants.StringConstants.init();
            Log.d(TAG, "getConstants: MEDIA_SOURCE_BASE_URL:" + AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL);
            Log.d(TAG, "getConstants: CLOUD_FRONT_BASE_PATH:" + AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH);
            Log.d(TAG, "getConstants: CLOUD_FRONT_BASE_HTTPs:" + AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void startOustServices() {
        try {
            Intent intent2 = new Intent(LandingActivity.this, SubmitRequestsService.class);
            startService(intent2);
            Intent intent3 = new Intent(LandingActivity.this, SubmitCourseCompleteService.class);
            intent3.setClassName("com.oustme.oustsdk", "com.oustme.oustsdk.service");
            startService(intent3);
            Intent intent4 = new Intent(LandingActivity.this, SubmitLevelCompleteService.class);
            startService(intent4);
            Intent intent5 = new Intent(LandingActivity.this, FeedBackService.class);
            startService(intent5);
            Intent intent6 = new Intent(LandingActivity.this, SubmitNBPostService.class);
            startService(intent6);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    void fetchLandingLayout() {
        landingLayoutViewModel = new ViewModelProvider(this).get(LandingLayoutViewModel.class);
        landingLayoutViewModel.init(this);
        landingLayoutViewModel.getBottomNavList().observe(this, landingLayout -> {
            if (landingLayout == null)
                return;
            readMultilingualCplFromFirebase = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.READ_MULTILINGUAL_CPL_FROM_FIREBASE);
            isAppTutorialShown = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN);
            isAppTutorialShownOnlyLogin = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN_ALWAYS_ON_LOGIN);
            tempIsAppTutorialShown = OustPreferences.get(ORG_ID_USER_ID_APP_TUTORIAL_VIEWED);
            if (OustPreferences.get("tanentid") != null && !OustPreferences.get("tanentid").isEmpty()) {
                tenantName = OustPreferences.get("tanentid").trim();
                if (tempIsAppTutorialShown != null) {
                    String[] tempAppTutorialString = tempIsAppTutorialShown.split("_");
                    Log.e(TAG, "fetchLandingLayout: tempAppTutorialString--> " + Arrays.toString(tempAppTutorialString) + " length--> " + tempAppTutorialString.length + " tanentid--> " + tenantName);
                    if (tempAppTutorialString.length > 0) {
                        if ((tempAppTutorialString[0] != null) && (!tempAppTutorialString[0].isEmpty())) {
                            if (tenantName.equalsIgnoreCase(tempAppTutorialString[0])) {
                                if (activeUser != null && activeUser.getStudentKey() != null) {
                                    if ((tempAppTutorialString[1] != null) && (!tempAppTutorialString[1].isEmpty())) {
                                        if (activeUser.getStudentKey().equalsIgnoreCase(tempAppTutorialString[1])) {
                                            if ((tempAppTutorialString[2] != null) && (!tempAppTutorialString[2].isEmpty())) {
                                                if (tempAppTutorialString[2].equalsIgnoreCase("true")) {
                                                    if (!isAppTutorialShown && isAppTutorialShownOnlyLogin) {
                                                        isAppTutorialShown = false;
                                                        OustPreferences.saveAppInstallVariable(IS_APP_TUTORIAL_SHOWN, false);
                                                    } else {
                                                        isAppTutorialShown = true;
                                                        OustPreferences.saveAppInstallVariable(IS_APP_TUTORIAL_SHOWN, true);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            setToolbar();
            setNavigation();
            profileNavigationList = landingLayout.getProfileNavigation();
            try {
                if (landingLayout.getTabNavigation() != null && landingLayout.getTabNavigation().size() != 0) {
                    for (Navigation navigation : landingLayout.getTabNavigation()) {
                        if (navigation != null) {
                            if (navigation.getNavType().equalsIgnoreCase("catalogue")) {
                                OustPreferences.saveAppInstallVariable("checkCatalogueInBottomNav", true);
                                break;
                            } else {
                                OustPreferences.saveAppInstallVariable("checkCatalogueInBottomNav", false);
                            }
                        }
                    }
                    for (Navigation navigation : landingLayout.getTabNavigation()) {
                        if (navigation != null) {
                            if (navigation.getNavType().equalsIgnoreCase("mytask")) {
                                OustPreferences.saveAppInstallVariable("checkLearningInBottomNav", true);
                                break;
                            } else {
                                OustPreferences.saveAppInstallVariable("checkLearningInBottomNav", false);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            extractBottomNav(landingLayout.getTabNavigation());
            setToolbarItem(landingLayout.getToolbar());
            if (!isAppTutorialShown) {
                getAppTutorialData();
            } else {
                checkForMultilingualCpl();
            }
            getAllUserNewsFeed();
        });
    }

    private void checkChildCPlDistributed() {
        try {
            if (landingLayoutViewModel == null) {
                landingLayoutViewModel = new ViewModelProvider(this).get(LandingLayoutViewModel.class);
                landingLayoutViewModel.init(this);
            }
            landingLayoutViewModel.checkParentCplDistributesOrNot().observe(this, parentCplDistributionResponse -> {
                if (parentCplDistributionResponse.getParentCplId() > 0 && parentCplDistributionResponse.getChildCplId() == 0) {
                    try {
                        if (OustStaticVariableHandling.getInstance().isCheckCPL()) {
                            CplIntroActivity.activity.finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    Log.d(TAG, "OpenLanguageScreenForMultilingual: " + parentCplDistributionResponse.getParentCplId() + " is language screen open--> " + OustStaticVariableHandling.getInstance().isCplLanguageScreenOpen() + "  readMultilingualCplFromFirebase--> " + readMultilingualCplFromFirebase);
                    Intent intent = new Intent(LandingActivity.this, LanguageSelectionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("CPL_ID", OustSdkTools.newConvertToLong(parentCplDistributionResponse.getParentCplId()));
                    intent.putExtra("allowBackPress", false);
                    intent.putExtra("FEED", false);
                    intent.putExtra("isChildCplDistributed", false);
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkForMultilingualCpl() {
        try {
            if (activeUser == null) {
                activeUser = OustAppState.getInstance().getActiveUser();
                if (OustPreferences.get("userdata") != null && !OustPreferences.get("userdata").isEmpty()) {
                    activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
                }
            }
            Log.d(TAG, "checkForMultilingualCpl: ");
            OustPreferences.saveAppInstallVariable(IS_LANG_SELECTED, false);
            OustPreferences.saveTimeForNotification("parentCplId", 0);
            isLanguageScreenEnabled = OustPreferences.getAppInstallVariable("showLanguageScreen");
            isCityScreenEnabled = OustPreferences.getAppInstallVariable("showCityListScreenForCPL");
            runOnUiThread(this::getCplData);
            if (readMultilingualCplFromFirebase) {
                checkChildCPlDistributed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void addToFireBaseRefList(String message, ValueEventListener eventListener) {
        OustFirebaseTools.getRootRef().child(message).addValueEventListener(eventListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, message));
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    private void fetchNotificationCount() {
        landingLayoutViewModel.getNotificationCount().observe(this, notificationResponses -> {
            ArrayList<NotificationResponse> fireBaseNotifications = new ArrayList<>();
            ArrayList<NotificationResponse> roomDbNotifications;
            roomDbNotifications = (ArrayList<NotificationResponse>) RoomHelper.getNotificationsData(activeUser.getStudentKey());
            if (notificationResponses.size() > 0) {
                if (roomDbNotifications.size() > 0) {
                    for (int i = 0; i < roomDbNotifications.size(); i++) {
                        if (roomDbNotifications.get(i).getFireBase() != null) {
                            if (roomDbNotifications.get(i).getFireBase()) {
                                fireBaseNotifications.add(roomDbNotifications.get(i));
                            }
                        }
                    }
                    if (notificationResponses.size() > fireBaseNotifications.size()) {
                        if (itemNotification != null) {
                            itemNotification.setIcon(R.drawable.notification_reddot_icon);
                        }
                    }
                } else {
                    try {
                        if (itemNotification != null) {
                            itemNotification.setIcon(R.drawable.notification_reddot_icon);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void fetchDirectMessageCount() {
        try {
            landingLayoutViewModel.getDirectMessageCount().observe(this, messageCount -> {
                Log.e(TAG, "fetchDirectMessageCount: along--> " + messageCount);
                if (messageCount != null && messageCount > 0) {
                    if (directMessageLayout != null) {
                        directMessageLayout.setVisibility(View.VISIBLE);
                        directMessageCount.setText(messageCount + "");
                    }
                } else {
                    if (directMessageLayout != null) {
                        directMessageLayout.setVisibility(View.GONE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    void setToolbar() {
        try {
            getColors();
            toolbar.setBackgroundColor(bgColor);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            tenant_name.setTextColor(color);

            if ((OustPreferences.get("hostAppIcon") != null)
                    && (!OustPreferences.get("hostAppIcon").isEmpty())) {
                Log.e(TAG, "hiding org name");
                tenant_name.setVisibility(View.GONE);
                brand_logo.setVisibility(View.VISIBLE);

                if (OustSdkTools.checkInternetStatus()) {
                    final String path = OustPreferences.get("hostAppIcon");
                    final Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            brand_logo.setImageBitmap(bitmap);
                            brand_logo.setVisibility(View.VISIBLE);
                            Log.e(TAG, "Displaying org logo");
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            tenant_name.setVisibility(View.VISIBLE);
                            brand_logo.setVisibility(View.GONE);
                            Log.e(TAG, "org logo bitmap failed error");
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    };
                    Picasso.get().load(path).into(target);
                    brand_logo.setTag(target);

                } else {
                    Picasso.get().load(OustPreferences.get("hostAppIcon")).networkPolicy(NetworkPolicy.OFFLINE).into(brand_logo);
                    brand_logo.setVisibility(View.VISIBLE);
                }
            } else if ((OustPreferences.get("companydisplayName") != null) && (!OustPreferences.get("companydisplayName").isEmpty())) {
                Log.d(TAG, "setToolbar: companydisplayName");
                tenant_name.setVisibility(View.VISIBLE);
                brand_logo.setVisibility(View.GONE);
                tenant_name.setText(OustPreferences.get("companydisplayName"));
            } else {
                tenant_name.setText(getResources().getString(R.string.landing_heading));
                Picasso.get().load(OustPreferences.get("hostAppIcon")).networkPolicy(NetworkPolicy.OFFLINE).into(brand_logo);
                brand_logo.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    void getColors() {
        try {
            color = OustResourceUtils.getColors();
            bgColor = OustResourceUtils.getToolBarBgColor();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            color = OustSdkTools.getColorBack(R.color.lgreen);
        }
    }

    void setNavigation() {
        Log.d(TAG, "setNavigation: ");
        try {
            ColorStateList iconsColorStates = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_checked},
                            new int[]{android.R.attr.state_checked}
                    },
                    new int[]{
                            Color.parseColor("#908F8F"),
                            color
                    });

            ColorStateList textColorStates = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_checked},
                            new int[]{android.R.attr.state_checked}
                    },
                    new int[]{
                            Color.parseColor("#908F8F"),
                            color
                    });

            navigation.setItemIconTintList(iconsColorStates);
            navigation.setItemTextColor(textColorStates);
            navigation.setOnNavigationItemSelectedListener(this);
            navigation.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    void extractBottomNav(List<Navigation> navigationList) {
        try {
            if (navigationList != null && navigationList.size() != 0) {
                int navItemCount = navigationList.size() <= 5 ? navigationList.size() : 4;
                bottomNavigationList = new ArrayList<>();

                for (int i = 0; i < navItemCount; i++) {
                    if (navigationList.get(i) != null) {
                        bottomNavigationList.add(navigationList.get(i));
                        navigationMenu.add(Menu.NONE, i + 1, Menu.NONE, navigationList.get(i).getNavName())
                                .setIcon(R.drawable.ic_home);

                        SpannableString spannableString = new SpannableString(navigationList.get(i).getNavName());
                        spannableString.setSpan(new RelativeSizeSpan(0.75f), 0, spannableString.length(), 0);
                        navigation.getMenu().getItem(i).setTitle(spannableString);
                        OustResourceUtils.setMenuIcon(navigation.getMenu().getItem(i), navigationList.get(i).getNavIcon());

                        if (navigationList.get(i).getNavType().equalsIgnoreCase("home")) {
                            //To avoid duplicate home fragment screen, we are checking if FM is holding any reference. If holding, use the existed otherwise create new.
                            if (fm != null) {
                                homeNavFragment = (HomeNavFragment) fm.findFragmentByTag(HomeNavFragment.TAG);
                                if (homeNavFragment == null) {
                                    homeNavFragment = new HomeNavFragment();
                                }
                                if (!homeNavFragment.isAdded()) {
                                    homeNavFragment = HomeNavFragment.newInstance(bottomNavigationList.get(i));
                                    mLaunchBottomNavFragment(fm, homeNavFragment, HomeNavFragment.TAG);
                                } else {
                                    fm.beginTransaction().hide(homeNavFragment).replace(R.id.frame_landing, homeNavFragment, HomeNavFragment.TAG).setMaxLifecycle(homeNavFragment, Lifecycle.State.STARTED).commit();
                                }
                            }
                        }

                        if (navigationList.get(i).getNavType().equalsIgnoreCase("mytask")) {
                            learningFragment = LearningFragment.newInstance(bottomNavigationList.get(i));
                            mLaunchBottomNavFragment(fm, learningFragment, LearningFragment.TAG);

                        }

                        if (navigationList.get(i).getNavType().equalsIgnoreCase("catalogue")) {
                            try {
                                CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
                                HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                                eventUpdate.put("ClickedOnCatalog", true);
                                Log.d(TAG, "CleverTap instance: " + eventUpdate);
                                if (clevertapDefaultInstance != null) {
                                    clevertapDefaultInstance.pushEvent("Catalog_Clicks", eventUpdate);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                            catalogueFragment = CatalogueFragment.newInstance(bottomNavigationList.get(i));
                            mLaunchBottomNavFragment(fm, catalogueFragment, CatalogueFragment.TAG);

                        }

                        if (navigationList.get(i).getNavType().equalsIgnoreCase("noticeboard")) {
                            showNewNoticeboardUI = OustPreferences.getAppInstallVariable("showNewNoticeboardUI");
                            Log.d("showNewNoticeboardUI2", "" + showNewNoticeboardUI);
                            if (OustPreferences.getAppInstallVariable("showNewNoticeboardUI")) {
                                newNoticeBoardFragment = NewNoticeBoardFragment.newInstance(bottomNavigationList.get(i));
                                mLaunchBottomNavFragment(fm, newNoticeBoardFragment, NewNoticeBoardFragment.TAG);

                            } else {
                                noticeBoardFragment = NoticeBoardFragment.newInstance(bottomNavigationList.get(i));
                                mLaunchBottomNavFragment(fm, noticeBoardFragment, NoticeBoardFragment.TAG);

                            }
                        }

                        if (navigationList.get(i).getNavType().equalsIgnoreCase("profile") || navigationList.get(i).getNavType().equalsIgnoreCase("account")) {
                            accountFragment = AccountFragment.newInstance(bottomNavigationList.get(i), profileNavigationList);
                            mLaunchBottomNavFragment(fm, accountFragment, AccountFragment.TAG);
                        }

                    }
                }
                setNavigationItems(bottomNavigationList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void mLaunchBottomNavFragment(FragmentManager fm, Fragment mNavFragment, String tag) {
        if (fm != null && mNavFragment != null) {
            fm.beginTransaction().add(R.id.frame_landing, mNavFragment, tag).hide(mNavFragment).setMaxLifecycle(mNavFragment, Lifecycle.State.STARTED).commit();
        }
    }

    void setNavigationItems(List<Navigation> navigationList) {
        try {
            if (navigationList != null && navigationList.size() != 0) {
                if (selectedPos < 0) {
                    navigation.setSelectedItemId(navigation.getMenu().getItem(0).getItemId());
                    OustResourceUtils.setMenuIconSelected(navigation.getMenu().getItem(0), navigationList.get(0).getNavIcon());
                } else {
                    OustResourceUtils.setMenuIconSelected(navigation.getMenu().getItem(selectedPos), navigationList.get(selectedPos).getNavIcon());
                }
            }

            new Handler().postDelayed(() -> {
                try {
                    branding_mani_layout.setVisibility(View.GONE);
                    content_layout.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }, 2000);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    void setToolbarItem(ToolbarModel toolbarModel) {
        if (toolbarModel == null)
            return;

        if (toolbarModel.getContentColor() != null && !toolbarModel.getContentColor().isEmpty())
            OustPreferences.save("toolbarColorCode", toolbarModel.getContentColor());

        if (toolbarModel.getBgColor() != null && !toolbarModel.getBgColor().isEmpty())
            OustPreferences.save("toolbarBgColor", toolbarModel.getBgColor());

        if (toolbarModel.getContent() != null) {
            menuList = toolbarModel.getContent();
            for (ToolbarItem toolbarItem : toolbarModel.getContent()) {
                switch (toolbarItem.getAction()) {
                    case "notification":
                        alertEnable = toolbarItem.isEnable();
                        alertShowAlways = toolbarItem.getShowAlways();
                        break;
                    case "calendar":
                        calendarEnable = toolbarItem.isEnable();
                        calendarShowAlways = toolbarItem.getShowAlways();
                        break;
                    case "leaderBoard":
                        leaderBoardEnable = toolbarItem.isEnable();
                        leaderBoardShowAlways = toolbarItem.getShowAlways();
                        break;
                    case "search":
                        searchEnable = toolbarItem.isEnable();
                        searchShowAlways = toolbarItem.getShowAlways();
                        break;
                    case "inbox":
                        directMessageEnable = toolbarItem.isEnable();
                        directMessageShowAlways = toolbarItem.getShowAlways();
                        break;
                    case "help":
                        helpEnable = toolbarItem.isEnable();
                        helpShowAlways = toolbarItem.getShowAlways();
                        break;
                }
            }
            invalidateOptionsMenu();
        }

    }

    boolean isSearchEnable() {
        return searchEnable;
    }

    boolean isLeaderBoardEnable() {
        return leaderBoardEnable;
    }

    boolean isAlertEnable() {
        return alertEnable;
    }

    boolean isCalendarEnable() {
        return calendarEnable;
    }

    public int getAlertShowAlways() {
        return alertShowAlways;
    }

    public int getCalendarShowAlways() {
        return calendarShowAlways;
    }

    public int getLeaderBoardShowAlways() {
        return leaderBoardShowAlways;
    }

    public int getSearchShowAlways() {
        return searchShowAlways;
    }

    private int getDirectMessageShowAlways() {
        return directMessageShowAlways;
    }

    private boolean isDirectMessageEnable() {
        return directMessageEnable;
    }

    boolean isHelpEnable() {
        return helpEnable;
    }

    public int getHelpShowAlways() {
        return helpShowAlways;
    }

    void onSearch(String query) {
        try {
            if (activeFragment != null) {
                if (activeFragment == homeNavFragment) {
                    homeNavFragment.onSearch(query);
                } else if (activeFragment == learningFragment) {
                    learningFragment.onSearch(query, true);
                } else if (activeFragment == catalogueFragment) {
                    catalogueFragment.onSearch(query);
                } else if (activeFragment == noticeBoardFragment)
                    noticeBoardFragment.onSearch(query);
                else if (activeFragment == newNoticeBoardFragment)
                    newNoticeBoardFragment.onSearch(query);

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void onDirectMessage() {
        try {
            Intent inBoxIntent = new Intent(LandingActivity.this, DirectMessageActivity.class);
            startActivity(inBoxIntent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void onHelpSupport() {
        try {
            Intent intentSupport = new Intent(LandingActivity.this, FormFillingActivity.class);
            startActivity(intentSupport);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void onLeaderBoard() {
        try {
            Intent intent = new Intent(this, CommonLeaderBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle containerBundle = new Bundle();
            containerBundle.putString("containerType", "overAllLeaderBoard");
            intent.putExtras(containerBundle);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void onCalendar() {
        try {
            Intent intent = new Intent(this, CalendarScreen.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void onNotification() {
        try {
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkWhetherComingFormNotification() {
        Log.e(TAG, "checkWhetherComingFormNotification");
        OustPreferences.saveTimeForNotification("lastNotificationOpenedTime", (System.currentTimeMillis() / 1000));
        final Intent CallingIntent = getIntent();
        new Handler().postDelayed(() -> {
            try {
                Log.e("Notification", " LandingActivity inside checkWhetherComingFormNotification()");
                String assessmentId = CallingIntent.getStringExtra("assessmentId");
                String courseId = CallingIntent.getStringExtra("courseId");
                String collectionId = CallingIntent.getStringExtra("collectionId");

                String noticeBoardId = CallingIntent.getStringExtra("noticeBoardId");
                String catalogueId = CallingIntent.getStringExtra("catalogue_id");
                String cpl_id = CallingIntent.getStringExtra("cpl_id");
                String meetingId = CallingIntent.getStringExtra("meetingId");
                String ml_cpl_id = CallingIntent.getStringExtra("ml_cpl_id");
                String feedId = CallingIntent.getStringExtra("feedId");
                String fffc_Id = CallingIntent.getStringExtra("fastest_finger_first_contest_id");

                boolean isFeedDistributed = CallingIntent.getBooleanExtra("isFeedDistributed", false);

                int postId = CallingIntent.getIntExtra("postId", 0);
                int commentId = CallingIntent.getIntExtra("commentId", 0);
                int nbId = CallingIntent.getIntExtra("nbId", 0);
                int replyId = CallingIntent.getIntExtra("replyId", 0);

                Log.d(TAG, "NoticeBoardId--> " + "postId:-> " + postId + " commentId " + commentId + " nbId " + nbId + " replyId " + replyId);
                Log.d(TAG, "CourseId--> " + courseId);
                Log.d(TAG, "assessmentId assessmentId--> " + assessmentId);
                Log.d(TAG, "collectionId--> " + collectionId);
                Log.d(TAG, "isFeedDistributed--> " + isFeedDistributed);
                Log.d(TAG, "collection-feed-Id--> " + feedId);
                Log.d(TAG, "noticeBoardId--> " + noticeBoardId);
                Log.d(TAG, "catalogueId --> " + catalogueId);
                Log.d(TAG, "fffc_Id --> " + fffc_Id);
                Log.d(TAG, "meetingId --> " + meetingId);

                if ((courseId != null) && (!courseId.isEmpty())) {
                    BranchTools.checkModuleDistributionOrNot(activeUser, courseId, "COURSE");
                } else if ((assessmentId != null) && (!assessmentId.isEmpty())) {
                    BranchTools.checkModuleDistributionOrNot(activeUser, assessmentId, "ASSESSMENT");
                } else if ((collectionId != null) && (!collectionId.isEmpty())) {
                    BranchTools.gotoCollectionPage(collectionId);
                } else if (isFeedDistributed) {
                    BranchTools.gotoFeedPage(feedId, activeUser);
                } else if (replyId != 0) {
                    Intent intent = new Intent(LandingActivity.this, NBCommentActivity.class);
                    intent.putExtra("nbId", (long) nbId);
                    intent.putExtra("postId", (long) postId);
                    intent.putExtra("commentId", (long) commentId);
                    startActivity(intent);
                } else if (commentId != 0) {
                    Intent intent = new Intent(LandingActivity.this, NBPostDetailsActivity.class);
                    intent.putExtra("nbId", (long) nbId);
                    intent.putExtra("postId", (long) postId);
                    intent.putExtra("commentId", (long) commentId);
                    startActivity(intent);
                } else if (postId != 0) {
                    Intent intent = new Intent(LandingActivity.this, NBPostDetailsActivity.class);
                    intent.putExtra("nbId", (long) nbId);
                    intent.putExtra("postId", (long) postId);
                    startActivity(intent);
                } else if (noticeBoardId != null && !noticeBoardId.isEmpty()) {
                    BranchTools.gotoNoticeBoardPage(noticeBoardId, activeUser.getStudentKey(), "", "", "", GCMType.NOTICE_BOARD_DISTRIBUTION.name());
                } else if (catalogueId != null && !catalogueId.isEmpty()) {
                    OustPreferences.saveTimeForNotification("catalogueId", Long.parseLong(catalogueId));
                    try {
                        BranchTools.getCatalog(Long.parseLong(catalogueId));
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                } else if (cpl_id != null && !cpl_id.isEmpty()) {
                    BranchTools.checkModuleDistributionOrNot(activeUser, cpl_id, "CPL");
                } else if (ml_cpl_id != null && !ml_cpl_id.isEmpty()) {
                    BranchTools.checkMlCPLDistributionOrNot(activeUser, ml_cpl_id);
                } else if (meetingId != null && !meetingId.isEmpty()) {
                    BranchTools.gotoCalendarPage(OustSdkTools.newConvertToLong(meetingId));
                } else if (fffc_Id != null && !fffc_Id.isEmpty()) {
                    BranchTools.getUserFFContest(activeUser.getStudentKey(), activeUser.getAvatar()
                            , activeUser.getUserDisplayName(), fffc_Id);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }, 300);
    }

    @Override
    public void onBackPressed() {
        try {
            OustAppState.getInstance().setLandingPageOpen(false);
            OustAppState.getInstance().setLandingPageLive(false);
            OustStaticVariableHandling.getInstance().setAppActive(false);
            if (OustStaticVariableHandling.getInstance().isContainerApp()) {
                if (doubleBackToExitPressedOnce) {
                    OustFirebaseTools.resetFirebase();
                    OustAppState.getInstance().clearAll();
                    this.finish();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity();
                    }
                    System.exit(0);
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getResources().getString(R.string.exit_on_back), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            } else {
                OustFirebaseTools.resetFirebase();
                OustAppState.getInstance().clearAll();
                LandingActivity.this.finish();
            }

            sendFeedClickedRequestToBackend();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onNewSelection(int position) {
        try {
            onNavigationItemSelected(navigationMenu.getItem(position));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        try {
            if (navigation.getMenu().size() > 0) {
                for (int i = 0; i < navigation.getMenu().size(); i++) {
                    if (navigation.getMenu().getItem(i).getItemId() == menuItem.getItemId()) {
                        OustResourceUtils.setMenuIconSelected(navigation.getMenu().getItem(i), bottomNavigationList.get(i).getNavIcon());
                    } else {
                        OustResourceUtils.setMenuIcon(navigation.getMenu().getItem(i), bottomNavigationList.get(i).getNavIcon());
                    }
                }
            }
            int itemId = menuItem.getItemId();
            if (itemId == 1) {
                selectedPos = 0;
            } else if (itemId == 2) {
                selectedPos = 1;
            } else if (itemId == 3) {
                selectedPos = 2;
            } else if (itemId == 4) {
                selectedPos = 3;
            } else if (itemId == 5) {
                selectedPos = 4;
            }
            onNav(selectedPos);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    void onNav(int position) {
        try {
            if (lastNavPosition == position) {
                if (activeFragment != null) {
                    if (activeFragment == homeNavFragment) {
                        homeNavFragment.onScrollViewTopPosition();
                    }
                }
                return;
            }

            FragmentTransaction transaction = fm.beginTransaction();
            if (lastNavPosition < position) {
                transaction.setCustomAnimations(
                        R.anim.enter_from_right,  // enter
                        R.anim.exit_to_left,  // exit
                        R.anim.enter_from_left,   // popEnter
                        R.anim.exit_to_right  // popExit
                );
            } else {
                transaction.setCustomAnimations(
                        R.anim.enter_from_left,  // enter
                        R.anim.exit_to_right,  // exit
                        R.anim.enter_from_right,   // popEnter
                        R.anim.exit_to_left  // popExit
                );
            }
            lastNavPosition = position;
            //TODO: handle fragment

            if (position >= 0) {
                String navType = bottomNavigationList.get(position).getNavType().toLowerCase();
                switch (navType) {
                    case "home":
                        if (activeFragment != null) {
                            transaction.hide(activeFragment).show(homeNavFragment).commit();
                        } else {
                            transaction.show(homeNavFragment).commit();
                        }
                        if (!learningFragment.isVisible()) {
                            OustPreferences.save("currentFragment", "homeNavFragment");
                        }
                        activeFragment = homeNavFragment;
                        break;
                    case "mytask":
                        if (activeFragment != null) {
                            transaction.hide(activeFragment).show(learningFragment).commit();
                        } else {
                            transaction.show(learningFragment).commit();
                        }
                        if (!learningFragment.isVisible()) {
                            OustPreferences.save("currentFragment", "learningFragment");
                        }
                        activeFragment = learningFragment;
                        closeSearchIcon();
                        break;
                    case "catalogue":
                        if (activeFragment != null) {
                            transaction.hide(activeFragment).show(catalogueFragment).commit();
                        } else {
                            transaction.show(catalogueFragment).commit();
                        }
                        if (!learningFragment.isVisible()) {
                            OustPreferences.save("currentFragment", "catalogueFragment");
                        }
                        activeFragment = catalogueFragment;
                        closeSearchIcon();
                        break;
                    case "noticeboard":
                        if (activeFragment != null) {
                            if (OustPreferences.getAppInstallVariable("showNewNoticeboardUI")) {
                                transaction.hide(activeFragment).show(newNoticeBoardFragment).commit();
                                activeFragment = newNoticeBoardFragment;
                            } else {
                                transaction.hide(activeFragment).show(noticeBoardFragment).commit();
                                activeFragment = noticeBoardFragment;
                            }
                        } else {
                            if (OustPreferences.getAppInstallVariable("showNewNoticeboardUI")) {
                                transaction.show(newNoticeBoardFragment).commit();
                                activeFragment = newNoticeBoardFragment;
                            } else {
                                transaction.show(noticeBoardFragment).commit();
                                activeFragment = noticeBoardFragment;
                            }
                        }
                        if (!learningFragment.isVisible()) {
                            OustPreferences.save("currentFragment", "noticeBoardFragment");
                        }
                        closeSearchIcon();
                        break;
                    case "account":
                    case "profile":
                        if (activeFragment != null) {
                            transaction.hide(activeFragment).show(accountFragment).commit();
                        } else {
                            transaction.show(accountFragment).commit();
                        }
                        if (!learningFragment.isVisible()) {
                            OustPreferences.save("currentFragment", "accountFragment");
                        }
                        activeFragment = accountFragment;
                        closeSearchIcon();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void closeSearchIcon() {
        try {
            if (itemSearch != null) {
                itemSearch.collapseActionView();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public String getOnNavSelection() {
        String menuName = null;
        try {
            menuName = String.valueOf(navigationMenu.getItem(1));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return menuName;
    }

    public interface RemoveFilterView {
        void removeFilterView();
    }

    public void getAllUserNewsFeed() {
        try {
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.OPEN_WELCOME_POPUP)) {
                ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
                String message = "landingPage/" + activeUser.getStudentKey() + "/specialFeed";
                Log.d(TAG, "getAllUserNewsFeed: " + message);
                ValueEventListener newsfeedListListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            SpecialFeedModel specialFeedModel = new SpecialFeedModel();
                            Map<String, Object> splFeedData = (Map<String, Object>) dataSnapshot.getValue();
                            if (splFeedData != null) {
                                if (splFeedData.get("feedId") != null) {
                                    specialFeedModel.setFeedId(OustSdkTools.newConvertToLong(splFeedData.get("feedId")));
                                }

                                if (splFeedData.get("isClicked") != null) {
                                    specialFeedModel.setClicked((boolean) splFeedData.get("isClicked"));
                                }
                            }
                            if (specialFeedModel.getFeedId() != 0 && !specialFeedModel.isClicked()) {
                                getSpecialFeedData(specialFeedModel.getFeedId());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError DatabaseError) {
                    }
                };
                DatabaseReference newsfeedRef = OustFirebaseTools.getRootRef().child(message);
                Query query = newsfeedRef.orderByChild("timeStamp");

                query.keepSynced(true);
                query.addValueEventListener(newsfeedListListener);
                OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(newsfeedListListener, message));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getSpecialFeedData(final long id) {
        String userFeedsDetails = OustSdkApplication.getContext().getResources().getString(R.string.userFeedDetails);
        userFeedsDetails = userFeedsDetails.replace("{feedId}", String.valueOf(id));
        userFeedsDetails = userFeedsDetails.replace("{userId}", activeUser.getStudentid());
        try {
            userFeedsDetails = HttpManager.getAbsoluteUrl(userFeedsDetails);
            ApiCallUtils.doNetworkCall(Request.Method.GET, userFeedsDetails, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "onResponse: user feeds:" + response.toString());
                    Gson gson = new Gson();
                    dtoUserFeedDetails = gson.fromJson(response.toString(), DTOUserFeedDetails.class);
                    if (dtoUserFeedDetails != null && dtoUserFeedDetails.getFeedDetails().getFeedId() != 0) {
                        SelectInitWelComePopViews(dtoUserFeedDetails.getFeedDetails(), getApplicationContext(), landingActivity_lay);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onResponse: Error:" + error.getMessage());
                    Toast.makeText(LandingActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getCplData() {
        try {
            final String message;
            if (!isMultipleCpl) {
                message = "/landingPage/" + activeUser.getStudentKey() + "/cpl";
            } else {
                message = "/landingPage/" + activeUser.getStudentKey() + "/multipleCPL";
            }
            Log.e(TAG, "getCplData: " + message);
            ValueEventListener cplDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        listCplCollectionData.clear();
                        Object o1 = dataSnapshot.getValue();
                        if (o1 != null) {
                            if (!isMultipleCpl) {
                                if (o1.getClass().equals(ArrayList.class)) {
                                    List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                                    cplCollectionData = new CplCollectionData(learningList);
                                } else {
                                    Map<String, Object> lpMainMap;
                                    lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                    if (lpMainMap != null) {
                                        cplCollectionData = new CplCollectionData(lpMainMap);
                                    }
                                }
                                if (cplCollectionData != null) {
                                    getCplExtraInfo(cplCollectionData.getCplId());
                                }
                            } else {
                                if (o1.getClass().equals(ArrayList.class)) {
                                    List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                                    cplCollectionData = new CplCollectionData(learningList);
                                } else {
                                    Map<String, Object> lpMainMap;
                                    Map<String, Object> cplMainMap;
                                    lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                    if (lpMainMap != null) {
                                        for (Map.Entry<String, Object> entry : lpMainMap.entrySet()) {
                                            cplMainMap = (Map<String, Object>) entry.getValue();
                                            extractData(entry.getKey(), cplMainMap);
                                        }
                                    }
                                }

                                if (listCplCollectionData.size() > 0) {
                                    inProgressCplCollectionData.clear();
                                    completedCplCollectionData.clear();
                                    cplCollectionDataNotStarted.clear();
                                    Collections.sort(listCplCollectionData, CplCollectionData.sortByDate);
                                    for (int i = 0; i < listCplCollectionData.size(); i++) {
                                        if (listCplCollectionData.get(i).getStatus() == null) {
                                            cplCollectionDataNotStarted.add(listCplCollectionData.get(i));
                                        } else if (listCplCollectionData.get(i).getStatus() != null && listCplCollectionData.get(i).getStatus().equals("PASSED")) {
                                            completedCplCollectionData.add(listCplCollectionData.get(i));
                                        } else if (listCplCollectionData.get(i).getStatus() != null && listCplCollectionData.get(i).getStatus().equals("INPROGRESS")) {
                                            inProgressCplCollectionData.add(listCplCollectionData.get(i));
                                        }
                                    }
                                }

                                try {
                                    if (inProgressCplCollectionData.size() > 0) {
                                        Collections.sort(inProgressCplCollectionData, CplCollectionData.sortByDate);
                                        for (int j = 0; j < inProgressCplCollectionData.size(); j++) {
                                            setCplData(inProgressCplCollectionData.get(j));
                                            getCplExtraInfo(inProgressCplCollectionData.get(j).getCplId());
                                            break;
                                        }
                                    } else if (cplCollectionDataNotStarted.size() > 0) {
                                        Collections.sort(cplCollectionDataNotStarted, CplCollectionData.sortByDate);
                                        for (int k = 0; k < cplCollectionDataNotStarted.size(); k++) {
                                            setCplData(cplCollectionDataNotStarted.get(k));
                                            getCplExtraInfo(cplCollectionDataNotStarted.get(k).getCplId());
                                            break;
                                        }
                                    } else if (completedCplCollectionData.size() > 0) {
                                        Collections.sort(completedCplCollectionData, CplCollectionData.sortByDate);
                                        for (int j = 0; j < completedCplCollectionData.size(); j++) {
                                            setCplData(completedCplCollectionData.get(j));
                                            getCplExtraInfo(completedCplCollectionData.get(j).getCplId());
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };

            addToFireBaseRefList(message, cplDataListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void extractData(String cplkey, Map<String, Object> cplMainMap) {
        CplCollectionData cplCollectionData = new CplCollectionData();

        if (cplMainMap != null) {
            if (cplkey.contains("cpl")) {
                cplkey = cplkey.replace("cpl", "");
            }
            cplCollectionData.setCplId(cplkey);

            if (cplMainMap.containsKey("addedOn")) {
                cplCollectionData.setAssignedDate(OustSdkTools.convertToLong(cplMainMap.get("addedOn")));
            }
            if (cplMainMap.containsKey("addedOn")) {
                cplCollectionData.setCplAddOn((String) cplMainMap.get("addedOn"));
            }
            if (cplMainMap.containsKey("cplDescription")) {
                cplCollectionData.setCplDescription((String) cplMainMap.get("cplDescription"));
            }
            if (cplMainMap.containsKey("cplName")) {
                cplCollectionData.setCplName((String) cplMainMap.get("cplName"));
            }
            if (cplMainMap.containsKey("completionPercentage")) {
                cplCollectionData.setProgress(OustSdkTools.convertToLong(cplMainMap.get("completionPercentage")));
            }
            if (cplMainMap.containsKey("completedOn")) {
                cplCollectionData.setCompletedDate(OustSdkTools.convertToLong(cplMainMap.get("completedOn")));
            }
            if (cplMainMap.containsKey("earnedCoins")) {
                cplCollectionData.setCplCoins(OustSdkTools.convertToLong(cplMainMap.get("earnedCoins")));
            }
            if (cplMainMap.containsKey("totalCoins")) {
                cplCollectionData.setTotalCoins(OustSdkTools.convertToLong(cplMainMap.get("totalCoins")));
            }

            if (cplMainMap.containsKey("enrolled")) {
                cplCollectionData.setEnrolled((boolean) cplMainMap.get("enrolled"));
            }

            if (cplMainMap.containsKey("updateTime")) {
                cplCollectionData.setUserUpdateTime(OustSdkTools.convertToLong(cplMainMap.get("updateTime")));
            }
            if (cplMainMap.containsKey("status")) {
                cplCollectionData.setStatus((String) cplMainMap.get("status"));
            }

            if (cplMainMap.get("activeChildCPL") != null) {
                cplCollectionData.setActiveChildCPL(OustSdkTools.convertToStr(cplMainMap.get("activeChildCPL")));
            } else {
                cplCollectionData.setActiveChildCPL("");
            }
            listCplCollectionData.add(cplCollectionData);
        }
    }

    private void setCplData(CplCollectionData cplData) {
        try {
            cplCollectionData = new CplCollectionData();
            cplCollectionData.setBgImage(getString(cplData.getBgImage()));
            cplCollectionData.setBanner(getString(cplData.getBanner()));
            cplCollectionData.setIntroBgImg(getString(cplData.getIntroBgImg()));
            cplCollectionData.setIntroIcon(getString(cplData.getIntroIcon()));
            cplCollectionData.setAssessmentMarkerIcon(getString(cplData.getAssessmentMarkerIcon()));
            cplCollectionData.setCourseMarkerIcon(getString(cplData.getCourseMarkerIcon()));
            cplCollectionData.setFeedMarkerIcon(getString(cplData.getFeedMarkerIcon()));
            cplCollectionData.setEndFlagIcon(getString(cplData.getEndFlagIcon()));
            cplCollectionData.setPathColor(getString(cplData.getPathColor()));
            cplCollectionData.setNavIcon(getString(cplData.getNavIcon()));
            cplCollectionData.setProgress(cplData.getProgress());
            cplCollectionData.setPathStartIcon(getString(cplData.getPathStartIcon()));
            cplCollectionData.setMarkerLockIcon(getString(cplData.getMarkerLockIcon()));
            cplCollectionData.setCplVersion(getString(cplData.getCplVersion()));
            cplCollectionData.setProgressAfterAssessmentFail(cplData.isProgressAfterAssessmentFail());
            cplCollectionData.setRateCourse(getBoolean(cplData.isRateCourse()));
            cplCollectionData.setOustCoins(OustSdkTools.convertToLong(cplData.getOustCoins()));
            cplCollectionData.setCplCompletionImg(getString(cplData.getCplCompletionImg()));
            if (cplData.getUpdateTime() != 0) {
                cplCollectionData.setUpdateTime(cplData.getUpdateTime());
            }
            cplCollectionData.setNavIconStartAudio(getString(cplData.getNavIconStartAudio()));
            cplCollectionData.setNavIconMovingAudio(getString(cplData.getNavIconMovingAudio()));
            cplCollectionData.setCplCompleteAudio(getString(cplData.getCplCompleteAudio()));
            cplCollectionData.setCoinAddedAudio(getString(cplData.getCoinAddedAudio()));
            cplCollectionData.setCertificate(getBoolean(cplData.isCertificate()));
            cplCollectionData.setModuleCompletionImg(getString(cplData.getModuleCompletionImg()));
            cplCollectionData.setShowModuleCompletionAnimation(cplData.isShowModuleCompletionAnimation());
            cplCollectionData.setCplType(getString(cplData.getCplType()));
            cplCollectionData.setDisableBackButton((cplData.isDisableBackButton()) && getBoolean(cplData.isDisableBackButton()));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getCplExtraInfo(String cplId) {
        String cplInfoNode = ("cpl/cpl" + cplId);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    cplShown = false;
                    extractCplExtraInfo(dataSnapshot);
                } catch (Exception e) {
                    Log.e(TAG, "caught exception inside set singelton ", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: CPL base node not found");
            }
        };
        Log.d(TAG, "getCplExtraInfo: " + cplInfoNode);
        addToFireBaseRefList(cplInfoNode, eventListener);
    }

    private void extractCplExtraInfo(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
            Map<String, Object> cplInfoMap = (Map<String, Object>) dataSnapshot.getValue();
            if (cplInfoMap != null) {
                Log.d(TAG, "getCplData: " + "extractCplExtraInfo()");
                //Log.d(TAG, "extractCplExtraInfo: ");
                OustPreferences.saveTimeForNotification("cplNotificationFreq", OustSdkTools.convertToLong(cplInfoMap.get("notificationFrequency")));
                OustPreferences.saveAppInstallVariable("isCplBulletin", getBoolean(cplInfoMap.get("bulletinBoard")));
                OustPreferences.saveAppInstallVariable("isCplLeaderboard", getBoolean(cplInfoMap.get("leaderboard")));
                cplCollectionData.setBgImage(getString(cplInfoMap.get("bgImage")));
                cplCollectionData.setBanner(getString(cplInfoMap.get("banner")));
                cplCollectionData.setCplId(getString(cplInfoMap.get("elementId")));
                cplCollectionData.setIntroBgImg(getString(cplInfoMap.get("introBgImg")));
                cplCollectionData.setIntroIcon(getString(cplInfoMap.get("introIcon")));
                cplCollectionData.setCplVersion(getString(cplInfoMap.get("version"))); //cplType
                cplCollectionData.setCplType(getString(cplInfoMap.get("cplType")));
                cplCollectionData.setCplId(String.valueOf(cplInfoMap.get("contentPlayListId")));
                cplCollectionData.setCplName(String.valueOf(cplInfoMap.get("name")));

                if (cplInfoMap.get("enrolledUsers") != null) {
                    cplCollectionData.setEnrolledCount(OustSdkTools.newConvertToLong(cplInfoMap.get("enrolledUsers")));
                } else {
                    cplCollectionData.setEnrolledCount(0);
                }

                if (cplInfoMap.get("oustCoins") != null) {
                    cplCollectionData.setOustCoins(OustSdkTools.newConvertToLong(cplInfoMap.get("oustCoins")));
                } else {
                    cplCollectionData.setOustCoins(0);
                }

                if (cplInfoMap.get("showOnMainScreen") != null) {
                    cplCollectionData.setShowOnMainScreen((boolean) cplInfoMap.get("showOnMainScreen"));
                } else {
                    cplCollectionData.setShowOnMainScreen(false);
                }

                if (cplInfoMap.get("oustCoins") != null) {
                    cplCollectionData.setOustCoins(OustSdkTools.newConvertToLong(cplInfoMap.get("oustCoins")));
                } else {
                    cplCollectionData.setOustCoins(0);
                }
                if (cplInfoMap.get("enrolledUsers") != null) {
                    cplCollectionData.setEnrolledCount(OustSdkTools.newConvertToLong(cplInfoMap.get("enrolledUsers")));
                } else cplCollectionData.setEnrolledCount(0);


                if (!isCityScreenEnabled) {
                    // Language Screen enabled but not yet selected
                    if (isLanguageScreenEnabled && !OustPreferences.getAppInstallVariable(IS_LANG_SELECTED) && cplCollectionData != null) {
                        if (OustPreferences.get(AppConstants.StringConstants.TENANT_ID) != null &&
                                !OustPreferences.get(AppConstants.StringConstants.TENANT_ID).equalsIgnoreCase("swiggy")) { // cplCollectionData.getCplType().equalsIgnoreCase("GENERAL")
                            Log.d(TAG, "extractCplExtraInfo: Language Screen enabled but not yet selected: it is ");
                            if (cplCollectionData != null) {
                                openCPLView();
                            }
                        }
                    } else if (isLanguageScreenEnabled && OustPreferences.getAppInstallVariable(IS_LANG_SELECTED) &&
                            OustStaticVariableHandling.getInstance().isNewCplDistributed()) {
                        //language enabled and selected and distributed new cpl
                        if (cplCollectionData != null) {
                            openCPLView();
                        }
                    } else if (isLanguageScreenEnabled && OustPreferences.getAppInstallVariable(IS_LANG_SELECTED)) {
                        //language enabled and selected and distributed new cpl
                        if (cplCollectionData != null) {
                            openCPLView();
                        }
                    } else if (!isLanguageScreenEnabled) {
                        //language screen disabled
                        if (cplCollectionData != null) {
                            openCPLView();
                        }
                    }
                } else {
                    if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_CITY_SELECTED)) {
                        if (isLanguageScreenEnabled && OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_LANG_SELECTED)) {
                            if (cplCollectionData != null) {
                                openCPLView();
                            }
                        } else {
                            if (isCityScreenEnabled && !OustStaticVariableHandling.getInstance().isCplLanguageScreenOpen()) {
                                OustStaticVariableHandling.getInstance().setCplCityScreenOpen(true);
                                Intent intent = new Intent(LandingActivity.this, CitySelection.class);
                                intent.putExtra("allowBackPress", false);
                                intent.putExtra("FEED", false);
                                intent.putExtra("openLanguage", true);
                                startActivity(intent);
                            } else {
                                if (cplCollectionData != null) {
                                    openCPLView();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean getBoolean(Object o) {
        return o != null && (boolean) o;
    }

    protected String getString(Object o) {
        return o != null ? (String) o : "";
    }

    private void openCPLView() {
        try {
            if (!isDeepLink && !isMultipleCpl) {
                if (cplCollectionData != null) {
                    if (cplCollectionData.getCplVersion() != null && !cplCollectionData.getCplVersion().isEmpty() && cplCollectionData.getCplVersion().equals("V2")
                            && OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_COURSE_TAB)) {
                        if (!cplShown && !OustPreferences.getAppInstallVariable("IsAssessmentPlaying")) {
                            if (active) {
                                cplShown = true;
                                CplDataHandler.getInstance().setCplCollectionData(cplCollectionData);
                                OustStaticVariableHandling.getInstance().setNewCplDistributed(false);
                                Intent intent = new Intent(LandingActivity.this, CplIntroActivity.class);
                                intent.putExtra("cplId", cplCollectionData.getCplId());
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void checkForValidUser() {
        isValidateUserFirstTime = true;
        String keyMsg = "userValidity/" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/invalid";
        ValueEventListener avatarListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (null != snapshot.getValue()) {
                        boolean inValid = (boolean) snapshot.getValue();
                        if (inValid) {
                            isUserValidInDB();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        OustFirebaseTools.getRootRef().child(keyMsg).keepSynced(true);
        OustFirebaseTools.getRootRef().child(keyMsg).addValueEventListener(avatarListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(avatarListener, keyMsg));
    }

    private void isUserValidInDB() {
        try {
            String checkUserValidity_url = OustSdkApplication.getContext().getResources().getString(R.string.checkUserValidity_url);
            checkUserValidity_url = HttpManager.getAbsoluteUrl(checkUserValidity_url) + OustAppState.getInstance().getActiveUser().getStudentid();

            ApiCallUtils.doNetworkCall(Request.Method.GET, checkUserValidity_url, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {

                        if (response.has("Validity") || response.has("validity")) {
                            try {
                                if ((response.has("Validity") && response.getBoolean("Validity")) || (response.has("validity") && response.getBoolean("validity"))) {
                                    OustPreferences.clear("saveduserdatalist");
                                    onLogout();
                                    if (isValidateUserFirstTime) {
                                        isValidateUserFirstTime = false;
                                        if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.DISABLE_USER_ON_CPL_COMPLETION)) {
                                            OustSdkTools.showToast("You are marked as Invalid user, Please contact your system administrator");
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onLogout() {
        try {
            String signOutUrl = OustSdkApplication.getContext().getResources().getString(R.string.signout);
            signOutUrl = HttpManager.getAbsoluteUrl(signOutUrl);
            String userName = activeUser.getStudentid();
            String institutional_id = OustPreferences.get("tanentid");

            SignOutRequest signOutRequest = new SignOutRequest();
            if ((OustPreferences.get("gcmToken") != null)) {
                signOutRequest.setDeviceToken(OustPreferences.get("gcmToken"));
            }
            signOutRequest.setDeviceIdentity("android");
            signOutRequest.setStudentid(userName);
            signOutRequest.setInstitutionLoginId(institutional_id);
            signOutRequest.setDevicePlatformName("android");
            String authToken = OustPreferences.get("authToken");
            if (!TextUtils.isEmpty(authToken)) {
                signOutRequest.setAuthToken(authToken);

                final Gson gson = new Gson();
                String jsonParams = gson.toJson(signOutRequest);

                ApiCallUtils.doNetworkCall(Request.Method.PUT, signOutUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null && response.optBoolean("success"))
                            localLogout();
                        ApiCallUtils.setIsLoggedOut(true);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "unable to logout");
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401)
                            localLogout();
                        else
                            Toast.makeText(LandingActivity.this, "Something went wrong, unable to logout", Toast.LENGTH_SHORT).show();

                        OustSdkTools.hideProgressbar();
                    }
                });
            } else {
                localLogout();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        OustSdkTools.showProgressBar();
    }

    private void localLogout() {
        try {
            if (!OustLogDetailHandler.getInstance().isUserForcedOut()) {
                boolean isAppTutorialShow = OustPreferences.getAppInstallVariable(IS_APP_TUTORIAL_SHOWN);
                tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
                removeAllReminderNotification();
                OustDataHandler.getInstance().resetData();
                OustStaticVariableHandling.getInstance().setEnterpriseUser(false);
                OustAppState.getInstance().setLandingPageLive(false);
                OustStaticVariableHandling.getInstance().setAppActive(false);
                OustStaticVariableHandling.getInstance().setNewCplDistributed(false);
                OustStaticVariableHandling.getInstance().setFeeds(new ArrayList<>());


                OustSdkTools.showProgressBar();
                OustSdkTools.clearAlldataAndlogout();
                OustPreferences.saveAppInstallVariable("LOGOUT", true);
                if (isAppTutorialShow) {
                    if (activeUser != null) {
                        String tempValue = tenantName + "_" + activeUser.getStudentKey() + "_" + isAppTutorialShow;
                        OustPreferences.save(ORG_ID_USER_ID_APP_TUTORIAL_VIEWED, tempValue);
                    }
                }
                try {
                    Intent intent = new Intent().setComponent(new ComponentName("com.oustme.oustapp",
                            "com.oustme.oustapp.newLayout.view.activity.NewLoginScreenActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to launch AutoStart Screen ", e);
                }

                Branch.getInstance().logout();
                LandingActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void initBranch() {
        try {
            OustStaticVariableHandling.getInstance().setAppActive(true);
            Uri linkData = this.getIntent().getData();
            if (isBranchLink) {
                isBranchLink = false;
                if (branchLink != null && !branchLink.isEmpty()) {
                    linkData = Uri.parse(branchLink);
                    branchLink = null;
                }
            }

            Log.e(TAG, "ReferringParams " + referringParamsString);
            if (referringParamsString != null && !referringParamsString.isEmpty()) {
                branchIOHandle(referringParamsString);
            } else {
                Branch.sessionBuilder(this).withCallback((referringParams, error) -> {
                    if (error == null) {
                        if (referringParams != null) {
                            Log.e(TAG, "ReferringParams branch  " + referringParams);
                            branchIOHandle(referringParams.toString());
                        }
                    } else {
                        Log.i("BRANCH SDK", error.getMessage());
                    }
                }).withData(linkData).init();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void branchIOHandle(String referringParamsString) {
        try {
            isDeepLink = true;
            final Gson gson = new GsonBuilder().create();
            String tenantId = OustPreferences.get("tanentid").trim();
            if (activeUser != null) {
                Branch.getInstance().setIdentity(activeUser.getStudentKey() + "_" + tenantId);
            }

            if ((referringParamsString != null) && (!(referringParamsString).isEmpty())) {
                JSONObject jsonObject = new JSONObject(referringParamsString);
                if (jsonObject.has("+clicked_branch_link") && jsonObject.getString("+clicked_branch_link").equalsIgnoreCase("true")) {
                    Log.e(TAG, "BranchIO Data: " + jsonObject);
                    OustPreferences.save("referringParamsString", (referringParamsString));
                    BranchIoResponce branchIoResponse = gson.fromJson(referringParamsString, BranchIoResponce.class);
                    if (branchIoResponse.getOrgId() != null && branchIoResponse.getOrgId().equalsIgnoreCase(tenantId)) {
                        if (branchIoResponse.getModuleType() != null && !branchIoResponse.getModuleType().isEmpty()) {
                            if (branchIoResponse.getModuleType().equalsIgnoreCase("NormalCourse") || branchIoResponse.getModuleType().equalsIgnoreCase("MicroLearning")) {
                                BranchTools.checkModuleDistributionOrNot(activeUser, branchIoResponse.getCourseId(), "COURSE");
                            } else if (branchIoResponse.getModuleType().equalsIgnoreCase("MultilingualCourse")) {
                                BranchTools.checkModuleDistributionOrNot(activeUser, branchIoResponse.getCourseId(), "COURSE");
                            } else if (branchIoResponse.getModuleType().equalsIgnoreCase("QUANTITATIVE")) {
                                BranchTools.checkModuleDistributionOrNot(activeUser, branchIoResponse.getAssessmentId(), "ASSESSMENT");
                            } else if (branchIoResponse.getModuleType().equalsIgnoreCase("FEED")) {
                                BranchTools.gotoFeedPage(branchIoResponse.getFeedId(), activeUser);
                            } else if (branchIoResponse.getModuleType().equalsIgnoreCase("SURVEY")) {
                                BranchTools.gotoSurvey(branchIoResponse.getAssessmentId(), 0);
                            } else if (branchIoResponse.getModuleType().equalsIgnoreCase("CPL") || branchIoResponse.getModuleType().equalsIgnoreCase("PLAYLIST")) {
                                BranchTools.checkModuleDistributionOrNot(activeUser, branchIoResponse.getCplId(), "CPL");
                            } else if (branchIoResponse.getModuleType().equalsIgnoreCase("LiveClass")) {
                                if (branchIoResponse.getCourseId() != null && !branchIoResponse.getCourseId().isEmpty()) {
                                    long meeting_id = OustSdkTools.newConvertToLong(branchIoResponse.getCourseId());
                                    if (meeting_id != 0) {
                                        BranchTools.gotoCalendarPage(meeting_id);
                                    } else {
                                        branding_mani_layout.setVisibility(View.GONE);
                                    }
                                } else {
                                    branding_mani_layout.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            if ((branchIoResponse.getAssessmentId() != null) && (!branchIoResponse.getAssessmentId().isEmpty())) {
                                BranchTools.checkModuleDistributionOrNot(activeUser, branchIoResponse.getAssessmentId(), "ASSESSMENT");
                            } else if ((branchIoResponse.getCourseId() != null) && (!branchIoResponse.getCourseId().isEmpty())) {
                                BranchTools.checkModuleDistributionOrNot(activeUser, branchIoResponse.getCourseId(), "COURSE");
                            } else if ((branchIoResponse.getCplId() != null) && (!branchIoResponse.getCplId().isEmpty())) {
                                BranchTools.checkModuleDistributionOrNot(activeUser, branchIoResponse.getCplId(), "CPL");
                            } else if ((branchIoResponse.getCollectionId() != null) && (!branchIoResponse.getCollectionId().isEmpty())) {
                                BranchTools.gotoCollectionPage(branchIoResponse.getCollectionId());
                            } else if ((branchIoResponse.getOrgId() != null && !branchIoResponse.getOrgId().isEmpty()) &&
                                    (branchIoResponse.getUserId() != null && !branchIoResponse.getUserId().isEmpty())) {
                                if (!branchIoResponse.getOrgId().equalsIgnoreCase(tenantId)) {
                                    OustSdkTools.decryptBranchData(branchIoResponse, null);
                                    //TODO:handle logout pop up when logged in with other tenant
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            sendFeedClickedRequestToBackend();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendFeedClickedRequestToBackend() {
        try {
            String clickFeedList = OustPreferences.get("clickFeedList");
            clickedFeedDataArrayList = OustSdkTools.getClickedFeedList(clickFeedList);

            String viewFeedList = OustPreferences.get("viewFeedLIst");
            viewedFeedDataMap = OustSdkTools.getViewedFeed(viewFeedList);

            if ((clickedFeedDataArrayList != null && clickedFeedDataArrayList.size() > 0)
                    || (viewedFeedDataMap != null && viewedFeedDataMap.size() > 0)) {


                ClickedFeedRequestData clickedFeedRequestData = new ClickedFeedRequestData();
                clickedFeedRequestData.setClickedFeedDataList(clickedFeedDataArrayList);
                if (viewedFeedDataMap != null && viewedFeedDataMap.size() > 0) {
                    if (alreadyAddedFeedDataMap != null) {
                        Map tmp = new HashMap(viewedFeedDataMap);
                        tmp.keySet().removeAll(alreadyAddedFeedDataMap.keySet());
                        alreadyAddedFeedDataMap.putAll(tmp);
                    } else {
                        alreadyAddedFeedDataMap = new HashMap(viewedFeedDataMap);
                    }

                    ArrayList<ViewedFeedData> viewedFeedDataArrayList = new ArrayList<>(viewedFeedDataMap.values());
                    clickedFeedRequestData.setViewdFeedDataList(viewedFeedDataArrayList);
                }
                if (activeUser != null) {
                    clickedFeedRequestData.setStudentid(activeUser.getStudentid());
                }
                Gson gson = new Gson();
                String str = gson.toJson(clickedFeedRequestData);
                List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedFeedClickedRequests");
                requests.add(str);

                OustPreferences.saveLocalNotificationMsg("savedFeedClickedRequests", requests);

                Intent feedService = new Intent(LandingActivity.this, FeedUpdatingServices.class);
                startService(feedService);

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1444) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (Objects.requireNonNull(data).getExtras() != null) {
                        long position = data.getLongExtra("FeedPosition", 0);
                        long feedComment = data.getLongExtra("FeedComment", 0);
                        boolean isFeedChange = data.getBooleanExtra("isFeedChange", false);
                        boolean isFeedRemove = data.getBooleanExtra("FeedRemove", false);
                        long feedShareCount = data.getLongExtra("isFeedShareCount", 0);
                        long feedLikeCount = data.getLongExtra("isFeedLikeCount", 0);
                        boolean isLikeClicked = data.getBooleanExtra("isLikeClicked", false);
                        boolean isClicked = data.getBooleanExtra("isClicked", false);

                        if (position != 0 && isFeedChange) {
                            if (activeFragment == homeNavFragment) {
                                homeNavFragment.feedModifyItems(position, feedComment, feedShareCount, isFeedChange, feedLikeCount, isLikeClicked, isClicked);
                            }
                        }

                        if (position != 0 && isFeedRemove && isFeedChange) {
                            if (activeFragment == homeNavFragment) {
                                homeNavFragment.feedRemoveItem(position, isFeedChange, isFeedRemove);
                            }
                        }
                        OustPreferences.saveAppInstallVariable("feedNot", false);
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void SelectInitWelComePopViews(DTOUserFeedDetails.FeedDetails newFeed, Context applicationContext, FrameLayout landingActivity_lay) {
        try {
            if (mPopupWindow != null && mPopupWindow.isShowing())
                mPopupWindow.dismiss();
            LayoutInflater inflater = (LayoutInflater) applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            DisplayMetrics metrics = applicationContext.getResources().getDisplayMetrics();
            scrWidth = metrics.widthPixels;
            scrHeight = metrics.heightPixels;

            scrWidth = (int) (scrWidth - 0.1 * scrWidth);
            scrHeight = (int) (scrHeight - 0.1 * scrHeight);

            View customView = inflater.inflate(R.layout.welcome_popup_window, null);
            mPopupWindow = new PopupWindow(
                    customView,
                    scrWidth,
                    scrHeight
            );

            TextView title = customView.findViewById(R.id.textViewFeedTitlePop);
            LinearLayout linearLayout = customView.findViewById(R.id.linearLayoutStart);
            TextView desc = customView.findViewById(R.id.textViewFeedDescriptionpop);
            ImageView imageViewBg = customView.findViewById(R.id.imageViewBg);
            ImageView imageViewVlose = customView.findViewById(R.id.close);
            ImageView imageViewPlayVideo = customView.findViewById(R.id.imageViewVideoPlay);
            LinearLayout linearLayoutOverlay = customView.findViewById(R.id.linearLayoutBlackOverlay);
            LinearLayout linearLayoutContent = customView.findViewById(R.id.linearLayoutContent);

            newFeed.setCourseCardClass(getCardFromMap(newFeed.getCardInfo()));

            imageViewPlayVideo.setOnClickListener(v -> {
                if (newFeed.getCourseCardClass() != null && newFeed.getType() != null && newFeed.getType().equalsIgnoreCase("COURSE_CARD_L")) {
                    if (newFeed.getCourseCardClass().getCardMedia() != null && newFeed.getCourseCardClass().getCardMedia().size() != 0) {
                        if (newFeed.getCourseCardClass().getCardMedia().get(0).getMediaType() != null && !newFeed.getCourseCardClass().getCardMedia().get(0).getMediaType().isEmpty()) {
                            String mediaType = newFeed.getCourseCardClass().getCardMedia().get(0).getMediaType();

                            if (mediaType.equalsIgnoreCase("IMAGE") ||
                                    mediaType.equalsIgnoreCase("GIF") || mediaType.equalsIgnoreCase("AUDIO")) {
                                feedImageView(newFeed, false, false);
                            } else {
                                feedVideoView(newFeed, false, newFeed.getCourseCardClass().getCardMedia().get(0).getMediaPrivacy() != null &&
                                        newFeed.getCourseCardClass().getCardMedia().get(0).getMediaPrivacy().equals("PUBLIC"));
                            }
                            updateFeedViewed(newFeed, activeUser.getStudentKey());
                            onFeedClick(newFeed.getFeedId(), 0);
                            onFeedViewed(newFeed.getFeedId());
                        }
                    }
                }
                mPopupWindow.dismiss();
            });


            imageViewVlose.setOnClickListener(v -> mPopupWindow.dismiss());

            linearLayout.setOnClickListener(v -> {
                if (newFeed.getCourseCardClass() != null && newFeed.getType().equalsIgnoreCase("COURSE_CARD_L")) {
                    if (newFeed.getCourseCardClass() != null) {
                        if (newFeed.getCourseCardClass().getCardMedia() != null && newFeed.getCourseCardClass().getCardMedia().size() != 0) {
                            if (newFeed.getCourseCardClass().getCardMedia().get(0).getMediaType() != null && !newFeed.getCourseCardClass().getCardMedia().get(0).getMediaType().isEmpty()) {
                                String mediaType = newFeed.getCourseCardClass().getCardMedia().get(0).getMediaType();

                                if (mediaType.equalsIgnoreCase("IMAGE") ||
                                        mediaType.equalsIgnoreCase("GIF") || mediaType.equalsIgnoreCase("AUDIO")) {
                                    feedImageView(newFeed, false, false);
                                } else {
                                    feedVideoView(newFeed, false, newFeed.getCourseCardClass().getCardMedia().get(0).getMediaPrivacy() != null &&
                                            newFeed.getCourseCardClass().getCardMedia().get(0).getMediaPrivacy().equals("PUBLIC"));
                                }
                                updateFeedViewed(newFeed, activeUser.getStudentKey());
                                onFeedClick(newFeed.getFeedId(), 0);
                                onFeedViewed(newFeed.getFeedId());
                            }
                        }
                    }
                }
                mPopupWindow.dismiss();
            });

            if (newFeed.getHeader() != null) {
                title.setText(Html.fromHtml(newFeed.getHeader()));
                if (!newFeed.isTitleVisibale()) {
                    title.setVisibility(View.GONE);
                } else {
                    title.setVisibility(View.VISIBLE);
                }
            }
            if (newFeed.getContent() != null) {
                desc.setText(Html.fromHtml(newFeed.getContent()));
                if (!newFeed.isDescVisible()) {
                    desc.setVisibility(View.GONE);
                } else {
                    desc.setVisibility(View.VISIBLE);
                }
            }

            if (newFeed.getImageUrl() != null && !newFeed.getImageUrl().isEmpty() && !newFeed.getImageUrl().equals("")) {
                Glide.with(context).load(newFeed.getImageUrl())
                        .placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).
                        error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources()
                                .getString(R.string.feed_default))).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(new RequestOptions().override(720, 1280)).into(imageViewBg);
            }

            if (!newFeed.isTitleVisibale() && !newFeed.isDescVisible()) {
                linearLayoutContent.setVisibility(View.GONE);
                linearLayoutOverlay.setVisibility(View.GONE);
            }

//            landingActivity_lay.post(() -> {
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.showAtLocation(landingActivity_lay, Gravity.CENTER, 0, 0);
            dimBehind(mPopupWindow);
//            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public DTOCourseCard getCardFromMap(DTOUserFeedDetails.CardInfo cardInfo) {
        DTOCourseCard courseCardClass = new DTOCourseCard();
        if (cardInfo != null) {
            if (cardInfo.getBgImg() != null) {
                courseCardClass.setBgImg(cardInfo.getBgImg());
            }
            if (cardInfo.getCardBgColor() != null) {
                courseCardClass.setCardBgColor(cardInfo.getCardBgColor());
            }

            if (cardInfo.getCardSolutionColor() != null) {
                courseCardClass.setCardSolutionColor(cardInfo.getCardSolutionColor());
            }
            if (cardInfo.getCardTextColor() != null) {
                courseCardClass.setCardTextColor(cardInfo.getCardTextColor());
            }
            String cardType = "";
            if (cardInfo.getCardType() != null) {
                cardType = cardInfo.getCardType();
            }
            courseCardClass.setCardType(cardType);
            if (cardInfo.getClCode() != null) {
                courseCardClass.setClCode((String) cardInfo.getClCode());
            }

            if (cardInfo.getScormIndexFile() != null) {
                courseCardClass.setScormIndexFile(cardInfo.getScormIndexFile());
            }
            if (cardInfo.getContent() != null) {
                courseCardClass.setContent(cardInfo.getContent());
            }

            if (cardInfo.getCardTitle() != null) {
                courseCardClass.setCardTitle(cardInfo.getCardTitle());
            }

            if (cardInfo.getMandatoryViewTime() != 0) {
                courseCardClass.setMandatoryViewTime(cardInfo.getMandatoryViewTime());
            }

            if (cardInfo.getCardLayout() != null) {
                courseCardClass.setCardLayout(cardInfo.getCardLayout());
            }

            if (cardInfo.getCardId() != 0) {
                courseCardClass.setCardId(cardInfo.getCardId());
            }
            if (cardInfo.getLanguage() != null) {
                courseCardClass.setLanguage(cardInfo.getLanguage());
            }
            if (cardInfo.getXp() != 0) {
                courseCardClass.setXp(cardInfo.getXp());
            }
            courseCardClass.setShareToSocialMedia(cardInfo.isShareToSocialMedia());
            courseCardClass.setPotraitModeVideo(cardInfo.isPotraitModeVideo());

            List<DTOCourseCardMedia> courseCardMediaList = new ArrayList<>();
            if (cardInfo.getCardMediaList() != null) {
                List<DTOUserFeedDetails.CardMedia> mediaMap = cardInfo.getCardMediaList();
                if (mediaMap != null) {
                    for (int k = 0; k < mediaMap.size(); k++) {
                        if (mediaMap.get(k) != null) {
                            DTOCourseCardMedia courseCardMedia = new DTOCourseCardMedia();
                            if (mediaMap.get(k).getData() != null) {
                                courseCardMedia.setData(mediaMap.get(k).getData());
                            }
                            if (mediaMap.get(k).getGumletVideoUrl() != null) {
                                courseCardMedia.setGumletVideoUrl(mediaMap.get(k).getGumletVideoUrl());
                            }
                            if (mediaMap.get(k).getMediaType() != null) {
                                courseCardMedia.setMediaType(mediaMap.get(k).getMediaType());
                            }
                            courseCardMedia.setFastForwardMedia(mediaMap.get(k).isFastForwardMedia());
                            if (mediaMap.get(k).getMediaPrivacy() != null) {
                                courseCardMedia.setMediaPrivacy(mediaMap.get(k).getMediaPrivacy());
                            }
                            if (mediaMap.get(k).getMediaThumbnail() != null) {
                                courseCardMedia.setMediaThumbnail(mediaMap.get(k).getMediaThumbnail());
                            }
                            courseCardMediaList.add(courseCardMedia);
                        }
                    }
                }
            }
            courseCardClass.setCardMedia(courseCardMediaList);
            if ((courseCardClass.getXp() == 0)) {
                courseCardClass.setXp(100);
            }
            DTOUserFeedDetails.CardColorScheme cardColorSchemes = cardInfo.getCardColorScheme();
            if (cardColorSchemes != null) {
                DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                if (cardColorSchemes.getContentColor() != null) {
                    cardColorScheme1.setContentColor(cardColorSchemes.getContentColor());
                }
                if (cardColorSchemes.getBgImage() != null) {
                    cardColorScheme1.setBgImage(cardColorSchemes.getBgImage());
                }
                if (cardColorSchemes.getIconColor() != null) {
                    cardColorScheme1.setIconColor(cardColorSchemes.getIconColor());
                }
                if (cardColorSchemes.getLevelNameColor() != null) {
                    cardColorScheme1.setLevelNameColor(cardColorSchemes.getLevelNameColor());
                }
                courseCardClass.setCardColorScheme(cardColorScheme1);
            }

            try {
                DTOUserFeedDetails.ReadMoreData readmoremaps = cardInfo.getReadMoreData();
                if (readmoremaps != null) {
                    DTOReadMore readMoreData = new DTOReadMore();
                    if (readmoremaps.getData() != null) {
                        readMoreData.setData(readmoremaps.getData());
                    }
                    if (readmoremaps.getDisplayText() != null) {
                        readMoreData.setDisplayText(readmoremaps.getDisplayText());
                    }
                    if (readmoremaps.getRmId() != 0) {
                        try {
                            Object object = readmoremaps.getRmId();
                            if (object.getClass().equals(String.class)) {
                                readMoreData.setRmId(Long.parseLong((String) object));
                            } else if (object.getClass().equals(Long.class)) {
                                readMoreData.setRmId((long) object);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                    if (readmoremaps.getScope() != null) {
                        readMoreData.setScope(readmoremaps.getScope());
                    }
                    if (readmoremaps.getType() != null) {
                        readMoreData.setType(readmoremaps.getType());
                    }
                    courseCardClass.setReadMoreData(readMoreData);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            return courseCardClass;
        }
        return courseCardClass;
    }

    private void feedVideoView(DTOUserFeedDetails.FeedDetails newFeed, boolean isComment, boolean isYoutubeVideo) {
        try {
            Intent feedIntent;
            if (isYoutubeVideo) {
                feedIntent = new Intent(this, PublicVideoFeedCardScreen.class);
            } else {
                feedIntent = new Intent(this, VideoCardDetailScreen.class);
            }
            Bundle feedBundle = new Bundle();
            feedBundle.putParcelable("Feed", newFeed);
            feedBundle.putString("CardData", new Gson().toJson(newFeed.getCourseCardClass()));
            feedBundle.putSerializable("ActiveUser", activeUser);
            feedBundle.putBoolean("FeedComment", isComment);
//            feedBundle.putBoolean("isFeedLikeable", newFeed.isLikeble());
            feedIntent.putExtras(feedBundle);
            startActivity(feedIntent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static void dimBehind(PopupWindow popupWindow) {
        try {
            View container;
            if (popupWindow.getBackground() == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    container = (View) popupWindow.getContentView().getParent();
                } else {
                    container = popupWindow.getContentView();
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    container = (View) popupWindow.getContentView().getParent().getParent();
                } else {
                    container = (View) popupWindow.getContentView().getParent();
                }
            }
            Context context = popupWindow.getContentView().getContext();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
            p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            p.dimAmount = 0.8f;
            wm.updateViewLayout(container, p);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void feedImageView(DTOUserFeedDetails.FeedDetails feed, boolean isComment, boolean isAttach) {
        try {
            Intent feedIntent = new Intent(context, ImageFeedDetailScreen.class);
            Bundle feedBundle = new Bundle();
            feedBundle.putParcelable("Feed", feed);
            if (feed.getCourseCardClass() != null) {
                feedBundle.putString("CardData", new Gson().toJson(feed.getCourseCardClass()));
            }
            feedBundle.putSerializable("ActiveUser", activeUser);
            feedBundle.putBoolean("FeedComment", isComment);
            feedBundle.putBoolean("FeedAttach", isAttach);
            feedBundle.putString("feedType", feed.getType());
            feedIntent.putExtras(feedBundle);
            startActivity(feedIntent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateFeedViewed(DTOUserFeedDetails.FeedDetails mFeed, String studentKey) {
        try {
            if (!mFeed.isClicked()) {
                String message1 = "/landingPage/" + studentKey + "/specialFeed/" + AppConstants.StringConstants.IS_FEED_CLICKED;
                OustFirebaseTools.getRootRef().child(message1).setValue(true);
                OustFirebaseTools.getRootRef().child(message1).keepSynced(true);
                DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message1);
                firebase.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        currentData.setValue(true);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                        if (DatabaseError != null) {
                            Log.d("TAG", "Firebase counter increment failed. New Count:{}" + dataSnapshot);
                        } else {
                            Log.d("TAG", "Firebase counter increment succeeded.");
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            if (!mFeed.isClicked()) { // TODO need to handle feed is clicked or not
                long feedId = mFeed.getFeedId();
                String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + AppConstants.StringConstants.IS_FEED_CLICKED;
                OustFirebaseTools.getRootRef().child(message1).setValue(true);
                OustFirebaseTools.getRootRef().child(message1).keepSynced(true);
                DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message1);
                firebase.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        currentData.setValue(true);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                        if (DatabaseError != null) {
                            Log.e("", "Firebase counter increment failed. New Count:{}" + dataSnapshot);
                        } else {
                            Log.e("", "Firebase counter increment succeeded.");
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onFeedClick(long newFeedId, int cplId) {
        try {
            ClickedFeedData clickedFeedData = new ClickedFeedData();
            clickedFeedData.setFeedId((int) newFeedId);
            clickedFeedData.setCplId(cplId);
            clickedFeedData.setClickedTimestamp("" + System.currentTimeMillis());
            String clickFeedList = OustPreferences.get("clickFeedList");
            ArrayList<ClickedFeedData> clickedFeedDataArrayList = OustSdkTools.getClickedFeedList(clickFeedList);
            if (clickedFeedDataArrayList == null) {
                clickedFeedDataArrayList = new ArrayList<>();
            }
            clickedFeedDataArrayList.add(clickedFeedData);

            OustPreferences.save("clickFeedList", new Gson().toJson(clickedFeedDataArrayList));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void onFeedViewed(long newFeedId) {
        try {
            ViewedFeedData viewedFeedData = new ViewedFeedData();
            viewedFeedData.setFeedId((int) newFeedId);
            long milliSec = System.currentTimeMillis();
            viewedFeedData.setViewedTimestamp("" + milliSec);
            String viewFeedList = OustPreferences.get("viewFeedLIst");
            try {
                HashMap<String, ViewedFeedData> viewedFeedDataMap = OustSdkTools.getViewedFeed(viewFeedList);
                if (viewedFeedDataMap == null) {
                    viewedFeedDataMap = new HashMap<>();
                } else if (viewedFeedDataMap.isEmpty()) {
                    viewedFeedDataMap = new HashMap<>();
                }
                String objectKey = newFeedId + new SimpleDateFormat("MMMddyyyyHH:mm", Locale.getDefault()).format(new Date(milliSec));
                Log.d("FeedAnalytics", "Feed key " + objectKey);
                viewedFeedDataMap.put(objectKey, viewedFeedData);
                OustPreferences.save("viewFeedLIst", new Gson().toJson(viewedFeedDataMap));
            } catch (JSONException e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
                //viewedFeedDataMap = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getAppTutorialData() {
        String path = "/system/appTutorial";
        Log.d(TAG, "getAppTutorialData: " + path);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (null != dataSnapshot.getValue()) {
                        ArrayList<AppTutorialDataModel> appTutorialList = (ArrayList<AppTutorialDataModel>) dataSnapshot.getValue();
                        appTutorialDataModelArrayList = new ArrayList<>();
                        if (appTutorialList.size() > 0) {
                            for (int i = 0; i < appTutorialList.size(); i++) {
                                AppTutorialDataModel tabInfoData = new AppTutorialDataModel();
                                Map<Object, Object> tabInfoMap = (Map<Object, Object>) appTutorialList.get(i);
                                if (tabInfoMap != null) {
                                    tabInfoData.setMediaId(OustSdkTools.newConvertToLong(tabInfoMap.get("mediaId")));
                                    if (tabInfoMap.get("mediaType") == null || tabInfoMap.get("mediaType").equals("")) {
                                        tabInfoData.setMediaType("IMAGE");
                                    } else {
                                        tabInfoData.setMediaType((String) tabInfoMap.get("mediaType"));
                                    }
                                    tabInfoData.setMediaUrl((String) tabInfoMap.get("mediaUrl"));
                                    tabInfoData.setMediaSequence(String.valueOf(tabInfoMap.get("mediaSequence")));
                                    appTutorialDataModelArrayList.add(tabInfoData);
                                }
                            }
                            Intent intent = new Intent(LandingActivity.this, AppTutorialActivity.class);
                            intent.putParcelableArrayListExtra("DATA", appTutorialDataModelArrayList);
                            startActivity(intent);
                        } else {
                            checkForMultilingualCpl();
                        }
                    } else {
                        checkForMultilingualCpl();
                    }
                } catch (Exception e) {
                    checkForMultilingualCpl();
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                OustSdkTools.showToast("Unable to access firebase. Proceeding...");
                checkForMultilingualCpl();
            }
        };
        OustFirebaseTools.getRootRef().child(path).keepSynced(true);
        OustFirebaseTools.getRootRef().child(path).addListenerForSingleValueEvent(valueEventListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(valueEventListener, path));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OustPreferences.getAppInstallVariable("After_App_Tutorial_open_CplIntroPage")) {
            OustPreferences.saveAppInstallVariable("After_App_Tutorial_open_CplIntroPage", false);
            checkForMultilingualCpl();
        }
        if (OustStaticVariableHandling.getInstance().getResult_code() == 1444) {
            long position = OustStaticVariableHandling.getInstance().getFeedId();
            long feedComment = OustStaticVariableHandling.getInstance().getNumOfComments();
            boolean isFeedChange = OustStaticVariableHandling.getInstance().isFeedChanged();
            boolean isFeedRemove = OustStaticVariableHandling.getInstance().isFeedRemove();
            long feedShareCount = OustStaticVariableHandling.getInstance().getNumOfShares();
            long feedLikeCount = OustStaticVariableHandling.getInstance().getNumOfLikes();
            boolean isLikeClicked = OustStaticVariableHandling.getInstance().isLikeClicked();
            boolean isClicked = OustStaticVariableHandling.getInstance().isClicked();

            if (position != 0 && isFeedChange) {
                if (activeFragment == homeNavFragment) {
                    homeNavFragment.feedModifyItems(position, feedComment, feedShareCount, isFeedChange, feedLikeCount, isLikeClicked, isClicked);
                }
            }

            if (position != 0 && isFeedRemove && isFeedChange) {
                if (activeFragment == homeNavFragment) {
                    homeNavFragment.feedRemoveItem(position, isFeedChange, isFeedRemove);
                }
            }
            OustPreferences.saveAppInstallVariable("feedNot", false);
            OustStaticVariableHandling.getInstance().setResult_code(0);
        }
    }
}
