package com.oustme.oustsdk.adapter.courses;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.courses.CourseMultiLingualActivity;
import com.oustme.oustsdk.activity.courses.LessonsActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.course.MultilingualCourse;
import com.oustme.oustsdk.interfaces.common.DataLoaderNotifier;
import com.oustme.oustsdk.interfaces.common.PageLoader;
import com.oustme.oustsdk.interfaces.common.RowClickCallBack;
import com.oustme.oustsdk.interfaces.course.SendCertificateCallBack;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 30/08/16.
 */
public class NewLandingCoursesAdapter extends RecyclerView.Adapter<NewLandingCoursesAdapter.MyViewHolder> {

    private static final String TAG = "NewLandingCoursesAdapte";
    
    List<CommonLandingData> allCources = new ArrayList<>();
    private int fragmentNo = 0;
    private boolean newActivityOpened = false;
    private boolean hideArchiveIcon = false;
    private int totalModules;
    private PageLoader pageLoader;

    private SendCertificateCallBack sendCertificateCallBack;

    public void setSendCertificateCallBack(SendCertificateCallBack sendCertificateCallBack) {
        this.sendCertificateCallBack = sendCertificateCallBack;
    }

    public void setTotalModules(int totalModules) {
        this.totalModules = totalModules;
    }

    private DataLoaderNotifier dataLoaderNotifier;

    public void setDataLoaderNotifier(DataLoaderNotifier dataLoaderNotifier) {
        this.dataLoaderNotifier = dataLoaderNotifier;
    }
    public void setPageLoader(PageLoader pageLoader){
        this.pageLoader=pageLoader;
    }

    private RowClickCallBack rowClickCallBack;

    public void setRowClickCallBack(RowClickCallBack rowClickCallBack) {
        this.rowClickCallBack = rowClickCallBack;
    }

    public NewLandingCoursesAdapter(List<CommonLandingData> allCources, int fragmentNo) {
        this.allCources = allCources;
        Log.e(TAG, "inside NewLandingCoursesAdapter() this.allCources " + allCources.toString());
        this.fragmentNo = fragmentNo;
        newActivityOpened = false;
        hideArchiveIcon = OustPreferences.getAppInstallVariable("hideArchive");
        for (int i = 0; i< allCources.size(); i++)
        {
            Log.d(TAG, "NewLandingCoursesAdapter: "+allCources.get(i).getName());
        }
    }

    public void notifyDataChanges(List<CommonLandingData> allCources) {
        this.allCources = allCources;
        newActivityOpened = false;
        hideArchiveIcon = OustPreferences.getAppInstallVariable("hideArchive");
        notifyDataSetChanged();
    }

    public void enableBtn() {
        newActivityOpened = false;
    }

    @Override
    public int getItemCount() {
        if (totalModules != 0) {
            if (allCources.size() < totalModules - 1) {
                return allCources.size();
            } else
                return allCources.size();
        } else
            return allCources.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView coureseimage, course_complete_icon, archive_courseicon, certificate_icon, totalenroll_imageview,
                totaltime_imageview, coinicon_imageview;
        TextView coursenametext, totaltime_text, totalenroll_text, totalcoin_text, duedate_text,loading_text;
        LinearLayout rowcoin_layout, timer_layout;
        CardView cource_mainrow;
        FrameLayout coursemain_framelayout;
        RelativeLayout locklayout, ll_progress;

        MyViewHolder(View view) {
            super(view);
            cource_mainrow = view.findViewById(R.id.cource_mainrow);
            coureseimage = view.findViewById(R.id.coureseimage);
            coursenametext = view.findViewById(R.id.coursenametext);
            totalenroll_text = view.findViewById(R.id.totalenroll_text);
            totalcoin_text = view.findViewById(R.id.totalcoin_text);
            totaltime_text = view.findViewById(R.id.totaltime_text);
            rowcoin_layout = view.findViewById(R.id.rowcoin_layout);
            timer_layout = view.findViewById(R.id.timer_layout);
            course_complete_icon = view.findViewById(R.id.course_complete_icon);
            coursemain_framelayout = view.findViewById(R.id.coursemain_framelayout);
            locklayout = view.findViewById(R.id.locklayout);
            archive_courseicon = view.findViewById(R.id.archive_courseicon);
            certificate_icon = view.findViewById(R.id.certificate_icon);
            duedate_text = view.findViewById(R.id.duedate_text);
            coinicon_imageview = view.findViewById(R.id.coinicon_imageview);
            totaltime_imageview = view.findViewById(R.id.totaltime_imageview);
            totalenroll_imageview = view.findViewById(R.id.totalenroll_imageview);
            ll_progress = view.findViewById(R.id.ll_progress);
            loading_text= view.findViewById(R.id.loading_text);
            coursenametext.setSelected(true);
            try {
                DisplayMetrics metrics = OustSdkApplication.getContext().getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                int size = (int) OustSdkApplication.getContext().getResources().getDimension(R.dimen.oustlayout_dimen15);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) coureseimage.getLayoutParams();
                int imageWidth = (scrWidth);
                if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 1) {
                    imageWidth = (scrWidth / 2);
                    size = (int) OustSdkApplication.getContext().getResources().getDimension(R.dimen.oustlayout_dimen50);
                }
                imageWidth = (imageWidth - size);
                float h = (imageWidth * 0.3f);
                params.height = (int) h;
                coureseimage.setLayoutParams(params);
            } catch (Exception e) {
                //OustSdkTools.showToast(e.getMessage());
            }
            OustSdkTools.setImage(totalenroll_imageview, OustSdkApplication.getContext().getResources().getString(R.string.new_reviewimg));
            OustSdkTools.setImage(totalenroll_imageview, OustSdkApplication.getContext().getResources().getString(R.string.groups_tab_orange));
            OustSdkTools.setImage(totaltime_imageview, OustSdkApplication.getContext().getResources().getString(R.string.mydesk_time));

            try {
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    coinicon_imageview.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
                }else{
                    OustSdkTools.setImage(coinicon_imageview, OustSdkApplication.getContext().getResources().getString(R.string.coins_icon));
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            OustSdkTools.setImage(certificate_icon, OustSdkApplication.getContext().getResources().getString(R.string.certificate_icona));
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.newcourse_row, parent, false);
        if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 1) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.newcourse_leyoutrow, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            if(holder.loading_text!=null)
                holder.loading_text.setText(OustStrings.getString("loading"));
            if (position != allCources.size() && allCources.get(position) != null) {
                if(holder.ll_progress!=null)
                    holder.ll_progress.setVisibility(View.GONE);
                OustSdkTools.setImage(holder.coureseimage, OustSdkApplication.getContext().getString(R.string.mydesk));
                OustSdkTools.setImage(holder.certificate_icon, OustSdkApplication.getContext().getString(R.string.certificate_icona));
                if (holder.certificate_icon != null)
                    holder.certificate_icon.setVisibility(View.GONE);
                if (holder.archive_courseicon != null) {
                    if (hideArchiveIcon) {
                        holder.archive_courseicon.setVisibility(View.GONE);
                    } else {
                        holder.archive_courseicon.setVisibility(View.VISIBLE);
                    }
                }

                if (fragmentNo == 0) {
                    if (allCources.get(position).getName() != null) {
                        holder.coursenametext.setText(allCources.get(position).getName());
                    } else {
                        holder.coursenametext.setText("");
                    }
                    if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 1) {
                        if ((allCources.get(position).getCourseDeadline() != null) && (!allCources.get(position).getCourseDeadline().isEmpty())) {
                            if (holder.duedate_text != null)
                                holder.duedate_text.setVisibility(View.VISIBLE);
                            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-mm-dd");
                            Date parsedDate = oldFormat.parse(allCources.get(position).getCourseDeadline());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                            holder.duedate_text.setText(OustStrings.getString("due_date") + (dateFormat.format(parsedDate)));
                        } else {
                            if (holder.duedate_text != null)
                                holder.duedate_text.setVisibility(View.GONE);
                        }
                    } else {

                    }

                    if ((allCources.get(position).getBanner() != null) && (!allCources.get(position).getBanner().isEmpty())) {
                        if (OustSdkTools.checkInternetStatus()) {
                            Picasso.get().load(allCources.get(position).getBanner())
                                    .placeholder(R.drawable.roundedcornergraysolid)
                                    .error(R.drawable.roundedcornergraysolid)
                                    .into(holder.coureseimage);
                        } else {
                            Picasso.get().load(allCources.get(position).getBanner())
                                    .placeholder(R.drawable.roundedcornergraysolid)
                                    .error(R.drawable.roundedcornergraysolid)
                                    .networkPolicy(NetworkPolicy.OFFLINE).into(holder.coureseimage);
                        }
                    }// else {
//                        holder.coureseimage.setImageDrawable(null);
//                    }
                    holder.totalenroll_text.setText("" + allCources.get(position).getEnrollCount());
                    holder.totalcoin_text.setText("" + allCources.get(position).getUserOc());
                    if (allCources.get(position).getTime() > 0) {
                        long n1 = allCources.get(position).getTime() % 60;
                        if (n1 < 10) {
                            holder.totaltime_text.setText("" + (allCources.get(position).getTime() / 60) + ":0" + n1);
                        } else {
                            holder.totaltime_text.setText("" + (allCources.get(position).getTime() / 60) + ":" + n1);
                        }
                    } else {
                        holder.totaltime_text.setText("0:00");
                    }
                    if (allCources.get(position).getType().equals("COLLECTION")) {
                        if (holder.archive_courseicon != null)
                            holder.archive_courseicon.setVisibility(View.GONE);
                        if (holder.rowcoin_layout != null)
                            holder.rowcoin_layout.setVisibility(View.GONE);
                        if (allCources.get(position).isEnrolled()) {
                            if (allCources.get(position).getCompletionPercentage() > 99) {
                                OustSdkTools.setImage(holder.course_complete_icon, OustSdkApplication.getContext().getResources().getString(R.string.new_reviewimg));
                            } else {
                                OustSdkTools.setImage(holder.course_complete_icon, OustSdkApplication.getContext().getResources().getString(R.string.new_resumeimg));
                            }
                        } else {
                            OustSdkTools.setImage(holder.course_complete_icon, OustSdkApplication.getContext().getResources().getString(R.string.new_ntstartedimg));
                        }
                    } else {
                        if (holder.rowcoin_layout != null)
                            holder.rowcoin_layout.setVisibility(View.VISIBLE);
                        if (holder.timer_layout != null)
                            holder.timer_layout.setVisibility(View.VISIBLE);
                        if (allCources.get(position).getCompletionPercentage() > 99) {
                            OustSdkTools.setImage(holder.course_complete_icon, OustSdkApplication.getContext().getResources().getString(R.string.new_reviewimg));
                            if (allCources.get(position).isCertificate()) {
//                            if(allCources.get(position).getMappedAssessmentId()>0){
//                                if((allCources.get(position).getAssessmentCompletionDate()!=null)&&(!allCources.get(position).getAssessmentCompletionDate().isEmpty())){
//                                    holder.certificate_icon.setVisibility(View.VISIBLE);
//                                }
//                            }else {
//                                holder.certificate_icon.setVisibility(View.VISIBLE);
//                            }
                            }
                        } else {
                            if ((!allCources.get(position).isEnrolled()) && (allCources.get(position).getCompletionPercentage() == 0)) {
                                OustSdkTools.setImage(holder.course_complete_icon, OustSdkApplication.getContext().getResources().getString(R.string.new_ntstartedimg));
                            } else {
                                OustSdkTools.setImage(holder.course_complete_icon, OustSdkApplication.getContext().getResources().getString(R.string.new_resumeimg));
                            }
                        }
                        if (allCources.get(position).isLocked()) {
                            if (holder.locklayout != null) {
                                holder.locklayout.setVisibility(View.VISIBLE);
                                holder.locklayout.bringToFront();
                            }
                            final int color = OustSdkTools.getColorBack(R.color.MoreLiteGrayd);
                            final Drawable drawable = new ColorDrawable(color);
                            holder.coursemain_framelayout.setForeground(drawable);
                        } else {
                            if (holder.locklayout != null)
                                holder.locklayout.setVisibility(View.GONE);
                            final int color = OustSdkTools.getColorBack(R.color.fulltransparent);
                            final Drawable drawable = new ColorDrawable(color);
                            holder.coursemain_framelayout.setForeground(drawable);
                        }
                    }
                } else if (fragmentNo == 1) {
                    holder.rowcoin_layout.setVisibility(View.GONE);
                    if (holder.timer_layout != null)
                        holder.timer_layout.setVisibility(View.GONE);
                    if ((allCources.get(position).getBanner() != null) && (!allCources.get(position).getBanner().isEmpty())) {
                        if (OustSdkTools.checkInternetStatus()) {
                            Picasso.get().load(allCources.get(position).getBanner())
                                    .placeholder(R.drawable.roundedcornergraysolid).error(R.drawable.roundedcornergraysolid)
                                    .into(holder.coureseimage);
                        } else {
                            Picasso.get().load(allCources.get(position).getBanner())
                                    .placeholder(R.drawable.roundedcornergraysolid).error(R.drawable.roundedcornergraysolid)
                                    .networkPolicy(NetworkPolicy.OFFLINE).into(holder.coureseimage);
                        }
                    }// else {
//                        holder.coureseimage.setImageDrawable(null);
//                    }
                    if (allCources.get(position).getName() != null) {
                        holder.coursenametext.setText(allCources.get(position).getName() + "\n ");
                    } else {
                        holder.coursenametext.setText("");
                    }
                    if ((allCources.get(position).getCompletionPercentage() != 0)) {
                        OustSdkTools.setImage(holder.course_complete_icon, OustSdkApplication.getContext().getResources().getString(R.string.new_reviewimg));
                        if (allCources.get(position).isCertificate()) {
                            if (holder.certificate_icon != null)
                                holder.certificate_icon.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (allCources.get(position).isEnrolled()) {
                            OustSdkTools.setImage(holder.course_complete_icon, OustSdkApplication.getContext().getResources().getString(R.string.new_resumeimg));
                        } else {
                            OustSdkTools.setImage(holder.course_complete_icon, OustSdkApplication.getContext().getResources().getString(R.string.new_ntstartedimg));
                        }
                    }
                    holder.totalenroll_text.setText("" + allCources.get(position).getEnrollCount());
                    if (allCources.get(position).getTime() > 0) {
                        long n1 = allCources.get(position).getTime() % 60;
                        if (n1 < 10) {
                            holder.totaltime_text.setText("" + (allCources.get(position).getTime() / 60) + ":0" + n1);
                        } else {
                            holder.totaltime_text.setText("" + (allCources.get(position).getTime() / 60) + ":" + n1);
                        }
                    } else {
                        holder.totaltime_text.setText("0:00");
                    }
                    if (allCources.get(position).isLocked()) {
                        holder.locklayout.setVisibility(View.VISIBLE);
                        holder.locklayout.bringToFront();
                        final int color = OustSdkTools.getColorBack(R.color.MoreLiteGrayd);
                        final Drawable drawable = new ColorDrawable(color);
                        holder.coursemain_framelayout.setForeground(drawable);
                    } else {
                        holder.locklayout.setVisibility(View.GONE);
                        final int color = OustSdkTools.getColorBack(R.color.fulltransparent);
                        final Drawable drawable = new ColorDrawable(color);
                        holder.coursemain_framelayout.setForeground(drawable);
                    }
                }
            } else {
                if(holder.ll_progress!=null)
                    holder.ll_progress.setVisibility(View.VISIBLE);
            }
            if (holder.archive_courseicon != null) {
                holder.archive_courseicon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.9f);
                        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.9f);
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
                                clickOnHideIcon(position);
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
            }
            holder.cource_mainrow.setOnClickListener(new View.OnClickListener() {
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
                            clickonRow(position);
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
            if (holder.certificate_icon != null) {
                holder.certificate_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (fragmentNo == 0) {
                            sendCertificateCallBack.sendCertificate(("" + allCources.get(position).getId()), allCources.get(position).getType().equals("COLLECTION"));
                        } else {
                            sendCertificateCallBack.sendCertificate(("" + allCources.get(position).getId()), false);
                        }
                    }
                });
            }
            if ((position == allCources.size() - 1)) {
                pageLoader.onPageLoad();
                dataLoaderNotifier.getNextData(allCources.get(position).getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void clickOnHideIcon(final int position) {
        try {
            if (fragmentNo == 0) {
                rowClickCallBack.onMainRowClick("" + allCources.get(position).getId(), position);
            } else {
                rowClickCallBack.onMainRowClick("" + allCources.get(position).getId(), position);
            }
        } catch (Exception e) {
        }
    }


    public void clickonRow(int position) {
        try {
            String id = allCources.get(position).getId();
            if (fragmentNo == 0) {
                if (!newActivityOpened) {
                    if (allCources.get(position).getType().equals("COLLECTION")) {
                        newActivityOpened = true;
                        id = id.replace("COLLECTION", "");
                        Intent intent = new Intent(OustSdkApplication.getContext(), LessonsActivity.class);
                        intent.putExtra("collectionId", ("" + id));
                        intent.putExtra("banner", allCources.get(position).getBanner());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        OustSdkApplication.getContext().startActivity(intent);
                    } else {
                        if (allCources.get(position).getCourseType()!=null &&
                                allCources.get(position).getCourseType().equalsIgnoreCase("Multilingual")) {
                            Intent intent = new Intent(OustSdkApplication.getContext(), CourseMultiLingualActivity.class);
                            id = id.replace("COURSE", "");
                            intent.putExtra("learningId", id);
                            List<MultilingualCourse> multilingualCourseList=new ArrayList<>();
                            multilingualCourseList=allCources.get(position).getMultilingualCourseListList();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("multilingualDataList", (Serializable) multilingualCourseList);
                            intent.putExtras(bundle);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            OustSdkApplication.getContext().startActivity(intent);
                        } else {
                            if (!allCources.get(position).isLocked()) {
                                newActivityOpened = true;
                                id = id.replace("COURSE", "");
                                Intent intent = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
                                intent.putExtra("learningId", ("" + id));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                OustSdkApplication.getContext().startActivity(intent);
                            }
                        }
                    }
                }
            } else if (fragmentNo == 1) {
                if (!newActivityOpened) {
                    CommonLandingData commonLandingData = allCources.get(position);
                    if (!allCources.get(position).isLocked()) {
                        newActivityOpened = true;
                        if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("ASSESSMENT"))) {
                            Gson gson = new Gson();
                            Intent intent;
                            if(OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)){
                                intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                            }else{
                                intent = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
                            }
                            //Intent intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                            ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
                            intent.putExtra("ActiveGame", gson.toJson(activeGame));
                            String assessment_id = commonLandingData.getId();
                            assessment_id = assessment_id.replace("ASSESSMENT", "");
                            intent.putExtra("assessmentId", assessment_id);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            OustSdkApplication.getContext().startActivity(intent);
                        } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("SURVEY"))) {
                            Gson gson = new Gson();
                            Intent intent = new Intent(OustSdkApplication.getContext(), SurveyDetailActivity.class);
                            if(OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI)){
                                intent = new Intent(OustSdkApplication.getContext(), SurveyComponentActivity.class);
                            }
                            ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
                            intent.putExtra("ActiveGame", gson.toJson(activeGame));
                            String assessment_id = commonLandingData.getId();
                            assessment_id = assessment_id.replace("ASSESSMENT", "");
                            intent.putExtra("assessmentId", assessment_id);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            OustSdkApplication.getContext().startActivity(intent);
                        }
                    }
                }
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
}
