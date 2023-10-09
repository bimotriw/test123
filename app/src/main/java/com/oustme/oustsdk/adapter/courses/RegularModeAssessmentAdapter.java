package com.oustme.oustsdk.adapter.courses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.firebase.course.CourseCardClass;
import com.oustme.oustsdk.response.course.AssessmentNavModel;
import com.oustme.oustsdk.response.course.UserCardData;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class RegularModeAssessmentAdapter extends RecyclerView.Adapter<RegularModeAssessmentAdapter.MyViewHolder> {

    List<AssessmentNavModel> assessmentNavModels;
    private boolean isCardCompleted;
    private boolean isCourseCompleted;
    List<DTOUserCardData> realmUserCarddataList;
    List<DTOUserCardData> userCardDataList;
    private boolean isSurveyMandatory;

    public RegularModeAssessmentAdapter(List<AssessmentNavModel> assessmentNavModels, boolean isCourseCompleted, List<DTOUserCardData> realmUserCarddataList, List<DTOUserCardData> userCardDataList, boolean isSurveyMandatory) {
        this.assessmentNavModels = assessmentNavModels;
        this.isCourseCompleted = isCourseCompleted;
        this.realmUserCarddataList = realmUserCarddataList;
        this.userCardDataList = userCardDataList;
        this.isSurveyMandatory = isSurveyMandatory;
    }

    @NonNull
    @Override
    public RegularModeAssessmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.regularmode_horizontalrowlayout, parent, false);
        return new RegularModeAssessmentAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RegularModeAssessmentAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(isSurveyMandatory){
            isCardCompleted = assessmentNavModels.get(position).getMappedAssessmentPercentage() > 90;
        }else {
            isCardCompleted = assessmentNavModels.get(position).getMappedAssessmentPercentage() > 95;
        }

        if (isCardCompleted) {
            holder.imageViewLockOpen.setImageDrawable(holder.itemView.getContext().getResources().getDrawable(R.drawable.ic_tick));
        }
        holder.layout_card_background.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.rounded_corner_plain));

        holder.card_text.setText(assessmentNavModels.get(position).getMappedAssessmentName());
//        holder.card_text.setVisibility(View.VISIBLE);
        if (assessmentNavModels.get(position).getMappedAssessmentImage() != null) {
            holder.card_questionimage.setVisibility(View.GONE);
            holder.card_image_gif.setVisibility(View.GONE);
            holder.card_image.setVisibility(View.VISIBLE);
            setImage(assessmentNavModels.get(position).getMappedAssessmentImage(), holder.card_image);
        } else {
            holder.card_questionimage.setVisibility(View.GONE);
            holder.card_image_gif.setVisibility(View.GONE);
            holder.card_image.setVisibility(View.VISIBLE);
            Picasso.get().load(R.drawable.assessment_thumbnail).into(holder.card_image);
        }
//        holder.card_image.setVisibility(View.VISIBLE);


        if (holder.card_text.getText().toString().length() > 0) {
            holder.card_text.setVisibility(View.VISIBLE);
            holder.shimmerTextView.setVisibility(View.GONE);
        } else {
            holder.card_text.setVisibility(View.GONE);
            holder.shimmerTextView.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (!isCardCompleted) {
                if (isCourseCompleted)
                    gotoAssessment(assessmentNavModels.get(position), v.getContext());
                else
                    OustSdkTools.showToast("You need to complete previous card before accessing this one");
            } else {
                gotoAssessment(assessmentNavModels.get(position), v.getContext());
            }
        });

    }

    @Override
    public int getItemCount() {
        return assessmentNavModels.size();
    }

    public void notifyDateChanges(List<AssessmentNavModel> assessmentNavModels) {
        this.assessmentNavModels = assessmentNavModels;
        notifyDataSetChanged();
    }

    public void notifyDateChanges(List<AssessmentNavModel> assessmentNavModels, boolean isCourseCompleted, List<DTOUserCardData> realmUserCarddataList, List<DTOUserCardData> userCardDataList, boolean isSurveyMandatory) {
        this.assessmentNavModels = assessmentNavModels;
        this.isCourseCompleted = isCourseCompleted;
        this.realmUserCarddataList = realmUserCarddataList;
        this.userCardDataList = userCardDataList;
        this.isSurveyMandatory = isSurveyMandatory;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView card_text;
        private ImageView card_videoicon, card_questionimage, card_image;
        private GifImageView card_image_gif;
        private RelativeLayout card_mainrow;
        private ImageView imageViewLockOpen;
        private ShimmerFrameLayout shimmerTextView;
        private LinearLayout layout_card_background;

        MyViewHolder(View view) {
            super(view);
            card_text = view.findViewById(R.id.card_text);
            card_image_gif = view.findViewById(R.id.card_image_gif);
            card_image = view.findViewById(R.id.card_image);
            card_videoicon = view.findViewById(R.id.card_videoicon);
            card_mainrow = view.findViewById(R.id.card_mainrow);
            card_questionimage = view.findViewById(R.id.card_questionimage);
            imageViewLockOpen = view.findViewById(R.id.imageViewLockOpen);
            shimmerTextView = view.findViewById(R.id.shimmerTextView);
            OustSdkTools.setImage(card_videoicon, OustSdkApplication.getContext().getResources().getString(R.string.challenge));

            layout_card_background = view.findViewById(R.id.layout_card_background);

        }
    }

    public void setImage(String fileName, ImageView imageView) {
        try {
            imageView.setVisibility(View.VISIBLE);
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            String audStr = enternalPrivateStorage.readSavedData(fileName);
//            if ((audStr != null) && (!audStr.isEmpty())) {
//                byte[] imageByte = Base64.decode(audStr, 0);
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 4;
//                InputStream input = new ByteArrayInputStream(imageByte);
//                Bitmap decodedByte = BitmapFactory.decodeStream(input, null, options);
//                imageView.setImageBitmap(decodedByte);
//                imageView.setVisibility(View.VISIBLE);
//            }
            File file = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
            if (file != null && file.exists()) {
                Picasso.get().load(file).into(imageView);
            } else {
                Picasso.get().load(fileName).into(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoAssessment(AssessmentNavModel assessmentNavModel, Context context) {
        Intent intent;
        if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
            intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
        } else {
            intent = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
        }
        intent.putExtra("courseId", ("" + assessmentNavModel.getCurrentLearningPathId()));
        if ((assessmentNavModel.getCourseColnId() != null) && (!assessmentNavModel.getCourseColnId().isEmpty())) {
            intent.putExtra("courseColnId", assessmentNavModel.getCourseColnId());
        }
        intent.putExtra("IS_FROM_COURSE", true);
        intent.putExtra("assessmentId", ("" + assessmentNavModel.getMappedAssessmentId()));
//            Gson gson = new Gson();
//            intent.putExtra("ActiveGame", gson.toJson(activeGame));
        intent.putExtra("containCertificate", assessmentNavModel.isCertificate());
        context.startActivity(intent);
    }


    private boolean getCardCompletedData(CourseCardClass courseCardClass) {
        boolean cardCompleted = false;
        try {
            if (userCardDataList != null) {
                for (DTOUserCardData userCardData1 : userCardDataList) {
                    Log.d("if check", "userdata:" + userCardData1.getCardId() + " -- courseData:" + courseCardClass.getCardId());
                    if (new Long(userCardData1.getCardId()).equals(new Long(courseCardClass.getCardId()))) {
                        cardCompleted = userCardData1.isCardCompleted();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("Cardadapter", "" + e.getMessage());
        }
        return (cardCompleted);
    }

    private boolean getRealmCardCompletedData(CourseCardClass courseCardClass) {
        boolean cardCompleted = false;
        try {
            if (realmUserCarddataList != null) {
                for (DTOUserCardData userCardData1 : realmUserCarddataList) {
                    Log.d("if check", "userdata:" + userCardData1.getCardId() + " -- courseData:" + courseCardClass.getCardId());
                    if (new Long(userCardData1.getCardId()).equals(new Long(courseCardClass.getCardId()))) {
                        cardCompleted = userCardData1.isCardCompleted();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("Cardadapter", "" + e.getMessage());
            //userCardData = new RealmUserCarddata();
        }
        return (cardCompleted);
    }
}
