package com.oustme.oustsdk.customviews;

import android.content.Context;
import androidx.appcompat.widget.SearchView;

/**
 * Created by shilpysamaddar on 08/03/17.
 */

public class CustomSearchView  extends SearchView {

    OnSearchViewCollapsedEventListener mSearchViewCollapsedEventListener;
    OnSearchViewExpandedEventListener mOnSearchViewExpandedEventListener;

    public CustomSearchView(Context context) {
        super(context);
    }

    @Override
    public void onActionViewCollapsed() {
        if (mSearchViewCollapsedEventListener != null)
            mSearchViewCollapsedEventListener.onSearchViewCollapsed();
        super.onActionViewCollapsed();
    }

    @Override
    public void onActionViewExpanded() {
        if (mOnSearchViewExpandedEventListener != null)
            mOnSearchViewExpandedEventListener.onSearchViewExpanded();
        super.onActionViewExpanded();
    }

    public interface OnSearchViewCollapsedEventListener {
        void onSearchViewCollapsed();
    }

    public interface OnSearchViewExpandedEventListener {
        void onSearchViewExpanded();
    }

    public void setOnSearchViewCollapsedEventListener(OnSearchViewCollapsedEventListener eventListener) {
        mSearchViewCollapsedEventListener = eventListener;
    }
    //
    public void setOnSearchViewExpandedEventListener(OnSearchViewExpandedEventListener eventListener) {
        mOnSearchViewExpandedEventListener = eventListener;
    }

}

