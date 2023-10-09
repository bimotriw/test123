package com.oustme.oustsdk.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.utils.LayoutType;
import com.oustme.oustsdk.utils.OustResourceUtils;

public class LayoutSwitcher extends LinearLayout implements View.OnClickListener {

    private LayoutSwitcherCallback callback;
    private ImageView iv_Grid;
    private ImageView iv_List;

    public LayoutSwitcher(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        @SuppressLint("CustomViewStyleable")
     /*   TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ComponentActiveUser, 0, 0);*/
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.layout_layout_switcher, this, true);

        initViews();
    }

    public void setCallback(LayoutSwitcherCallback callback) {
        this.callback = callback;
    }

    private void initViews() {
        iv_Grid = findViewById(R.id.iv_grid);
        iv_List = findViewById(R.id.iv_list);
        OustResourceUtils.setDefaultDrawableColor(iv_Grid.getDrawable());
        OustResourceUtils.setDefaultDrawableColor(iv_List.getDrawable());

        iv_Grid.setOnClickListener(this);
        iv_List.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int type = LayoutType.LIST;
        if (id == R.id.iv_grid) {
            type = LayoutType.GRID;
            OustResourceUtils.setDefaultDrawableColor(iv_Grid.getDrawable());
            OustResourceUtils.setDefaultDrawableColor(iv_List.getDrawable());
            iv_List.setVisibility(VISIBLE);
            iv_Grid.setVisibility(GONE);
            if (callback != null)
                callback.onLayoutSelected(type);
        } else if (id == R.id.iv_list) {
            OustResourceUtils.setDefaultDrawableColor(iv_Grid.getDrawable());
            OustResourceUtils.setDefaultDrawableColor(iv_List.getDrawable());
            iv_List.setVisibility(GONE);
            iv_Grid.setVisibility(VISIBLE);
            if (callback != null)
                callback.onLayoutSelected(type);
        }
    }

    public interface LayoutSwitcherCallback {
        void onLayoutSelected(int Type);
    }


}
