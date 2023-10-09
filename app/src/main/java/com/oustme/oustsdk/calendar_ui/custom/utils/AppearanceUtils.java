package com.oustme.oustsdk.calendar_ui.custom.utils;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.oustme.oustsdk.R;

import java.util.ArrayList;
import java.util.List;

public class AppearanceUtils {

    public static void setAbbreviationsLabels(View view, int color, int firstDayOfWeek) {
        List<TextView> labels = new ArrayList<>();
        labels.add(view.findViewById(R.id.monday));
        labels.add(view.findViewById(R.id.tuesday));
        labels.add(view.findViewById(R.id.wednesday));
        labels.add(view.findViewById(R.id.thursday));
        labels.add(view.findViewById(R.id.friday));
        labels.add(view.findViewById(R.id.saturday));
        labels.add(view.findViewById(R.id.sunday));

        String[] abbreviations = view.getContext().getResources().getStringArray(R.array.calendar_day_abbreviations_array);
        for (int i = 0; i < 7; i++) {
            TextView label = labels.get(i);
            label.setText(abbreviations[(i + firstDayOfWeek - 1) % 7]);

            if (color != 0) {
                label.setTextColor(color);
            }
        }
    }

    public static void setHeaderColor(View view, int color) {
        if (color == 0) {
            return;
        }

        ConstraintLayout calendarHeader = view.findViewById(R.id.calendar_nav_bar);
        calendarHeader.setBackgroundColor(color);
    }

    public static void setHeaderLabelColor(View view, int color) {
        if (color == 0) {
            return;
        }

        ((TextView) view.findViewById(R.id.month_name_text)).setTextColor(color);
    }

    public static void setAbbreviationsBarColor(View view, int color) {
        if (color == 0) {
            return;
        }

        view.findViewById(R.id.day_name_lay).setBackgroundColor(color);
    }

    public static void setPagesColor(View view, int color) {
        if (color == 0) {
            return;
        }

        view.findViewById(R.id.calendar_view_pager).setBackgroundColor(color);
    }

    private AppearanceUtils() {
    }

    public static void setPreviousButtonImage(View view, Drawable drawable) {
        if (drawable == null) {
            return;
        }

        ((ImageView) view.findViewById(R.id.previous_month)).setImageDrawable(drawable);
    }

    public static void setForwardButtonImage(View view, Drawable drawable) {
        if (drawable == null) {
            return;
        }

        ((ImageView) view.findViewById(R.id.next_month)).setImageDrawable(drawable);
    }

    public static void setHeaderVisibility(View view, int visibility) {
        ConstraintLayout calendarHeader = view.findViewById(R.id.calendar_nav_bar);
        calendarHeader.setVisibility(View.GONE);
    }

    public static void setNavigationVisibility(View view, int visibility) {
        view.findViewById(R.id.previous_month).setVisibility(View.GONE);
        view.findViewById(R.id.next_month).setVisibility(View.GONE);
    }

    public static void setAbbreviationsBarVisibility(View view, int visibility) {
        LinearLayout calendarAbbreviationsBar = view.findViewById(R.id.day_name_lay);
        calendarAbbreviationsBar.setVisibility(visibility);
    }
}
