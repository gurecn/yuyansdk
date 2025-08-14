package com.yuyan.imemodule.libs.recyclerview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;

abstract class Horizontal {

    private int direction;
    private View menuView;
    protected Checker mChecker;

    public Horizontal(int direction, View menuView) {
        this.direction = direction;
        this.menuView = menuView;
        mChecker = new Checker();
    }

    public boolean canSwipe() {
        if (menuView instanceof ViewGroup) {
            return ((ViewGroup)menuView).getChildCount() > 0;
        }
        return false;
    }

    public boolean isCompleteClose(int scrollX) {
        int i = -getMenuView().getWidth() * getDirection();
        return scrollX == 0 && i != 0;
    }

    public abstract boolean isMenuOpen(int scrollX);

    public abstract boolean isMenuOpenNotEqual(int scrollX);

    public abstract void autoOpenMenu(OverScroller scroller, int scrollX, int duration);

    public abstract void autoCloseMenu(OverScroller scroller, int scrollX, int duration);

    public abstract Checker checkXY(int x, int y);

    public abstract boolean isClickOnContentView(int contentViewWidth, float x);

    public int getDirection() {
        return direction;
    }

    public View getMenuView() {
        return menuView;
    }

    public int getMenuWidth() {
        return menuView.getWidth();
    }

    public static final class Checker {

        public int x;
        public int y;
        public boolean shouldResetSwipe;
    }

}