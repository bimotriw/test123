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
import com.oustme.oustsdk.layoutFour.data.response.GroupDataList;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupFilterDialogAdapter extends RecyclerView.Adapter<GroupFilterDialogAdapter.ViewHolder> {

    private final ArrayList<GroupDataList> filterCategories;
    private final HashMap<Long, GroupDataList> selectedFilterMap;

    private FilterAdapterCallback callback;

    public GroupFilterDialogAdapter(ArrayList<GroupDataList> filterCategories, ArrayList<GroupDataList> selectedFilterCategories) {
        this.filterCategories = filterCategories;
        selectedFilterMap = new HashMap<>();
        for (GroupDataList filterCategory : selectedFilterCategories) {
            selectedFilterMap.put(filterCategory.getGroupId(), filterCategory);
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

            GroupDataList filterCategory = filterCategories.get(position);

            if (filterCategory != null) {
                holder.dialog_option.setText(filterCategories.get(position).getGroupName());
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

    public void setCallback(FilterAdapterCallback callback) {
        this.callback = callback;
    }

    public interface FilterAdapterCallback {
        void onItemClicked(ArrayList<GroupDataList> selectedFilter);
    }

    private void setSelection(ViewHolder holder, GroupDataList filterCategory) {


        if (selectedFilterMap != null) {
            long categoryType = filterCategory.getGroupId();
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
    }

    private void handleClick(int position) {

        if (selectedFilterMap != null) {
            GroupDataList filterCategory = filterCategories.get(position);
            String filterName = filterCategory.getGroupName();
            long categoryType = filterCategory.getGroupId();

            if (filterName.equalsIgnoreCase("all")) {
                if (selectedFilterMap.containsKey(categoryType)) {
                    for (GroupDataList filter : filterCategories)
                        selectedFilterMap.remove(filter.getGroupId());
                } else {
                    for (GroupDataList filter : filterCategories)
                        selectedFilterMap.put(filter.getGroupId(), filter);
                }
            } else {
                if (selectedFilterMap.containsKey(categoryType)) {
                    selectedFilterMap.clear();

                } else {
                    selectedFilterMap.clear();
                    selectedFilterMap.put(categoryType, filterCategory);
                }

            }
        }


        notifyDataSetChanged();

        if (callback == null)
            return;

        assert selectedFilterMap != null;
        callback.onItemClicked(new ArrayList<>(selectedFilterMap.values()));
    }

}
