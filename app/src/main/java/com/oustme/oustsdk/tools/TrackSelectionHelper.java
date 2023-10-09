package com.oustme.oustsdk.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.ExoTrackSelection;
import com.google.android.exoplayer2.trackselection.FixedTrackSelection;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.oustme.oustsdk.R;

import java.util.Arrays;

public class TrackSelectionHelper implements View.OnClickListener {

    private static final FixedTrackSelection.Factory FIXED_FACTORY = (definitions, bandwidthMeter, mediaPeriodId, timeline) -> new ExoTrackSelection[0];

    private static final RandomTrackSelection.Factory RANDOM_FACTORY = new RandomTrackSelection.Factory();

    private final DefaultTrackSelector selector;
    private final AdaptiveTrackSelection.Factory adaptiveTrackSelectionFactory;

    private MappingTrackSelector.MappedTrackInfo trackInfo;
    private int rendererIndex;
    private TrackGroupArray trackGroups;
    private boolean[] trackGroupsAdaptive;
    private boolean isDisabled;
    private DefaultTrackSelector.SelectionOverride override;

    private CheckedTextView disableView;
    private CheckedTextView defaultView;
    private CheckedTextView enableRandomAdaptationView;
    private CheckedTextView[][] trackViews;

    /**
     * @param selector                      The track selector.
     * @param adaptiveTrackSelectionFactory A factory for adaptive {@link TrackSelection}s, or null
     *                                      if the selection helper should not support adaptive tracks.
     */
    public TrackSelectionHelper(DefaultTrackSelector selector,
                                AdaptiveTrackSelection.Factory adaptiveTrackSelectionFactory) {
        this.selector = selector;
        this.adaptiveTrackSelectionFactory = adaptiveTrackSelectionFactory;
    }

    /**
     * Shows the selection dialog for a given renderer.
     *
     * @param activity      The parent activity.
     * @param title         The dialog's title.
     * @param trackInfo     The current track information.
     * @param rendererIndex The index of the renderer.
     */
    public void showSelectionDialog(Activity activity, CharSequence title, MappingTrackSelector.MappedTrackInfo trackInfo, int rendererIndex) {
        this.trackInfo = trackInfo;
        this.rendererIndex = rendererIndex;

        trackGroups = trackInfo.getTrackGroups(rendererIndex);
        trackGroupsAdaptive = new boolean[trackGroups.length];
        for (int i = 0; i < trackGroups.length; i++) {
            trackGroupsAdaptive[i] = adaptiveTrackSelectionFactory != null
                    && trackInfo.getAdaptiveSupport(rendererIndex, i, false)
                    != RendererCapabilities.ADAPTIVE_NOT_SUPPORTED
                    && trackGroups.get(i).length > 1;
        }
        isDisabled = selector.getParameters().getRendererDisabled(rendererIndex);
        override = selector.getParameters().getSelectionOverride(rendererIndex, trackGroups);

        View popUpView = activity.getLayoutInflater().inflate(R.layout.track_selection_dialog, null);
        final PopupWindow video_preset_popup = OustSdkTools.createPopUp(popUpView);
        LinearLayout root = popUpView.findViewById(R.id.root);
        buildView(activity, root);
        Button ok_layout = popUpView.findViewById(R.id.ok_layout);
        Button cancel_layout = popUpView.findViewById(R.id.cancel_layout);
        ok_layout.setOnClickListener(v -> {
            onOkClick();
            video_preset_popup.dismiss();
        });
        cancel_layout.setOnClickListener(v -> video_preset_popup.dismiss());
    }

    @SuppressLint({"InflateParams"})
    private void buildView(Context context, ViewGroup root) {
        LayoutInflater inflater = LayoutInflater.from(context);

        TypedArray attributeArray = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
        int selectableItemBackgroundResourceId = attributeArray.getResourceId(0, 0);
        attributeArray.recycle();


        // View for clearing the override to allow the selector to use its default selection logic.
        defaultView = (CheckedTextView) inflater.inflate(android.R.layout.simple_list_item_single_choice, root, false);
        defaultView.setBackgroundResource(selectableItemBackgroundResourceId);
        defaultView.setText("Auto");
        defaultView.setTextColor(Color.WHITE);
        defaultView.setFocusable(true);
        defaultView.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            defaultView.setCheckMarkTintList(ColorStateList.valueOf(Color.WHITE));
        }
//        root.addView(inflater.inflate(R.layout.list_divider, root, false));
        root.addView(defaultView);

        // Per-track views.
        boolean haveAdaptiveTracks = false;
        String previoustrack = "abc";
        trackViews = new CheckedTextView[trackGroups.length][];
        for (int groupIndex = 0; groupIndex < trackGroups.length; groupIndex++) {
            TrackGroup group = trackGroups.get(groupIndex);
            boolean groupIsAdaptive = trackGroupsAdaptive[groupIndex];
            haveAdaptiveTracks |= groupIsAdaptive;
            trackViews[groupIndex] = new CheckedTextView[group.length];
            for (int trackIndex = 0; trackIndex < group.length; trackIndex++) {
                if (trackIndex == 0) {
                    root.addView(inflater.inflate(R.layout.list_divider, root, false));
                }
                if (!previoustrack.equalsIgnoreCase(DemoUtil.buildTrackName(group.getFormat(trackIndex)))) {
                    CheckedTextView trackView = (CheckedTextView) inflater.inflate(android.R.layout.simple_list_item_single_choice, root, false);
                    trackView.setBackgroundResource(selectableItemBackgroundResourceId);
                    trackView.setText(DemoUtil.buildTrackName(group.getFormat(trackIndex)));
                    previoustrack = DemoUtil.buildTrackName(group.getFormat(trackIndex));
                    trackView.setTextColor(Color.WHITE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        trackView.setCheckMarkTintList(ColorStateList.valueOf(Color.WHITE));
                    }
                    if (trackInfo.getTrackSupport(rendererIndex, groupIndex, trackIndex) == RendererCapabilities.FORMAT_HANDLED) {
                        trackView.setFocusable(true);
                        trackView.setTag(Pair.create(groupIndex, trackIndex));
                        trackView.setOnClickListener(this);
                    } else {
                        trackView.setFocusable(false);
                        trackView.setEnabled(false);
                    }
                    trackViews[groupIndex][trackIndex] = trackView;
                    root.addView(trackView);
                }
            }
        }

        updateViews();
    }

    private void updateViews() {
        if (trackViews != null) {
            defaultView.setChecked(!isDisabled && override == null);
            if (defaultView.isChecked()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    defaultView.setCheckMarkTintList(ColorStateList.valueOf(Color.WHITE));
                }
            }
            for (int i = 0; i < trackViews.length; i++) {
                for (int j = 0; j < trackViews[i].length; j++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (trackViews[i][j] != null) {
                            trackViews[i][j].setChecked(override != null && override.groupIndex == i && override.containsTrack(j));
                            if (trackViews[i][j].isChecked()) {
                                trackViews[i][j].setCheckMarkTintList(ColorStateList.valueOf(Color.WHITE));
                            }
                        }
                    }
                }
            }
            if (enableRandomAdaptationView != null) {
                boolean enableView = !isDisabled && override != null && override.length > 1;
                enableRandomAdaptationView.setEnabled(enableView);
                enableRandomAdaptationView.setFocusable(enableView);
                if (enableView) {
                    enableRandomAdaptationView.setChecked(!isDisabled);
                }
            }
        } else {
            OustSdkTools.showToast("No tracks available");
        }
    }



    public void onOkClick() {
        DefaultTrackSelector.ParametersBuilder parametersBuilder = new DefaultTrackSelector.ParametersBuilder();
        parametersBuilder.setRendererDisabled(rendererIndex, isDisabled);
        if (override != null) {
            parametersBuilder.setSelectionOverride(rendererIndex, trackGroups, override);
        } else {
            parametersBuilder.clearSelectionOverrides(rendererIndex);
        }
        selector.setParameters(parametersBuilder);
    }

    @Override
    public void onClick(View view) {
        if (view == defaultView) {
            isDisabled = false;
            override = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                defaultView.setCheckMarkTintList(ColorStateList.valueOf(Color.WHITE));
            }
        } else {
            isDisabled = false;
            @SuppressWarnings("unchecked")
            Pair<Integer, Integer> tag = (Pair<Integer, Integer>) view.getTag();
            int groupIndex = tag.first;
            int trackIndex = tag.second;
            for (int i = 0; i < trackGroups.length; i++) {
                TrackGroup group = trackGroups.get(groupIndex);
                for (int j = 0; j < group.length; j++) {
                    if (trackViews[i][j] != null) {
                        Pair<Integer, Integer> tag1 = (Pair<Integer, Integer>) trackViews[i][j].getTag();
                        if (tag1 != tag) {
                            trackViews[i][j].setChecked(false);
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                trackViews[i][j].setCheckMarkTintList(ColorStateList.valueOf(Color.WHITE));
                            }
                        }
                    }
                }
            }
            override = new DefaultTrackSelector.SelectionOverride(groupIndex, trackIndex);
        }
        // Update the views with the new state.
        updateViews();
    }

    private void setOverride(int group, int[] tracks, boolean enableRandomAdaptation) {
        AdaptiveTrackSelection.Factory factory;
        if (tracks.length == 1) {
            factory = (AdaptiveTrackSelection.Factory) FIXED_FACTORY;

        } else {
            factory = adaptiveTrackSelectionFactory;
        }
        override = new DefaultTrackSelector.SelectionOverride(group, tracks);
    }

    // Track array manipulation.

    private static int[] getTracksAdding(DefaultTrackSelector.SelectionOverride override, int addedTrack) {
        int[] tracks = override.tracks;
        tracks = Arrays.copyOf(tracks, tracks.length + 1);
        tracks[tracks.length - 1] = addedTrack;
        return tracks;
    }

    private static int[] getTracksRemoving(DefaultTrackSelector.SelectionOverride override, int removedTrack) {
        int[] tracks = new int[override.length - 1];
        int trackCount = 0;
        for (int i = 0; i < tracks.length + 1; i++) {
            int track = override.tracks[i];
            if (track != removedTrack) {
                tracks[trackCount++] = track;
            }
        }
        return tracks;
    }

}