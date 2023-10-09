package com.oustme.oustsdk.adapter.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blongho.country_data.World;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.interfaces.common.OnLanguageSelected;
import com.oustme.oustsdk.response.common.LanguageClass;

import java.util.ArrayList;
import java.util.List;



public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    private final List<LanguageClass> languageList;
    private final OnLanguageSelected onItemClickListener;
    private int lastSelectedPosition = -1;

    public LanguageAdapter(List<LanguageClass> languageList, OnLanguageSelected onItemClickListener) {
        this.languageList = languageList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_select_language, parent, false);
        return new LanguageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        LanguageClass language = languageList.get(position);
        holder.tvCountryName.setText(language.getName());
        if(language.getCountryCode()!=null){
            final int flag= World.getFlagOf(language.getCountryCode());
            holder.imgCountryFlag.setImageResource(flag);
        }else{
            holder.imgCountryFlag.setVisibility(View.GONE);
        }
        holder.itemLangWrapper.setOnClickListener(v -> {
            onItemClickListener.onSelectLanguage(language);
        });
        holder.cbLanguage.setChecked(lastSelectedPosition == position);
        holder.cbLanguage.setOnClickListener(v -> {
            language.setSelected(!language.getSelected());
            lastSelectedPosition = position;  // update the last selected position
            notifyDataSetChanged();  // refresh the adapter
        });
    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }

    public ArrayList<LanguageClass> getItems() {
        return (ArrayList<LanguageClass>) languageList;
    }

    class LanguageViewHolder extends RecyclerView.ViewHolder {
        TextView tvCountryName;
        CircleImageView imgCountryFlag;
        RadioButton cbLanguage;

        RelativeLayout itemLangWrapper;

        LanguageViewHolder(View itemView) {
            super(itemView);
            tvCountryName = itemView.findViewById(R.id.countryName);
            imgCountryFlag = itemView.findViewById(R.id.countryFlag);
            cbLanguage = itemView.findViewById(R.id.cbLanguage);
            itemLangWrapper = itemView.findViewById(R.id.itemLangWrapper);
            // Initialize other views as needed
        }
    }
}
