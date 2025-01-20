package com.yuyan.imemodule.libs.recyclerview;

/**
 * Created by YanZhenjie on 2017/7/20.
 */
public class SwipeMenuBridge {

    private final Controller mController;
    private final int mPosition;

    public SwipeMenuBridge(Controller controller, int position) {
        this.mController = controller;
        this.mPosition = position;
    }

    /**
     * Get the position of button in the menu.
     */
    public int getPosition() {
        return mPosition;
    }

    public void closeMenu() {
        mController.smoothCloseMenu();
    }
}