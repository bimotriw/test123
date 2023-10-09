package com.oustme.oustsdk.question_module.adapter;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.course.QuestionOptionData;
import com.oustme.oustsdk.room.dto.DTOQuestionOption;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


public class CategoryImageTextAdapter extends BaseAdapter {
    private static final String TAG = "ImageTextAdapter";
    private List<DTOQuestionOption> models;
    private Context cx;
    private LayoutInflater inflater;

    public CategoryImageTextAdapter(Context cx, List<DTOQuestionOption> models) {
        this.models = models;
        this.cx = cx;
        inflater = LayoutInflater.from(cx);
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_category, parent, false);
            viewHolder.imageView = convertView.findViewById(R.id.options_imglayout);
            viewHolder.tv = convertView.findViewById(R.id.options_textlayout);
            viewHolder.options_textlayoutMaths = convertView.findViewById(R.id.options_textlayoutMaths);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (models != null) {
            if (models.get(position).getType().equalsIgnoreCase("Image")) {
                viewHolder.tv.setVisibility(View.GONE);
                if (models.get(position).getData_CDN() != null && !models.get(position).getData_CDN().isEmpty()) {
                    setImageInViewFromFile(models.get(position).getData_CDN(), viewHolder.imageView);
                    viewHolder.imageView.setVisibility(View.VISIBLE);
                    viewHolder.tv.setVisibility(View.GONE);
                    viewHolder.options_textlayoutMaths.setVisibility(View.GONE);
                } else
                    setImageInView(models.get(position).getData(), viewHolder.imageView);
                viewHolder.imageView.setVisibility(View.VISIBLE);
                viewHolder.tv.setVisibility(View.GONE);
                viewHolder.options_textlayoutMaths.setVisibility(View.GONE);

            } else if (models.get(position).getType().equalsIgnoreCase("text")) {
                viewHolder.imageView.setVisibility(View.GONE);
                if (models.get(position).getData().contains(KATEX_DELIMITER)) {
                    viewHolder.tv.setVisibility(View.GONE);
                    viewHolder.options_textlayoutMaths.setVisibility(View.VISIBLE);
                    viewHolder.imageView.setVisibility(View.GONE);
                    viewHolder.options_textlayoutMaths.setText(models.get(position).getData());
                } else {
                    viewHolder.tv.setVisibility(View.VISIBLE);
                    viewHolder.imageView.setVisibility(View.GONE);
                    viewHolder.options_textlayoutMaths.setVisibility(View.GONE);
                    viewHolder.tv.setText(models.get(position).getData());
                }
            }

        }
        return convertView;
    }

    public class ViewHolder {
        private TextView tv;
        private ImageView imageView;
        private KatexView options_textlayoutMaths;
    }

    public void setImageInView(String fileName, ImageView imageView) {
        try {
            if ((fileName != null) && (!fileName.isEmpty())) {
                byte[] imageByte = Base64.decode(fileName, 0);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                if (decodedByte == null) {
                    setImageInViewFromFile(fileName, imageView);
                } else {
                    imageView.setImageBitmap(decodedByte);
                }
            }
        } catch (Exception e) {
            //OustSdkTools.showToast("Something not good, please try again");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setImageInViewFromFile(String url1, ImageView imageView) {
        try {
            String url = url1;
            if (url != null) {
                url = OustMediaTools.removeAwsOrCDnUrl(url);
            }
            url = "oustlearn_" + OustMediaTools.getMediaFileName(url);
            File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
            if (file != null && file.exists()) {
                Uri uri = Uri.fromFile(file);
                imageView.setImageURI(uri);
            } else {
                Log.d(TAG, "setImageInViewFromFile: loading from url:");
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(url1).into(imageView);
                } else {
                    OustSdkTools.showToast(cx.getString(R.string.no_internet_connection));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
