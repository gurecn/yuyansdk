package com.yuyan.imemodule.libs.recyclerview;

public interface SwipeMenuCreator {

    /**
     * Create menu for recyclerVie item.
     *
     * @param leftMenu the menu on the left.
     * @param rightMenu the menu on the right.
     * @param position the position of item.
     */
    void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position);
}