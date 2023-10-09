package com.oustme.oustsdk.adapter.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.course_ui.CourseDetailScreen;
import com.oustme.oustsdk.response.common.FriendProfileResponceAssessmentRow;
import com.oustme.oustsdk.response.common.FriendProfileResponceRow;
import com.oustme.oustsdk.response.common.GameType;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

public class FriendProfileRowAdapter extends RecyclerView.Adapter<FriendProfileRowAdapter.MyViewHolder> {
    private Activity activity;
    private List<FriendProfileResponceAssessmentRow> assessmentFirebaseClasses = new ArrayList<>();
    private List<FriendProfileResponceRow> allCources = new ArrayList<>();
    private int fragmentNo = 0;
    private int lastPosition = 0;
    private HashMap<String, String> myKeysMap;

    public FriendProfileRowAdapter(Activity activity, List<FriendProfileResponceAssessmentRow> assessmentFirebaseClasses, List<FriendProfileResponceRow> allCources, HashMap<String, String> keysMap, int fragmentNo) {
        this.activity = activity;
        this.assessmentFirebaseClasses = assessmentFirebaseClasses;
        this.allCources = allCources;
        this.fragmentNo = fragmentNo;
        myKeysMap = keysMap;
        lastPosition = 0;
    }


    public void notifyDateChanges(List<FriendProfileResponceAssessmentRow> assessmentFirebaseClasses, List<FriendProfileResponceRow> allCources, HashMap<String, String> keysMap) {
        this.assessmentFirebaseClasses = assessmentFirebaseClasses;
        this.allCources = allCources;
        myKeysMap = keysMap;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (fragmentNo == 0) {
            return allCources.size();
        } else if (fragmentNo == 1) {
            return assessmentFirebaseClasses.size();
        } else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView coureseimage;
        TextView coursenametext, courseCompletteText, completed_text, certificate_sent_text;
        LinearLayout cource_mainrow, complete_ll;
        ImageView completed_img, certificate_img, coins_img;

        MyViewHolder(View view) {
            super(view);
            cource_mainrow = view.findViewById(R.id.cource_mainrow);
            coureseimage = view.findViewById(R.id.coureseimage);
            coursenametext = view.findViewById(R.id.coursenametext);
            courseCompletteText = view.findViewById(R.id.coursecompletetext);
            completed_img = view.findViewById(R.id.completed_img);
            certificate_img = view.findViewById(R.id.certificate_img);
            coins_img = view.findViewById(R.id.coins_img);

            try {
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    coins_img.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
                } else {
                    OustSdkTools.setImage(coins_img, OustSdkApplication.getContext().getResources().getString(R.string.coins_icon));
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            complete_ll = view.findViewById(R.id.complete_ll);
            completed_text = view.findViewById(R.id.completed_text);
            certificate_sent_text = view.findViewById(R.id.certificate_sent_text);
            coursenametext.setSelected(true);
            try {
                DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                int size = (int) activity.getResources().getDimension(R.dimen.oustlayout_dimen15);
                int imageWidth = (scrWidth);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) coureseimage.getLayoutParams();
                float h = ((imageWidth * 0.21f));
//                int h1 = (int) (h-size);
                params.height = (int) h;
                coureseimage.setLayoutParams(params);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendprofile_rowlayout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        try {
            holder.coureseimage.setImageDrawable(null);
            holder.coursenametext.setText("");
            holder.courseCompletteText.setVisibility(View.GONE);
            holder.complete_ll.setVisibility(View.GONE);

            holder.completed_text.setText(OustStrings.getString("completed"));
            holder.certificate_sent_text.setText(OustStrings.getString("certificate_sent"));

            if (fragmentNo == 0) {
                if (allCources.get(position).getType().equals("COURSE")) {
                    if ((allCources.get(position).getUserCompletionTime() != null) && (!allCources.get(position).getUserCompletionTime().isEmpty()) && !(allCources.get(position).getUserCompletionTime().equals("null"))) {
                        holder.courseCompletteText.setVisibility(View.VISIBLE);
                        holder.complete_ll.setVisibility(View.VISIBLE);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                        Date parsedDate = new Date(Long.valueOf(allCources.get(position).getUserCompletionTime()));
                        holder.courseCompletteText.setText("" + (dateFormat.format(parsedDate)));
                    }
                    if (allCources.get(position).getRowName() != null) {
                        holder.coursenametext.setText(allCources.get(position).getRowName() + "\n ");
                    }
                    if ((allCources.get(position).getBgImg() != null) && (!allCources.get(position).getBgImg().isEmpty())) {
                        if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                            Picasso.get().load(allCources.get(position).getBgImg())
                                    .placeholder(R.drawable.roundedcornergraysolid)
                                    .error(R.drawable.roundedcornergraysolid)
                                    .into(holder.coureseimage);
                        } else {
                            Picasso.get().load(allCources.get(position).getBgImg())
                                    .placeholder(R.drawable.roundedcornergraysolid)
                                    .error(R.drawable.roundedcornergraysolid)
                                    .networkPolicy(NetworkPolicy.OFFLINE).into(holder.coureseimage);
                        }
                    } else {
                        OustSdkTools.setImage(holder.coureseimage, OustSdkApplication.getContext().getString(R.string.mydesk));
                    }
                } else {
                    if ((allCources.get(position).getUserCompletionTime() != null) && (!allCources.get(position).getUserCompletionTime().isEmpty())) {
                        holder.courseCompletteText.setVisibility(View.VISIBLE);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                        Date parsedDate = new Date(Long.valueOf(allCources.get(position).getUserCompletionTime()));
                        holder.courseCompletteText.setText("" + (dateFormat.format(parsedDate)));
                    }
                    if ((allCources.get(position).getBanner() != null) && (!allCources.get(position).getBanner().isEmpty()) && (!allCources.get(position).getBanner().equalsIgnoreCase("null"))) {
                        if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                            Picasso.get().load(allCources.get(position).getBanner())
                                    .placeholder(R.drawable.roundedcornergraysolid).error(R.drawable.roundedcornergraysolid)
                                    .into(holder.coureseimage);
                        } else {
                            Picasso.get().load(allCources.get(position).getBanner())
                                    .placeholder(R.drawable.roundedcornergraysolid).error(R.drawable.roundedcornergraysolid)
                                    .networkPolicy(NetworkPolicy.OFFLINE).into(holder.coureseimage);
                        }
                    } else {
                        OustSdkTools.setImage(holder.coureseimage, OustSdkApplication.getContext().getString(R.string.mydesk));
                    }
                    if (allCources.get(position).getRowName() != null) {
                        holder.coursenametext.setText(allCources.get(position).getRowName() + "\n ");
                    }
                }
            } else if (fragmentNo == 1) {
                if ((assessmentFirebaseClasses.get(position).getUserCompletionTime() != null) && (!assessmentFirebaseClasses.get(position).getUserCompletionTime().isEmpty())) {
                    holder.courseCompletteText.setVisibility(View.VISIBLE);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                    Date parsedDate = new Date(Long.valueOf(assessmentFirebaseClasses.get(position).getUserCompletionTime()));
                    holder.courseCompletteText.setText("" + (dateFormat.format(parsedDate)));
                }
                if ((assessmentFirebaseClasses.get(position).getBanner() != null) && (!assessmentFirebaseClasses.get(position).getBanner().isEmpty())) {
                    if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                        Picasso.get().load(assessmentFirebaseClasses.get(position).getBanner())
                                .placeholder(R.drawable.roundedcornergraysolid).error(R.drawable.roundedcornergraysolid)
                                .into(holder.coureseimage);
                    } else {
                        Picasso.get().load(assessmentFirebaseClasses.get(position).getBanner())
                                .placeholder(R.drawable.roundedcornergraysolid).error(R.drawable.roundedcornergraysolid)
                                .networkPolicy(NetworkPolicy.OFFLINE).into(holder.coureseimage);
                    }
                } else {
                    OustSdkTools.setImage(holder.coureseimage, OustSdkApplication.getContext().getString(R.string.mydesk));
                }
                if (assessmentFirebaseClasses.get(position).getName() != null) {
                    holder.coursenametext.setText(assessmentFirebaseClasses.get(position).getName() + "\n ");
                }
            }
            holder.cource_mainrow.setOnClickListener(v -> {
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
            });
            if (lastPosition < position) {
                setAnimation(holder.cource_mainrow);
            } else {
                if (lastPosition == 0 && position == 0) {
                    setAnimation(holder.cource_mainrow);
                }
            }
            lastPosition = position;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setAnimation(View view) {
        try {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1.0f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1.0f);
            scaleDownX.setDuration(500);
            scaleDownY.setDuration(500);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void clickonRow(int position) {
        try {
            if (fragmentNo == 0) {
                String type = allCources.get(position).getType();
                Log.e("TAG", "clickonRow: position--> " + allCources.get(position).getRowId());
                if (type.equals("COURSE")) {
                    if (myKeysMap.containsKey(("" + allCources.get(position).getRowId()))) {
                        Intent intent;
                        if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_NEW_LEARNING_MAP_MODE)) {
                            intent = new Intent(activity, CourseDetailScreen.class);
                        } else {
                            intent = new Intent(activity, NewLearningMapActivity.class);
                        }
                        intent.putExtra("learningId", ("" + allCources.get(position).getRowId()));
                        activity.startActivity(intent);
                    } else {
                        OustSdkTools.showToast(OustStrings.getString("course_not_available_text"));
                    }
                } else if (type.equals("ASSESSMENT")) {
                    if (myKeysMap.containsKey(("" + allCources.get(position).getRowId()))) {
                        Gson gson = new Gson();
                        Intent intent;
                        if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                            intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                        } else {
                            intent = new Intent(activity, AssessmentPlayActivity.class);
                        }

                        ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
                        intent.putExtra("ActiveGame", gson.toJson(activeGame));
                        intent.putExtra("assessmentId", ("" + allCources.get(position).getRowId()));
                        activity.startActivity(intent);
                    } else {
                        OustSdkTools.showToast(OustStrings.getString("assessment_not_available"));
                    }
                }
            } else if (fragmentNo == 1) {
                if (myKeysMap.containsKey(("" + assessmentFirebaseClasses.get(position).getAssessmentId()))) {
                    Gson gson = new Gson();
                    Intent intent;
                    if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                        intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                    } else {
                        intent = new Intent(activity, AssessmentPlayActivity.class);
                    }
                    ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
                    intent.putExtra("ActiveGame", gson.toJson(activeGame));
                    intent.putExtra("assessmentId", ("" + assessmentFirebaseClasses.get(position).getAssessmentId()));
                    activity.startActivity(intent);
                } else {
                    OustSdkTools.showToast(OustStrings.getString("assessment_not_available"));
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
