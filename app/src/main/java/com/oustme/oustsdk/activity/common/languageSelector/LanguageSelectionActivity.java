package com.oustme.oustsdk.activity.common.languageSelector;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.ENGLISH;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_LANG_SELECTED;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.SELECTED_CPL_ID;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.SELECTED_LANGUAGE;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.SELECTED_LANG_ID;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.languageSelector.model.response.Language;
import com.oustme.oustsdk.activity.common.languageSelector.model.response.LanguageListResponse;
import com.oustme.oustsdk.activity.common.languageSelector.presenter.LanguagePresenter;
import com.oustme.oustsdk.activity.common.languageSelector.view.LanguageView;
import com.oustme.oustsdk.adapter.common.CustomLanguageListAdapter;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.response.CheckModuleDistributedOrNot;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.BranchTools;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LanguageSelectionActivity extends AppCompatActivity implements LanguageView.LView {

    private static final String TAG = "LanguageSelection";
    CustomLanguageListAdapter mCustomLanguageListAdapter;
    private ShimmerFrameLayout mShimmerFrameLayout;
    private LinearLayout mLinearLayoutNext;
    private LinearLayout mProgressBarHolder;
    private List<Language> mLanguageList;
    private long mCplIDForLanguage;
    private int mSelectedLanguageId;
    private int mSelectedCplId;
    private String mSelectedLanguage;
    private LanguagePresenter mPresenter;
    private boolean allowBackPress;
    private boolean isChildCplDistributed = false;
    private boolean isLanguageSwitch = false;
    private boolean forLanguageSwitch;
    private long childLanguageID;
    RecyclerView listViewLanguage_recyclerView;
    int sortPosition;
    int colors, bgColor;
    private Toolbar toolbar;
    private TextView screen_name;
    private TextView progress_bar_text;
    private ImageView back_button;
    private boolean isMultipleCpl = false;
    private boolean isComingFromNotification = false;
    private boolean isComingFromCplBaseActivity = false;
    ActiveUser activeUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_language_selection2);
        activeUser = OustAppState.getInstance().getActiveUser();
        getColors();

        mPresenter = new LanguagePresenter(this);
        listViewLanguage_recyclerView = findViewById(R.id.listViewLanguage_recyclerView);
        mLinearLayoutNext = findViewById(R.id.linearLayoutNext);
        mShimmerFrameLayout = findViewById(R.id.shimmerContainer);
        mProgressBarHolder = findViewById(R.id.ProgressBarHolder);
        toolbar = findViewById(R.id.toolbar_lay);
        screen_name = findViewById(R.id.screen_name);
        back_button = findViewById(R.id.back_button);
        progress_bar_text = findViewById(R.id.progress_bar_text);

        toolbar.setBackgroundColor(bgColor);
        screen_name.setTextColor(colors);
        OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), colors);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        isMultipleCpl = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_MULTIPLE_CPL);
        isLanguageSwitch = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_MULTILINGUAL_LANGUAGE_SWITCH);

        mLanguageList = new ArrayList<>();
        Intent intent = getIntent();
        mCplIDForLanguage = intent.getLongExtra("CPL_ID", 0);
        forLanguageSwitch = intent.getBooleanExtra("forLanguageSwitch", false);
        childLanguageID = intent.getLongExtra("childLanguageID", 0);
        isComingFromNotification = intent.getBooleanExtra("IsComingFromNotification", false);
        isComingFromCplBaseActivity = intent.getBooleanExtra("isComingFromCplBaseActivity", false);
        allowBackPress = intent.getBooleanExtra("allowBackPress", false);
        isChildCplDistributed = intent.getBooleanExtra("isChildCplDistributed", true);

        if (intent.hasExtra("languageList") && intent.getStringExtra("languageList") != null) {
            final Gson mGson = new Gson();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(Objects.requireNonNull(intent.getStringExtra("languageList")));
            } catch (JSONException e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            hideProgressBar(1);
            extractCplLanguageData(jsonObject, mGson);
        } else {
            if (OustSdkTools.checkInternetStatus()) {
                mPresenter.getLanguages(mCplIDForLanguage);
            } else {
                OustSdkTools.showSnackBarBasedonStatus(false);
            }
        }

        setTitleBar();

        try {
            disable(mLinearLayoutNext);
            OustStaticVariableHandling.getInstance().setSortPosition(-1);
            if (OustStaticVariableHandling.getInstance().getSortPosition() != -1) {
                enable(mLinearLayoutNext);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (isComingFromCplBaseActivity || allowBackPress) {
            back_button.setVisibility(View.VISIBLE);
        } else {
            back_button.setVisibility(View.GONE);
        }

        back_button.setOnClickListener(v -> onBackPressed());

        mLinearLayoutNext.setOnClickListener(view -> {
            if (forLanguageSwitch) {
                if (mSelectedCplId != 0) {
                    checkCplIsAlreadyDistributedOrNot(String.valueOf(mSelectedCplId), false);
                } else {
                    OustSdkTools.showToast(getResources().getString(R.string.please_select_the_language));
                }
            } else {
                if (!isLanguageSwitch) {
                    languageConfirmation();
                } else {
                    if (mSelectedCplId != 0) {
                        if (!isChildCplDistributed) {
                            mPresenter.getCPLData(String.valueOf(mCplIDForLanguage), mSelectedLanguage, mSelectedLanguageId, String.valueOf(mSelectedCplId));
                        } else {
                            mPresenter.distributeCplData(String.valueOf(mSelectedCplId), mSelectedLanguage, mSelectedLanguageId);
                        }
                    } else {
                        OustSdkTools.showToast(getResources().getString(R.string.please_select_the_language));
                    }
                }
            }
        });

        back_button.setOnClickListener(view -> onBackPressed());

        OustStaticVariableHandling.getInstance().setCplLanguageScreenOpen(false);
    }

    private void getColors() {
        try {
            if (OustPreferences.getAppInstallVariable("isLayout4")) {
                colors = OustResourceUtils.getColors();
                bgColor = OustResourceUtils.getToolBarBgColor();
            } else {
                bgColor = OustResourceUtils.getColors();
                colors = OustResourceUtils.getToolBarBgColor();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setTitleBar() {
        try {
            toolbar.setBackgroundColor(bgColor);
            screen_name.setTextColor(colors);
            OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), colors);
            screen_name.setText(getResources().getString(R.string.multilingual_popup_title) + "  for Playlist");
            screen_name.setTextColor(colors);
            OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), colors);
            setSupportActionBar(toolbar);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void languageConfirmation() {
        try {
            Dialog languageDialog = new Dialog(LanguageSelectionActivity.this, R.style.DialogTheme);
            languageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            languageDialog.setContentView(R.layout.common_pop_up);
            languageDialog.setCancelable(false);
            Objects.requireNonNull(languageDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            languageDialog.show();

            TextView info_title = languageDialog.findViewById(R.id.info_title);

            String title = getResources().getString(R.string.new_language) + ": " + mSelectedLanguage;
            String[] spilt = title.split(":");
            Spannable title_span = new SpannableString(title);
            title_span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.unselected_text)), 0, spilt[0].length() + 1, 0);

            TextView info_description = languageDialog.findViewById(R.id.info_description);
            LinearLayout info_no = languageDialog.findViewById(R.id.info_no);
            ImageView info_image = languageDialog.findViewById(R.id.info_image);
            TextView info_no_text = languageDialog.findViewById(R.id.info_no_text);
            LinearLayout info_yes = languageDialog.findViewById(R.id.info_yes);
            TextView info_yes_text = languageDialog.findViewById(R.id.info_yes_text);
            info_yes.setBackground(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.button_rounded_ten_bg)));

            info_title.setText(title_span);
            //info_title.setTextSize(15);
            info_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning));

            info_description.setText(getResources().getString(R.string.language_confrim));
            info_description.setVisibility(View.VISIBLE);

            info_no_text.setText(getResources().getString(R.string.no));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                info_no.setBackgroundTintList(null);
                info_yes.setBackgroundTintList(null);
                info_yes.setBackgroundTintList(getResources().getColorStateList(R.color.lgreen));
            }

            info_yes_text.setText(getResources().getString(R.string.yes));

            info_no.setOnClickListener(v -> {
                if (languageDialog.isShowing()) {
                    languageDialog.dismiss();
                }
            });

            info_yes.setOnClickListener(v -> {
                if (languageDialog.isShowing()) {
                    languageDialog.dismiss();
                }
                if (mSelectedCplId != 0) {
                    if (!isChildCplDistributed) {
                        mPresenter.getCPLData(String.valueOf(mCplIDForLanguage), mSelectedLanguage, mSelectedLanguageId, String.valueOf(mSelectedCplId));
                    } else {
                        mPresenter.distributeCplData(String.valueOf(mSelectedCplId), mSelectedLanguage, mSelectedLanguageId);
                    }
                } else {
                    OustSdkTools.showToast(getResources().getString(R.string.please_select_the_language));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void languageChangeConfirmation() {
        try {
            Dialog languageDialog = new Dialog(LanguageSelectionActivity.this, R.style.DialogTheme);
            languageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            languageDialog.setContentView(R.layout.common_pop_up);
            languageDialog.setCancelable(false);
            Objects.requireNonNull(languageDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            languageDialog.show();

            TextView info_title = languageDialog.findViewById(R.id.info_title);

            String title = getResources().getString(R.string.new_language) + ": " + mSelectedLanguage;
            String[] spilt = title.split(":");
            Spannable title_span = new SpannableString(title);
            title_span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.unselected_text)), 0, spilt[0].length() + 1, 0);

            TextView info_description = languageDialog.findViewById(R.id.info_description);
            TextView info_sub_description = languageDialog.findViewById(R.id.info_sub_description);
            LinearLayout info_no = languageDialog.findViewById(R.id.info_no);
            TextView info_no_text = languageDialog.findViewById(R.id.info_no_text);
            LinearLayout info_yes = languageDialog.findViewById(R.id.info_yes);
            TextView info_yes_text = languageDialog.findViewById(R.id.info_yes_text);
            ImageView image_view = languageDialog.findViewById(R.id.info_image);

            info_title.setText(title_span);

            info_description.setText(getResources().getString(R.string.language_switch_desc));
            info_description.setVisibility(View.VISIBLE);
            info_sub_description.setText(getResources().getString(R.string.language_sub_text));
            info_sub_description.setVisibility(View.VISIBLE);
            info_no_text.setText(getResources().getString(R.string.no));
            image_view.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning_info));

            Drawable actionDrawable = getResources().getDrawable(R.drawable.button_rounded_ten_bg);
            info_yes.setBackground(OustSdkTools.drawableColor(actionDrawable));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                info_no.setBackgroundTintList(null);
            }

            info_yes_text.setText(getResources().getString(R.string.yes));
            info_no_text.setText(getResources().getString(R.string.no));

            info_no.setOnClickListener(v -> {
                if (languageDialog.isShowing()) {
                    languageDialog.dismiss();
                }
            });

            info_yes.setOnClickListener(v -> {
                if (languageDialog.isShowing()) {
                    languageDialog.dismiss();
                }
                if (!isChildCplDistributed) {
                    mPresenter.getCPLData(String.valueOf(mCplIDForLanguage), mSelectedLanguage, mSelectedLanguageId, String.valueOf(mSelectedCplId));
                } else {
                    mPresenter.distributeCplData(String.valueOf(mSelectedCplId), mSelectedLanguage, mSelectedLanguageId);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setItemSelected() {
        try {
            enable(mLinearLayoutNext);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onBackPressed() {
        if (allowBackPress) {
            OustStaticVariableHandling.getInstance().setCplLanguageScreenOpen(false);
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    @Override
    public void updateLanguage(LanguageListResponse languageListResponse, String mSelectedLanguage, int mSelectedLanguageId, String newChildCplLanguageId) {
        try {
            if (languageListResponse.isSuccess()) {
                OustPreferences.saveAppInstallVariable(IS_LANG_SELECTED, true);
                OustStaticVariableHandling.getInstance().setCplLanguageScreenOpen(false);
                if (!allowBackPress) {
                    OustStaticVariableHandling.getInstance().setNewCplDistributed(true);
                } else {
                    OustStaticVariableHandling.getInstance().setNewCplDistributed(false);
                    OustStaticVariableHandling.getInstance().setRefreshReq(true);
                }
                setResult(RESULT_OK);

                Log.d(TAG, "updateLanguage: isComingFromNotification--> " + isComingFromNotification + " isComingFromCplBaseActivity--> " + isComingFromCplBaseActivity +
                        " newChildCplLanguageId--> " + newChildCplLanguageId);
                if (isComingFromNotification) {
                    OustPreferences.saveintVar(SELECTED_LANG_ID, mSelectedLanguageId);
                    OustPreferences.save(SELECTED_LANGUAGE, mSelectedLanguage);
                    OustPreferences.save(SELECTED_CPL_ID, newChildCplLanguageId);
                    BranchTools.checkModuleDistributionOrNot(activeUser, String.valueOf(this.mCplIDForLanguage), "CPL");
                    hideProgressBar(2);
                } else {
                    redirectToParticularCPl(newChildCplLanguageId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            if (isComingFromNotification) {
                OustPreferences.saveintVar(SELECTED_LANG_ID, mSelectedLanguageId);
                OustPreferences.save(SELECTED_LANGUAGE, mSelectedLanguage);
                OustPreferences.save(SELECTED_CPL_ID, newChildCplLanguageId);
                BranchTools.checkModuleDistributionOrNot(activeUser, newChildCplLanguageId, "CPL");
                hideProgressBar(2);
            } else {
                redirectToParticularCPl(newChildCplLanguageId);
            }
        }
    }

    private void redirectToParticularCPl(String newChildCplLanguageId) {
        try {
            if (isComingFromCplBaseActivity) {
                checkCplIsAlreadyDistributedOrNot(String.valueOf(newChildCplLanguageId), isComingFromCplBaseActivity);
            } else {
                OustPreferences.saveintVar(SELECTED_LANG_ID, this.mSelectedLanguageId);
                OustPreferences.save(SELECTED_LANGUAGE, mSelectedLanguage);
                OustPreferences.save(SELECTED_CPL_ID, newChildCplLanguageId);
                OustStaticVariableHandling.getInstance().setCplId(newChildCplLanguageId);
                OustStaticVariableHandling.getInstance().setNewCplDistributed(false);
                BranchTools.checkModuleDistributionOrNot(activeUser, newChildCplLanguageId, "CPL");
                hideProgressBar(2);
            }
        } catch (Exception e) {
            hideProgressBar(2);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addToFireBaseRefList(String message, ValueEventListener eventListener) {
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(eventListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, message));
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    @Override
    public void onErrorCPLData() {
        OustSdkTools.showToast(getString(R.string.unable_fetch_connection_error));
    }

    @Override
    public void showProgressBar(int type) {
        if (type == 1) {
            mShimmerFrameLayout.setVisibility(View.VISIBLE);
        } else if (type == 2) {
            mProgressBarHolder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgressBar(int type) {
        if (type == 1) {
            mShimmerFrameLayout.setVisibility(View.GONE);
        } else if (type == 2) {
            mProgressBarHolder.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLanguageSelected() {

    }

    @Override
    public void onError(String error) {
        OustSdkTools.showToast(getResources().getString(R.string.restart_msg));
    }

    @Override
    public void getLanguages() {

    }

    @Override
    public void extractCplLanguageData(JSONObject response, Gson mGson) {
        try {
            LanguageListResponse languageListResponse = mGson.fromJson(response.toString(), LanguageListResponse.class);
            mLanguageList.addAll(languageListResponse.getLanguageList());
            if (mLanguageList != null && mLanguageList.size() > 0) {
                Collections.sort(mLanguageList, (o1, o2) -> {
                    int c;
                    c = Boolean.compare(o2.isDefaultSelected(), o1.isDefaultSelected());
                    if (c == 0) {
                        c = o1.getName().compareTo(o2.getName());
                    }
                    return c;
                });
                listViewLanguage_recyclerView = findViewById(R.id.listViewLanguage_recyclerView);

                mCustomLanguageListAdapter = new CustomLanguageListAdapter(mLanguageList, LanguageSelectionActivity.this, position -> {
                    if (OustStaticVariableHandling.getInstance().getSortPosition() != -1) {
                        sortPosition = position;
                        enable(mLinearLayoutNext);
                        mSelectedLanguage = mLanguageList.get(sortPosition).getName();
                        mSelectedLanguageId = mLanguageList.get(sortPosition).getLanguageId();
                        mSelectedCplId = mLanguageList.get(sortPosition).getChildCPLId();
                        Log.d(TAG, "extractCplLanguageData: Position--> " + position + " mSelectedCplId --> " + mLanguageList.get(sortPosition).getChildCPLId() +
                                " mSelectedLanguageId--> " + mLanguageList.get(sortPosition).getLanguageId() + " mSelectedLanguage--> " + mLanguageList.get(sortPosition).getName());
                    }
                });

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(LanguageSelectionActivity.this, LinearLayoutManager.VERTICAL, false);
                listViewLanguage_recyclerView.setLayoutManager(mLayoutManager);
                listViewLanguage_recyclerView.setItemAnimator(new DefaultItemAnimator());
                listViewLanguage_recyclerView.setAdapter(mCustomLanguageListAdapter);
            } else {
                if (mLanguageList != null) {
                    OustPreferences.save(SELECTED_LANGUAGE, ENGLISH);
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void enable(LinearLayout linearLayout) {
        linearLayout.setBackground(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.button_rounded_ten_bg)));
        linearLayout.setClickable(true);
    }

    private void disable(LinearLayout linearLayout) {
        linearLayout.setBackground(getResources().getDrawable(R.drawable.button_rounded_ten_bg));
        linearLayout.setClickable(false);
    }

    private void checkCplIsAlreadyDistributedOrNot(String newChildCplLanguageId, boolean isComingFromCplBaseActivity) {
        try {
            ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            String tenantName = OustPreferences.get("tanentid").trim();
            String checkModuleDistributedUrl = OustSdkApplication.getContext().getResources().getString(R.string.check_module_distributedOrNot);
            checkModuleDistributedUrl = checkModuleDistributedUrl.replace("{orgId}", tenantName);
            checkModuleDistributedUrl = checkModuleDistributedUrl.replace("{userId}", activeUser.getStudentid());
            checkModuleDistributedUrl = checkModuleDistributedUrl.replace("{moduleType}", "CPL");
            checkModuleDistributedUrl = checkModuleDistributedUrl.replace("{moduleId}", newChildCplLanguageId);

            checkModuleDistributedUrl = HttpManager.getAbsoluteUrl(checkModuleDistributedUrl);
            Gson mGson = new Gson();
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);
            Log.e(TAG, "checkModuleDistributionOrNot: --> " + checkModuleDistributedUrl);
            ApiCallUtils.doNetworkCall(Request.Method.GET, checkModuleDistributedUrl, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "onResponse: checkModuleDistributionOrNot--> " + response);
                    if (response.optBoolean("success")) {
                        if (isComingFromCplBaseActivity) {
                            if (forLanguageSwitch) {
                                OustStaticVariableHandling.getInstance().setLanguage_switch_done(true);
                            }
                            OustPreferences.saveintVar(SELECTED_LANG_ID, mSelectedLanguageId);
                            OustPreferences.save(SELECTED_LANGUAGE, mSelectedLanguage);
                            OustPreferences.save(SELECTED_CPL_ID, newChildCplLanguageId);
                            OustStaticVariableHandling.getInstance().setCplId(newChildCplLanguageId);
                            OustStaticVariableHandling.getInstance().setNewCplDistributed(false);
                            finish();
                        } else {
                            Gson gson = new Gson();
                            CheckModuleDistributedOrNot checkModuleDistributedOrNot = gson.fromJson(response.toString(), CheckModuleDistributedOrNot.class);
                            if (checkModuleDistributedOrNot != null) {
                                if (!checkModuleDistributedOrNot.getDistributed()) {
                                    languageChangeConfirmation();
                                } else {
                                    if (!isChildCplDistributed) {
                                        mPresenter.getCPLData(String.valueOf(mCplIDForLanguage), mSelectedLanguage, mSelectedLanguageId, String.valueOf(mSelectedCplId));
                                    } else {
                                        mPresenter.distributeCplData(String.valueOf(mSelectedCplId), mSelectedLanguage, mSelectedLanguageId);
                                    }
                                }
                            } else {
                                languageChangeConfirmation();
                            }
                        }
                    } else {
                        if (!isComingFromCplBaseActivity) {
                            languageChangeConfirmation();
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    languageChangeConfirmation();
                }
            });
        } catch (Exception e) {
            languageChangeConfirmation();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
