package com.oustme.oustsdk.profile;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.profile.fragment.BadgeFragment;
import com.oustme.oustsdk.profile.fragment.CertificatesFragment;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.Objects;

public class AchievementTabActivity extends AppCompatActivity {

    private TabLayout achievementTabs;
    private TextView toolBar_text;
    private ImageView backArrow;
    private Toolbar achievementToolBar;
    private int color;
    private int bgColor;
    private String selected_tab = "Certificates";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_tab);
        initView();
        initData();
        initListener();
    }

    protected void initView() {
        achievementTabs = findViewById(R.id.tab_layout_achievement);
        achievementToolBar = findViewById(R.id.toolbar_Achievement);
        toolBar_text = findViewById(R.id.screen_name_achievement);
        backArrow = findViewById(R.id.back_button_achievement);

        getColors();
        achievementToolBar.setBackgroundColor(bgColor);
        toolBar_text.setTextColor(color);
        OustResourceUtils.setDefaultDrawableColor(backArrow.getDrawable(), color);
        setSupportActionBar(achievementToolBar);

        TabLayout.Tab tab1 = achievementTabs.newTab();
        tab1.setText(R.string.certificate);
        tab1.setIcon(R.drawable.certificate_icon);

        TabLayout.Tab tab2 = achievementTabs.newTab();
        tab2.setText(R.string.badges);
        tab2.setIcon(R.drawable.badge_icon);
        achievementTabs.addTab(tab1);
        achievementTabs.addTab(tab2);

        achievementTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selected_tab = Objects.requireNonNull(tab.getText()).toString();
                achievementTabs.setTabTextColors(getResources().getColor(R.color.textBlack), getResources().getColor(R.color.whitelight));
                if (selected_tab.equals(getResources().getString(R.string.certificate))) {
                    tab.setIcon(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.certificate_icon), getResources().getColor(R.color.white)));
                    CertificatesFragment certificatesFragment = new CertificatesFragment();
                    loadFragment(certificatesFragment, "certificatesFragment", 0);
                } else if (selected_tab.equals(getResources().getString(R.string.badges))) {
                    tab.setIcon(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.badge_icon), getResources().getColor(R.color.white)));
                    BadgeFragment badgeFragment = new BadgeFragment();
                    loadFragment(badgeFragment, "badgeFragment", 1);
                }
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                selected_tab = Objects.requireNonNull(tab.getText()).toString();
                if (selected_tab.equals(getResources().getString(R.string.certificate))) {
                    tab.setIcon(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.certificate_icon),
                            getResources().getColor(R.color.primary_text)));
                } else if (selected_tab.equals(getResources().getString(R.string.badges))) {
                    tab.setIcon(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.badge_icon),
                            getResources().getColor(R.color.primary_text)));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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

    protected void initData() {
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(getApplicationContext());
        }
        OustSdkTools.setLocale(AchievementTabActivity.this);

//        OustGATools.getInstance().reportPageViewToGoogle(AchievementTabActivity.this, "achievements page");

        achievementTabs.setTabTextColors(getResources().getColor(R.color.primary_text), getResources().getColor(R.color.white));
        CertificatesFragment certificatesFragment = new CertificatesFragment();
        loadFragment(certificatesFragment, "certificatesFragment", 0);
    }


    protected void initListener() {
        try {
            backArrow.setOnClickListener(v -> onBackPressed());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        for (int i = 0; i < achievementTabs.getTabCount(); i++) {
            View tab = ((ViewGroup) achievementTabs.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(10, 0, 10, 0);
            tab.requestLayout();
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

            EditText searchEditText = search.findViewById(androidx.appcompat.R.id.search_src_text);
            searchEditText.setTextColor(getResources().getColor(R.color.primary_text));
            searchEditText.setHintTextColor(getResources().getColor(R.color.unselected_text));

            ImageView searchIcon = search.findViewById(androidx.appcompat.R.id.search_button);
            searchIcon.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.search_oust), color));

            search.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    toolBar_text.setVisibility(View.GONE);
                    itemFilter.setVisible(false);
                    itemSort.setVisible(false);
                }
            });

            search.setOnCloseListener(() -> {
                toolBar_text.setVisibility(View.VISIBLE);
                itemFilter.setVisible(false);
                itemSort.setVisible(false);
                return false;
            });

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (selected_tab.equals(getResources().getString(R.string.certificate))) {
                        CertificatesFragment firstFragment = (CertificatesFragment) getSupportFragmentManager().findFragmentByTag("certificatesFragment");
                        if (firstFragment != null) {
                            firstFragment.searchBarData(query);
                        }

                    } else if (selected_tab.equals(getResources().getString(R.string.badges))) {
                        BadgeFragment firstFragment = (BadgeFragment) getSupportFragmentManager().findFragmentByTag("badgeFragment");
                        if (firstFragment != null) {
                            firstFragment.searchBarData(query);
                        }
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    if (selected_tab.equals(getResources().getString(R.string.certificate))) {
                        CertificatesFragment firstFragment = (CertificatesFragment) getSupportFragmentManager().findFragmentByTag("certificatesFragment");
                        if (firstFragment != null) {
                            firstFragment.searchBarData(newText);
                        }


                    } else if (selected_tab.equals(getResources().getString(R.string.badges))) {
                        BadgeFragment firstFragment = (BadgeFragment) getSupportFragmentManager().findFragmentByTag("badgeFragment");
                        if (firstFragment != null) {
                            firstFragment.searchBarData(newText);
                        }
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

    @SuppressLint("ResourceType")
    public void loadFragment(Fragment fragment, String TAG, int position) {
        try {
            if (fragment != null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                if (position < 1) {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, 0, 0, R.anim.exit_to_right);
                } else {
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, 0, 0, R.anim.exit_to_left
                    );
                }
                fragmentTransaction.replace(R.id.tab_frame_layout_achievement, fragment, TAG);
                fragmentTransaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}