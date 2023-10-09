package com.oustme.oustsdk.layoutFour.components.newCatalogue.adapter;

import static android.content.ContentValues.TAG;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModule;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModuleUpdate;
import com.oustme.oustsdk.course_ui.CourseDetailScreen;
import com.oustme.oustsdk.model.request.CatalogViewUpdate;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.skill_ui.ui.SkillDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.oustme.oustsdk.utils.LayoutType.GRID;

public class CatalogueModuleAdapter extends RecyclerView.Adapter<CatalogueModuleAdapter.ViewHolder> implements Filterable {

    private ArrayList<CatalogueModule> catalogueModuleArrayList = new ArrayList<>();
    public Context context;
    String catalogueCategoryName;
    ActiveUser activeUser;
    int type;
    ValueFilter valueFilter;
    ArrayList<CatalogueModule> mData;

    public void setCatalogueModuleArrayList(ArrayList<CatalogueModule> catalogueModuleArrayList, Context context, int type) {
        this.catalogueModuleArrayList = catalogueModuleArrayList;
        this.mData = catalogueModuleArrayList;
        this.context = context;
        this.type = type;

    }

    public void setCatalogueCategoryName(String catalogueCategoryName) {
        this.catalogueCategoryName = catalogueCategoryName;
    }

    public void setActiveUser(ActiveUser activeUser) {
        this.activeUser = activeUser;
    }

    public ArrayList<CatalogueModule> getCatalogueModuleArrayList() {
        if (catalogueModuleArrayList != null) {
            return catalogueModuleArrayList;
        } else {
            return new ArrayList<>();
        }
    }

    public ArrayList<CatalogueModule> getDataCatalogueModuleArrayList() {
        if (mData != null) {
            return mData;
        } else {
            return new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        if (type == GRID) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_modules_grid, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_adapter, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.setIsRecyclable(false);
        return viewHolder;


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            CatalogueModule catalogueModule = mData.get(position);

            if (catalogueModule != null) {

                holder.include_date.setVisibility(View.GONE);

                if (type == GRID) {
                    ViewGroup.LayoutParams layoutParams = holder.iv_banner.getLayoutParams();
                    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                    int scrWidth = metrics.widthPixels;
                    layoutParams.height = (int) (scrWidth / 2.5);
                    holder.iv_banner.setLayoutParams(layoutParams);
                }

                String displayType = "";
                String name;
                String coins;
                String score;
                String action_name = "" + context.getResources().getString(R.string.view);
                Drawable action_drawable = context.getResources().getDrawable(R.drawable.ic_start_web);
                String image = "";
                String durationText = "1 min";
                long progress;

                String toolbarColorCode = OustPreferences.get("toolbarColorCode");

                if (toolbarColorCode == null || toolbarColorCode.isEmpty()) {

                    toolbarColorCode = "#01b5a2";
                }

                String textColorCode = OustPreferences.get("toolbarColorCode");

                if (textColorCode == null || textColorCode.isEmpty()) {

                    textColorCode = "#01b5a2";
                }

                holder.pb_module.setVisibility(View.INVISIBLE);
                holder.tv_percentage.setVisibility(View.INVISIBLE);
                holder.tv_timer.setVisibility(View.GONE);
                holder.tv_score.setVisibility(View.INVISIBLE);

                if (catalogueModule.getContentType() != null && !catalogueModule.getContentType().isEmpty()) {

                    if (catalogueModule.getContentType().equalsIgnoreCase("ASSESSMENT")) {
                        displayType = context.getResources().getString(R.string.assessment);
                        score = context.getResources().getString(R.string.score_text) + " " + catalogueModule.getAssessmentScore();
                        action_name = "" + context.getResources().getString(R.string.start);
                        Spannable spanText = new SpannableString(score);
                        if (score.contains(" ")) {
                            String[] scoreCount = score.split(" ");
                            spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#F87800")), 0, scoreCount[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            //spanText.setSpan(new RelativeSizeSpan(1.75f), 0, scoreCount[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        }
                        holder.tv_coin.setVisibility(View.GONE);
                        holder.tv_score.setText(spanText);
                        if (catalogueModule.getAssessmentScore() != 0 && catalogueModule.isShowAssessmentResultScore()) {
                            holder.tv_score.setVisibility(View.VISIBLE);
                        } else {
                            holder.info_separator.setVisibility(View.GONE);
                            holder.tv_score.setVisibility(View.GONE);
                        }


                        if (!catalogueModule.isRecurring() && catalogueModule.getCompletionDateAndTime() != null &&
                                !catalogueModule.getCompletionDateAndTime().isEmpty() && !catalogueModule.getCompletionDateAndTime().equals("null")) {

                            action_name = "" + context.getResources().getString(R.string.view);
                            action_drawable = context.getResources().getDrawable(R.drawable.ic_start_web);

                            if (catalogueModule.getState() != null &&
                                    catalogueModule.getState().equalsIgnoreCase("COMPLETED")) {

                                if (catalogueModule.isShowAssessmentResultScore()) {
                                    if (catalogueModule.isPassed()) {
                                        action_name = "" + context.getResources().getString(R.string.passed);
                                        action_drawable = context.getResources().getDrawable(R.drawable.ic_tick_done);
                                        toolbarColorCode = "#34C759";
                                        textColorCode = "#34C759";
                                    } else {
                                        action_name = "" + context.getResources().getString(R.string.failed_text);
                                        action_drawable = context.getResources().getDrawable(R.drawable.ic_close_circle);
                                        toolbarColorCode = "#E21B1B";
                                        textColorCode = "#E21B1B";
                                    }
                                } else {
                                    action_name = "" + context.getResources().getString(R.string.done_text);
                                    action_drawable = context.getResources().getDrawable(R.drawable.ic_tick_done);
                                    toolbarColorCode = "#34C759";
                                    textColorCode = "#34C759";
                                }

                            }

                        } else {
                            if (catalogueModule.getCompletionPercentage() != 0) {
                                if (catalogueModule.getCompletionPercentage() < 100) {
                                    action_name = "" + context.getResources().getString(R.string.resume);
                                    progress = catalogueModule.getCompletionPercentage();
                                    holder.pb_module.setVisibility(View.VISIBLE);
                                    holder.tv_percentage.setVisibility(View.VISIBLE);

                                    holder.pb_module.setProgress((int) progress);
                                    String percentage = progress + " %";
                                    holder.tv_percentage.setText(percentage);
                                }
                            }

                            if (catalogueModule.getState() != null
                                    && catalogueModule.getState().equalsIgnoreCase("INPROGRESS")) {
                                action_name = "" + context.getResources().getString(R.string.resume);
                                if (catalogueModule.getCompletionPercentage() == 0) {
                                    try {
                                        String assessmentUserProgressUrl = OustSdkApplication.getContext().getResources().getString(R.string.get_user_user_assessment_progress);
                                        String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
                                        assessmentUserProgressUrl = assessmentUserProgressUrl.replace("{org-id}", "" + tenantName);
                                        assessmentUserProgressUrl = assessmentUserProgressUrl.replace("{assessmentId}", "" + catalogueModule.getContentId());
                                        assessmentUserProgressUrl = assessmentUserProgressUrl.replace("{userId}", "" + OustAppState.getInstance().getActiveUser().getStudentid());

                                        assessmentUserProgressUrl = HttpManager.getAbsoluteUrl(assessmentUserProgressUrl);

                                        Log.d(TAG, "checkForSavedAssessment: " + assessmentUserProgressUrl);
                                        ApiCallUtils.doNetworkCall(Request.Method.GET, assessmentUserProgressUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Log.d(TAG, "fetchAssessmentsFromFirebase onResponse: " + response.toString());
                                                Map<String, Object> assessmentProgressMainMap = new HashMap<>();
                                                ObjectMapper mapper = new ObjectMapper();
                                                try {
                                                    assessmentProgressMainMap = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                                                    });
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    OustSdkTools.sendSentryException(e);
                                                }
                                                if (assessmentProgressMainMap != null) {
                                                    long questionIndex = 0;
                                                    int totalQues = 0;
                                                    String gameId = null;
                                                    if (assessmentProgressMainMap.get("gameId") != null) {
                                                        if (Objects.requireNonNull(assessmentProgressMainMap.get("gameId")).getClass() == Long.class) {
                                                            gameId = String.valueOf(assessmentProgressMainMap.get("gameId"));
                                                        } else if (Objects.requireNonNull(assessmentProgressMainMap.get("gameId")).getClass() == Integer.class) {
                                                            gameId = String.valueOf(assessmentProgressMainMap.get("gameId"));
                                                        } else {
                                                            gameId = (String) assessmentProgressMainMap.get("gameId");
                                                        }
                                                    }
                                                    if (assessmentProgressMainMap.get("assessmentState") != null && gameId != null && !gameId.equalsIgnoreCase("0") && !gameId.isEmpty()) {
                                                        if (assessmentProgressMainMap.get("totalQuestion") != null) {
                                                            if (Objects.requireNonNull(assessmentProgressMainMap.get("totalQuestion")).getClass() == Long.class) {
                                                                totalQues = (int) ((long) assessmentProgressMainMap.get("totalQuestion"));
                                                            } else if (Objects.requireNonNull(assessmentProgressMainMap.get("totalQuestion")).getClass() == Integer.class) {
                                                                totalQues = (int) assessmentProgressMainMap.get("totalQuestion");
                                                            } else {
                                                                totalQues = Integer.parseInt((String) Objects.requireNonNull(assessmentProgressMainMap.get("totalQuestion")));
                                                            }
                                                        }

                                                        if (assessmentProgressMainMap.get("questionIndex") != null) {
                                                            if (Objects.requireNonNull(assessmentProgressMainMap.get("questionIndex")).getClass() == Long.class) {
                                                                questionIndex = (int) (long) assessmentProgressMainMap.get("questionIndex");
                                                            } else {
                                                                questionIndex = OustSdkTools.newConvertToLong(assessmentProgressMainMap.get("questionIndex"));
                                                            }
                                                            if (questionIndex != 0) {
                                                                questionIndex = questionIndex - 1;
                                                            }
                                                        }

                                                        String assessmentStatus = null;
                                                        if (assessmentProgressMainMap.get("assessmentState") != null) {
                                                            assessmentStatus = (String) assessmentProgressMainMap.get("assessmentState");
                                                        }

                                                        if (totalQues > 0 && assessmentStatus != null && !assessmentStatus.isEmpty() && !assessmentStatus.equalsIgnoreCase("SUBMITTED")) {
                                                            double value = ((float) questionIndex / (float) totalQues);
                                                            double percentageValue = value * 100;
                                                            catalogueModule.setCompletionPercentage((long) percentageValue);
                                                            if (catalogueModule.getCompletionPercentage() != 0) {
                                                                if (catalogueModule.getCompletionPercentage() < 100) {
                                                                    holder.pb_module.setVisibility(View.VISIBLE);
                                                                    holder.tv_percentage.setVisibility(View.VISIBLE);
                                                                    holder.pb_module.setProgress((int) catalogueModule.getCompletionPercentage());
                                                                    String percentage = catalogueModule.getCompletionPercentage() + " %";
                                                                    holder.tv_percentage.setText(percentage);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        OustSdkTools.sendSentryException(e);
                                    }
                                }
                            }
                        }
                    } else if (catalogueModule.getContentType().equalsIgnoreCase("COURSE")) {
                        displayType = context.getResources().getString(R.string.course_text);
                        coins = catalogueModule.getOustCoins() + "";
                        holder.tv_score.setVisibility(View.GONE);

                        if (catalogueModule.getCompletionPercentage() != 0) {
                            if (catalogueModule.getCompletionPercentage() < 100) {
                                action_name = "" + context.getResources().getString(R.string.resume);
                                progress = catalogueModule.getCompletionPercentage();
                                holder.pb_module.setVisibility(View.VISIBLE);
                                holder.tv_percentage.setVisibility(View.VISIBLE);

                                holder.pb_module.setProgress((int) progress);
                                String percentage = progress + " %";
                                holder.tv_percentage.setText(percentage);
                            } else {
                                action_name = "" + context.getResources().getString(R.string.done_text);
                                action_drawable = context.getResources().getDrawable(R.drawable.ic_tick_done);
                                toolbarColorCode = "#34C759";
                                textColorCode = "#34C759";
                            }
                        } else {
                            action_name = "" + context.getResources().getString(R.string.start);
                        }

                        if (coins.equalsIgnoreCase("0") || coins.isEmpty() || coins.equalsIgnoreCase("null")) {
                            holder.tv_coin.setVisibility(View.GONE);
                            holder.info_separator.setVisibility(View.GONE);
                        } else {
                            holder.tv_coin.setVisibility(View.VISIBLE);
                            holder.info_separator.setVisibility(View.VISIBLE);
                            holder.tv_coin.setText(coins);
                        }
                    } else if (catalogueModule.getContentType().equalsIgnoreCase("SOCCER_SKILL")) {
                        displayType = context.getResources().getString(R.string.skill_text);
                    }
                }

                if (catalogueModule.getContentDuration() != 0) {
                    double duration = (catalogueModule.getContentDuration()) / 60;
                    durationText = (int) duration + " min";
                    if (catalogueModule.getContentDuration() < 60) {
                        durationText = "1 min";

                    }


                }

                holder.tv_timer.setVisibility(View.VISIBLE);
                holder.tv_timer.setText(durationText);


                if (catalogueModule.getIcon() != null && !catalogueModule.getIcon().isEmpty() && !catalogueModule.getIcon().equalsIgnoreCase("null")) {
                    image = catalogueModule.getIcon();
                } else if (catalogueModule.getBanner() != null && !catalogueModule.getBanner().isEmpty() && !catalogueModule.getBanner().equalsIgnoreCase("null")) {
                    image = catalogueModule.getBanner();
                } else {
                    if (catalogueModule.getContentType() != null && catalogueModule.getContentType().equalsIgnoreCase("SOCCER_SKILL")) {
                        Glide.with(context).load(context.getDrawable(R.drawable.skill_thumbnail)).into(holder.iv_banner);
                    }
                }

                name = catalogueModule.getName();

                holder.imageView2.setImageDrawable(action_drawable);
                Drawable btn_image_drawable = holder.imageView2.getDrawable();

                DrawableCompat.setTint(
                        DrawableCompat.wrap(btn_image_drawable),
                        Color.parseColor(toolbarColorCode)
                );

                holder.imageView2.setImageDrawable(btn_image_drawable);

                holder.tv_action.setTextColor(Color.parseColor(textColorCode));

                GradientDrawable borderBackground = (GradientDrawable) context.getResources().getDrawable(R.drawable.bg_round_8);
                borderBackground.setStroke(3, Color.parseColor(toolbarColorCode));
                holder.action_root_lay.setBackground(borderBackground);

                holder.tv_module_type.setText(displayType);

                holder.tv_title.setText(name);
                holder.tv_action.setText(action_name);


                if (image != null && !image.isEmpty()) {
                    Glide.with(context).load(image).into(holder.iv_banner);
                }


                holder.task_root_layout.setOnClickListener(v -> {
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 0.94f);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 0.96f);
                    scaleDownX.setDuration(150);
                    scaleDownY.setDuration(150);
                    scaleDownX.setRepeatCount(1);
                    scaleDownY.setRepeatCount(1);
                    scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
                    scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
                    scaleDownX.setInterpolator(new DecelerateInterpolator());
                    scaleDownY.setInterpolator(new DecelerateInterpolator());
                    AnimatorSet scaleDown = new AnimatorSet();
                    scaleDown.play(scaleDownX).with(scaleDownY);
                    scaleDown.start();
                    scaleDown.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            catalogueRedirection(catalogueModule, position);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }


    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        View include_date, include_module, tv_action_lay, info_separator;
        ImageView iv_banner, imageView2, edit_iv, delete_iv;//view3,
        LinearLayout action_root_lay;
        LinearLayout event_lay, end_lay, entry_layout;
        LinearLayout non_event_lay;
        ConstraintLayout task_root_layout;
        //CardView card_date;
        CardView card;
        TextView tv_date, tv_module_type, tv_title, tv_coin, tv_timer, tv_score, startlabel_text,
                tv_time, tv_time_end, tv_percentage, tv_action;
        ProgressBar pb_module;


        public ViewHolder(View itemView) {
            super(itemView);

            include_date = itemView.findViewById(R.id.include_date);
            include_module = itemView.findViewById(R.id.include_module);
            tv_date = include_date.findViewById(R.id.tv_date);
            tv_module_type = include_module.findViewById(R.id.tv_module_type);
            card = include_module.findViewById(R.id.card);
            iv_banner = include_module.findViewById(R.id.iv_banner);
            tv_title = include_module.findViewById(R.id.tv_title);
            event_lay = include_module.findViewById(R.id.event_lay);
            non_event_lay = include_module.findViewById(R.id.non_event_lay);
            entry_layout = include_module.findViewById(R.id.entry_layout);
            edit_iv = include_module.findViewById(R.id.edit_iv);
            delete_iv = include_module.findViewById(R.id.delete_iv);
            info_separator = include_module.findViewById(R.id.info_separator);
            tv_coin = include_module.findViewById(R.id.tv_coin);
            try {
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    tv_coin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_corn, 0, 0, 0);
                } else {
                    tv_coin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_golden, 0, 0, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            tv_timer = include_module.findViewById(R.id.tv_timer);
            tv_score = include_module.findViewById(R.id.tv_score);
            startlabel_text = include_module.findViewById(R.id.startlabel_text);
            tv_time = include_module.findViewById(R.id.tv_time);
            end_lay = include_module.findViewById(R.id.end_lay);
            tv_time_end = include_module.findViewById(R.id.tv_time_end);
            tv_action_lay = include_module.findViewById(R.id.tv_action_lay);
            pb_module = include_module.findViewById(R.id.pb_module);
            tv_percentage = include_module.findViewById(R.id.tv_percentage);
            tv_action = tv_action_lay.findViewById(R.id.tv_action);
            imageView2 = tv_action_lay.findViewById(R.id.imageView2);
            action_root_lay = tv_action_lay.findViewById(R.id.action_root_lay);
            task_root_layout = itemView.findViewById(R.id.task_root_layout);
        }
    }


    private void catalogueRedirection(CatalogueModule catalogueModule, int pos) {
        try {
            String catalog_distribution_url;
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(OustAppState.getInstance().getActiveUser().getStudentid());

            updateViewStatus(catalogueModule, pos);

            CatalogueModuleUpdate catalogueModuleUpdate = new CatalogueModuleUpdate();
            catalogueModuleUpdate.setPosition(pos);
            catalogueModuleUpdate.setType("Module");
            catalogueModuleUpdate.setCatalogueModule(catalogueModule);
            OustStaticVariableHandling.getInstance().setCatalogueModuleUpdate(catalogueModuleUpdate);

            if ((catalogueModule.getContentType() != null) && ((catalogueModule.getContentType().toUpperCase().contains("ASSESSMENT")))) {
                Gson gson = new Gson();
                ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
                if (catalogueModule.getDistributeTS() != null && !catalogueModule.getDistributeTS().isEmpty() && !catalogueModule.getDistributeTS().equalsIgnoreCase("null") &&
                        catalogueModule.getDistributedId() != 0) {
                    Intent assessmentIntent;
                    if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                        assessmentIntent = new Intent(context, AssessmentDetailScreen.class);
                    } else {
                        assessmentIntent = new Intent(context, AssessmentPlayActivity.class);
                    }
                    assessmentIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    assessmentIntent.putExtra("ActiveGame", gson.toJson(activeGame));
                    String id = "" + catalogueModule.getDistributedId();
                    assessmentIntent.putExtra("assessmentId", id);
                    assessmentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(assessmentIntent);
                } else {
                    catalog_distribution_url = context.getResources().getString(R.string.distribut_assessment_url);
                    catalog_distribution_url = catalog_distribution_url.replace("{assessmentId}", "" + catalogueModule.getContentId());
                    catalog_distribution_url = HttpManager.getAbsoluteUrl(catalog_distribution_url);
                    JSONObject jsonParams = new JSONObject();
                    jsonParams.put("users", jsonArray);
                    jsonParams.put("reusabilityAllowed", true);

                    ApiCallUtils.doNetworkCall(Request.Method.PUT, catalog_distribution_url,
                            OustSdkTools.getRequestObjectforJSONObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Log.d("TAG", "onResponse: " + response.toString());
                                        final CommonResponse[] commonResponses = new CommonResponse[]{null};
                                        if (response.optBoolean("success") && response.has("distributedId") && response.optLong("distributedId") > 0) {
                                            String finalAssessmentID = "" + response.optLong("distributedId");
                                            OustPreferences.save("catalogId", "ASSESSMENT" + finalAssessmentID);
                                            Intent assessmentIntent;
                                            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                                                assessmentIntent = new Intent(context, AssessmentDetailScreen.class);
                                            } else {
                                                assessmentIntent = new Intent(context, AssessmentPlayActivity.class);
                                            }
                                            assessmentIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            assessmentIntent.putExtra("ActiveGame", gson.toJson(activeGame));
                                            assessmentIntent.putExtra("assessmentId", finalAssessmentID);
                                            assessmentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(assessmentIntent);
                                        } else {
                                            commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                                            if (commonResponses[0] != null && commonResponses[0].getExceptionData() != null) {
                                                if (commonResponses[0].getExceptionData().getMessage() != null) {
                                                    OustSdkTools.showToast(commonResponses[0].getExceptionData().getMessage());
                                                } else {
                                                    OustSdkTools.showToast(context.getResources().getString(R.string.error_message));
                                                }
                                            } else if (commonResponses[0] != null && commonResponses[0].getError() != null) {
                                                OustSdkTools.showToast(commonResponses[0].getError());
                                            } else {
                                                OustSdkTools.showToast(context.getResources().getString(R.string.error_message));
                                            }
                                        }
                                    } catch (Exception e) {
                                        OustSdkTools.showToast(context.getResources().getString(R.string.error_message));
                                        e.printStackTrace();
                                        OustSdkTools.sendSentryException(e);
                                    }
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    OustSdkTools.showToast(context.getResources().getString(R.string.error_message));
                                }
                            });
                }
            } else if ((catalogueModule.getContentType() != null) && (catalogueModule.getContentType().toUpperCase().contains("COURSE"))) {
                Log.d("TAG", "catalogueRedirection: contentId:" + catalogueModule.getContentId() + " --- distributedId:" + catalogueModule.getDistributedId());
                if (catalogueModule.getDistributedId() > 0) {
                    String courseID = "" + catalogueModule.getDistributedId();
                    Intent courseIntent = new Intent(context, CourseDetailScreen.class);
                    courseIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    courseIntent.putExtra("learningId", courseID);
                    courseIntent.putExtra("catalog_id", "" + courseID);
                    courseIntent.putExtra("catalog_type", "COURSE");
                    context.startActivity(courseIntent);
                } else {
                    catalog_distribution_url = context.getResources().getString(R.string.distribut_course_url);
                    catalog_distribution_url = catalog_distribution_url.replace("{courseId}", "" + catalogueModule.getContentId());
                    catalog_distribution_url = HttpManager.getAbsoluteUrl(catalog_distribution_url);
                    JSONObject jsonParams = new JSONObject();
                    jsonParams.put("users", jsonArray);
                    jsonParams.put("reusabilityAllowed", true);

                    ApiCallUtils.doNetworkCall(Request.Method.PUT, catalog_distribution_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Log.d("TAG", "onResponse: " + response.toString());
                                        final CommonResponse[] commonResponses = new CommonResponse[]{null};
                                        if (response.optBoolean("success") && response.has("distributedId") && response.optLong("distributedId") > 0) {
                                            String finalCourseID = "" + response.optLong("distributedId");
                                            OustPreferences.save("catalogId", "COURSE" + catalogueModule.getContentId());
                                            Intent courseIntent = new Intent(context, CourseDetailScreen.class);
                                            courseIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            courseIntent.putExtra("learningId", finalCourseID);
                                            courseIntent.putExtra("catalog_id", "" + finalCourseID);
                                            courseIntent.putExtra("catalog_type", "COURSE");
                                            context.startActivity(courseIntent);
                                        } else {
                                            commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                                            if (commonResponses[0] != null && commonResponses[0].getExceptionData() != null) {
                                                if (commonResponses[0].getExceptionData().getMessage() != null) {
                                                    OustSdkTools.showToast(commonResponses[0].getExceptionData().getMessage());
                                                } else {
                                                    OustSdkTools.showToast(context.getResources().getString(R.string.error_message));
                                                }
                                            } else if (commonResponses[0] != null && commonResponses[0].getError() != null) {
                                                OustSdkTools.showToast(commonResponses[0].getError());
                                            } else {
                                                OustSdkTools.showToast(context.getResources().getString(R.string.error_message));
                                            }
                                        }
                                    } catch (Exception e) {
                                        OustSdkTools.showToast(context.getResources().getString(R.string.error_message));
                                        e.printStackTrace();
                                        OustSdkTools.sendSentryException(e);
                                    }
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    OustSdkTools.showToast(context.getResources().getString(R.string.error_message));
                                }
                            });
                }


            } else if ((catalogueModule.getContentType() != null) && (catalogueModule.getContentType().toUpperCase().contains("SOCCER_SKILL"))) {
                Intent skillIntent = new Intent(context, SkillDetailActivity.class);
                skillIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                skillIntent.putExtra("SkillId", "" + catalogueModule.getContentId());
                skillIntent.putExtra("category", catalogueCategoryName);
                skillIntent.putExtra("categoryId", catalogueModule.getCatalogueCategoryId());
                skillIntent.putExtra("catalog_type", catalogueModule.getContentType());
                skillIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(skillIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    public ActiveGame setGame(ActiveUser activeUser) {
        ActiveGame activeGame = new ActiveGame();
        activeGame.setGameid("");
        activeGame.setGames(activeUser.getGames());
        activeGame.setStudentid(activeUser.getStudentid());
        activeGame.setChallengerid(activeUser.getStudentid());
        activeGame.setChallengerDisplayName(activeUser.getUserDisplayName());
        activeGame.setChallengerAvatar(activeUser.getAvatar());
        activeGame.setOpponentAvatar(OustSdkTools.getMysteryAvatar());
        activeGame.setOpponentid("Mystery");
        activeGame.setOpponentDisplayName("Mystery");
        activeGame.setGameType(GameType.MYSTERY);
        activeGame.setGuestUser(false);
        activeGame.setRematch(false);
        activeGame.setGroupId("");
        activeGame.setLevel(activeUser.getLevel());
        activeGame.setLevelPercentage(activeUser.getLevelPercentage());
        activeGame.setWins(activeUser.getWins());
        activeGame.setIsLpGame(false);
        return activeGame;
    }

    private void updateViewStatus(CatalogueModule catalogueModule, int pos) {

        if (catalogueModule.getViewStatus() != null && (catalogueModule.getViewStatus().equalsIgnoreCase("NEW") ||
                catalogueModule.getViewStatus().equalsIgnoreCase("UPDATE"))) {
            mData.get(pos).setViewStatus("SEEN");
            ActiveUser activeUser;
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            CatalogViewUpdate catalogViewUpdate = new CatalogViewUpdate();
            catalogViewUpdate.setCatalogId(catalogueModule.getCatalogueId());
            catalogViewUpdate.setContentType(catalogueModule.getContentType());
            catalogViewUpdate.setContentId(catalogueModule.getContentId());
            catalogViewUpdate.setCategoryId(catalogueModule.getCatalogueCategoryId());
            catalogViewUpdate.setStudentid(activeUser.getStudentid());

            String url = OustSdkApplication.getContext().getResources().getString(R.string.catalog_view_update);
            final Gson gson = new Gson();
            url = HttpManager.getAbsoluteUrl(url);
            String jsonParams = gson.toJson(catalogViewUpdate);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    notifyDataSetChanged();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("CMA Error", "onErrorResponse: onError:" + error.getLocalizedMessage());
                }
            });

        }
    }

    public void modifyItem(final int position, final CatalogueModule catalogueModule) {
        try {
            catalogueModule.setDistributeTS(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
            mData.set(position, catalogueModule);
            notifyItemChanged(position);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<CatalogueModule> filterList = new ArrayList<>();
                for (int i = 0; i < catalogueModuleArrayList.size(); i++) {
                    if ((catalogueModuleArrayList.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(catalogueModuleArrayList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = catalogueModuleArrayList.size();
                results.values = catalogueModuleArrayList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mData = (ArrayList<CatalogueModule>) results.values;
            notifyDataSetChanged();
        }
    }
}