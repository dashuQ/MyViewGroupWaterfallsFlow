package com.example.myviewgroupwaterfallsflow.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.example.myviewgroupwaterfallsflow.activity.MainActivity;
import com.example.myviewgroupwaterfallsflow.util.VirtualImage;

/**
 * Created by lenovo on 2017/5/8.
 */

public class MyViewGroup extends ViewGroup {
    private float mLastMotionY;
    private int mTouchSlop;
    private boolean mIsBeingDragged;
    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mMaxScrollY = 0;

    public MyViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initScrollView();
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MyViewGroup(Context context) {
        this(context, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        layoutTimes = 0;
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(ev);
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastMotionY = (int) ev.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE:
                final float y = ev.getY();
                final int deltaY = (int) (mLastMotionY - y);
                if (!mIsBeingDragged) {
                    if (Math.abs(deltaY) > mTouchSlop) {
                        mIsBeingDragged = true;
                    }
                }
                if (mIsBeingDragged) {
                    mLastMotionY = y;
                    float oldScrollY = getScrollY();
                    int scrollY = (int) (oldScrollY + deltaY);
                    if (scrollY > mMaxScrollY) {
                        scrollY = mMaxScrollY;
                    } else if (scrollY < 0) {
                        scrollY = 0;
                    }
                    scrollTo(0, scrollY);
                    refreshImages();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int initialVelocity = (int) velocityTracker
                            .getYVelocity();

                    if (getChildCount() > 0) {
                        // 速度超过某个阀值时才视为fling
                        if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
                            fling(-initialVelocity);
                        }
                    }
                    endDrag();
                }
                break;
        }
        return true;
    }

    private void endDrag() {
        mIsBeingDragged = false;
        recycleVelocityTracker();
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public void fling(int velocityY) {
        mScroller.fling(getScrollX(), getScrollY(), 0, velocityY, 0, 0, 0,
                getmMaxScrollY());

        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            if (oldX != x || oldY != y) {
                scrollTo(x, y);
                refreshImages();
            }
            invalidate();
            return;
        }
    }

    private void initScrollView() {
        mScroller = new Scroller(getContext());
        final ViewConfiguration configuration = ViewConfiguration
                .get(getContext());
        mTouchSlop = ViewConfigurationCompat
                .getScaledPagingTouchSlop(configuration);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        return true;
    }

    public int getmMaxScrollY() {
        return mMaxScrollY;
    }

    public void setmMaxScrollY(int mMaxScrollY) {
        if (mMaxScrollY > this.mMaxScrollY)
            this.mMaxScrollY = mMaxScrollY;
    }
    int layoutTimes = 0;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        for (int i = 0; i < MainActivity.mVImages.size(); i++) {
            MyIV child = MainActivity.mVImages.get(i).mIV;
            if (child != null) {
                child.layout(child.mVI.x, child.mVI.y, child.mVI.x
                        + child.mVI.mWidth, child.mVI.y + child.mVI.mHeight);
            }
        }
    }

    private void refreshImages() {
        for (int i = 0; i < MainActivity.mVImages.size(); i++) {
            VirtualImage vi = MainActivity.mVImages.get(i);
            if (isInScreenVisible(vi)) {
                if (vi.mState == VirtualImage.ImageViewState.NONE) {
                    vi.reLoadBMP(this);
                }
            } else {
                vi.recycleBMP(this);
            }
        }
        if (!mHasAdd && isInScreenVisible(mFirstInVisibleVI)) {
            mFirstInVisibleVI.justRecycleBMP();
            mFirstInVisibleVI.loadMoreBMP();
            mHasAdd = true;
        }
    }

    public boolean isInScreenVisible(VirtualImage vi) {
        if (vi == null) {
            Log.e("asdasd", "vi is null");
            return true;
        }
        int viTop = vi.y;
        int viBottom = vi.mHeight + viTop;
        int scTop = getScrollY();
        int scBottom = getHeight() + scTop;
        if ((scTop >= viTop && scTop <= viBottom)
                || (scTop <= viTop && scBottom >= viBottom)
                || (scBottom >= viTop && scBottom <= viBottom)) {
            return true;
        } else {
            return false;
        }
    }

    private VirtualImage mFirstInVisibleVI;
    private boolean mHasAdd = true;

    public void setFirstInVisibleVI(VirtualImage vi) {
        mFirstInVisibleVI = vi;
        mHasAdd = false;
        setmMaxScrollY(vi.y + vi.mHeight);
    }

    @Override
    public void addView(View child) {
        // TODO Auto-generated method stub
        super.addView(child);
        if (child instanceof MyIV) {
            VirtualImage vi = ((MyIV) child).mVI;
            setmMaxScrollY(vi.y + vi.mHeight);
        }
    }
}
