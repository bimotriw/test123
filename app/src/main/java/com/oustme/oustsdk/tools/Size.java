package com.oustme.oustsdk.tools;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.os.Build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Size implements Serializable{

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Size(android.util.Size size) {
        this.mWidth = size.getWidth();
        this.mHeight = size.getHeight();
    }

    @SuppressWarnings("deprecation")
    public Size(Camera.Size size) {
        this.mWidth = size.width;
        this.mHeight = size.height;
    }

    public Size(int width, int height) {
        mWidth = width;
        mHeight = height;
    }
    /**
     * Get the width of the size (in pixels).
     * @return width
     */
    public int getWidth() {
        return mWidth;
    }
    /**
     * Get the height of the size (in pixels).
     * @return height
     */
    public int getHeight() {
        return mHeight;
    }
    /**
     * Check if this size is equal to another size.
     * <p>
     * Two sizes are equal if and only if both their widths and heights are
     * equal.
     * </p>
     * <p>
     * A size object is never equal to any other type of object.
     * </p>
     *
     * @return {@code true} if the objects were equal, {@code false} otherwise
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Size) {
            Size other = (Size) obj;
            return mWidth == other.mWidth && mHeight == other.mHeight;
        }
        return false;
    }
    /**
     * Return the size represented as a string with the format {@code "WxH"}
     *
     * @return string representation of the size
     */
    @Override
    public String toString() {
        return mWidth + "x" + mHeight;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // assuming most sizes are <2^16, doing a rotate will give us perfect hashing
        return mHeight ^ ((mWidth << (Integer.SIZE / 2)) | (mWidth >>> (Integer.SIZE / 2)));
    }
    private final int mWidth;
    private final int mHeight;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Size[] fromArray2(android.util.Size[] sizes) {
        if (sizes == null) return null;
        Size[] result = new Size[sizes.length];

        for (int i = 0; i < sizes.length; ++i) {
            result[i] = new Size(sizes[i]);
        }

        return result;
    }

    @SuppressWarnings("deprecation")
    public static Size[] fromArray(Camera.Size[] sizes) {
        if (sizes == null) return null;
        Size[] result = new Size[sizes.length];

        for (int i = 0; i < sizes.length; ++i) {
            result[i] = new Size(sizes[i]);
        }

        return result;
    }

    public static List<Size> fromList(List<Camera.Size> sizes) {
        if (sizes == null) return null;
        List<Size> result = new ArrayList<>();

        for (int i = 0; i < sizes.size(); ++i) {
            Size size= new Size(sizes.get(i));
            result.add(size);
        }

        return result;
    }
    public static List<Size> fromList2(android.util.Size[] sizes) {
        if (sizes == null) return null;
        List<Size> result = new ArrayList<>();
        for (int i = 0; i < sizes.length; ++i) {
            Size size= new Size(sizes[i]);
            result.add(size);
        }

        return result;
    }



}
