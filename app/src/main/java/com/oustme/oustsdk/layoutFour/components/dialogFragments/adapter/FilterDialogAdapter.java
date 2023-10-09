package com.oustme.oustsdk.layoutFour.components.dialogFragments.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.FilterCategory;
import com.oustme.oustsdk.layoutFour.components.feedList.UserFeedFilters;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.HashMap;


public class FilterDialogAdapter extends RecyclerView.Adapter<FilterDialogAdapter.ViewHolder> {

    private final ArrayList<UserFeedFilters.FeedFilter> filterCategories;
    private final HashMap<Integer, UserFeedFilters.FeedFilter> selectedFilterMap;

    private FeedFilterAdapterCallback callback;

    public FilterDialogAdapter(ArrayList<UserFeedFilters.FeedFilter> filterCategories, ArrayList<UserFeedFilters.FeedFilter> selectedFilterCategories) {
        this.filterCategories = filterCategories;
        selectedFilterMap = new HashMap<>();
        for (UserFeedFilters.FeedFilter filterCategory : selectedFilterCategories) {
            selectedFilterMap.put(filterCategory.getCategoryType(), filterCategory);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_bottom_filter_dialog_list_dialog_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {

            UserFeedFilters.FeedFilter filterCategory = filterCategories.get(position);

            if (filterCategory != null) {
                holder.dialog_option.setText(filterCategories.get(position).getCategoryName());

                setSelection(holder, filterCategory);

                holder.itemView.setOnClickListener(v -> handleClick(position));
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    @Override
    public int getItemCount() {
        return filterCategories == null ? 0 : filterCategories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView dialog_option;
        ImageView dialog_option_chosen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dialog_option = itemView.findViewById(R.id.dialog_option);
            dialog_option_chosen = itemView.findViewById(R.id.dialog_option_chosen);


        }
    }

    public void setCallback(FeedFilterAdapterCallback callback) {
        this.callback = callback;
    }

    public interface FeedFilterAdapterCallback {
        void onItemClicked(ArrayList<UserFeedFilters.FeedFilter> selectedFilter);
    }

    private void setSelection(ViewHolder holder, UserFeedFilters.FeedFilter filterCategory) {
        try {
            if (selectedFilterMap != null) {
                int categoryType = filterCategory.getCategoryType();
                if (selectedFilterMap.containsKey(categoryType)) {
                    holder.dialog_option.setTypeface(null, Typeface.BOLD);
                    holder.dialog_option_chosen.setVisibility(View.VISIBLE);
                } else {
                    holder.dialog_option.setTypeface(null, Typeface.NORMAL);
                    holder.dialog_option_chosen.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.dialog_option.setTypeface(null, Typeface.NORMAL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleClick(int position) {
        try {
            int filterCount = 0;
            if (selectedFilterMap != null) {
                UserFeedFilters.FeedFilter filterCategory = filterCategories.get(position);
                String filterName = filterCategory.getCategoryName();
                int categoryType = filterCategory.getCategoryType();

                if (filterName.equalsIgnoreCase("all")) {
                    if (selectedFilterMap.containsKey(categoryType)) {
                        for (UserFeedFilters.FeedFilter filter : filterCategories)
                            selectedFilterMap.remove(filter.getCategoryType());
                    } else {
                        for (UserFeedFilters.FeedFilter filter : filterCategories)
                            selectedFilterMap.put(filter.getCategoryType(), filter);
                    }
                } else {
                    selectedFilterMap.remove(0);
                    if (selectedFilterMap.containsKey(categoryType)) {
                        selectedFilterMap.remove(categoryType);
                    } else {
                        selectedFilterMap.put(categoryType, filterCategory);
                    }
                    if (selectedFilterMap.size() > 0) {
                        for (UserFeedFilters.FeedFilter filter : filterCategories) {
                            boolean valueFound = false;
                            UserFeedFilters.FeedFilter filterSub = selectedFilterMap.get(filter.getCategoryType());
                            if (filterSub != null) {
                                if (filter.getCategoryType() == filterSub.getCategoryType()) {
                                    valueFound = true;
                                }
                            }
                            if (valueFound) {
                                filterCount++;
                            }
                        }
                        if (filterCategories.size() - 1 == filterCount) {
                            for (UserFeedFilters.FeedFilter filter : filterCategories) {
                                selectedFilterMap.put(filter.getCategoryType(), filter);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


        notifyDataSetChanged();
        if (callback == null)
            return;

        assert selectedFilterMap != null;
        callback.onItemClicked(new ArrayList<>(selectedFilterMap.values()));
    }
}
