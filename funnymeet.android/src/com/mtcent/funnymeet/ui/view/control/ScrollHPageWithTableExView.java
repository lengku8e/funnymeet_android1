package com.mtcent.funnymeet.ui.view.control;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mtcent.funnymeet.util.StrUtil;

import java.util.ArrayList;

public class ScrollHPageWithTableExView extends RelativeLayout implements
		SecondScrollHPageView.ScrollHPageACT {

	int unselected = Color.BLACK;
	String selectedColor = "#ff50b848";

	public static interface ScrollHPageWithTableAdapterEx {
		public int getPageCount();

		public View getTableView(int index);

		public View getPageView(int index);

		public void onPageScrolled(View pageList[], View tableList[], int left,
				float offsetProgress, int right);

		public void onPageChange(View pageList[], View tableList[], int index);

	}

	public ScrollHPageWithTableExView(Context context) {
		super(context);
		oncreate(context);
	}

	public ScrollHPageWithTableExView(Context context, AttributeSet attrs) {
		super(context, attrs);
		oncreate(context);
	}

	public ScrollHPageWithTableExView(Context context, AttributeSet attrs,
									  int defStyle) {
		super(context, attrs, defStyle);
		oncreate(context);
	}

	public void setSmoothScroll(boolean smoothScroll) {
		scrollHPageScrollView.setSmoothScroll(smoothScroll);
	}

	public Activity mActivity;
	public ArrayList<View> mListViews = new ArrayList<View>();
	ViewPager mViewPager;
	ScrollHPageWithTableAdapterEx mAdapter;
	LinearLayout mainLayout;
	RelativeLayout tableLayout;
	LinearLayout tableContextLayout;
	// View tableLine;
	// ScrollHPageScrollView tableScrollView;
	SecondScrollHPageView scrollHPageScrollView;

//	void addPic(String url) {
//		XVImageViewURL imageviewUrl = new XVImageViewURL(mActivity);
//		imageviewUrl.setImageUrl(url);
//		imageviewUrl.setScaleType(ScaleType.CENTER_CROP);
//		scrollHPageScrollView.addPageView(imageviewUrl);
//	}

	View tableList[];
	View pageList[];

	public void setScrollHPageWithTableAdapter(
			ScrollHPageWithTableAdapterEx adapter) {
		mAdapter = adapter;
		scrollHPageScrollView.setScrollHPageACT(this);

		if (mAdapter != null && mAdapter.getPageCount() > 0) {
			int count = mAdapter.getPageCount();
			tableList = new View[count];
			pageList = new View[count];
			tableContextLayout.removeAllViews();
			for (int i = 0; i < count; i++) {
				// page
				View v = mAdapter.getPageView(i);
				pageList[i] = v;
				FrameLayout layout = new FrameLayout(mActivity);
				if (v != null) {
					FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT);
					layout.addView(v, p);
				}
				scrollHPageScrollView.addPageView(layout);

				// table
				View tableView = mAdapter.getTableView(i);
				View tableViewLayout = createTable(tableView, i);
				tableContextLayout.addView(tableViewLayout);
				tableList[i] = tableView;

			}
		}
	}

	FrameLayout createTable(View tableView, final int index) {
		FrameLayout tableViewLayout = new FrameLayout(mActivity);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
				LayoutParams.MATCH_PARENT, 1);

		tableViewLayout.setLayoutParams(params);
		tableViewLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				scrollHPageScrollView.scrollIndex(index);
			}
		});
		tableViewLayout.addView(tableView);
		return tableViewLayout;
	}

	void oncreate(Context context) {
		mActivity = (Activity) context;
		createMainLayout();
		createTableLayout();
		createScrollHPageView();

		mainLayout.addView(scrollHPageScrollView);
		mainLayout.addView(tableLayout);
		this.addView(mainLayout);

	}

	LinearLayout createMainLayout() {
		mainLayout = new LinearLayout(mActivity);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mainLayout.setLayoutParams(params);
		mainLayout.setBackgroundColor(0xffffffff);
		return mainLayout;
	}

	RelativeLayout createTableLayout() {
		RelativeLayout.LayoutParams paramsRelative;
		// table
		tableLayout = new RelativeLayout(mActivity);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, StrUtil.dip2px(mActivity, 55));
		tableLayout.setLayoutParams(params);
		tableLayout.setBackgroundColor(0xfff6f8f8);

		// line
		View tableLine = new View(mActivity);
		paramsRelative = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 1);
		paramsRelative.addRule(RelativeLayout.ALIGN_PARENT_TOP,
				RelativeLayout.TRUE);
		tableLine.setLayoutParams(paramsRelative);
		tableLine.setBackgroundColor(0xffd9d9d9);
		tableLayout.addView(tableLine);

		// context
		tableContextLayout = new LinearLayout(mActivity);
		paramsRelative = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		paramsRelative.topMargin = 1;
		tableContextLayout.setLayoutParams(paramsRelative);
		tableContextLayout.setPadding(0, StrUtil.dip2px(3), 0,
				StrUtil.dip2px(2));

		tableLayout.addView(tableContextLayout);

		return tableLayout;
	}

	SecondScrollHPageView createScrollHPageView() {
		scrollHPageScrollView = new SecondScrollHPageView(mActivity);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 0, 1);
		scrollHPageScrollView.setLayoutParams(params);
		return scrollHPageScrollView;
	}

	public int getCurIndex() {
		return scrollHPageScrollView.getCurIndex();
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		if (mAdapter != null) {
			int newP = arg0 + 1;
			mAdapter.onPageScrolled(pageList, tableList, arg0, arg1, newP);
		}
	}

	@Override
	public void onPageChange(int index) {

		if (mAdapter != null) {
			mAdapter.onPageChange(pageList, tableList, index);
		}
	}
}