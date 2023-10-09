package com.oustme.oustsdk.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.NewLandingActivity;
import com.oustme.oustsdk.activity.common.UserSettingActivity;
import com.oustme.oustsdk.adapter.common.LanguageAdapter;
import com.oustme.oustsdk.interfaces.common.OnLanguageSelected;
import com.oustme.oustsdk.layoutFour.LandingActivity;
import com.oustme.oustsdk.response.common.LanguageClass;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.LanguagePreferences;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LanguageBottomSheet extends BottomSheetDialogFragment implements OnLanguageSelected {

    private boolean isFullScreen = false;
    private boolean showButton = true;
    private RecyclerView rvLanguage;
    private Button buttonSetLang;

    private LanguageAdapter adapter;

    public LanguageBottomSheet(boolean isFullScreen, boolean showButton) {
        this.isFullScreen = isFullScreen;
        this.showButton = showButton;
    }

     @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_language, container, false);
        rvLanguage = view.findViewById(R.id.rvLanguage);
        buttonSetLang = view.findViewById(R.id.buttonSetLang);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Your view initialization code here
        getAvailableLanguage();
        handleButtonClick();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (isFullScreen) {
                // Set the bottom sheet to full screen
                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            buttonSetLang.setVisibility(showButton?View.VISIBLE:View.INVISIBLE);
        }
    }

    private void handleButtonClick(){
        buttonSetLang.setOnClickListener(v -> {
            LanguageClass languageClass = adapter.getItems().get(adapter.getLastSelectedPosition());
            int selectedId=languageClass.getIndex();
            String selectedPrefix = languageClass.getLanguagePerfix();
            if(selectedId > 0){
                setUserLanguage(selectedId,selectedPrefix);
            }
        });
    }

    private void setLocale(String selectedLanguage) {
        LanguagePreferences.save("appSelectedLanguage", selectedLanguage);
        Intent refreshApp = new Intent(getContext(), NewLandingActivity.class);
        if (OustPreferences.getAppInstallVariable("isLayout4")) {
            refreshApp = new Intent(getContext(), LandingActivity.class);
        }
        refreshApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(refreshApp);
        LanguageBottomSheet.this.dismiss();
    }

    private void getAvailableLanguage(){
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
                            if(!languageCode.isEmpty()){
                                String[] langCode = languageCode.split("_");
                                if (langCode.length > 0) {
                                    languageClass.setLanguagePerfix(langCode[0]);

                                    if (langCode.length > 1) {
                                        Log.d("test lang","set contry code to IN code "+langCode[1]+" "+displayName);
                                        languageClass.setCountryCode(langCode[1]);
                                    } else {
                                        Log.d("test lang","set contry code to IN code "+langCode[0]+" "+displayName);
                                        languageClass.setCountryCode("IN");
                                    }
                                }
                                languageClasses.add(languageClass);
                            }

                        }
                        adapter = new LanguageAdapter(languageClasses, LanguageBottomSheet.this);
                        rvLanguage.setLayoutManager(new LinearLayoutManager(getContext()));
                        rvLanguage.setAdapter(adapter);
                    } catch (Exception e) {
                        Log.d("test lang","error happened"+e);
                    }

                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onSelectLanguage(LanguageClass languageClass) {
        if(!showButton){
            setUserLanguage(languageClass.getIndex(),languageClass.getLanguagePerfix());
        }
    }

    private void setUserLanguage(int langId,String langPrefix){
        String getPointsUrl = OustSdkApplication.getContext().getResources().getString(R.string.set_student_language);
        ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
        getPointsUrl = getPointsUrl.replace("{studentId}", "arvind");
        try {
            getPointsUrl = HttpManager.getAbsoluteUrl(getPointsUrl);
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("languageId", langId);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, getPointsUrl, OustSdkTools.getRequestObjectforJSONObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("lang_api","response "+response.toString());
                    setLocale(langPrefix);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
