package com.yuyan.imemodule.utils.recyclerview;

/**
 * Created by Yan Zhenjie on 2016/7/26.
 */
public interface OnItemMenuClickListener {

    /**
     * @param menuBridge menu bridge.
     * @param adapterPosition position of item.
     */
    void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition);
}