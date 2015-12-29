package com.mtcent.funnymeet.ui.view.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ScrollHPageWithTableExExView extends ScrollHPageWithTableExView implements
		SecondScrollHPageView.ScrollHPageACT, ScrollHPageWithTableExView.ScrollHPageWithTableAdapterEx {

	private View normalIconView[];
	//private View selectIconView[];
	
	public ScrollHPageWithTableExExView(Context context) {
		super(context);
	}

	public ScrollHPageWithTableExExView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollHPageWithTableExExView(Context context, AttributeSet attrs,
										int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	void oncreate(Context context) {
		super.oncreate(context);
		setSmoothScroll(false);
	}

	public View[] getNormalIconView() {
		return this.normalIconView;
	}
	
	
	public static interface ScrollHPageWithTableAdapterExEx {
		public int getPageCount();

		public String getTableTitle(int index);

		public int getTableIconSelect(int index);

		public int getTableIconNormal(int index);

		public int getTableColorSelect(int index);

		public int getTableColorNormal(int index);

		public View getPageView(int index);

		public void onPageChange(int index);

		public FunnymeetBaseView getClassBaseView(int index);
		
		public View getNormalIconView(int index);
		
	}

	ScrollHPageWithTableAdapterExEx adapterExEx;

	public void setScrollHPageWithTableAdapterExEx(
			ScrollHPageWithTableAdapterExEx adapter) {
		adapterExEx = adapter;
		this.normalIconView = new View[adapterExEx.getPageCount()];
		
		setScrollHPageWithTableAdapter(this);
	}

	@Override
	public int getPageCount() {
		if (adapterExEx != null) {
			return adapterExEx.getPageCount();
		}
		return 0;
	}

	@Override
	public View getPageView(int index) {
		if (adapterExEx != null) {
			return adapterExEx.getPageView(index);
		}
		return null;
	}

	View createTableView(String title, int textColor, int iconId, int index, boolean isNormal) {
		if (adapterExEx != null) {
			LinearLayout tableViewLayout = new LinearLayout(mActivity);
			ViewGroup.LayoutParams paramsRelative = new ViewGroup.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			tableViewLayout.setLayoutParams(paramsRelative);
			tableViewLayout.setOrientation(LinearLayout.VERTICAL);
			// 图标
			ImageView icon = new ImageView(mActivity);
			icon.setScaleType(ScaleType.CENTER_INSIDE);
			icon.setPadding(5, 5, 5, 5);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 0, 1);
			icon.setLayoutParams(params);
			icon.setImageResource(iconId);

			// 文字
			TextView tv = new TextView(mActivity);
			tv.setText(title);
			params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			// params.topMargin = StrUtil.dip2px(3);
			tv.setTextSize(10);
			tv.setTextColor(textColor);
			tv.setGravity(Gravity.CENTER);
			tv.setLayoutParams(params);

			tableViewLayout.addView(icon);
			if (isNormal) {
				this.normalIconView[index] = tableViewLayout;
			}
			tableViewLayout.addView(tv);

			return tableViewLayout;
		}
		return null;
	}

	@Override
	public View getTableView(int index) {
		if (adapterExEx != null) {
			RelativeLayout tableLayout = new RelativeLayout(mActivity);

			String title = adapterExEx.getTableTitle(index);
			View normal = createTableView(title,
					adapterExEx.getTableColorNormal(index),
					adapterExEx.getTableIconNormal(index), index, true);
			View select = createTableView(title,
					adapterExEx.getTableColorSelect(index),
					adapterExEx.getTableIconSelect(index), index, false);

			tableLayout.addView(normal);

			tableLayout.addView(select);
			tableLayout.setTag(select);

			return tableLayout;
		}
		return null;
	}

	@Override
	public void onPageScrolled(View pageList[], View[] tableList, int left,
			float offsetProgress, int right) {
		for (int i = 0; i < tableList.length; i ++) {
			normalIconView[i].setVisibility(VISIBLE);
		}
		if (offsetProgress < 0.05) {
			offsetProgress = 0;
		}
		if (offsetProgress > 0.95) {
			offsetProgress = 1;
		}
		View leftSelectPage = pageList[left];
		View rightSelectPage = null;
		if (right < pageList.length) {
			rightSelectPage = pageList[right];
		}
		if (leftSelectPage != null) {
			leftSelectPage.setAlpha(1 - offsetProgress);
		}
		if (rightSelectPage != null) {
			rightSelectPage.setAlpha(offsetProgress);
		}
		for (View v : pageList) {
			if (v != null && v != leftSelectPage && v != rightSelectPage) {
				v.setAlpha(0);
			}
		}

		for (View v : tableList) {
			((View) v.getTag()).setAlpha(0);
			//((View) v.getTag()).setVisibility(View.GONE);
		}
		View leftSelect = (View) tableList[left].getTag();
		leftSelect.setAlpha(1 - offsetProgress);
		if (right < tableList.length) {
			View rightSelect = (View) tableList[right].getTag();
			rightSelect.setAlpha(offsetProgress);
		}
		if (offsetProgress == 0) {
			normalIconView[left].setVisibility(GONE);
		} else {
			normalIconView[right].setVisibility(GONE);
		}
	}

	@Override
	public void onPageChange(View pageList[], View[] tableList, int index) {
		if (adapterExEx != null) {
			adapterExEx.onPageChange(index);
		}
	}

}