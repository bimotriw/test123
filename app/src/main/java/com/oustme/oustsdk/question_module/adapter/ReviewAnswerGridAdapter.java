package com.oustme.oustsdk.question_module.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.ArrayList;


/**
 * This adapter is responsible for displaying answer cards provided by user.
 * Correct answer cards will be marked in green and wrong answer cards marked in red.
 * We will compare user selected/entered answers with mPower answers(correct answer).
 */

public class ReviewAnswerGridAdapter extends ArrayAdapter<String> {
    private final String mAnswer; //mpower answer
    private final boolean isMRQ;
    private final Drawable drawableCorrect;
    private final Drawable drawableWrong;
    ArrayList<String> showAnswers;

    public ReviewAnswerGridAdapter(Context context, String[] score, String answer, Scores scores, boolean isMRQ) {
        super(context, 0, score);
        this.mAnswer = answer;
        this.isMRQ = isMRQ;
        drawableCorrect = getContext().getResources().getDrawable(R.drawable.ic_checkmark_greenfill);
        drawableWrong = getContext().getResources().getDrawable(R.drawable.ic_close_circle);
    }

    public ReviewAnswerGridAdapter(Context context, ArrayList<String> showAnswers, String answer, Scores scores, boolean isMRQ) {
        super(context, 0, showAnswers);
        this.mAnswer = answer;
        this.showAnswers = showAnswers;
        this.isMRQ = isMRQ;
        drawableCorrect = getContext().getResources().getDrawable(R.drawable.ic_checkmark_greenfill);
        drawableWrong = getContext().getResources().getDrawable(R.drawable.ic_close_circle);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View gridView = convertView;
        if (gridView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            gridView = LayoutInflater.from(getContext()).inflate(R.layout.answer_grid_item_layout, parent, false);
        }

        String item = getItem(position);
        TextView textView = gridView.findViewById(R.id.answer_option);
        textView.setSelected(true);
        CardView cardView = gridView.findViewById(R.id.grid_card_view);
        ImageView cardIcon = gridView.findViewById(R.id.card_icon);
        if (item != null) {
            //MRQ & MCQ
            if (isMRQ) {
                textView.setText(item.toUpperCase());
                if (mAnswer.contains(item)) {
                    checkInCorrect(cardIcon, cardView);
                } else {
                    checkInWrong(cardIcon, cardView);
                }
                //FIB
            } else {
                textView.setText(item);
                String[] mPowerAnswer = mAnswer.split("#");
                if (mPowerAnswer[position].trim().equalsIgnoreCase(item.trim())) {
                    checkInCorrect(cardIcon, cardView);
                } else {
                    checkInWrong(cardIcon, cardView);
                }
            }
        }
        return gridView;
    }

    private void checkInCorrect(ImageView cardIcon, CardView cardView) {
        cardIcon.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(drawableCorrect, getContext().getResources().getColor(R.color.progress_correct)));
        cardView.setBackground(getContext().getResources().getDrawable(R.drawable.right_answer_background));
    }

    private void checkInWrong(ImageView cardIcon, CardView cardView) {
        cardIcon.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(drawableWrong, getContext().getResources().getColor(R.color.error_incorrect)));
        cardView.setBackground(getContext().getResources().getDrawable(R.drawable.wrong_answer_background));
    }
}
