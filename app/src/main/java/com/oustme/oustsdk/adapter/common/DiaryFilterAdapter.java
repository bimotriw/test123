package com.oustme.oustsdk.adapter.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.model.response.diary.FilterModel;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.List;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.TOOL_BAR_COLOR_CODE;

public class DiaryFilterAdapter extends RecyclerView.Adapter<DiaryFilterAdapter.MyViewHolder> {

    private List<FilterModel> dataSet;
    private Context mContext;
    private int row_index=0;
    private SelectFilter mListener;
    private Drawable mBackgroundDrawable;
    private String toolbarColorCode;
    private int appColor;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewType;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewType = itemView.findViewById(R.id.textViewType);
        }
    }

    public DiaryFilterAdapter(Context context, List<FilterModel> data) {
        try {
        this.dataSet = data;
        this.mContext = context;
        mListener = (SelectFilter) context;
        mBackgroundDrawable = this.mContext.getResources().getDrawable(R.drawable.diary_filter_bg_green);
        toolbarColorCode = OustPreferences.get(TOOL_BAR_COLOR_CODE);
            if(toolbarColorCode!=null)
            {
                appColor = Color.parseColor(toolbarColorCode);
            }
            if(appColor!=0){
                mBackgroundDrawable.setColorFilter(appColor, PorterDuff.Mode.SRC_ATOP);
            }
            else {
                mBackgroundDrawable.setColorFilter(mContext.getResources().getColor(R.color.lgreen), PorterDuff.Mode.SRC_ATOP);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_filter_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.textViewType.setText(dataSet.get(position).getLearningDiaryFilterLabel());
        holder.textViewType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index= position;
                notifyDataSetChanged();
                onClickItem(position);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index= position;
                notifyDataSetChanged();
                onClickItem(position);
            }
        });
        if(row_index==position){
            holder.textViewType.setBackgroundDrawable(mBackgroundDrawable);
        }
        else
        {
            holder.textViewType.setBackgroundResource(R.drawable.diary_filter_bg_grey);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
    public void onClickItem(int position) {
        if (mListener != null) {
            mListener.selectedPosition(position);
        }
    }
    public interface SelectFilter {
        void selectedPosition(int position);
    }
}
