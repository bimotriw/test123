package com.oustme.oustsdk.layoutFour.components.newCatalogue.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.FilterCategory;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class FeedFilterAdapter extends RecyclerView.Adapter<FeedFilterAdapter.ViewHolder> {


    private ArrayList<FilterCategory> filterCategories;
//    private HashMap<String, FilterCategory> selectedFilterMap;
    private HashMap<Integer, FilterCategory> selectedFilterMap1;

    private FeedFilterAdapterCallback callback;

    public FeedFilterAdapter(ArrayList<FilterCategory> filterCategories, ArrayList<FilterCategory> selectedFilterCategories) {
        this.filterCategories = filterCategories;
//        selectedFilterMap = new HashMap<>();
        selectedFilterMap1 = new HashMap<>();
        for (FilterCategory filterCategory : selectedFilterCategories) {
//            selectedFilterMap.put(filterCategory.getCategoryName(), filterCategory);
            selectedFilterMap1.put(filterCategory.getCategoryType(), filterCategory);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvFilterItem.setText(filterCategories.get(position).getCategoryName());
        FilterCategory filterCategory = filterCategories.get(position);
        holder.tvFilterItem.setTypeface(holder.tvFilterItem.getTypeface(), Typeface.NORMAL);

        setSelection(holder,filterCategory);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterCategories == null ? 0 : filterCategories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFilterItem;
        CheckBox cbFilter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFilterItem = itemView.findViewById(R.id.tv_filter);
            cbFilter = itemView.findViewById(R.id.cb_filter);

            CompoundButtonCompat.setButtonTintList(cbFilter, OustResourceUtils.getDefaultTint());


        }
    }

    public void setCallback(FeedFilterAdapterCallback callback) {
        this.callback = callback;
    }

    public interface FeedFilterAdapterCallback {
        void onItemClicked(ArrayList<FilterCategory> selectedFilter);
    }

    private void handleClick(int position) {

        /*if (selectedFilterMap != null) {
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
        }*/

        if (selectedFilterMap1 != null) {
            FilterCategory filterCategory = filterCategories.get(position);
            String filterName = filterCategory.getCategoryName();
            int categoryType = filterCategory.getCategoryType();

            if (filterName.equalsIgnoreCase("all")) {
                if (selectedFilterMap1.containsKey(categoryType)) {
                    for (FilterCategory filter : filterCategories)
                        selectedFilterMap1.remove(filter.getCategoryType());
                } else {
                    for (FilterCategory filter : filterCategories)
                        selectedFilterMap1.put(filter.getCategoryType(), filter);
                }
            } else {
                if (selectedFilterMap1.containsKey(categoryType)) {
                    selectedFilterMap1.remove(categoryType);
                } else {
                    selectedFilterMap1.put(categoryType, filterCategory);
                }
            }
        }

//        if (selectedFilterMap.size() < filterCategories.size()) {
//            selectedFilterMap.remove("All");
//        }

        notifyDataSetChanged();

        if (callback == null)
            return;

        callback.onItemClicked(new ArrayList<FilterCategory>(selectedFilterMap1.values()));
    }

    private void setSelection(ViewHolder holder, FilterCategory filterCategory) {

        /*if (selectedFilterMap != null) {
            String filterName = filterCategory.getCategoryName();
            if (selectedFilterMap.containsKey(filterName)) {
                holder.tvFilterItem.setTypeface(null, Typeface.BOLD);
                holder.cbFilter.setChecked(true);
            } else {
                holder.tvFilterItem.setTypeface(null, Typeface.NORMAL);
                holder.cbFilter.setChecked(false);
            }
        } else {
            holder.tvFilterItem.setTypeface(null, Typeface.NORMAL);
        }*/

        if (selectedFilterMap1 != null) {
            int categoryType = filterCategory.getCategoryType();
            if (selectedFilterMap1.containsKey(categoryType)) {
                holder.tvFilterItem.setTypeface(null, Typeface.BOLD);
                holder.cbFilter.setChecked(true);
            } else {
                holder.tvFilterItem.setTypeface(null, Typeface.NORMAL);
                holder.cbFilter.setChecked(false);
            }
        } else {
            holder.tvFilterItem.setTypeface(null, Typeface.NORMAL);
        }
    }


}
