package com.yuyan.imemodule.libs.recyclerview;

import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.IntDef;

public class SwipeMenu {

    @IntDef({LinearLayout.HORIZONTAL, LinearLayout.VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {}

    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    @OrientationMode
    private int mOrientation;
    private final List<SwipeMenuItem> mSwipeMenuItems;

    public SwipeMenu() {
        this.mOrientation = HORIZONTAL;
        this.mSwipeMenuItems = new ArrayList<>(2);
    }

    /**
     * Set the menu mOrientation.
     */
    public void setOrientation(@OrientationMode int orientation) {
        this.mOrientation = orientation;
    }

    /**
     * Get the menu mOrientation.
     */
    public int getOrientation() {
        return mOrientation;
    }

    public void addMenuItem(SwipeMenuItem item) {
        mSwipeMenuItems.add(item);
    }


    public List<SwipeMenuItem> getMenuItems() {
        return mSwipeMenuItems;
    }

    public boolean hasMenuItems() {
        return !mSwipeMenuItems.isEmpty();
    }
}