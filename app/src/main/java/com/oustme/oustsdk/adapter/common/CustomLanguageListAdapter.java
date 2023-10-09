package com.oustme.oustsdk.adapter.common;

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
import com.oustme.oustsdk.activity.common.languageSelector.LanguageSelectionActivity;
import com.oustme.oustsdk.activity.common.languageSelector.model.response.Language;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.util.List;

public class CustomLanguageListAdapter extends RecyclerView.Adapter<CustomLanguageListAdapter.MyViewHolder> {

    List<Language> dataSet;
    Context mContext;
    CustomLanguageItemListener customLanguageItemListener;

    public CustomLanguageListAdapter(List<Language> dataSet, LanguageSelectionActivity languageSelectionActivity, CustomLanguageListAdapter.CustomLanguageItemListener customLanguageItemListener) {
        this.customLanguageItemListener = customLanguageItemListener;
        this.dataSet = dataSet;
        this.mContext = languageSelectionActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            if (OustStaticVariableHandling.getInstance().getSortPosition() == position) {
                holder.text_language.setText(dataSet.get(position).getName());
                holder.text_language.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.text_language.setTypeface(null, Typeface.BOLD);
                holder.text_language.setVisibility(View.VISIBLE);
                holder.dialog_option_chosen.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_filled_tick));
                holder.dialog_option_chosen.setVisibility(View.VISIBLE);
            } /*else if (dataSet.get(position).defaultSelected) {
                OustStaticVariableHandling.getInstance().setSortPosition(position);
                holder.text_language.setText(dataSet.get(position).getName());
                holder.text_language.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.text_language.setTypeface(null, Typeface.BOLD);
                holder.text_language.setVisibility(View.VISIBLE);
                holder.dialog_option_chosen.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_filled_tick));
                holder.dialog_option_chosen.setVisibility(View.VISIBLE);
                Language language = new Language();
                language.setLanguageId(dataSet.get(position).languageId);
                language.setName(dataSet.get(position).name);
                language.setDefaultSelected(false);
                language.setChildCPLId(dataSet.get(position).getChildCPLId());
                dataSet.set(position, language);
                if (customLanguageItemListener != null) {
                    customLanguageItemListener.onItemClicked(holder.getAbsoluteAdapterPosition());
                }
            } */ else {
                holder.text_language.setText(dataSet.get(position).getName());
                holder.text_language.setTypeface(null, Typeface.NORMAL);
                holder.text_language.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.dialog_option_chosen.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        holder.itemView.setOnClickListener(v -> {
            try {
                if (OustStaticVariableHandling.getInstance().getSortPosition() >= 0)
                    notifyItemChanged(OustStaticVariableHandling.getInstance().getSortPosition());
                OustStaticVariableHandling.getInstance().setSortPosition(holder.getAbsoluteAdapterPosition());
                notifyItemChanged(OustStaticVariableHandling.getInstance().getSortPosition());
                if (customLanguageItemListener != null) {
                    customLanguageItemListener.onItemClicked(holder.getAbsoluteAdapterPosition());
                    OustStaticVariableHandling.getInstance().setSortPosition(holder.getAbsoluteAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface CustomLanguageItemListener {
        void onItemClicked(int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_language;
        ImageView dialog_option_chosen;

        public MyViewHolder(View itemView) {
            super(itemView);
            text_language = itemView.findViewById(R.id.text_language);
            dialog_option_chosen = itemView.findViewById(R.id.dialog_option_chosen);
        }
    }
}
