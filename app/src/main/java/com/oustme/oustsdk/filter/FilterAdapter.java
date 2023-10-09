package com.oustme.oustsdk.filter;

import android.content.Context;
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
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {


    private ArrayList<FilterForAll> filterCategories;
    private HashMap<Integer, FilterForAll> selectedFilterMap1;
    private FilterAdapterCallback callback;
    private String filterType;
    private final Context context;

    public FilterAdapter(ArrayList<FilterForAll> filterCategories, ArrayList<FilterForAll> selectedFilterCategories, String filterType, Context context) {
        this.filterCategories = filterCategories;
        this.filterType = filterType;
        this.context = context;
        selectedFilterMap1 = new HashMap<>();
        for (FilterForAll filterCategory : selectedFilterCategories) {
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
        FilterForAll filterCategory = filterCategories.get(position);
        holder.tvFilterItem.setTypeface(holder.tvFilterItem.getTypeface(), Typeface.NORMAL);

        setSelection(holder, filterCategory);

        holder.itemView.setOnClickListener(v -> handleClick(position));
    }

    @Override
    public int getItemCount() {
        return filterCategories == null ? 0 : filterCategories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFilterItem;
        CheckBox cbFilter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFilterItem = itemView.findViewById(R.id.tv_filter);
            cbFilter = itemView.findViewById(R.id.cb_filter);

            CompoundButtonCompat.setButtonTintList(cbFilter, OustResourceUtils.getDefaultTint());
        }
    }

    public void setCallback(FilterAdapterCallback callback) {
        this.callback = callback;
    }

    public interface FilterAdapterCallback {
        void onItemClicked(ArrayList<FilterForAll> selectedFilter);
    }

    private void handleClick(int position) {
        try {
            int filterCount = 0;
            if (selectedFilterMap1 != null) {
                FilterForAll filterCategory = filterCategories.get(position);
                String filterName = filterCategory.getCategoryName();
                int categoryType = filterCategory.getCategoryType();

                if (filterName.equalsIgnoreCase("all")) {
                    if (selectedFilterMap1.containsKey(categoryType)) {
                        for (FilterForAll filter : filterCategories) {
                            selectedFilterMap1.remove(filter.getCategoryType());
                        }
                    } else {
                        for (FilterForAll filter : filterCategories) {
                            selectedFilterMap1.put(filter.getCategoryType(), filter);
                        }
                    }
                } else {
                    if (filterType.equalsIgnoreCase("type")) {
                        selectedFilterMap1.remove(0);
                    } else {
                        selectedFilterMap1.remove(5);
                    }

                    if (selectedFilterMap1.containsKey(categoryType)) {
                        selectedFilterMap1.remove(categoryType);
                    } else {
                        selectedFilterMap1.put(categoryType, filterCategory);
                    }

                    if (selectedFilterMap1.size() > 0) {
                        for (FilterForAll filter : filterCategories) {
                            boolean valueFound = false;
                            FilterForAll filterSub = selectedFilterMap1.get(filter.getCategoryType());
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
                            for (FilterForAll filter : filterCategories) {
                                selectedFilterMap1.put(filter.getCategoryType(), filter);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        notifyDataSetChanged();

        if (callback == null)
            return;

        callback.onItemClicked(new ArrayList<>(selectedFilterMap1.values()));
    }

    private void setSelection(ViewHolder holder, FilterForAll filterCategory) {


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

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
