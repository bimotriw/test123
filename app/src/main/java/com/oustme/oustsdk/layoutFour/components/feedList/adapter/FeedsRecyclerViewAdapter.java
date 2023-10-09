package com.oustme.oustsdk.layoutFour.components.feedList.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.RequestManager;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.layoutFour.components.feedList.FeedsViewHolder;
import com.oustme.oustsdk.room.dto.DTOUserFeeds;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.util.ArrayList;

public class FeedsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private ArrayList<DTOUserFeeds.FeedList> feedArrayList = new ArrayList<>();
    private ArrayList<DTOUserFeeds.FeedList> tempFeedArrayList = new ArrayList<>();
    private ArrayList<Long> removedFeedPosition = new ArrayList<>();
    private Filter filter;
    private Context context;
    ActiveUser activeUser;
    RequestManager requestManager;
    FeedClickListener feedClickListener;
    private boolean isMultipleCpl = false;
    RecyclerView rvFeeds;
    RecyclerView.ViewHolder viewHolder;

    public void setFeedsRecyclerAdapter(ActiveUser activeUser, RequestManager initGlide, Context context, FeedClickListener feedClickListener, boolean isMultipleCpl, RecyclerView rvFeeds) {
        try {
            feedArrayList = new ArrayList<>();
            tempFeedArrayList = new ArrayList<>();
            this.activeUser = activeUser;
            this.requestManager = initGlide;
            this.context = context;
            this.feedClickListener = feedClickListener;
            this.isMultipleCpl = isMultipleCpl;
            this.rvFeeds = rvFeeds;
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
        return new FeedsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.feeds_ui_viewholder_2, parent, false), context, rvFeeds);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        viewHolder = holder;
        ((FeedsViewHolder) holder).onBindData(tempFeedArrayList.get(position), context, feedClickListener, rvFeeds, activeUser, isMultipleCpl, tempFeedArrayList);
    }

    @Override
    public int getItemCount() {
        return tempFeedArrayList != null ? tempFeedArrayList.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removedFeedPosition(ArrayList<Long> removedFeedPosition) {
        this.removedFeedPosition = removedFeedPosition;
    }

    public void add(DTOUserFeeds.FeedList feedList) {
        tempFeedArrayList.add(feedList);
        OustStaticVariableHandling.getInstance().setFeeds(tempFeedArrayList);
        notifyItemInserted(tempFeedArrayList.size() - 1);
    }

    public void notifyFeedChange(ArrayList<DTOUserFeeds.FeedList> feed) {
        for (DTOUserFeeds.FeedList result : feed) {
            //Log.d("checkingPagination=>9", "" + tempFeedArrayList.size());
            add(result);
        }
        /*// this.feeds = feed;
        int filterType = OustPreferences.getSavedInt("filterTypeFeed");
        if (filterType > 0) {
            tempFeedArrayList = feed;
        } else {
            tempFeedArrayList = OustStaticVariableHandling.getInstance().getFeeds();
            if (removedFeedPosition != null && removedFeedPosition.size() != 0 && tempFeedArrayList != null && tempFeedArrayList.size() != 0) {
                for (int i = 0; i < removedFeedPosition.size(); i++) {
                    for (int j = 0; j < tempFeedArrayList.size(); j++) {
                        if (removedFeedPosition.get(i) == tempFeedArrayList.get(j).getFeedId()) {
                            tempFeedArrayList.remove(j);
                            break;
                        }
                    }
                }
            }
            assert tempFeedArrayList != null;
            int feedOldSize = tempFeedArrayList.size();
            if (feed != null && tempFeedArrayList.size() != 0) {

                for (int i = 0; i < tempFeedArrayList.size(); i++) {
                    for (int j = 0; j < feed.size(); j++) {

                        if (tempFeedArrayList.get(i).getFeedId() == feed.get(j).getFeedId() || feed.get(j).getFeedId() == 0) {
                            if (feed.get(j).getFeedId() != 0) {
                                tempFeedArrayList.set(i, feed.get(j));
                            }
                            feed.remove(j);
                            break;
                        }
                    }

                }
                if (feed.size() != 0) {
                    tempFeedArrayList.addAll(feed);
                }
            }
            int feedNewSize = tempFeedArrayList.size();
            OustStaticVariableHandling.getInstance().setFeeds(tempFeedArrayList);
            OustPreferences.saveAppInstallVariable("newFeed", feedNewSize > feedOldSize);
        }
        notifyDataSetChanged();*/
    }

    public void modifyItem(final long id, final long feedComment, long feedShareCount, long feedLikeCount, boolean isLikeClicked, boolean isClicked) {
        try {
            int position = find(tempFeedArrayList, id);
            tempFeedArrayList.get(position).setNumComments((int) feedComment);
            tempFeedArrayList.get(position).setNumShares((int) feedShareCount);
            tempFeedArrayList.get(position).setNumLikes(((int) feedLikeCount));
            tempFeedArrayList.get(position).setLiked(isLikeClicked);
            tempFeedArrayList.get(position).setFeedViewed(isClicked);
            tempFeedArrayList.get(position).setClicked(isClicked);
            tempFeedArrayList.set(position, tempFeedArrayList.get(position));
            notifyItemRemoved(position);
            notifyItemInserted(position);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static int find(ArrayList<DTOUserFeeds.FeedList> a, long id) {
        for (int i = 0; i < a.size(); i++)
            if (a.get(i).getFeedId() == id) return i;

        return -1;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FeedFilter();
        }
        return filter;
    }

    public void clear() {
        tempFeedArrayList.clear();
        notifyDataSetChanged();
    }

    public void playVideo(boolean isEndOfList) {
        ((FeedsViewHolder) viewHolder).playVideo(isEndOfList);
    }

    public void releasePlayer() {
        ((FeedsViewHolder) viewHolder).releasePlayer();
    }

    private class FeedFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.e("TAG", "constraint Data: " + constraint);
            FilterResults filterResults = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                filterResults.values = tempFeedArrayList;
                filterResults.count = tempFeedArrayList.size();
            } else {
                ArrayList<DTOUserFeeds.FeedList> list = new ArrayList<>();
                constraint = constraint.toString().toLowerCase();
                if (tempFeedArrayList != null) {
                    for (DTOUserFeeds.FeedList item : tempFeedArrayList) {
                        if (item.getHeader().toLowerCase().contains(constraint.toString())) {
                            list.add(item);
                        }
                        filterResults.count = list.size();
                        filterResults.values = list;
                    }
                }
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            tempFeedArrayList = (ArrayList<DTOUserFeeds.FeedList>) results.values;
            notifyDataSetChanged();
        }
    }

    public void onFeedViewedInScroll(int position) {
        if (feedClickListener != null && tempFeedArrayList != null && tempFeedArrayList.get(position) != null) {
            Log.d("TAG", "onFeedViewedInScroll: FeediD:" + tempFeedArrayList.get(position).getFeedId() + " --- FeedViewd:" + tempFeedArrayList.get(position).isFeedViewed());
            if (!tempFeedArrayList.get(position).isFeedViewed()) {
                feedClickListener.onFeedViewed(tempFeedArrayList.get(position).getFeedId());
            }
        }
    }
}
