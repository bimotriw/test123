package com.oustme.oustsdk.layoutFour.components.myTask.fragment.playList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModuleUpdate;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class TaskHashMapCplAdapter extends RecyclerView.Adapter<TaskHashMapCplAdapter.ViewHolder> implements Filterable {

    private Map<String, ArrayList<CommonLandingData>> taskModuleHashMap = new TreeMap<>();
    private Map<String, ArrayList<CommonLandingData>> tempTaskModuleHashMap = new TreeMap<>();
    public Context context;
    int type;
    TaskHashMapCplAdapter.ValueFilter valueFilter;
    ArrayList<String> keyList;
    TaskListCplAdapter adapter;


    public void setTaskRecyclerAdapter(Map<String, ArrayList<CommonLandingData>> taskModuleHashMap,
                                       Context context, int type) {
        this.tempTaskModuleHashMap = taskModuleHashMap;
        this.context = context;
        this.type = type;
        this.keyList = new ArrayList<>(taskModuleHashMap.keySet());
    }

    public void setType(int type) {
        this.type = type;
        notifyDataSetChanged();
    }

    public void setTaskModuleHashMap(Map<String, ArrayList<CommonLandingData>> taskModuleHashMap) {
        this.taskModuleHashMap = taskModuleHashMap;
        this.keyList = new ArrayList<>(taskModuleHashMap.keySet());
        OustStaticVariableHandling.getInstance().setTaskPlayListModuleHashMap(taskModuleHashMap);
    }

    public Map<String, ArrayList<CommonLandingData>> getList() {
        return Objects.requireNonNullElseGet(tempTaskModuleHashMap, TreeMap::new);
    }

    @NonNull
    @Override
    public TaskHashMapCplAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_adapter_module, parent, false);
        TaskHashMapCplAdapter.ViewHolder viewHolder = new TaskHashMapCplAdapter.ViewHolder(v);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHashMapCplAdapter.ViewHolder holder, int position) {

        try {
            String dateKey = keyList.get(position);
            if (dateKey != null && !dateKey.isEmpty()) {
                Date date = new SimpleDateFormat("yyyyMMdd", Locale.US).parse(dateKey);
                holder.tv_date.setText(new SimpleDateFormat("dd\nMMM", Locale.getDefault()).format(Objects.requireNonNull(date)));
                RecyclerView.LayoutManager mModulesLayoutManager;
                if (type == 1) {
                    mModulesLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                } else {
                    mModulesLayoutManager = new GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false);

                }

                ArrayList<CommonLandingData> taskModuleList = tempTaskModuleHashMap.get(dateKey);
                if (taskModuleList != null && taskModuleList.size() != 0) {
                    Collections.reverse(taskModuleList);
                }
                holder.include_module.setLayoutManager(mModulesLayoutManager);
                holder.include_module.setItemAnimator(new DefaultItemAnimator());
                holder.include_module.removeAllViews();

                adapter = new TaskListCplAdapter();
                adapter.setTaskRecyclerAdapter(taskModuleList, context, type, position, dateKey);
                holder.include_module.setAdapter(adapter);
                RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, MotionEvent e) {
                        int action = e.getAction();
                        if (action == MotionEvent.ACTION_MOVE) {
                            rv.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        return false;
                    }

                    @Override
                    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

                    }

                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                    }
                };

                holder.include_module.addOnItemTouchListener(mScrollTouchListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public int getItemCount() {
        if (tempTaskModuleHashMap == null) {
            return 0;
        }
        //this.keyList = new ArrayList<>(taskModuleHashMap.keySet());
        return tempTaskModuleHashMap.size();
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new TaskHashMapCplAdapter.ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                Map<String, ArrayList<CommonLandingData>> filterTaskModuleHashMap = new TreeMap<>();
                constraint = constraint.toString().toUpperCase();
                if (OustStaticVariableHandling.getInstance().getTaskPlayListModuleHashMap() != null && OustStaticVariableHandling.getInstance().getTaskPlayListModuleHashMap().size() != 0) {
                    for (String data : OustStaticVariableHandling.getInstance().getTaskPlayListModuleHashMap().keySet()) {
                        ArrayList<CommonLandingData> commonLandingData = taskModuleHashMap.get(data);
                        if (commonLandingData != null && commonLandingData.size() > 0) {
                            ArrayList<CommonLandingData> data1 = new ArrayList<>();
                            for (CommonLandingData landingData : commonLandingData) {
                                if (landingData.getName() != null) {
                                    if (landingData.getName().toUpperCase().contains(constraint)) {
                                        data1.add(landingData);
                                    }
                                }
                            }
                            if (data1.size() > 0) {
                                filterTaskModuleHashMap.put(data, data1);
                            }
                        }
                    }
                }
                results.count = filterTaskModuleHashMap.size();
                results.values = filterTaskModuleHashMap;
            } else {
                results.count = taskModuleHashMap.size();
                results.values = taskModuleHashMap;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            tempTaskModuleHashMap = (Map<String, ArrayList<CommonLandingData>>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View include_date;
        RecyclerView include_module;
        ImageView view4;
        TextView tv_date;

        public ViewHolder(View itemView) {
            super(itemView);
            include_date = itemView.findViewById(R.id.include_date);
            include_module = itemView.findViewById(R.id.include_module);
            view4 = include_date.findViewById(R.id.view4);
            tv_date = include_date.findViewById(R.id.tv_date);
        }
    }

    public void modifyItem(final int position, final CatalogueModuleUpdate catalogueModuleUpdate) {
        try {
            if (catalogueModuleUpdate != null && catalogueModuleUpdate.getCommonLandingData() != null) {
                if (catalogueModuleUpdate.getCommonLandingData().getAddedOn() != null && !catalogueModuleUpdate.getCommonLandingData().getAddedOn().isEmpty()) {
                    try {
                        long addedOn = Long.parseLong(catalogueModuleUpdate.getCommonLandingData().getAddedOn());
                        if (addedOn != 0) {
                            Date date = new Date(addedOn);
                            String dateKey = new SimpleDateFormat("yyyyMMdd", Locale.US).format(date);
                            if (tempTaskModuleHashMap.size() != 0) {
                                if (tempTaskModuleHashMap.get(dateKey) != null) {
                                    ArrayList<CommonLandingData> updatedList = tempTaskModuleHashMap.get(dateKey);
                                    if (updatedList != null && updatedList.size() != 0 && catalogueModuleUpdate.getPosition() < updatedList.size()) {
                                        updatedList.set(catalogueModuleUpdate.getPosition(), catalogueModuleUpdate.getCommonLandingData());
                                        tempTaskModuleHashMap.put(dateKey, updatedList);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
                notifyItemChanged(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void notifyTaskHashMap(Map<String, ArrayList<CommonLandingData>> taskModuleHashMap) {
        try {
            if (taskModuleHashMap != null && taskModuleHashMap.size() != 0) {
                tempTaskModuleHashMap.putAll(taskModuleHashMap);
                notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }
}

