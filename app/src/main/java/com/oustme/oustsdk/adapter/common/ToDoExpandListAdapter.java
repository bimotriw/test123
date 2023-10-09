package com.oustme.oustsdk.adapter.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.CplBaseActivity;
import com.oustme.oustsdk.activity.common.todoactivity.TodoListActivity;
import com.oustme.oustsdk.model.response.common.ToDoChildModel;
import com.oustme.oustsdk.model.response.common.ToDoHeaderModel;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.RecyclerViewItemClickSupport;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 09/11/18.
 */

public class ToDoExpandListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "ToDoExpandListAdapter";
    private Context _context;
    private List<ToDoHeaderModel> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<ToDoChildModel>> _listDataChild;
    private boolean isRecy;
    private List<ToDoChildModel> childModelList;

    public ToDoExpandListAdapter(Context context, List<ToDoHeaderModel> listDataHeader,
                                 HashMap<String, List<ToDoChildModel>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        // ToDoChildModel childText = (ToDoChildModel) getChild(0, childPosition);
        boolean hasBanner = _listDataHeader.get(groupPosition).isHasBanner();
        String mBannerURL = _listDataHeader.get(groupPosition).getmUrl();
        final int type = _listDataHeader.get(groupPosition).getType();
        childModelList = _listDataChild.get(_listDataHeader.get(groupPosition).getTitle());

        LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.todo_expand_list_item, null);
        RelativeLayout mRelativeLayout = convertView.findViewById(R.id.ffcbanner_imglayout);

        View mFFCView = convertView.findViewById(R.id.layout_ffc);
        View mCPLView = convertView.findViewById(R.id.layout_cpl);
        RecyclerView recyclerView = convertView.findViewById(R.id.rec);

        try {
            ImageView coins_icon = convertView.findViewById(R.id.coins_icon);
            if (OustPreferences.getAppInstallVariable("showCorn")) {
                coins_icon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
            } else {
                OustSdkTools.setImage(coins_icon, OustSdkApplication.getContext().getResources().getString(R.string.coins_icon));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        ImageView imageView = convertView.findViewById(R.id.ffcbanner_img);

        if (!hasBanner) {
            ToDoRecyclerViewAdapter adapter = new ToDoRecyclerViewAdapter(_context, childModelList, _listDataHeader.get(groupPosition).getType());
            recyclerView.setAdapter(adapter);
            GridLayoutManager mLayoutManager1 = new GridLayoutManager(_context, 2);
            recyclerView.setLayoutManager(mLayoutManager1);
            adapter.notifyDataSetChanged();

            //((TodoListActivity)_context).mPendingCount+= childModelList.size();//to set the pending count
            //((TodoListActivity)_context).setActionBarTitle();//to set count on title

            RecyclerViewItemClickSupport.addTo(recyclerView).setOnItemClickListener(new RecyclerViewItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                }
            });
        }
        if (type == 0) {
            if (childModelList != null) {
                ToDoChildModel childText = childModelList.get(0);//ToDoChildModel) getChild(0, 0);
                mFFCView.setVisibility(View.VISIBLE);
                TextView textViewStartTime = convertView.findViewById(R.id.textViewFFCTime);
                if (childText != null) {
                    if (childText.getFfcStartTime() != null)
                        textViewStartTime.setText(childText.getFfcStartTime());
                    TextView textViewUsersCount = convertView.findViewById(R.id.textViewFFCEnrollCount);
                    if (childText.getFfcEnrolledCount() != null)
                        textViewUsersCount.setText(childText.getFfcEnrolledCount());
                }
            }
        } else if (type == 1) {
            if (childModelList != null) {
                ToDoChildModel childText = childModelList.get(0);//(ToDoChildModel) getChild(0, 0);
                mCPLView.setVisibility(View.VISIBLE);
                TextView textViewUsersCount = convertView.findViewById(R.id.textViewEnrollCount);
                TextView textViewOustCoins = convertView.findViewById(R.id.textViewOustCoinCount);
                TextView textViewCPLTitle = convertView.findViewById(R.id.textViewCplTitle);
                TextView textViewCPLDescription = convertView.findViewById(R.id.textViewCplDescription);
                Button buttonCPLContinue = convertView.findViewById(R.id.buttonContinue);
                final ConstraintLayout cplBackground = convertView.findViewById(R.id.cl_banner);
                // Log.d(TAG, "getChildView: "+childText.getCplBanner());
                if (childText.getCplBanner() != null && !childText.getCplBanner().isEmpty()) {
                    try {
                        Picasso.get().load(childText.getCplBanner()).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                cplBackground.setAlpha(0.6f);
                                cplBackground.setBackgroundDrawable(new BitmapDrawable(bitmap));
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                buttonCPLContinue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gotoCPLActivity(childText.getCplId());
                    }
                });

                if (childText != null) {
                    if (childText.getCPLOustCoinsCount() != null)
                        textViewOustCoins.setText(childText.getCPLOustCoinsCount());
                    if (childText.getCPLUsersCount() != null)
                        textViewUsersCount.setText(childText.getCPLUsersCount());

                    if (childText.getCPLTitle() != null) {
                        textViewCPLTitle.setText(childText.getCPLTitle());
                    }
                    if (childText.getCPLDescription() != null) {
                        textViewCPLDescription.setText(childText.getCPLDescription());
                    }
                }
            }

        } else {
            mCPLView.setVisibility(View.GONE);
            mFFCView.setVisibility(View.GONE);
        }
        if (hasBanner && type == 0) {
            Picasso.get().load(mBannerURL).into(imageView);
            recyclerView.setVisibility(View.GONE);
            mFFCView.setVisibility(View.VISIBLE);
            mCPLView.setVisibility(View.GONE);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((TodoListActivity) _context).gotoFFContest();
                }
            });
        } else if (hasBanner && type == 1) {
            recyclerView.setVisibility(View.GONE);
            mCPLView.setVisibility(View.VISIBLE);
            mFFCView.setVisibility(View.GONE);
        } else {
            mCPLView.setVisibility(View.GONE);
            mFFCView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private void gotoCPLActivity(String cplId) {
        Intent intent = new Intent(_context, CplBaseActivity.class);
        intent.putExtra("cplId", cplId);
        _context.startActivity(intent);
        //overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ToDoHeaderModel headerTitle = (ToDoHeaderModel) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.todo_expand_list_grp, null);
        }

        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle.getTitle());

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void loadImageFromPicasso(int id, String url) {

    }
}
