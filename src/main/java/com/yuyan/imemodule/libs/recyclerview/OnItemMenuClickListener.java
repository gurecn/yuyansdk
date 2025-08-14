package com.yuyan.imemodule.libs.recyclerview;

public interface OnItemMenuClickListener {

    /**
     * @param menuBridge menu bridge.
     * @param adapterPosition position of item.
     */
    void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition);
}