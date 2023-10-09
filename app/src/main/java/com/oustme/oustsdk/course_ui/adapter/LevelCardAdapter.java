package com.oustme.oustsdk.course_ui.adapter;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.course.SearchCourseCard;
import com.oustme.oustsdk.interfaces.course.ReviewModeCallBack;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.util.WebViewClass;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.io.File;
import java.util.List;

public class LevelCardAdapter extends RecyclerView.Adapter<LevelCardAdapter.ViewHolder> {

    Context context;
    List<SearchCourseCard> searchCourseCardList;
    int levelPosition;
    List<DTOUserCardData> dtoUserCardDataList;
    boolean isLevelLock;
    boolean isSalesMode;
    boolean isCourseCompleted;
    List<DTOUserCardData> userCardDataList;
    ReviewModeCallBack reviewModeCallBack;

    public void setAdapter(Context context, List<SearchCourseCard> searchCourseCardList, int levelPosition,
                           List<DTOUserCardData> dtoUserCardDataList, boolean isLevelLock, boolean isSalesMode,
                           boolean isCourseCompleted, List<DTOUserCardData> userCardDataList) {
        this.context = context;
        this.searchCourseCardList = searchCourseCardList;
        this.levelPosition = levelPosition;
        this.dtoUserCardDataList = dtoUserCardDataList;
        this.isLevelLock = isLevelLock;
        this.isSalesMode = isSalesMode;
        this.isCourseCompleted = isCourseCompleted;
        this.userCardDataList = userCardDataList;
    }

    public void setReviewModeCallBack(ReviewModeCallBack reviewModeCallBack) {
        this.reviewModeCallBack = reviewModeCallBack;
    }

    public void notifyData(Context context, List<SearchCourseCard> searchCourseCardList, int levelPosition,
                           List<DTOUserCardData> dtoUserCardDataList, boolean isLevelLock, boolean isSalesMode,
                           boolean isCourseCompleted, List<DTOUserCardData> userCardDataList) {
        this.context = context;
        if (searchCourseCardList != null) {
            this.searchCourseCardList.clear();
            this.searchCourseCardList.addAll(searchCourseCardList);
        }
        this.levelPosition = levelPosition;
        this.dtoUserCardDataList = dtoUserCardDataList;
        this.isLevelLock = isLevelLock;
        this.isSalesMode = isSalesMode;
        this.isCourseCompleted = isCourseCompleted;
        this.userCardDataList = userCardDataList;
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView level_card_root;
        ImageView card_thumb;
        ImageView video_play;
        TextView card_name;
        ImageView iv_card_status;
        LinearLayout katex_layout;
        KatexView card_name_katex;
        LinearLayout math_web_view_layout;
        WebViewClass math_level_option_name;

        ViewHolder(View view) {
            super(view);
            level_card_root = view.findViewById(R.id.level_card_root);
            card_thumb = view.findViewById(R.id.card_thumb);
            video_play = view.findViewById(R.id.video_play);
            card_name = view.findViewById(R.id.card_name);
            iv_card_status = view.findViewById(R.id.iv_card_status);
            katex_layout = view.findViewById(R.id.katex_layout);
            card_name_katex = view.findViewById(R.id.card_name_katex);
            math_level_option_name = view.findViewById(R.id.math_level_option_name);
            math_web_view_layout = view.findViewById(R.id.math_web_view_layout);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_level_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            DTOCourseCard dtoCourseCard = OustSdkTools.databaseHandler.getCardClass(((int) (searchCourseCardList.get(position).getId())));
            Log.d("RegularCard", "onBindViewHolder: position:" + position);
            if (dtoCourseCard != null) {
                boolean isCardCompleted, isPreviousCardCompleted = false;
                boolean isOpenableCard;

                isCardCompleted = getDBCardCompletedData(dtoCourseCard);
                if (!isCardCompleted && userCardDataList != null) {
                    isCardCompleted = getCardCompletedData(dtoCourseCard);
                }

                if (position > 0) {
                    DTOCourseCard previousCourseCardClass = OustSdkTools.databaseHandler.getCardClass(((int) (searchCourseCardList.get(position - 1).getId())));
                    Log.d("RegularCard", "onBindViewHolder: Previous Card");
                    isPreviousCardCompleted = getDBCardCompletedData(previousCourseCardClass);
                    if (!isPreviousCardCompleted && userCardDataList != null) {
                        isPreviousCardCompleted = getCardCompletedData(previousCourseCardClass);
                    }
                }

                Log.d("RegularCard", "levelLock : " + isLevelLock + " --- courseCompleted:" + isCourseCompleted + " ---salesMode:" + isSalesMode + " --- card:" + isCardCompleted);
                if (!isSalesMode && (isLevelLock || !isCardCompleted) && !isCourseCompleted) {
                    if ((levelPosition == 0 && position == 0) || (!isLevelLock && position == 0) ||
                            (isLevelLock && isCardCompleted) || (position > 0 && isPreviousCardCompleted)) {
                        isOpenableCard = true;
                        if (isCardCompleted) {
                            holder.iv_card_status.setVisibility(View.VISIBLE);
                            holder.iv_card_status.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(context.getResources().getDrawable(R.drawable.ic_tick_done),
                                    context.getResources().getColor(R.color.progress_correct)));
                        } else {
                            holder.iv_card_status.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(context.getResources().getDrawable(R.drawable.ic_lock_img),
                                    context.getResources().getColor(R.color.unselected_text)));
                            holder.iv_card_status.setVisibility(View.GONE);
                        }
                    } else {
                        isOpenableCard = false;
                        holder.iv_card_status.setVisibility(View.VISIBLE);
                        holder.iv_card_status.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(context.getResources().getDrawable(R.drawable.ic_lock_img),
                                context.getResources().getColor(R.color.unselected_text)));
                    }

                } else {
                    isOpenableCard = true;
                    if (!isSalesMode) {
                        holder.iv_card_status.setVisibility(View.VISIBLE);
                        holder.iv_card_status.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(context.getResources().getDrawable(R.drawable.ic_tick_done),
                                context.getResources().getColor(R.color.progress_correct)));
                    } else {
                        if (!isCardCompleted && !isCourseCompleted) {
                            holder.iv_card_status.setVisibility(View.GONE);
                            holder.iv_card_status.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(context.getResources().getDrawable(R.drawable.ic_lock_img),
                                    context.getResources().getColor(R.color.unselected_text)));
                        } else {
                            holder.iv_card_status.setVisibility(View.VISIBLE);
                            holder.iv_card_status.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(context.getResources().getDrawable(R.drawable.ic_tick_done),
                                    context.getResources().getColor(R.color.progress_correct)));
                        }
                    }
                }

                final boolean isOpenable = isOpenableCard;

                if (dtoCourseCard.getCardType() != null) {
                    Log.d("RegularCard", "" + dtoCourseCard.getCardType());
                    if ((dtoCourseCard.getCardType().equalsIgnoreCase("LEARNING"))) {
                        holder.card_thumb.setImageDrawable(context.getResources().getDrawable(R.drawable.default_learning_card));
                        if (dtoCourseCard.getCardTitle() != null) {
                            if (dtoCourseCard.getCardTitle().contains(KATEX_DELIMITER)) {
                                holder.card_name_katex.setTextColor(context.getResources().getColor(R.color.primary_text));
                                holder.card_name_katex.setTextColorString("#212121");
                                holder.card_name_katex.setTextDirection(View.TEXT_ALIGNMENT_CENTER);
                                holder.card_name_katex.setText(dtoCourseCard.getCardTitle());
                                holder.katex_layout.setVisibility(View.VISIBLE);
                                holder.card_name.setVisibility(View.GONE);
                                holder.math_web_view_layout.setVisibility(View.GONE);
                            } else if (dtoCourseCard.getCardTitle().contains("<math")) {
                                holder.math_web_view_layout.setVisibility(View.VISIBLE);
                                holder.katex_layout.setVisibility(View.GONE);
                                holder.card_name.setVisibility(View.GONE);
                                OustSdkTools.getSpannedMathmlContent(dtoCourseCard.getCardTitle(), holder.math_level_option_name, false);
                            } else {
                                OustSdkTools.getSpannedContent(dtoCourseCard.getCardTitle(), holder.card_name);
                                holder.katex_layout.setVisibility(View.GONE);
                                holder.card_name.setVisibility(View.VISIBLE);
                                holder.math_web_view_layout.setVisibility(View.GONE);
                            }
                        }
                        if (dtoCourseCard.getCardMedia() != null) {
                            for (int i = 0; i < dtoCourseCard.getCardMedia().size(); i++) {
                                DTOCourseCardMedia courseCardMedia = dtoCourseCard.getCardMedia().get(i);
                                if ((courseCardMedia != null) && (courseCardMedia.getMediaType() != null)) {
                                    String mediaType = courseCardMedia.getMediaType();
                                    if ((mediaType.equalsIgnoreCase("GIF")) || (mediaType.equalsIgnoreCase("IMAGE"))) {
                                        File file = new File(OustSdkApplication.getContext().getFilesDir(), "oustlearn_" + courseCardMedia.getData());
                                        if (file.exists()) {
                                            Uri uri = Uri.fromFile(file);
                                            Glide.with(context).load(uri).error(R.drawable.default_learning_card).into(holder.card_thumb);
                                        }
                                    } else if (mediaType.equalsIgnoreCase("VIDEO")) {
                                        holder.video_play.setVisibility(View.VISIBLE);
                                        if ((courseCardMedia.getMediaThumbnail() != null) && (!courseCardMedia.getMediaThumbnail().isEmpty())) {
                                            Glide.with(context).load(courseCardMedia.getMediaThumbnail()).error(R.drawable.default_learning_card).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.card_thumb);
                                        }
                                    } else if (mediaType.equalsIgnoreCase("YOUTUBE_VIDEO")) {
                                        holder.video_play.setVisibility(View.VISIBLE);
                                        String youtubeKey = courseCardMedia.getData();
                                        if (youtubeKey.contains("https://www.youtube.com/watch?v=")) {
                                            youtubeKey = youtubeKey.replace("https://www.youtube.com/watch?v=", "");
                                        }
                                        if (youtubeKey.contains("https://youtu.be/")) {
                                            youtubeKey = youtubeKey.replace("https://youtu.be/", "");
                                        }
                                        if (youtubeKey.contains("&")) {
                                            int pos = youtubeKey.indexOf("&");
                                            youtubeKey = youtubeKey.substring(0, pos);
                                        }
                                        String imagePath = "http://img.youtube.com/vi/" + youtubeKey + "/default.jpg";

                                        Glide.with(context).load(imagePath).error(R.drawable.default_learning_card).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.card_thumb);

                                    }
                                }
                            }
                        }
                    } else if (dtoCourseCard.getCardType().equalsIgnoreCase("QUESTION")) {
                        holder.card_thumb.setImageDrawable(context.getResources().getDrawable(R.drawable.default_course_card));
                        if (dtoCourseCard.getQuestionData() != null &&
                                dtoCourseCard.getQuestionData().getQuestion() != null && !dtoCourseCard.getQuestionData().getQuestion().isEmpty()) {
                            if (dtoCourseCard.getQuestionData().getQuestion().contains(KATEX_DELIMITER)) {
                                holder.card_name_katex.setTextColor(context.getResources().getColor(R.color.primary_text));
                                holder.card_name_katex.setTextColorString("#212121");
                                holder.card_name_katex.setTextDirection(View.TEXT_ALIGNMENT_CENTER);
                                holder.card_name_katex.setText(dtoCourseCard.getQuestionData().getQuestion());
                                holder.katex_layout.setVisibility(View.VISIBLE);
                                holder.card_name.setVisibility(View.GONE);
                                holder.math_web_view_layout.setVisibility(View.GONE);
                            } else if (dtoCourseCard.getQuestionData().getQuestion().contains("<math")) {
                                holder.math_web_view_layout.setVisibility(View.VISIBLE);
                                holder.katex_layout.setVisibility(View.GONE);
                                holder.card_name.setVisibility(View.GONE);
                                OustSdkTools.getSpannedMathmlContent(dtoCourseCard.getQuestionData().getQuestion(), holder.math_level_option_name, false);
                            } else {
                                OustSdkTools.getSpannedContent(dtoCourseCard.getQuestionData().getQuestion(), holder.card_name);
                                holder.katex_layout.setVisibility(View.GONE);
                                holder.math_web_view_layout.setVisibility(View.GONE);
                                holder.card_name.setVisibility(View.VISIBLE);
                            }
                        } else if (dtoCourseCard.getQuestionCategory() != null &&
                                dtoCourseCard.getQuestionCategory().equalsIgnoreCase(QuestionCategory.HOTSPOT)) {
                            holder.card_name.setText("Hotspot Question");
                        } else {
                            holder.card_name.setText("Question : " + (position + 1));
                        }
                        if (dtoCourseCard.getQuestionData() != null && dtoCourseCard.getQuestionData().getImage() != null) {
                            try {
                                String str = dtoCourseCard.getQuestionData().getImage();
                                if ((str != null) && (!str.isEmpty())) {
                                    Glide.with(context).load(str).diskCacheStrategy(DiskCacheStrategy.DATA).error(context.getResources().getDrawable(R.drawable.default_learning_card)).into(holder.card_thumb);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }

                    } else if (dtoCourseCard.getCardType().equalsIgnoreCase("SCORM")) {
                        holder.card_thumb.setImageDrawable(context.getResources().getDrawable(R.drawable.default_learning_card));
                        if (dtoCourseCard.getCardTitle() != null) {
                            if (dtoCourseCard.getCardTitle().contains(KATEX_DELIMITER)) {
                                holder.card_name_katex.setTextColor(context.getResources().getColor(R.color.primary_text));
                                holder.card_name_katex.setTextColorString("#212121");
                                holder.card_name_katex.setTextDirection(View.TEXT_ALIGNMENT_CENTER);
                                holder.card_name_katex.setText(dtoCourseCard.getCardTitle());
                                holder.katex_layout.setVisibility(View.VISIBLE);
                                holder.card_name.setVisibility(View.GONE);
                                holder.math_web_view_layout.setVisibility(View.GONE);
                            } else if (dtoCourseCard.getCardTitle().contains("<math")) {
                                holder.math_web_view_layout.setVisibility(View.VISIBLE);
                                holder.katex_layout.setVisibility(View.GONE);
                                holder.card_name.setVisibility(View.GONE);
                                OustSdkTools.getSpannedMathmlContent(dtoCourseCard.getCardTitle(), holder.math_level_option_name, false);
                            } else {
                                OustSdkTools.getSpannedContent(dtoCourseCard.getCardTitle(), holder.card_name);
                                holder.katex_layout.setVisibility(View.GONE);
                                holder.math_web_view_layout.setVisibility(View.GONE);
                                holder.card_name.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    SearchCourseCard searchCourseCard = searchCourseCardList.get(position);
                    if ((searchCourseCard.getCardType() != null)) {
                        Log.d("RegularCard", "" + searchCourseCard.getCardType());
                        if ((searchCourseCard.getCardType().equalsIgnoreCase("LEARNING")) ||
                                (searchCourseCard.getCardType().equalsIgnoreCase("SCORM"))) {
                            if (searchCourseCard.getName() != null) {
                                if (searchCourseCard.getName().contains(KATEX_DELIMITER)) {
                                    holder.card_name_katex.setTextColor(context.getResources().getColor(R.color.primary_text));
                                    holder.card_name_katex.setTextColorString("#212121");
                                    holder.card_name_katex.setTextDirection(View.TEXT_ALIGNMENT_CENTER);
                                    holder.card_name_katex.setText(searchCourseCard.getName());
                                    holder.katex_layout.setVisibility(View.VISIBLE);
                                    holder.card_name.setVisibility(View.GONE);
                                    holder.math_web_view_layout.setVisibility(View.GONE);
                                } else if (searchCourseCard.getName().contains("<math")) {
                                    holder.math_web_view_layout.setVisibility(View.VISIBLE);
                                    holder.katex_layout.setVisibility(View.GONE);
                                    holder.card_name.setVisibility(View.GONE);
                                    OustSdkTools.getSpannedMathmlContent(searchCourseCard.getName(), holder.math_level_option_name, false);
                                } else {
                                    OustSdkTools.getSpannedContent(searchCourseCard.getName(), holder.card_name);
                                    holder.katex_layout.setVisibility(View.GONE);
                                    holder.math_web_view_layout.setVisibility(View.GONE);
                                    holder.card_name.setVisibility(View.VISIBLE);
                                }
                            }
                            OustSdkTools.setImage(holder.card_thumb, OustSdkApplication.getContext().getResources().getString(R.string.information));
                        } else if (searchCourseCard.getCardType().equalsIgnoreCase("QUESTION")) {
                            OustSdkTools.setImage(holder.card_thumb, OustSdkApplication.getContext().getResources().getString(R.string.question_default));
                            if (searchCourseCard.getName() != null && !searchCourseCard.getName().isEmpty()) {
                                if (searchCourseCard.getName().contains(KATEX_DELIMITER)) {
                                    holder.card_name_katex.setTextColor(context.getResources().getColor(R.color.primary_text));
                                    holder.card_name_katex.setTextColorString("#212121");
                                    holder.card_name_katex.setTextDirection(View.TEXT_ALIGNMENT_CENTER);
                                    holder.card_name_katex.setText(searchCourseCard.getName());
                                    holder.katex_layout.setVisibility(View.VISIBLE);
                                    holder.card_name.setVisibility(View.GONE);
                                    holder.math_web_view_layout.setVisibility(View.GONE);
                                } else if (searchCourseCard.getName().contains("<math")) {
                                    holder.math_web_view_layout.setVisibility(View.VISIBLE);
                                    holder.katex_layout.setVisibility(View.GONE);
                                    holder.card_name.setVisibility(View.GONE);
                                    OustSdkTools.getSpannedMathmlContent(searchCourseCard.getName(), holder.math_level_option_name, false);
                                } else {
                                    OustSdkTools.getSpannedContent(searchCourseCard.getName(), holder.card_name);
                                    holder.katex_layout.setVisibility(View.GONE);
                                    holder.card_name.setVisibility(View.VISIBLE);
                                    holder.math_web_view_layout.setVisibility(View.GONE);
                                }
                            } else if (searchCourseCard.getDescription() != null && !searchCourseCard.getDescription().isEmpty()) {
                                if (searchCourseCard.getDescription().contains(KATEX_DELIMITER)) {
                                    holder.card_name_katex.setTextColor(context.getResources().getColor(R.color.primary_text));
                                    holder.card_name_katex.setTextColorString("#212121");
                                    holder.card_name_katex.setTextDirection(View.TEXT_ALIGNMENT_CENTER);
                                    holder.card_name_katex.setText(searchCourseCard.getDescription());
                                    holder.katex_layout.setVisibility(View.VISIBLE);
                                    holder.card_name.setVisibility(View.GONE);
                                    holder.math_web_view_layout.setVisibility(View.GONE);
                                } else if (searchCourseCard.getDescription().contains("<math")) {
                                    holder.math_web_view_layout.setVisibility(View.VISIBLE);
                                    holder.katex_layout.setVisibility(View.GONE);
                                    holder.card_name.setVisibility(View.GONE);
                                    OustSdkTools.getSpannedMathmlContent(searchCourseCard.getDescription(), holder.math_level_option_name, false);
                                } else {
                                    OustSdkTools.getSpannedContent(searchCourseCard.getDescription(), holder.card_name);
                                    holder.katex_layout.setVisibility(View.GONE);
                                    holder.card_name.setVisibility(View.VISIBLE);
                                    holder.math_web_view_layout.setVisibility(View.GONE);
                                }
                            } else {
                                holder.card_name.setText(OustStrings.getString("question_text") + " : " + (position + 1));
                            }
                        }
                    }
                }

                holder.level_card_root.setOnClickListener(view -> openQuestionCards(isOpenable, view, position));

                holder.math_web_view_layout.setOnClickListener(v -> openQuestionCards(isOpenable, v, position));

                holder.card_name_katex.setOnTouchListener(new View.OnTouchListener() {
                    public final static int FINGER_RELEASED = 0;
                    private int fingerState = FINGER_RELEASED;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            if (fingerState == FINGER_RELEASED) {
                                openQuestionCards(isOpenable, v, position);
                            }
                        }
                        return false;
                    }
                });

                holder.card_name_katex.setOnTouchListener((v, event) -> {
                    int action = event.getAction();
                    if (action == MotionEvent.ACTION_MOVE) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    return false;
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openQuestionCards(boolean isOpenable, View v, int position) {
        try {
            if (isOpenable) {
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 0.98f);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 0.99f);
                scaleDownX.setDuration(100);
                scaleDownY.setDuration(100);
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
                        reviewModeCallBack.onCardClick(levelPosition, position, true);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
            } else {
                OustSdkTools.showToast("You need to complete previous card before accessing this one");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        return searchCourseCardList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    //handle adapter logic
    private boolean getDBCardCompletedData(DTOCourseCard courseCard) {
        boolean cardCompleted = false;
        try {
            if (dtoUserCardDataList != null) {
                for (DTOUserCardData userCardData : dtoUserCardDataList) {
                    Log.d("if check", "getDBCardCompletedData -- userdata:" + userCardData.getCardId() + " -- courseData:" + courseCard.getCardId());
                    if (new Long(userCardData.getCardId()).equals(courseCard.getCardId())) {
                        cardCompleted = userCardData.isCardCompleted();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("Card adapter", "" + e.getMessage());
        }
        return (cardCompleted);
    }

    private boolean getCardCompletedData(DTOCourseCard courseCard) {
        boolean cardCompleted = false;
        try {
            if (userCardDataList != null) {
                for (DTOUserCardData userCardData : userCardDataList) {
                    Log.d("if check", "userdata:" + userCardData.getCardId() + " -- courseData:" + courseCard.getCardId());
                    if (new Long(userCardData.getCardId()).equals(courseCard.getCardId())) {
                        cardCompleted = userCardData.isCardCompleted();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("Card adapter", "" + e.getMessage());
        }
        return (cardCompleted);
    }
}
