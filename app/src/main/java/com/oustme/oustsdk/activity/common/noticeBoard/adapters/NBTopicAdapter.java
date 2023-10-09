package com.oustme.oustsdk.activity.common.noticeBoard.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.activity.NBMembersListActivity;
import com.oustme.oustsdk.activity.common.noticeBoard.activity.NBTopicDetailActivity;
import com.oustme.oustsdk.activity.common.noticeBoard.data.handlers.NBDataHandler;
import com.oustme.oustsdk.activity.common.noticeBoard.extras.ClickState;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBTopicData;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModule;
import com.oustme.oustsdk.layoutFour.components.newCatalogue.adapter.CatalogueFolderAdapter;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oust on 2/18/19.
 */

public class NBTopicAdapter extends RecyclerView.Adapter<NBTopicAdapter.MyViewHolder> implements Filterable {
    private final String TAG = "NBTopicAdapter";
    List<NBTopicData> nbTopicDataArrayList;
    List<NBTopicData> mData;
    private Context context;
    ValueFilter valueFilter;
    private boolean isClicked = false;

    public NBTopicAdapter(Context context, List<NBTopicData> nbTopicDataArrayList) {
        this.nbTopicDataArrayList = nbTopicDataArrayList;
        this.mData = nbTopicDataArrayList;
        this.context = context;
    }

    public void notifyListChnage(List<NBTopicData> nbTopicDataArrayList) {
        this.nbTopicDataArrayList = nbTopicDataArrayList;
        this.mData = nbTopicDataArrayList;
        if (OustStaticVariableHandling.getInstance().isNbPostClicked() && nbTopicDataArrayList != null) {
            try {
                Log.d(TAG, "notifyListChnage: Original size:" + nbTopicDataArrayList.get(OustStaticVariableHandling.getInstance().getNbPosition()).getPostUpdateData().size() + " --- update:" + OustStaticVariableHandling.getInstance().getNbPostSize());
                if (nbTopicDataArrayList.get(OustStaticVariableHandling.getInstance().getNbPosition()).getPostUpdateData().size() >= (OustStaticVariableHandling.getInstance().getNbPostSize() + 1)) {
                    Log.d(TAG, "notifyListChnage: updated ");
                    NBDataHandler.getInstance().setNbTopicData(nbTopicDataArrayList.get(OustStaticVariableHandling.getInstance().getNbPosition()));
                    OustStaticVariableHandling.getInstance().setNbStateChanged(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
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
                ArrayList<NBTopicData> filterListData = new ArrayList<>();
                for (int i = 0; i < nbTopicDataArrayList.size(); i++) {
                    if (nbTopicDataArrayList.get(i).getTopic() != null &&
                            (nbTopicDataArrayList.get(i).getTopic().toLowerCase()).contains(constraint.toString().toLowerCase())) {
                        filterListData.add(nbTopicDataArrayList.get(i));
                    }
                }
                results.count = filterListData.size();
                results.values = filterListData;
            } else {
                results.count = nbTopicDataArrayList.size();
                results.values = nbTopicDataArrayList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mData = (List<NBTopicData>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView nb_topic_img;
        private LinearLayout nb_topic_container, no_post_ll, members_ll, ll1;
        private TextView topic_title, new_post_count, nbUpdateText, nbTotalMembers;

        MyViewHolder(View view) {
            super(view);
            nb_topic_img = view.findViewById(R.id.nb_topic_img);
            nb_topic_container = view.findViewById(R.id.nb_topic_container);
            no_post_ll = view.findViewById(R.id.no_post_ll);
            topic_title = view.findViewById(R.id.topic_title);
            new_post_count = view.findViewById(R.id.new_post_count);
            nbUpdateText = view.findViewById(R.id.nbUpdateText);
            nbTotalMembers = view.findViewById(R.id.nbTotalMembers);
            members_ll = view.findViewById(R.id.members_ll);
            ll1 = view.findViewById(R.id.ll1);
            int color = OustSdkTools.getColorBack(R.color.lgreen);
            if (OustPreferences.get("toolbarColorCode") != null && !OustPreferences.get("toolbarColorCode").isEmpty()) {
                color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
            }

            Drawable d = context.getResources().getDrawable(
                    R.drawable.greenlite_round_textview);
            d.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            new_post_count.setBackgroundDrawable(d);
        }

    }

    private void clickView(View view, final int position, final ClickState clickState) {
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
                    if (clickState == ClickState.OPEN_DETAILS) {
//                        if (!OustStaticVariableHandling.getInstance().isModuleClicked()) {
                        clickOnRow(position);
//                        }
                    } else {
                        openMembersList(position);
                    }
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

    private void openMembersList(int position) {
        try {
            Intent intent = new Intent(context, NBMembersListActivity.class);
            intent.putExtra("nbId", mData.get(position).getId());
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //open Topic Details activity
    private void clickOnRow(int position) {
        try {
            Log.d(TAG, "clickOnRow: " + position);
            OustStaticVariableHandling.getInstance().setNbPosition(position);
            OustStaticVariableHandling.getInstance().setNbPostClicked(true);

            NBDataHandler.getInstance().setNbTopicData(mData.get(position));
            Intent intent = new Intent(context, NBTopicDetailActivity.class);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nb_topic_item, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final NBTopicAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        try {
            if (mData.get(position).getTopic() != null && !mData.get(position).getTopic().equalsIgnoreCase("null")) {
                holder.topic_title.setText("" + mData.get(position).getTopic());
                holder.new_post_count.setText("" + mData.get(position).getNewPostCount());
                holder.nbTotalMembers.setText(String.format("%s %d", OustStrings.getString("members"), mData.get(position).getNo_of_members()));
                holder.nbUpdateText.setText(OustSdkTools.getDate(mData.get(position).getAssignedOn() + ""));
                if (mData.get(position).getNewPostCount() == 0) {
                    holder.no_post_ll.setVisibility(View.GONE);
                } else {
                    holder.no_post_ll.setVisibility(View.VISIBLE);
                }
                Picasso.get().load(mData.get(position).getIcon()).into(holder.nb_topic_img);


                holder.nb_topic_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickView(holder.nb_topic_container, position, ClickState.OPEN_DETAILS);
                    }
                });

                holder.members_ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickView(v, position, ClickState.OPEN_MEMBERS);
                    }
                });
                holder.nbUpdateText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickView(holder.nb_topic_container, position, ClickState.OPEN_DETAILS);
                    }
                });

                holder.ll1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickView(holder.nb_topic_container, position, ClickState.OPEN_DETAILS);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}

