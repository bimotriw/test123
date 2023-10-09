package com.oustme.oustsdk.adapter.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.request.BloodGrpModel;

import java.util.ArrayList;

public class CustomBloodGrpAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final ArrayList<BloodGrpModel> items;
    private final int mResource;

    public CustomBloodGrpAdapter(Context context, int resource,
                                 ArrayList<BloodGrpModel> objects) {
        super(context, resource, 0);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
    }
    @Override
    public View getDropDownView(int position,  View convertView,
                                 ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public  View getView(int position,  View convertView,  ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = mInflater.inflate(mResource, parent, false);

        TextView spinner_txt = view.findViewById(R.id.spinner_txt);
        View sp_line = view.findViewById(R.id.sp_line);

        BloodGrpModel bloodGrpModel = items.get(position);

        spinner_txt.setText(""+bloodGrpModel.getBloodGrp());

        return view;
    }


    @Override
    public int getCount() {
        return items.size();
    }
}
