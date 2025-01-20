package com.yuyan.imemodule.utils.recyclerview.touch;

import androidx.recyclerview.widget.ItemTouchHelper;

/**
 * Created by Yolanda on 2016/4/19.
 */
public class DefaultItemTouchHelper extends ItemTouchHelper {

    private final ItemTouchHelperCallback mItemTouchHelperCallback;

    /**
     * Create default item touch helper.
     */
    public DefaultItemTouchHelper() {
        this(new ItemTouchHelperCallback());
    }

    /**
     * @param callback the behavior of ItemTouchHelper.
     */
    private DefaultItemTouchHelper(ItemTouchHelperCallback callback) {
        super(callback);
        mItemTouchHelperCallback = callback;
    }

    /**
     * Set OnItemMoveListener.
     *
     * @param onItemMoveListener {@link OnItemMoveListener}.
     */
    public void setOnItemMoveListener(OnItemMoveListener onItemMoveListener) {
        mItemTouchHelperCallback.setOnItemMoveListener(onItemMoveListener);
    }

    /**
     * Set can long press drag.
     *
     * @param canDrag drag true, otherwise is can't.
     */
    public void setLongPressDragEnabled(boolean canDrag) {
        mItemTouchHelperCallback.setLongPressDragEnabled(canDrag);
    }

}