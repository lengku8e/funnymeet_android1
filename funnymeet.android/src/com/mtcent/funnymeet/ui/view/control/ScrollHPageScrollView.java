package com.mtcent.funnymeet.ui.view.control;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class ScrollHPageScrollView extends View {

	public ScrollHPageScrollView(Context context) {
		super(context);
		oncreate(context);
	}

	public ScrollHPageScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		oncreate(context);
	}

	public ScrollHPageScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		oncreate(context);
	}

	Activity mActivity;
	int viewCount;
	float position = 0;
	Paint paint = new Paint();

	public void setViewCount(int num) {
		viewCount = num;
	}

	public void setCurrentPosition(float p) {
		position = p;
		invalidate();
	}

	void oncreate(Context context) {
		mActivity = (Activity) context;
		// 设置颜色
		paint.setColor(0xff50b848);
		// 设置样式-填充
		paint.setStyle(Style.FILL);
		// 绘制一个矩形

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (viewCount > 0) {
			int w = getWidth() / viewCount;
			int h = getHeight();
			int x = 0;
			x = (int) (w * position);
			canvas.drawRect(new Rect(x, 0, x+w, h), paint);
		}
	}
}