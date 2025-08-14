package com.yuyan.imemodule.libs.recyclerview;

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