package com.oustme.oustsdk.question_module.course;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.Objects;

public class CourseQuestionMarkFragment extends Fragment {

    private ImageView backGroundImg;
    private ImageView questionMarkImg;
    private TextView questionTimer;
    private String backgroundImage;
    private long millis;
    boolean isCurrentCardQuestion = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_question_mark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view) {
        backGroundImg = view.findViewById(R.id.course_question_mark_bck_img);
        questionMarkImg = view.findViewById(R.id.course_question_mark_img);
        questionTimer = view.findViewById(R.id.course_question_mark_timer);

        OustSdkTools.setImage(questionMarkImg, OustSdkApplication.getContext().getResources().getString(R.string.whitequestion_img));

    }

    private void initData() {
        Log.e("TAG", "initData: BG" + backgroundImage);
        Log.e("TAG", "initData: time" + millis);

        setViewBackgroundImage();
        @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d", millis / 60, millis % 60);
        questionTimer.setText(hms);
    }

    private void setViewBackgroundImage() {
        try {
            if (backgroundImage != null) {
                backGroundImg.setVisibility(View.VISIBLE);
                Glide.with(Objects.requireNonNull(requireActivity())).load(backgroundImage).diskCacheStrategy(DiskCacheStrategy.DATA).into(backGroundImg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setBackGroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setQuestionTimer(long maxtime) {
        this.millis = maxtime;
    }
}