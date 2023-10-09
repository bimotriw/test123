package com.oustme.oustsdk.presenter.common;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.UserSettingActivity;
import com.oustme.oustsdk.firebase.common.UserBalanceState;
import com.oustme.oustsdk.request.UserSettingRequest;
import com.oustme.oustsdk.response.common.LanguageClass;
import com.oustme.oustsdk.response.common.LanguagesClasses;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.LanguagePreferences;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by shilpysamaddar on 22/03/17.
 */

public class UserSettingActivityPresenter {
    private UserSettingActivity view;
    private ActiveUser activeUser;
    private boolean hasTanentId = false;
    private List<LanguageClass> languageClasses;
    private List<String> languages;
    private String imageString;
    private String selectedGoal;

    private LanguageClass currentSelectedLanguage;

    public UserSettingActivityPresenter(UserSettingActivity view) {
        this.view = view;
        activeUser = OustAppState.getInstance().getActiveUser();
    }

    public void setStartingData() {
        try {
            List<String> goals = UserBalanceState.getInstance().getGoals();
            if (goals != null) {
                view.setGoalList(goals, activeUser.getGoal());
                view.hideGoalLayout();
            }
            if (OustStaticVariableHandling.getInstance().isEnterpriseUser()) {
                view.hideAcademicInfoFroEnterpriseUsers();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setLanguageSpinner(String langListStr, String laungeStr) {
        if ((langListStr != null) && (!langListStr.isEmpty())) {
            Gson gson = new Gson();
            LanguagesClasses classes = gson.fromJson(langListStr, LanguagesClasses.class);
            if ((classes != null) && (classes.getLanguageClasses() != null)) {
                languageClasses = classes.getLanguageClasses();
                languages = new ArrayList<>();
                for (int i = 0; i < languageClasses.size(); i++) {
                    languages.add(languageClasses.get(i).getName());
                }
                for (int i = 0; i < languageClasses.size(); i++) {
                    if (laungeStr.equalsIgnoreCase(languageClasses.get(i).getLanguagePerfix())) {
                        laungeStr = languageClasses.get(i).getName();
                    }
                }
                String languagePrefix = LanguagePreferences.get("appSelectedLanguage");
                if (languagePrefix == null || languagePrefix.isEmpty()) {
                    languagePrefix = Locale.getDefault().getLanguage();
                }
                Log.d("choose lang"," language prefix "+languagePrefix);
                view.setLanguageGoalSpinner(languages, languagePrefix, languageClasses);
            } else {
                classes = new LanguagesClasses();
                languageClasses = new ArrayList<>();
                languages = new ArrayList<>();
                LanguageClass languageClass = new LanguageClass();
                languageClass.setIndex(1);
                languageClass.setName("English");
                languageClass.setLanguagePerfix("en");
                languageClass.setFileName("englishStr.properties");
                languageClasses.add(languageClass);
                classes.setLanguageClasses(languageClasses);
                languages.add("English");

                languageClass = new LanguageClass();
                languageClass.setIndex(2);
                languageClass.setName("Kannada");
                languageClass.setLanguagePerfix("kn");
                languageClass.setFileName("kannadaStr.properties");
                languageClasses.add(languageClass);
                classes.setLanguageClasses(languageClasses);
                languages.add("Kannada");

                languageClass = new LanguageClass();
                languageClass.setIndex(3);
                languageClass.setName("Japanese");
                languageClass.setLanguagePerfix("ja");
                languageClass.setFileName("japaneseStr.properties");
                languageClasses.add(languageClass);
                classes.setLanguageClasses(languageClasses);
                languages.add("Japanese");

                languageClass = new LanguageClass();
                languageClass.setIndex(4);
                languageClass.setName("Thai");
                languageClass.setLanguagePerfix("th");
                languageClass.setFileName("thaiStr.properties");
                languageClasses.add(languageClass);
                classes.setLanguageClasses(languageClasses);
                languages.add("Thai");

                languageClass = new LanguageClass();
                languageClass.setIndex(5);
                languageClass.setName("Korean");
                languageClass.setLanguagePerfix("ko");
                languageClass.setFileName("koreanStr.properties");
                languageClasses.add(languageClass);
                classes.setLanguageClasses(languageClasses);
                languages.add("Korean");

                languageClass = new LanguageClass();
                languageClass.setIndex(6);
                languageClass.setName("Chinese(Traditional Taiwan)");
                languageClass.setLanguagePerfix("zh_TW");
                languageClass.setFileName("chineseStr.properties");
                languageClasses.add(languageClass);
                classes.setLanguageClasses(languageClasses);
                languages.add("Chinese(Traditional Taiwan");

                languageClass = new LanguageClass();
                languageClass.setIndex(7);
                languageClass.setName("Vietnamese");
                languageClass.setLanguagePerfix("vi");
                languageClass.setFileName("vietnameseStr.properties");
                languageClasses.add(languageClass);
                classes.setLanguageClasses(languageClasses);
                languages.add("Vietnamese");

                languageClass = new LanguageClass();
                languageClass.setIndex(8);
                languageClass.setName("Bahasa");
                languageClass.setLanguagePerfix("in");
                languageClass.setFileName("bahasaStr.properties");
                languageClasses.add(languageClass);
                classes.setLanguageClasses(languageClasses);
                languages.add("Bahasa");

                languageClass = new LanguageClass();
                languageClass.setIndex(9);
                languageClass.setName("Spanish");
                languageClass.setLanguagePerfix("es");//in_ID
                languageClass.setFileName("spanishStr.properties");
                languageClasses.add(languageClass);
                classes.setLanguageClasses(languageClasses);
                languages.add("Spanish");

                String languagePrefix = LanguagePreferences.get("appSelectedLanguage");
                if (languagePrefix == null || languagePrefix.isEmpty()) {
                    languagePrefix = Locale.getDefault().getLanguage();
                }
                Log.d("choose lang"," language prefix "+languagePrefix);
                view.setLanguageGoalSpinner(languages, languagePrefix, languageClasses);

            }
        } else {
            LanguagesClasses classes = new LanguagesClasses();
            languageClasses = new ArrayList<>();
            languages = new ArrayList<>();
            LanguageClass languageClass = new LanguageClass();
            languageClass.setIndex(1);
            languageClass.setName("English");
            languageClass.setLanguagePerfix("en");
            languageClass.setFileName("englishStr.properties");
            languageClasses.add(languageClass);
            classes.setLanguageClasses(languageClasses);
            languages.add("English");

            languageClass = new LanguageClass();
            languageClass.setIndex(2);
            languageClass.setName("Kannada");
            languageClass.setLanguagePerfix("kn");
            languageClass.setFileName("kannadaStr.properties");
            languageClasses.add(languageClass);
            classes.setLanguageClasses(languageClasses);
            languages.add("Kannada");

            languageClass = new LanguageClass();
            languageClass.setIndex(3);
            languageClass.setName("Japanese");
            languageClass.setLanguagePerfix("ja");
            languageClass.setFileName("japaneseStr.properties");
            languageClasses.add(languageClass);
            classes.setLanguageClasses(languageClasses);
            languages.add("Japanese");

            languageClass = new LanguageClass();
            languageClass.setIndex(4);
            languageClass.setName("Thai");
            languageClass.setLanguagePerfix("th");
            languageClass.setFileName("thaiStr.properties");
            languageClasses.add(languageClass);
            classes.setLanguageClasses(languageClasses);
            languages.add("Thai");

            languageClass = new LanguageClass();
            languageClass.setIndex(5);
            languageClass.setName("Korean");
            languageClass.setLanguagePerfix("ko");
            languageClass.setFileName("koreanStr.properties");
            languageClasses.add(languageClass);
            classes.setLanguageClasses(languageClasses);
            languages.add("Korean");

            languageClass = new LanguageClass();
            languageClass.setIndex(6);
            languageClass.setName("Chinese(Traditional Taiwan)");
            languageClass.setLanguagePerfix("zh_TW");
            languageClass.setFileName("chineseStr.properties");
            languageClasses.add(languageClass);
            classes.setLanguageClasses(languageClasses);
            languages.add("Chinese(Traditional Taiwan)");

            languageClass = new LanguageClass();
            languageClass.setIndex(7);
            languageClass.setName("Vietnamese");
            languageClass.setLanguagePerfix("vi");
            languageClass.setFileName("vietnameseStr.properties");
            languageClasses.add(languageClass);
            classes.setLanguageClasses(languageClasses);
            languages.add("Vietnamese");

            languageClass = new LanguageClass();
            languageClass.setIndex(8);
            languageClass.setName("Bahasa");
            languageClass.setLanguagePerfix("in");//in_ID
            languageClass.setFileName("bahasaStr.properties");
            languageClasses.add(languageClass);
            classes.setLanguageClasses(languageClasses);
            languages.add("Bahasa");

            languageClass = new LanguageClass();
            languageClass.setIndex(9);
            languageClass.setName("Spanish");
            languageClass.setLanguagePerfix("es");//in_ID
            languageClass.setFileName("spanishStr.properties");
            languageClasses.add(languageClass);
            classes.setLanguageClasses(languageClasses);
            languages.add("Spanish");

            String languagePrefix = LanguagePreferences.get("appSelectedLanguage");
            if (languagePrefix == null || languagePrefix.isEmpty()) {
                languagePrefix = Locale.getDefault().getLanguage();
            }
            Log.d("choose lang"," language prefix "+languagePrefix);
            view.setLanguageGoalSpinner(languages, languagePrefix, languageClasses);
        }
    }

    public void setResumeData() {
        activeUser = OustAppState.getInstance().getActiveUser();
        if ((activeUser.getUserDisplayName() != null) && (!activeUser.getUserDisplayName().isEmpty())) {
            view.setUserName(activeUser.getUserDisplayName());
        }
        if ((OustSdkTools.tempProfile != null)) {
            view.setSavedAvatar();
        } else {
            if ((activeUser.getAvatar() != null) && (!activeUser.getAvatar().isEmpty())) {
                view.setUserAvatar(activeUser.getAvatar());
            }
        }
        if (OustStaticVariableHandling.getInstance().isEnterpriseUser()) {
            view.setProgressVal(getProgressForEnterPrise());
        } else {
            view.setProgressVal(getProgress());
        }
        view.setuserExtraDetail();
    }

    private int getProgress() {
        int n1 = 0;
        if (activeUser.getFname() != null) {
            n1++;
        }
        if (activeUser.getGrade() != null) {
            n1++;
        }
        if (activeUser.getDob() > 1000) {
            n1++;
        }
        if (activeUser.getUserGender() != null) {
            n1++;
        }
        if (activeUser.getEmail() != null) {
            n1++;
        }
        if (activeUser.getUserCity() != null) {
            n1++;
        }
        if (activeUser.getUserCountry() != null) {
            n1++;
        }
        if (activeUser.getUserMobile() > 1000) {
            n1++;
        }
        if (activeUser.getSchoolName() != null) {
            n1++;
        }
        if (activeUser.getGoal() != null) {
            n1++;
        }
        return (n1 * 10);
    }

    private int getProgressForEnterPrise() {
        int n1 = 0;
        try {
            if (activeUser.getFname() != null) {
                n1 += 2;
            }
            if (activeUser.getDob() > 1000) {
                n1++;
            }
            if (activeUser.getUserGender() != null) {
                n1++;
            }
            if (activeUser.getEmail() != null) {
                n1 += 2;
            }
            if (activeUser.getUserCity() != null) {
                n1++;
            }
            if (activeUser.getUserCountry() != null) {
                n1++;
            }
            if (activeUser.getUserMobile() > 1000) {
                n1 += 2;
            }
        } catch (Exception e) {
        }
        return (n1 * 10);
    }

    public void setTanentIdStatus(String tenentId) {
        if ((tenentId != null) && (!tenentId.isEmpty())) {
            hasTanentId = true;
            view.setChangeAvatarIconVisible();
        }
    }

    public void setSelectedGoal(String selectedGoal1) {
        if ((selectedGoal1 != null) && (!selectedGoal1.isEmpty())) {
            this.selectedGoal = selectedGoal1;
        }
    }

    public void changePicOptionClick() {
        if (hasTanentId) {
            view.checkForStoragePermission();
        }
    }

    public void onBackBtnPressed() {
        if ((selectedGoal != null) && (!selectedGoal.isEmpty())) {
            if (!(selectedGoal.equalsIgnoreCase(activeUser.getGoal()))) {
                saveGoalUserInfo();
            }
        }
    }

    private void saveGoalUserInfo() {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            UserSettingRequest userSettingRequest = new UserSettingRequest();
            userSettingRequest.setGoal(selectedGoal);

            String settingUrl = OustSdkApplication.getContext().getResources().getString(R.string.usersetting_url);
            settingUrl = settingUrl.replace("{studentid}", activeUser.getStudentid());
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(userSettingRequest);

            try {
                settingUrl = HttpManager.getAbsoluteUrl(settingUrl);

                ApiCallUtils.doNetworkCall(Request.Method.PUT, settingUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        OustAppState.getInstance().getActiveUser().setGoal(selectedGoal);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        OustAppState.getInstance().getActiveUser().setGoal(selectedGoal);
                    }
                });


                /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, settingUrl, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        OustAppState.getInstance().getActiveUser().setGoal(selectedGoal);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        OustAppState.getInstance().getActiveUser().setGoal(selectedGoal);
                    }
                });
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

    public void setImageString(String imageString) {
        if ((imageString != null) && (!imageString.isEmpty())) {
            this.imageString = imageString;
        }
    }

    public void imageSaveBtnClick() {
        try {
            UserSettingRequest userSettingRequest = new UserSettingRequest();
            if ((imageString != null) && (imageString.length() > 10)) {
                userSettingRequest.setAvatarImgData(imageString);
                view.saveUserInfo(userSettingRequest);
            }
        } catch (Exception e) {
        }
    }

    public void saveProcessFinish(CommonResponse commonResponse) {
        if (commonResponse != null) {
            if (commonResponse.isSuccess()) {

                view.showToast(OustStrings.getString("profileUpdateSuccess"));
                if (activeUser != null) {
                    activeUser.setAvatar(OustAppState.getInstance().getActiveUser().getAvatar());
                    OustAppState.getInstance().setActiveUser(activeUser);
                }
                view.setJustSelectedAvtar();

            } else {
                if (commonResponse.getPopup() != null) {
                    view.showErrorPopup(commonResponse.getPopup());
                } else if ((commonResponse.getError() != null) && (!commonResponse.getError().isEmpty())) {
                    view.showToast(commonResponse.getError());
                } else {
                    view.showToast(OustStrings.getString("updateFailureMsg"));
                }
            }
        } else {
            view.showToast(OustStrings.getString("retry_internet_msg"));
        }
    }

    public void setLanguageStart(String str, String laungeStr) {
        currentSelectedLanguage = new LanguageClass();
        if ((languageClasses != null) && (languageClasses.size() > 0)) {
            for (int i = 0; i < languageClasses.size(); i++) {
                if (str.equalsIgnoreCase(languageClasses.get(i).getName())) {
                    currentSelectedLanguage = languageClasses.get(i);
                }
            }
            if (!(currentSelectedLanguage.getLanguagePerfix()).equalsIgnoreCase(laungeStr)) {
                view.checkLanguageAvailability(currentSelectedLanguage.getLanguagePerfix(), currentSelectedLanguage);
            }
        }
    }
}
