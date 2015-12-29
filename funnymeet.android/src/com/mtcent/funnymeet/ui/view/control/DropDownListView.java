package com.mtcent.funnymeet.ui.view.control;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.mtcent.funnymeet.util.StrUtil;

//implements OnScrollListener
public class DropDownListView extends ListView {
	private Context context = null;
	View headerView = null;
	View footerView = null;
	boolean isWait = false;
	public static final int initView = 0;// 初始化
	public static final int showwingView = 1;// 正显示
	public static final int showView = 2;// 下拉完全显示
	public static final int waitView = 3;// 更新等待中
	public static final int waitHideView = 4;// 更新完毕，等待消失
	public static final int hidingView = 5;// 消失中
	long beginHeaderWaitTime = 0;
	long headerWaitKeepTime = 0;
	int headerViewState = initView;

	long beginFooterWaitTime = 0;
	long footerWaitKeepTime = 0;
	int footerViewState = initView;

	int headerViewH = 0;
	int headerViewLatoutH = 0;
	int footerViewH = 0;
	int footerViewLayoutH = 0;
	int speed = 15;

	boolean loadMoreAble = true;
	//
	DropViewAdaper mDropViewAdaper = null;

	private LinearLayout headerLayout = null;
	// LinearLayout headerViewLayout = null;

	private LinearLayout footerLayout = null;

	// RelativeLayout footerViewLayout = null;

	public DropDownListView(Context context) {
		super(context);
		init(context);
	}

	public DropDownListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DropDownListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		speed = StrUtil.dip2px(context, 15);
		// should set, to run onScroll method and so on
		// super.setOnScrollListener(this);
	}

	protected interface DropViewAdaper {
		public View doHeaderView(DropDownListView thiz, View convertView,
				int state);

		public View doFooterView(DropDownListView thiz, View convertView,
				int state);

	}

	// 更新完毕，等待消失
	public void stopHeaderWait(final long waitTime) {
		post(new Runnable() {
			@Override
			public void run() {
				if (headerViewState == waitView) {
					beginHeaderWaitTime = System.currentTimeMillis();
					headerWaitKeepTime = waitTime;
					headerViewState = waitHideView;
					mDropViewAdaper.doHeaderView(DropDownListView.this,
							headerView, headerViewState);
					postInvalidate();
				}
			}
		});
	}

	// 更新完毕，等待消失
	public void stopFooterWait(final long waitTime) {
		post(new Runnable() {
			@Override
			public void run() {
				if (footerViewState == waitView) {
					beginFooterWaitTime = System.currentTimeMillis();
					footerWaitKeepTime = waitTime;
					footerViewState = waitHideView;
					mDropViewAdaper.doFooterView(DropDownListView.this,
							footerView, footerViewState);
					postInvalidate();
				}
			}
		});
	}

	public void setDropViewAdaper(DropViewAdaper dropViewAdaper) {
		mDropViewAdaper = dropViewAdaper;
		headerViewState = initView;
		headerView = mDropViewAdaper.doHeaderView(this, headerView,
				headerViewState);
		footerView = mDropViewAdaper.doFooterView(this, footerView, initView);
		initDropDownStyle();
		initOnBottomStyle();
	}

	public void setLoadMoreAble(boolean able) {
		loadMoreAble = able;
	}

	private void initDropDownStyle() {

		if (headerLayout == null) {
			headerLayout = new LinearLayout(context);
			if (headerView != null) {
				headerLayout.addView(headerView);
				measureHeaderLayout(headerLayout);
				headerViewH = headerLayout.getMeasuredHeight();
				headerLayout.setPadding(0, -headerViewH, 0, 0);
			}
		}
		addHeaderView(headerLayout);
	}

	int getHeaderHeight() {
		int padTop = headerLayout.getPaddingTop();
		return headerViewH + padTop;
	}

	void setHeaderHeight(int h) {
		headerLayout.setPadding(0, h - headerViewH, 0, 0);

	}

	int getFooterHeight() {
		int padBottom = footerLayout.getPaddingBottom();
		return footerViewH + padBottom;
	}

	void setFooterHeight(int h) {
		footerLayout.setPadding(0, 0, 0, h - footerViewH);
	}

	/**
	 * initViewControl on bottom style, only initViewControl once
	 */
	private void initOnBottomStyle() {

		if (footerLayout == null) {
			footerLayout = new LinearLayout(context);
		}

		if (footerView != null) {
			footerLayout.addView(footerView);
			measureHeaderLayout(footerView);
			footerViewH = footerView.getMeasuredHeight();
			footerLayout.setPadding(0, 0, 0, -footerViewH);
		}
		addFooterView(footerLayout);
	}

	float actionDownPointY = -1;
	float actionDownPointY2 = -1;
	float lastY = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			actionDownPointY = event.getY();
			actionDownPointY2 = event.getY();
			initHeaderViewParameter();
			initFooterViewParameter();
			break;
		case MotionEvent.ACTION_MOVE:// 不会多点ACTION_DOWN
			boolean ret1 = changeHeaderView(event);
			boolean ret2 = changeFooterView(event);
			if (ret1 || ret2) {
				//super.onTouchEvent(event);
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			resetHeaderView();
			resetFooterView();
			actionDownPointY = -1;
			actionDownPointY2 = -1;
			break;
		}

		return super.onTouchEvent(event);
	}

	// 如果向上推动
	boolean changeHeaderView(MotionEvent event) {
		if (headerView != null) {
			int dy = (int) (event.getY() - actionDownPointY);
			int firstIndex = getFirstVisiblePosition();
			if (firstIndex == 0) {
				if (headerViewLatoutH + dy >= 0) {
					float height = headerViewLatoutH + dy;
					float maxHeight = 4*headerViewH ;
					float stepMaxHeight = getHeight();//960f ;
					
					height = maxHeight*(float)Math.sin(Math.toRadians(90*height/stepMaxHeight));
					
					setHeaderHeight((int)height);
				} else {
					setHeaderHeight(0);
				}
				int currHeaderHeight = getHeaderHeight();
				// 下拉中
				if (currHeaderHeight > 0 && currHeaderHeight < headerViewH) {
					if (headerViewState == initView
							|| headerViewState == showView) {
						headerViewState = showwingView;
						mDropViewAdaper.doHeaderView(this, headerView,
								headerViewState);
					}
				}

				// 松手刷新
				if (currHeaderHeight >= headerViewH) {
					if (headerViewState == showwingView
							|| headerViewState == initView) {
						headerViewState = showView;
						mDropViewAdaper.doHeaderView(this, headerView,
								headerViewState);
					}
				}

				// 刷新中
				if (currHeaderHeight < headerViewH) {
					if (headerViewState == waitView
							|| waitHideView == headerViewState) {
						setHeaderHeight(headerViewH);
					}
				}

				if (currHeaderHeight >= headerViewH
						&& (headerViewState != waitView || waitHideView != headerViewState)) {
					return true;
				}
			} else {
				if (headerViewState != waitView
						|| waitHideView != headerViewState) {
					actionDownPointY = event.getY();
				}
			}
		}
		return false;
	}

	void resetHeaderView() {
		if (headerView != null) {
			if (headerViewState == showView) {
				// params.topMargin = 0;
				// headerView.setLayoutParams(params);
			} else if (headerViewState == waitView) {
				// params.topMargin = 0;
				// headerView.setLayoutParams(params);
			} else if (headerViewState == showwingView) {
				headerViewState = hidingView;
			}
		}
	}

	void initHeaderViewParameter() {
		if (headerView != null) {
			measureHeaderLayout(headerView);
			headerViewH = headerView.getHeight();
			headerViewLatoutH = getHeaderHeight();
		}
	}

	void initFooterViewParameter() {
		if (footerView != null) {
			measureHeaderLayout(footerView);
			footerViewH = footerView.getMeasuredHeight();
			footerViewLayoutH = getFooterHeight();
		}
	}

	void resetFooterView() {
		if (footerView != null) {
			if (footerViewState == showView) {
				// params.topMargin = 0;
				// headerView.setLayoutParams(params);
			} else if (footerViewState == waitView) {
				// params.topMargin = 0;
				// headerView.setLayoutParams(params);
			} else if (footerViewState == showwingView) {
				footerViewState = hidingView;
			}
		}
	}

	boolean changeFooterView(MotionEvent event) {
		if (footerView != null && loadMoreAble) {
			int curFooterHeight = getFooterHeight();
			int lastIndex = getLastVisiblePosition();
			int count = getCount();

			if (lastIndex == count - 1) {
				int dy = (int) (actionDownPointY2 - event.getY());
				if (footerViewLayoutH + dy > footerViewH) {
					curFooterHeight = footerViewH;
				} else if (footerViewLayoutH + dy > 0) {
					curFooterHeight = footerViewLayoutH + dy;
				} else {
					curFooterHeight = 0;
				}
				setFooterHeight(curFooterHeight);

				// 上拉中
				if (curFooterHeight > 0 && curFooterHeight < footerViewH) {
					if (footerViewState == initView
							|| footerViewState == showView) {
						footerViewState = showwingView;
						mDropViewAdaper.doFooterView(this, footerView,
								footerViewState);
					}
				}

				// 松手加载
				if (curFooterHeight >= footerViewH) {
					if (footerViewState == showwingView
							|| footerViewState == initView) {
						footerViewState = showView;
						mDropViewAdaper.doFooterView(this, footerView,
								footerViewState);
					}
				}

				// 刷新中
				if (curFooterHeight < footerViewH) {
					if (footerViewState == waitView
							|| waitHideView == footerViewState) {
						curFooterHeight = footerViewH;
						setFooterHeight(curFooterHeight);
					}
				}

//				 if (curFooterHeight > 0 && footerViewState != waitView) {
//					 return true;
//				 }

			} else {
				if (footerViewState != waitView
						|| waitHideView != footerViewState) {
					actionDownPointY2 = event.getY();
				}
			}

		}
		return false;
	}

	public boolean isHeadHide() {

		return (this.getFirstVisiblePosition() > 0);

	}

	void onHeaderAnimation(int speed) {
		if (headerView != null) {
			int currHeaderHeight = getHeaderHeight();

			if (headerViewState == waitView || headerViewState == showView) {
				int dy = (int) Math.abs((float) currHeaderHeight * 0.15);
				dy = (dy > speed) ? dy : speed;
				if (currHeaderHeight > headerViewH) {
					currHeaderHeight -= dy;
					if (currHeaderHeight < headerViewH) {
						currHeaderHeight = headerViewH;
					}
				} else if (currHeaderHeight < headerViewH) {
					currHeaderHeight += dy;
					if (currHeaderHeight > headerViewH) {
						currHeaderHeight = headerViewH;
					}
				}
				setHeaderHeight(currHeaderHeight);
				if (currHeaderHeight != headerViewH) {
					postInvalidate();
				} else if (headerViewState == showView) {
					headerViewState = waitView;
					mDropViewAdaper.doHeaderView(this, headerView,
							headerViewState);
					postInvalidate();
				}
			}
			if (headerViewState == waitHideView) {

				if (currHeaderHeight > headerViewH) {
					int dy = (int) Math.abs((float) currHeaderHeight * 0.15);
					dy = (dy > speed) ? dy : speed;
					currHeaderHeight -= dy;
					if (currHeaderHeight < headerViewH) {
						currHeaderHeight = headerViewH;
					}
					setHeaderHeight(currHeaderHeight);
				}

				long now = System.currentTimeMillis();
				if (now > beginHeaderWaitTime + headerWaitKeepTime) {
					headerViewState = hidingView;
					mDropViewAdaper.doHeaderView(this, headerView,
							headerViewState);
				}
				postInvalidate();
			}

			if (headerViewState == hidingView) {
				int dy = (int) Math.abs((float) currHeaderHeight * 0.15);
				dy = (dy > speed) ? dy : speed;
				if (currHeaderHeight > 0) {
					currHeaderHeight -= dy;
				}
				if (currHeaderHeight < 0) {
					currHeaderHeight = 0;
				}
				setHeaderHeight(currHeaderHeight);
				if (currHeaderHeight != 0) {
					postInvalidate();
				} else {
					headerViewState = initView;
					postInvalidate();
				}
			}
			if (headerViewState == initView && currHeaderHeight != 0) {
				currHeaderHeight = 0;
				setHeaderHeight(currHeaderHeight);
			}
		}
	}

	void onFooterAnimation(int speed) {
		if (footerView != null) {
			int curFooterHeight = getFooterHeight();
			if (footerViewState == waitView || footerViewState == showView) {
				int dh = (int) Math.abs((float) curFooterHeight * 0.15);
				dh = (dh > speed) ? dh : speed;

				if (curFooterHeight > footerViewH) {
					curFooterHeight -= dh;
					if (curFooterHeight < footerViewH) {
						curFooterHeight = footerViewH;
					}
				} else if (curFooterHeight < footerViewH) {
					curFooterHeight += dh;
					if (curFooterHeight > footerViewH) {
						curFooterHeight = footerViewH;
					}
				}
				setFooterHeight(curFooterHeight);
				if (curFooterHeight != footerViewH) {
					postInvalidate();
				} else if (footerViewState == showView) {
					footerViewState = waitView;
					mDropViewAdaper.doFooterView(this, footerView,
							footerViewState);
					postInvalidate();
				}
			}
			if (footerViewState == waitHideView) {
				if (curFooterHeight > footerViewH) {
					int dh = (int) Math.abs((float) curFooterHeight * 0.15);
					dh = (dh > speed) ? dh : speed;
					curFooterHeight -= dh;
					if (curFooterHeight < footerViewH) {
						curFooterHeight = footerViewH;
					}
					setFooterHeight(curFooterHeight);
				}

				long now = System.currentTimeMillis();
				if (now > beginFooterWaitTime + footerWaitKeepTime) {
					footerViewState = hidingView;
					mDropViewAdaper.doFooterView(this, footerView,
							footerViewState);
				}
				postInvalidate();
			}

			if (footerViewState == hidingView) {
				if (curFooterHeight > 0) {
					int dh = (int) Math.abs((float) curFooterHeight * 0.15);
					dh = (dh > speed) ? dh : speed;
					curFooterHeight -= dh;
				}
				if (curFooterHeight < 0) {
					curFooterHeight = 0;
				}
				setFooterHeight(curFooterHeight);
				if (curFooterHeight != 0) {
					postInvalidate();
				} else {
					footerViewState = initView;
					postInvalidate();
				}
			}
			if (footerViewState == initView && curFooterHeight != 0) {
				curFooterHeight = 0;
				setFooterHeight(curFooterHeight);
			}
		}
	}

	void runAnimation() {
		if (actionDownPointY < 0) {
			onHeaderAnimation(speed);
		}
		if (actionDownPointY2 < 0) {
			onFooterAnimation(speed);
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 如果是消失状态，此处动态消失
		runAnimation();
		super.onDraw(canvas);
	}

	private void measureHeaderLayout(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	Toast toast = null;

	// 弹出消息
	void showMsg(final String msg) {
		// handler.post(new Runnable() {
		// @Override
		// public void run() {
		if (toast == null) {
			toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
		}
		toast.setText(msg);
		toast.show();
		// }
		// });
	}
}
