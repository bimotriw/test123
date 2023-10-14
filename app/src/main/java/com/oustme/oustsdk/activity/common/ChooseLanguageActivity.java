package com.oustme.oustsdk.activity.common;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.dialogs.LanguageBottomSheet;
import com.oustme.oustsdk.interfaces.common.OnLanguageSelected;
import com.oustme.oustsdk.layoutFour.LandingActivity;
import com.oustme.oustsdk.presenter.common.ChooseLanguagePresenter;
import com.oustme.oustsdk.response.common.LanguageClass;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.LanguagePreferences;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChooseLanguageActivity extends AppCompatActivity implements OnLanguageSelected {
    private RelativeLayout languageLayout;
    private ImageView countryFlag, icBack;
    private TextView selectedLang;
    private LanguageClass currentSelectedLanguage;
    private ChooseLanguagePresenter presenter;
    private ActiveUser activeUser;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetDialog processingDialog;
    private List<LanguageClass> langClasses;

    private boolean isFirstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);
        initPage();
        initUserSettings();
        handleAction();
    }

    private void initPage() {
        languageLayout = findViewById(R.id.languageWrapper);
        countryFlag = findViewById(R.id.ctryFlag);
        selectedLang = findViewById(R.id.selectedLang);
        icBack = findViewById(R.id.icBack);
    }

    private void handleAction() {
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
//                presenter.getLanguageData("", Locale.getDefault().getLanguage());
            } else {
                ChooseLanguageActivity.this.finish();
            }
            getAvailableLanguage();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showLanguageBottomSheet() {
        LanguageBottomSheet bottomSheet = new LanguageBottomSheet(false, false, this);

        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }

    private void showProcessingDialog(LanguageClass languageClass) {
        processingDialog = new BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme);
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

    private void getAvailableLanguage() {
        String getPointsUrl = OustSdkApplication.getContext().getResources().getString(R.string.get_all_languages);
        try {
            getPointsUrl = HttpManager.getAbsoluteUrl(getPointsUrl);
            ApiCallUtils.doNetworkCall(Request.Method.GET, getPointsUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject jsonObject = null;
                    List<LanguageClass> languageClasses = new ArrayList<>();
                    try {
                        jsonObject = new JSONObject(response.toString());
                        JSONArray languageDataArray = jsonObject.getJSONArray("languageDataList");
                        for (int i = 0; i < languageDataArray.length(); i++) {
                            JSONObject languageDataObject = languageDataArray.getJSONObject(i);

                            // Now you can extract individual fields from each languageDataObject
                            int languageId = languageDataObject.getInt("languageId");
                            String displayName = languageDataObject.isNull("displayName") ? null : languageDataObject.getString("displayName");
                            LanguageClass languageClass = new LanguageClass();
                            String languageCode = languageDataObject.isNull("languageCode") ? "" : languageDataObject.getString("languageCode");
                            languageClass.setIndex(languageId);
                            languageClass.setName(displayName);
                            if (!languageCode.isEmpty()) {
                                String[] langCode = languageCode.split("_");
                                if (langCode.length > 0) {
                                    languageClass.setLanguagePerfix(langCode[0]);
                                    if (langCode.length > 1) {
                                        languageClass.setCountryCode(langCode[1]);
                                    } else {
                                        languageClass.setCountryCode("GB");
                                    }
                                }
                            }
                            languageClasses.add(languageClass);
                        }

                    } catch (Exception e) {
                        Log.d("test lang", "error happened" + e);
                    }
                    Log.d("test lang", "REEEESSSPONNNNNNN = " + languageClasses.size() + " - " + response);
                    String lang = LanguagePreferences.get("appSelectedLanguage");
                    populateLanguage(lang, languageClasses);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ChooseLanguageActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void populateLanguage(String languagePrefix, List<LanguageClass> languageClasses) {
        try {
            this.langClasses = languageClasses;
            for (int i = 0; i < languageClasses.size(); i++) {
                if (languageClasses.get(i).getLanguagePerfix() != null && languageClasses.get(i).getLanguagePerfix().equals(languagePrefix)) {
                    selectedLang.setText(languageClasses.get(i).getName());
                    Drawable flag = getResources().getDrawable(R.drawable.english);
                    if (!languagePrefix.toLowerCase().equals("en")) {
                        flag = getCountryFlag(languageClasses.get(i).getCountryCode());
                    }
                    countryFlag.setImageDrawable(flag);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private Drawable getCountryFlag(String code) {
        Log.d("test lang", " flag " + code);
        Drawable flag = getResources().getDrawable(R.drawable.english);
        switch (code) {
            case "AE":
                flag = getResources().getDrawable(R.drawable.arab);
                break;
            case "IN":
                flag = getResources().getDrawable(R.drawable.india);
                break;
            case "MY":
                flag = getResources().getDrawable(R.drawable.malaysia);
                break;
            case "ID":
                flag = getResources().getDrawable(R.drawable.indonesia);
                break;
        }
        return flag;
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