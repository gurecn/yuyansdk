package com.yuyan.imemodule.utils.recyclerview;

import android.view.View;

/**
 * Created by YanZhenjie on 2017/7/21.
 */
public interface OnItemClickListener {

    /**
     * @param view target view.
     * @param adapterPosition position of item.
     */
    void onItemClick(View view, int adapterPosition);
}