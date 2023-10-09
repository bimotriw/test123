package com.oustme.oustsdk.layoutFour.components.dialogFragments.adapter;

import androidx.recyclerview.widget.RecyclerView;


import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;


public class SortFilterDialogAdapter extends RecyclerView.Adapter<SortFilterDialogAdapter.ViewHolder> {

    String[] optionsData;
    private int lastCheckedPosition = -1;
    private final SortFilterDialogListener mListener;

    public interface SortFilterDialogListener {
        void onItemClicked(int position);
    }

    public SortFilterDialogAdapter(String[] optionsData, SortFilterDialogListener sortFilterDialogListener) {
        this.optionsData = optionsData;
        mListener = sortFilterDialogListener;
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
            holder.dialog_option.setText(optionsData[position]);

            if (OustStaticVariableHandling.getInstance().getSortPosition() == position) {
                holder.dialog_option.setTypeface(null, Typeface.BOLD);
                holder.dialog_option_chosen.setVisibility(View.VISIBLE);
            } else {
                holder.dialog_option.setTypeface(null, Typeface.NORMAL);
                holder.dialog_option_chosen.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

    }

    @Override
    public int getItemCount() {
        return optionsData == null ? 0 : optionsData.length;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView dialog_option;
        ImageView dialog_option_chosen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dialog_option = itemView.findViewById(R.id.dialog_option);
            dialog_option_chosen = itemView.findViewById(R.id.dialog_option_chosen);
        }
    }
}
