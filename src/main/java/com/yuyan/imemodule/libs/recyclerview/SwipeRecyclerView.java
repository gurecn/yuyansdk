package com.yuyan.imemodule.libs.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.yuyan.imemodule.libs.recyclerview.touch.DefaultItemTouchHelper;
import com.yuyan.imemodule.libs.recyclerview.touch.OnItemMoveListener;
import com.yuyan.imemodule.libs.recyclerview.touch.DefaultItemTouchHelper;
import com.yuyan.imemodule.libs.recyclerview.touch.OnItemMoveListener;

import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeRecyclerView extends RecyclerView {

    /**
     * Left menu.
     */
    public static final int LEFT_DIRECTION = 1;
    /**
     * Right menu.
     */
    public static final int RIGHT_DIRECTION = -1;

    /**
     * Invalid position.
     */
    private static final int INVALID_POSITION = -1;

    protected int mScaleTouchSlop;
    protected SwipeMenuLayout mOldSwipedLayout;
    protected int mOldTouchedPosition = INVALID_POSITION;

    private int mDownX;
    private int mDownY;

    private DefaultItemTouchHelper mItemTouchHelper;

    private SwipeMenuCreator mSwipeMenuCreator;
    private OnItemMenuClickListener mOnItemMenuClickListener;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private AdapterWrapper mAdapterWrapper;

    public SwipeRecyclerView(Context context) {
        this(context, null);
    }

    public SwipeRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScaleTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    private void initializeItemTouchHelper() {
        if (mItemTouchHelper == null) {
            mItemTouchHelper = new DefaultItemTouchHelper();
            mItemTouchHelper.attachToRecyclerView(this);
        }
    }

    /**
     * Set OnItemMoveListener.
     *
     * @param listener {@link OnItemMoveListener}.
     */
    public void setOnItemMoveListener(OnItemMoveListener listener) {
        initializeItemTouchHelper();
        this.mItemTouchHelper.setOnItemMoveListener(listener);
    }

    /**
     * Set can long press drag.
     *
     * @param canDrag drag true, otherwise is can't.
     */
    public void setLongPressDragEnabled(boolean canDrag) {
        initializeItemTouchHelper();
        this.mItemTouchHelper.setLongPressDragEnabled(canDrag);
    }

    /**
     * Check the Adapter and throw an exception if it already exists.
     */
    private void checkAdapterExist(String message) {
        if (mAdapterWrapper != null) throw new IllegalStateException(message);
    }

    /**
     * Set item click listener.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        if (listener == null) return;
        checkAdapterExist("Cannot set item click listener, setAdapter has already been called.");
        this.mOnItemClickListener = new ItemClickListener(this, listener);
    }

    private static class ItemClickListener implements OnItemClickListener {

        private final SwipeRecyclerView mRecyclerView;
        private final OnItemClickListener mListener;

        public ItemClickListener(SwipeRecyclerView recyclerView, OnItemClickListener listener) {
            this.mRecyclerView = recyclerView;
            this.mListener = listener;
        }

        @Override
        public void onItemClick(View itemView, int position) {
            position -= mRecyclerView.getHeaderCount();
            if (position >= 0) mListener.onItemClick(itemView, position);
        }
    }

    /**
     * Set item click listener.
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (listener == null) return;
        checkAdapterExist("Cannot set item long click listener, setAdapter has already been called.");
        this.mOnItemLongClickListener = new ItemLongClickListener(this, listener);
    }

    private static class ItemLongClickListener implements OnItemLongClickListener {

        private final SwipeRecyclerView mRecyclerView;
        private final OnItemLongClickListener mListener;

        public ItemLongClickListener(SwipeRecyclerView recyclerView, OnItemLongClickListener listener) {
            this.mRecyclerView = recyclerView;
            this.mListener = listener;
        }

        @Override
        public void onItemLongClick(View itemView, int position) {
            position -= mRecyclerView.getHeaderCount();
            if (position >= 0) mListener.onItemLongClick(itemView, position);
        }
    }

    /**
     * Set to create menu listener.
     */
    public void setSwipeMenuCreator(SwipeMenuCreator menuCreator) {
        if (menuCreator == null) return;
        checkAdapterExist("Cannot set menu creator, setAdapter has already been called.");
        this.mSwipeMenuCreator = menuCreator;
    }

    /**
     * Set to click menu listener.
     */
    public void setOnItemMenuClickListener(OnItemMenuClickListener listener) {
        if (listener == null) return;
        checkAdapterExist("Cannot set menu item click listener, setAdapter has already been called.");
        this.mOnItemMenuClickListener = new ItemMenuClickListener(this, listener);
    }

    private static class ItemMenuClickListener implements OnItemMenuClickListener {

        private final SwipeRecyclerView mRecyclerView;
        private final OnItemMenuClickListener mListener;

        public ItemMenuClickListener(SwipeRecyclerView recyclerView, OnItemMenuClickListener listener) {
            this.mRecyclerView = recyclerView;
            this.mListener = listener;
        }

        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            position -= mRecyclerView.getHeaderCount();
            if (position >= 0) {
                mListener.onItemClick(menuBridge, position);
            }
        }
    }

    @Override
    public void setLayoutManager(LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager gridLayoutManager) {
            final GridLayoutManager.SpanSizeLookup spanSizeLookupHolder = gridLayoutManager.getSpanSizeLookup();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (mAdapterWrapper.isHeader(position) || mAdapterWrapper.isFooter(position)) {
                        return gridLayoutManager.getSpanCount();
                    }
                    if (spanSizeLookupHolder != null) {
                        return spanSizeLookupHolder.getSpanSize(position - getHeaderCount());
                    }
                    return 1;
                }
            });
        }
        super.setLayoutManager(layoutManager);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mAdapterWrapper != null) {
            mAdapterWrapper.getOriginAdapter().unregisterAdapterDataObserver(mAdapterDataObserver);
        }

        if (adapter == null) {
            mAdapterWrapper = null;
        } else {
            adapter.registerAdapterDataObserver(mAdapterDataObserver);

            mAdapterWrapper = new AdapterWrapper(getContext(), adapter);
            mAdapterWrapper.setOnItemClickListener(mOnItemClickListener);
            mAdapterWrapper.setOnItemLongClickListener(mOnItemLongClickListener);
            mAdapterWrapper.setSwipeMenuCreator(mSwipeMenuCreator);
            mAdapterWrapper.setOnItemMenuClickListener(mOnItemMenuClickListener);
            if (!mFooterViewList.isEmpty()) {
                for (View view : mFooterViewList) {
                    mAdapterWrapper.addFooterView(view);
                }
            }
        }
        super.setAdapter(mAdapterWrapper);
    }

    private final AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            mAdapterWrapper.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            positionStart += getHeaderCount();
            mAdapterWrapper.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            positionStart += getHeaderCount();
            mAdapterWrapper.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            positionStart += getHeaderCount();
            mAdapterWrapper.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            positionStart += getHeaderCount();
            mAdapterWrapper.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            fromPosition += getHeaderCount();
            toPosition += getHeaderCount();
            mAdapterWrapper.notifyItemMoved(fromPosition, toPosition);
        }
    };
    private final List<View> mFooterViewList = new ArrayList<>();

    /**
     * Add view at the footer.
     */
    public void addFooterView(View view) {
        mFooterViewList.add(view);
        if (mAdapterWrapper != null) {
            mAdapterWrapper.addFooterViewAndNotify(view);
        }
    }

    public void removeFooterView(View view) {
        mFooterViewList.remove(view);
        if (mAdapterWrapper != null) {
            mAdapterWrapper.removeFooterViewAndNotify(view);
        }
    }

    /**
     * Get size of headers.
     */
    public int getHeaderCount() {
        if (mAdapterWrapper == null) return 0;
        return mAdapterWrapper.getHeaderCount();
    }

    /**
     * Get size of footer.
     */
    public int getFooterCount() {
        if (mAdapterWrapper == null) return 0;
        return mAdapterWrapper.getFooterCount();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        boolean isIntercepted = super.onInterceptTouchEvent(e);
        if (mSwipeMenuCreator == null) {
            return isIntercepted;
        } else {
            if (e.getPointerCount() > 1) return true;
            int action = e.getAction();
            int x = (int)e.getX();
            int y = (int)e.getY();

            int touchPosition = getChildAdapterPosition(findChildViewUnder(x, y));
            ViewHolder touchVH = findViewHolderForAdapterPosition(touchPosition);
            SwipeMenuLayout touchView = null;
            if (touchVH != null) {
                View itemView = getSwipeMenuView(touchVH.itemView);
                if (itemView instanceof SwipeMenuLayout) {
                    touchView = (SwipeMenuLayout)itemView;
                }
            }
            if (touchView != null) {
                touchView.setSwipeEnable(true);
            }
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    mDownX = x;
                    mDownY = y;

                    isIntercepted = false;
                    if (touchPosition != mOldTouchedPosition && mOldSwipedLayout != null &&
                        mOldSwipedLayout.isMenuOpen()) {
                        mOldSwipedLayout.smoothCloseMenu();
                        isIntercepted = true;
                    }

                    if (isIntercepted) {
                        mOldSwipedLayout = null;
                        mOldTouchedPosition = INVALID_POSITION;
                    } else if (touchView != null) {
                        mOldSwipedLayout = touchView;
                        mOldTouchedPosition = touchPosition;
                    }
                    break;
                }
                // They are sensitive to retain sliding and inertia.
                case MotionEvent.ACTION_MOVE: {
                    isIntercepted = handleUnDown(x, y, isIntercepted);
                    if (mOldSwipedLayout == null) break;
                    ViewParent viewParent = getParent();
                    if (viewParent == null) break;
                    int disX = mDownX - x;
                    // 向左滑，显示右侧菜单，或者关闭左侧菜单。
                    boolean showRightCloseLeft = disX > 0 && (mOldSwipedLayout.hasRightMenu() || mOldSwipedLayout.isLeftCompleteOpen());
                    // 向右滑，显示左侧菜单，或者关闭右侧菜单。
                    boolean showLeftCloseRight = disX < 0 && (mOldSwipedLayout.hasLeftMenu() || mOldSwipedLayout.isRightCompleteOpen());
                    viewParent.requestDisallowInterceptTouchEvent(showRightCloseLeft || showLeftCloseRight);
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    isIntercepted = handleUnDown(x, y, isIntercepted);
                    break;
                }
            }
        }
        return isIntercepted;
    }

    private boolean handleUnDown(int x, int y, boolean defaultValue) {
        int disX = mDownX - x;
        int disY = mDownY - y;
        // swipe
        if (Math.abs(disX) > mScaleTouchSlop && Math.abs(disX) > Math.abs(disY)) return false;
        // click
        if (Math.abs(disY) < mScaleTouchSlop && Math.abs(disX) < mScaleTouchSlop) return false;
        return defaultValue;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_MOVE:
                if (mOldSwipedLayout != null && mOldSwipedLayout.isMenuOpen()) {
                    mOldSwipedLayout.smoothCloseMenu();
                }
                break;
        }
        return super.onTouchEvent(e);
    }

    private View getSwipeMenuView(View itemView) {
        if (itemView instanceof SwipeMenuLayout) return itemView;
        List<View> unvisited = new ArrayList<>();
        unvisited.add(itemView);
        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            if (!(child instanceof ViewGroup group)) { // view
                continue;
            }
            if (child instanceof SwipeMenuLayout) return child;
            final int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) unvisited.add(group.getChildAt(i));
        }
        return itemView;
    }
}