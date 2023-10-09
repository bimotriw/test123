package com.oustme.oustsdk.activity.common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blongho.country_data.World;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.common.LanguageAdapter;
import com.oustme.oustsdk.dialogs.LanguageBottomSheet;
import com.oustme.oustsdk.interfaces.common.OnLanguageSelected;
import com.oustme.oustsdk.layoutFour.LandingActivity;
import com.oustme.oustsdk.presenter.common.ChooseLanguagePresenter;
import com.oustme.oustsdk.response.common.LanguageClass;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.LanguagePreferences;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.List;
import java.util.Locale;

public class ChooseLanguageActivity extends AppCompatActivity implements OnLanguageSelected {
    private RelativeLayout languageLayout;
    private ImageView countryFlag,icBack;
    private TextView selectedLang;
    private LanguageClass currentSelectedLanguage;
    private ChooseLanguagePresenter presenter;
    private ActiveUser activeUser;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetDialog processingDialog;
    private List<LanguageClass> languageClasses;

    private boolean isFirstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);
        initPage();
        initUserSettings();
        handleAction();
    }

    private void initPage(){
        languageLayout = findViewById(R.id.languageWrapper);
        countryFlag = findViewById(R.id.ctryFlag);
        selectedLang = findViewById(R.id.selectedLang);
        icBack = findViewById(R.id.icBack);
    }

    private void handleAction(){
        icBack.setOnClickListener(v -> {
            finish();
        });

        languageLayout.setOnClickListener(v -> {
            showLanguageBottomSheet();
        });
    }

    public void initUserSettings() {
        try {
            activeUser = OustAppState.getInstance().getActiveUser();
            if (activeUser != null) {
                presenter = new ChooseLanguagePresenter(this);
                presenter.getLanguageData("", Locale.getDefault().getLanguage());
            } else {
                ChooseLanguageActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showLanguageBottomSheet() {
        LanguageBottomSheet bottomSheet = new LanguageBottomSheet(false, false);
        bottomSheet.setCancelable(false);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }

//    private void showLanguageBottomSheet() {
//        bottomSheetDialog = new BottomSheetDialog(this,R.style.AppBottomSheetDialogTheme);
//        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_language, null);
//        RecyclerView rvLanguages = bottomSheetView.findViewById(R.id.rvLanguage);
//
//        // Set up the RecyclerView with the LanguageAdapter
//        List<LanguageClass> languageList = languageClasses;
//        // Populate the languageList with data
//        LanguageAdapter adapter = new LanguageAdapter(languageList,ChooseLanguageActivity.this);
//        rvLanguages.setLayoutManager(new LinearLayoutManager(this));
//        rvLanguages.setAdapter(adapter);
//
//        bottomSheetDialog.setContentView(bottomSheetView);
//        bottomSheetDialog.show();
//    }

    private void showProcessingDialog(LanguageClass languageClass){
        processingDialog = new BottomSheetDialog(this,R.style.AppBottomSheetDialogTheme);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_processing_lang, null);
        ProgressBar progressBar = bottomSheetView.findViewById(R.id.progressBar);

        // Create a ValueAnimator that animates from 0 to 100 (the max value of the ProgressBar)
        ValueAnimator animator = ValueAnimator.ofInt(0, progressBar.getMax());
        animator.setDuration(5000); // Duration of 5 seconds
        animator.addUpdateListener(animation -> {
            int progressValue = (int) animation.getAnimatedValue();
            progressBar.setProgress(progressValue);
        });

        // When the animation ends, start the new activity
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                String languagePrefix = LanguagePreferences.get("appSelectedLanguage");
                if (languagePrefix == null || languagePrefix.isEmpty()) {
                    languagePrefix = Locale.getDefault().getLanguage();
                }
                presenter.setLanguageStart(languageClass.getName(), languagePrefix);
            }
        });

        animator.start();
        processingDialog.setContentView(bottomSheetView);
        processingDialog.show();
    }

    private void setLocale(String selectedLanguage) {
        LanguagePreferences.save("appSelectedLanguage", selectedLanguage);
        Intent refreshApp = new Intent(ChooseLanguageActivity.this, NewLandingActivity.class);
        if (OustPreferences.getAppInstallVariable("isLayout4")) {
            refreshApp = new Intent(ChooseLanguageActivity.this, LandingActivity.class);
        }
        refreshApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(refreshApp);
        ChooseLanguageActivity.this.finish();

    }

    public void populateLanguage(List<String> languages, String languagePrefix, List<LanguageClass> languageClasses) {
        try {
            this.languageClasses = languageClasses;
            for(int i=0;i<languageClasses.size();i++){
                if(languageClasses.get(i).getLanguagePerfix().equals(languagePrefix)){
                    final int flag= World.getFlagOf(languageClasses.get(i).getCountryCode());
                    countryFlag.setImageResource(flag);
                    selectedLang.setText(languageClasses.get(i).getName());
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkLanguageAvailability(String selectedLanguage, LanguageClass currentSelectedLanguage) {
        try {
            this.currentSelectedLanguage = currentSelectedLanguage;
            /*EntityResourseStrings resourseStringsModel = RoomHelper.getResourceStringModel(currentSelectedLanguage.getLanguagePerfix());
            if ((resourseStringsModel != null) && (resourseStringsModel.getHashmapStr() != null) && (!resourseStringsModel.getHashmapStr().isEmpty())) {
                setLacalLanguage(selectedLanguage);
            } else {
                showDownloadPopup();
            }*/

            setLocale(selectedLanguage);
        } catch (Exception e) {
            //  showDownloadPopup();
        }
    }

    @Override
    public void onSelectLanguage(LanguageClass languageClass) {
        bottomSheetDialog.dismiss();
        showProcessingDialog(languageClass);
    }
}