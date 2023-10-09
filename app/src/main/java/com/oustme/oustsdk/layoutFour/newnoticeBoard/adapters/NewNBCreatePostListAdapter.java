package com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.activity.NewNBCreatePostList;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBTopicData;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.util.List;

public class NewNBCreatePostListAdapter extends RecyclerView.Adapter<NewNBCreatePostListAdapter.MyViewHolder> {

    List<NewNBTopicData> nbTopicData1;
    private final Context context;
    NewNBCreatePostItemListener mListener;

    public interface NewNBCreatePostItemListener {
        void onItemClicked(int position);
    }

    public NewNBCreatePostListAdapter(List<NewNBTopicData> nbTopicData1, NewNBCreatePostList nbCreatePostList, NewNBCreatePostItemListener newNBCreatePostList) {
        this.mListener = newNBCreatePostList;
        this.nbTopicData1 = nbTopicData1;
        this.context = nbCreatePostList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_members_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            holder.dialog_option.setText("" + nbTopicData1.get(position).getTopic());

            if (OustStaticVariableHandling.getInstance().getSortPosition() == position) {
                holder.dialog_option.setTypeface(null, Typeface.BOLD);
                holder.dialog_option_chosen.setVisibility(View.VISIBLE);
                holder.dialog_option_chosen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_filled_tick));
            } else {
                holder.dialog_option.setTypeface(null, Typeface.NORMAL);
                holder.dialog_option.setTextColor(context.getResources().getColor(R.color.unselected_text));
                holder.dialog_option_chosen.setVisibility(View.VISIBLE);
                holder.dialog_option_chosen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_checkmark_unselected));

            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        holder.itemView.setOnClickListener(v -> {
            try {
                if (OustStaticVariableHandling.getInstance().getSortPosition() >= 0)
                    notifyItemChanged(OustStaticVariableHandling.getInstance().getSortPosition());
                OustStaticVariableHandling.getInstance().setSortPosition(holder.getAdapterPosition());
                notifyItemChanged(OustStaticVariableHandling.getInstance().getSortPosition());
                if (mListener != null) {
                    mListener.onItemClicked(holder.getAdapterPosition());
                    OustStaticVariableHandling.getInstance().setSortPosition(holder.getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nbTopicData1.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dialog_option;
        ImageView dialog_option_chosen;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dialog_option = itemView.findViewById(R.id.dialog_option);
            dialog_option_chosen = itemView.findViewById(R.id.dialog_option_chosen);

        }
    }
}
