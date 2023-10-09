package com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.AdapterPostionNotifier;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewAdapterPostionNotifier;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBMemberData;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewNBMembersAdapter extends RecyclerView.Adapter<NewNBMembersAdapter.MyViewHolder> {
    private final String TAG = "NBTopicAdapter";
    List<NewNBMemberData> nbMemberDataList;
    BitmapDrawable bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image));
    BitmapDrawable bd_loading = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.profile_image_loading));
    private Context context;
    private boolean isClicked = false;
    private NewAdapterPostionNotifier adapterPostionNotifier;

    public NewNBMembersAdapter(Context context, List<NewNBMemberData> nbMemberDataList, NewAdapterPostionNotifier adapterPostionNotifier) {
        this.nbMemberDataList = nbMemberDataList;
        this.context = context;
        this.adapterPostionNotifier = adapterPostionNotifier;
    }

    public void notifyListChnage(List<NewNBMemberData> nbMemberDataList) {
        this.nbMemberDataList = nbMemberDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return nbMemberDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private LinearLayout email_ll, phone_ll;
        private TextView user_name, user_role, department, user_email, location, user_mobile;


        MyViewHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.avatar);
            email_ll = view.findViewById(R.id.email_ll);
            phone_ll = view.findViewById(R.id.phone_ll);
            user_name = view.findViewById(R.id.user_name);
            user_role = view.findViewById(R.id.user_role);
            department = view.findViewById(R.id.department);
            user_email = view.findViewById(R.id.user_email);
            location = view.findViewById(R.id.location);
            user_mobile = view.findViewById(R.id.user_mobile);

        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nb_members_item_2, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            NewNBMemberData nbMemberData = nbMemberDataList.get(position);
            if (nbMemberData.hasAvatar()) {
                Picasso.get().load(nbMemberData.getAvatar())
                        .placeholder(bd_loading).error(bd).into(holder.avatar);
            } else {
                Picasso.get().load(context.getString(R.string.oust_user_avatar_link) + nbMemberData.getAvatar())
                        .placeholder(bd_loading).error(bd).into(holder.avatar);
            }
            if (nbMemberData.getFname() != null && nbMemberData.getLname() != null) {
                holder.user_name.setText(nbMemberData.getFname() + " " + nbMemberData.getLname());
            } else if (nbMemberData.getFname() != null) {
                holder.user_name.setText(nbMemberData.getFname());
            } else if (nbMemberData.getLname() != null) {
                holder.user_name.setText(nbMemberData.getLname());
            } else {
                holder.user_name.setText(nbMemberData.getStudentid());
            }

//            setTextData(holder.user_email, holder.email_ll, nbMemberData.getEmail());
//            setTextData(holder.user_mobile, holder.phone_ll, nbMemberData.getPhone());

            holder.location.setText(nbMemberData.getUserLocation());
            if (nbMemberData.getRole() != null) {
                holder.user_role.setText(nbMemberData.getRole());
            } else {
                holder.user_role.setText("");
            }

            if (nbMemberData.getDepartment() != null) {
                holder.department.setText(nbMemberData.getDepartment());
            } else {
                holder.department.setText("");
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setTextData(TextView tv, LinearLayout ll, String message) {
        try {
            if (message == null || message.isEmpty()) {
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

}


