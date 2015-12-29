package com.mtcent.funnymeet.ui.view.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

@SuppressLint("NewApi")
public class InputChangeLinearLayout extends LinearLayout {

	public InputChangeLinearLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public InputChangeLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public InputChangeLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * 当前活动主窗口大小改变时调用
	 */
	int maxHeight=0;
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if(maxHeight<h){
			maxHeight = h;
		}
		if (null != listener) {
			listener.onKeyboardChange(h<maxHeight,w, h, oldw, oldh);
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * 
	 * Activity主窗口大小改变时的回调接口(本示例中，等价于软键盘显示隐藏时的回调接口)
	 * 
	 * @author mo
	 * 
	 * 
	 */

	public static interface KeyboardChangeListener {

		public void onKeyboardChange(boolean isShow,int w, int h, int oldw, int oldh);

	}

	KeyboardChangeListener listener;

	public void setOnKeyboardChangeListener(KeyboardChangeListener listener) {

		this.listener = listener;

	}
}
