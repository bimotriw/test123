package com.oustme.oustsdk.layoutFour.components.feedList.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.List;

public class CourseLanguageAdapter extends RecyclerView.Adapter<CourseLanguageAdapter.ViewHolder> {

    private List<CourseDataClass> courseDataClassList;
    private Context context;
    int row_index = -1;

    public CourseLanguageAdapter(List<CourseDataClass> courseDataClassList, Context context) {
        this.context = context;
        this.courseDataClassList = courseDataClassList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.course_language_adapter, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {

            if (courseDataClassList.get(position) != null) {

                holder.course_language.setText(courseDataClassList.get(position).getLanguage());


                holder.course_language_lay.setOnClickListener(v -> {
                    row_index = position;
                    notifyDataSetChanged();
                });

                if (row_index == -1) {
                    row_index = 0;
                }
                if (row_index == position) {
                    OustPreferences.save("FeedCourseId", courseDataClassList.get(position).getCourseId() + "");
                    String toolbarColorCode = OustPreferences.get("toolbarColorCode");
                    holder.course_language_lay.setBackgroundColor(Color.parseColor(toolbarColorCode));
                    holder.course_language.setTextColor(Color.parseColor("#ffffff"));
                    holder.check_course.setVisibility(View.VISIBLE);
                } else {
                    holder.course_language_lay.setBackgroundColor(Color.parseColor("#ffffff"));
                    //holder.course_language_lay.setBackground(R.drawable.grey_rectangular_box);
                    final int sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.course_language_lay.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.grey_rectangular_box));
                    } else {
                        holder.course_language_lay.setBackground(ContextCompat.getDrawable(context, R.drawable.grey_rectangular_box));
                    }
                    holder.course_language.setTextColor(Color.parseColor("#000000"));
                    holder.check_course.setVisibility(View.GONE);
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        return courseDataClassList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout course_language_lay;
        TextView course_language;
        ImageView check_course;

        ViewHolder(View itemView) {
            super(itemView);
            check_course = itemView.findViewById(R.id.check_course);
            course_language = itemView.findViewById(R.id.course_language);
            course_language_lay = itemView.findViewById(R.id.course_language_lay);
        }
    }


}
