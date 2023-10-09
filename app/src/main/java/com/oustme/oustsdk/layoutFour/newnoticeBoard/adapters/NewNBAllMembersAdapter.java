package com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.activity.NewNBMembersList;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.activity.NewNBMembersListActivity;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBAllMemberData;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

public class NewNBAllMembersAdapter extends RecyclerView.Adapter<NewNBAllMembersAdapter.viewHolder> {

    private static final int USER_VIEW_TYPE = 0;
    private static final int GROUP_VIEW_TYPE = 1;
    Context context;
    private boolean isClicked = false;
    private long nbId = 0;

    ArrayList<NewNBAllMemberData> nbAllMemberDataArrayList = new ArrayList<NewNBAllMemberData>();

    //    List<NewNBMemberData> nbMemberDataList;
//    List<NewNBGroupData> nbGroupDataList;
    Drawable bd1 = OustSdkApplication.getContext().getResources().getDrawable(R.drawable.ic_person_profile_image_nb);
    Drawable group = OustSdkApplication.getContext().getResources().getDrawable(R.drawable.nb_group);
    BitmapDrawable bd_loading = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading));

    public NewNBAllMembersAdapter(NewNBMembersList newNBMembersList, ArrayList<NewNBAllMemberData> nbAllMemberDataArrayList) {
        this.context = newNBMembersList;
        this.nbAllMemberDataArrayList = nbAllMemberDataArrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
//        switch (viewType) {
//            case USER_VIEW_TYPE:
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nb_members_list_item_2, parent, false);
        return new viewHolder(view);
//            case GROUP_VIEW_TYPE:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nb_mem_list_item_2, parent, false);
//                return new GroupViewHolder(view);
//            default:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nb_members_list_item_2, parent, false);
//                return new UserViewHolder(view);
//        }
    }


    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        try {
            if (nbAllMemberDataArrayList.get(position).getGroupId() != 0) {
                try {
                    holder.user_name.setText(nbAllMemberDataArrayList.get(position).getGroupName());
                    holder.department.setText(nbAllMemberDataArrayList.get(position).getCreatorId());
                    if (nbAllMemberDataArrayList.get(position).hasAvatar()) {
                        Picasso.get().load(nbAllMemberDataArrayList.get(position).getAvatar())
                                .placeholder(group).error(group).into(holder.avatar);
                    } else {
                        Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + nbAllMemberDataArrayList.get(position).getAvatar())
                                .placeholder(group).error(group).into(holder.avatar);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } else {
                if (nbAllMemberDataArrayList.get(position).hasAvatar()) {
                    Picasso.get().load(nbAllMemberDataArrayList.get(position).getAvatar())
                            .placeholder(bd_loading).error(bd1).into(holder.avatar);
                } else {
                    Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + nbAllMemberDataArrayList.get(position).getAvatar())
                            .placeholder(bd_loading).error(bd1).into(holder.avatar);
                }
                if ((nbAllMemberDataArrayList.get(position).getFname() != null && !nbAllMemberDataArrayList.get(position).getFname().equals("null")) &&
                        (nbAllMemberDataArrayList.get(position).getLname() != null && !nbAllMemberDataArrayList.get(position).getLname().equals("null"))) {
                    holder.user_name.setText(WordUtils.capitalize(nbAllMemberDataArrayList.get(position).getFname() + " " + nbAllMemberDataArrayList.get(position).getLname()));
                } else if (nbAllMemberDataArrayList.get(position).getFname() != null && !nbAllMemberDataArrayList.get(position).getFname().equals("null")) {
                    holder.user_name.setText(WordUtils.capitalize(nbAllMemberDataArrayList.get(position).getFname()));
                } else if (nbAllMemberDataArrayList.get(position).getLname() != null && !nbAllMemberDataArrayList.get(position).getLname().equals("null")) {
                    holder.user_name.setText(WordUtils.capitalize(nbAllMemberDataArrayList.get(position).getLname()));
                } else {
                    holder.user_name.setText(WordUtils.capitalize(nbAllMemberDataArrayList.get(position).getStudentid()));
                }
                if (nbAllMemberDataArrayList.get(position).getEmail() != null && !nbAllMemberDataArrayList.get(position).getEmail().equals("null")) {
                    setTextData(holder.user_email, holder.email_ll, nbAllMemberDataArrayList.get(position).getEmail());
                }

                if (nbAllMemberDataArrayList.get(position).getPhone() != null && !nbAllMemberDataArrayList.get(position).getPhone().equals("null")) {
                    setTextData(holder.user_mobile, holder.phone_ll, nbAllMemberDataArrayList.get(position).getPhone());
                }

                if (nbAllMemberDataArrayList.get(position).getUserLocation() != null && !nbAllMemberDataArrayList.get(position).getUserLocation().equals("null")) {
                    holder.location.setText(nbAllMemberDataArrayList.get(position).getUserLocation());
                    holder.location.setVisibility(View.VISIBLE);
                } else {
                    holder.location.setVisibility(View.GONE);
                    holder.location.setText("");
                }

                if (nbAllMemberDataArrayList.get(position).getRole() != null && !nbAllMemberDataArrayList.get(position).getRole().equals("null")) {
                    holder.user_role.setText(nbAllMemberDataArrayList.get(position).getRole());
                } else {
                    holder.user_role.setText("");
                }

                if (nbAllMemberDataArrayList.get(position).getDepartment() != null && !nbAllMemberDataArrayList.get(position).getDepartment().equals("null")) {
                    holder.department.setVisibility(View.VISIBLE);
                    holder.department.setText(nbAllMemberDataArrayList.get(position).getDepartment());
                } else {
                    holder.department.setVisibility(View.GONE);
                    holder.department.setText("");
                }

                if (nbAllMemberDataArrayList.get(position).getDesignation() != null && !nbAllMemberDataArrayList.get(position).getDesignation().equals("null")) {
                    holder.designation.setVisibility(View.VISIBLE);
                    holder.designation.setText(nbAllMemberDataArrayList.get(position).getDesignation() + "  ");
                } else {
                    holder.designation.setVisibility(View.GONE);
                    holder.designation.setText("");
                }
            }
            holder.main_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (nbAllMemberDataArrayList.get(position).getGroupId() != 0
                            && nbAllMemberDataArrayList.get(position).getGroupName() != null && !nbAllMemberDataArrayList.get(position).getGroupName().isEmpty()
                            && !nbAllMemberDataArrayList.get(position).getGroupName().equalsIgnoreCase("null")) {
                        Intent intent = new Intent(context, NewNBMembersList.class);
                        intent.putExtra("groupId", nbAllMemberDataArrayList.get(position).getGroupId());
                        intent.putExtra("nbId", nbId);
                        context.startActivity(intent);
                    }

                }
            });
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

    private void clickView(View view, final long groupId) {
        if (!isClicked) {
            isClicked = true;
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.94f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.96f);
            scaleDownX.setDuration(150);
            scaleDownY.setDuration(150);
            scaleDownX.setRepeatCount(1);
            scaleDownY.setRepeatCount(1);
            scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    isClicked = false;
                    openGroupMembersPage(groupId);

                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        }
    }

    private void openGroupMembersPage(long groupId) {
        Intent intent = new Intent(context, NewNBMembersListActivity.class);
        intent.putExtra("groupId", groupId);
        intent.putExtra("nbId", nbId);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return nbAllMemberDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position % 2 == 0) ? GROUP_VIEW_TYPE : USER_VIEW_TYPE;
    }

    public void setNbId(long nbId) {
        this.nbId = nbId;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatar;
        private LinearLayout email_ll, phone_ll;
        private TextView user_name, user_role, department, user_email, location, user_mobile, designation;

        public UserViewHolder(View view) {
            super(view);
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

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        LinearLayout main_ll;
        TextView user_name, department;

        public GroupViewHolder(View view) {
            super(view);
            user_name = itemView.findViewById(R.id.user_name);
            department = itemView.findViewById(R.id.department);
            main_ll = itemView.findViewById(R.id.main_ll);
        }
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatar;
        private LinearLayout email_ll, phone_ll;
        private TextView user_name, user_role, department, user_email, location, user_mobile, designation;
        LinearLayout main_ll;

        public viewHolder(View view) {
            super(view);
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
            main_ll = itemView.findViewById(R.id.main_ll);
        }
    }
}
