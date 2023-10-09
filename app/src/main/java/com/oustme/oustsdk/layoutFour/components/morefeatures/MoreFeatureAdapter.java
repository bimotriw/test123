package com.oustme.oustsdk.layoutFour.components.morefeatures;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.layoutFour.data.Navigation;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class MoreFeatureAdapter extends RecyclerView.Adapter<MoreFeatureAdapter.ViewHolder> {
    private final ArrayList<Navigation> moreFeatureList;
    private final NavigationCallback callback;

    public MoreFeatureAdapter(ArrayList<Navigation> moreFeatureList, NavigationCallback callback) {
        this.moreFeatureList = moreFeatureList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feature, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e("TAG", "onBindViewHolder:  names--> " + moreFeatureList.get(position).getNavName());
        final Navigation navigation = moreFeatureList.get(position);
        try {
            if (navigation != null) {
                if (navigation.getNavType().equalsIgnoreCase("teamanalytics")) {
                    if (OustPreferences.get(AppConstants.StringConstants.TEAM_ANALYTICS) != null) {
                        if (OustPreferences.get(AppConstants.StringConstants.TEAM_ANALYTICS).equalsIgnoreCase("true")) {
                            holder.profileItems.setVisibility(View.VISIBLE);
                            holder.tvFeature.setText(navigation.getNavName());
                            showIcons(navigation, holder);
                        } else {
                            holder.profileItems.setVisibility(View.GONE);
                        }
                    } else {
                        holder.profileItems.setVisibility(View.GONE);
                    }
                } else if (navigation.getNavType().equalsIgnoreCase("faq")) {
                    if (OustPreferences.get(AppConstants.StringConstants.SHOW_FAQ) != null) {
                        if (OustPreferences.get(AppConstants.StringConstants.SHOW_FAQ).equalsIgnoreCase("true")) {
                            holder.profileItems.setVisibility(View.VISIBLE);
                            holder.tvFeature.setText(navigation.getNavName());
                            showIcons(navigation, holder);
                        } else {
                            holder.profileItems.setVisibility(View.GONE);
                        }
                    } else {
                        holder.profileItems.setVisibility(View.GONE);
                    }
                } else {
                    holder.profileItems.setVisibility(View.VISIBLE);
                    holder.tvFeature.setText(navigation.getNavName());
                    showIcons(navigation, holder);
                }


                holder.itemView.setOnClickListener(v -> {
                    if (callback == null)
                        return;
                    callback.onNav(navigation);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void showIcons(Navigation navigation, ViewHolder holder) {
        if (navigation.getNavIcon() != null && !navigation.getNavIcon().isEmpty()) {
            //OustResourceUtils.setMenuIcon(holder.ivFeature, navigation.getNavIcon());
            if (navigation.getNavType().equalsIgnoreCase("favourites")) {
                Picasso.get().load(navigation.getNavIcon()).placeholder(R.drawable.favourites).into(holder.ivFeature);
            } else if (navigation.getNavType().equalsIgnoreCase("learningdiary")) {
                Picasso.get().load(navigation.getNavIcon()).placeholder(R.drawable.learning).into(holder.ivFeature);
            } else if (navigation.getNavType().equalsIgnoreCase("helpsupport")) {
                Picasso.get().load(navigation.getNavIcon()).placeholder(R.drawable.help).into(holder.ivFeature);
            } else if (navigation.getNavType().equalsIgnoreCase("rateapp")) {
                Picasso.get().load(navigation.getNavIcon()).placeholder(R.drawable.rateapp).into(holder.ivFeature);
            } else if (navigation.getNavType().equalsIgnoreCase("settings")) {
                Picasso.get().load(navigation.getNavIcon()).placeholder(R.drawable.setting).into(holder.ivFeature);
            } else if (navigation.getNavType().equalsIgnoreCase("logout")) {
                Picasso.get().load(navigation.getNavIcon()).placeholder(R.drawable.logoff).into(holder.ivFeature);
            } else {
                Picasso.get().load(navigation.getNavIcon()).placeholder(R.drawable.home).into(holder.ivFeature);
            }

        } else {
            if (navigation.getNavType().equalsIgnoreCase("favourites")) {
                holder.ivFeature.setImageResource(R.drawable.favourites);
            } else if (navigation.getNavType().equalsIgnoreCase("learningdiary")) {
                holder.ivFeature.setImageResource(R.drawable.learning);
            } else if (navigation.getNavType().equalsIgnoreCase("helpsupport")) {
                holder.ivFeature.setImageResource(R.drawable.help);
            } else if (navigation.getNavType().equalsIgnoreCase("rateapp")) {
                holder.ivFeature.setImageResource(R.drawable.rateapp);
            } else if (navigation.getNavType().equalsIgnoreCase("settings")) {
                holder.ivFeature.setImageResource(R.drawable.setting);
            } else if (navigation.getNavType().equalsIgnoreCase("logout")) {
                holder.ivFeature.setImageResource(R.drawable.logoff);
            } else {
                holder.ivFeature.setImageResource(R.drawable.home);
            }
        }
    }

    @Override
    public int getItemCount() {
        return moreFeatureList == null ? 0 : moreFeatureList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFeature;
        ImageView ivFeature;
        ImageView forwardArrow;
        LinearLayout profileItems;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFeature = itemView.findViewById(R.id.tv_feature);
            ivFeature = itemView.findViewById(R.id.iv_feature);
            forwardArrow = itemView.findViewById(R.id.arrow_forward_profile);
            profileItems = itemView.findViewById(R.id.profile_items);
        }
    }

    interface NavigationCallback {
        void onNav(Navigation navigation);
    }
}
