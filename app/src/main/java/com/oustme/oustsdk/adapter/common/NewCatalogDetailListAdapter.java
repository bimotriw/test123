package com.oustme.oustsdk.adapter.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.CatalogListActivity;
import com.oustme.oustsdk.firebase.common.CatalogDeatilData;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.util.HashMap;
import java.util.List;

/**
 * Created by oust on 11/2/17.
 */

public class NewCatalogDetailListAdapter extends RecyclerView.Adapter<NewCatalogDetailListAdapter.MyViewHolder> {
    private static final String TAG = "NewCatalogDetailListAda";
    private Context context;
    private List<CatalogDeatilData> catalogDeatilDataArrayList;
    private String toolbarColor;
    private HashMap<String,String> myDeskMap;
    private boolean[] expands;
    private int len;

    public NewCatalogDetailListAdapter(Context context, List<CatalogDeatilData> catalogDeatilDataArrayList){
        this.context=context;
        this.catalogDeatilDataArrayList=catalogDeatilDataArrayList;
        this.expands= new boolean[this.catalogDeatilDataArrayList.size()];
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
        private LinearLayout seeall_courseslayout, linearLayoutExpander;
        private ImageView imageViewExpand, ic_grey_coin;
        public MyViewHolder(View itemView) {
            super(itemView);
            catalogListView= itemView.findViewById(R.id.catalogListView);
            dummyHeader= itemView.findViewById(R.id.dummyHeader);
            header= itemView.findViewById(R.id.header);
            nodata_text= itemView.findViewById(R.id.nodata_text);
            seealltext= itemView.findViewById(R.id.seealltext);
            if(toolbarColor!=null)
            seealltext.setTextColor(Color.parseColor(toolbarColor));
            seeall_courseslayout= itemView.findViewById(R.id.seeall_courseslayout);
            linearLayoutExpander = itemView.findViewById(R.id.linearLayoutExpander);
            imageViewExpand = itemView.findViewById(R.id.imageViewExpand);

            try {
                ic_grey_coin = itemView.findViewById(R.id.ic_grey_coin);
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    ic_grey_coin.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
                }else{
                    ic_grey_coin.setImageResource(R.drawable.ic_coin);
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.new_catalog_detail_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try{
            final CatalogDeatilData catalogDeatilData=catalogDeatilDataArrayList.get(position);
            if(catalogDeatilData!=null) {
                holder.header.setText("" + catalogDeatilData.getTitle());
                if(catalogDeatilData.getTitle().length()>=4)
                {
                    len = catalogDeatilData.getTitle().length()/4;
                }
                else
                {
                    len = 1;
                }
                holder.dummyHeader.setText("" + catalogDeatilData.getTitle().substring(0, len));
                if(toolbarColor!=null && !toolbarColor.isEmpty()){
                    //holder.header.setTextColor(Color.parseColor(toolbarColor));
                    holder.dummyHeader.setBackgroundColor(Color.parseColor(toolbarColor));
                }
                setAdapter(holder.catalogListView,catalogDeatilData,holder.nodata_text);
                holder.linearLayoutExpander.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!expands[position]) {
                            expands[position] = true;
                            holder.imageViewExpand.setImageResource(R.drawable.ic_downarrow);
                            holder.catalogListView.setVisibility(View.GONE);
                        } else {
                            expands[position] = false;
                            holder.imageViewExpand.setImageResource(R.drawable.ic_uparrow);
                            holder.catalogListView.setVisibility(View.VISIBLE);
                            setAdapter(holder.catalogListView,catalogDeatilData,holder.nodata_text);
                        }
                    }
                });
                /*holder.seeall_courseslayout.setOnClickListener(new View.OnClickListener() {
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
                });*/
            }

        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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
            final GridLayoutManager mGridLayoutManager = new GridLayoutManager(context, 2);
            catalogRecyclerView.setLayoutManager(mGridLayoutManager);

            /*LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            catalogRecyclerView.setLayoutManager(mLayoutManager1);*/

            NewCatalogListAdapter catalogListAdapter = new NewCatalogListAdapter(context,catalogDeatilData.getCommonLandingDatas(),1);
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
