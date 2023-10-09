package com.oustme.oustsdk.feed_ui.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.RequestManager;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class FeedsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<DTONewFeed> feedArrayList = new ArrayList<>();
    private ArrayList<Long> removedFeedPosition = new ArrayList<>();
    private Context context;
    static ActiveUser activeUser;
    private RequestManager requestManager;
    private FeedClickListener feedClickListener;
    private final int survey_feed = 1;
    private final int course_feed = 2;
    private final int assessment_feed = 3;
    private final int video_feed = 4;
    private final int youtube_video_feed = 5;
    private boolean isMultipleCpl = false;
    private HashMap<String, String> myDeskMap;

    public void setMyDeskMap(HashMap<String, String> myDeskMap) {
        this.myDeskMap = myDeskMap;
    }

    public void setFeedsRecyclerAdapter(ArrayList<DTONewFeed> feedArrayList, ActiveUser activeUser, RequestManager requestManager, Context context, boolean isMultipleCpl) {

        try {
            this.feedArrayList = feedArrayList;
//            OustStaticVariableHandling.getInstance().setFeeds(feedArrayList);
            FeedsRecyclerAdapter.activeUser = activeUser;
            this.requestManager = requestManager;
            this.context = context;
            this.feedClickListener = (FeedClickListener) context;
            this.isMultipleCpl = isMultipleCpl;
            setHasStableIds(true);

            if (context != null) {
                OustSdkTools.setLocale(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case survey_feed:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_ui_survey_adapter, parent, false);
                SurveyViewHolder surveyViewHolder = new SurveyViewHolder(v);
                surveyViewHolder.setIsRecyclable(false);
                return surveyViewHolder;
            case video_feed:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_ui_video_adapter, parent, false);
                FeedCardVideoViewHolder feedCardVideoViewHolder = new FeedCardVideoViewHolder(v);
                feedCardVideoViewHolder.setIsRecyclable(false);
                return new FeedCardVideoViewHolder(v);
            case youtube_video_feed:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_ui_public_video_adapter, parent, false);
                FeedPublicVideoHolder feedPublicVideoViewHolder = new FeedPublicVideoHolder(v);
                feedPublicVideoViewHolder.setIsRecyclable(false);
                return new FeedPublicVideoHolder(v);
            case assessment_feed:
            case course_feed:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_ui_assessment_adapter, parent, false);
                AssessmentViewHolder assessmentViewHolder = new AssessmentViewHolder(v);
                assessmentViewHolder.setIsRecyclable(false);
                return new AssessmentViewHolder(v);
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_demo_ui_recycler, parent, false);
                StandardViewHolder standardViewHolder = new StandardViewHolder(v);
                standardViewHolder.setIsRecyclable(false);
                return new StandardViewHolder(v);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        DTONewFeed feed = feedArrayList.get(position);

        try {
            if (feed.isNewFeedNotNull(feed)) {
                if (getItemViewType(position) == survey_feed) {

                    ((SurveyViewHolder) holder).setFeedDetails(feed, context, activeUser, feedClickListener);

                } else if (getItemViewType(position) == assessment_feed || getItemViewType(position) == course_feed) {

                    ((AssessmentViewHolder) holder).setMyDeskMap(myDeskMap);
                    ((AssessmentViewHolder) holder).setFeedDetails(feed, context, activeUser, feedClickListener);

                } else if (getItemViewType(position) == video_feed) {

                    ((FeedCardVideoViewHolder) holder).setFeedDetails(feed, context, requestManager, activeUser, feedClickListener);

                } else if (getItemViewType(position) == youtube_video_feed) {

                    ((FeedPublicVideoHolder) holder).setFeedDetails(feed, context, activeUser, feedClickListener);

                } else {

                    ((StandardViewHolder) holder).setMyDeskMap(myDeskMap);
                    ((StandardViewHolder) holder).setFeedDetails(feed, context, activeUser, feedClickListener,isMultipleCpl);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }


    @Override
    public int getItemCount() {
        if (feedArrayList == null) {
            return 0;
        }
        return feedArrayList.size();
        // return feedArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int general_feed = 0;
        switch (feedArrayList.get(position).getFeedType()) {
            case SURVEY:
                return survey_feed;
            case COURSE_UPDATE:
                return course_feed;
            case ASSESSMENT_PLAY:
                return assessment_feed;
            case COURSE_CARD_L:
                if (feedArrayList.get(position).getCourseCardClass() != null) {
                    if (feedArrayList.get(position).getCourseCardClass().getCardMedia() != null && feedArrayList.get(position).getCourseCardClass().getCardMedia().size() != 0) {
                        if (feedArrayList.get(position).getCourseCardClass().getCardMedia().get(0).getMediaType() != null && !feedArrayList.get(position).getCourseCardClass().getCardMedia().get(0).getMediaType().isEmpty()) {
                            String mediaType = feedArrayList.get(position).getCourseCardClass().getCardMedia().get(0).getMediaType();

                            if (mediaType.equalsIgnoreCase("IMAGE") || mediaType.equalsIgnoreCase("GIF") || mediaType.equalsIgnoreCase("AUDIO")) {
                                return general_feed;
                            } else {
                                if (feedArrayList.get(position).getCourseCardClass().getCardMedia().get(0).getMediaPrivacy() != null && feedArrayList.get(position).getCourseCardClass().getCardMedia().get(0).getMediaPrivacy().equals("PUBLIC")) {
                                    return youtube_video_feed;
                                } else {
                                    return video_feed;
                                }
                            }
                        }

                    } else {
                        return general_feed;
                    }
                } else {
                    return general_feed;
                }

            default:
                return general_feed;

        }
    }

    public void removedFeedPosition(ArrayList<Long> removedFeedPosition) {

        this.removedFeedPosition = removedFeedPosition;
    }

    public void notifyFeedChange(ArrayList<DTONewFeed> feed, boolean isSortFeed) {
        // this.feeds = feed;
        int filterType = OustPreferences.getSavedInt("filterTypeFeed");
        if (filterType > 0) {
            feedArrayList = feed;
        } else {
//            feedArrayList = OustStaticVariableHandling.getInstance().getFeeds();
            if (removedFeedPosition != null && removedFeedPosition.size() != 0 && feedArrayList != null && feedArrayList.size() != 0) {
                for (int i = 0; i < removedFeedPosition.size(); i++) {
                    for (int j = 0; j < feedArrayList.size(); j++) {

                        if (removedFeedPosition.get(i) == feedArrayList.get(j).getFeedId()) {
                            feedArrayList.remove(j);
                            break;
                        }

                    }
                }


            }
            assert feedArrayList != null;
            int feedOldSize = feedArrayList.size();
            if (feed != null && feedArrayList.size() != 0) {

                for (int i = 0; i < feedArrayList.size(); i++) {
                    for (int j = 0; j < feed.size(); j++) {

                        if (feedArrayList.get(i).getFeedId() == feed.get(j).getFeedId() || feed.get(j).getFeedId() == 0) {
                            if (feed.get(j).getFeedId() != 0) {
                                feedArrayList.set(i, feed.get(j));
                            }

                            feed.remove(j);
                            break;
                        }
                    }

                }

                if (feed.size() != 0) {
                    feedArrayList.addAll(feed);
                }
            }

            if (isSortFeed) {
                Collections.sort(feedArrayList, DTONewFeed.newsFeedSorter);
            }

            int feedNewSize = feedArrayList.size();
//            OustStaticVariableHandling.getInstance().setFeeds(feedArrayList);

            OustPreferences.saveAppInstallVariable("newFeed", feedNewSize > feedOldSize);

        }


        notifyDataSetChanged();


    }


    public void enableButton() {
        //  notifyDataSetChanged();
    }

    public void onFeedViewedInScroll(int position) {
        if (feedClickListener != null && feedArrayList != null && feedArrayList.get(position) != null) {
            Log.d("TAG", "onFeedViewedInScroll: FeediD:" + feedArrayList.get(position).getFeedId() + " --- FeedViewd:" + feedArrayList.get(position).isFeedViewed());
            if (!feedArrayList.get(position).isFeedViewed()) {
                feedClickListener.onFeedViewed(feedArrayList.get(position).getFeedId());
            }
        }
    }


    //Inside the Adapter class
    @Override
    public long getItemId(int position) {
        return feedArrayList.get(position).getId();
    }

    public void modifyItem(final long id, final long feedComment) {

        int position = find(feedArrayList, id);

        feedArrayList.get(position).setNumComments(feedComment);
        //feedArrayList.get(position).setPlaying(feedComment);
        feedArrayList.set(position, feedArrayList.get(position));
        notifyItemChanged(position);
    }

    public void playVideo(final int position) {

        if ((getItemViewType(position) == video_feed || getItemViewType(position) == youtube_video_feed)) {

            feedArrayList.get(position).setAutoPlay(true);
            feedArrayList.set(position, feedArrayList.get(position));
            if (OustPreferences.getSavedLong("FeedId") != 0) {
                removeVideo(OustPreferences.getSavedLong("FeedId"));
            }
            notifyDataSetChanged();


        } else {
            if (OustPreferences.getSavedLong("FeedId") != 0) {
                removeVideo(OustPreferences.getSavedLong("FeedId"));
                notifyDataSetChanged();
            }

            //  notifyDataSetChanged();

        }


    }

    public void removeVideo(final long id) {

        if (id != 0) {
            int position = find(feedArrayList, id);
            //
            if (position >= 0) {
                Log.d("feedAdapter", id + "-" + position);
                feedArrayList.get(position).setPlaying(true);
                OustPreferences.saveLongVar("FeedId", 0);
                feedArrayList.set(position, feedArrayList.get(position));
                //   notifyItemChanged(position);
            }

        }


    }

    public void removeItem(final long id) {

        if (id != 0) {
            int position = find(feedArrayList, id);
            //
            if (position >= 0) {
                Log.d("feedAdapter", id + "-" + position);
                feedArrayList.remove(position);
//                OustStaticVariableHandling.getInstance().setFeeds(feedArrayList);
                notifyItemRangeRemoved(position, 1);
                notifyDataSetChanged();
            }

        }


    }

    // Generic function to find the index of an element in an object array in Java
    public static int find(ArrayList<DTONewFeed> a, long id) {

        for (int i = 0; i < a.size(); i++)
            if (a.get(i).getFeedId() == id)
                return i;

        return -1;
    }

    public void notifySearchFeedChange(ArrayList<DTONewFeed> newFeedList1, boolean b) {
        feedArrayList = newFeedList1;
        notifyDataSetChanged();
    }
}
