package com.oustme.oustsdk.adapter.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


import android.util.DisplayMetrics;
import android.util.Log;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.common.CatalogInfoActivity;
import com.oustme.oustsdk.activity.courses.LessonsActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.calendar_ui.ui.EventDataDetailScreen;
import com.oustme.oustsdk.customviews.CurveLayout;
import com.oustme.oustsdk.course_ui.CourseDetailScreen;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.interfaces.common.CommonLandingDataCallback;
import com.oustme.oustsdk.interfaces.common.DataLoaderNotifier;
import com.oustme.oustsdk.model.request.CatalogViewUpdate;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.skill_ui.ui.SkillDetailActivity;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 11/10/17.
 */

public class NewLandingModuleAdaptera extends RecyclerView.Adapter<NewLandingModuleAdaptera.MyViewHolder> {
    private static final String TAG = "NewLandingModuleAdapter";

    private List<CommonLandingData> commonLandingDataList;
    private String categoryName;
    private int lastPosition = 0;
    private boolean isMyDeskData = true;
    private HashMap<String, String> myDeskDataMap;
    private DataLoaderNotifier dataLoaderNotifier;
    private boolean isAdapterHorizontal = false;
    private boolean showLoading = false;
    private boolean showProgress = true;
    private OpenCPl mListener;
    private Context mContext;
    private CategoryClick categoryClick;

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public void setmyDeskDataMap(HashMap<String, String> myDeskDataMap) {
        this.myDeskDataMap = myDeskDataMap;
//        Log.d(TAG, "setmyDeskDataMap: "+this.myDeskDataMap.size());
    }

    public boolean isMyDeskData() {
        return isMyDeskData;
    }

    public void setMyDeskData(boolean myDeskData) {
        this.isMyDeskData = myDeskData;
    }

    public void setAdapterHorizontal(boolean adapterHorizontal) {
        isAdapterHorizontal = adapterHorizontal;
    }

    public NewLandingModuleAdaptera(List<CommonLandingData> commonLandingDataList) {
        this.commonLandingDataList = commonLandingDataList;
    }

    public void setCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setContext(Context context) {
        try {
            mContext = context;
            mListener = (OpenCPl) mContext;
            categoryClick = (CategoryClick) mContext;
            Log.d(TAG, "NewLandingModuleAdaptera: ");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setDataLoaderNotifier(DataLoaderNotifier dataLoaderNotifier) {
        this.dataLoaderNotifier = dataLoaderNotifier;
    }

    public void notifyListChnage(List<CommonLandingData> commonLandingDataList, int localSize) {
        this.commonLandingDataList = commonLandingDataList;
        newActivityOpened = false;
        notifyDataSetChanged();
    }

    public void updateListItems(List<CommonLandingData> commonLandingDataList1, int adapterSize) {
        try {
            newActivityOpened = false;
            final CommonLandingDataCallback diffCallback = new CommonLandingDataCallback(this.commonLandingDataList, commonLandingDataList1);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

            this.commonLandingDataList = new ArrayList<>();
            this.commonLandingDataList.addAll(commonLandingDataList1);
            diffResult.dispatchUpdatesTo(this);
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        if (commonLandingDataList != null)
            return commonLandingDataList.size();
        else
            return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView rowicon_image;
        private ImageView rowbanner_image, imageViewPercentageBg, category_banner, folder_image;
        private TextView coursenametext, total_enrolled_count, totaloc_text, moduleprogress, category_name;
        private RelativeLayout main_layout;
        private CurveLayout main_card_ll;
        private View folder_view;
        private CardView category_layout;
        // private RelativeLayout cource_mainrow;
        private RelativeLayout module_progress_ll;
        private LinearLayout linearLayoutBottomMain, coins_layout;
        private RelativeLayout bannerimage_layout;
        private ImageView mIconImage;
        private ImageView mImageViewCatalogIndicator, ic_grey_coin;

        private View percentageView, linearLayoutBottomView;

        MyViewHolder(View view) {
            super(view);
            // cource_mainrow = (RelativeLayout) view.findViewById(R.id.cource_mainrow);
            // coursenametext = (TextView) view.findViewById(R.id.coursenametext);
            // total_enrolled_count = (TextView) view.findViewById(R.id.total_enrolled_count);
            // totaloc_text = (TextView) view.findViewById(R.id.totaloc_text);
            // rowbanner_image = (ImageView) view.findViewById(R.id.rowbanner_image);
            main_card_ll = view.findViewById(R.id.curveLayout);
            coins_layout = view.findViewById(R.id.coins_layout);
            folder_image = view.findViewById(R.id.folder_image);
            folder_view = view.findViewById(R.id.folder_view);
            folder_view.setBackgroundColor(Color.parseColor(OustPreferences.get("toolbarColorCode")));
            folder_image.setBackground(OustSdkTools.drawableColor(mContext.getResources().getDrawable(R.drawable.ic_folder)));
            category_layout = view.findViewById(R.id.category_layout);
            //rowicon_image = (ImageView) view.findViewById(R.id.rowicon_image);
            main_layout = view.findViewById(R.id.main_layout);
            imageViewPercentageBg = view.findViewById(R.id.imageViewPercentageBg);

            coursenametext = view.findViewById(R.id.textViewTitle);
            total_enrolled_count = view.findViewById(R.id.textViewPeopleCount);
            totaloc_text = view.findViewById(R.id.textViewCoinsCount);
            category_name = view.findViewById(R.id.category_name);
            category_banner = view.findViewById(R.id.category_banner);
            rowbanner_image = view.findViewById(R.id.imageViewBg);
            rowicon_image = view.findViewById(R.id.rowicon_image);
            mIconImage = view.findViewById(R.id.rowicon_image);

            percentageView = view.findViewById(R.id.percentageView);
            percentageView.setVisibility(View.GONE);
            linearLayoutBottomMain = view.findViewById(R.id.linearLayoutBottomMain);
            linearLayoutBottomView = view.findViewById(R.id.linearLayoutBottomView);
            mImageViewCatalogIndicator = view.findViewById(R.id.CatalogueUpdateIndicator);
            bannerimage_layout = view.findViewById(R.id.curveLayout);

            try {
                ic_grey_coin = view.findViewById(R.id.ic_grey_coin);
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    ic_grey_coin.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
                } else {
                    ic_grey_coin.setImageResource(R.drawable.ic_coin);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                linearLayoutBottomView.setElevation(5.0f);
            }

            try {
                if (!isAdapterHorizontal) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bannerimage_layout.getLayoutParams();
                    //  LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bannerimage_layout.getLayoutParams();
                    DisplayMetrics metrics = OustSdkApplication.getContext().getResources().getDisplayMetrics();
                    int scrWidth = metrics.widthPixels;
                    int height = (int) ((((scrWidth - getDpForPixel(16)) / 2) - (getDpForPixel(16))) * 0.94);
                    //params.height = height;
                    //Log.d(TAG, "MyViewHolder: banner height: "+height);
                    layoutParams.height = height;
                    bannerimage_layout.setLayoutParams(layoutParams);
                } else {
                    DisplayMetrics metrics = OustSdkApplication.getContext().getResources().getDisplayMetrics();
                    int scrWidth = metrics.widthPixels;
                    int height = (int) ((((scrWidth - getDpForPixel(16)) / 2) - (getDpForPixel(16))) * 0.94);

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(height, LinearLayout.LayoutParams.WRAP_CONTENT);

                    //RelativeLayout.LayoutParams params = new  RelativeLayout.LayoutParams(getDpForPixel(122), LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, getDpForPixel(10), 0);
                    main_layout.setLayoutParams(params);
                    RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) bannerimage_layout.getLayoutParams();
                    params1.height = height;
                    //params1.height = getDpForPixel(122);
                    bannerimage_layout.setLayoutParams(params1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            if (isShowProgress()) {
                percentageView.setVisibility(View.VISIBLE);
                percentageView.bringToFront();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    percentageView.setElevation(5.0f);
                }
                // LayoutInflater mInflater = (LayoutInflater) OustSdkApplication.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                // View convertView1 = mInflater.inflate(R.layout.rotated_feedview, null);
                moduleprogress = view.findViewById(R.id.moduleprogress);
                module_progress_ll = view.findViewById(R.id.module_progress_ll);
                // main_card_ll.addView(convertView1);
            }
        }
    }

    private int getDpForPixel(int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, OustSdkApplication.getContext().getResources().getDisplayMetrics());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_catalog_item_landing, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            if (position < commonLandingDataList.size()) {
                CommonLandingData commonLandingData = commonLandingDataList.get(position);
                if (commonLandingData.getType() != null && (commonLandingData.getType().equalsIgnoreCase("SOCCER_SKILL") ||
                        commonLandingData.getType().equalsIgnoreCase("CLASSROOM") || commonLandingData.getType().equalsIgnoreCase("WEBINAR"))) {
                    holder.coins_layout.setVisibility(View.GONE);
                    holder.percentageView.setVisibility(View.GONE);
                    holder.rowicon_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    holder.mImageViewCatalogIndicator.setVisibility(View.GONE);
                }

                if (commonLandingData.getType() != null && commonLandingData.getType().equalsIgnoreCase("SOCCER_SKILL")) {

                    if ((commonLandingData.getIcon() != null) && (!commonLandingData.getIcon().isEmpty()) && (!commonLandingData.getIcon().equalsIgnoreCase("null"))) {
                        holder.rowbanner_image.setVisibility(View.GONE);
                        holder.mIconImage.setVisibility(View.VISIBLE);
                        holder.bannerimage_layout.bringToFront();
                        holder.bannerimage_layout.setVisibility(View.VISIBLE);
                        if (OustSdkTools.checkInternetStatus()) {
                            Picasso.get().load(commonLandingData.getIcon()).error(R.drawable.skill_thumbnail).into(holder.mIconImage);
                            Picasso.get().load(commonLandingData.getIcon()).error(R.drawable.skill_thumbnail).into(holder.category_banner);
                        } else {

                            Picasso.get().load(commonLandingData.getIcon()).error(R.drawable.skill_thumbnail).networkPolicy(NetworkPolicy.OFFLINE).into(holder.mIconImage);
                            Picasso.get().load(commonLandingData.getIcon()).error(R.drawable.skill_thumbnail).networkPolicy(NetworkPolicy.OFFLINE).into(holder.category_banner);
                        }

                    } else if ((commonLandingData.getBanner() != null) && (!commonLandingData.getBanner().isEmpty() && (!commonLandingData.getBanner().equalsIgnoreCase("null")))) {
                        holder.rowbanner_image.setVisibility(View.VISIBLE);
                        holder.mIconImage.setVisibility(View.GONE);
                        holder.bannerimage_layout.bringToFront();
                        holder.bannerimage_layout.setVisibility(View.VISIBLE);
                        if (OustSdkTools.checkInternetStatus()) {
                            Picasso.get().load(commonLandingData.getBanner()).error(R.drawable.skill_thumbnail).into(holder.rowbanner_image);
                            Picasso.get().load(commonLandingData.getBanner()).error(R.drawable.skill_thumbnail).into(holder.category_banner);
                        } else {
                            Picasso.get().load(commonLandingData.getBanner()).error(R.drawable.skill_thumbnail).networkPolicy(NetworkPolicy.OFFLINE).into(holder.rowbanner_image);
                            Picasso.get().load(commonLandingData.getBanner()).error(R.drawable.skill_thumbnail).networkPolicy(NetworkPolicy.OFFLINE).into(holder.category_banner);
                        }
                    } else {


                        holder.category_banner.setImageResource(R.drawable.skill_thumbnail);
                        holder.rowbanner_image.setImageResource(R.drawable.skill_thumbnail);
                        holder.rowicon_image.setImageResource(R.drawable.skill_thumbnail);

                    }

                } else {
                    if ((commonLandingData.getIcon() != null) && (!commonLandingData.getIcon().isEmpty()) && (!commonLandingData.getIcon().equalsIgnoreCase("null"))) {
                        holder.rowbanner_image.setVisibility(View.GONE);
                        holder.mIconImage.setVisibility(View.VISIBLE);
                        holder.bannerimage_layout.bringToFront();
                        holder.bannerimage_layout.setVisibility(View.VISIBLE);
                        if (OustSdkTools.checkInternetStatus()) {
                            Picasso.get().load(commonLandingData.getIcon()).into(holder.mIconImage);
                            Picasso.get().load(commonLandingData.getIcon()).into(holder.category_banner);
                        } else {

                            Picasso.get().load(commonLandingData.getIcon()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.mIconImage);
                            Picasso.get().load(commonLandingData.getIcon()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.category_banner);
                        }

                    } else if ((commonLandingData.getBanner() != null) && (!commonLandingData.getBanner().isEmpty() && (!commonLandingData.getBanner().equalsIgnoreCase("null")))) {
                        holder.rowbanner_image.setVisibility(View.VISIBLE);
                        holder.mIconImage.setVisibility(View.GONE);
                        holder.bannerimage_layout.bringToFront();
                        holder.bannerimage_layout.setVisibility(View.VISIBLE);
                        if (OustSdkTools.checkInternetStatus()) {
                            Picasso.get().load(commonLandingData.getBanner()).into(holder.rowbanner_image);
                            Picasso.get().load(commonLandingData.getBanner()).into(holder.category_banner);
                        } else {
                            Picasso.get().load(commonLandingData.getBanner()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.rowbanner_image);
                            Picasso.get().load(commonLandingData.getBanner()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.category_banner);
                        }
                    } else {
                        BitmapDrawable bd;
                        if (commonLandingData.getType().equalsIgnoreCase("COURSE")) {
                            Glide.with(mContext).load(R.drawable.course_thumbnail).into(holder.rowicon_image);
                        } else if (commonLandingData.getType().equalsIgnoreCase("ASSESSMENT")) {
                            Glide.with(mContext).load(R.drawable.assessment_thumbnail).into(holder.rowicon_image);
                        } else {
                            bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.mydesk));
                            Picasso.get().load("nnn")
                                    .placeholder(bd).error(bd)
                                    .into(holder.category_banner);
                            Picasso.get().load("nnn")
                                    .placeholder(bd).error(bd)
                                    .into(holder.rowbanner_image);
                            Picasso.get().load("nnn")
                                    .placeholder(bd).error(bd)
                                    .into(holder.rowicon_image);
                        }
                    }
                }

                if (commonLandingData.getName() != null) {
                    holder.coursenametext.setVisibility(View.VISIBLE);
                    holder.coursenametext.setText(commonLandingData.getName());
                    holder.category_name.setText(commonLandingData.getName());
                } else {
                    holder.coursenametext.setVisibility(View.VISIBLE);
                    holder.coursenametext.setText("");
                }
                if (commonLandingData.getEnrollCount() > 0) {
                    holder.total_enrolled_count.setText("" + commonLandingData.getEnrollCount());
                } else {
                    holder.total_enrolled_count.setText("0");
                }
                if (commonLandingData.getOc() > 0) {
                    holder.totaloc_text.setText("" + commonLandingData.getOc());
                } else {
                    holder.totaloc_text.setText("0");
                }
                if (isShowProgress()) {
                    if (commonLandingData.getType().equalsIgnoreCase("ASSESSMENT")) {
                        holder.module_progress_ll.setVisibility(View.GONE);
                        holder.percentageView.setVisibility(View.GONE);
                    } else if (commonLandingData.getType().equalsIgnoreCase("COURSE")) {
                        holder.module_progress_ll.setVisibility(View.VISIBLE);
                        holder.percentageView.setVisibility(View.VISIBLE);
                    }
                    if (commonLandingData.getCompletionPercentage() > 0) {
                        if (commonLandingData.getCompletionPercentage() >= 100) {
                            holder.imageViewPercentageBg.setImageResource(R.drawable.ic_completed_icon);
                        }
                        holder.moduleprogress.setText("" + commonLandingData.getCompletionPercentage() + "%");
                    } else {
                        holder.moduleprogress.setText("0%");
                    }
                }

                if (commonLandingData.getType().equalsIgnoreCase("CATEGORY")) {
                    holder.main_card_ll.setVisibility(View.GONE);
                    holder.category_layout.setVisibility(View.VISIBLE);
                } else {
                    holder.main_card_ll.setVisibility(View.VISIBLE);
                    holder.category_layout.setVisibility(View.GONE);
                }

                holder.main_layout.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void onClick(View v) {
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
                                if (!OustStaticVariableHandling.getInstance().isModuleClicked()) {
                                    clickonRow(holder.getAdapterPosition());
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {
                            }
                        });
                    }
                });
                if (commonLandingData.getViewStatus() != null && commonLandingData.getViewStatus().equalsIgnoreCase("NEW") || commonLandingData.getViewStatus().equalsIgnoreCase("UPDATE")) {
                    holder.mImageViewCatalogIndicator.setVisibility(View.VISIBLE);
                } else {
                    holder.mImageViewCatalogIndicator.setVisibility(View.GONE);
                }
            } else {
                holder.main_layout.setVisibility(View.GONE);
                callForNextData();
            }

            if ((position == commonLandingDataList.size() - 1)) {
                if (!OustDataHandler.getInstance().isAllCoursesLoaded()) {
                    try {
                        if (dataLoaderNotifier != null)
                            dataLoaderNotifier.getNextData(commonLandingDataList.get(position).getType());
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void callForNextData() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (dataLoaderNotifier != null) {
                            dataLoaderNotifier.getNextData("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }, 150);
        } catch (Exception e) {
        }
    }


    private boolean newActivityOpened = false;

    public void clickonRow(int position) {
        try {
            CommonLandingData commonLandingData = commonLandingDataList.get(position);
            updateViewStatus(commonLandingData, position);
            if (isMyDeskData) {
                if (!newActivityOpened) {
                    launchActivity(commonLandingData);
                }
            } else {
                String catalog_id = commonLandingData.getId();
                String catalog_type = commonLandingData.getType();
                if (catalog_type.equals("CPL")) {
                    catalog_id = catalog_id.replace("cpl", "");
                    catalog_id = catalog_id.replace("CPL", "");
                }
                if (catalog_type.equals("COURSE")) {
                    catalog_id = catalog_id.replace("course", "");
                    catalog_id = catalog_id.replace("COURSE", "");
                } else if (catalog_type.equals("ASSESSMENT")) {
                    catalog_id = catalog_id.replace("assessment", "");
                    catalog_id = catalog_id.replace("ASSESSMENT", "");
                } else if (catalog_type.equals("COLLECTION")) {
                    catalog_id = catalog_id.replace("collection", "");
                    catalog_id = catalog_id.replace("COLLECTION", "");
                }
                if (catalog_id.contains("SURVEY")) {
                    catalog_id.replace("SURVEY", "");
                }
                if (catalog_id.contains("SOCCER_SKILL")) {
                    catalog_id.replace("SOCCER_SKILL", "");
                }
                commonLandingData.setId(catalog_id);
                if (!catalog_id.contains(commonLandingData.getType())) {
                    catalog_id = commonLandingData.getType() + "" + catalog_id;
                }
                if (commonLandingData.getType() != null && commonLandingData.getType().equalsIgnoreCase("SOCCER_SKILL")) {

                    Intent intent = new Intent(OustSdkApplication.getContext(), SkillDetailActivity.class);
                    intent.putExtra("SkillId", commonLandingData.getId());
                    intent.putExtra("category", categoryName);
                    intent.putExtra("categoryId", commonLandingData.getCatalogCategoryId());
                    intent.putExtra("catalog_type", commonLandingData.getType());
                    intent.putExtra("deskmap", myDeskDataMap);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);


                } else {
                    if (myDeskDataMap != null && myDeskDataMap.containsKey(catalog_id)) {
                        launchActivity(commonLandingData);
                    } else {
                        if (myDeskDataMap != null) {
                            OustStaticVariableHandling.getInstance().setModuleClicked(true);
                            if (commonLandingData.getType().equalsIgnoreCase("CATEGORY")) {
                                Log.d(TAG, "clickonRow: " + commonLandingData.getId());
                                if (categoryClick != null) {
                                    categoryClick.categoryClick(commonLandingData.getId());
                                }
                            } else {
                                Intent intent = new Intent(OustSdkApplication.getContext(), CatalogInfoActivity.class);
                                intent.putExtra("catalog_id", commonLandingData.getId());
                                intent.putExtra("catalog_type", commonLandingData.getType());
                                intent.putExtra("deskmap", myDeskDataMap);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                            }

                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateViewStatus(CommonLandingData commonLandingData, int pos) {
        if (commonLandingData.getViewStatus() != null && (commonLandingData.getViewStatus().equalsIgnoreCase("NEW") ||
                commonLandingData.getViewStatus().equalsIgnoreCase("UPDATE"))) {
            commonLandingDataList.get(pos).setViewStatus("SEEN");
            ActiveUser activeUser;
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            CatalogViewUpdate catalogViewUpdate = new CatalogViewUpdate();
            catalogViewUpdate.setCatalogId(commonLandingData.getCatalogId());
            catalogViewUpdate.setContentType(commonLandingData.getType());
            catalogViewUpdate.setContentId(commonLandingData.getCatalogContentId());
            catalogViewUpdate.setCategoryId(commonLandingData.getCatalogCategoryId());
            catalogViewUpdate.setStudentid(activeUser.getStudentid());

            String url = OustSdkApplication.getContext().getResources().getString(R.string.catalog_view_update);
            final Gson mGson = new Gson();
            url = HttpManager.getAbsoluteUrl(url);
            String jsonParams = mGson.toJson(catalogViewUpdate);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    // OustSdkTools.showToast("CPL Distributed Successfully");
                    // mProgressbarAPICall.setVisibility(View.GONE);
                    notifyDataSetChanged();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //  OustSdkTools.showToast("Unable to Distribute CPL, Please try again.");
                    //  mProgressbarAPICall.setVisibility(View.GONE);
                    Log.d(TAG, "onErrorResponse: onError:" + error.getLocalizedMessage());
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, url, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                   // OustSdkTools.showToast("CPL Distributed Successfully");
                   // mProgressbarAPICall.setVisibility(View.GONE);
                    notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  //  OustSdkTools.showToast("Unable to Distribute CPL, Please try again.");
                  //  mProgressbarAPICall.setVisibility(View.GONE);
                    Log.d(TAG, "onErrorResponse: onError:"+error.getLocalizedMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", OustPreferences.get("tanentid"));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    return params;
                }
            };
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
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

    public interface OpenCPl {
        void launchCPL(String cplId);
    }

    public interface CategoryClick {
        void categoryClick(String id);
    }

    private void launchActivity(CommonLandingData commonLandingData) {
        Log.d(TAG, "launchActivity: ");
        Log.d(TAG, "launchActivity: type:" + commonLandingData.getType());
        OustStaticVariableHandling.getInstance().setModuleClicked(true);

        if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("COLLECTION"))) {
            Intent intent = new Intent(OustSdkApplication.getContext(), LessonsActivity.class);
            String id = commonLandingData.getId();
            id = id.replace("COLLECTION", "");
            intent.putExtra("collectionId", id);
            intent.putExtra("banner", commonLandingData.getBanner());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("CPL"))) {
            if (commonLandingData.getId() != null) {
                mListener.launchCPL(commonLandingData.getId());
            } else if (commonLandingData.getCplId() != 0) {
                mListener.launchCPL(String.valueOf(commonLandingData.getCplId()));
            }
        } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("ASSESSMENT"))) {
            Gson gson = new Gson();
            Intent intent;
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
            } else {
                intent = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
            }
            //Intent intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
            ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            if (commonLandingData.getCompletionPercentage() > 99) {
                intent.putExtra("isFromWorkDairy", true);
            }
            String id = commonLandingData.getId();
            id = id.replace("ASSESSMENT", "");
            intent.putExtra("assessmentId", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("SURVEY"))) {
            Gson gson = new Gson();
            Log.d(TAG, "launchActivity: assessmentquestion:");
            Intent intent = new Intent(OustSdkApplication.getContext(), SurveyDetailActivity.class);
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI)) {
                intent = new Intent(OustSdkApplication.getContext(), SurveyComponentActivity.class);
            }
            ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            String id = commonLandingData.getId();
            id = id.replace("ASSESSMENT", "");
            intent.putExtra("assessmentId", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("WEBINAR") || commonLandingData.getType().contains("CLASSROOM"))) {
            Intent eventDetail = new Intent(mContext, EventDataDetailScreen.class);
            long meetingId = Long.parseLong(commonLandingData.getId().replace("MEETINGCALENDAR", ""));
            eventDetail.putExtra("meetingId", meetingId);
            mContext.startActivity(eventDetail);
        } else {
            Intent intent = new Intent(OustSdkApplication.getContext(), CourseDetailScreen.class);
            String id = commonLandingData.getId();
            id = id.replace("COURSE", "");
            intent.putExtra("learningId", id);
            intent.putExtra("isNotFromFeed", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        }
    }

    public void removeItem(final String id) {

        if (id != null && !id.isEmpty()) {
            int position = find(commonLandingDataList, id);
            //
            if (position >= 0) {
                Log.d("NewLandingModule", id + "-" + position);
                commonLandingDataList.remove(position);
                notifyItemRangeRemoved(position, 1);
                notifyDataSetChanged();
            }

        }


    }

    // Generic function to find the index of an element in an object array in Java
    public static int find(List<CommonLandingData> commonLandingData, String id) {

        for (int i = 0; i < commonLandingData.size(); i++)
            if (commonLandingData.get(i).getId().equalsIgnoreCase(id))
                return i;

        return -1;
    }

}
