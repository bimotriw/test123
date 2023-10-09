package com.oustme.oustsdk.adapter.common;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.FilterCategory;
import com.oustme.oustsdk.interfaces.common.FilterCategoryCallBack;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.util.ArrayList;

/**
 * Created by oust on 5/14/18.
 */

public class NewFeedVerticalFilterAdapter extends RecyclerView.Adapter<NewFeedVerticalFilterAdapter.MyViewHolder> {

    private static final String TAG = "NewFeedVerticalFilterAd";
    private ArrayList<FilterCategory> filterCategories;
    private Context context;
    private int selectPos = 0;
    private FilterCategoryCallBack filterCategoryCallBack;
    private String label;

    public NewFeedVerticalFilterAdapter(Context context, ArrayList<FilterCategory> filterCategories, FilterCategoryCallBack filterCategoryCallBack) {
        this.context = context;
        this.filterCategories = filterCategories;
        this.filterCategoryCallBack = filterCategoryCallBack;
    }

    public void setSelectionPos(int pos) {
        this.selectPos = pos;
    }

    public void updateAdapter(int selectPos,String label) {
        this.label= label;
        if(label!=null && label.length()!=0){
            if(filterCategories!=null){
                for(int i=0;i<filterCategories.size();i++){
                    if(filterCategories.get(i).getCategoryName()!=null && filterCategories.get(i).getCategoryName().equals(label)){
                        this.selectPos=i;
                    }
                }
            }
        }else{
            this.selectPos=selectPos;
        }
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_category_item_2, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.filter_text.setText("");
        holder.filter_text.setText(filterCategories.get(position).getCategoryName());

        if(label!=null && !label.isEmpty()){
            if(filterCategories.get(position).getCategoryName().equals(label)){
                holder.filter_img.setImageDrawable(context.getResources().getDrawable(R.drawable.checked));
            }else{
                holder.filter_img.setImageDrawable(context.getResources().getDrawable(R.drawable.checkbox));
            }
        }else {
            if (filterCategories.get(position).getCategoryType() == selectPos) {
                holder.filter_img.setImageDrawable(context.getResources().getDrawable(R.drawable.checked));
            } else {
                holder.filter_img.setImageDrawable(context.getResources().getDrawable(R.drawable.checkbox));
            }
        }
        holder.filter_text_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OustStaticVariableHandling.getInstance().isFilterAllowed()) {
                    if(!filterCategories.get(position).isSelected()) {
                        filterCategories.get(position).setSelected(true);
                        clickFilter(holder.getAdapterPosition());
                    }else{
                        filterCategories.get(position).setSelected(false);
                        unClickFilter();
                    }
                } else
                    OustSdkTools.showToast("Complete your search . Filters cannot be applied.");
            }
        });

    }

    private void unClickFilter() {
        filterCategoryCallBack.filterCategoryFeeds(0, 2,"");
    }

    private void clickFilter(int position) {
        //Log.d(TAG, "clickFilter position: "+filterCategories.get(position).getCategoryType());
        filterCategoryCallBack.filterCategoryFeeds(filterCategories.get(position).getCategoryType(), 2,filterCategories.get(position).getCategoryName());
    }


    @Override
    public int getItemCount() {
        return filterCategories.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView filter_text;
        LinearLayout filter_text_ll;
        ImageView filter_img;

        MyViewHolder(View view) {
            super(view);
            filter_text = view.findViewById(R.id.filter_item_text);
            filter_text_ll = view.findViewById(R.id.filter_text_ll);
            filter_img = view.findViewById(R.id.filter_img);
        }
    }
}
