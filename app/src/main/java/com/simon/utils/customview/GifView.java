package com.simon.utils.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.simon.utils.R;

import java.io.InputStream;

/**
 * @author Simon
 * @Description 可以播放gif动画，直接当组件在布局中使用。
 * @date createTime: 2016-4-28
 */
public class GifView extends View {

	private Movie movie;
	private long mMovieStart;
	private float ratioWidth;
	private float ratioHeight;

	public GifView(Context context) {
		this(context, null);
	}

	public GifView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GifView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// 新api代码块
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		// 获取资源属性
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.GifViews);
		// 获取资源id
		int resourceId = ta.getResourceId(R.styleable.GifViews_src, -1);
		// 设置gif资源
		setGifResource(context, resourceId);
		// 资源回收
		ta.recycle();

	}

	/**
	 * 设置默认gif资源调用，在xml布局中src属性调用
	 */
	public void setGifResource(Context context, int resourceId) {
		if (resourceId == -1) {
			return;
		}
		// 获取gif动画资源
		InputStream is = context.getResources().openRawResource(resourceId);
		// 解码流转换为movie格式
		movie = Movie.decodeStream(is);
		// 重新布局
		requestLayout();

	}

	/**
	 * 设置Gif资源--流Stream
	 */
	public void setGifStream(InputStream is) {
		// 解码流转换为movie格式
		movie = Movie.decodeStream(is);
		// 重新布局
		requestLayout();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (movie != null) {
			int width = movie.width();
			int height = movie.height();
			if (width <= 0) {
				width = 1;
			}
			if (height <= 0) {
				height = 1;
			}
			int paddingLeft = getPaddingLeft();
			int paddingRight = getPaddingRight();
			int paddingTop = getPaddingTop();
			int paddingBottom = getPaddingBottom();

			width += paddingLeft + paddingRight;
			height += paddingTop + paddingBottom;

			width = Math.max(width, getSuggestedMinimumWidth());
			height = Math.max(height, getSuggestedMinimumHeight());

			int widthSize = resolveSizeAndState(width, widthMeasureSpec, 0);
			int heightSize = resolveSizeAndState(height, heightMeasureSpec, 0);

			ratioWidth = (float) widthSize / width;
			ratioHeight = (float) heightSize / height;

			setMeasuredDimension(widthSize, heightSize);

		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 查看上次时间
		long now = android.os.SystemClock.uptimeMillis();
		// 如果当前是第一次播放
		if (mMovieStart == 0) {
			mMovieStart = now;
		}
		if (movie != null) {
			// 获取movie时间长
			int duration = movie.duration();
			if (duration == 0) {
				duration = 1000;
			}
			int relTime = (int) ((now - mMovieStart) % duration);
			movie.setTime(relTime);

			// 最小缩放比率
			float scale = Math.min(ratioWidth, ratioHeight);
			canvas.scale(scale, scale);
			// 从 0 0 开始绘制
			movie.draw(canvas, 0, 0);
			// movie.draw(canvas, getWidth() - movie.width(),
			// getHeight() - movie.height());
			// 重新绘制
			invalidate();

		}
	}
}
