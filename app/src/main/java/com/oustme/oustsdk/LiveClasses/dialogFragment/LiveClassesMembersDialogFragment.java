package com.oustme.oustsdk.LiveClasses.dialogFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.oustme.oustsdk.LiveClasses.MeetingActivity;
import com.oustme.oustsdk.LiveClasses.adapter.MembersAdapter;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.request.MeetingCreateParticipant;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class LiveClassesMembersDialogFragment extends BottomSheetDialogFragment {

    TextView meeting_attendees_txt;
    ImageView meeting_attendees_close;
    RecyclerView members_recycleView;
    ArrayList<MeetingCreateParticipant> participantArrayList;
    Context context;
    ActiveUser activeUser;
    SetIsDialogFragmentShown setIsDialogFragmentShown;

    public LiveClassesMembersDialogFragment(ArrayList<MeetingCreateParticipant> participantArrayList, ActiveUser activeUser, MeetingActivity meetingActivity, SetIsDialogFragmentShown isDialogFragmentShown) {
        this.participantArrayList = participantArrayList;
        this.context = meetingActivity;
        this.activeUser = activeUser;
        setIsDialogFragmentShown = isDialogFragmentShown;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
        bottomSheetDialog.setContentView(R.layout.fragment_live_classes_members_dialog);
        try {
            bottomSheetDialog.setCanceledOnTouchOutside(false);
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setOnKeyListener((dialog1, keyCode, event) -> keyCode == android.view.KeyEvent.KEYCODE_BACK);
            Field behaviorField = bottomSheetDialog.getClass().getDeclaredField("behavior");
            behaviorField.setAccessible(true);
            BottomSheetBehavior behavior = (BottomSheetBehavior) behaviorField.get(bottomSheetDialog);
            if (behavior != null) {
                behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_live_classes_members_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        meeting_attendees_txt = view.findViewById(R.id.meeting_attendees_txt);
        meeting_attendees_close = view.findViewById(R.id.meeting_attendees_close);
        members_recycleView = view.findViewById(R.id.members_recycleView);

        if (participantArrayList != null && participantArrayList.size() > 0) {
            meeting_attendees_txt.setText(context.getResources().getString(R.string.attendees) + " " + participantArrayList.size());
            if (members_recycleView != null) {
                MembersAdapter membersAdapter = new MembersAdapter(participantArrayList, activeUser, context);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                members_recycleView.setLayoutManager(mLayoutManager);
                members_recycleView.setAdapter(membersAdapter);
            }
        }

        meeting_attendees_close.setOnClickListener(v -> {
            dismiss();
            if (setIsDialogFragmentShown != null) {
                setIsDialogFragmentShown.setIsDialogFragmentShown(false);
            }
        });
    }

    public interface SetIsDialogFragmentShown {
        void setIsDialogFragmentShown(boolean status);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (setIsDialogFragmentShown != null) {
                setIsDialogFragmentShown.setIsDialogFragmentShown(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}