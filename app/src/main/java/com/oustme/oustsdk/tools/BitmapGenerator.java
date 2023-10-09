package com.oustme.oustsdk.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;


import com.oustme.oustsdk.interfaces.common.BitmapCreateListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by oust on 9/3/18.
 */

public class BitmapGenerator implements Target {

    private BitmapCreateListener bitmapCreateListener;
    private Context mContext;

    public BitmapGenerator(Context context, BitmapCreateListener bitmapCreateListener) {
        this.bitmapCreateListener = bitmapCreateListener;
        this.mContext = context;
    }

    public void generateImageBitmap(String url){
        Log.d("TAG", "generateImageBitmap: "+url);
        Picasso.get()
                .load(url)
                .into(this);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if(bitmapCreateListener!=null)
        bitmapCreateListener.onBitmapCreated(bitmap);
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        bitmapCreateListener.onNoBitmapFound();
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
