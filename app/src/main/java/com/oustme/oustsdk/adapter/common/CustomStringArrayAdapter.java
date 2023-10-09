package com.oustme.oustsdk.adapter.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.request.AreaModel;

import java.util.ArrayList;

/**
 * Created by oust on 9/7/18.
 */

public class CustomStringArrayAdapter  extends ArrayAdapter<String> {
    private final LayoutInflater mInflater;
    private final Context mContext;
    private final ArrayList<String> items;
    private final int mResource;

    public CustomStringArrayAdapter(Context context, int resource,
                                ArrayList<String> objects) {
        super(context, resource, 0);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
    }
    @Override
    public View getDropDownView(int position, View convertView,
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

        spinner_txt.setText(""+items.get(position));

        return view;
    }


    @Override
    public int getCount() {
        return items.size();
    }
}
