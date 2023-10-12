package com.oustme.oustsdk.adapter.common;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        Log.d("test lang","code "+language.getCountryCode());

        if(language.getCountryCode()!=null){
            Drawable flag= holder.imgCountryFlag.getContext().getResources().getDrawable(R.drawable.english);
            switch (language.getCountryCode()) {
                case "AE":
                    flag = holder.imgCountryFlag.getContext().getResources().getDrawable(R.drawable.arab);
                    break;
                case "IN":
                    flag = holder.imgCountryFlag.getContext().getResources().getDrawable(R.drawable.india);
                    break;
                case "MY":
                    flag = holder.imgCountryFlag.getContext().getResources().getDrawable(R.drawable.malaysia);
                    break;
                case "ID":
                    flag = holder.imgCountryFlag.getContext().getResources().getDrawable(R.drawable.indonesia);
                    break;
            }
            holder.imgCountryFlag.setImageDrawable(flag);
        }else{
            holder.imgCountryFlag.setVisibility(View.GONE);
        }
        holder.cbLanguage.setChecked(lastSelectedPosition == position);
        holder.itemLangWrapper.setOnClickListener(v -> {
            Log.d("test lang","wrapper clicked");
            onItemClickListener.onSelectLanguage(language);
            language.setSelected(!language.getSelected());
            lastSelectedPosition = position;  // update the last selected position
            notifyDataSetChanged();  // refresh the adapter
        });
        holder.cbLanguage.setOnClickListener(v -> {
            lastSelectedPosition = position;  // update the last selected position
            notifyDataSetChanged();  // refresh
            onItemClickListener.onSelectLanguage(language);
        });
    }


    public int getLastSelectedPosition(){
        return lastSelectedPosition;
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
