package com.oustme.oustsdk.adapter.common;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.customviews.CurveLayout;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.skill_ui.model.UserSkillData;
import com.oustme.oustsdk.skill_ui.ui.SkillDetailActivity;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class SkillModuleAdapter extends RecyclerView.Adapter<SkillModuleAdapter.ViewHolder> {

    private ArrayList<UserSkillData> userSkillDataArrayList = new ArrayList<>();
    public Context context;
    private boolean isAdapterHorizontal = false;


    public void setSkillModuleAdapter(ArrayList<UserSkillData> userSkillDataArrayList, Context context) {
        this.userSkillDataArrayList = userSkillDataArrayList;
        this.context = context;
        setHasStableIds(true);

    }

    public void setAdapterHorizontal(boolean adapterHorizontal) {
        isAdapterHorizontal = adapterHorizontal;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.skill_adapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.setIsRecyclable(false);
        return viewHolder;


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            if (OustPreferences.getAppInstallVariable("showCorn")) {
                holder.ic_grey_coin.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
            } else {
                holder.ic_grey_coin.setImageResource(R.drawable.ic_coin);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        UserSkillData userSkillData = userSkillDataArrayList.get(position);
        if (!isAdapterHorizontal) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.curveLayout.getLayoutParams();
            DisplayMetrics metrics = OustSdkApplication.getContext().getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            layoutParams.height = (int) ((((scrWidth - getDpForPixel(16)) / 2) - (getDpForPixel(16))) * 0.94);
            holder.curveLayout.setLayoutParams(layoutParams);
        } else {
            DisplayMetrics metrics = OustSdkApplication.getContext().getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int height = (int) ((((scrWidth - getDpForPixel(16)) / 2) - (getDpForPixel(16))) * 0.94);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(height, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, getDpForPixel(10), 0);
            holder.main_layout.setLayoutParams(params);
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) holder.curveLayout.getLayoutParams();
            params1.height = height;
            holder.curveLayout.setLayoutParams(params1);
        }


        if (userSkillData != null) {

            if (userSkillData.getIdpTargetScore() != 0 && userSkillData.getIdpTargetDate() != 0) {
                Date date = new Date(userSkillData.getIdpTargetDate());
                Date currentDate = new Date();
                if (date.getTime() >= currentDate.getTime()) {
                    holder.idp_lay.setVisibility(View.VISIBLE);
                } else {
                    holder.idp_lay.setVisibility(View.GONE);
                }

            } else {
                holder.idp_lay.setVisibility(View.GONE);
            }

            if (userSkillData.isVerifyFlag()) {
                holder.flag_lay.setVisibility(View.VISIBLE);
                holder.flag_iv.setImageResource(R.drawable.ic_tick_done);
            } else {
                if (userSkillData.isRedFlag()) {
                    holder.flag_lay.setVisibility(View.VISIBLE);
                    holder.flag_iv.setImageResource(R.drawable.ic_red_flag);
                } else {
                    holder.flag_lay.setVisibility(View.GONE);
                }
            }

            if ((userSkillData.getThumbnailImg() != null) && (!userSkillData.getThumbnailImg().isEmpty() && (!userSkillData.getThumbnailImg().equalsIgnoreCase("null")))) {
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(userSkillData.getThumbnailImg()).error(R.drawable.skill_thumbnail).into(holder.rowicon_image);
                } else {
                    Picasso.get().load(userSkillData.getThumbnailImg()).error(R.drawable.skill_thumbnail).networkPolicy(NetworkPolicy.OFFLINE).into(holder.rowicon_image);
                }
            } else if ((userSkillData.getBannerImg() != null) && (!userSkillData.getBannerImg().isEmpty() && (!userSkillData.getBannerImg().equalsIgnoreCase("null")))) {
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(userSkillData.getBannerImg()).error(R.drawable.skill_thumbnail).into(holder.rowicon_image);
                } else {
                    Picasso.get().load(userSkillData.getBannerImg()).error(R.drawable.skill_thumbnail).networkPolicy(NetworkPolicy.OFFLINE).into(holder.rowicon_image);
                }
            } else {
                holder.rowicon_image.setImageResource(R.drawable.skill_thumbnail);
            }

            if (userSkillData.getSoccerSkillName() != null && !userSkillData.getSoccerSkillName().isEmpty()) {
                holder.textViewTitle.setText(userSkillData.getSoccerSkillName());
            }

            if (userSkillData.isEnrolled()) {
                holder.CatalogueUpdateIndicator.setVisibility(View.GONE);
            }
            holder.coins_layout.setVisibility(View.GONE);
            holder.textViewPeopleCount.setText(String.valueOf(userSkillData.getTotalEnrolledCount()));

            holder.main_layout.setOnClickListener(v -> {

                Intent intent = new Intent(OustSdkApplication.getContext(), SkillDetailActivity.class);
                intent.putExtra("SkillId", "" + userSkillData.getSoccerSkillId());
                intent.putExtra("category", "Skill");
                intent.putExtra("catalog_type", "SOCCER_SKILL");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            });


        }


    }


    @Override
    public int getItemCount() {
        if (userSkillDataArrayList == null) {
            return 0;
        }
        return userSkillDataArrayList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView CatalogueUpdateIndicator;
        ImageView imageViewBg;
        ImageView rowicon_image;
        FrameLayout idp_lay;
        FrameLayout flag_lay;
        ImageView flag_iv;
        CustomTextView textViewTitle;
        CustomTextView textViewPeopleCount;
        LinearLayout coins_layout;
        CurveLayout curveLayout;
        RelativeLayout main_layout;

        ImageView ic_grey_coin;


        public ViewHolder(View itemView) {
            super(itemView);
            ic_grey_coin = itemView.findViewById(R.id.ic_grey_coin);
            main_layout = itemView.findViewById(R.id.main_layout);
            curveLayout = itemView.findViewById(R.id.curveLayout);
            coins_layout = itemView.findViewById(R.id.coins_layout);
            textViewPeopleCount = itemView.findViewById(R.id.textViewPeopleCount);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            flag_iv = itemView.findViewById(R.id.flag_iv);
            flag_lay = itemView.findViewById(R.id.flag_lay);
            idp_lay = itemView.findViewById(R.id.idp_lay);
            rowicon_image = itemView.findViewById(R.id.rowicon_image);
            imageViewBg = itemView.findViewById(R.id.imageViewBg);
            CatalogueUpdateIndicator = itemView.findViewById(R.id.CatalogueUpdateIndicator);
        }
    }

    private int getDpForPixel(int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, OustSdkApplication.getContext().getResources().getDisplayMetrics());
    }


}

