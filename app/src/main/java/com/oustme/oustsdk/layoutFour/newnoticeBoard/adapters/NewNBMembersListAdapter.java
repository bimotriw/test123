package com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.oustme.oustsdk.catalogue_ui.adapter.CatalogueFolderAdapter;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.activity.NewNBMembersList;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.activity.NewNBTopicDetailActivity;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBMemberData;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

public class NewNBMembersListAdapter extends RecyclerView.Adapter<NewNBMembersListAdapter.MyViewholder> {
    private final String TAG = "NewNBMembersListAdapter";
    List<NewNBMemberData> nbMemberDataList;
    private Context context;
    BitmapDrawable bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image));
    Drawable bd1 = OustSdkApplication.getContext().getResources().getDrawable(R.drawable.ic_person_profile_image_nb);
    BitmapDrawable bd_loading = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading));
    int val = 0;

    public NewNBMembersListAdapter(NewNBMembersList newNBMembersList, ArrayList<NewNBMemberData> nbMemberDataList, int val) {
        this.context = newNBMembersList;
        this.nbMemberDataList = nbMemberDataList;
        this.val = val;
    }

    public NewNBMembersListAdapter(NewNBTopicDetailActivity newNBTopicDetailActivity, ArrayList<NewNBMemberData> nbMemberDataList, int val) {
        this.context = newNBTopicDetailActivity;
        this.nbMemberDataList = nbMemberDataList;
        this.val = val;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (val == 2) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nb_members_list_item_3, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nb_members_list_item_2, parent, false);
        }

        MyViewholder viewHolder = new MyViewholder(itemView);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, int position) {
        try {
            if (val == 2) {
                if (nbMemberDataList.get(position).hasAvatar()) {
                    Picasso.get().load(nbMemberDataList.get(position).getAvatar())
                            .placeholder(bd_loading).error(bd1).into(holder.avatar);
                } else {
                    Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + nbMemberDataList.get(position).getAvatar())
                            .placeholder(bd_loading).error(bd1).into(holder.avatar);
                }
            } else {
                if (nbMemberDataList.get(position).hasAvatar()) {
                    Picasso.get().load(nbMemberDataList.get(position).getAvatar())
                            .placeholder(bd_loading).error(bd1).into(holder.avatar);
                } else {
                    Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + nbMemberDataList.get(position).getAvatar())
                            .placeholder(bd_loading).error(bd1).into(holder.avatar);
                }
                if ((nbMemberDataList.get(position).getFname() != null && !nbMemberDataList.get(position).getFname().equals("null")) &&
                        (nbMemberDataList.get(position).getLname() != null && !nbMemberDataList.get(position).getLname().equals("null"))) {
                    holder.user_name.setText(WordUtils.capitalize(nbMemberDataList.get(position).getFname() + " " + nbMemberDataList.get(position).getLname()));
                } else if (nbMemberDataList.get(position).getFname() != null && !nbMemberDataList.get(position).getFname().equals("null")) {
                    holder.user_name.setText(WordUtils.capitalize(nbMemberDataList.get(position).getFname()));
                } else if (nbMemberDataList.get(position).getLname() != null && !nbMemberDataList.get(position).getLname().equals("null")) {
                    holder.user_name.setText(WordUtils.capitalize(nbMemberDataList.get(position).getLname()));
                } else {
                    holder.user_name.setText(WordUtils.capitalize(nbMemberDataList.get(position).getStudentid()));
                }
                if (nbMemberDataList.get(position).getEmail() != null && !nbMemberDataList.get(position).getEmail().equals("null")) {
                    setTextData(holder.user_email, holder.email_ll, nbMemberDataList.get(position).getEmail());
                }

                if (nbMemberDataList.get(position).getPhone() != null && !nbMemberDataList.get(position).getPhone().equals("null")) {
                    setTextData(holder.user_mobile, holder.phone_ll, nbMemberDataList.get(position).getPhone());
                }

                if (nbMemberDataList.get(position).getUserLocation() != null && !nbMemberDataList.get(position).getUserLocation().equals("null")) {
                    holder.location.setText(nbMemberDataList.get(position).getUserLocation());
                    holder.location.setVisibility(View.VISIBLE);
                } else {
                    holder.location.setVisibility(View.GONE);
                    holder.location.setText("");
                }

                if (nbMemberDataList.get(position).getRole() != null && !nbMemberDataList.get(position).getRole().equals("null")) {
                    holder.user_role.setText(nbMemberDataList.get(position).getRole());
                } else {
                    holder.user_role.setText("");
                }

                if (nbMemberDataList.get(position).getDepartment() != null && !nbMemberDataList.get(position).getDepartment().equals("null")) {
                    holder.department.setVisibility(View.VISIBLE);
                    holder.department.setText(nbMemberDataList.get(position).getDepartment());
                } else {
                    holder.department.setVisibility(View.GONE);
                    holder.department.setText("");
                }

                if (nbMemberDataList.get(position).getDesignation() != null && !nbMemberDataList.get(position).getDesignation().equals("null")) {
                    holder.designation.setVisibility(View.VISIBLE);
                    holder.designation.setText(nbMemberDataList.get(position).getDesignation() + "  ");
                } else {
                    holder.designation.setVisibility(View.GONE);
                    holder.designation.setText("");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setTextData(TextView tv, LinearLayout ll, String message) {
        try {
            if (message == null || message.isEmpty() || message.equals("null")) {
                ll.setVisibility(View.GONE);
            } else {
                ll.setVisibility(View.VISIBLE);
                tv.setText("" + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        return nbMemberDataList.size();
    }

    public class MyViewholder extends RecyclerView.ViewHolder {

        private CircleImageView avatar;
        private LinearLayout email_ll, phone_ll;
        private TextView user_name, user_role, department, user_email, location, user_mobile, designation;

        public MyViewholder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.avatar);
            email_ll = itemView.findViewById(R.id.email_ll);
            phone_ll = itemView.findViewById(R.id.phone_ll);
            user_name = itemView.findViewById(R.id.user_name);
            user_role = itemView.findViewById(R.id.user_role);
            department = itemView.findViewById(R.id.department);
            user_email = itemView.findViewById(R.id.user_email);
            location = itemView.findViewById(R.id.location);
            user_mobile = itemView.findViewById(R.id.user_mobile);
            designation = itemView.findViewById(R.id.designation);

        }
    }
}

