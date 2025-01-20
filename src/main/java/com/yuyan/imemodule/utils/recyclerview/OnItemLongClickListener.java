package com.yuyan.imemodule.utils.recyclerview;

import android.view.View;

/**
 * Created by YanZhenjie on 2017/7/21.
 */
public interface OnItemLongClickListener {

    /**
     * @param view target view.
     * @param adapterPosition position of item.
     */
    void onItemLongClick(View view, int adapterPosition);
}