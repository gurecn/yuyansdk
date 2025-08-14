package com.yuyan.imemodule.libs.recyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

public class StickyNestedScrollView extends NestedScrollView {

    /**
     * Tag for views that should stick and have constant drawing. e.g. TextViews, ImageViews etc
     */
    public static final String STICKY_TAG = "sticky";
    /**
     * Flag for views that should stick and have non-constant drawing. e.g. Buttons, ProgressBars etc
     */
    public static final String FLAG_NONCONSTANT = "-nonconstant";
    /**
     * Flag for views that have aren't fully opaque
     */
    public static final String FLAG_HASTRANSPARENCY = "-hastransparency";
    /**
     * Default height of the shadow peeking out below the stuck view.
     */
    private static final int DEFAULT_SHADOW_HEIGHT = 10; // dp;
    private ArrayList<View> stickyViews;
    private View currentlyStickingView;
    private float stickyViewTopOffset;
    private final Runnable invalidateRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentlyStickingView != null) {
                int l = getLeftForViewRelativeOnlyChild(currentlyStickingView);
                int t = getBottomForViewRelativeOnlyChild(currentlyStickingView);
                int r = getRightForViewRelativeOnlyChild(currentlyStickingView);
                int b = (int)(getScrollY() + (currentlyStickingView.getHeight() + stickyViewTopOffset));
                invalidate(l, t, r, b);
            }
            postDelayed(this, 16);
        }
    };
    private int stickyViewLeftOffset;
    private boolean redirectTouchesToStickyView;
    private boolean clippingToPadding;
    private boolean clipToPaddingHasBeenSet;
    private boolean hasNotDoneActionDown = true;

    public StickyNestedScrollView(Context context) {
        this(context, null);
    }

    public StickyNestedScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.scrollViewStyle);
    }

    public StickyNestedScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    public void setup() {
        stickyViews = new ArrayList<>();
    }

    private int getLeftForViewRelativeOnlyChild(View v) {
        int left = v.getLeft();
        while (v.getParent() != null && v.getParent() != getChildAt(0)) {
            v = (View)v.getParent();
            left += v.getLeft();
        }
        return left;
    }

    private int getTopForViewRelativeOnlyChild(View v) {
        int top = v.getTop();
        while (v.getParent() != null && v.getParent() != getChildAt(0)) {
            v = (View)v.getParent();
            top += v.getTop();
        }
        return top;
    }

    private int getRightForViewRelativeOnlyChild(View v) {
        int right = v.getRight();
        while (v.getParent() != null && v.getParent() != getChildAt(0)) {
            v = (View)v.getParent();
            right += v.getRight();
        }
        return right;
    }

    private int getBottomForViewRelativeOnlyChild(View v) {
        int bottom = v.getBottom();
        while (v.getParent() != null && v.getParent() != getChildAt(0)) {
            v = (View)v.getParent();
            bottom += v.getBottom();
        }
        return bottom;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!clipToPaddingHasBeenSet) {
            clippingToPadding = true;
        }
        notifyHierarchyChanged();
    }

    @Override
    public void setClipToPadding(boolean clipToPadding) {
        super.setClipToPadding(clipToPadding);
        clippingToPadding = clipToPadding;
        clipToPaddingHasBeenSet = true;
    }

    @Override
    public void addView(@NonNull View child) {
        super.addView(child);
        findStickyViews(child);
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        findStickyViews(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        findStickyViews(child);
    }

    @Override
    public void addView(View child, int width, int height) {
        super.addView(child, width, height);
        findStickyViews(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        findStickyViews(child);
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);
        if (currentlyStickingView != null) {
            canvas.save();
            canvas.translate(getPaddingLeft() + stickyViewLeftOffset,
                getScrollY() + stickyViewTopOffset + (clippingToPadding ? getPaddingTop() : 0));
            canvas.clipRect(0, (clippingToPadding ? -stickyViewTopOffset : 0), getWidth() - stickyViewLeftOffset,
                currentlyStickingView.getHeight() + DEFAULT_SHADOW_HEIGHT + 1);
            canvas.clipRect(0, (clippingToPadding ? -stickyViewTopOffset : 0), getWidth(),
                currentlyStickingView.getHeight());
            if (getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARENCY)) {
                showView(currentlyStickingView);
                currentlyStickingView.draw(canvas);
                hideView(currentlyStickingView);
            } else {
                currentlyStickingView.draw(canvas);
            }
            canvas.restore();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            redirectTouchesToStickyView = true;
        }
        if (redirectTouchesToStickyView) {
            redirectTouchesToStickyView = currentlyStickingView != null;
            if (redirectTouchesToStickyView) {
                redirectTouchesToStickyView = ev.getY() <= (currentlyStickingView.getHeight() + stickyViewTopOffset) &&
                    ev.getX() >= getLeftForViewRelativeOnlyChild(currentlyStickingView) &&
                    ev.getX() <= getRightForViewRelativeOnlyChild(currentlyStickingView);
            }
        } else if (currentlyStickingView == null) {
            redirectTouchesToStickyView = false;
        }
        if (redirectTouchesToStickyView) {
            ev.offsetLocation(0,
                -1 * ((getScrollY() + stickyViewTopOffset) - getTopForViewRelativeOnlyChild(currentlyStickingView)));
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        if (redirectTouchesToStickyView) {
            ev.offsetLocation(0,
                ((getScrollY() + stickyViewTopOffset) - getTopForViewRelativeOnlyChild(currentlyStickingView)));
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            hasNotDoneActionDown = false;
        }
        if (hasNotDoneActionDown) {
            MotionEvent down = MotionEvent.obtain(ev);
            down.setAction(MotionEvent.ACTION_DOWN);
            super.onTouchEvent(down);
            hasNotDoneActionDown = false;
        }
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            hasNotDoneActionDown = true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        doTheStickyThing();
    }

    private void doTheStickyThing() {
        View viewThatShouldStick = null;
        View approachingView = null;
        for (View v : stickyViews) {
            int viewTop = getTopForViewRelativeOnlyChild(v) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop());
            if (viewTop <= 0) {
                if (viewThatShouldStick == null || viewTop >
                    (getTopForViewRelativeOnlyChild(viewThatShouldStick) - getScrollY() +
                        (clippingToPadding ? 0 : getPaddingTop()))) {
                    viewThatShouldStick = v;
                }
            } else {
                if (approachingView == null || viewTop <
                    (getTopForViewRelativeOnlyChild(approachingView) - getScrollY() +
                        (clippingToPadding ? 0 : getPaddingTop()))) {
                    approachingView = v;
                }
            }
        }
        if (viewThatShouldStick != null) {
            stickyViewTopOffset = approachingView == null
                ? 0
                : Math.min(0, getTopForViewRelativeOnlyChild(approachingView) - getScrollY() +
                    (clippingToPadding ? 0 : getPaddingTop()) - viewThatShouldStick.getHeight());
            if (viewThatShouldStick != currentlyStickingView) {
                if (currentlyStickingView != null) {
                    stopStickingCurrentlyStickingView();
                }
                // only compute the left offset when we start sticking.
                stickyViewLeftOffset = getLeftForViewRelativeOnlyChild(viewThatShouldStick);
                startStickingView(viewThatShouldStick);
            }
        } else if (currentlyStickingView != null) {
            stopStickingCurrentlyStickingView();
        }
    }

    private void startStickingView(View viewThatShouldStick) {
        currentlyStickingView = viewThatShouldStick;
        if (currentlyStickingView != null) {
            if (getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARENCY)) {
                hideView(currentlyStickingView);
            }
            if (getStringTagForView(currentlyStickingView).contains(FLAG_NONCONSTANT)) {
                post(invalidateRunnable);
            }
        }
    }

    private void stopStickingCurrentlyStickingView() {
        if (getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARENCY)) {
            showView(currentlyStickingView);
        }
        currentlyStickingView = null;
        removeCallbacks(invalidateRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(invalidateRunnable);
        super.onDetachedFromWindow();
    }

    private void notifyHierarchyChanged() {
        if (currentlyStickingView != null) {
            stopStickingCurrentlyStickingView();
        }
        stickyViews.clear();
        findStickyViews(getChildAt(0));
        doTheStickyThing();
        invalidate();
    }

    private void findStickyViews(View v) {
        if (!detainStickyView(v) && (v instanceof ViewGroup vg)) {
            for (int i = 0; i < vg.getChildCount(); i++)
                findStickyViews(vg.getChildAt(i));
        }
    }

    private boolean detainStickyView(View view) {
        String tag = getStringTagForView(view);
        if (tag.contains(STICKY_TAG)) {
            stickyViews.add(view);
            return true;
        }
        return false;
    }

    private String getStringTagForView(View v) {
        Object tagObject = v.getTag();
        return String.valueOf(tagObject);
    }

    private void hideView(View v) {
        v.setAlpha(0);
    }

    private void showView(View v) {
        v.setAlpha(1);
    }

}