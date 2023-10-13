package com.oustme.oustsdk.dialogs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.NewLandingActivity;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageBottomSheet extends BottomSheetDialogFragment implements OnLanguageSelected {

    private boolean isFullScreen = false;
    private boolean showButton = true;
    private RecyclerView rvLanguage;
    private Button buttonSetLang;
    private Activity activity;
    private LanguageAdapter adapter;
    private TextView tvSelectLang;
    private LinearLayoutCompat btnWrapper,divider;

    public LanguageBottomSheet(boolean isFullScreen, boolean showButton, Activity activity) {
        this.isFullScreen = isFullScreen;
        this.showButton = showButton;
        this.activity = activity;
    }

     @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_language, container, false);
        rvLanguage = view.findViewById(R.id.rvLanguage);
        buttonSetLang = view.findViewById(R.id.buttonSetLang);
        tvSelectLang = view.findViewById(R.id.select_lang);
        btnWrapper = view.findViewById(R.id.buttonWrapper);
        divider = view.findViewById(R.id.divider);
        initUI();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Your view initialization code here
        View bottomSheet = (View) view.getParent();
        bottomSheet.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
        bottomSheet.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        bottomSheet.setBackgroundColor(Color.TRANSPARENT);
        getAvailableLanguage();
        handleButtonClick();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.getBehavior().setDraggable(!showButton);
        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    private void initUI(){
        btnWrapper.setVisibility(showButton?View.VISIBLE:View.GONE);
        divider.setVisibility(showButton?View.VISIBLE:View.GONE);
        tvSelectLang.setAllCaps(showButton);
        tvSelectLang.setTextColor(showButton?getResources().getColor(R.color.linkcolor):getResources().getColor(R.color.black));
        if(!showButton) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) rvLanguage.getLayoutParams();
            params.setMargins(16, 100, 16, 0); //substitute parameters for left, top, right, bottom
            rvLanguage.setLayoutParams(params);
        }
        rvLanguage.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLanguage.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));

    }

    private void showProcessingDialog(LanguageClass languageClass){
        BottomSheetDialog processingDialog = new BottomSheetDialog(requireContext(),R.style.AppBottomSheetDialogTheme);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_processing_lang, null);
        ProgressBar progressBar = bottomSheetView.findViewById(R.id.progressBar);
        TextView tvCounter = bottomSheetView.findViewById(R.id.tvCounter);


        // Create a ValueAnimator that animates from 0 to 100 (the max value of the ProgressBar)
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(5000); // Duration of 5 seconds
        animator.addUpdateListener(animation -> {
            int progressValue = (int) animation.getAnimatedValue();
            progressBar.setProgress(progressValue);

            // Calculate countdown value for tvCounter
            int countdownValue = 5 - (progressValue / 20);
            tvCounter.setText(countdownValue + "s");

            if (progressValue == 100) {
                processingDialog.dismiss();
                int selectedId = languageClass.getIndex();
                String selectedPrefix = languageClass.getLanguagePerfix();
                setUserLanguage(selectedId, selectedPrefix);
            }
        });

        // When the animation ends, start the new activity
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                String languagePrefix = LanguagePreferences.get("appSelectedLanguage");
                if (languagePrefix == null || languagePrefix.isEmpty()) {
                    languagePrefix = Locale.getDefault().getLanguage();
                }
            }
        });

        animator.start();
        processingDialog.setContentView(bottomSheetView);
        processingDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            if (isFullScreen) {
                // Set the bottom sheet to full screen
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }else{
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenHeight = displayMetrics.heightPixels;
                // Set the minimum height (peek height)
                bottomSheetBehavior.setPeekHeight((int) (screenHeight*0.30));
                // Set the maximum height
                bottomSheet.getLayoutParams().height =  (int) (screenHeight*0.70); // e.g., 800 pixels
                bottomSheet.requestLayout();
            }
        }
    }

    private void handleButtonClick(){
        buttonSetLang.setOnClickListener(v -> {
            //check adapter selected position
            int lastSelectedPosition = adapter.getLastSelectedPosition();
            if(lastSelectedPosition > -1) {
                LanguageClass languageClass = adapter.getItems().get(adapter.getLastSelectedPosition());
                int selectedId = languageClass.getIndex();
                String selectedPrefix = languageClass.getLanguagePerfix();
                if (selectedId > 0) {
                    dismiss();
                    setUserLanguage(selectedId, selectedPrefix);
                }
            }
        });
    }

    private void setLocale(String selectedLanguage) {
        LanguagePreferences.save("appSelectedLanguage", selectedLanguage);
        if(!showButton){
            LanguagePreferences.save("appLanguageUpdateSuccess","true");
        }
        Intent refreshApp = new Intent(activity, NewLandingActivity.class);
        if (OustPreferences.getAppInstallVariable("isLayout4")) {
            refreshApp = new Intent(activity, LandingActivity.class);
        }
        refreshApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(refreshApp);
    }

    private void getAvailableLanguage(){
        try {
        String getPointsUrl = OustSdkApplication.getContext().getResources().getString(R.string.get_all_languages);
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
            dismiss();
            showProcessingDialog(languageClass);
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
                    Log.d("test lang","response set user language"+response.toString());
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
