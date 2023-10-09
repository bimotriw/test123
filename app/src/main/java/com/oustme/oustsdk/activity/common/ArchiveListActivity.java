package com.oustme.oustsdk.activity.common;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.common.ArchiveTabFragmentPaderAdaptor;
import com.oustme.oustsdk.customviews.CustomViewPager;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;


/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class ArchiveListActivity extends AppCompatActivity {
    Toolbar toolbar;
    CustomViewPager newViewPager;
    TabLayout tabLayout;
    AppBarLayout archive_appbar;
    LinearLayout archive_mainlayout;
    ProgressBar archive_loader_progressbar;
    private ActiveUser activeUser;
    MenuItem oust;
    private ArchiveTabFragmentPaderAdaptor archiveTabFragmentPaderAdaptor;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try{
            OustSdkTools.setLocale(ArchiveListActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_archivelist);
        initViews();
        initLanding();
//        OustGATools.getInstance().reportPageViewToGoogle(ArchiveListActivity.this,"Archive Course/Assessment Page");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.historymenu, menu);
            oust=menu.findItem(R.id.oust);
            Drawable drawable=OustSdkTools.getImageDrawable(getResources().getString(R.string.whiteboy));
            oust.setIcon(drawable);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId= item.getItemId();
        if(itemId==android.R.id.home) {
            onBackPressed();
            return true;
        }
        else if(itemId==R.id.oust) {
            finish();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initViews() {
        toolbar= findViewById(R.id.tabanim_toolbar);
        newViewPager= findViewById(R.id.tabanim_viewpager);
        tabLayout= findViewById(R.id.tabanim_tabs);
        archive_appbar= findViewById(R.id.archive_appbar);
        archive_mainlayout= findViewById(R.id.archive_mainlayout);
        archive_loader_progressbar= findViewById(R.id.archive_loader_progressbar);
    }

    public void initLanding() {
        try {
            activeUser= OustAppState.getInstance().getActiveUser();
            if((activeUser!=null)&&(activeUser.getStudentid()!=null)){}else {
                HttpManager.setBaseUrl();
                OustFirebaseTools.initFirebase();
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            }
            setToolBarColor();
            showTab();
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            OustSdkTools.setSnackbarElements(archive_mainlayout, ArchiveListActivity.this);
            String toolbarColorCode=OustPreferences.get("toolbarColorCode");
            if((toolbarColorCode!=null)&&(!toolbarColorCode.isEmpty())){
                int color = Color.parseColor(toolbarColorCode);
                PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
                final LayerDrawable ld = (LayerDrawable) getApplicationContext().getResources().getDrawable(R.drawable.progressbar_test);
                final ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.customPlayerProgress);
                d1.setColorFilter(color,mode);
                archive_loader_progressbar.setIndeterminateDrawable(ld);
            }
            OustSdkTools.setProgressbar(archive_loader_progressbar);
        }catch (Exception e){}
    }

    private void setToolBarColor(){
        try{
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            TextView titleTextView= toolbar.findViewById(R.id.title);
            titleTextView.setText(getResources().getString(R.string.archived_list).toUpperCase());
            String toolbarColorCode=OustPreferences.get("toolbarColorCode");
            if((toolbarColorCode!=null)&&(!toolbarColorCode.isEmpty())){
                archive_appbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
        }catch (Exception e){
        }
    }


    public void showTab() {
        try {
            tabLayout.addTab(tabLayout.newTab().setCustomView(getTabIndicator(getResources().getString(R.string.courses_text),0)));
            tabLayout.addTab(tabLayout.newTab().setCustomView(getTabIndicator(getResources().getString(R.string.challenges_text),1)));

            archiveTabFragmentPaderAdaptor = new ArchiveTabFragmentPaderAdaptor(getSupportFragmentManager(), tabLayout.getTabCount());
            newViewPager.setAdapter(archiveTabFragmentPaderAdaptor);

            newViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    newViewPager.setCurrentItem(tab.getPosition(),true);
                    ((TextView)tabLayout.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tabText)).setTextColor(OustSdkTools.getColorBack(R.color.Gray));
                }
                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    ((TextView)tabLayout.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tabText)).setTextColor(OustSdkTools.getColorBack(R.color.DarkGray));
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}

            });
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private View getTabIndicator(final String title, int no) {
        View view = LayoutInflater.from(this).inflate(R.layout.newlanding_tab, null);
        TextView tv = view.findViewById(R.id.tabText);
        if(no==0){
            tv.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
        }else {
            tv.setTextColor(OustSdkTools.getColorBack(R.color.DarkGray));
        }
        tv.setText(title);
        return view;
    }
}
