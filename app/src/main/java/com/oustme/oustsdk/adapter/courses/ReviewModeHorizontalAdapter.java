package com.oustme.oustsdk.adapter.courses;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.course.CourseCardMedia;
import com.oustme.oustsdk.firebase.course.SearchCourseCard;
import com.oustme.oustsdk.interfaces.course.ReviewModeCallBack;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static android.view.View.GONE;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;

public class ReviewModeHorizontalAdapter extends RecyclerView.Adapter<ReviewModeHorizontalAdapter.MyViewHolder> {
    private List<SearchCourseCard> searchCourseCards;
    private int levelPosition;
    private ReviewModeCallBack reviewModeCallBack;
    private String language;

    public void setReviewModeCallBack(ReviewModeCallBack reviewModeCallBack) {
        this.reviewModeCallBack = reviewModeCallBack;
    }

    public ReviewModeHorizontalAdapter(List<SearchCourseCard> searchCourseCards, int levelPosition, String language) {
        this.searchCourseCards = searchCourseCards;
        this.levelPosition = levelPosition;
        this.language=language;
    }

    public void notifyDateChanges(List<SearchCourseCard> searchCourseCards, int levelPosition) {
        this.searchCourseCards = searchCourseCards;
        this.levelPosition = levelPosition;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return searchCourseCards.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView card_text;
        private ImageView  card_videoicon, card_questionimage ,card_image;
        private GifImageView card_image_gif;
        private RelativeLayout card_mainrow;
        private LinearLayout linearLayoutOverLay;
        private KatexView card_text_maths;

        MyViewHolder(View view) {
            super(view);
            card_text = view.findViewById(R.id.card_text);
            card_text_maths = view.findViewById(R.id.card_text_maths);

            card_image_gif = view.findViewById(R.id.card_image_gif);
            card_image= view.findViewById(R.id.card_image);
            card_videoicon = view.findViewById(R.id.card_videoicon);
            card_mainrow = view.findViewById(R.id.card_mainrow);
            card_questionimage = view.findViewById(R.id.card_questionimage);
          //  linearLayoutOverLay = (LinearLayout)view.findViewById(R.id.linearLayoutOverLay);

            OustSdkTools.setImage(card_videoicon, OustSdkApplication.getContext().getResources().getString(R.string.challenge));
        }
    }

    @Override
    public ReviewModeHorizontalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviewmode_horizontalrowlayout, parent, false);
        return new ReviewModeHorizontalAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReviewModeHorizontalAdapter.MyViewHolder holder, final int position) {
        try {
            DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(((int) (searchCourseCards.get(position).getId())));
            setMediumFont(holder.card_text);
            holder.card_text.setText("");
            holder.card_image.setVisibility(GONE);
            holder.card_image_gif.setVisibility(GONE);
            holder.card_videoicon.setVisibility(GONE);
            holder.card_text_maths.setTextColorString("#333333");
            //holder.linearLayoutOverLay.setVisibility(View.GONE);
            holder.card_videoicon.setImageResource(R.drawable.ic_play_button_grey);
            OustSdkTools.setImage(holder.card_questionimage, OustSdkApplication.getContext().getResources().getString(R.string.question_default));
            if ((courseCardClass != null) && (courseCardClass.getCardType() != null)) {
                if ((courseCardClass.getCardType().equalsIgnoreCase("LEARNING"))) {
                    OustSdkTools.setImage(holder.card_questionimage, OustSdkApplication.getContext().getResources().getString(R.string.information));
                    if ((courseCardClass != null) && (courseCardClass.getCardTitle() != null)) {
                        if(courseCardClass.getCardTitle().contains(KATEX_DELIMITER)){
                            holder.card_text_maths.setText(courseCardClass.getCardTitle());
                            holder.card_text.setVisibility(GONE);
                            holder.card_text_maths.setVisibility(View.VISIBLE);
                        }
                        else {
                            holder.card_text_maths.setVisibility(GONE);
                            holder.card_text.setVisibility(View.VISIBLE);
                            OustSdkTools.getSpannedContent(courseCardClass.getCardTitle(), holder.card_text);
                        }
                    } else if ((courseCardClass != null) && (courseCardClass.getContent() != null)) {
                        if(courseCardClass.getContent().contains(KATEX_DELIMITER)){
                            holder.card_text_maths.setText(courseCardClass.getContent());
                            holder.card_text.setVisibility(GONE);
                            holder.card_text_maths.setVisibility(View.VISIBLE);
                        }
                        else {
                            holder.card_text_maths.setVisibility(GONE);
                            holder.card_text.setVisibility(View.VISIBLE);
                            OustSdkTools.getSpannedContent(courseCardClass.getContent(), holder.card_text);
                        }
                    }
                    if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null)) {
                        for (int i = 0; i < courseCardClass.getCardMedia().size(); i++) {
                            DTOCourseCardMedia courseCardMedia = courseCardClass.getCardMedia().get(i);
                            if ((courseCardMedia != null) && (courseCardMedia.getMediaType() != null)) {
                                String mediatype = courseCardMedia.getMediaType();
                                if ((mediatype.equalsIgnoreCase("GIF")) || (mediatype.equalsIgnoreCase("IMAGE"))) {
                                    if(mediatype.equalsIgnoreCase("GIF")){
                                        setGifImage("oustlearn_" + courseCardMedia.getData(), holder.card_image_gif);
                                    }else {
                                        setImage("oustlearn_" + courseCardMedia.getData(), holder.card_image);
                                    }
                                } else if (mediatype.equalsIgnoreCase("VIDEO")) {
                                    holder.card_videoicon.setVisibility(View.VISIBLE);
                                    if ((courseCardMedia.getMediaThumbnail() != null) && (!courseCardMedia.getMediaThumbnail().isEmpty())) {
                                        setThumbnailImage(courseCardMedia.getMediaThumbnail(), holder.card_image);
                                    } else {
                                        holder.card_image.setVisibility(GONE);
                                    }
                                } else if (mediatype.equalsIgnoreCase("YOUTUBE_VIDEO")) {
                                    holder.card_videoicon.setVisibility(View.VISIBLE);
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
                                    setThumbnailImage(imagePath, holder.card_image);
                                }
                            }
                        }
                    }
                } else if (courseCardClass.getCardType().equalsIgnoreCase("QUESTION")) {
                    OustSdkTools.setImage(holder.card_questionimage, OustSdkApplication.getContext().getResources().getString(R.string.question_default));
                    if ((courseCardClass.getQuestionData() != null) && (courseCardClass.getQuestionData() != null) && (courseCardClass.getQuestionData().getQuestion() != null)) {
                        if(courseCardClass.getQuestionData().getQuestion().contains(KATEX_DELIMITER)){
                            holder.card_text_maths.setText(courseCardClass.getQuestionData().getQuestion());
                            holder.card_text.setVisibility(GONE);
                            holder.card_text_maths.setVisibility(View.VISIBLE);
                        }
                        else {
                            holder.card_text_maths.setVisibility(GONE);
                            holder.card_text.setVisibility(View.VISIBLE);
                            OustSdkTools.getSpannedContent(courseCardClass.getQuestionData().getQuestion(), holder.card_text);
                        }
                    } else {
                        holder.card_text.setText("Question : " + (position + 1));
                    }
                    if ((courseCardClass != null) && (courseCardClass.getQuestionData() != null) && (courseCardClass.getQuestionData().getImage() != null)) {
                        try {
                            String str = courseCardClass.getQuestionData().getImage();
                            if ((str != null) && (!str.isEmpty())) {
                                byte[] imageByte = Base64.decode(str, 0);
                                GifDrawable gifFromBytes = new GifDrawable(imageByte);
                                holder.card_image.setImageDrawable(gifFromBytes);
                                holder.card_image.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            holder.card_image.setVisibility(GONE);
                        }
                    } else {
                        holder.card_image.setVisibility(GONE);
                    }
                }else if (courseCardClass.getCardType().equalsIgnoreCase("SCORM")) {
                    OustSdkTools.setImage(holder.card_questionimage, OustSdkApplication.getContext().getResources().getString(R.string.information));
                    if ((courseCardClass != null) && (courseCardClass.getCardTitle() != null)) {
                        if(courseCardClass.getCardTitle().contains(KATEX_DELIMITER)){
                            holder.card_text_maths.setText(courseCardClass.getCardTitle());
                            holder.card_text.setVisibility(GONE);
                            holder.card_text_maths.setVisibility(View.VISIBLE);
                        }
                        else {
                            holder.card_text_maths.setVisibility(GONE);
                            holder.card_text.setVisibility(View.VISIBLE);
                            OustSdkTools.getSpannedContent(courseCardClass.getCardTitle(), holder.card_text);
                        }
                    } else if ((courseCardClass != null) && (courseCardClass.getContent() != null)) {
                        if(courseCardClass.getContent().contains(KATEX_DELIMITER)){
                            holder.card_text_maths.setText(courseCardClass.getContent());
                            holder.card_text.setVisibility(GONE);
                            holder.card_text_maths.setVisibility(View.VISIBLE);
                        }
                        else {
                            holder.card_text_maths.setVisibility(GONE);
                            holder.card_text.setVisibility(View.VISIBLE);
                            OustSdkTools.getSpannedContent(courseCardClass.getContent(), holder.card_text);
                        }
                    }
                }
            } else {
                SearchCourseCard searchCourseCard = searchCourseCards.get(position);
                if ((searchCourseCard.getCardType() != null)) {
                    if ((searchCourseCard.getCardType().equalsIgnoreCase("LEARNING"))||(searchCourseCard.getCardType().equalsIgnoreCase("SCORM"))) {
                        holder.card_image.setVisibility(GONE);
                        if ((courseCardClass != null) && (searchCourseCard.getName() != null)) {
                            OustSdkTools.getSpannedContent(searchCourseCard.getName(), holder.card_text);
                        } else if ((searchCourseCard != null) && (searchCourseCard.getDescription() != null)) {
                            OustSdkTools.getSpannedContent(searchCourseCard.getDescription(), holder.card_text);
                        }
                        OustSdkTools.setImage(holder.card_questionimage, OustSdkApplication.getContext().getResources().getString(R.string.information));
                    } else if (searchCourseCard.getCardType().equalsIgnoreCase("QUESTION")) {
                        holder.card_image.setVisibility(GONE);
                        OustSdkTools.setImage(holder.card_questionimage, OustSdkApplication.getContext().getResources().getString(R.string.question_default));
                        if ((courseCardClass != null) && (searchCourseCard.getName() != null) && (!searchCourseCard.getName().isEmpty())) {
                            OustSdkTools.getSpannedContent(searchCourseCard.getName(), holder.card_text);
                        } else if ((searchCourseCard != null) && (searchCourseCard.getDescription() != null) && (!searchCourseCard.getDescription().isEmpty())) {
                            OustSdkTools.getSpannedContent(searchCourseCard.getDescription(), holder.card_text);
                        } else {
                            holder.card_text.setText(OustStrings.getString("question_text") + " : " + (position + 1));
                        }
                    }else if (searchCourseCard.getCardType().equalsIgnoreCase("SCORM")) {
                        holder.card_image.setVisibility(GONE);
                        if ((courseCardClass != null) && (searchCourseCard.getName() != null)) {
                            OustSdkTools.getSpannedContent(searchCourseCard.getName(), holder.card_text);
                        } else if ((searchCourseCard != null) && (searchCourseCard.getDescription() != null)) {
                            OustSdkTools.getSpannedContent(searchCourseCard.getDescription(), holder.card_text);
                        }
                        OustSdkTools.setImage(holder.card_questionimage, OustSdkApplication.getContext().getResources().getString(R.string.information));
                    }
                }
            }
            holder.card_mainrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.98f);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.99f);
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
                            reviewModeCallBack.onCardClick(levelPosition, position);
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void setThumbnailImage(String imagePath, ImageView imageView) {
        try {
            imageView.setVisibility(View.VISIBLE);
            if ((imagePath != null) && (!imagePath.isEmpty())) {
                if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                    Picasso.get().load(imagePath).into(imageView);
                } else {
                    Picasso.get().load(imagePath).networkPolicy(NetworkPolicy.OFFLINE).into(imageView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void setImage(String fileName, ImageView imageView) {
        try {
            imageView.setVisibility(View.VISIBLE);
            File file=new File(OustSdkApplication.getContext().getFilesDir(),fileName);
            if(file.exists()){
                Picasso.get().load(file).into(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setMediumFont(TextView tv){
        try {
            if (language == null || (language!=null && language.isEmpty()) ||
                    (language != null && !language.isEmpty() && language.equalsIgnoreCase("en"))) {
                tv.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setGifImage(String filename, GifImageView imageView){
        try {

            File file=new File(OustSdkApplication.getContext().getFilesDir(),filename);
            if(file.exists()){
                Uri uri=Uri.fromFile(file);
                imageView.setImageURI(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }
}
