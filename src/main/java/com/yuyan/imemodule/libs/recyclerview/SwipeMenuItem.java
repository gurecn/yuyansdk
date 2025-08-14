package com.yuyan.imemodule.libs.recyclerview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

public class SwipeMenuItem {

    private Context mContext;
    private Drawable background;
    private Drawable icon;
    private String title;
    private ColorStateList titleColor;
    private int titleSize;
    private int width = -2;
    private int height = -2;
    private int weight = 0;

    public SwipeMenuItem(Context context) {
        mContext = context;
    }

    public SwipeMenuItem setBackground(@DrawableRes int resId) {
        return setBackground(ContextCompat.getDrawable(mContext, resId));
    }

    public SwipeMenuItem setBackground(Drawable background) {
        this.background = background;
        return this;
    }

    public Drawable getBackground() {
        return background;
    }

    public SwipeMenuItem setImage(@DrawableRes int resId) {
        return setImage(ContextCompat.getDrawable(mContext, resId));
    }

    public SwipeMenuItem setImage(Drawable icon) {
        this.icon = icon;
        return this;
    }

    public Drawable getImage() {
        return icon;
    }

    public SwipeMenuItem setText(@StringRes int resId) {
        return setText(mContext.getString(resId));
    }

    public SwipeMenuItem setText(String title) {
        this.title = title;
        return this;
    }

    public String getText() {
        return title;
    }

    public SwipeMenuItem setTextColor(@ColorInt int titleColor) {
        this.titleColor = ColorStateList.valueOf(titleColor);
        return this;
    }

    public ColorStateList getTitleColor() {
        return titleColor;
    }

    public SwipeMenuItem setTextSize(int titleSize) {
        this.titleSize = titleSize;
        return this;
    }

    public int getTextSize() {
        return titleSize;
    }

    public SwipeMenuItem setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public SwipeMenuItem setHeight(int height) {
        this.height = height;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }
}