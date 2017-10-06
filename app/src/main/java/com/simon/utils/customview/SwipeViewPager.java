package com.simon.utils.customview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 解决ViewPager和HorizontalScrollView冲突
 * <p>
 * Created by Simon on 2017/9/29.
 */

public class SwipeViewPager extends ViewPager {

    public SwipeViewPager(Context context) {
        super(context);
    }

    public SwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 一定要spuer，否则事件打住,不会在向下调用了
        super.dispatchTouchEvent(ev);
        switch (ev.getAction()) {
            // 记录用户手指点击的位置 在距离屏幕边缘Y轴80内的任何区域 放弃拦截事件继续传递
            case MotionEvent.ACTION_DOWN:
                int StartX = (int) ev.getX();
                if (StartX <= 80) {
                    return false;
                }
                break;
        }
        // 拦截,不向下传递
        return true;
        // 继续向下传递
        // return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
