package com.oustme.oustsdk.adapter.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.BitmapDrawable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.interfaces.common.OustCallBack;
import com.oustme.oustsdk.response.common.OFBModule;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class NewModuleAdapter extends RecyclerView.Adapter<NewModuleAdapter.MyViewHolder> {
    private List<OFBModule> ofbModuleList;

    private OustCallBack alertFriendInfoCallBack;
    public void setOustCallBack(OustCallBack alertFriendInfoCallBack) {
        this.alertFriendInfoCallBack = alertFriendInfoCallBack;
    }

    public NewModuleAdapter(List<OFBModule> ofbModuleList) {
        this.ofbModuleList = ofbModuleList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView newmodulew_imag;
        public TextView topicText, modulePercent, modulePercentSymbol, modulePercentText,gradeViewButton,userSubjectButton,expertLevel;
        public CardView module_mainrow;

        public MyViewHolder(View view) {
            super(view);
            topicText = view.findViewById(R.id.topicText);
            modulePercent = view.findViewById(R.id.modulePercent);
            modulePercentSymbol = view.findViewById(R.id.modulePercentSymbol);
            modulePercentText = view.findViewById(R.id.modulePercentText);
            gradeViewButton = view.findViewById(R.id.gradeViewButton);
            userSubjectButton = view.findViewById(R.id.userSubjectButton);
            expertLevel= view.findViewById(R.id.expertLevel);
            newmodulew_imag = view.findViewById(R.id.newmodulew_imag);
            module_mainrow = view.findViewById(R.id.module_mainrow);

            OustSdkTools.setImage(newmodulew_imag, OustSdkApplication.getContext().getResources().getString(R.string.mydesk));
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.newmodule_rowlayout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            if (ofbModuleList != null) {
                if(ofbModuleList.get(position).getExpertise()!=null){
                    holder.expertLevel.setText(ofbModuleList.get(position).getExpertise());
                }
                if (ofbModuleList.get(position).getModuleName() != null) {
                    holder.topicText.setText(ofbModuleList.get(position).getModuleName());
                }
                if (ofbModuleList.get(position).getTopic() != null) {
                    holder.userSubjectButton.setText(ofbModuleList.get(position).getTopic());
                }
                if (ofbModuleList.get(position).getPercentageComp() != null) {
                    holder. modulePercent.setText(ofbModuleList.get(position).getPercentageComp() + "");
                    holder.modulePercentSymbol.setText("%");
                    holder.modulePercentText.setText(OustStrings.getString("complete"));
                }
                if (ofbModuleList.get(position).getPercentageComp() != null) {
                    holder. modulePercent.setText(ofbModuleList.get(position).getPercentageComp() + "");
                    holder.modulePercentSymbol.setText("%");
                    holder.modulePercentText.setText(OustStrings.getString("complete"));
                }
                try {
                    if ((ofbModuleList.get(position).getGrade() != null) && (!ofbModuleList.get(position).getGrade().equalsIgnoreCase("0"))) {
                        if (Character.isDigit(ofbModuleList.get(position).getGrade().charAt(0))) {
                            holder.gradeViewButton.setText(OustStrings.getString("gradeLabel") + ofbModuleList.get(position).getGrade());
                        } else {
                            holder.gradeViewButton.setText(ofbModuleList.get(position).getGrade());
                        }
                    }
                }catch (Exception e){}
                if((ofbModuleList.get(position).getBgImage()!=null)&&(!ofbModuleList.get(position).getBgImage().isEmpty())){
                    BitmapDrawable bd= OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.mydesk));
                    Picasso.get().load(ofbModuleList.get(position).getBgImage())
                            .placeholder(bd)
                            .error(bd)
                            .into(holder.newmodulew_imag);
                }
                holder.module_mainrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f,0.94f);
                        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f,0.94f);
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
                            public void onAnimationStart(Animator animator) {}
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                alertFriendInfoCallBack.onMainRowClick(position);
                            }
                            @Override
                            public void onAnimationCancel(Animator animator) {}

                            @Override
                            public void onAnimationRepeat(Animator animator) {}
                        });
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        if(ofbModuleList!=null){
            return ofbModuleList.size();
        }else {
            return 0;
        }
    }
    public void dataChanged(List<OFBModule> ofbModuleList) {
        try {
            this.ofbModuleList = ofbModuleList;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


}
