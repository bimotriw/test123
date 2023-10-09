package com.oustme.oustsdk.question_module.adapter;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.question_module.dragger.ItemTouchHelperAdapter;
import com.oustme.oustsdk.question_module.dragger.ItemTouchHelperViewHolder;
import com.oustme.oustsdk.question_module.dragger.OnStartDragListener;
import com.oustme.oustsdk.room.dto.DTOMTFColumnData;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifDrawable;

public class MTFAnswerAdapter extends RecyclerView.Adapter<MTFAnswerAdapter.ViewHolder> implements ItemTouchHelperAdapter {


    List<DTOMTFColumnData> leftList = new ArrayList<>();
    List<DTOMTFColumnData> rightList = new ArrayList<>();
    private Context context;
    private final String TAG = "MTFAnswerAdapter";

    private OnStartDragListener mDragStartListener;
    private MTFAnswerCheck mtfAnswerCheck;

    public void setOptionsList(List<DTOMTFColumnData> leftList, List<DTOMTFColumnData> rightList, Context context, OnStartDragListener dragStartListener) {
        this.leftList = leftList;
        this.rightList = rightList;
        this.context = context;
        mDragStartListener = dragStartListener;
    }

    public void setMTFAnswerCheck(MTFAnswerCheck mtfAnswerCheck) {
        this.mtfAnswerCheck = mtfAnswerCheck;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mtf_left, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mtf_right, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            DTOMTFColumnData mtfColumnData = null;
            int remainder = position / 2;
            if ((position % 2) == 0) {
                if (remainder < leftList.size()) {
                    mtfColumnData = leftList.get(remainder);
                }
                holder.option_root.setOnTouchListener((v, event) -> {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                });
            } else {
                if (remainder < rightList.size()) {
                    mtfColumnData = rightList.get(remainder);
                }
            }
            if (mtfColumnData != null) {
                if (mtfColumnData.getMtfColMediaType() != null) {
                    if (mtfColumnData.getMtfColMediaType().equalsIgnoreCase("IMAGE")) {
                        if (mtfColumnData.getMtfColData() != null && !mtfColumnData.getMtfColData().isEmpty()) {
                            setImageOptionFromFile(mtfColumnData.getMtfColData(), holder.option_image);
                        } else
                            setImageOption(mtfColumnData.getMtfColData(), holder.option_image);
                        holder.option_card_for_Image.setVisibility(View.VISIBLE);
                        holder.option_card_for_text.setVisibility(View.GONE);
                    } else if (mtfColumnData.getMtfColMediaType().equalsIgnoreCase("AUDIO")) {
                        Log.e("MTFA", "Not handling type");

                    } else {
                        OustSdkTools.getSpannedContent(mtfColumnData.getMtfColData(), holder.option_text);
                        holder.option_card_for_Image.setVisibility(View.GONE);
                        holder.option_card_for_text.setVisibility(View.VISIBLE);
                    }
                } else {
                    OustSdkTools.getSpannedContent(mtfColumnData.getMtfColData(), holder.option_text);
                    holder.option_card_for_Image.setVisibility(View.GONE);
                    holder.option_card_for_text.setVisibility(View.VISIBLE);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public int getItemCount() {
        return leftList.size() + rightList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Log.d(TAG, "Adapter " + fromPosition + " - " + toPosition);

        if ((toPosition % 2 != 0)) {
            int leftPosition = fromPosition / 2;
            int rightPosition = toPosition / 2;
            mtfAnswerCheck.optionSelected(leftPosition, rightPosition);
        }
        return true;
    }

    @Override
    public boolean onItemDismiss(int position) {
        return false;
    }

    public void onItemSwap(int fromPosition, int toPosition) {
        try {
            if (rightList != null && rightList.size() != 0) {
                Collections.swap(rightList, fromPosition, toPosition);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        public final ConstraintLayout option_root;
        public final CardView option_card_for_Image;
        public final CardView option_card_for_text;
        public final ImageView option_image;
        public final TextView option_text;

        public ViewHolder(View itemView) {
            super(itemView);

            option_root = itemView.findViewById(R.id.option_root);
            option_card_for_text = itemView.findViewById(R.id.option_card_for_text);
            option_card_for_Image = itemView.findViewById(R.id.option_card_for_Image);
            option_image = itemView.findViewById(R.id.option_image);
            option_text = itemView.findViewById(R.id.option_text);
        }

        @Override
        public void onItemSelected() {
        }

        @Override
        public void onItemClear() {
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        if ((position % 2) == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public void setImageOptionFromFile(String imageUrl, ImageView imageView) {
        try {
            String url = imageUrl;
            if (url != null) {
                url = OustMediaTools.removeAwsOrCDnUrl(url);
            }
            url = "oustlearn_" + OustMediaTools.getMediaFileName(url);
            File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
            if (file.exists()) {
                try {
                    Uri uri = Uri.fromFile(file);
                    if (uri != null && imageView != null) {
                        GifDrawable gifFromBytes = new GifDrawable(getBytes(Objects.requireNonNull(context.getContentResolver().openInputStream(uri))));
                        imageView.setImageDrawable(gifFromBytes);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    setNonGifImage(imageUrl, imageView);
                }
            } else {
                if (OustSdkTools.checkInternetStatus())
                    Glide.with(context).load(imageUrl).into(imageView);
                else {
                    OustSdkTools.showToast(context.getString(R.string.no_internet_connection));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            setNonGifImage(imageUrl, imageView);
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void setNonGifImage(String imageUrl, ImageView imageView) {
        String url = imageUrl;
        Log.d(TAG, "setImageOptionUrl: " + url);
        if (url != null) {
            url = OustMediaTools.removeAwsOrCDnUrl(url);
        }
        assert url != null;
        if (!url.contains("oustlearn_")) {
            url = "oustlearn_" + OustMediaTools.getMediaFileName(url);
        }
        File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            //Bitmap bitmap = BitmapFactory.decodeFile(file.toString(), options);
            if (options.outWidth != -1 && options.outHeight != -1) {
                Log.d(TAG, "setImageOptionUrl: this is proper image");
                if (OustSdkTools.checkInternetStatus()) {
                    Glide.with(context).load(imageUrl).into(imageView);
                    if (file.exists()) {
                        boolean b = file.delete();
                        Log.e(TAG, "File exists " + b);
                    }
                    downLoad(imageUrl);
                } else {
                    Glide.with(context).load(uri).into(imageView);
                }
            } else {
                Log.d(TAG, "setImageOptionUrl: this is not proper image");
                Glide.with(context).load(imageUrl).into(imageView);
                if (file.exists()) {
                    boolean b = file.delete();
                    Log.e(TAG, "File exists " + b);
                }
                downLoad(imageUrl);
            }

        } else {
            if (OustSdkTools.checkInternetStatus())
                Glide.with(context).load(imageUrl).into(imageView);
            else {
                OustSdkTools.showToast(context.getString(R.string.no_internet_connection));
            }
        }
    }

    public void downLoad(String url) {

        Log.d(TAG, "downLoad: Media:" + CLOUD_FRONT_BASE_PATH);
        Log.d(TAG, "downLoad: Media:" + AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH);
        try {

            DownloadFiles downloadFiles;
            downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
                @Override
                public void onDownLoadProgressChanged(String message, String progress) {
                    //showDownloadProgress();
                }

                @Override
                public void onDownLoadError(String message, int errorCode) {
                    Log.d(TAG, "onDownLoadError: Message:" + message + " errorcode:" + errorCode);
                }

                @Override
                public void onDownLoadStateChanged(String message, int code) {
                    if (code == _COMPLETED) {
                        //removeFile();
                        Log.d(TAG, "Download completed");
                    }

                }

                @Override
                public void onAddedToQueue(String id) {

                }

                @Override
                public void onDownLoadStateChangedWithId(String message, int code, String id) {

                }
            });

            String path = context.getFilesDir() + "/";
            String filename = OustMediaTools.getMediaFileName(url);
            downloadFiles.startDownLoad(url, path, filename, true, false);


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setImageOption(String str, ImageView imgView) {
        try {
            if ((str != null) && (!str.isEmpty())) {
                byte[] imageByte = Base64.decode(str, 0);
                GifDrawable gifFromBytes = new GifDrawable(imageByte);
                imgView.setImageDrawable(gifFromBytes);
            }
        } catch (Exception e) {
            setImageOptionBitmap(str, imgView);
        }
    }

    public void setImageOptionBitmap(String str, ImageView imgView) {
        try {
            if ((str != null) && (!str.isEmpty())) {
                byte[] imageByte = Base64.decode(str, 0);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                imgView.setImageBitmap(decodedByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
