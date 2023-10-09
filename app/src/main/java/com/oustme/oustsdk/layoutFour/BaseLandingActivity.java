package com.oustme.oustsdk.layoutFour;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk._utils.BaseActivity;
import com.oustme.oustsdk.layoutFour.data.Navigation;
import com.oustme.oustsdk.layoutFour.data.ToolbarItem;
import com.oustme.oustsdk.layoutFour.data.ToolbarModel;
import com.oustme.oustsdk.notification.model.NotificationResponse;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseLandingActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    int color;
    int bgColor;
    //private String toolbarColorCode;
    Toolbar toolbar;
    ImageView brand_logo;
    TextView tenant_name;
    MenuItem itemNotification;
    RelativeLayout layout_loader;
    BottomNavigationView navigation;
    Menu navigationMenu;
    LandingLayoutViewModel landingLayoutViewModel;
    boolean alertEnable;
    boolean calendarEnable;
    boolean leaderBoardEnable;
    boolean searchEnable;
    List<Navigation> listBottomNav;
    List<Navigation> listNavAll;
    int selectedPos = -1;
    private ActiveUser activeUser;


    @Override
    protected int getContentView() {
        return R.layout.activity_landing;
    }

    @Override
    protected void initView() {
        toolbar = findViewById(R.id.toolbar);
        brand_logo = findViewById(R.id.brand_logo);
        tenant_name = findViewById(R.id.tenant_name);
        layout_loader = findViewById(R.id.layout_loader);
        navigation = findViewById(R.id.bottomNavigationView);
        navigationMenu = navigation.getMenu();
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(BaseLandingActivity.this);
        }
        OustSdkTools.setLocale(BaseLandingActivity.this);
        OustPreferences.saveAppInstallVariable("isLayout4", true);
        fetchLandingLayout();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.newlandingmenu, menu);

        MenuItem itemAlert = menu.findItem(R.id.action_alert);
        //itemAlert.getIcon().setColorFilter(colorFilter);
        Drawable alertDrawable = getResources().getDrawable(R.drawable.ic_landing_notification);
        itemAlert.setIcon(OustResourceUtils.setDefaultDrawableColor(alertDrawable, color));
        itemAlert.setVisible(isAlertEnable());

        itemNotification = menu.findItem(R.id.notification_alert);
        Drawable notificationDrawable = getResources().getDrawable(R.drawable.ic_landing_notification);
        itemNotification.setIcon(OustResourceUtils.setDefaultDrawableColor(notificationDrawable, color));
        itemNotification.setVisible(true);

        MenuItem itemSearch = menu.findItem(R.id.action_search);
        //itemSearch.getIcon().setColorFilter(colorFilter);
        Drawable searchDrawable = getResources().getDrawable(R.drawable.ic_landing_search);
        itemSearch.setIcon(OustResourceUtils.setDefaultDrawableColor(searchDrawable, color));
        //itemSearch.setVisible(isSearchEnable());
        itemSearch.setVisible(false);

        MenuItem itemCalendar = menu.findItem(R.id.action_calendar);
        Drawable calendarDrawable = getResources().getDrawable(R.drawable.ic_calendar_icon);
        itemCalendar.setIcon(OustResourceUtils.setDefaultDrawableColor(calendarDrawable, color));
        itemCalendar.setVisible(isCalendarEnable());

        MenuItem itemLeaderBoard = menu.findItem(R.id.action_leaderBoard);
        //itemLeaderBoard.getIcon().setColorFilter(colorFilter);
        Drawable leaderBoardDrawable = getResources().getDrawable(R.drawable.ic_landing_leader_board);
        itemLeaderBoard.setIcon(OustResourceUtils.setDefaultDrawableColor(leaderBoardDrawable, color));
        itemLeaderBoard.setVisible(isLeaderBoardEnable());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_alert) {
            onAlert();
        } else if (itemId == R.id.action_search) {
            onSearch();
        } else if (itemId == R.id.action_leaderBoard) {
            onLeaderBoard();
        } else if (itemId == R.id.action_calendar) {
            onCalendar();
        } else if (itemId == R.id.notification_alert) {
            Drawable notificationDrawable = getResources().getDrawable(R.drawable.ic_landing_notification);
            itemNotification.setIcon(OustResourceUtils.setDefaultDrawableColor(notificationDrawable, color));
            onNotification();
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (navigation.getMenu().size() > 0) {
            for (int i = 0; i < navigation.getMenu().size(); i++) {
                if (navigation.getMenu().getItem(i).getItemId() == menuItem.getItemId()) {
                    OustResourceUtils.setMenuIconSelected(navigation.getMenu().getItem(i), listBottomNav.get(i).getNavIcon());
                } else {
                    OustResourceUtils.setMenuIcon(navigation.getMenu().getItem(i), listBottomNav.get(i).getNavIcon());
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
        layout_loader.setVisibility(View.GONE);
        onNav(selectedPos);
        return true;
    }

    private void getColors() {

        try {
            color = OustResourceUtils.getColors();
            bgColor = OustResourceUtils.getToolBarBgColor();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            color = OustSdkTools.getColorBack(R.color.lgreen);
        }

    }


    private void setToolbar() {
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

    private void setNavigation() {
        Log.d(TAG, "setNavigation: ");
        //getColors();
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

    private void fetchLandingLayout() {
        activeUser = OustAppState.getInstance().getActiveUser();
        if (OustPreferences.get("userdata") != null && !OustPreferences.get("userdata").isEmpty()) {
            activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
        }
        landingLayoutViewModel = new ViewModelProvider(this).get(LandingLayoutViewModel.class);
        landingLayoutViewModel.init(this);
        landingLayoutViewModel.getBottomNavList().observe(this, landingLayout -> {
            if (landingLayout == null)
                return;
            setToolbar();
            setNavigation();
            listNavAll = landingLayout.getProfileNavigation();
            extractBottomNav(landingLayout.getTabNavigation());
            setToolbarItem(landingLayout.getToolbar());
        });

        landingLayoutViewModel.getNotificationCount().observe(this, notificationResponses -> {
            Log.e("TAG", "fetchLandingLayout:''''''''' ");
            ArrayList<NotificationResponse> fireBaseNotifications = new ArrayList<>();
            ArrayList<NotificationResponse> roomDbNotifications;
            roomDbNotifications = (ArrayList<NotificationResponse>) RoomHelper.getNotificationsData(activeUser.getStudentKey());
            fireBaseNotifications.clear();
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
                        //itemNotification.setIcon(R.drawable.alertreddot_new);
                        Log.e(TAG, "fetchLandingLayout: -------");
                        Drawable notificationDrawable = getResources().getDrawable(R.drawable.alertreddot_new);
                        itemNotification.setIcon(OustResourceUtils.setDefaultDrawableColor(notificationDrawable, color));
                        // show
                    }
                } else {
                    Log.e(TAG, "fetchLandingLayout: ----//---");

                    Drawable notificationDrawable = getResources().getDrawable(R.drawable.alertreddot_new);
                    itemNotification.setIcon(OustResourceUtils.setDefaultDrawableColor(notificationDrawable, color));
                    // itemNotification.setIcon(R.drawable.alertreddot_new);
                    // show Notification icon
                }
            } else {
                Log.e("TAG", "fetchLandingLayout : : :");
                // hide Notification icon
            }
        });
    }

    private void extractBottomNav(List<Navigation> navigationList) {
        try {
            if (navigationList != null && navigationList.size() != 0) {
                int navItemCount = navigationList.size() <= 5 ? navigationList.size() : 4;
                listBottomNav = new ArrayList<>();
                for (int i = 0; i < navItemCount; i++) {
                    if (navigationList.get(i) != null) {
                        listBottomNav.add(navigationList.get(i));
                    }
                }
                setListBottomNavFragment();
                setNavigationItems(listBottomNav);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setNavigationItems(List<Navigation> navigationList) {
        try {
            if (navigationList != null && navigationList.size() != 0) {
                int navItemCount = navigationList.size();
                for (int i = 0; i < navItemCount; i++) {
                    navigationMenu.add(Menu.NONE, i + 1, Menu.NONE, navigationList.get(i).getNavName())
                            .setIcon(R.drawable.ic_home);
                }
                for (int i = 0; i < navItemCount; i++) {
                    SpannableString spannableString = new SpannableString(navigationList.get(i).getNavName());
                    spannableString.setSpan(new RelativeSizeSpan(0.75f), 0, spannableString.length(), 0);
                    navigation.getMenu().getItem(i).setTitle(spannableString);
                    OustResourceUtils.setMenuIcon(navigation.getMenu().getItem(i), navigationList.get(i).getNavIcon());
                }
                if (selectedPos < 0) {
                    navigation.setSelectedItemId(navigation.getMenu().getItem(0).getItemId());
                    OustResourceUtils.setMenuIconSelected(navigation.getMenu().getItem(0), navigationList.get(0).getNavIcon());
                } else {
                    OustResourceUtils.setMenuIconSelected(navigation.getMenu().getItem(selectedPos), navigationList.get(selectedPos).getNavIcon());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }


    private void setToolbarItem(ToolbarModel toolbarModel) {
        if (toolbarModel == null)
            return;

        if (toolbarModel.getContentColor() != null && !toolbarModel.getContentColor().isEmpty())
            OustPreferences.save("toolbarColorCode", toolbarModel.getContentColor());

        if (toolbarModel.getBgColor() != null && !toolbarModel.getBgColor().isEmpty())
            OustPreferences.save("toolbarBgColor", toolbarModel.getBgColor());

        if (toolbarModel.getContent() != null)
            for (ToolbarItem toolbarItem : toolbarModel.getContent()) {
                switch (toolbarItem.getAction()) {
                    case "notification":
                        alertEnable = toolbarItem.isEnable();
                        break;
                    case "calendar":
                        calendarEnable = toolbarItem.isEnable();
                        break;
                    case "leaderBoard":
                        leaderBoardEnable = toolbarItem.isEnable();
                        break;
                    case "search":
                        searchEnable = toolbarItem.isEnable();
                        break;

                }
            }
        invalidateOptionsMenu();
    }

    private boolean isSearchEnable() {
        return searchEnable;
    }

    private boolean isLeaderBoardEnable() {
        return leaderBoardEnable;
    }

    private boolean isAlertEnable() {
        return alertEnable;
    }

    private boolean isCalendarEnable() {
        return calendarEnable;
    }

    public List<Navigation> getListBottomNav() {
        return listBottomNav;
    }

    public List<Navigation> getListNavAll() {
        return listNavAll;
    }

    abstract void onSearch();

    abstract void onLeaderBoard();

    abstract void onCalendar();

    abstract void onAlert();

    abstract void onNav(int position);

    abstract void setListBottomNavFragment();

    abstract void onNotification();

}
