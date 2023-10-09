package com.oustme.oustsdk.adapter.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.CatalogListActivity;
import com.oustme.oustsdk.firebase.common.CatalogDeatilData;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by oust on 11/2/17.
 */

public class CatalogDetailListAdapter extends RecyclerView.Adapter<CatalogDetailListAdapter.MyViewHolder> {

    private static final String TAG = "CatalogDetailListAdapte";
    private Context context;
    private ArrayList<CatalogDeatilData> catalogDeatilDataArrayList;
    private String toolbarColor;
    private HashMap<String,String> myDeskMap;

    public CatalogDetailListAdapter(Context context,ArrayList<CatalogDeatilData> catalogDeatilDataArrayList){
        this.context=context;
        this.catalogDeatilDataArrayList=catalogDeatilDataArrayList;
    }
    public void setTitleColor(String toolbarColor){
        this.toolbarColor=toolbarColor;
    }
    public void setMyDeskMap( HashMap<String,String> myDeskMap){
        this.myDeskMap=myDeskMap;
        Log.d(TAG, "setMyDeskMap: ");
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView catalogListView;
        private TextView header,dummyHeader,nodata_text,seealltext;
        private LinearLayout seeall_courseslayout;
        public MyViewHolder(View itemView) {
            super(itemView);
            catalogListView= itemView.findViewById(R.id.catalogListView);
            dummyHeader= itemView.findViewById(R.id.dummyHeader);
            header= itemView.findViewById(R.id.header);
            nodata_text= itemView.findViewById(R.id.nodata_text);
            seealltext= itemView.findViewById(R.id.seealltext);
            seealltext.setTextColor(Color.parseColor(toolbarColor));
            seeall_courseslayout= itemView.findViewById(R.id.seeall_courseslayout);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_detail_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try{
            final CatalogDeatilData catalogDeatilData=catalogDeatilDataArrayList.get(position);
            if(catalogDeatilData!=null) {
                holder.header.setText("" + catalogDeatilData.getTitle());
                holder.dummyHeader.setText("      " + catalogDeatilData.getTitle());
                if(toolbarColor!=null && !toolbarColor.isEmpty()){
                    holder.header.setTextColor(Color.parseColor(toolbarColor));
                    holder.dummyHeader.setBackgroundColor(Color.parseColor(toolbarColor));
                }
                if((catalogDeatilData.getCommonLandingDatas()!=null)&&(catalogDeatilData.getCommonLandingDatas().size()>2)){
                    holder.seealltext.setText(context.getResources().getString(R.string.see_all)+" ");
                    holder.seealltext.setTextColor(Color.parseColor(toolbarColor));
                    holder.seeall_courseslayout.setVisibility(View.VISIBLE);
                }else {
                    holder.seeall_courseslayout.setVisibility(View.GONE);
                }
                setAdapter(holder.catalogListView,catalogDeatilData,holder.nodata_text);
                holder.seeall_courseslayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!OustStaticVariableHandling.getInstance().isModuleClicked()) {
                            OustStaticVariableHandling.getInstance().setModuleClicked(true);
                            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 0.94f);
                            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 0.96f);
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
                                    clickonRow(catalogDeatilData);
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
                });
            }

        }catch (Exception e){}
    }

    private void clickonRow(CatalogDeatilData catalogDeatilData) {
        Log.d(TAG, "clickonRow: calling Catalist:");
        Log.d(TAG, "clickonRow: catalog Id:"+catalogDeatilData.getId());
        Intent intent = new Intent(context, CatalogListActivity.class);
        intent.putExtra("deskDataMap", myDeskMap);
        Log.d(TAG, "clickonRow: catalogDeatilData.getType():"+catalogDeatilData.getType());
        if(catalogDeatilData.getType().equals("Category")) {
            Log.d(TAG, "clickonRow: sending catalogID");
            intent.putExtra("catalog_id", catalogDeatilData.getId());
            intent.putExtra("deskDataMap", myDeskMap);
            intent.putExtra("topDisplayName",catalogDeatilData.getTitle());
            intent.putExtra("hasBanner", true);
        }else{
            Log.d(TAG, "clickonRow: sending mydeskmap");
            OustDataHandler.getInstance().saveData(catalogDeatilData.getCommonLandingDatas());
            intent.putExtra("hasBanner", false);
            intent.putExtra("topDisplayName",catalogDeatilData.getTitle());
            if(catalogDeatilData.getType().equals("My Desk")){
                intent.putExtra("filter_type","MyDesk");
            }
        }
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return catalogDeatilDataArrayList.size();
    }

    private void setAdapter(RecyclerView catalogRecyclerView,CatalogDeatilData catalogDeatilData,TextView nodata_text) {
        if((catalogDeatilData.getCommonLandingDatas()!=null)&&(catalogDeatilData.getCommonLandingDatas().size()>0)) {
            nodata_text.setVisibility(View.GONE);
            catalogRecyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            catalogRecyclerView.setLayoutManager(mLayoutManager1);
            CatalogListAdapter catalogListAdapter = new CatalogListAdapter(context,catalogDeatilData.getCommonLandingDatas(),2);
            catalogListAdapter.setCategory(catalogDeatilData.getTitle());
            catalogListAdapter.isMyCourse(false);
            catalogListAdapter.setMyDeskMap(myDeskMap);
            catalogRecyclerView.setAdapter(catalogListAdapter);
        }else {
            if(OustStaticVariableHandling.getInstance().isInternetConnectionOff()){
                nodata_text.setText(context.getResources().getString(R.string.no_internet_data));
            } else {
                nodata_text.setText(context.getResources().getString(R.string.no_data_available));
            }
            nodata_text.setVisibility(View.VISIBLE);
            catalogRecyclerView.setVisibility(View.GONE);
        }
    }

}
