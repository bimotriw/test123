package com.oustme.oustsdk.adapter.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.FilterCategory;
import com.oustme.oustsdk.interfaces.common.FilterCategoryCallBack;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.util.ArrayList;

/**
 * Created by oust on 5/14/18.
 */

public class NewFeedFilterAdapter extends RecyclerView.Adapter<NewFeedFilterAdapter.MyViewHolder> {

    private static final String TAG = "NewFeedFilterAdapter";
    private ArrayList<FilterCategory> filterCategories;
    private Context context;
    private int selectPos=0;
    private FilterCategoryCallBack filterCategoryCallBack;
    private String label;

    public NewFeedFilterAdapter(Context context,ArrayList<FilterCategory> filterCategories,FilterCategoryCallBack filterCategoryCallBack) {
        this.context=context;
        this.label="";
        this.filterCategories=filterCategories;
        this.filterCategoryCallBack=filterCategoryCallBack;
    }

    public void setSelectionPos(int pos){
        this.selectPos=pos;
    }

    public void updateAdapter(int selectPos,String label) {
        this.label=label;
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_category_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.filter_text.setText("");
        holder.filter_text.setText(filterCategories.get(position).getCategoryName());

        if(label!=null && !label.isEmpty()){
            if(filterCategories.get(position).getCategoryName().equals(label)){
                setBackGroundColor(holder.filter_text_rl, true);
            }else{
                setBackGroundColor(holder.filter_text_rl, false);
            }
        }else {
            if (filterCategories.get(position).getCategoryType() == selectPos) {
                setBackGroundColor(holder.filter_text_rl, true);
            } else {
                setBackGroundColor(holder.filter_text_rl, false);
            }
        }
        holder.filter_text_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(OustStaticVariableHandling.getInstance().isFilterAllowed())
                    clickFilter(holder.getAdapterPosition());
                else
                    OustSdkTools.showToast("Complete your search . Filters cannot be applied.");
            }
        });

    }

    private void clickFilter(int position) {
        try {
            filterCategoryCallBack.filterCategoryFeeds(filterCategories.get(position).getCategoryType(), 1, filterCategories.get(position).getCategoryName());
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setBackGroundColor(RelativeLayout filter_text_rl, boolean isSelected) {
        try {
            int color=0;
            if (isSelected) {
                color = OustSdkTools.getColorBack(R.color.lgreen);
                if (OustPreferences.get("toolbarColorCode") != null) {
                    color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                }
            } else {
                color = context.getResources().getColor(R.color.LiteGray);
            }
            GradientDrawable gd= (GradientDrawable) filter_text_rl.getBackground();
            gd.setColor(color);
            filter_text_rl.setBackgroundDrawable(gd);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public int getItemCount() {
        if(filterCategories!=null) {
            if(filterCategories.size()>3){
                return 3;
            }
            return filterCategories.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView filter_text;
        RelativeLayout filter_text_rl;
        MyViewHolder(View view) {
            super(view);
            filter_text= view.findViewById(R.id.filter_item_text);
            filter_text_rl= view.findViewById(R.id.filter_text_rl);
        }
    }
}
