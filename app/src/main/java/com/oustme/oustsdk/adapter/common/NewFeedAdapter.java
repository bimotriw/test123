package com.oustme.oustsdk.adapter.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.PopupActivity;
import com.oustme.oustsdk.firebase.common.FeedType;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.interfaces.common.NewsFeedClickCallback;
import com.oustme.oustsdk.interfaces.course.DialogKeyListener;
import com.oustme.oustsdk.response.common.OustPopupButton;
import com.oustme.oustsdk.response.common.OustPopupCategory;
import com.oustme.oustsdk.response.common.OustPopupType;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.OustTagHandlerNew;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class NewFeedAdapter  extends RecyclerView.Adapter<NewFeedAdapter.MyViewHolder> implements DialogKeyListener {

    private static final String TAG = "NewFeedAdapter";
    private ActiveUser activeUser;
    private List<DTONewFeed> feeds=new ArrayList<>();
    private Context cx;
    private Activity activity;
    private boolean clickOnRow=false;

    private NewsFeedClickCallback newsFeedClickCallback;

    public void setNewsFeedClickCallback(NewsFeedClickCallback newsFeedClickCallback) {
        this.newsFeedClickCallback = newsFeedClickCallback;
    }

    public void  SetNewFeedAdapter(Context cx, List<DTONewFeed> feed, ActiveUser activeUser, Activity activity) {
        this.activeUser = activeUser;
        this.feeds=feed;
        this.cx=cx;
        this.activity=activity;
        clickOnRow=false;

    }

    public void notifyFeedChange(List<DTONewFeed> feed){
        this.feeds=feed;
        clickOnRow=false;
        notifyDataSetChanged();
    }
    public void enableButton(){
        clickOnRow=false;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (feeds == null) {
            return 0;
        }
        return feeds.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView newsfeedDescriptions,newsfeddHeader,newsFeedDate;
        public TextView newfeedBtn;
        public RelativeLayout newrow_btnlayout,imagelayout;
        public ImageView imgAvatar,feedimage;

        public TextView cardTitle,card_description;
        public ImageView card_videoicon,card_questionimage,grayoverlay_forvideo_bgd;
        public GifImageView card_image;
        public LinearLayout card_mainrow;
        public RelativeLayout grayoverlay_forvideo,card_text_layout;

        public MyViewHolder(View view) {
            super(view);
            newsfeedDescriptions = view.findViewById(R.id.newsrow_content);
            newsfeddHeader= view.findViewById(R.id.newsrow_header);
            newsFeedDate= view.findViewById(R.id.newsrow_date);
            imgAvatar = view.findViewById(R.id.newsrow_icon);
            newfeedBtn= view.findViewById(R.id.newsrow_btn);
            newrow_btnlayout= view.findViewById(R.id.newrow_btnlayout);
            feedimage= view.findViewById(R.id.feedimage);
            imagelayout= view.findViewById(R.id.imagelayout);

            card_image= view.findViewById(R.id.card_image);
            card_videoicon= view.findViewById(R.id.card_videoicon);
            card_mainrow= view.findViewById(R.id.feed_card_mainrow);
            grayoverlay_forvideo= view.findViewById(R.id.grayoverlay_forvideo);
            grayoverlay_forvideo_bgd= view.findViewById(R.id.grayoverlay_forvideo_bgd);
            card_text_layout= view.findViewById(R.id.card_text_layout);
            cardTitle= view.findViewById(R.id.cardTitle);
            card_description= view.findViewById(R.id.card_description);

            OustSdkTools.setImage(card_videoicon, OustSdkApplication.getContext().getResources().getString(R.string.challenge));
            OustSdkTools.setImage(grayoverlay_forvideo_bgd, OustSdkApplication.getContext().getResources().getString(R.string.feed_default));
            OustSdkTools.setImage(feedimage, OustSdkApplication.getContext().getResources().getString(R.string.mydesk));
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsfeed_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {

            Spanned headerText = Html.fromHtml(feeds.get(position).getHeader());
            holder.newsfeddHeader.setText(headerText);
            holder.card_mainrow.setVisibility(View.GONE);
            setFriendsAvatar(position, holder.imgAvatar);

            holder.card_description.setText("");
            holder.newsfeedDescriptions.setText("");

            holder.newfeedBtn.setTextColor(OustSdkTools.getColorBack(R.color.Black));
            OustSdkTools.setLayoutBackgroudDrawable(holder.newfeedBtn,OustSdkTools.getImgDrawable(R.drawable.roundedblack_cornerb));
            if (feeds.get(position).getFeedType() != null) {
                if(feeds.get(position).getFeedType() == FeedType.GAMELET_WORDJUMBLE||
                        feeds.get(position).getFeedType() == FeedType.GAMELET_WORDJUMBLE_V2||
                        feeds.get(position).getFeedType() == FeedType.GAMELET_WORDJUMBLE_V3) {
                    holder.newfeedBtn.setTextColor(OustSdkTools.getColorBack(R.color.White));
                    OustSdkTools.setLayoutBackgroudDrawable(holder.newfeedBtn,OustSdkTools.getImgDrawable(R.drawable.rounded_backcornera));
//                    holder.newfeedBtn.setBackground(OustSdkTools.getImgDrawable(R.drawable.rounded_backcornera));
                }
            }
            if (feeds.get(position).getBtntext() != null && !feeds.get(position).getBtntext().isEmpty()) {
                if(feeds.get(position).getContent()!=null) {
                    Log.d(TAG, "onBindViewHolder: "+feeds.get(position).getContent());
                    holder.newsfeedDescriptions.setText(Html.fromHtml(feeds.get(position).getContent() + " <a href=\"" + feeds.get(position).getLink() + "/\">" + feeds.get(position).getBtntext() + "</a>", null, new OustTagHandlerNew()));
                }else{
                    Log.d(TAG, "onBindViewHolder: without content: ");
                    holder.newsfeedDescriptions.setText(Html.fromHtml( " <a href=\"" + feeds.get(position).getLink() + "/\">" + feeds.get(position).getBtntext() + "</a>", null, new OustTagHandlerNew()));

                }
            } else {
                if (feeds.get(position).getContent() != null) {
                    holder.newsfeedDescriptions.setText(Html.fromHtml(feeds.get(position).getContent(), null, new OustTagHandlerNew()));
                }
            }
            holder.newsfeedDescriptions.setLinkTextColor(OustSdkTools.getColorBack(R.color.Blue));
            holder.newsfeedDescriptions.setMovementMethod(new TextViewLinkHandler() {
                @Override
                public void onLinkClick(String mUrl) {
                    if (!OustSdkTools.checkInternetStatus()) {
                        return;
                    }
                    if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
                        mUrl = "http://" + mUrl;
                    }
                    newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                    newsFeedClickCallback.onnewsfeedClick(mUrl);
                }
            });
            if (feeds.get(position).getTimestamp() != 0) {
                holder.newsFeedDate.setText(OustSdkTools.getDate(""+feeds.get(position).getTimestamp()));
            }
            if (feeds.get(position).getFeedType() != null && feeds.get(position).getFeedType() == FeedType.COURSE_CARD_L) {
                final DTOCourseCard courseCardClass = feeds.get(position).getCourseCardClass();
                holder.newrow_btnlayout.setVisibility(View.GONE);
                holder.card_image.setVisibility(View.GONE);
                holder.card_image.setImageBitmap(null);
                holder.card_videoicon.setVisibility(View.GONE);
                if ((courseCardClass != null) && (courseCardClass.getCardType() != null)) {
                    if ((courseCardClass.getCardType().equalsIgnoreCase("LEARNING")) || (courseCardClass.getCardType().equalsIgnoreCase("SCORM"))) {
                        holder.card_mainrow.setVisibility(View.VISIBLE);
                        holder.imagelayout.setVisibility(View.GONE);
                        holder.card_description.setVisibility(View.GONE);
                        if ((courseCardClass != null) && (courseCardClass.getCardTitle() != null)) {
                            OustSdkTools.getSpannedContent(courseCardClass.getCardTitle(), holder.cardTitle);
                        }
                        if ((courseCardClass != null) && (courseCardClass.getContent() != null)) {
                            holder.card_description.setVisibility(View.VISIBLE);
                            OustSdkTools.getSpannedContent(courseCardClass.getContent(), holder.card_description);
                        }
                        if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null) && (courseCardClass.getCardMedia().size()>0)) {
                            for (int i = 0; i < courseCardClass.getCardMedia().size(); i++) {
                                DTOCourseCardMedia courseCardMedia = courseCardClass.getCardMedia().get(i);
                                if ((courseCardMedia != null) && (courseCardMedia.getMediaType() != null)) {
                                    switch (courseCardMedia.getMediaType()) {
                                        case "GIF":
                                            if ((feeds.get(position).getImageUrl() != null) && (!feeds.get(position).getImageUrl().isEmpty())) {
                                                setThumbnailImage(feeds.get(position).getImageUrl(),holder.card_image);
                                            } else {
                                                setGifImage("oustlearn_" + courseCardMedia.getData(), holder.card_image);
                                            }
                                            break;
                                        case "IMAGE":
                                            holder.card_description.setVisibility(View.GONE);
                                            if ((feeds.get(position).getImageUrl() != null) && (!feeds.get(position).getImageUrl().isEmpty())) {
                                                setThumbnailImage(feeds.get(position).getImageUrl(),holder.card_image);
                                            } else {
                                                setThumbnailImage(feeds.get(position).getTempSignedImage(), holder.card_image);
                                            }
                                            break;
                                        case "VIDEO":
                                            holder.card_description.setVisibility(View.GONE);
                                            holder.card_videoicon.setVisibility(View.VISIBLE);
                                            if ((feeds.get(position).getImageUrl() != null) && (!feeds.get(position).getImageUrl().isEmpty())) {
                                                setThumbnailImage(feeds.get(position).getImageUrl(),holder.card_image);
                                            }else if ((courseCardMedia.getMediaThumbnail() != null) && (!courseCardMedia.getMediaThumbnail().isEmpty())) {
                                                setThumbnailImage(courseCardMedia.getMediaThumbnail(), holder.card_image);
                                            } else {
                                                holder.card_image.setVisibility(View.VISIBLE);
                                                OustSdkTools.setImage(holder.card_image,OustSdkApplication.getContext().getString(R.string.feed_default));
                                            }
                                            break;
                                        case "YOUTUBE_VIDEO":
                                            holder.card_description.setVisibility(View.GONE);
                                            holder.card_videoicon.setVisibility(View.VISIBLE);
                                            String youtubeKey = courseCardMedia.getData();

                                            if (youtubeKey.contains("https://www.youtube.com/watch?v=")) {
                                                youtubeKey = youtubeKey.replace("https://www.youtube.com/watch?v=", "");
                                            }
                                            if (youtubeKey.contains("https://youtu.be/")) {
                                                youtubeKey = youtubeKey.replace("https://youtu.be/", "");
                                            }
                                            if (youtubeKey.contains("&")) {
                                                int state = youtubeKey.indexOf("&");
                                                youtubeKey = youtubeKey.substring(0, state);
                                            }

                                            if ((feeds.get(position).getImageUrl() != null) && (!feeds.get(position).getImageUrl().isEmpty())) {
                                                setThumbnailImage(feeds.get(position).getImageUrl(),holder.card_image);
                                            } else {
                                                String imagePath = "http://img.youtube.com/vi/" + youtubeKey + "/default.jpg";
                                                setThumbnailImage(imagePath, holder.card_image);
                                            }
                                            break;

                                    }
                                }
                            }
                        } else {
                            if ((feeds.get(position).getImageUrl() != null) && (!feeds.get(position).getImageUrl().isEmpty())) {
                                setThumbnailImage(feeds.get(position).getImageUrl(),holder.card_image);
                            }
                        }
                    }
                }
                holder.card_mainrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!clickOnRow) {
                            newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                            clickOnRow=true;
                            newsFeedClickCallback.clickOnCard(courseCardClass);
                        }
                    }
                });
            } else {
                holder.card_mainrow.setVisibility(View.GONE);
                if ((feeds.get(position).getImageUrl() != null) && (!feeds.get(position).getImageUrl().isEmpty())) {
                    holder.imagelayout.setVisibility(View.VISIBLE);
                    holder.feedimage.setImageBitmap(null);
                    if (OustSdkTools.checkInternetStatus()) {
                        Picasso.get().load(feeds.get(position).getImageUrl()).placeholder(R.drawable.roundedcornergraysolid).into(holder.feedimage);
                    } else {
                        Picasso.get().load(feeds.get(position).getImageUrl()).placeholder(R.drawable.roundedcornergraysolid)
                                .networkPolicy(NetworkPolicy.OFFLINE).into(holder.feedimage);
                    }
                } else {
                    if ((feeds.get(position).getFeedType() != null) && (feeds.get(position).getFeedType() == FeedType.SURVEY)) {
                        OustSdkTools.setImage(holder.feedimage,OustSdkApplication.getContext().getResources().getString(R.string.survey_defaulticon));
                        holder.imagelayout.setVisibility(View.VISIBLE);
                    } else {
                        holder.imagelayout.setVisibility(View.GONE);
                    }
                    holder.imagelayout.setVisibility(View.GONE);
                }

                holder.feedimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if(!clickOnRow) {
                                if (feeds.get(position).getFeedType() == FeedType.ASSESSMENT_PLAY) {
                                    newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                    clickOnRow=true;
                                    newsFeedClickCallback.gotoAssessment("" + feeds.get(position).getAssessmentId());
                                } else if (feeds.get(position).getFeedType() == FeedType.COURSE_UPDATE) {
                                    newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                    clickOnRow=true;
                                    newsFeedClickCallback.gotoCourse("" + feeds.get(position).getCourseId());
                                } else if (feeds.get(position).getFeedType() == FeedType.SURVEY) {
                                    newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                    clickOnRow=true;
                                    newsFeedClickCallback.gotoSurvey("" + feeds.get(position).getAssessmentId(), feeds.get(position).getHeader());
                                } else if (feeds.get(position).getFeedType() == FeedType.GAMELET_WORDJUMBLE
                                        || (feeds.get(position).getFeedType() == FeedType.GAMELET_WORDJUMBLE_V2)
                                        || (feeds.get(position).getFeedType() == FeedType.GAMELET_WORDJUMBLE_V3)) {
                                    newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                    clickOnRow=true;
                                    String feedType= String.valueOf(feeds.get(position).getFeedType());
                                    newsFeedClickCallback.gotoGamelet("" + feeds.get(position).getAssessmentId(),feedType);
                                }else if (feeds.get(position).getFeedType() == FeedType.JOIN_MEETING) {
                                    newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                    newsFeedClickCallback.joinMeeting("" + feeds.get(position).getId());
                                } else {
                                    newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                    changeToUnspecifiedMode();
                                    OustSdkTools.gifZoomPopup(holder.feedimage.getDrawable(), activity, NewFeedAdapter.this);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                });

                if (feeds.get(position).getNewBtnText() != null && !feeds.get(position).getNewBtnText().isEmpty()
                        && (feeds.get(position).getFeedType() != FeedType.COURSE_CARD_L)) {
                    holder.newfeedBtn.setText(feeds.get(position).getNewBtnText());
                    holder.newrow_btnlayout.setVisibility(View.VISIBLE);
                } else {
                    holder.newrow_btnlayout.setVisibility(View.GONE);
                }

                holder.newfeedBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OustSdkTools.oustTouchEffect(view, 100);
                        if (feeds.get(position).getFeedType() == null) {
                            showUpdatePopup();
                            return;
                        }
                        Log.e("Feed", "inside click on button" );
                        if(!clickOnRow) {
                            Log.e("Feed", "clickOnRow is false" );
                            if((feeds.get(position).getFeedType()!=null)) {
                                Log.e("Feed", "getFeedType()!=null" );
                                Log.e("Feed", "getFeedType() " + feeds.get(position).getFeedType() );
                                switch (feeds.get(position).getFeedType()) {
                                    case DOUBLE_REFERRAL:
                                        newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                        newsFeedClickCallback.doubleReferalPopup();
                                        break;
                                    case CONTENT_UPDATE:
                                        newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                        newsFeedClickCallback.moduleUnlock(feeds.get(position).getModuleId());
                                        break;
                                    case EVENT_UPDATE:
                                        newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                        newsFeedClickCallback.gototEvent(feeds.get(position).getEventItd());
                                        break;
                                    case APP_UPGRADE:
                                        newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                        clickOnRow = true;
                                        newsFeedClickCallback.rateApp();
                                        break;
                                    case GROUP_UPDATE:
                                        newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                        newsFeedClickCallback.gotoGroupPage(feeds.get(position).getGroupId());
                                        break;
                                    case ASSESSMENT_PLAY:
                                        Log.e("Feed", "ASSESSMENT_PLAY" );
                                        Log.e("Feed", "getAssessmentId() " + feeds.get(position).getAssessmentId() );
                                        newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                        clickOnRow = true;
                                        newsFeedClickCallback.gotoAssessment("" + feeds.get(position).getAssessmentId());
                                        break;
                                    case COURSE_UPDATE:
                                        newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                        clickOnRow = true;
                                        newsFeedClickCallback.gotoCourse("" + feeds.get(position).getCourseId());
                                        break;
                                    case SURVEY:
                                        newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                        clickOnRow = true;
                                        newsFeedClickCallback.gotoSurvey("" + feeds.get(position).getAssessmentId(), feeds.get(position).getHeader());
                                        OustAppState.getInstance().setCurrentSurveyFeed(feeds.get(position));
                                        break;
                                    case GAMELET_WORDJUMBLE:
                                    case GAMELET_WORDJUMBLE_V2:
                                    case GAMELET_WORDJUMBLE_V3:
                                        newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                        clickOnRow = true;
                                        String feedType= String.valueOf(feeds.get(position).getFeedType());
                                        newsFeedClickCallback.gotoGamelet("" + feeds.get(position).getAssessmentId(),feedType);
                                        break;
                                    case JOIN_MEETING:
                                        newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                        clickOnRow = true;
                                        newsFeedClickCallback.joinMeeting("" + feeds.get(position).getId());
                                        break;
                                    case GENERAL:
                                        newsFeedClickCallback.feedClicked(feeds.get(position).getFeedId());
                                        String mUrl = feeds.get(position).getLink();
                                        if ((mUrl != null) && (!mUrl.isEmpty())) {
                                            if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
                                                mUrl = "http://" + mUrl;
                                            }
                                            newsFeedClickCallback.onnewsfeedClick(mUrl);
                                        }
                                        break;
                                }
                            } else {
                                Log.e("Feed", "getFeedType()==null" );
                            }
                        }else {
                            Log.e("Feed", "clickOnRow is already true" );
                        }
                    }
                });
            }
        }catch (Exception e){
            Log.e("Feed", "caught exception" + e.toString() );
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setFriendsAvatar(int position, ImageView imgAvatar) {
        try {
            imgAvatar.setVisibility(View.VISIBLE);
            if ((feeds.get(position).getIcon()==null)||((feeds.get(position)!=null)&&(feeds.get(position).getIcon().isEmpty()))) {
                imgAvatar.setImageResource(R.drawable.app_icon);
            } else {
                if(OustSdkTools.checkInternetStatus()){
                    Picasso.get().load(feeds.get(position).getIcon()).placeholder(R.drawable.app_icon).into(imgAvatar);
                }else {
                    Picasso.get().load(feeds.get(position).getIcon()).placeholder(R.drawable.app_icon)
                            .networkPolicy(NetworkPolicy.OFFLINE).into(imgAvatar);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public abstract class TextViewLinkHandler extends LinkMovementMethod {
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            if (event.getAction() != MotionEvent.ACTION_UP)
                return super.onTouchEvent(widget, buffer, event);
            int x = (int) event.getX();
            int y = (int) event.getY();
            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
            if (link.length != 0) {
                onLinkClick(link[0].getURL());
            }
            return true;
        }
        abstract public void onLinkClick(String url);
    }

    private void showUpdatePopup(){
        try {
            if(!clickOnRow) {
                clickOnRow=true;
                Popup popup = new Popup();
                popup.setHeader(OustStrings.getString("updatepopup_title"));
                popup.setContent(OustStrings.getString("updatepopup_content"));
                popup.setCategory(OustPopupCategory.REDIRECT);
                popup.setType(OustPopupType.APP_UPGRADE);

                List<OustPopupButton> buttons = new ArrayList<>();
                OustPopupButton btn1 = new OustPopupButton();
                btn1.setBtnText(OustStrings.getString("updatepopup_btntext"));
                buttons.add(btn1);
                popup.setButtons(buttons);

                OustStaticVariableHandling.getInstance().setOustpopup(popup);
                Intent intent = new Intent(OustSdkApplication.getContext(), PopupActivity.class);
                Gson gson = new GsonBuilder().create();
                intent.putExtra("ActiveUser", gson.toJson(activeUser));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cx.startActivity(intent);
            }
        }catch (Exception e){}
    }

    public void setThumbnailImage(String imagePath,ImageView imageView){
        try {
            imageView.setVisibility(View.VISIBLE);
            if((imagePath!=null)&&(!imagePath.isEmpty())){
                if((OustSdkTools.checkInternetStatus())&&(OustStaticVariableHandling.getInstance().isNetConnectionAvailable())){
                    Picasso.get().load(imagePath).placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).into(imageView);
                }else {
                    Picasso.get().load(imagePath).placeholder(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).error(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).networkPolicy(NetworkPolicy.OFFLINE).into(imageView);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onDialogClose() {
        //Toast.makeText(getActivity(), "dialog cancel", Toast.LENGTH_SHORT).show();
        changeToPortraitMode();
    }
    public void changeToPortraitMode() {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    public void changeToUnspecifiedMode() {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    public void setGifImage(String filename, GifImageView imageView){
        try {
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            imageView.setVisibility(View.VISIBLE);
//            String audStr = enternalPrivateStorage.readSavedData(filename);
//            if ((audStr != null) && (!audStr.isEmpty())) {
//                byte[] imageByte = Base64.decode(audStr, 0);
//                GifDrawable gifFromBytes = new GifDrawable(imageByte);
//                imageView.setImageDrawable(gifFromBytes);
//            }
            File file = new File(OustSdkApplication.getContext().getFilesDir(),filename);
            if(file!=null && file.exists()) {
                Uri uri = Uri.fromFile(file);
                imageView.setImageURI(uri);
                imageView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void onFeedViewedInScroll(int position){
        if (newsFeedClickCallback != null && feeds!=null && feeds.get(position)!=null) {
            Log.d("TAG", "onFeedViewedInScroll: FeediD:"+feeds.get(position).getFeedId()+" --- FeedViewd:"+feeds.get(position).isFeedViewed());
            if(!feeds.get(position).isFeedViewed()) {
                newsFeedClickCallback.feedViewed(feeds.get(position).getFeedId());
            }
        }
    }

}



