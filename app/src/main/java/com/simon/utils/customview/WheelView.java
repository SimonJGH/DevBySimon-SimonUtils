package com.simon.utils.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * 顺序不可打乱
 * <p>
 * wva.setOffset(2)// item偏移量 数量=offset*2+1
 * .setItemPadding(6)// textview padding
 * .setItems(mList)// list资源
 * .setSeletion(4)// 默认选中itemPos
 * .setFontSize(18)// 文字大小
 * .setSelectItemColor(Color.RED)// 选中文字颜色 默认黑色
 * .setUnSelectItemColor(Color.BLUE)// 未选中文字颜色 默认黑色
 * .setBGColor(Color.GREEN)// 背景线颜色
 * .setBGStyle("full")// 背景样式 full-全屏 part-半屏
 * .setOnWheelViewListener(new WheelView.OnWheelViewListener() {// wheelview数据回调
 *
 * @Override public void onSelected(int selectedIndex, String item) {
 * super.onSelected(selectedIndex, item);
 * Log.i("Simon", "selectedIndex: " + selectedIndex + ", item: " + item);
 * }
 * });
 */
@SuppressWarnings("all")
public class WheelView extends ScrollView {
    private Context context;
    private float fontSize = 18;// 文字大小
    private int selcetColor = Color.BLACK; // 默认选中颜色
    private int unSelectColor = Color.BLACK;  // 默认未选中颜色
    private int bgColor = Color.parseColor("#83cde6");// 背景颜色
    private String bgStyle = "part";// 背景样式 full-全屏  part-部分
    private List<String> items; // wheelview资源
    // 偏移量设置
    private static final int OFF_SET_DEFAULT = 1;// 默认偏移量
    private int offset = OFF_SET_DEFAULT; //（需要在最前面和最后面补全）
    // 每页显示的数量
    private static final int DISPLAY_COUNT_DEFAULT = 1;// 默认展示数量
    private int displayItemCount = DISPLAY_COUNT_DEFAULT;
    // wheelview回调监听器
    private OnWheelViewListener onWheelViewListener;
    private int scrollDirection = -1; // 滑动方向
    private static final int SCROLL_DIRECTION_UP = 0;// 上滑标识
    private static final int SCROLL_DIRECTION_DOWN = 1;// 下滑标识
    // 背景画笔
    private Paint mPaintBG;
    private Paint mTxtPaint;
    // 背景宽度
    private int viewWidth;
    // 文字padding
    private int paddingSize = 10;
    // wheelview高度
    private int itemHeight = 0;
    // 选中项下标
    private int selectedIndex = 1;
    // wheelview容器（textview）
    private LinearLayout views;
    private Runnable scrollerTask;// 延迟任务
    private int newCheck = 50;// 滑动延迟
    private int initialY;// y轴滑动距离

    public WheelView(Context context) {
        super(context);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        mTxtPaint = new Paint();
        mTxtPaint.setAntiAlias(true);
        this.setVerticalScrollBarEnabled(false);
        views = new LinearLayout(context);
        views.setOrientation(LinearLayout.VERTICAL);
        this.addView(views);
        scrollerTask = new Runnable() {

            public void run() {
                int newY = getScrollY();
                if (initialY - newY == 0) { // stopped
                    final int remainder = initialY % itemHeight;
                    final int divided = initialY / itemHeight;
                    if (remainder == 0) {
                        selectedIndex = divided + offset;
                        onSeletedCallBack();
                    } else {
                        if (remainder > itemHeight / 2) {
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    WheelView.this.smoothScrollTo(0, initialY - remainder + itemHeight);
                                    selectedIndex = divided + offset + 1;
                                    onSeletedCallBack();
                                }
                            });
                        } else {
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    WheelView.this.smoothScrollTo(0, initialY - remainder);
                                    selectedIndex = divided + offset;
                                    onSeletedCallBack();
                                }
                            });
                        }
                    }
                } else {
                    initialY = getScrollY();
                    WheelView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    /**
     * 设置wheelview资源
     *
     * @param list
     */
    public WheelView setItems(List<String> list) {
        if (null == items) {
            items = new ArrayList<String>();
        }
        items.clear();
        items.addAll(list);
        // 前面和后面补全
        for (int i = 0; i < offset; i++) {
            items.add(0, "");
            items.add("");
        }
        for (String item : items) {
            views.addView(createItemView(item));
        }
        refreshItemView(0);
        return this;
    }

    /**
     * 设置文字padding
     *
     * @param size
     */
    public WheelView setItemPadding(int size) {
        this.paddingSize = size;
        return this;
    }

    // 创建wheelview项textview
    private TextView createItemView(String item) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setSingleLine(true);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        tv.setText(item);
        tv.setGravity(Gravity.CENTER);
        int padding = dip2px(paddingSize);
        tv.setPadding(padding, padding, padding, padding);
        if (0 == itemHeight) {
            itemHeight = getViewMeasuredHeight(tv);
            views.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.getLayoutParams();
            this.setLayoutParams(new LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount));
        }
        return tv;
    }

    // 刷新itemview
    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        int remainder = y % itemHeight;
        int divided = y / itemHeight;
        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }
        }
        int childSize = views.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) views.getChildAt(i);
            if (null == itemView) {
                return;
            }
            if (position == i) {
                itemView.setTextColor(selcetColor);
            } else {
                itemView.setTextColor(unSelectColor);
            }
        }
    }

    /**
     * 设置偏移量与显示数量(奇数)
     *
     * @param offset
     */
    public WheelView setOffset(int offset) {
        this.offset = offset;
        displayItemCount = offset * 2 + 1;
        return this;
    }

    /**
     * 设置选中的文字颜色
     */
    public WheelView setSelectItemColor(int color) {
        this.selcetColor = color;
        return this;
    }

    /**
     * 设置文字大小
     */
    public WheelView setFontSize(float size) {
        this.fontSize = size;
        return this;
    }

    /**
     * 设置未选中的文字颜色
     */
    public WheelView setUnSelectItemColor(int color) {
        this.unSelectColor = color;
        return this;
    }

    /**
     * 默认选中
     *
     * @param position
     */
    public WheelView setSeletion(int position) {
        final int p = position;
        selectedIndex = p + offset;
        this.post(new Runnable() {
            @Override
            public void run() {
                WheelView.this.smoothScrollTo(0, p * itemHeight);
            }
        });
        return this;
    }

    /**
     * 获取选择项内容
     *
     * @return
     */
    public String getSeletedItem() {
        return items.get(selectedIndex);
    }

    /**
     * 获取选择项下标
     *
     * @return
     */
    public int getSeletedIndex() {
        return selectedIndex - offset;
    }

    /**
     * 获取选中区域的边界
     */
    int[] selectedAreaBorder;

    private int[] obtainSelectedAreaBorder() {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = new int[2];
            selectedAreaBorder[0] = itemHeight * offset;
            selectedAreaBorder[1] = itemHeight * (offset + 1);
        }
        return selectedAreaBorder;
    }

    // 设置背景色
    public WheelView setBGColor(int color) {
        this.bgColor = color;
        return this;
    }

    // 设置背景样式
    public WheelView setBGStyle(String flag) {
        this.bgStyle = flag;
        return this;
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        if (viewWidth == 0) {
            viewWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        }
        if (null == mPaintBG) {
            mPaintBG = new Paint();
            mPaintBG.setColor(bgColor);
            mPaintBG.setStrokeWidth(dip2px(1f));
        }
        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                if (bgStyle.equals("part")) {
                    canvas.drawLine(viewWidth * 1 / 6, obtainSelectedAreaBorder()[0], viewWidth * 5 / 6, obtainSelectedAreaBorder()[0], mPaintBG);
                    canvas.drawLine(viewWidth * 1 / 6, obtainSelectedAreaBorder()[1], viewWidth * 5 / 6, obtainSelectedAreaBorder()[1], mPaintBG);
                } else {
                    canvas.drawLine(0, obtainSelectedAreaBorder()[0], viewWidth, obtainSelectedAreaBorder()[0], mPaintBG);
                    canvas.drawLine(0, obtainSelectedAreaBorder()[1], viewWidth, obtainSelectedAreaBorder()[1], mPaintBG);
                }
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        };


        super.setBackgroundDrawable(background);

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        refreshItemView(t);
        if (t > oldt) {
            Log.d(TAG, "向下滚动");
            scrollDirection = SCROLL_DIRECTION_DOWN;
        } else {
            Log.d(TAG, "向上滚动");
            scrollDirection = SCROLL_DIRECTION_UP;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        setBackgroundDrawable(null);
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            startScrollerTask();
        }
        return super.onTouchEvent(ev);
    }

    public void startScrollerTask() {
        initialY = getScrollY();
        this.postDelayed(scrollerTask, newCheck);
    }

    private int getViewMeasuredHeight(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }

    private int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 选中回调
     */
    private void onSeletedCallBack() {
        if (null != onWheelViewListener) {
            onWheelViewListener.onSelected(selectedIndex, items.get(selectedIndex));
        }
    }

    public OnWheelViewListener getOnWheelViewListener() {
        return onWheelViewListener;
    }

    public WheelView setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
        this.onWheelViewListener = onWheelViewListener;
        return this;
    }

    public static class OnWheelViewListener {
        public void onSelected(int selectedIndex, String item) {
        }
    }
}
