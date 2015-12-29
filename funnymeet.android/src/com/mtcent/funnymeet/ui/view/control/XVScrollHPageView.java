package com.mtcent.funnymeet.ui.view.control;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class XVScrollHPageView extends RelativeLayout {

	public XVScrollHPageView(Context context) {
		super(context);
		oncreate(context);
	}

	public XVScrollHPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		oncreate(context);
	}

	public XVScrollHPageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		oncreate(context);
	}

	public Activity mActivity;
	public ArrayList<View> mListViews = new ArrayList<View>();
	public int mScrollTime = 0;
	public Timer timer;
	LinearLayout ovalLayout;
	ViewPager mViewPager;

	public void onPageChange(int index) {

	}

	public void oncreate(Context context) {

		mActivity = (Activity) context;
		mViewPager = new ViewPager(mActivity);
		mViewPager.setAdapter(new MyPagerAdapter());
		RelativeLayout.LayoutParams paramViewPage = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		XVScrollHPageView.this.addView(mViewPager, paramViewPage);
		mViewPager.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				stopTimer();
				return false;
			}
		});
		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int i) {
				int count = ovalLayout.getChildCount();
				int curi = mViewPager.getCurrentItem();
				for (int index = 0; index < count; index++) {
					View oval = ovalLayout.getChildAt(index);
					Drawable drawable = oval.getBackground();
					if (drawable != null && drawable instanceof ShapeDrawable) {

						if (index == curi) {
							((ShapeDrawable) drawable).getPaint().setColor(
									Color.WHITE);
						} else {
							((ShapeDrawable) drawable).getPaint().setColor(
									0xaa333333);
						}
						oval.postInvalidate();
					}
				}

			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			public void onPageScrollStateChanged(int arg0) {

			}
		});

		ovalLayout = new LinearLayout(mActivity);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 20);
		param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		param.bottomMargin = 5;
		ovalLayout.setGravity(Gravity.CENTER);
		ovalLayout.setOrientation(LinearLayout.HORIZONTAL);
		XVScrollHPageView.this.addView(ovalLayout, param);

		setScrollTime(2500);
	}

	public void showOvalLayout() {
		ovalLayout.setVisibility(View.VISIBLE);
	}

	public void hideOvalLayout() {
		ovalLayout.setVisibility(View.GONE);
	}

	public void setScrollTime(int t) {
		mScrollTime = t;
		if (mScrollTime > 0) {
			startTimer();
		} else {
			stopTimer();
		}
	}

	public void addPageView(View v) {
		mListViews.add(v);
		OvalLayoutadd();
		mViewPager.getAdapter().notifyDataSetChanged();
	}

	private void OvalLayoutadd() {
		ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
		drawable.setBounds(0, 0, 15, 15);

		View oval = new View(mActivity);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15, 15);
		params.leftMargin = 5;
		params.rightMargin = 5;
		int curi = mViewPager.getCurrentItem();
		if (mListViews.size() == (curi + 1)) {
			drawable.getPaint().setColor(Color.WHITE);
		} else {
			drawable.getPaint().setColor(0xaa333333);
		}
		oval.setBackgroundDrawable(drawable);
		ovalLayout.addView(oval, params);
	}

	public int getCurIndex() {
		return mViewPager.getCurrentItem();
	}

	public void scrollIndex(final int index) {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				stopTimer();
				int count = mListViews.size();
				int i = index;
				if (i >= count) {
					i = 0;
				}
				mViewPager.setCurrentItem(i);
				stopTimer();
			}
		});

	}

	void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

	}

	public void startTimer() {
		stopTimer();
		if (mScrollTime > 0 && timer ==null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					mActivity.runOnUiThread(new Runnable() {
						public void run() {
							int count = mListViews.size();
							int index = mViewPager.getCurrentItem() + 1;
							if (index >= count) {
								index = 0;
							}
							mViewPager.setCurrentItem(index);
							stopTimer();

						}
					});
				}
			}, mScrollTime);
		}
	}

	private class MyPagerAdapter extends PagerAdapter {

		public void finishUpdate(View arg0) {
			startTimer();
			onPageChange(mViewPager.getCurrentItem());
		}

		public int getCount() {
			if (mListViews != null) {
				return mListViews.size();
			}
			return 0;
		}

		public Object instantiateItem(ViewGroup view, int i) {

			// if (view.getChildCount() == mListViews.size())
			// {
			// view.removeView(mListViews.get(i % mListViews.size()));
			// }
			// view.addView(mListViews.get(i % mListViews.size()), 0);
			//
			// Log.d("instantiateItem", i+"");
			//
			// return mListViews.get(i % mListViews.size());

			int index = i % mListViews.size();
			view.addView(mListViews.get(i));
			return mListViews.get(i);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		public Parcelable saveState() {
			return null;
		}

		public void startUpdate(View arg0) {

		}

		public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {

			arg0.removeView(mListViews.get(arg1));
		}
	}
}
