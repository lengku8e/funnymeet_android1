package com.mtcent.funnymeet.ui.view.control;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class ScrollHPageView extends RelativeLayout {
	public interface ScrollHPageACT {
		void onPageScrolled(int arg0, float arg1, int arg2);

		void onPageChange(int index);
	}

	public ScrollHPageView(Context context) {
		super(context);
		oncreate(context);
	}

	public ScrollHPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		oncreate(context);
	}

	public ScrollHPageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		oncreate(context);
	}

	public Activity mActivity;
	public ArrayList<View> mListViews = new ArrayList<View>();
	ViewPager mViewPager;
	ScrollHPageACT mAct;

	public void setScrollHPageACT(ScrollHPageACT act) {
		mAct = act;
	}



	void oncreate(Context context) {

		mActivity = (Activity) context;
		mViewPager = new ViewPager(mActivity);
		mViewPager.setAdapter(new MyPagerAdapter());
		RelativeLayout.LayoutParams paramViewPage = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ScrollHPageView.this.addView(mViewPager, paramViewPage);

		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int i) {
				if (mAct != null) {
					mAct.onPageChange(i);
				}
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (mAct != null) {
					mAct.onPageScrolled(arg0, arg1, arg2);
				}
			}

			public void onPageScrollStateChanged(int arg0) {

			}
		});

		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 20);
		param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		param.bottomMargin = 5;

	}

	public void addPageView(View v) {
		mListViews.add(v);
		mViewPager.getAdapter().notifyDataSetChanged();
	}

	public int getCurIndex() {
		return mViewPager.getCurrentItem();
	}

	public void scrollPage(View v) {
		for (int index = 0; index < mListViews.size(); index++) {
			View view = mListViews.get(index);
			if (view == v) {
				scrollIndex(index);
				break;
			}
		}
	}

	public void scrollIndex(final int index) {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				int count = mListViews.size();
				int i = index;
				if (i >= count) {
					i = 0;
				}
				mViewPager.setCurrentItem(i);
			}
		});

	}

	private class MyPagerAdapter extends PagerAdapter {

		public void finishUpdate(View arg0) {
		}

		public int getCount() {
			if (mListViews != null) {
				return mListViews.size();
			}
			return 0;
		}

		public Object instantiateItem(ViewGroup view, int i) {
			// int index = i % mListViews.size();
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