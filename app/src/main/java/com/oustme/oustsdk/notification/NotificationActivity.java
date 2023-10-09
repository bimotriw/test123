package com.oustme.oustsdk.notification;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;
import static com.oustme.oustsdk.util.AchievementUtils.convertDate;
import static com.oustme.oustsdk.util.NotificationUtils.findInBetweenDateCount;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.notification.adapter.NotificationListAdapter;
import com.oustme.oustsdk.notification.model.NotificationResponse;
import com.oustme.oustsdk.notification.viewModel.NotificationViewModel;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.service.GCMType;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.BranchTools;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NotificationActivity extends BaseActivity {

    private int bgColor;
    private int color;
    private ImageView backButton;
    private TextView screenName;
    private RecyclerView notificationRecycleView;

    //Branding loader
    private RelativeLayout branding_mani_layout;
    //End
    private ActiveUser activeUser;
    private NotificationViewModel notificationViewModel;
    private NotificationListAdapter notificationListAdapter;
    private ArrayList<NotificationResponse> notificationResponses = new ArrayList<>();
    private final ArrayList<NotificationResponse> removeNotificationFromFireBase = new ArrayList<>();
    private final ArrayList<NotificationResponse> remainingNotificationFromFireBase = new ArrayList<>();
    private View no_data_layout;
    private ImageView no_image;
    private TextView no_data_content;

    @Override
    protected int getContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        return R.layout.activity_notification;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void initView() {
        activeUser = OustAppState.getInstance().getActiveUser();

        Toolbar notificationToolbar = findViewById(R.id.toolbar_notification_layout);
        notificationRecycleView = findViewById(R.id.notification_recycler_view);
        backButton = findViewById(R.id.back_button);
        screenName = findViewById(R.id.screen_name);
        no_data_layout = findViewById(R.id.notification_no_data_layout);
        no_image = no_data_layout.findViewById(R.id.no_image);
        no_data_content = no_data_layout.findViewById(R.id.no_data_content);

        //Branding loader
        branding_mani_layout = findViewById(R.id.branding_main_layout);
        ImageView branding_bg = branding_mani_layout.findViewById(R.id.branding_bg);
        ImageView branding_icon = branding_mani_layout.findViewById(R.id.brand_loader);
        //End

        getColors();
        notificationToolbar.setBackgroundColor(bgColor);
        screenName.setTextColor(color);
        OustResourceUtils.setDefaultDrawableColor(backButton.getDrawable(), color);
        notificationToolbar.setTitle("");
        screenName.setText(getResources().getString(R.string.notifications));
        setSupportActionBar(notificationToolbar);

        try {
            activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            String tenantId = OustPreferences.get("tanentid");

            if (tenantId != null && !tenantId.isEmpty()) {
                File brandingBg = new File(OustSdkApplication.getContext().getFilesDir(), ("oustlearn_" + tenantId.toUpperCase().trim() + "splashScreen"));

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

    private void getColors() {
        if (OustPreferences.getAppInstallVariable("isLayout4")) {
            color = OustResourceUtils.getColors();
            bgColor = OustResourceUtils.getToolBarBgColor();
        } else {
            bgColor = OustResourceUtils.getColors();
            color = OustResourceUtils.getToolBarBgColor();
        }
    }

    @Override
    protected void initData() {
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(getApplicationContext());
        }
        OustSdkTools.setLocale(NotificationActivity.this);
    }

    @Override
    protected void initListener() {
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        backButton.setOnClickListener(view -> onBackPressed());
        showLoader();
        notificationViewModel.initData(NotificationActivity.this);
        notificationViewModel.getNotificationData().observe(this, notificationComponentModelData -> {
            try {
                removeNotificationFromFireBase.clear();
                remainingNotificationFromFireBase.clear();
                notificationResponses = (ArrayList<NotificationResponse>) RoomHelper.getNotificationsData(activeUser.getStudentKey());
                if (notificationComponentModelData != null) {
                    if (notificationResponses.size() > 0) {
                        no_data_content.setVisibility(View.GONE);
                        no_image.setVisibility(View.GONE);
                        notificationRecycleView.setVisibility(View.VISIBLE);
                        for (int i = 0; i < notificationResponses.size(); i++) {
                            if (notificationResponses.get(i).getFireBase() != null) {
                                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                                String count = null;
                                if (notificationResponses.get(i).getUpdateTime() != null) {
                                    String convertedDate = convertDate(String.valueOf(notificationResponses.get(i).getUpdateTime()), "yyyy-MM-dd HH:mm:ss");
                                    count = findInBetweenDateCount(currentDate, convertedDate);
                                }
                                if (count != null) {
                                    if (Integer.parseInt(count) >= 30) {
                                        removeNotificationFromFireBase.add(notificationResponses.get(i));
                                    } else {
                                        remainingNotificationFromFireBase.add(notificationResponses.get(i));
                                    }
                                }
                            }
                        }
                        setData(remainingNotificationFromFireBase);
                        //Removing Notifications
                        if (removeNotificationFromFireBase.size() > 0) {
                            ArrayList<NotificationResponse> separateFireBaseData = new ArrayList<>();
                            for (int i = 0; i < removeNotificationFromFireBase.size(); i++)
                                if (removeNotificationFromFireBase.get(i).getFireBase()) {
                                    separateFireBaseData.add(removeNotificationFromFireBase.get(i));
                                } else {
                                    RoomHelper.deleteOfflineNotifications(removeNotificationFromFireBase.get(i).getContentId());
                                }
                            if (separateFireBaseData.size() > 0) {
                                notificationViewModel.removeNotifications(separateFireBaseData);
                                for (int i = 0; i < separateFireBaseData.size(); i++) {
                                    RoomHelper.deleteOfflineNotifications(separateFireBaseData.get(i).getContentId());
                                }
                            }
                            //End
                        } else {
                            Log.d("TAG", "initListener removeNotificationFromFireBase-> : " + removeNotificationFromFireBase.size());
                        }
                        //End
                    } else {
                        noDataFound();
                    }
                }
                hideLoader();
            } catch (Exception e) {
                hideLoader();
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setData(ArrayList<NotificationResponse> notificationResponses) {
        try {
            notificationListAdapter = new NotificationListAdapter();
            notificationListAdapter.setNotificationListAdapter(notificationResponses, getApplicationContext(), new NotificationListAdapter.SelectNotification() {
                @Override
                public void selectedPosition(NotificationResponse notificationResponse) {
                    showSelectedNotification(notificationResponse);
                }

                @Override
                public void searchModuleCount(ArrayList<NotificationResponse> userMessageLists) {
                    if (userMessageLists.size() == 0) {
                        noSearchDataFound();
                    } else {
                        no_data_layout.setVisibility(View.GONE);
                        notificationRecycleView.setVisibility(View.VISIBLE);
                    }
                }
            });
            notificationRecycleView.setLayoutManager(new LinearLayoutManager(this));
            notificationRecycleView.setItemAnimator(new DefaultItemAnimator());
            notificationRecycleView.setAdapter(notificationListAdapter);
            notificationListAdapter.notifyDataSetChanged();

            SimpleNotificationItemTouchHelperCallback simpleNotificationItemTouchHelperCallback = new SimpleNotificationItemTouchHelperCallback(this) {
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    try {
                        String keyAndStudentKey = notificationResponses.get(viewHolder.getAbsoluteAdapterPosition()).getKey();
                        String keyValue = keyAndStudentKey.substring(0, keyAndStudentKey.indexOf("_"));
                        String contentId = notificationResponses.get(viewHolder.getAbsoluteAdapterPosition()).getContentId();
                        boolean checkFireBaseOrNot = notificationResponses.get(viewHolder.getAbsoluteAdapterPosition()).getFireBase();
                        RoomHelper.deleteOfflineNotifications(contentId);
                        notificationListAdapter.removeItem(viewHolder.getAbsoluteAdapterPosition());
                        if (checkFireBaseOrNot) {
                            String message = "/landingPage/" + activeUser.getStudentKey() + "/pushNotifications/" + keyValue;
                            OustFirebaseTools.getRootRef().child(message).removeValue((error, ref) -> {
                                Log.e(TAG, "onComplete: remove-->");
                                notificationListAdapter.notifyDataSetChanged();
                            });
                        } else {
                            RoomHelper.deleteOfflineNotifications(notificationResponses.get(viewHolder.getAbsoluteAdapterPosition()).getContentId());
                            notificationListAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            };

            ItemTouchHelper itemTouchhelper = new ItemTouchHelper(simpleNotificationItemTouchHelperCallback);
            itemTouchhelper.attachToRecyclerView(notificationRecycleView);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
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
                    if (notificationResponses.size() > 0) {
                        notificationListAdapter.getFilter().filter(query);
                    } else {
                        noDataFound();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (notificationResponses.size() > 0) {
                        notificationListAdapter.getFilter().filter(newText);
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

    private void showLoader() {
        try {
            branding_mani_layout.setVisibility(View.VISIBLE);
            branding_mani_layout.bringToFront();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hideLoader() {
        try {
            branding_mani_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showSelectedNotification(NotificationResponse notificationResponse) {
        try {
            String moduleId = notificationResponse.getContentId();
            String commentId = notificationResponse.getCommentId();
            String noticeBoardId = notificationResponse.getNoticeBoardId();
            String replyId = notificationResponse.getReplyId();
            String keyValue = notificationResponse.getKey();
            String result = keyValue.substring(0, keyValue.indexOf("_"));
            String readParameter = "read";
            activeUser = OustAppState.getInstance().getActiveUser();
            String message = "/landingPage/" + activeUser.getStudentKey() + "/pushNotifications/" + result + "/" + readParameter;
            OustFirebaseTools.getRootRef().child(message).setValue(false);


            if (notificationResponse.getType().equalsIgnoreCase(GCMType.COURSE_DISTRIBUTE.name()) || notificationResponse.getType().equalsIgnoreCase(GCMType.COURSE_REMINDER.name())) {
                if ((moduleId != null) && (!moduleId.isEmpty())) {
                    BranchTools.checkModuleDistributionOrNot(activeUser, moduleId, "COURSE");
                }
            } else if (notificationResponse.getType().equalsIgnoreCase(GCMType.ASSESSMENT_DISTRIBUTE.name()) || notificationResponse.getType().equalsIgnoreCase(GCMType.ASSESSMENT_REMINDER.name())) {
                if ((moduleId != null) && (!moduleId.isEmpty())) {
                    BranchTools.checkModuleDistributionOrNot(activeUser, moduleId, "ASSESSMENT");
                }
            } else if (notificationResponse.getType().equalsIgnoreCase(GCMType.FEED_DISTRIBUTE.name())) {
                if ((moduleId != null) && (!moduleId.isEmpty())) {
                    BranchTools.gotoFeedPage(moduleId, activeUser);
                }
            } else if (notificationResponse.getType().equalsIgnoreCase(GCMType.NOTICE_BOARD_DISTRIBUTION.name())) {
                if ((moduleId != null) && (!moduleId.isEmpty())) {
                    Log.e(TAG, "showSelectedNotification: " + moduleId);
                    BranchTools.gotoNoticeBoardPage(moduleId, activeUser.getStudentKey(), commentId, noticeBoardId, replyId, notificationResponse.getType());
                }
            } else if (notificationResponse.getType().equalsIgnoreCase(GCMType.NOTICEBOARD_POST.name())) {
                BranchTools.gotoNoticeBoardPage(moduleId, activeUser.getStudentKey(), commentId, noticeBoardId, replyId, notificationResponse.getType());
            } else if (notificationResponse.getType().equalsIgnoreCase(GCMType.NOTICEBOARD_COMMENT.name())) {
                BranchTools.gotoNoticeBoardPage(moduleId, activeUser.getStudentKey(), commentId, noticeBoardId, replyId, notificationResponse.getType());
            } else if (notificationResponse.getType().equalsIgnoreCase(GCMType.NOTICEBOARD_REPLY.name())) {
                BranchTools.gotoNoticeBoardPage(moduleId, activeUser.getStudentKey(), commentId, noticeBoardId, replyId, notificationResponse.getType());
            } else if (notificationResponse.getType().equalsIgnoreCase(GCMType.CATALOGUE_DISTRIBUTED.name())) {
                if ((moduleId != null) && (!moduleId.isEmpty())) {
                    OustPreferences.saveTimeForNotification("catalogueId", Long.parseLong(moduleId));
                    BranchTools.checkCatalogExistOrNot(Long.parseLong(moduleId), activeUser);
                }
            } else if (notificationResponse.getType().equalsIgnoreCase(GCMType.CPL_DISTRIBUTE.name())) {
                if ((moduleId != null) && (!moduleId.isEmpty())) {
                    BranchTools.checkModuleDistributionOrNot(activeUser, moduleId, "CPL");
                }
            } else if (notificationResponse.getType().equalsIgnoreCase(GCMType.ML_CPL_DISTRIBUTE.name())) {
                if ((moduleId != null) && (!moduleId.isEmpty())) {
                    BranchTools.checkMlCPLDistributionOrNot(activeUser, moduleId);
                }
            } else if (notificationResponse.getType().equalsIgnoreCase(GCMType.MEETING_DISTRIBUTED.name())) {
                if ((moduleId != null) && (!moduleId.isEmpty())) {
                    BranchTools.gotoCalendarPage(OustSdkTools.newConvertToLong(moduleId));
                }
            } else if (notificationResponse.getType().equalsIgnoreCase(GCMType.PUBLIC_NOTIFICATION.name())) {
                if ((moduleId != null) && (!moduleId.isEmpty())) {
                    BranchTools.getUserFFContest(activeUser.getStudentKey(), activeUser.getAvatar(), activeUser.getUserDisplayName(), moduleId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            notificationResponses.clear();
            notificationResponses = (ArrayList<NotificationResponse>) RoomHelper.getNotificationsData(activeUser.getStudentKey());
            if (notificationResponses.size() > 0) {
                setData(notificationResponses);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void noSearchDataFound() {
        try {
            notificationRecycleView.setVisibility(View.GONE);
            no_data_layout.setVisibility(View.VISIBLE);
            no_image.setVisibility(View.VISIBLE);
            no_data_content.setVisibility(View.VISIBLE);
            no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_catalogue));
            no_data_content.setText(getResources().getString(R.string.no_search_found));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void noDataFound() {
        try {
            notificationRecycleView.setVisibility(View.GONE);
            no_data_layout.setVisibility(View.VISIBLE);
            no_image.setVisibility(View.VISIBLE);
            no_data_content.setVisibility(View.VISIBLE);
            no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_feed));
            no_data_content.setText(getResources().getString(R.string.no_notification_found));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}