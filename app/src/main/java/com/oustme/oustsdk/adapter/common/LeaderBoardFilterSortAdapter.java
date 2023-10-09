package com.oustme.oustsdk.adapter.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.oustme.oustsdk.R;

public class LeaderBoardFilterSortAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public LeaderBoardFilterSortAdapter(Context context, String[] values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.leaderboard_filter_item, parent, false);
        CheckBox radioButton = rowView.findViewById(R.id.radioButton);
        radioButton.setText(values[position]);
        return rowView;
    }
}