package com.oustme.oustsdk.layoutFour.components.feedList.adapter;

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

import java.util.ArrayList;
import java.util.HashMap;

public class FeedFilterAdapter extends RecyclerView.Adapter<FeedFilterAdapter.ViewHolder> {


    private ArrayList<FilterCategory> filterCategories;
    private HashMap<String, FilterCategory> selectedFilterMap;

    private FeedFilterAdapterCallback callback;

    public FeedFilterAdapter(ArrayList<FilterCategory> filterCategories, ArrayList<FilterCategory> selectedFilterCategories) {
        this.filterCategories = filterCategories;
        selectedFilterMap = new HashMap<>();
        for (FilterCategory filterCategory: selectedFilterCategories){
            selectedFilterMap.put(filterCategory.getCategoryName(),filterCategory);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvFilterItem.setText(filterCategories.get(position).getCategoryName());
        FilterCategory filterCategory = filterCategories.get(position);
        holder.tvFilterItem.setTypeface(holder.tvFilterItem.getTypeface(), Typeface.NORMAL);

        if (selectedFilterMap != null) {
            String filterName = filterCategory.getCategoryName();
            if (selectedFilterMap.containsKey(filterName)) {
                holder.ivFilter.setVisibility(View.VISIBLE);
                holder.tvFilterItem.setTypeface(null, Typeface.BOLD);
            } else {
                holder.ivFilter.setVisibility(View.INVISIBLE);
                holder.tvFilterItem.setTypeface(null, Typeface.NORMAL);
            }
        }else {
            holder.ivFilter.setVisibility(View.INVISIBLE);
            holder.tvFilterItem.setTypeface(null, Typeface.NORMAL);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFilterMap != null) {
                    FilterCategory filterCategory = filterCategories.get(position);
                    String filterName = filterCategory.getCategoryName();
                    if (filterName.equalsIgnoreCase("all")) {
                        if (selectedFilterMap.containsKey(filterName)) {
                            selectedFilterMap.clear();
                        } else {
                            for (FilterCategory filter : filterCategories)
                                selectedFilterMap.put(filter.getCategoryName(), filter);
                        }
                    } else {
                        if (selectedFilterMap.containsKey(filterName)) {
                            selectedFilterMap.remove(filterName);
                        } else {
                            selectedFilterMap.put(filterName, filterCategory);
                        }
                    }
                }

                if(selectedFilterMap.size()<filterCategories.size()){
                    selectedFilterMap.remove("All");
                }
                notifyDataSetChanged();

                if (callback == null)
                    return;

                callback.onItemClicked(new ArrayList<FilterCategory>(selectedFilterMap.values()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterCategories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFilterItem;
        ImageView ivFilter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFilterItem = itemView.findViewById(R.id.tv_filter);
            ivFilter = itemView.findViewById(R.id.iv_filter);

        }
    }

    public void setCallback(FeedFilterAdapterCallback callback) {
        this.callback = callback;
    }

    public interface FeedFilterAdapterCallback {
        void onItemClicked(ArrayList<FilterCategory> selectedFilter);
    }


}
