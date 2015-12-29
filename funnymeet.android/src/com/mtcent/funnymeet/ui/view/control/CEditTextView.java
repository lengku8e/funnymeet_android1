package com.mtcent.funnymeet.ui.view.control;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class CEditTextView extends EditText {

	public CEditTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CEditTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CEditTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static interface onRectChangeListener {
		void onChage();
	}

	onRectChangeListener rectChangeListener;

	public void setRectChangeListener(onRectChangeListener rectChangeListener) {
		this.rectChangeListener = rectChangeListener;
	}

	int[] xywh = new int[4];

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);

		int x = xywh[0];
		int y = xywh[1];
		int w = xywh[2];
		int h = xywh[3];
		this.getLocationInWindow(xywh);

		if (rectChangeListener != null
				&& (x != xywh[0] || y != xywh[1] || w != xywh[2] || h != xywh[3])) {
			rectChangeListener.onChage();
		}
	}

}
