package com.simon.utils.customlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.simon.utils.R;


/**
 * Created by Administrator on 2016/7/31 0031. 百分比布局
 */
public class PercentLayout extends RelativeLayout {
	/*
	 * 对于自定义UI而言，构造函数是必然存在的，至于属性的设置可以声明在attr文件中也可以以setXX（）的形式存在。如使用attr形式2、3构造函数必不可少
	 * ，如以setXX（）形式，随意了。。。
	 */
	public PercentLayout(Context context) {
		super(context);
	}

	public PercentLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PercentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 测量出子控件的宽高重新绘制
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		// 获取子控件的数量
		int childCount = getChildCount();
		// 遍历每一个子控件 获取layoutparams属性
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			ViewGroup.LayoutParams childLayoutParams = child.getLayoutParams();
			float percentWidth = 0;
			float percentHeight = 0;
			// 判断是否含有该属性
			if (childLayoutParams instanceof LayoutParams) {
				// 获取 宽和高的百分比
				percentWidth = ((PercentLayoutParams) childLayoutParams)
						.getPercentWidth();
				percentHeight = ((PercentLayoutParams) childLayoutParams)
						.getPercentHeight();
			}
			if (percentWidth != 0) {
				// 如果百分比宽度不为0 计算子控件宽度
				childLayoutParams.width = (int) (width * percentWidth);
			}
			if (percentHeight != 0) {
				// 如果百分比高度不为0 计算子控件高度
				childLayoutParams.height = (int) (height * percentHeight);
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/*
	 * 生成新的LayoutParams
	 */
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new PercentLayoutParams(getContext(), attrs);
	}

	/*
	 * 因为是自定义Layout，所以必须要有LayoutParams属性
	 */
	public static class PercentLayoutParams extends LayoutParams {
		private float percentWidth;
		private float percentHeight;

		public float getPercentWidth() {
			return percentWidth;
		}

		public void setPercentWidth(float percentWidth) {
			this.percentWidth = percentWidth;
		}

		public float getPercentHeight() {
			return percentHeight;
		}

		public void setPercentHeight(float percentHeight) {
			this.percentHeight = percentHeight;
		}

		public PercentLayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
			TypedArray typedArray = c.obtainStyledAttributes(attrs,
					R.styleable.percent_layout);
			percentWidth = typedArray.getFloat(
					R.styleable.percent_layout_percent_width, percentWidth);
			percentHeight = typedArray.getFloat(
					R.styleable.percent_layout_percent_height, percentHeight);
			typedArray.recycle();
		}

		public PercentLayoutParams(int w, int h) {
			super(w, h);
		}

		public PercentLayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}

		public PercentLayoutParams(MarginLayoutParams source) {
			super(source);
		}

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

}
