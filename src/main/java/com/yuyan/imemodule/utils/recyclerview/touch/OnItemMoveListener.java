package com.yuyan.imemodule.utils.recyclerview.touch;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Yolanda on 2016/4/19.
 */
public interface OnItemMoveListener {

    /**
     * When drag and drop the callback.
     *
     * @param srcHolder src.
     * @param targetHolder target.
     *
     * @return To deal with the returns true, false otherwise.
     */
    boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder);

    /**
     * When items should be removed when the callback.
     *
     * @param srcHolder src.
     */
    void onItemDismiss(RecyclerView.ViewHolder srcHolder);

}