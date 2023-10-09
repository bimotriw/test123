package com.oustme.oustsdk.LiveClasses.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.DefaultVideoRenderView;
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSession;
import com.google.android.material.card.MaterialCardView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.request.MeetingCreateParticipant;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.Random;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewModel> {
    Context context;
    Random rnd = new Random();
    MeetingSession meetingSession;
    ActiveUser activeUser;
    ArrayList<MeetingCreateParticipant> participantArrayList;

    public MeetingAdapter(ArrayList<MeetingCreateParticipant> participantArrayList, MeetingSession meetingSession, ActiveUser activeUser, Context context) {
        this.participantArrayList = participantArrayList;
        this.meetingSession = meetingSession;
        this.context = context;
        this.activeUser = activeUser;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public ViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_attended_pepole_list, parent, false);
        return new ViewModel(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewModel holder, int position) {
        try {
            MeetingCreateParticipant meetingCreateParticipant = participantArrayList.get(position);
            if (meetingCreateParticipant != null) {
                holder.meeting_attender_audio_mute_unMute.setVisibility(View.VISIBLE);
                holder.meeting_attender_name.setText(meetingCreateParticipant.getExternalUserId());
                if (meetingCreateParticipant.isMuted()) {
                    holder.meeting_attender_audio_mute_unMute.setBackground(context.getResources().getDrawable(R.drawable.voice_state));
                } else {
                    holder.meeting_attender_audio_mute_unMute.setBackground(context.getResources().getDrawable(R.drawable.microphone_off));
                }

                if (meetingCreateParticipant.isUserSpeaking()) {
                    holder.materialCardView.setStrokeWidth(2);
                } else {
                    holder.materialCardView.setStrokeWidth(0);
                }

                holder.text_view_title.setText("" + meetingCreateParticipant.getExternalUserId().charAt(0));
                try {
                    if (meetingSession != null) {
                        if (meetingCreateParticipant.isVideo()) {
                            holder.videoSurface.setVisibility(View.VISIBLE);
                            holder.text_view_title.setVisibility(View.GONE);
                            holder.attended_image.setVisibility(View.GONE);
                            if (meetingCreateParticipant.getVideoTileState() != null) {
                                meetingSession.getAudioVideo().bindVideoView(holder.videoSurface, meetingCreateParticipant.getVideoTileState().getTileId());
                            }
                        } else {
                            holder.videoSurface.setVisibility(View.GONE);
                            holder.text_view_title.setVisibility(View.VISIBLE);
                            holder.attended_image.setVisibility(View.VISIBLE);
                            if (meetingCreateParticipant.getVideoTileState() != null) {
                                meetingSession.getAudioVideo().unbindVideoView(meetingCreateParticipant.getVideoTileState().getTileId());
                            }
                            holder.videoSurface.release();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        if (participantArrayList == null) {
            return 0;
        }
        return participantArrayList.size();
    }

    public void notifyListAdapter(ArrayList<MeetingCreateParticipant> participantArrayList) {
        this.participantArrayList = participantArrayList;
        notifyDataSetChanged();
    }

    public static class ViewModel extends RecyclerView.ViewHolder {
        TextView meeting_attender_name;
        TextView text_view_title;
        ImageView meeting_attender_audio_mute_unMute;
        DefaultVideoRenderView videoSurface;
        LinearLayout attendee_card_layout;
        ImageView attended_image;
        MaterialCardView materialCardView;

        public ViewModel(@NonNull View itemView) {
            super(itemView);
            meeting_attender_name = itemView.findViewById(R.id.meeting_attender_name);
            text_view_title = itemView.findViewById(R.id.text_view_title);
            meeting_attender_audio_mute_unMute = itemView.findViewById(R.id.meeting_attender_audio_mute);
            videoSurface = itemView.findViewById(R.id.video_surface);
            attendee_card_layout = itemView.findViewById(R.id.attendee_card_layout);
            attended_image = itemView.findViewById(R.id.attended_image);
            materialCardView = itemView.findViewById(R.id.card_main_layout);
        }
    }

    private int getRandomColor() {
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
}
