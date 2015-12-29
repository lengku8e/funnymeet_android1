package com.mtcent.funnymeet.ui.view.control;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.view.control.ScrollHPageView.ScrollHPageACT;
import com.mtcent.funnymeet.util.StrUtil;

import java.util.ArrayList;

public class ScrollHPageWithTableView extends RelativeLayout implements
		ScrollHPageACT {

	int unselected = Color.BLACK;
	String selectedColor = "#ff50b848";

	public interface ScrollHPageWithTableAdapter {
		public int getPageCount();

		public String getTableString(int index);

		public View getPageView(int index);

		public void onPageChange(int index);
	}

	public ScrollHPageWithTableView(Context context) {
		super(context);
		oncreate(context);
	}

	public ScrollHPageWithTableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		oncreate(context);
	}

	public ScrollHPageWithTableView(Context context, AttributeSet attrs,
									int defStyle) {
		super(context, attrs, defStyle);
		oncreate(context);
	}

	public Activity mActivity;
	public ArrayList<View> mListViews = new ArrayList<View>();
	ViewPager mViewPager;
	ScrollHPageWithTableAdapter mAdapter;
	LinearLayout mainLayout;
	RelativeLayout tableLayout;
	LinearLayout tableContextLayout;
	View tableLine;
	ScrollHPageScrollView tableScrollView;
	ScrollHPageView scrollHPageScrollView;

	void addPic(String url) {
		XVURLImageView imageviewUrl = new XVURLImageView(mActivity);
		imageviewUrl.setImageUrl(url);
		imageviewUrl.setScaleType(ScaleType.CENTER_CROP);
		scrollHPageScrollView.addPageView(imageviewUrl);
	}

	public void setTableContextLayoutBackColor(int color) {
		tableContextLayout.setBackgroundColor(color);
	}

	TextView tableList[];

	public void setScrollHPageWithTableAdapter(
			ScrollHPageWithTableAdapter adapter) {
		mAdapter = adapter;
		scrollHPageScrollView.setScrollHPageACT(this);

		if (mAdapter != null && mAdapter.getPageCount() > 0) {
			int count = mAdapter.getPageCount();
			tableScrollView.setViewCount(count);
			tableList = new TextView[count];
			tableContextLayout.removeAllViews();
			for (int i = 0; i < count; i++) {
				// page
				View v = mAdapter.getPageView(i);
				FrameLayout layout = new FrameLayout(mActivity);
				if (v != null) {
					FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT);
					layout.addView(v, p);
				}
				scrollHPageScrollView.addPageView(layout);

				// table
				String table = mAdapter.getTableString(i);
				TextView tableView = createTable(table, i);
				tableView.setTextColor(unselected);
				tableContextLayout.addView(tableView);
				tableList[i] = tableView;
			}
		}
	}

	TextView createTable(String table, final int index) {
		TextView tableView = new TextView(mActivity);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
				LayoutParams.MATCH_PARENT, 1);
		tableView.setText(table);
		tableView.setTextSize(16);
		tableView.setTextColor(unselected);
		tableView.setGravity(Gravity.CENTER);
		tableView.setLayoutParams(params);
		tableView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				scrollHPageScrollView.scrollIndex(index);
			}
		});
		return tableView;
	}

	void oncreate(Context context) {
		mActivity = (Activity) context;
		createMainLayout();
		createTableLayout();
		createScrollHPageView();

		mainLayout.addView(tableLayout);
		mainLayout.addView(scrollHPageScrollView);
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
				LayoutParams.MATCH_PARENT, StrUtil.dip2px(mActivity, 38));
		tableLayout.setLayoutParams(params);

		// context
		tableContextLayout = new LinearLayout(mActivity);
		paramsRelative = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		tableContextLayout.setLayoutParams(paramsRelative);
		tableLayout.addView(tableContextLayout);

		// line
		tableLine = new View(mActivity);
		paramsRelative = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, StrUtil.dip2px(mActivity, 1));
		paramsRelative.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
				RelativeLayout.TRUE);
		tableLine.setLayoutParams(paramsRelative);
		tableLine.setBackgroundColor(0xffd9d9d9);
		tableLayout.addView(tableLine);

		// tableScroll
		tableScrollView = new ScrollHPageScrollView(mActivity);
		paramsRelative = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, StrUtil.dip2px(mActivity, 3));
		paramsRelative.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
				RelativeLayout.TRUE);
		tableScrollView.setLayoutParams(paramsRelative);
		// tableScrollView.setBackgroundColor(0xffd9d9d9);
		tableLayout.addView(tableScrollView);

		return tableLayout;
	}

	ScrollHPageView createScrollHPageView() {
		scrollHPageScrollView = new ScrollHPageView(mActivity);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		scrollHPageScrollView.setLayoutParams(params);
		return scrollHPageScrollView;
	}

	public int getCurIndex() {
		return 0;
	}

	void clearAllMenuBarFouchState() {
		for (TextView tablev : tableList) {
			tablev.setTextColor(unselected);
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		tableScrollView.setCurrentPosition(arg0 + arg1);
	}

	@Override
	public void onPageChange(int index) {
		if (tableList != null && index < tableList.length) {
			clearAllMenuBarFouchState();
			tableList[index].setTextColor(Color.parseColor(selectedColor));
		}
		if (mAdapter != null) {
			mAdapter.onPageChange(index);
		}
	}
	
	public TextView[] getTableList() {
		return this.tableList;
	}
}