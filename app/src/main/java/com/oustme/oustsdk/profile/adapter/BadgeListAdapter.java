package com.oustme.oustsdk.profile.adapter;

import static com.oustme.oustsdk.util.AchievementUtils.convertDate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.profile.CourseCompletionWithBadgeActivity;
import com.oustme.oustsdk.profile.fragment.BadgeFragment;
import com.oustme.oustsdk.profile.model.BadgeModel;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class BadgeListAdapter extends RecyclerView.Adapter<BadgeListAdapter.ViewHolder> implements Filterable {

    private HashMap<Long, BadgeModel> badgeModelHashMap = new HashMap<>();
    private HashMap<Long, BadgeModel> tempData = new HashMap<>();
    public Context context;
    private Long[] keySet;
    private Fragment fragment;
    private Filter fRecords;


    public void setBadgeListAdapter(HashMap<Long, BadgeModel> badgeModelHashMap, Context context, BadgeFragment badgeFragment) {
        this.badgeModelHashMap = badgeModelHashMap;
        this.tempData = badgeModelHashMap;
        this.context = context;
        this.fragment = badgeFragment;
        keySet = tempData.keySet().toArray(new Long[tempData.size()]);
        // setHasStableIds(true);
    }

    public HashMap<Long, BadgeModel> getBadgeModelHashMap() {
        if (badgeModelHashMap != null) {
            return badgeModelHashMap;
        } else {
            return new HashMap<>();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_badge_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.setIsRecyclable(false);
        return viewHolder;


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (keySet != null && keySet.length == tempData.size()) {
            BadgeModel badgeModel = tempData.get(keySet[position]);
            if (badgeModel != null) {
                if (badgeModel.getBadgeName() != null && !badgeModel.getBadgeName().isEmpty()) {
                    holder.badge_name.setText(badgeModel.getBadgeName());
                }

                if (badgeModel.getBadgeIcon() != null && !badgeModel.getBadgeIcon().isEmpty()) {
                    Glide.with(context).load(badgeModel.getBadgeIcon()).error(context.getResources().getDrawable(R.drawable.trophy_cup_popup)).into(holder.badge_image);
                }

                if (badgeModel.getCompletedOn() != null && !badgeModel.getCompletedOn().isEmpty()) {
                    String convertedDate = convertDate(String.valueOf(badgeModel.getCompletedOn()), "dd MMM yyyy");
                    holder.badgeDate.setText(convertedDate);
                }
                if (badgeModel.getCourseName() != null && !badgeModel.getCourseName().isEmpty()) {
                    holder.badgeContentName.setText(badgeModel.getCourseName());
                } else {
                    String course_name = "#" + context.getResources().getString(R.string.course_text);
                    holder.badgeContentName.setText(course_name);
                }

                holder.badge_layout.setOnClickListener(v -> {
                    //handle screen with module name
                    try {
                        Intent badgeIntent = new Intent(context, CourseCompletionWithBadgeActivity.class);
                        badgeIntent.putExtra("courseId", keySet[position]);
                        badgeIntent.putExtra("position", position);
                        badgeIntent.putExtra("badgeName", badgeModel.getBadgeName());
                        badgeIntent.putExtra("badgeIcon", badgeModel.getBadgeIcon());
                        badgeIntent.putExtra("badgeModelHashMap", badgeModelHashMap);
                        badgeIntent.putExtra("isMicroCourse", false);
                        badgeIntent.putExtra("isComingFromCourseLearningMapPage", false);
                        ((Activity) context).startActivity(badgeIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                });
            }
        }
    }


    @Override
    public int getItemCount() {
        if (tempData == null) {
            return 0;
        }
        return tempData.size();
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if (fRecords == null) {
            fRecords = new RecordFilter();
        }
        return fRecords;
    }

    private class RecordFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.e("TAG", "constraint Data: " + constraint);
            FilterResults filterResults = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                constraint = constraint.toString().toLowerCase();
                HashMap<Long, BadgeModel> resultHashMap = new HashMap<>();
                for (Long key : badgeModelHashMap.keySet()) {
                    BadgeModel value = badgeModelHashMap.get(key);
                    if (value != null && value.getBadgeName().toLowerCase().contains(constraint.toString())) {
                        resultHashMap.put(key, value);
                    }
                }


                filterResults.count = resultHashMap.size();
                filterResults.values = resultHashMap;


            } else {
                filterResults.values = badgeModelHashMap;
                filterResults.count = badgeModelHashMap.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                ((BadgeFragment) fragment).searchBadgeNotFound("");
            } else {
                ((BadgeFragment) fragment).searchBadgeNotFound("notFound");
            }
            tempData = (HashMap<Long, BadgeModel>) results.values;
            keySet = tempData.keySet().toArray(new Long[tempData.size()]);
            notifyDataSetChanged();

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout badge_layout;
        CircleImageView badge_image;
        TextView badge_name;
        TextView badgeDate;
        TextView badgeContentName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            badge_layout = itemView.findViewById(R.id.badge_layout);
            badge_image = itemView.findViewById(R.id.badge_image);
            badge_name = itemView.findViewById(R.id.badge_name);
            badgeDate = itemView.findViewById(R.id.badge_date);
            badgeContentName = itemView.findViewById(R.id.badge_user_name);
        }
    }
}
