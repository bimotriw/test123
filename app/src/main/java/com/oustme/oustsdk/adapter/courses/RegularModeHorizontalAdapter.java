package com.oustme.oustsdk.adapter.courses;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.facebook.shimmer.ShimmerFrameLayout;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.course.SearchCourseCard;
import com.oustme.oustsdk.interfaces.course.ReviewModeCallBack;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.course.UserCardData;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
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

public class RegularModeHorizontalAdapter extends RecyclerView.Adapter<RegularModeHorizontalAdapter.MyViewHolder> {
    private List<SearchCourseCard> searchCourseCards;
    private int levelPosition;
    private ReviewModeCallBack reviewModeCallBack;
    private String language;
    List<DTOUserCardData> dtoUserCardDataList;
    List<DTOUserCardData> userCardDataList;
    Context context;
    private boolean isLevelLock;
    private boolean isSalesMode = false;
    private boolean isCourseCompleted;

    public void setReviewModeCallBack(ReviewModeCallBack reviewModeCallBack) {
        this.reviewModeCallBack = reviewModeCallBack;
    }

    public RegularModeHorizontalAdapter(Context context, List<SearchCourseCard> searchCourseCards, int levelPosition, String language, List<DTOUserCardData> dtoUserCardDataList, boolean isLevelLock, boolean isSalesMode, boolean isCourseCompleted, List<DTOUserCardData> userCardDataList) {
        this.searchCourseCards = searchCourseCards;
        this.levelPosition = levelPosition;
        this.language = language;
        this.dtoUserCardDataList = dtoUserCardDataList;
        this.context = context;
        this.isLevelLock = isLevelLock;
        this.isSalesMode = isSalesMode;
        this.userCardDataList = userCardDataList;
        this.isCourseCompleted = isCourseCompleted;
    }

    public void notifyDateChanges(Context context, List<SearchCourseCard> searchCourseCards, int levelPosition, List<DTOUserCardData> dtoUserCardDataList, boolean isLevelLock, boolean isSalesMode, boolean isCourseCompleted, List<DTOUserCardData> userCardDataList) {
        this.searchCourseCards = searchCourseCards;
        this.levelPosition = levelPosition;
        this.dtoUserCardDataList = dtoUserCardDataList;
        this.userCardDataList = userCardDataList;
        this.context = context;
        this.isLevelLock = isLevelLock;
        this.isSalesMode = isSalesMode;
        this.isCourseCompleted = isCourseCompleted;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return searchCourseCards.size();
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
            card_image= view.findViewById(R.id.card_image);
            card_videoicon = view.findViewById(R.id.card_videoicon);
            card_mainrow = view.findViewById(R.id.card_mainrow);
            card_questionimage = view.findViewById(R.id.card_questionimage);
            imageViewLockOpen = view.findViewById(R.id.imageViewLockOpen);
            shimmerTextView = view.findViewById(R.id.shimmerTextView);
            OustSdkTools.setImage(card_videoicon, OustSdkApplication.getContext().getResources().getString(R.string.challenge));

            layout_card_background = view.findViewById(R.id.layout_card_background);
        }
    }

    @Override
    public RegularModeHorizontalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.regularmode_horizontalrowlayout, parent, false);
        return new RegularModeHorizontalAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RegularModeHorizontalAdapter.MyViewHolder holder, final int position) {
        try {

            DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(((int) (searchCourseCards.get(position).getId())));
            int number_of_attempt = 0, previous_number_of_attempt = 0;
            boolean canOpenCard = false;

            /*int number_of_attempt =0;
            if(dtoUserCardDataList!=null){
                RealmUserCarddata userCardData = getRelamUserCardData(courseCardClass);
                number_of_attempt = (int) userCardData.getNoofAttempt();
            }else{
                UserCardData userCardData = getUserCardData(courseCardClass);
                number_of_attempt = (int) userCardData.getNoofAttempt();
            }*/

            boolean isCardCompleted = false, isPreviousCardCompleted = false;

            isCardCompleted = getRealmCardCompletedData(courseCardClass);
            if (!isCardCompleted && userCardDataList != null) {
                isCardCompleted = getCardCompletedData(courseCardClass);
            }
            Log.d("Cardadapter", "onBindViewHolder: position:" + position + "----cardcompleted:" + isCardCompleted);
            /*number_of_attempt =  getRelamUserCardData(courseCardClass);
            if(number_of_attempt==0 && userCardDataList!=null){
                number_of_attempt = getUserCardData(courseCardClass);
            }*/

            if (position > 0) {
                DTOCourseCard previousCourseCardClass = OustSdkTools.databaseHandler.getCardClass(((int) (searchCourseCards.get(position - 1).getId())));
                isPreviousCardCompleted = getRealmCardCompletedData(previousCourseCardClass);
                if (!isPreviousCardCompleted && userCardDataList != null) {
                    isPreviousCardCompleted = getCardCompletedData(previousCourseCardClass);
                }
                /*previous_number_of_attempt =  getRelamUserCardData(previousCourseCardClass);
                if(previous_number_of_attempt==0 && userCardDataList!=null){
                    previous_number_of_attempt = getUserCardData(previousCourseCardClass);
                }*/
            }

            //Log.d("Cardadapter","Course attempt count:"+userCardData.getNoofAttempt()+" from card Id:"+courseCardClass.getCardId()+"  fromuserCardId:"+userCardData.getCardId()+" -- LevelLock:"+isLevelLock);

            /*if (!isSalesMode &&  (isLevelLock ||  number_of_attempt== 0) && !isCourseCompleted) {*/
            if (!isSalesMode && (isLevelLock || !isCardCompleted) && !isCourseCompleted) {

                /*if ((levelPosition == 0 && position == 0) || (!isLevelLock && position == 0) || (isLevelLock && number_of_attempt>0) || (position>0 && previous_number_of_attempt>0)) {*/
                if ((levelPosition == 0 && position == 0) || (!isLevelLock && position == 0) || (isLevelLock && isCardCompleted) || (position > 0 && isPreviousCardCompleted)) {
                    //holder.card_mainrow.setEnabled(true);
                    //holder.card_mainrow.setClickable(true);
                    canOpenCard = true;

                    /*if(number_of_attempt== 0){*/
                    if (!isCardCompleted) {
                        holder.layout_card_background.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner_orange));
                    } else {
                        holder.imageViewLockOpen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_tick));
                        holder.layout_card_background.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner_plain));
                    }
                } else {
                    canOpenCard = false;
                    //holder.card_mainrow.setClickable(false);
                    //holder.card_mainrow.setEnabled(false);
                    holder.layout_card_background.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner_orange));
                }

            } else {
                canOpenCard = true;
                //holder.card_mainrow.setEnabled(true);
                //holder.card_mainrow.setClickable(true);
                if (!isSalesMode) {
                    holder.imageViewLockOpen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_tick));
                    holder.layout_card_background.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner_plain));
                } else {
                    /*if(number_of_attempt== 0){*/

                    if(!isCardCompleted && !isCourseCompleted){
                        holder.layout_card_background.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner_orange));
                    } else {
                        holder.imageViewLockOpen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_tick));
                        holder.layout_card_background.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner_plain));
                    }
                }
            }

            final boolean isOpenCard = canOpenCard;

            setMediumFont(holder.card_text);
            holder.card_text.setText("");
            holder.card_text.setVisibility(View.GONE);
            holder.shimmerTextView.setVisibility(View.VISIBLE);

            holder.card_image.setVisibility(View.GONE);
            holder.card_image_gif.setVisibility(View.GONE);
            holder.card_videoicon.setVisibility(View.GONE);
            //holder.linearLayoutOverLay.setVisibility(View.GONE);
            holder.card_videoicon.setImageResource(R.drawable.ic_play_button_grey);
            //holder.card_videoicon.setColorFilter();

            OustSdkTools.setImage(holder.card_questionimage, OustSdkApplication.getContext().getResources().getString(R.string.question_default));
            if ((courseCardClass != null) && (courseCardClass.getCardType() != null)) {
                Log.d("RegularCard", "" + courseCardClass.getCardType());
                if ((courseCardClass.getCardType().equalsIgnoreCase("LEARNING"))) {
                    OustSdkTools.setImage(holder.card_questionimage, OustSdkApplication.getContext().getResources().getString(R.string.information));
                    if ((courseCardClass != null) && (courseCardClass.getCardTitle() != null)) {
                        OustSdkTools.getSpannedContent(courseCardClass.getCardTitle(), holder.card_text);
                    /*} else if ((courseCardClass != null) && (courseCardClass.getContent() != null)) {
                        OustSdkTools.getSpannedContent(courseCardClass.getContent(), holder.card_text);*/
                    }
                    if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null)) {
                        for (int i = 0; i < courseCardClass.getCardMedia().size(); i++) {
                            DTOCourseCardMedia courseCardMedia = courseCardClass.getCardMedia().get(i);
                            if ((courseCardMedia != null) && (courseCardMedia.getMediaType() != null)) {
                                String mediatype = courseCardMedia.getMediaType();
                                if ((mediatype.equalsIgnoreCase("GIF")) || (mediatype.equalsIgnoreCase("IMAGE"))) {
                                    if (mediatype.equalsIgnoreCase("GIF")) {
                                        setGifImage("oustlearn_" + courseCardMedia.getData(), holder.card_image_gif);
                                    } else {
                                        setImage("oustlearn_" + courseCardMedia.getData(), holder.card_image);
                                    }
                                } else if (mediatype.equalsIgnoreCase("VIDEO")) {
                                    holder.card_videoicon.setVisibility(View.VISIBLE);
                                    if ((courseCardMedia.getMediaThumbnail() != null) && (!courseCardMedia.getMediaThumbnail().isEmpty())) {
                                        setThumbnailImage(courseCardMedia.getMediaThumbnail(), holder.card_image);
                                    } else {
                                        holder.card_image.setVisibility(View.GONE);
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
                    if ((courseCardClass != null) && (courseCardClass.getQuestionData() != null) && (courseCardClass.getQuestionData().getQuestion() != null) && !courseCardClass.getQuestionData().getQuestion().isEmpty()) {
                        OustSdkTools.getSpannedContent(courseCardClass.getQuestionData().getQuestion(), holder.card_text);
                    } else if (courseCardClass.getQuestionCategory() != null && courseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.HOTSPOT)) {
                        holder.card_text.setText("Hotspot Question");
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
                            holder.card_image.setVisibility(View.GONE);
                        }
                    } else {
                        holder.card_image.setVisibility(View.GONE);
                    }

                } else if (courseCardClass.getCardType().equalsIgnoreCase("SCORM")) {
                    OustSdkTools.setImage(holder.card_questionimage, OustSdkApplication.getContext().getResources().getString(R.string.information));
                    if ((courseCardClass != null) && (courseCardClass.getCardTitle() != null)) {
                        OustSdkTools.getSpannedContent(courseCardClass.getCardTitle(), holder.card_text);
                    /*} else if ((courseCardClass != null) && (courseCardClass.getContent() != null)) {
                        OustSdkTools.getSpannedContent(courseCardClass.getContent(), holder.card_text);*/
                    }
                }
                /*holder.card_text.setVisibility(View.VISIBLE);
                holder.shimmerTextView.setVisibility(View.GONE);*/
            }
            else {
                SearchCourseCard searchCourseCard = searchCourseCards.get(position);
                if ((searchCourseCard.getCardType() != null)) {
                    Log.d("RegularCard", "" + searchCourseCard.getCardType());
                    if ((searchCourseCard.getCardType().equalsIgnoreCase("LEARNING")) || (searchCourseCard.getCardType().equalsIgnoreCase("SCORM"))) {
                        holder.card_image.setVisibility(View.GONE);
                        if ((courseCardClass != null) && (searchCourseCard.getName() != null)) {
                            OustSdkTools.getSpannedContent(searchCourseCard.getName(), holder.card_text);
                        /*} else if ((searchCourseCard != null) && (searchCourseCard.getDescription() != null)) {
                            OustSdkTools.getSpannedContent(searchCourseCard.getDescription(), holder.card_text);*/
                        }
                        OustSdkTools.setImage(holder.card_questionimage, OustSdkApplication.getContext().getResources().getString(R.string.information));
                    } else if (searchCourseCard.getCardType().equalsIgnoreCase("QUESTION")) {
                        holder.card_image.setVisibility(View.GONE);
                        OustSdkTools.setImage(holder.card_questionimage, OustSdkApplication.getContext().getResources().getString(R.string.question_default));
                        if ((courseCardClass != null) && (searchCourseCard.getName() != null) && (!searchCourseCard.getName().isEmpty())) {
                            OustSdkTools.getSpannedContent(searchCourseCard.getName(), holder.card_text);
                        } else if ((searchCourseCard != null) && (searchCourseCard.getDescription() != null) && (!searchCourseCard.getDescription().isEmpty())) {
                            OustSdkTools.getSpannedContent(searchCourseCard.getDescription(), holder.card_text);
                        } else {
                            holder.card_text.setText(OustStrings.getString("question_text") + " : " + (position + 1));
                        }
                    } else if (searchCourseCard.getCardType().equalsIgnoreCase("SCORM")) {
                        holder.card_image.setVisibility(View.GONE);
                        if ((courseCardClass != null) && (searchCourseCard.getName() != null)) {
                            OustSdkTools.getSpannedContent(searchCourseCard.getName(), holder.card_text);
                        /*} else if ((searchCourseCard != null) && (searchCourseCard.getDescription() != null)) {
                            OustSdkTools.getSpannedContent(searchCourseCard.getDescription(), holder.card_text);*/
                        }
                        OustSdkTools.setImage(holder.card_questionimage, OustSdkApplication.getContext().getResources().getString(R.string.information));
                    }
                    /*holder.card_text.setVisibility(View.VISIBLE);
                    holder.shimmerTextView.setVisibility(View.GONE);*/
                }/*else{
                    holder.card_text.setVisibility(View.GONE);
                    holder.shimmerTextView.setVisibility(View.VISIBLE);
                }*/
            }

            if (holder.card_text.getText().toString().length() > 0) {
                holder.card_text.setVisibility(View.VISIBLE);
                holder.shimmerTextView.setVisibility(View.GONE);
            } else {
                holder.card_text.setVisibility(View.GONE);
                holder.shimmerTextView.setVisibility(View.VISIBLE);
            }

            holder.card_mainrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isOpenCard) {
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
            File file=new File(OustSdkApplication.getContext().getFilesDir(),fileName);
            if(file!=null && file.exists()){
                Picasso.get().load(file).into(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setMediumFont(TextView tv) {
        try {
            if (language == null || (language != null && language.isEmpty()) ||
                    (language != null && !language.isEmpty() && language.equalsIgnoreCase("en"))) {
                tv.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setGifImage(String filename, GifImageView imageView) {
        try {
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            imageView.setVisibility(View.VISIBLE);
//            String audStr = enternalPrivateStorage.readSavedData(filename);
//            if ((audStr != null) && (!audStr.isEmpty())) {
//                byte[] imageByte = Base64.decode(audStr, 0);
//                GifDrawable gifFromBytes = new GifDrawable(imageByte);
//                imageView.setImageDrawable(gifFromBytes);
//            }
            File file = new File(OustSdkApplication.getContext().getFilesDir(), filename);
            if (file != null && file.exists()) {
                Uri uri = Uri.fromFile(file);
                imageView.setImageURI(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private int getUserCardData(DTOCourseCard courseCardClass) {
        //UserCardData userCardData = new UserCardData();
        long attempt_count = 0;
        try {
            if (userCardDataList != null) {
                for (DTOUserCardData userCardData1 : userCardDataList) {
                    Log.d("if check", "userdata:" + userCardData1.getCardId() + " -- courseData:" + courseCardClass.getCardId());

                    if (new Long(userCardData1.getCardId()).equals(new Long(courseCardClass.getCardId()))) {
                        if ((int) userCardData1.getNoofAttempt() > 0) {
                            attempt_count = userCardData1.getNoofAttempt();
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("Cardadapter", "" + e.getMessage());
        }
        return ((int) attempt_count);
    }

    private int getRelamUserCardData(DTOCourseCard courseCardClass) {
        //RealmUserCarddata userCardData = new RealmUserCarddata();
        long attempt_count = 0;
        try {
            if (dtoUserCardDataList != null) {
                for (DTOUserCardData userCardData1 : dtoUserCardDataList) {
                    Log.d("if check", "userdata:" + userCardData1.getCardId() + " -- courseData:" + courseCardClass.getCardId());
                    if (new Long(userCardData1.getCardId()).equals(new Long(courseCardClass.getCardId()))) {
                        if ((int) userCardData1.getNoofAttempt() > 0) {
                            attempt_count = userCardData1.getNoofAttempt();
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("Cardadapter", "" + e.getMessage());
            //userCardData = new RealmUserCarddata();
        }
        return ((int) attempt_count);
    }

    private boolean getCardCompletedData(DTOCourseCard courseCardClass) {
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

    private boolean getRealmCardCompletedData(DTOCourseCard courseCardClass) {
        boolean cardCompleted = false;
        try {
            if (dtoUserCardDataList != null) {
                for (DTOUserCardData userCardData1 : dtoUserCardDataList) {
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
