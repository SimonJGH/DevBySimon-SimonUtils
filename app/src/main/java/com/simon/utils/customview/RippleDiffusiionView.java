package com.simon.utils.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.simon.utils.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 圆环波纹扩散
 * <p>
 * attrs:
 * <declare-styleable name="RippleView">
 * <!--圆颜色-->
 * <attr name="wave_color" format="color"/>
 * <!--中心圆图片半径-->
 * <attr name="wave_coreImageRadius" format="integer"/>
 * <!--波浪圆之间间距,值越小越窄-->
 * <attr name="wave_width" format="integer"/>
 * </declare-styleable>
 * <p>
 * <p>
 * layout:
 * xmlns:app="http://schemas.android.com/apk/res-auto"
 * app:wave_color="@color/black"
 * app:wave_coreImageRadius="50"
 * app:wave_width="50"
 * <p>
 * activity:
 * mWaveView.setFill(false);
 * mWaveView.setColor(Color.WHITE);
 * mWaveView.start();
 */
@SuppressWarnings("all")
public class RippleDiffusiionView extends View {

    /**
     * 波浪圆圈颜色
     */
    private int mColor = Color.BLACK;
    /**
     * 第一个圆圈的半径(也就是圆形图片的半径)
     */
    private int mImageRadius = 50;
    /**
     * 波浪圆之间间距
     */
    private int mWidth = 5;
    /**
     * 最大宽度
     */
    private Integer mMaxRadius = 300;
    /**
     * 是否正在扩散中
     */
    private boolean mIsWave = false;
    // 透明度集合
    private List<Integer> mAlphas = new ArrayList<>();
    // 扩散圆半径集合
    private List<Integer> mRadius = new ArrayList<>();
    private Paint mPaint;
    //扩散的圆形是否是实心圆
    private boolean isFill = true;

    public RippleDiffusiionView(Context context) {
        this(context, null);
    }

    public RippleDiffusiionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleDiffusiionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RippleDiffusiionView, defStyleAttr, 0);
        mColor = a.getColor(R.styleable.RippleDiffusiionView_wave_color, mColor);
        mWidth = a.getInt(R.styleable.RippleDiffusiionView_wave_width, mWidth);
        mImageRadius = a.getInt(R.styleable.RippleDiffusiionView_wave_coreImageRadius, mImageRadius);
        a.recycle();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mAlphas.add(255);
        mRadius.add(0);

    }

    /**
     * 获取View的宽高在构造方法中拿不到的，getWidth()，getHeight()都会为零
     *
     * @param hasWindowFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        mMaxRadius = getWidth() > getHeight() ? getHeight() / 2 : getWidth() / 2;
        invalidate();
    }

    /**
     * 防止window是去焦点时，也就是应用在后台时，停止View的绘制
     */
    @Override
    public void invalidate() {
        if (hasWindowFocus()) {
            super.invalidate();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        // 绘制扩散圆
        mPaint.setColor(mColor);
        for (int i = 0; i < mAlphas.size(); i++) {
            // 设置透明度
            Integer alpha = mAlphas.get(i);
            mPaint.setAlpha(alpha);
            // 绘制波浪圆
            Integer radius = mRadius.get(i);
            if (isFill) {
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, mImageRadius + radius, mPaint);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(5);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, mImageRadius + radius, mPaint);
            } else {
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(5);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, mImageRadius + radius, mPaint);
            }

            if (alpha > 0 && mImageRadius + radius < mMaxRadius) {
                alpha = (int) (255.0F * (1.0F - (mImageRadius + radius) * 1.0f / mMaxRadius));
                mAlphas.set(i, alpha);
                mRadius.set(i, radius + 1);
            } else if (alpha < 0 && mImageRadius + radius > mMaxRadius) {
                // 当最外面那个圆达到了View的宽度时，移除，保证内存的回收
                mRadius.remove(i);
                mAlphas.remove(i);
            }

        }
        // 判断当波浪圆扩散到指定宽度时添加新扩散圆
        if (mRadius.get(mRadius.size() - 1) == mWidth) {
            addWave();
        }

        if (mIsWave) {
            invalidate();
        }
    }

    /**
     * 开始扩散
     */
    public void start() {
        mIsWave = true;
        invalidate();
    }

    /**
     * 停止扩散
     */
    public void stop() {
        mIsWave = false;
    }

    /**
     * 是否扩散中
     */
    public boolean isWave() {
        return mIsWave;
    }

    /**
     * 设置波浪圆颜色
     */
    public void setColor(int colorId) {
        mColor = colorId;
    }

    /**
     * 设置波浪圆之间间距
     */
    public void setWidth(int width) {
        mWidth = width;
    }

    /**
     * 设置中心圆半径
     */
    public void setMaxRadius(int maxRadius) {
        mMaxRadius = maxRadius;
    }

    /*
     * 最小中心圆半径
     */
    public void setImageRadius(int imageRadius) {
        mImageRadius = imageRadius;
    }

    /**
     * 是否填充
     *
     * @param fill
     */
    public void setFill(boolean fill) {
        isFill = fill;
    }

    public boolean isFill() {
        return isFill;
    }

    public void addWave() {
        mAlphas.add(255);
        mRadius.add(0);
    }
}