package com.oustme.oustsdk.question_module.adapter;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.question_module.model.QuestionAnswerModel;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.WebViewClass;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class QuestionAnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<QuestionAnswerModel> questionAnswerModels = new ArrayList<>();
    private final String[] optionTextArray = new String[]{"A", "B", "C", "D", "E"};
    public Context context;
    public String type;
    public String answer;
    Scores scores;
    int row_index = -1;
    boolean isFirst = true;
    boolean isAnswerSelected = false;
    boolean isCourseQuestion = false;
    boolean isReviewMode = false;
    int count = 0;
    QuestionOnItemClickListener questionOnItemClickListener;
    PopupWindow zoomImagePopup;

    private final String imageOnly = "Image";
    private final String textOnly = "Text";
    private final String imageText = "Image_Text";
    boolean loadData = false;

    public void setQuestionAnswerModels(ArrayList<QuestionAnswerModel> questionAnswerModels, Context context, String type, Scores scores) {
        this.questionAnswerModels = questionAnswerModels;
        this.context = context;
        this.scores = scores;
        this.type = type;
        OustPreferences.saveintVar("QuestionAnswerPosition", -1);
        setHasStableIds(true);
    }

    public void setCourseQuestion(boolean isCourseQuestion, boolean isReviewMode) {
        this.isCourseQuestion = isCourseQuestion;
        this.isReviewMode = isReviewMode;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setClickEvent(QuestionOnItemClickListener questionOnItemClickListener) {
        this.questionOnItemClickListener = questionOnItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (imageText.equals(type)) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_image_text, parent, false);
            ImageAndTextViewHolder imageAndTextViewHolder = new ImageAndTextViewHolder(v);
            imageAndTextViewHolder.setIsRecyclable(false);
            return imageAndTextViewHolder;
        }
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.qstn_text_answer_adapter, parent, false);
        ImageOrTextViewHolder imageOrTextViewHolder = new ImageOrTextViewHolder(v);
        imageOrTextViewHolder.setIsRecyclable(false);
        return imageOrTextViewHolder;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder bindHolder, @SuppressLint("RecyclerView") int position) {
        try {
            QuestionAnswerModel questionAnswerModel = questionAnswerModels.get(position);
            if (type != null && type.equalsIgnoreCase(imageText)) {
                ImageAndTextViewHolder holder = (ImageAndTextViewHolder) bindHolder;
                if (questionAnswerModel != null) {
                    if (optionTextArray.length > position) {
                        questionAnswerModel.setOption(optionTextArray[position]);
                    }
                    String type = questionAnswerModel.getOptionType();
                    if (type != null && type.equalsIgnoreCase("Image")) {
                        holder.answer_image_layout.setVisibility(View.VISIBLE);
                        holder.answer_layout.setVisibility(View.GONE);
                        holder.option_image.setText(questionAnswerModel.getOption());
                        if (questionAnswerModel.getImage() != null && !questionAnswerModel.getImage().isEmpty()) {
                            setImageOptionUrl(questionAnswerModel.getImage(), holder.answer_image);
                        }

                        Drawable optionDrawable = context.getResources().getDrawable(R.drawable.answer_option_lay);
                        holder.answer_option_lay.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawable, context.getResources().getColor(R.color.unselected_text)));


                        GradientDrawable cardDrawableDefault = (GradientDrawable) holder.answer_image_layout.getBackground();
                        cardDrawableDefault.setStroke(3, context.getResources().getColor(R.color.feed_border_stroke));
                        holder.answer_image_layout.setElevation(0.0f);

                        holder.answer_image_layout.setOnClickListener(v -> {
                            OustPreferences.saveintVar("QuestionAnswerPosition", position);
                            if (isCourseQuestion) {
                                if (!isReviewMode) {
                                    row_index = position;
                                    count++;
                                    holder.answer_image_layout.setClickable(false);

                                    if (answer != null && answer.equalsIgnoreCase(questionAnswerModel.getAnswerOption())) {
                                        holder.option_image.setTextColor(context.getResources().getColor(R.color.white));
                                        holder.check_image_question.setVisibility(View.VISIBLE);
                                        Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.answer_option_lay);
                                        holder.answer_option_lay.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected, context.getResources().getColor(R.color.progress_correct)));
                                        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_tick_done);
                                        holder.check_image_question.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(drawable, context.getResources().getColor(R.color.progress_correct)));
                                        GradientDrawable cardDrawableSelected = (GradientDrawable) holder.answer_image_layout.getBackground();
                                        cardDrawableSelected.setStroke(3, context.getResources().getColor(R.color.progress_correct));
                                        holder.answer_image_layout.setElevation(5.0f);
                                    } else {
                                        holder.option_image.setTextColor(context.getResources().getColor(R.color.white));
                                        holder.check_image_question.setVisibility(View.VISIBLE);
                                        Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.answer_option_lay);
                                        holder.answer_option_lay.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected, context.getResources().getColor(R.color.error_incorrect)));
                                        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_close_circle);
                                        holder.check_image_question.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(drawable, context.getResources().getColor(R.color.error_incorrect)));
                                        GradientDrawable cardDrawableSelected = (GradientDrawable) holder.answer_image_layout.getBackground();
                                        cardDrawableSelected.setStroke(3, context.getResources().getColor(R.color.error_incorrect));
                                        holder.answer_image_layout.setElevation(5.0f);
                                    }
                                    questionOnItemClickListener.onItemClicked(count);
                                }
                            } else {
                                row_index = position;
                                isFirst = true;
                                isAnswerSelected = true;
                                count = 1;
                                questionOnItemClickListener.onItemClicked(count);
                                notifyDataSetChanged();
                            }
                        });

                        holder.expand_icon.setOnClickListener(v -> gifZoomPopup(holder.answer_image.getDrawable()));

                        if (scores != null && !isAnswerSelected) {
                            if (scores.getAnswer() != null && scores.getAnswer().equals(questionAnswerModel.getAnswerOption())) {
                                OustPreferences.saveintVar("QuestionAnswerPosition", position);
                                row_index = position;
                                isFirst = true;
                                count = 1;
                                questionOnItemClickListener.onItemClicked(count);
                            }
                        }

                        if (isReviewMode) {
                            holder.answer_image_layout.setEnabled(false);
                            if (answer != null && answer.equalsIgnoreCase(questionAnswerModel.getAnswerOption())) {
                                holder.option_image.setTextColor(context.getResources().getColor(R.color.white));
                                holder.check_image_question.setVisibility(View.VISIBLE);
                                Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.answer_option_lay);
                                holder.answer_option_lay.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected, context.getResources().getColor(R.color.progress_correct)));
                                GradientDrawable cardDrawableSelected = (GradientDrawable) holder.answer_image_layout.getBackground();
                                cardDrawableSelected.setStroke(3, context.getResources().getColor(R.color.progress_correct));
                                holder.answer_image_layout.setElevation(5.0f);
                            } else if (row_index == position) {
                                holder.option_image.setTextColor(context.getResources().getColor(R.color.white));
                                holder.check_image_question.setVisibility(View.VISIBLE);
                                Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.answer_option_lay);
                                holder.answer_option_lay.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected, context.getResources().getColor(R.color.error_incorrect)));
                                GradientDrawable cardDrawableSelected = (GradientDrawable) holder.answer_image_layout.getBackground();
                                cardDrawableSelected.setStroke(3, context.getResources().getColor(R.color.error_incorrect));
                                holder.answer_image_layout.setElevation(5.0f);
                            }
                        } else if (isFirst && !isCourseQuestion) {
                            if (row_index == position) {
                                holder.option_image.setTextColor(context.getResources().getColor(R.color.white));
                                holder.check_image_question.setVisibility(View.VISIBLE);
                                Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.answer_option_lay);
                                holder.answer_option_lay.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected));
                                GradientDrawable cardDrawableSelected = (GradientDrawable) holder.answer_image_layout.getBackground();
                                cardDrawableSelected.setStroke(3, OustResourceUtils.getColors());
                                holder.answer_image_layout.setElevation(5.0f);
                            } else {
                                holder.option_image.setTextColor(context.getResources().getColor(R.color.primary_text));
                                holder.check_image_question.setVisibility(View.GONE);
                                GradientDrawable cardDrawableSelected = (GradientDrawable) holder.answer_image_layout.getBackground();
                                cardDrawableSelected.setStroke(3, context.getResources().getColor(R.color.unselected_text));
                                holder.answer_image_layout.setElevation(0.0f);
                            }
                        }
                    } else {
                        holder.answer_image_layout.setVisibility(View.GONE);
                        holder.answer_layout.setVisibility(View.VISIBLE);
                        holder.option.setText(questionAnswerModel.getOption());
                        if (questionAnswerModel.getAnswerOption().contains(KATEX_DELIMITER)) {
                            holder.answer_option_katex.setTextColor(context.getResources().getColor(R.color.primary_text));
                            holder.answer_option_katex.setTextColorString("#212121");
                            holder.answer_option_katex.setTextDirection(View.TEXT_ALIGNMENT_CENTER);
                            holder.answer_option_katex.setText(questionAnswerModel.getAnswerOption());
                            holder.katex_layout.setVisibility(View.VISIBLE);
                            holder.answer_option.setVisibility(View.GONE);
                            holder.webview_layout.setVisibility(View.GONE);
                        } else if (questionAnswerModel.getAnswerOption().contains("<math")) {
                            if (!loadData) {
                                OustSdkTools.getSpannedMathmlContent(questionAnswerModel.getAnswerOption(), holder.answer_option_math, false);
                            }
                            holder.katex_layout.setVisibility(View.GONE);
                            holder.answer_option.setVisibility(View.GONE);
                            holder.webview_layout.setVisibility(View.VISIBLE);
                        } else if (questionAnswerModel.getAnswerOption().contains("<img")) {
                            OustSdkTools.getSpannedMathmlContent(questionAnswerModel.getAnswerOption(), holder.answer_option_math, false);
                            holder.katex_layout.setVisibility(View.GONE);
                            holder.answer_option.setVisibility(View.GONE);
                            holder.webview_layout.setVisibility(View.VISIBLE);
                        } else {
//                            OustSdkTools.getSpannedContent(questionAnswerModel.getAnswerOption(), holder.answer_option);
                            OustSdkTools.loadDataFromHtml(questionAnswerModel.getAnswerOption(),holder.answer_option);
                            holder.katex_layout.setVisibility(View.GONE);
                            holder.answer_option.setVisibility(View.VISIBLE);
                            holder.webview_layout.setVisibility(View.GONE);
                        }

                        Drawable optionDrawable = context.getResources().getDrawable(R.drawable.answer_option_lay);
                        holder.option_layout.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawable, context.getResources().getColor(R.color.unselected_text)));

                        GradientDrawable cardDrawableDefault = (GradientDrawable) holder.answer_layout.getBackground();
                        cardDrawableDefault.setStroke(3, context.getResources().getColor(R.color.feed_border_stroke));
                        holder.answer_layout.setElevation(0.0f);

                        holder.answer_layout.setOnClickListener(v -> {
                            OustPreferences.saveintVar("QuestionAnswerPosition", position);
                            if (isCourseQuestion) {
                                selectedAnswers(holder, position, questionAnswerModel);
                            } else {
                                row_index = position;
                                isFirst = true;
                                isAnswerSelected = true;
                                count = 1;
                                questionOnItemClickListener.onItemClicked(count);
                                notifyDataSetChanged();
                            }
                        });

                        holder.answer_option_katex.setOnTouchListener(new View.OnTouchListener() {
                            public final static int FINGER_RELEASED = 0;
                            private int fingerState = FINGER_RELEASED;

                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                    if (fingerState == FINGER_RELEASED) {
                                        OustPreferences.saveintVar("QuestionAnswerPosition", position);
                                        if (isCourseQuestion) {
                                            selectedAnswers(holder, position, questionAnswerModel);
                                        } else {
                                            row_index = position;
                                            isFirst = true;
                                            isAnswerSelected = true;
                                            count = 1;
                                            questionOnItemClickListener.onItemClicked(count);
                                            notifyDataSetChanged();
                                        }
                                    }
                                }
                                return false;
                            }
                        });

                        holder.webview_layout.setOnClickListener(v -> {
                            OustPreferences.saveintVar("QuestionAnswerPosition", position);
                            if (isCourseQuestion) {
                                selectedAnswers(holder, position, questionAnswerModel);
                            } else {
                                row_index = position;
                                isFirst = true;
                                isAnswerSelected = true;
                                count = 1;
                                questionOnItemClickListener.onItemClicked(count);
                                loadData = true;
                                notifyDataSetChanged();
                            }
                        });

                        if (scores != null && !isAnswerSelected) {
                            if (scores.getAnswer() != null && scores.getAnswer().equals(questionAnswerModel.getAnswerOption())) {
                                OustPreferences.saveintVar("QuestionAnswerPosition", position);
                                row_index = position;
                                isFirst = true;
                                count = 1;
                                if (isReviewMode) {
                                    questionOnItemClickListener.onItemClicked(count);
                                }
                                // notifyDataSetChanged();
                            }
                        }
                        selectAnswer(holder, position, questionAnswerModel);
                    }
                }
            } else {
                ImageOrTextViewHolder holder = (ImageOrTextViewHolder) bindHolder;
                if (questionAnswerModel != null) {
                    if (optionTextArray.length > position) {
                        questionAnswerModel.setOption(optionTextArray[position]);
                    }
                    String type = questionAnswerModel.getOptionType();
                    if (type != null && type.equalsIgnoreCase("Image")) {
                        holder.answer_image_layout.setVisibility(View.VISIBLE);
                        holder.answer_layout.setVisibility(View.GONE);
                        holder.option_image.setText(questionAnswerModel.getOption());
                        if (questionAnswerModel.getImage() != null && !questionAnswerModel.getImage().isEmpty()) {
                            setImageOptionUrl(questionAnswerModel.getImage(), holder.answer_image);
                        }
                        //holder.option_image.setTextColor(context.getResources().getColor(R.color.white));
                        Drawable optionDrawable = context.getResources().getDrawable(R.drawable.circle_grey_bg);
                        holder.option_image.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawable, context.getResources().getColor(R.color.unselected_text)));

                        GradientDrawable cardDrawableDefault = (GradientDrawable) holder.answer_image_layout.getBackground();
                        cardDrawableDefault.setStroke(3, context.getResources().getColor(R.color.feed_border_stroke));
                        holder.answer_image_layout.setElevation(0.0f);

                        holder.answer_image_layout.setOnClickListener(v -> {
                            OustPreferences.saveintVar("QuestionAnswerPosition", position);
                            if (isCourseQuestion) {
                                if (!isReviewMode) {
                                    row_index = position;
                                    count++;
                                    holder.answer_image_layout.setClickable(false);

                                    if (answer != null && answer.equalsIgnoreCase(questionAnswerModel.getAnswerOption())) {
                                        holder.option_image.setTextColor(context.getResources().getColor(R.color.white));
                                        holder.check_image_question.setVisibility(View.VISIBLE);
                                        Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.circle_grey_bg);
                                        holder.option_image.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected, context.getResources().getColor(R.color.progress_correct)));
                                        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_tick_done);
                                        holder.check_image_question.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(drawable, context.getResources().getColor(R.color.progress_correct)));
                                        GradientDrawable cardDrawableSelected = (GradientDrawable) holder.answer_image_layout.getBackground();
                                        cardDrawableSelected.setStroke(3, context.getResources().getColor(R.color.progress_correct));
                                        holder.answer_image_layout.setElevation(5.0f);
                                    } else {
                                        holder.option_image.setTextColor(context.getResources().getColor(R.color.white));
                                        holder.check_image_question.setVisibility(View.VISIBLE);
                                        Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.circle_grey_bg);
                                        holder.option_image.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected, context.getResources().getColor(R.color.error_incorrect)));
                                        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_close_circle);
                                        holder.check_image_question.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(drawable, context.getResources().getColor(R.color.error_incorrect)));
                                        GradientDrawable cardDrawableSelected = (GradientDrawable) holder.answer_image_layout.getBackground();
                                        cardDrawableSelected.setStroke(3, context.getResources().getColor(R.color.error_incorrect));
                                        holder.answer_image_layout.setElevation(5.0f);
                                    }
                                    questionOnItemClickListener.onItemClicked(count);
                                }
                            } else {
                                row_index = position;
                                isFirst = true;
                                isAnswerSelected = true;
                                count = 1;
                                questionOnItemClickListener.onItemClicked(count);
                                notifyDataSetChanged();
                            }
                        });

                        holder.expand_icon.setOnClickListener(v -> gifZoomPopup(holder.answer_image.getDrawable()));

                        if (scores != null && !isAnswerSelected) {
                            if (scores.getAnswer() != null && scores.getAnswer().equals(questionAnswerModel.getAnswerOption())) {
                                OustPreferences.saveintVar("QuestionAnswerPosition", position);
                                row_index = position;
                                isFirst = true;
                                count = 1;
                                questionOnItemClickListener.onItemClicked(count);
                            }
                        }

                        if (isReviewMode) {
                            holder.answer_image_layout.setEnabled(false);
                            if (answer != null && answer.equalsIgnoreCase(questionAnswerModel.getAnswerOption())) {
                                holder.option_image.setTextColor(context.getResources().getColor(R.color.white));
                                holder.check_image_question.setVisibility(View.VISIBLE);
                                Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.circle_grey_bg);
                                holder.option_image.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected, context.getResources().getColor(R.color.progress_correct)));
                                GradientDrawable cardDrawableSelected = (GradientDrawable) holder.answer_image_layout.getBackground();
                                cardDrawableSelected.setStroke(3, context.getResources().getColor(R.color.progress_correct));
                                holder.answer_image_layout.setElevation(5.0f);
                            } else if (row_index == position) {
                                holder.option_image.setTextColor(context.getResources().getColor(R.color.white));
                                holder.check_image_question.setVisibility(View.VISIBLE);
                                Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.circle_grey_bg);
                                holder.option_image.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected, context.getResources().getColor(R.color.error_incorrect)));
                                GradientDrawable cardDrawableSelected = (GradientDrawable) holder.answer_image_layout.getBackground();
                                cardDrawableSelected.setStroke(3, context.getResources().getColor(R.color.error_incorrect));
                                holder.answer_image_layout.setElevation(5.0f);
                            }
                        } else if (isFirst && !isCourseQuestion) {
                            if (row_index == position) {
                                holder.option_image.setTextColor(context.getResources().getColor(R.color.white));
                                holder.check_image_question.setVisibility(View.VISIBLE);
                                Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.circle_grey_bg);
                                holder.option_image.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected));
                                GradientDrawable cardDrawableSelected = (GradientDrawable) holder.answer_image_layout.getBackground();
                                cardDrawableSelected.setStroke(3, OustResourceUtils.getColors());
                                holder.answer_image_layout.setElevation(5.0f);
                            } else {
                                holder.option_image.setTextColor(context.getResources().getColor(R.color.primary_text));
                                holder.check_image_question.setVisibility(View.GONE);
                                GradientDrawable cardDrawableSelected = (GradientDrawable) holder.answer_image_layout.getBackground();
                                cardDrawableSelected.setStroke(3, context.getResources().getColor(R.color.unselected_text));
                                holder.answer_image_layout.setElevation(0.0f);
                            }
                        }
                    } else {
                        holder.answer_image_layout.setVisibility(View.GONE);
                        holder.answer_layout.setVisibility(View.VISIBLE);
                        holder.option.setText(questionAnswerModel.getOption());
                        if (questionAnswerModel.getAnswerOption().contains(KATEX_DELIMITER)) {
                            holder.answer_option_katex.setTextColor(context.getResources().getColor(R.color.primary_text));
                            holder.answer_option_katex.setTextColorString("#212121");
                            holder.answer_option_katex.setTextDirection(View.TEXT_ALIGNMENT_CENTER);
                            holder.answer_option_katex.setText(questionAnswerModel.getAnswerOption());
                            holder.katex_layout.setVisibility(View.VISIBLE);
                            holder.answer_option.setVisibility(View.GONE);
                            holder.webview_layout.setVisibility(View.GONE);
                        } else if (questionAnswerModel.getAnswerOption().contains("<math")) {
                            if (!loadData) {
                                OustSdkTools.getSpannedMathmlContent(questionAnswerModel.getAnswerOption(), holder.answer_option_math, false);
                            }
                            holder.katex_layout.setVisibility(View.GONE);
                            holder.answer_option.setVisibility(View.GONE);
                            holder.webview_layout.setVisibility(View.VISIBLE);
                        } else if (questionAnswerModel.getAnswerOption().contains("<img")) {
                            OustSdkTools.getSpannedMathmlContent(questionAnswerModel.getAnswerOption(), holder.answer_option_math, false);
                            holder.katex_layout.setVisibility(View.GONE);
                            holder.answer_option.setVisibility(View.GONE);
                            holder.webview_layout.setVisibility(View.VISIBLE);
                        } else {
//                            OustSdkTools.getSpannedContent(questionAnswerModel.getAnswerOption(), holder.answer_option);
                            OustSdkTools.loadDataFromHtml(questionAnswerModel.getAnswerOption(),holder.answer_option);
                            holder.katex_layout.setVisibility(View.GONE);
                            holder.answer_option.setVisibility(View.VISIBLE);
                            holder.webview_layout.setVisibility(View.GONE);
                        }

                        Drawable optionDrawable = context.getResources().getDrawable(R.drawable.answer_option_lay);
                        holder.option_layout.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawable, context.getResources().getColor(R.color.unselected_text)));

                        GradientDrawable cardDrawableDefault = (GradientDrawable) holder.answer_layout.getBackground();
                        cardDrawableDefault.setStroke(3, context.getResources().getColor(R.color.feed_border_stroke));
                        holder.answer_layout.setElevation(0.0f);

                        holder.answer_layout.setOnClickListener(v -> {
                            OustPreferences.saveintVar("QuestionAnswerPosition", position);
                            if (isCourseQuestion) {
                                selectedAnswers(holder, position, questionAnswerModel);
                            } else {
                                row_index = position;
                                isFirst = true;
                                isAnswerSelected = true;
                                count = 1;
                                questionOnItemClickListener.onItemClicked(count);
                                notifyDataSetChanged();
                            }
                        });

                        holder.webview_layout.setOnClickListener(v -> {
                            OustPreferences.saveintVar("QuestionAnswerPosition", position);
                            if (isCourseQuestion) {
                                selectedAnswers(holder, position, questionAnswerModel);
                            } else {
                                row_index = position;
                                isFirst = true;
                                isAnswerSelected = true;
                                count = 1;
                                questionOnItemClickListener.onItemClicked(count);
                                loadData = true;
                                notifyDataSetChanged();
                            }
                        });

                        holder.answer_option_katex.setOnTouchListener(new View.OnTouchListener() {
                            public final static int FINGER_RELEASED = 0;
                            private int fingerState = FINGER_RELEASED;

                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                    if (fingerState == FINGER_RELEASED) {
                                        OustPreferences.saveintVar("QuestionAnswerPosition", position);
                                        if (isCourseQuestion) {
                                            selectedAnswers(holder, position, questionAnswerModel);
                                        } else {
                                            row_index = position;
                                            isFirst = true;
                                            isAnswerSelected = true;
                                            count = 1;
                                            questionOnItemClickListener.onItemClicked(count);
                                            notifyDataSetChanged();
                                        }
                                    }
                                }
                                return false;
                            }
                        });

                        if (scores != null && !isAnswerSelected) {
                            if (scores.getAnswer() != null && scores.getAnswer().equals(questionAnswerModel.getAnswerOption())) {
                                OustPreferences.saveintVar("QuestionAnswerPosition", position);
                                row_index = position;
                                isFirst = true;
                                count = 1;
                                if (isReviewMode) {
                                    questionOnItemClickListener.onItemClicked(count);
                                }
                                // notifyDataSetChanged();
                            }
                        }
                        selectAnswer(holder, position, questionAnswerModel);
                    }
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void selectAnswer(ImageOrTextViewHolder holder, int position, QuestionAnswerModel questionAnswerModel) {
        if (isReviewMode) {
            holder.answer_layout.setEnabled(false);
            if (answer != null && answer.equalsIgnoreCase(questionAnswerModel.getAnswerOption())) {
                holder.option.setTextColor(context.getResources().getColor(R.color.white));
                holder.check_question.setVisibility(View.VISIBLE);
                Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.answer_option_lay);
                holder.option_layout.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected, context.getResources().getColor(R.color.progress_correct)));
                GradientDrawable cardDrawable = (GradientDrawable) holder.answer_layout.getBackground();
                cardDrawable.setStroke(3, context.getResources().getColor(R.color.progress_correct));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.answer_layout.setElevation(5.0f);
                }

            } else if (row_index == position) {
                holder.option.setTextColor(context.getResources().getColor(R.color.white));
                holder.check_question.setVisibility(View.VISIBLE);
                Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.answer_option_lay);
                holder.option_layout.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected, context.getResources().getColor(R.color.error_incorrect)));
                GradientDrawable cardDrawable = (GradientDrawable) holder.answer_layout.getBackground();
                cardDrawable.setStroke(3, context.getResources().getColor(R.color.error_incorrect));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.answer_layout.setElevation(5.0f);
                }
            }
        } else if (isFirst && !isCourseQuestion) {
            if (row_index == position) {
                holder.option.setTextColor(context.getResources().getColor(R.color.white));
                holder.check_question.setVisibility(View.VISIBLE);
                Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.answer_option_lay);
                holder.option_layout.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected));
                GradientDrawable cardDrawable = (GradientDrawable) holder.answer_layout.getBackground();
                cardDrawable.setStroke(3, OustResourceUtils.getColors());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.answer_layout.setElevation(5.0f);
                }
            } else {
                holder.option.setTextColor(context.getResources().getColor(R.color.primary_text));
                holder.check_question.setVisibility(View.GONE);
                GradientDrawable cardDrawable = (GradientDrawable) holder.answer_layout.getBackground();
                cardDrawable.setStroke(3, context.getResources().getColor(R.color.unselected_text));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.answer_layout.setElevation(0.0f);
                }
            }
        }
    }

    private void selectedAnswers(ImageOrTextViewHolder holder, int position, QuestionAnswerModel questionAnswerModel) {
        if (!isReviewMode) {
            row_index = position;
            count++;
            if (answer != null && answer.equalsIgnoreCase(questionAnswerModel.getAnswerOption())) {
                if (holder.webview_layout.getVisibility() == View.VISIBLE) {
                    ViewGroup.LayoutParams layoutParams = holder.option_layout.getLayoutParams();
                    layoutParams.height = holder.webview_layout.getHeight();
                    //layoutParams.width = holder.webview_layout.getWidth();
                    holder.option_layout.setLayoutParams(layoutParams);
                }

                holder.option.setTextColor(context.getResources().getColor(R.color.white));
                holder.check_question.setVisibility(View.VISIBLE);
                Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.answer_option_lay);
                holder.option_layout.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected, context.getResources().getColor(R.color.progress_correct)));
                Drawable drawable = context.getResources().getDrawable(R.drawable.ic_tick_done);
                holder.check_question.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(drawable, context.getResources().getColor(R.color.progress_correct)));
                GradientDrawable cardDrawable = (GradientDrawable) holder.answer_layout.getBackground();
                cardDrawable.setStroke(3, context.getResources().getColor(R.color.progress_correct));

            } else {
                if (holder.webview_layout.getVisibility() == View.VISIBLE) {
                    ViewGroup.LayoutParams layoutParams = holder.option_layout.getLayoutParams();
                    layoutParams.height = holder.webview_layout.getHeight();
                    //layoutParams.width = holder.webview_layout.getWidth();
                    holder.option_layout.setLayoutParams(layoutParams);
                }

                holder.option.setTextColor(context.getResources().getColor(R.color.white));
                holder.check_question.setVisibility(View.VISIBLE);
                Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.answer_option_lay);
                holder.option_layout.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected, context.getResources().getColor(R.color.error_incorrect)));
                Drawable drawable = context.getResources().getDrawable(R.drawable.ic_close_circle);
                holder.check_question.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(drawable, context.getResources().getColor(R.color.error_incorrect)));
                GradientDrawable cardDrawable = (GradientDrawable) holder.answer_layout.getBackground();
                cardDrawable.setStroke(3, context.getResources().getColor(R.color.error_incorrect));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.answer_layout.setElevation(5.0f);
            }

            if (questionOnItemClickListener != null) {
                questionOnItemClickListener.onItemClicked(count);
            }
            holder.answer_layout.setEnabled(false);
        }
    }

    private void selectAnswer(ImageAndTextViewHolder holder, int position, QuestionAnswerModel questionAnswerModel) {
        Log.d("TAG", "selectAnswer: 2");
        try {
            if (isReviewMode) {
                holder.answer_layout.setEnabled(false);
                if (answer != null && answer.equalsIgnoreCase(questionAnswerModel.getAnswerOption())) {
                    holder.option.setTextColor(context.getResources().getColor(R.color.white));
                    holder.check_question.setVisibility(View.VISIBLE);
                    Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.answer_option_lay);
                    holder.option_layout.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected, context.getResources().getColor(R.color.progress_correct)));
                    GradientDrawable cardDrawable = (GradientDrawable) holder.answer_layout.getBackground();
                    cardDrawable.setStroke(3, context.getResources().getColor(R.color.progress_correct));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.answer_layout.setElevation(5.0f);
                    }
                } else if (row_index == position) {
                    holder.option.setTextColor(context.getResources().getColor(R.color.white));
                    holder.check_question.setVisibility(View.VISIBLE);
                    Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.answer_option_lay);
                    holder.option_layout.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected, context.getResources().getColor(R.color.error_incorrect)));
                    GradientDrawable cardDrawable = (GradientDrawable) holder.answer_layout.getBackground();
                    cardDrawable.setStroke(3, context.getResources().getColor(R.color.error_incorrect));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.answer_layout.setElevation(5.0f);
                    }
                }
            } else if (isFirst && !isCourseQuestion) {
                if (row_index == position) {
                    holder.option.setTextColor(context.getResources().getColor(R.color.white));
                    holder.check_question.setVisibility(View.VISIBLE);
                    Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.answer_option_lay);
                    holder.option_layout.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected));
                    GradientDrawable cardDrawable = (GradientDrawable) holder.answer_layout.getBackground();
                    cardDrawable.setStroke(3, OustResourceUtils.getColors());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.answer_layout.setElevation(5.0f);
                    }
                } else {
                    holder.option.setTextColor(context.getResources().getColor(R.color.primary_text));
                    holder.check_question.setVisibility(View.GONE);
                    GradientDrawable cardDrawable = (GradientDrawable) holder.answer_layout.getBackground();
                    cardDrawable.setStroke(3, context.getResources().getColor(R.color.unselected_text));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.answer_layout.setElevation(0.0f);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void selectedAnswers(ImageAndTextViewHolder holder, int position, QuestionAnswerModel questionAnswerModel) {
        if (!isReviewMode) {
            row_index = position;
            count++;
            if (answer != null && answer.equalsIgnoreCase(questionAnswerModel.getAnswerOption())) {
                holder.option.setTextColor(context.getResources().getColor(R.color.white));
                holder.check_question.setVisibility(View.VISIBLE);
                if (holder.webview_layout.getVisibility() == View.VISIBLE) {
                    ViewGroup.LayoutParams layoutParams = holder.option_layout.getLayoutParams();
                    layoutParams.height = holder.webview_layout.getHeight();
                    //layoutParams.width = holder.webview_layout.getWidth();
                    holder.option_layout.setLayoutParams(layoutParams);
                }

                Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.answer_option_lay);
                holder.option_layout.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected, context.getResources().getColor(R.color.progress_correct)));
                Drawable drawable = context.getResources().getDrawable(R.drawable.ic_tick_done);
                holder.check_question.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(drawable, context.getResources().getColor(R.color.progress_correct)));
                GradientDrawable cardDrawable = (GradientDrawable) holder.answer_layout.getBackground();
                cardDrawable.setStroke(3, context.getResources().getColor(R.color.progress_correct));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.answer_layout.setElevation(5.0f);
                }

            } else {
                holder.option.setTextColor(context.getResources().getColor(R.color.white));
                holder.check_question.setVisibility(View.VISIBLE);
                if (holder.webview_layout.getVisibility() == View.VISIBLE) {
                    ViewGroup.LayoutParams layoutParams = holder.option_layout.getLayoutParams();
                    layoutParams.height = holder.webview_layout.getHeight();
                    //layoutParams.width = holder.webview_layout.getWidth();
                    holder.option_layout.setLayoutParams(layoutParams);
                }

                Drawable optionDrawableForSelected = context.getResources().getDrawable(R.drawable.answer_option_lay);
                holder.option_layout.setBackground(OustResourceUtils.setDefaultDrawableColor(optionDrawableForSelected, context.getResources().getColor(R.color.error_incorrect)));
                Drawable drawable = context.getResources().getDrawable(R.drawable.ic_close_circle);
                holder.check_question.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(drawable, context.getResources().getColor(R.color.error_incorrect)));
                GradientDrawable cardDrawable = (GradientDrawable) holder.answer_layout.getBackground();
                cardDrawable.setStroke(3, context.getResources().getColor(R.color.error_incorrect));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.answer_layout.setElevation(5.0f);
                }
            }

            if (questionOnItemClickListener != null) {
                questionOnItemClickListener.onItemClicked(count);
            }
            holder.answer_layout.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        if (questionAnswerModels == null) {
            return 0;
        }
        return questionAnswerModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ImageOrTextViewHolder extends RecyclerView.ViewHolder {

        LinearLayout answer_layout;
        LinearLayout answer_image_layout;
        LinearLayout option_layout;
        TextView option;
        TextView option_image;
        TextView answer_option;
        WebViewClass answer_option_math;
        LinearLayout katex_layout, webview_layout;
        KatexView answer_option_katex;
        ImageView check_question;
        ImageView answer_image;
        ImageView check_image_question;
        ImageView expand_icon;

        public ImageOrTextViewHolder(View itemView) {
            super(itemView);
            answer_layout = itemView.findViewById(R.id.answer_layout);
            answer_image_layout = itemView.findViewById(R.id.answer_image_layout);
            option_layout = itemView.findViewById(R.id.option_layout);
            option = itemView.findViewById(R.id.option);
            option_image = itemView.findViewById(R.id.option_image);
            answer_option = itemView.findViewById(R.id.answer_option);
            katex_layout = itemView.findViewById(R.id.katex_layout);
            answer_option_katex = itemView.findViewById(R.id.answer_option_katex);
            check_question = itemView.findViewById(R.id.check_question);
            answer_image = itemView.findViewById(R.id.answer_image);
            check_image_question = itemView.findViewById(R.id.check_image_question);
            expand_icon = itemView.findViewById(R.id.expand_icon);
            answer_option_math = itemView.findViewById(R.id.answer_option_math);
            webview_layout = itemView.findViewById(R.id.webview_layout);
        }
    }

    public static class ImageAndTextViewHolder extends RecyclerView.ViewHolder {

        LinearLayout answer_layout;
        LinearLayout answer_image_layout;
        LinearLayout option_layout;
        LinearLayout answer_option_lay;
        TextView option;
        TextView option_image;
        TextView answer_option;
        WebViewClass answer_option_math;
        LinearLayout katex_layout, webview_layout;
        KatexView answer_option_katex;
        ImageView check_question;
        ImageView answer_image;
        ImageView check_image_question;
        ImageView expand_icon;

        public ImageAndTextViewHolder(View itemView) {
            super(itemView);
            answer_layout = itemView.findViewById(R.id.answer_layout);
            answer_image_layout = itemView.findViewById(R.id.answer_image_layout);
            option_layout = itemView.findViewById(R.id.option_layout);
            answer_option_lay = itemView.findViewById(R.id.answer_option_lay);
            option = itemView.findViewById(R.id.option);
            option_image = itemView.findViewById(R.id.option_image);
            answer_option = itemView.findViewById(R.id.answer_option);
            katex_layout = itemView.findViewById(R.id.katex_layout);
            answer_option_katex = itemView.findViewById(R.id.answer_option_katex);
            check_question = itemView.findViewById(R.id.check_question);
            answer_image = itemView.findViewById(R.id.answer_image);
            check_image_question = itemView.findViewById(R.id.check_image_question);
            expand_icon = itemView.findViewById(R.id.expand_icon);
            answer_option_math = itemView.findViewById(R.id.answer_option_math);
            webview_layout = itemView.findViewById(R.id.webview_layout);
        }

    }

    public void setImageOptionUrl(String url, ImageView imageView) {
        String imageURL = url;
        if (url != null) {
            url = OustMediaTools.removeAwsOrCDnUrl(url);
        }
        url = "oustlearn_" + OustMediaTools.getMediaFileName(url);
        File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
        try {
            if (file.exists()) {
                Uri uri = Uri.fromFile(file);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                if (options.outWidth != -1 && options.outHeight != -1) {
                    if (OustSdkTools.checkInternetStatus()) {
                        Glide.with(context).load(imageURL).into(imageView);
                        if (file.exists()) {
                            boolean b = file.delete();
                            Log.d("QuestionAnswerAdapter", "" + b);
                        }
                        downLoad(imageURL);
                    } else {
                        Glide.with(context).load(uri).into(imageView);
                    }
                } else {
                    Glide.with(context).load(imageURL).into(imageView);
                    if (file.exists()) {
                        boolean b = file.delete();
                        Log.d("QuestionAnswerAdapter", "" + b);
                    }
                    downLoad(imageURL);
                }

            } else {
                if (imageURL != null && !imageURL.isEmpty()) {
                    Picasso.get().load(imageURL).into(imageView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void downLoad(String url) {

        try {
            DownloadFiles downloadFiles;
            downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
                @Override
                public void onDownLoadProgressChanged(String message, String progress) {
                }

                @Override
                public void onDownLoadError(String message, int errorCode) {
                }

                @Override
                public void onDownLoadStateChanged(String message, int code) {
                }

                @Override
                public void onAddedToQueue(String id) {
                }

                @Override
                public void onDownLoadStateChangedWithId(String message, int code, String id) {
                }
            });

            String path = context.getFilesDir() + "/";
            String filename = OustMediaTools.getMediaFileName(url);
            downloadFiles.startDownLoad(url, path, filename, true, false);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gifZoomPopup(final Drawable gif) {
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams")
            View popUpView = inflater.inflate(R.layout.gifzoom_popup, null);
            zoomImagePopup = OustSdkTools.createPopUp(popUpView);
            GifImageView gifImageView = popUpView.findViewById(R.id.mainzooming_img);
            gifImageView.setImageDrawable(gif);
            ImageButton imageCloseButton = popUpView.findViewById(R.id.zooming_imgclose_btn);
            final RelativeLayout zoomLayout = popUpView.findViewById(R.id.mainzoomimg_layout);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(zoomLayout, "scaleX", 0.0f, 1);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(zoomLayout, "scaleY", 0.0f, 1);
            scaleDownX.setDuration(400);
            scaleDownY.setDuration(400);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
            zoomLayout.setVisibility(View.VISIBLE);
            zoomImagePopup.setOnDismissListener(zoomImagePopup::dismiss);
            imageCloseButton.setOnClickListener(view -> {
                ObjectAnimator scaleDownX1 = ObjectAnimator.ofFloat(zoomLayout, "scaleX", 1.0f, 0);
                ObjectAnimator scaleDownY1 = ObjectAnimator.ofFloat(zoomLayout, "scaleY", 1.0f, 0);
                scaleDownX1.setDuration(350);
                scaleDownY1.setDuration(350);
                scaleDownX1.setInterpolator(new DecelerateInterpolator());
                scaleDownY1.setInterpolator(new DecelerateInterpolator());
                AnimatorSet scaleDown1 = new AnimatorSet();
                scaleDown1.play(scaleDownX1).with(scaleDownY1);
                scaleDown1.start();
                scaleDown1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        try {
                            if (zoomImagePopup.isShowing()) {
                                zoomImagePopup.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
            });
            popUpView.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ObjectAnimator scaleDownX12 = ObjectAnimator.ofFloat(zoomLayout, "scaleX", 1.0f, 0);
                    ObjectAnimator scaleDownY12 = ObjectAnimator.ofFloat(zoomLayout, "scaleY", 1.0f, 0);
                    scaleDownX12.setDuration(350);
                    scaleDownY12.setDuration(350);
                    scaleDownX12.setInterpolator(new DecelerateInterpolator());
                    scaleDownY12.setInterpolator(new DecelerateInterpolator());
                    AnimatorSet scaleDown12 = new AnimatorSet();
                    scaleDown12.play(scaleDownX12).with(scaleDownY12);
                    scaleDown12.start();
                    scaleDown12.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (zoomImagePopup.isShowing()) {
                                zoomImagePopup.dismiss();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void closePopWindow() {
        try {
            if (zoomImagePopup != null && zoomImagePopup.isShowing())
                zoomImagePopup.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

}
