package com.oustme.oustsdk.layoutFour.components.newCatalogue.adapter;

import static com.oustme.oustsdk.utils.LayoutType.GRID;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.catalogue_ui.CatalogueModuleListActivity;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModule;
import com.oustme.oustsdk.model.request.CatalogViewUpdate;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.MaskTransformation;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

public class CatalogueFolderAdapter extends RecyclerView.Adapter<CatalogueFolderAdapter.ViewHolder> implements Filterable {

    ArrayList<CatalogueModule> catalogueModuleArrayList;
    Context context;
    int screenWidth;
    int type;
    ValueFilter valueFilter;
    ArrayList<CatalogueModule> mData;

    public void setCatalogueFolderAdapter(ArrayList<CatalogueModule> catalogueModuleArrayList, Context context, int screenWidth, int type) {

        this.catalogueModuleArrayList = catalogueModuleArrayList;
        this.mData = catalogueModuleArrayList;
        this.context = context;
        this.screenWidth = screenWidth;
        this.type = type;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (type == GRID) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_folder_grid_item, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_catalogue_folder, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        CatalogueModule catalogueModule = mData.get(position);

        if (catalogueModule != null) {

            if (catalogueModule.getName() != null && !catalogueModule.getName().isEmpty()) {
                holder.catalogue_folder_name.setText(catalogueModule.getName());
            }

            Drawable drawable = OustSdkTools.drawableColor(context.getResources().getDrawable(R.drawable.ic_folder));
            holder.folder_image.setBackground(drawable);
            holder.category_banner.setBackground(drawable);

            if (catalogueModule.getThumbnail() != null && !catalogueModule.getThumbnail().isEmpty() && !catalogueModule.getThumbnail().equalsIgnoreCase("null")) {
                maskImage(holder.category_banner, getBannerUrl(catalogueModule.getThumbnail()));
            } else if (catalogueModule.getBanner() != null && !catalogueModule.getBanner().isEmpty() && !catalogueModule.getBanner().equalsIgnoreCase("null")) {
                maskImage(holder.category_banner, getBannerUrl(catalogueModule.getBanner()));
            } /*else if (catalogueModule.getIcon() != null && !catalogueModule.getIcon().isEmpty()) {
                maskImage(holder.category_banner, getBannerUrl(catalogueModule.getIcon()));
            }*/

            if (catalogueModule.getNumOfModules() != 0) {
                String noOfModules;
                noOfModules = catalogueModule.getNumOfModules() + " " + context.getResources().getString(R.string.module);

                holder.tv_module_count.setText(noOfModules);
                holder.tv_module_count.setTextColor(Color.parseColor("#212121"));
            }


            holder.folder_root_lay.setOnClickListener(v -> {

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
                        updateViewStatus(catalogueModule, position);
                        Intent intent = new Intent(context, CatalogueModuleListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("catalog_id", catalogueModule.getContentId());
                        context.startActivity(intent);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });

            });

        }

    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView folder_image, category_banner;
        TextView catalogue_folder_name, tv_module_count;
        LinearLayout folder_root_lay;

        public ViewHolder(View itemView) {
            super(itemView);

            folder_image = itemView.findViewById(R.id.folder_image);
            category_banner = itemView.findViewById(R.id.category_banner);
            catalogue_folder_name = itemView.findViewById(R.id.catalogue_folder_name);
            tv_module_count = itemView.findViewById(R.id.tv_module_count);
            folder_root_lay = itemView.findViewById(R.id.folder_root_lay);


        }
    }

    public ArrayList<CatalogueModule> getCatalogueModuleArrayList() {
        if (catalogueModuleArrayList != null) {
            return catalogueModuleArrayList;
        } else {
            return new ArrayList<>();
        }
    }

    public ArrayList<CatalogueModule> getDataCatalogueModuleArrayList() {
        if (mData != null) {
            return mData;
        } else {
            return new ArrayList<>();
        }
    }

    private String getBannerUrl(String banner) {
        if (!banner.contains("http"))
            banner = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS + "catalogues/mpower/" + banner;
        return banner;
    }

    public static void maskImage(ImageView mImageView, String url) {
        try {
            Drawable drawable = mImageView.getDrawable();
            if (drawable != null) {
                MaskTransformation maskTransformation = new MaskTransformation(drawable);
                Picasso.get()
                        .load(url)
                        .transform(maskTransformation)
                        .into(mImageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateViewStatus(CatalogueModule catalogueModule, int pos) {

        if (catalogueModule.getViewStatus() != null && (catalogueModule.getViewStatus().equalsIgnoreCase("NEW") ||
                catalogueModule.getViewStatus().equalsIgnoreCase("UPDATE"))) {
            mData.get(pos).setViewStatus("SEEN");
            ActiveUser activeUser;
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            CatalogViewUpdate catalogViewUpdate = new CatalogViewUpdate();
            catalogViewUpdate.setCatalogId(catalogueModule.getCatalogueId());
            catalogViewUpdate.setContentType(catalogueModule.getContentType());
            catalogViewUpdate.setContentId(catalogueModule.getContentId());
            catalogViewUpdate.setCategoryId(catalogueModule.getCatalogueCategoryId());
            catalogViewUpdate.setStudentid(activeUser.getStudentid());

            String url = OustSdkApplication.getContext().getResources().getString(R.string.catalog_view_update);
            final Gson gson = new Gson();
            url = HttpManager.getAbsoluteUrl(url);
            String jsonParams = gson.toJson(catalogViewUpdate);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    notifyDataSetChanged();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("CMA Error", "onErrorResponse: onError:" + error.getLocalizedMessage());
                }
            });

        }
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<CatalogueModule> filterList = new ArrayList<>();
                for (int i = 0; i < catalogueModuleArrayList.size(); i++) {
                    if ((catalogueModuleArrayList.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(catalogueModuleArrayList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = catalogueModuleArrayList.size();
                results.values = catalogueModuleArrayList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mData = (ArrayList<CatalogueModule>) results.values;
            notifyDataSetChanged();
        }

    }
}
