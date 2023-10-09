package com.oustme.oustsdk.question_module.adapter;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.assessment_ui.examMode.ExamModeCallBack;
import com.oustme.oustsdk.assessment_ui.examMode.QuestionView;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.ViewHolder> {

    Context context;
    ArrayList<DTOQuestions> questionsList;
    ExamModeCallBack examModeCallBack;
    boolean isReviewMode;

    public void setAdapter(Context context, ArrayList<DTOQuestions> questionsList) {
        this.context = context;
        this.questionsList = questionsList;
    }

    public void setExamModeCallBack(ExamModeCallBack examModeCallBack){
        this.examModeCallBack = examModeCallBack;
    }

    public void setReviewMode(boolean isReviewMode){
        this.isReviewMode = isReviewMode;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView question_card_root;
        FrameLayout question_num_layout;
        TextView question_num;
        TextView question_text;
        ImageView question_status;
        LinearLayout katex_question_layout;
        KatexView katex_question;

        ViewHolder(View view) {
            super(view);
            question_card_root = view.findViewById(R.id.question_card_root);
            question_num_layout = view.findViewById(R.id.question_num_layout);
            question_num = view.findViewById(R.id.question_num);
            question_text = view.findViewById(R.id.question_text);
            question_status = view.findViewById(R.id.question_status);
            katex_question_layout = view.findViewById(R.id.katex_question_layout);
            katex_question = view.findViewById(R.id.katex_question);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_question_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            DTOQuestions questions = questionsList.get(position);
            if(questions!=null){
                if (questions.getQuestion() != null && !questions.getQuestion().isEmpty()) {

                    if (questions.getQuestion().contains(KATEX_DELIMITER)) {
                        holder.katex_question.setTextColor(context.getResources().getColor(R.color.primary_text));
                        holder.katex_question.setTextColorString("#212121");
                        holder.katex_question.setText(questions.getQuestion());
                        holder.katex_question_layout.setVisibility(View.VISIBLE);
                        holder.question_text.setVisibility(View.GONE);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            holder.question_text.setText(Html.fromHtml(questions.getQuestion().trim(), Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            holder.question_text.setText(Html.fromHtml(questions.getQuestion().trim()));
                        }
                        holder.katex_question_layout.setVisibility(View.GONE);
                        holder.question_text.setVisibility(View.VISIBLE);
                    }
                }

                holder.question_num.setText(String.valueOf(position+1));
                List<Scores> scoresList = OustStaticVariableHandling.getInstance().getScoresList();
                HashMap<Integer, QuestionView> questionViewHashMap = OustStaticVariableHandling.getInstance().getQuestionViewList();
                holder.question_num_layout.setBackgroundColor(Color.parseColor("#AEAEAE"));
                if(!isReviewMode){
                    if(questionViewHashMap!=null&&questionViewHashMap.size()!=0){
                        QuestionView questionView = questionViewHashMap.get((int)questions.getQuestionId());
                        if(questionView!=null){
                            if(questionView.isAnswered()){
                                holder.question_num_layout.setBackgroundColor(OustResourceUtils.getColors());
                            }else if(questionView.isViewed()){
                                holder.question_num_layout.setBackgroundColor(Color.parseColor(OustPreferences.get("secondaryColor")));
                            }
                        }
                    }else if(scoresList!=null&&scoresList.size()!=0){
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            boolean isSelected = scoresList.stream().anyMatch(o -> o.getQuestion()==questions.getQuestionId());
                            if(isSelected){
                                holder.question_num_layout.setBackgroundColor(OustResourceUtils.getColors());
                            }else{
                                if(scoresList.size()>position&&scoresList.get(position)!=null){
                                    holder.question_num_layout.setBackgroundColor(Color.parseColor(OustPreferences.get("secondaryColor")));
                                }
                            }
                        }else{
                            for (int i =0;i<scoresList.size();i++) {
                                if(scoresList.get(i).getQuestion()==questions.getQuestionId()){
                                    // isSelected = true;
                                    holder.question_num_layout.setBackgroundColor(OustResourceUtils.getColors());
                                    break;
                                }else if(scoresList.get(i)!=null&&i==position){
                                    holder.question_num_layout.setBackgroundColor(Color.parseColor(OustPreferences.get("secondaryColor")));
                                    break;
                                }
                            }
                        }
                    }
                }else{
                    if(scoresList!=null&&scoresList.size()!=0){
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            boolean isSelected = scoresList.stream().anyMatch(o -> o.getQuestion()==questions.getQuestionId());
                            if(isSelected){
                                holder.question_num_layout.setBackgroundColor(OustResourceUtils.getColors());
                            }
                        }else{
                            for (int i =0;i<scoresList.size();i++) {
                                if(scoresList.get(i).getQuestion()==questions.getQuestionId()){
                                    // isSelected = true;
                                    holder.question_num_layout.setBackgroundColor(OustResourceUtils.getColors());
                                    break;
                                }
                            }
                        }
                    }
                }
                holder.question_card_root.setOnClickListener(v -> examModeCallBack.onQuestionClick(position));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

}
