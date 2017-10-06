package com.simon.utils.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

import com.simon.utils.R;

/**
 * 布局文件
 * <?xml version="1.0" encoding="utf-8"?>
 * <com.simon.utils.customview.SlidingMenu xmlns:android="http://schemas.android.com/apk/res/android"
 * xmlns:Simon="http://schemas.android.com/apk/res-auto"
 * android:id="@+id/sliding_menu"
 * android:layout_width="match_parent"
 * android:layout_height="match_parent"
 * Simon:rightPadding="150dp">
 * <p>
 * <FrameLayout
 * android:id="@+id/fl_left_content"
 * android:layout_width="match_parent"
 * android:layout_height="match_parent" />
 * <p>
 * <RelativeLayout
 * android:layout_width="match_parent"
 * android:layout_height="match_parent">
 * <p>
 * <FrameLayout
 * android:id="@+id/fl_right_content"
 * android:layout_width="match_parent"
 * android:layout_height="match_parent" />
 * <p>
 * <ImageView
 * android:id="@+id/iv_toggle"
 * android:layout_width="60dp"
 * android:layout_height="60dp"
 * android:layout_alignParentStart="true"
 * android:paddingBottom="20dp"
 * android:paddingRight="20dp"
 * android:src="@mipmap/menu_flip" />
 * </RelativeLayout>
 * <p>
 * </com.simon.utils.customview.SlidingMenu>
 */

/**
 * ①：为了简化主容器逻辑代码 将SlidingMenu分为Left 和 Right两个Fragment 想要在点击Left menu 跳转后SlidingMenu隐藏 需在Left容器中获取主容器引用
 * MainActiity mActivity=(MainActivity)getActivity();
 * mActivity.slidingMenuToggle();
 * <p>
 * <p>主容器：slidingmenu 开关
 * public void slidingMenuToggle() {
 * mSliding_menu.toggle();
 * }
 * <p>
 * ②：如果说我们的Right主容器中有ViewPager那么需要使用自定义的 SwipeViewPager
 * ③：slidingMenu有两种模式 1、modeTranslate()  2、modeScale()
 */

@SuppressWarnings("all")
public class SlidingMenu extends ViewGroup {

    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mMenuWidth;
    private int mContentWidth;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mMenuRightPadding;
    private Scroller mScroller;
    private int mLastX;
    private int mLastY;
    private int mLastXIntercept;
    private int mLastYIntercept;
    private float scale;
    private boolean isOpen;


    public SlidingMenu(Context context) {
        this(context, null, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 获取自定义的属性 Menu距离屏幕右侧的距离
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SlidingMenu, defStyleAttr, 0);
        int i = a.getIndexCount();
        for (int j = 0; j < i; j++) {
            int attr = a.getIndex(j);
            switch (attr) {
                case R.styleable.SlidingMenu_rightPadding:
                    mMenuRightPadding = a.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP, 50f, context
                                            .getResources().getDisplayMetrics()));
                    break;

                default:
                    break;
            }
        }
        a.recycle();
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        //获取屏幕的宽和高
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        mScroller = new Scroller(context);
        isOpen = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //拿到Menu，Menu是第0个孩子
        mMenu = (ViewGroup) getChildAt(0);
        //拿到Content，Content是第1个孩子
        mContent = (ViewGroup) getChildAt(1);
        //设置Menu的宽为屏幕的宽度减去Menu距离屏幕右侧的距离
        mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
        //设置Content的宽为屏幕的宽度
        mContentWidth = mContent.getLayoutParams().width = mScreenWidth;
        //测量Menu
        measureChild(mMenu, widthMeasureSpec, heightMeasureSpec);
        //测量Content
        measureChild(mContent, widthMeasureSpec, heightMeasureSpec);
        //测量自己，自己的宽度为Menu宽度加上Content宽度，高度为屏幕高度
        setMeasuredDimension(mMenuWidth + mContentWidth, mScreenHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //摆放Menu的位置，根据上面图可以确定上下左右的坐标
        mMenu.layout(-mMenuWidth, 0, 0, mScreenHeight);
        //摆放Content的位置
        mContent.layout(0, 0, mScreenWidth, mScreenHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) ev.getX() - mLastXIntercept;
                int deltaY = (int) ev.getY() - mLastYIntercept;
                if (Math.abs(deltaX) > Math.abs(deltaY)) {//横向滑动
                    intercept = true;
                } else {//纵向滑动
                    intercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
        }
        mLastX = x;
        mLastY = y;
        mLastXIntercept = x;
        mLastYIntercept = y;
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) event.getX();
                mLastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int currentX = (int) event.getX();
                int currentY = (int) event.getY();
                //拿到x方向的偏移量
                int dx = currentX - mLastX;
                if (dx < 0) {//向左滑动
                    //边界控制，如果Menu已经完全显示，再滑动的话
                    //Menu左侧就会出现白边了,进行边界控制
                    if (getScrollX() + Math.abs(dx) >= 0) {
                        //直接移动到（0，0）位置，不会出现白边
                        scrollTo(0, 0);
                        mMenu.setTranslationX(0);
                    } else {//Menu没有完全显示呢
                        //其实这里dx还是-dx，大家不用刻意去记
                        //大家可以先使用dx，然后运行一下，发现
                        //移动的方向是相反的，那么果断这里加个负号就可以了
                        scrollBy(-dx, 0);
                        modeTranslate();
                    }
                } else {//向右滑动
                    //边界控制，如果Content已经完全显示，再滑动的话
                    //Content右侧就会出现白边了，进行边界控制
                    if (getScrollX() - dx <= -mMenuWidth) {
                        //直接移动到（-mMenuWidth,0）位置，不会出现白边
                        scrollTo(-mMenuWidth, 0);
                        mMenu.setTranslationX(0);
                    } else {//Content没有完全显示呢
                        //根据手指移动
                        scrollBy(-dx, 0);
                        modeTranslate();
                    }
                }
                mLastX = currentX;
                mLastY = currentY;
                scale = Math.abs((float) getScrollX()) / (float) mMenuWidth;
                break;

            case MotionEvent.ACTION_UP:
                if (getScrollX() < -mMenuWidth / 2) {//打开Menu
                    //调用startScroll方法，第一个参数是起始X坐标，第二个参数
                    //是起始Y坐标，第三个参数是X方向偏移量，第四个参数是Y方向偏移量
                    mScroller.startScroll(getScrollX(), 0, -mMenuWidth - getScrollX(), 0, 300);
                    //设置一个已经打开的标识，当实现点击开关自动打开关闭功能时会用到
                    isOpen = true;
                    //一定不要忘了调用这个方法重绘，否则没有动画效果
                    invalidate();
                } else {//关闭Menu
                    //同上
                    mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 300);
                    isOpen = false;
                    invalidate();
                }

                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            scale = Math.abs((float) getScrollX()) / (float) mMenuWidth;
            modeTranslate();
            invalidate();
        }
    }

    private void modeTranslate() {
        mMenu.setTranslationX(2 * (mMenuWidth + getScrollX()) / 3);
    }

    public void modeScale() {
        mMenu.setTranslationX(mMenuWidth + getScrollX() - (mMenuWidth / 2) * (1.0f - scale));
        mMenu.setScaleX(0.7f + 0.3f * scale);
        mMenu.setScaleY(0.7f + 0.3f * scale);
        mMenu.setAlpha(scale);

        mContent.setScaleX(1 - 0.3f * scale);
        mContent.setPivotX(0);
        mContent.setScaleY(1.0f - 0.3f * scale);
    }

    /**
     * 点击开关，开闭Menu，如果当前menu已经打开，则关闭，如果当前menu已经关闭，则打开
     */
    public void toggle() {
        if (isOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    /**
     * 关闭menu
     */
    private void closeMenu() {
        //也是使用startScroll方法，dx和dy的计算方法一样
        mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 500);
        invalidate();
        isOpen = false;
    }

    /**
     * 打开menu
     */
    private void openMenu() {
        mScroller.startScroll(getScrollX(), 0, -mMenuWidth - getScrollX(), 0, 500);
        invalidate();
        isOpen = true;
    }

}
