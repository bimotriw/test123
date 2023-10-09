package com.oustme.oustsdk.activity.common.apptutorial;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.appTutorial.AppTutorialAdapter;
import com.oustme.oustsdk.model.orgSettings.AppTutorial;
import com.oustme.oustsdk.model.response.common.AppTutorialDataModel;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.ArrayList;

public class AppTutorialActivity extends AppCompatActivity implements AppTutorialAdapter.VideoInterFace {

    private ViewPager2 tutorial_view_pager;
    private LinearLayout dotsLayout;
    private ArrayList<AppTutorialDataModel> appTutorialDataModelArrayList;
    private TextView skipText;
    private CardView get_started;
    private int color;
    private int[] colorsActive;
    private int[] colorsInactive;
    private TextView[] dots;
    private AppTutorialAdapter appTutorialAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        OustSdkTools.setLocale(AppTutorialActivity.this);
        setContentView(R.layout.activity_app_tutorial);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        dotsLayout = findViewById(R.id.layoutDots);
        skipText = findViewById(R.id.app_tutorial_skip);
        get_started = findViewById(R.id.app_tutorial_get_started);
        tutorial_view_pager = findViewById(R.id.tutorial_view_pager);
    }

    private void initData() {
        try {
            if (getIntent() != null) {
                appTutorialDataModelArrayList = getIntent().getParcelableArrayListExtra("DATA");
            } else {
                finish();
            }
            color = OustResourceUtils.getColors();

            // adding bottom dots
            addBottomDotsView();

            if (appTutorialDataModelArrayList.size() == 1) {
                dotsAndNextArrowHandling(0, true);
            }

            // making notification bar transparent
            changeStatusBarColor();

            appTutorialAdapter = new AppTutorialAdapter(appTutorialDataModelArrayList, AppTutorialActivity.this);
            tutorial_view_pager.setAdapter(appTutorialAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initListener() {
        try {
            tutorial_view_pager.registerOnPageChangeCallback(addOnPageChangeListener);

            skipText.setOnClickListener(v -> launchHomeScreen());

            get_started.setOnClickListener(v -> launchHomeScreen());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("ResourceType")
    private void addBottomDotsView() {
        try {
            if (appTutorialDataModelArrayList != null) {
                /*if (appTutorialDataModelArrayList.size() == 1) {
                    dots = new TextView[0];
                } else {
                    dots = new TextView[appTutorialDataModelArrayList.size()];
                }*/
                dots = new TextView[appTutorialDataModelArrayList.size()];
            } else {
                dots = new TextView[0];
            }

            colorsActive = getResources().getIntArray(R.array.array_dot_active);
            colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

            for (int i = 0; i < dots.length; i++) {
                dots[i] = new TextView(this);
                dots[i].setText(Html.fromHtml("&#8226;"));
                dots[i].setTextSize(35);
                dots[i].setTextColor(colorsInactive[1]);
                dotsLayout.addView(dots[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void launchHomeScreen() {
        try {
            if (tutorial_view_pager != null) {
                tutorial_view_pager.removeAllViews();
            }

            if (appTutorialAdapter != null) {
                appTutorialAdapter.stopPlayer();
            }

            OustPreferences.saveAppInstallVariable(IS_APP_TUTORIAL_SHOWN, true);
            OustStaticVariableHandling.getInstance().setAppTutorialShown(true);
            OustPreferences.saveAppInstallVariable("After_App_Tutorial_open_CplIntroPage", true);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    ViewPager2.OnPageChangeCallback addOnPageChangeListener = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            dotsAndNextArrowHandling(position, position == appTutorialDataModelArrayList.size() - 1);
        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void moveToNextPosition(int position) {
        try {
            if (appTutorialDataModelArrayList.size() > 0) {
                if (position + 1 < appTutorialDataModelArrayList.size()) {
                    dotsAndNextArrowHandling(position + 1, position + 1 == appTutorialDataModelArrayList.size() - 1);
                    tutorial_view_pager.setCurrentItem(position + 1, true);
                }
            } else {
                launchHomeScreen();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void dotsAndNextArrowHandling(int position, boolean lastCard) {
        try {
            if (lastCard) {
                get_started.setVisibility(View.VISIBLE);
                skipText.setVisibility(View.GONE);
                if (color != 0) {
                    get_started.setCardBackgroundColor(color);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        get_started.setElevation(4);
                    }
                }
            } else {
                get_started.setVisibility(View.GONE);
                skipText.setVisibility(View.VISIBLE);
            }
            updateBottomDotsView(position);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateBottomDotsView(int currentPage) {
        try {
            if (dots.length > 0) {
                dots[currentPage].setTextColor(colorsActive[0]);
                if ((currentPage + 1) < dots.length) {
                    dots[currentPage + 1].setTextColor(colorsInactive[0]);
                }
                if ((currentPage - 1) >= 0) {
                    dots[currentPage - 1].setTextColor(colorsInactive[0]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            launchHomeScreen();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tutorial_view_pager != null) {
            tutorial_view_pager.unregisterOnPageChangeCallback(addOnPageChangeListener);
        }
    }
}