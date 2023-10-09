package com.oustme.oustsdk.adapter.courses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.interfaces.common.RowClickCallBack;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class CourseMultilingualRowAdapter extends RecyclerView.Adapter<CourseMultilingualRowAdapter.MyViewHolder>  {
    private RowClickCallBack rowClickCallBack;
    private List<CourseDataClass> courseDataClassList;

    public CourseMultilingualRowAdapter(List<CourseDataClass> courseDataClassList,RowClickCallBack rowClickCallBack) {
        this.courseDataClassList=courseDataClassList;
        this.rowClickCallBack=rowClickCallBack;
    }

    public void notifyDataChange(List<CourseDataClass> courseDataClassList){
        this.courseDataClassList=courseDataClassList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.multilingual_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CourseMultilingualRowAdapter.MyViewHolder holder, final int position) {
        holder.rowbanner_image.setImageBitmap(null);
        holder.totaloc_text.setText("");
        holder.total_enrolled_count.setText("");
        holder.coursenametext.setText("");
        holder.rowbanner_image.setVisibility(View.VISIBLE);
        holder.totaloc_text.setText(""+courseDataClassList.get(position).getTotalOc());
        holder.total_enrolled_count.setText(""+courseDataClassList.get(position).getNumEnrolledUsers());
        holder.coursenametext.setText(courseDataClassList.get(position).getCourseName());

        if(courseDataClassList.get(position).getLanguage()!=null && !courseDataClassList.get(position).getLanguage().isEmpty()) {
            holder.language.setText(courseDataClassList.get(position).getLanguage());
            holder.lang_bg.setVisibility(View.VISIBLE);
        } else {
            holder.lang_bg.setVisibility(View.GONE);
        }

        if (OustSdkTools.checkInternetStatus()) {
            Picasso.get().load(courseDataClassList.get(position).getBgImg()).into(holder.rowbanner_image);
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    try {
                        holder.rowbanner_image.setImageBitmap(bitmap);
                        int bitmapHeight = (int) (bitmap.getWidth() * 0.35);
                        int yStart = bitmap.getHeight() / 2 - bitmapHeight / 2;
                        if (yStart <= 0) {
                            yStart = 0;
                        }
                        if (bitmapHeight > bitmap.getHeight()) {
                            bitmapHeight = bitmap.getHeight();
                        }
                        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2 - bitmapHeight / 2, bitmap.getWidth(), bitmapHeight, null, false);
                        holder.rowbanner_image.setImageBitmap(newBitmap);
                        holder.rowbanner_image.setBackgroundResource(0);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            Picasso.get().load(courseDataClassList.get(position).getBgImg()).into(holder.rowbanner_image);
            holder.rowbanner_image.setTag(target);
        } else {
            Picasso.get().load(courseDataClassList.get(position).getBgImg()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.rowbanner_image);
        }

        holder.multilingual_mainrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rowClickCallBack.onMainRowClick(courseDataClassList.get(position).getLanguage(), (int) courseDataClassList.get(position).getCourseId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseDataClassList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView rowbanner_image, coins_icon;
        private TextView coursenametext,total_enrolled_count,totaloc_text,language;
        private RelativeLayout main_layout;
        private RelativeLayout multilingual_mainrow, main_card_ll,module_progress_ll,bannerimage_layout,lang_bg;

        MyViewHolder(View view) {
            super(view);
            multilingual_mainrow = view.findViewById(R.id.multilingual_mainrow);
            coursenametext = view.findViewById(R.id.coursenametext);
            total_enrolled_count = view.findViewById(R.id.total_enrolled_count);
            totaloc_text = view.findViewById(R.id.totaloc_text);
            rowbanner_image = view.findViewById(R.id.rowbanner_image);
            main_card_ll = view.findViewById(R.id.main_card_ll);
            main_layout= view.findViewById(R.id.main_layout);
            bannerimage_layout= view.findViewById(R.id.bannerimage_layout);
            language= view.findViewById(R.id.language);
            lang_bg= view.findViewById(R.id.lang_bg);

            try {
                coins_icon = view.findViewById(R.id.coins_icon);
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    coins_icon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
                }else{
                    OustSdkTools.setImage(coins_icon, OustSdkApplication.getContext().getResources().getString(R.string.coins_icon));
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            DisplayMetrics metrics = OustSdkApplication.getContext().getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            if (scrWidth > 0) {
                rowbanner_image.getLayoutParams().height = (scrWidth)/ 3;
                rowbanner_image.requestLayout();
            }
        }
    }

}
