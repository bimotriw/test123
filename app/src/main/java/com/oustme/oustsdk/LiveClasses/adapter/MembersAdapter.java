package com.oustme.oustsdk.LiveClasses.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.request.MeetingCreateParticipant;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {
    ArrayList<MeetingCreateParticipant> participantArrayList;
    Context context;
    ActiveUser activeUser;

    public MembersAdapter(ArrayList<MeetingCreateParticipant> participantArrayList, ActiveUser activeUser, Context context) {
        this.participantArrayList = participantArrayList;
        this.context = context;
        this.activeUser = activeUser;
    }

    @NonNull
    @Override
    public MembersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_classes_members_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersAdapter.ViewHolder holder, int position) {
        try {
            MeetingCreateParticipant meetingCreateParticipant = participantArrayList.get(position);
            if (meetingCreateParticipant != null) {
                if (meetingCreateParticipant.isScreenSharing()) {
                    holder.live_class_members_mic.setVisibility(View.GONE);
                    holder.live_class_members_camera.setVisibility(View.GONE);
                    holder.live_class_screen_presenting.setVisibility(View.VISIBLE);
                    if (activeUser.getStudentid().equalsIgnoreCase(meetingCreateParticipant.getExternalUserId())) {
                        holder.live_class_screen_presenting.setText(context.getResources().getString(R.string.your_presentation));
                    } else {
                        holder.live_class_screen_presenting.setText(meetingCreateParticipant.getExternalUserId() + " " + context.getResources().getString(R.string.is_presenting));
                    }
                } else {
                    holder.live_class_members_mic.setVisibility(View.VISIBLE);
                    holder.live_class_members_camera.setVisibility(View.VISIBLE);
                    holder.live_class_screen_presenting.setVisibility(View.GONE);
                }
                holder.live_class_members_name.setText(meetingCreateParticipant.getExternalUserId());
                if (meetingCreateParticipant.isMuted()) {
                    holder.live_class_members_mic.setBackground(context.getResources().getDrawable(R.drawable.ic_mic));
                } else {
                    holder.live_class_members_mic.setBackground(context.getResources().getDrawable(R.drawable.ic_mic_off));
                }

                if (meetingCreateParticipant.isVideo()) {
                    holder.live_class_members_camera.setBackground(context.getResources().getDrawable(R.drawable.videocam_icon));
                } else {
                    holder.live_class_members_camera.setBackground(context.getResources().getDrawable(R.drawable.videocam_off));
                }

                holder.members_text_view_title.setText("" + meetingCreateParticipant.getExternalUserId().charAt(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        return participantArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView member_image;
        ImageView live_class_members_mic;
        ImageView live_class_members_camera;
        TextView members_text_view_title;
        TextView live_class_members_name;
        TextView live_class_screen_presenting;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            member_image = itemView.findViewById(R.id.member_image);
            members_text_view_title = itemView.findViewById(R.id.members_text_view_title);
            live_class_members_name = itemView.findViewById(R.id.live_class_members_name);
            live_class_members_mic = itemView.findViewById(R.id.live_class_members_mic);
            live_class_members_camera = itemView.findViewById(R.id.live_class_members_camera);
            live_class_screen_presenting = itemView.findViewById(R.id.live_class_screen_presenting);
        }
    }
}
