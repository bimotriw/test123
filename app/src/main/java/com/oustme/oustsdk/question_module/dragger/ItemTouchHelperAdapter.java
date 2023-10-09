package com.oustme.oustsdk.question_module.dragger;

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);
    boolean onItemDismiss(int position);
}
