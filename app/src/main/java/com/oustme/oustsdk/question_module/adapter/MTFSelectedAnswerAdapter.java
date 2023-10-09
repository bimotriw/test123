package com.oustme.oustsdk.question_module.adapter;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.question_module.model.MTFSelectedAnswer;
import com.oustme.oustsdk.room.dto.DTOMTFColumnData;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifDrawable;

public class MTFSelectedAnswerAdapter extends RecyclerView.Adapter<MTFSelectedAnswerAdapter.ViewHolder> {

    private final String TAG = "MTFAnswerAdapter";
    List<MTFSelectedAnswer> selectedAnswerArrayList;
    Context context;

    public MTFSelectedAnswerAdapter(List<MTFSelectedAnswer> selectedAnswerArrayList, Context context) {
        this.selectedAnswerArrayList = selectedAnswerArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mtf_selected_answer, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            MTFSelectedAnswer mtfSelectedAnswer = selectedAnswerArrayList.get(position);
            if (mtfSelectedAnswer != null) {

                DTOMTFColumnData leftMTFColumnData = mtfSelectedAnswer.getLeftColumn();
                DTOMTFColumnData rightMTFColumnData = mtfSelectedAnswer.getRightColumn();

                if (leftMTFColumnData != null) {
                    if (leftMTFColumnData.getMtfColMediaType() != null) {
                        if (leftMTFColumnData.getMtfColMediaType().equalsIgnoreCase("IMAGE")) {
                            if (leftMTFColumnData.getMtfColData() != null && !leftMTFColumnData.getMtfColData().isEmpty()) {
                                setImageOptionFromFile(leftMTFColumnData.getMtfColData(), holder.answer_left_image);
                            } else
                                setImageOption(leftMTFColumnData.getMtfColData(), holder.answer_left_image);
                            holder.answer_left_image.setVisibility(View.VISIBLE);
                            holder.answer_left_text.setVisibility(View.GONE);
                        } else if (leftMTFColumnData.getMtfColMediaType().equalsIgnoreCase("AUDIO")) {
                            Log.e("MTFA", "Not handling type");
                        } else {
                            OustSdkTools.getSpannedContent(leftMTFColumnData.getMtfColData(), holder.answer_left_text);
                            holder.answer_left_image.setVisibility(View.GONE);
                            holder.answer_left_text.setVisibility(View.VISIBLE);
                        }
                    } else {
                        OustSdkTools.getSpannedContent(leftMTFColumnData.getMtfColData(), holder.answer_left_text);
                        holder.answer_left_image.setVisibility(View.GONE);
                        holder.answer_left_text.setVisibility(View.VISIBLE);
                    }
                }

                if (rightMTFColumnData != null) {
                    if (rightMTFColumnData.getMtfColMediaType() != null) {
                        if (rightMTFColumnData.getMtfColMediaType().equalsIgnoreCase("IMAGE")) {
                            if (rightMTFColumnData.getMtfColData() != null && !rightMTFColumnData.getMtfColData().isEmpty()) {
                                setImageOptionFromFile(rightMTFColumnData.getMtfColData(), holder.answer_right_image);
                            } else
                                setImageOption(rightMTFColumnData.getMtfColData(), holder.answer_right_image);
                            holder.answer_right_image.setVisibility(View.VISIBLE);
                        } else if (rightMTFColumnData.getMtfColMediaType().equalsIgnoreCase("AUDIO")) {
                            Log.e("MTFA", "Not handling type");
                        } else {
                            OustSdkTools.getSpannedContent(rightMTFColumnData.getMtfColData(), holder.answer_right_text);
                            holder.answer_right_image.setVisibility(View.GONE);
                            holder.answer_right_text.setVisibility(View.VISIBLE);
                        }
                    } else {
                        OustSdkTools.getSpannedContent(rightMTFColumnData.getMtfColData(), holder.answer_right_text);
                        holder.answer_right_image.setVisibility(View.GONE);
                        holder.answer_right_text.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public int getItemCount() {
        return selectedAnswerArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final RelativeLayout answer_option_root;
        public final CardView answer_card;
        public final ImageView answer_left_image;
        public final ImageView answer_right_image;
        public final TextView answer_left_text;
        public final TextView answer_right_text;

        public ViewHolder(View itemView) {
            super(itemView);

            answer_option_root = itemView.findViewById(R.id.answer_option_root);
            answer_card = itemView.findViewById(R.id.answer_card);
            answer_left_image = itemView.findViewById(R.id.answer_left_image);
            answer_right_image = itemView.findViewById(R.id.answer_right_image);
            answer_left_text = itemView.findViewById(R.id.answer_left_text);
            answer_right_text = itemView.findViewById(R.id.answer_right_text);
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
                Uri uri = Uri.fromFile(file);
                GifDrawable gifFromBytes = new GifDrawable(getBytes(Objects.requireNonNull(context.getContentResolver().openInputStream(uri))));
                imageView.setImageDrawable(gifFromBytes);
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
