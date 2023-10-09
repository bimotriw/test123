package com.oustme.oustsdk.adapter.common;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.model.response.common.BottomItemModel;

import java.util.List;

/**
 * Created by admin on 08/11/18.
 */

public class BottomSheetItemRecyclerAdapter extends RecyclerView.Adapter<BottomSheetItemRecyclerAdapter.MyViewHolder> {

    private List<BottomItemModel> mItemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.textViewItem);

        }
    }


    public BottomSheetItemRecyclerAdapter(Context context, List<BottomItemModel> mItemList) {
        this.mItemList = mItemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_bottom_sheet_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BottomItemModel movie = mItemList.get(position);
        holder.title.setText(movie.getmTitle());
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }
}
