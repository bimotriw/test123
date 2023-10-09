package com.oustme.oustsdk.layoutFour.components.leaderBoard.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.clevertap.android.sdk.CleverTapAPI;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.UserProfileActivity;
import com.oustme.oustsdk.layoutFour.data.response.LeaderBoardDataList;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderBoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private ArrayList<LeaderBoardDataList> leaderBoardDataArrayList = new ArrayList<>();
    public Context context;
    ValueFilter valueFilter;
    ArrayList<LeaderBoardDataList> mData;
    String typeIs;
    BitmapDrawable bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image));
    BitmapDrawable bd_loading = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading));


    public void setLeaderBoardAdapter(ArrayList<LeaderBoardDataList> leaderBoardDataArrayList, Context context, String typeIs) {
        this.leaderBoardDataArrayList = leaderBoardDataArrayList;
        this.mData = leaderBoardDataArrayList;
        this.context = context;
        this.typeIs = typeIs;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == 0 || viewType == 2) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_adapter, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            viewHolder.setIsRecyclable(false);
            return viewHolder;
        }
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_normal, parent, false);
        NormalViewHolder normalviewHolder = new NormalViewHolder(v);
        normalviewHolder.setIsRecyclable(false);
        return normalviewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        LeaderBoardDataList leaderBoardData = mData.get(position);
        if (leaderBoardData != null) {
            String rank_user_image = leaderBoardData.getAvatar();
            String rank_user_rank = leaderBoardData.getRank() + "";
            String displayName = "" + leaderBoardData.getDisplayName();
            String score = "" + OustSdkTools.formatMilliinFormat(leaderBoardData.getScore());

            if (holder.getItemViewType() == 0) {
                ViewHolder viewHolder = (ViewHolder) holder;
                if (rank_user_image != null && !rank_user_image.isEmpty()) {
                    if (rank_user_image.startsWith("http") || rank_user_image.startsWith("https")) {
                        Picasso.get().load(rank_user_image)
                                .placeholder(bd_loading).error(bd)
                                .into(viewHolder.user_image);
                    } else {
                        Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + rank_user_image)
                                .placeholder(bd_loading).error(bd).into(viewHolder.user_image);
                    }
                }

                viewHolder.user_name.setText(displayName);
                viewHolder.user_score.setText(score);
                viewHolder.serial_no.setText(rank_user_rank);

                if (leaderBoardData.getRank() == 1) {


                    viewHolder.leaderboard_border.setBackground(setBorderColor(context.getResources().getDrawable(R.drawable.card_rounded_ten), OustPreferences.get("toolbarColorCode")));

                }
                if (leaderBoardData.getRank() == 2) {

                    viewHolder.leaderboard_border.setBackground(setBorderColor(context.getResources().getDrawable(R.drawable.card_rounded_ten), OustPreferences.get("secondaryColor")));

                }
                if (leaderBoardData.getRank() == 3) {

                    viewHolder.leaderboard_border.setBackground(setBorderColor(context.getResources().getDrawable(R.drawable.card_rounded_ten), OustPreferences.get("treasuryColor")));

                }

                viewHolder.leaderboard_border.setOnClickListener(v -> {
                    try {
                        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
                        HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                        eventUpdate.put("ClickedOnUserList", true);
                        Log.d("TAG", "CleverTap instance: " + eventUpdate.toString());
                        if (clevertapDefaultInstance != null) {
                            clevertapDefaultInstance.pushEvent("Leaderboard_List_User_Click", eventUpdate);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    if (!leaderBoardData.getUserid().equalsIgnoreCase(OustSdkTools.getActiveUserData().getStudentid())) {
                        Intent intent = new Intent(context, UserProfileActivity.class);
                        intent.putExtra("avatar", ("" + leaderBoardData.getAvatar()));
                        intent.putExtra("name", ("" + leaderBoardData.getDisplayName()));
                        intent.putExtra("studentId", ("" + leaderBoardData.getUserid()));
                        intent.putExtra("xp", ("" + leaderBoardData.getScore()));
                        intent.putExtra("rank", ("" + leaderBoardData.getRank()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        // }
                    }
                });

            } else if (holder.getItemViewType() == 2) {
                ViewHolder viewHolder1 = (ViewHolder) holder;
                if (rank_user_image != null && !rank_user_image.isEmpty()) {
                    if (rank_user_image.startsWith("http") || rank_user_image.startsWith("https")) {
                        Picasso.get().load(rank_user_image)
                                .placeholder(bd_loading).error(bd)
                                .into(viewHolder1.user_image);
                    } else {
                        Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + rank_user_image)
                                .placeholder(bd_loading).error(bd).into(viewHolder1.user_image);
                    }
                }

                viewHolder1.user_name.setText(displayName);
                viewHolder1.user_score.setText(score);
                viewHolder1.serial_no.setText(rank_user_rank);

                if (position == 0) {
                    viewHolder1.leaderboard_border.setBackground(setBorderColor(context.getResources().getDrawable(R.drawable.card_rounded_ten), OustPreferences.get("toolbarColorCode")));
                }
                if (position == 1) {
                    viewHolder1.leaderboard_border.setBackground(setBorderColor(context.getResources().getDrawable(R.drawable.card_rounded_ten), OustPreferences.get("secondaryColor")));
                }
                if (position == 2) {
                    viewHolder1.leaderboard_border.setBackground(setBorderColor(context.getResources().getDrawable(R.drawable.card_rounded_ten), OustPreferences.get("treasuryColor")));
                }

                viewHolder1.leaderboard_border.setOnClickListener(v -> {
                    try {
                        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
                        HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                        eventUpdate.put("ClickedOnUserList", true);
                        Log.d("TAG", "CleverTap instance: " + eventUpdate.toString());
                        if (clevertapDefaultInstance != null) {
                            clevertapDefaultInstance.pushEvent("Leaderboard_List_User_Click", eventUpdate);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    if (!leaderBoardData.getUserid().equalsIgnoreCase(OustSdkTools.getActiveUserData().getStudentid())) {
                        Intent intent = new Intent(context, UserProfileActivity.class);
                        intent.putExtra("avatar", ("" + leaderBoardData.getAvatar()));
                        intent.putExtra("name", ("" + leaderBoardData.getDisplayName()));
                        intent.putExtra("studentId", ("" + leaderBoardData.getUserid()));
                        intent.putExtra("xp", ("" + leaderBoardData.getScore()));
                        intent.putExtra("rank", ("" + leaderBoardData.getRank()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        // }
                    }
                });
            } else {
                NormalViewHolder normalviewHolder = (NormalViewHolder) holder;
                if (rank_user_image != null && !rank_user_image.isEmpty()) {
                    if (rank_user_image.startsWith("http") || rank_user_image.startsWith("https")) {
                        Picasso.get().load(rank_user_image)
                                .placeholder(bd_loading).error(bd)
                                .into(normalviewHolder.user_image);
                    } else {
                        Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + rank_user_image)
                                .placeholder(bd_loading).error(bd).into(normalviewHolder.user_image);
                    }
                }

                normalviewHolder.user_name.setText(displayName);
                normalviewHolder.user_score.setText(score);
                normalviewHolder.serial_no.setText(rank_user_rank);

                normalviewHolder.leaderboard_border.setOnClickListener(v -> {
                    try {
                        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
                        HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                        eventUpdate.put("ClickedOnUserList", true);
                        Log.d("TAG", "CleverTap instance: " + eventUpdate.toString());
                        if (clevertapDefaultInstance != null) {
                            clevertapDefaultInstance.pushEvent("Leaderboard_List_User_Click", eventUpdate);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    if (!leaderBoardData.getUserid().equalsIgnoreCase(OustSdkTools.getActiveUserData().getStudentid())) {
                        Intent intent = new Intent(context, UserProfileActivity.class);
                        intent.putExtra("avatar", ("" + leaderBoardData.getAvatar()));
                        intent.putExtra("name", ("" + leaderBoardData.getDisplayName()));
                        intent.putExtra("studentId", ("" + leaderBoardData.getUserid()));
                        intent.putExtra("xp", ("" + leaderBoardData.getScore()));
                        intent.putExtra("rank", ("" + leaderBoardData.getRank()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        // }
                    }
                });
            }
        }
    }


    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (mData != null) {
            if (typeIs.equalsIgnoreCase("filter")) {
                if (mData.get(position).getRank() > 3) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                if (position > 2) {
                    return 1;
                } else {
                    return 2;
                }
            }
        } else {
            return 1;
        }

        //  return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leaderboard_border;
        TextView serial_no;
        CircleImageView user_image;
        TextView user_name;
        TextView user_score;

        public ViewHolder(View itemView) {
            super(itemView);
            leaderboard_border = itemView.findViewById(R.id.leaderboard_border);
            serial_no = itemView.findViewById(R.id.serial_no);
            user_image = itemView.findViewById(R.id.user_image);
            user_name = itemView.findViewById(R.id.user_name);
            user_score = itemView.findViewById(R.id.user_score);
        }
    }

    public static class NormalViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leaderboard_border;
        TextView serial_no;
        CircleImageView user_image;
        TextView user_name;
        TextView user_score;

        public NormalViewHolder(View itemView) {
            super(itemView);
            leaderboard_border = itemView.findViewById(R.id.leaderboard_border);
            serial_no = itemView.findViewById(R.id.serial_no);
            user_name = itemView.findViewById(R.id.user_name);
            user_image = itemView.findViewById(R.id.user_image);
            user_score = itemView.findViewById(R.id.user_score);
        }
    }

    private Drawable setBorderColor(Drawable drawable, String color) {
        if (color != null && drawable != null) {
            DrawableCompat.setTint(
                    DrawableCompat.wrap(drawable),
                    Color.parseColor(color)
            );
        }
        return drawable;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<LeaderBoardDataList> filterList = new ArrayList<>();
                for (int i = 0; i < leaderBoardDataArrayList.size(); i++) {
                    if ((leaderBoardDataArrayList.get(i).getDisplayName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(leaderBoardDataArrayList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = leaderBoardDataArrayList.size();
                results.values = leaderBoardDataArrayList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mData = (ArrayList<LeaderBoardDataList>) results.values;
            notifyDataSetChanged();
        }
    }
}
