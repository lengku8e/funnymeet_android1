package com.mtcent.funnymeet.ui.view.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;

import mtcent.funnymeet.R;

@SuppressLint("NewApi")
public class PopupViewDialog extends Dialog {
	Activity mActivity;
	Animation mShowAnimation;
	Animation mHideAnimation;
	View contentView;
	FrameLayout contentFrameLayout;
	OnViewLocation mLocation = null;
	int[] position = new int[2];
	int vw;
	int vh;

	public static interface OnViewLocation {
		public FrameLayout.LayoutParams getLayoutParams(
				FrameLayout.LayoutParams params, int locationViewX,
				int locationViewY, int locationViewW, int locationViewH);
	}

	public PopupViewDialog(Activity context) {
		super(context, R.style.PopupViewDialog);
		mActivity = context;
		contentFrameLayout = new FrameLayout(context) {
			@Override
			protected void onLayout(boolean changed, int left, int top,
					int right, int bottom) {
				super.onLayout(changed, left, top, right, bottom);

				if (mLocation != null) {
					int[] position2 = new int[2];
					contentFrameLayout.getLocationInWindow(position2);
					FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) contentView
							.getLayoutParams();
					params = mLocation.getLayoutParams(params, position[0]
							- position2[0], position[1] - position2[1], vw, vh);
					contentView.setLayoutParams(params);
				}
			}
		};
		ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		setContentView(contentFrameLayout, p);
		contentFrameLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				PopupViewDialog.this.dismiss();
			}
		});
		// contentFrameLayout.setOnSystemUiVisibilityChangeListener(null);
	}

	public ViewGroup getRootView() {
		return contentFrameLayout;
	}

	public void setAnimation(View contentView,Animation showAnimation, Animation hideAnimation) {
		this.contentView  = contentView;
		mShowAnimation = showAnimation;
		mHideAnimation = hideAnimation;
	}

	@Override
	public void dismiss() {
		if (mHideAnimation != null && contentView != null) {
			mHideAnimation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation arg0) {

				}

				@Override
				public void onAnimationRepeat(Animation arg0) {

				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					PopupViewDialog.super.dismiss();
				}
			});
			contentView.startAnimation(mHideAnimation);
		} else {
			super.dismiss();
		}
	}

	@Override
	public void show() {
		if (mShowAnimation != null && contentView != null) {
			contentView.startAnimation(mShowAnimation);
		}
		super.show();
	}

	public void setContentView(View v, Animation showAnimation,
			Animation hideAnimation) {
		contentView = v;
		contentFrameLayout.removeAllViews();
		contentFrameLayout.addView(contentView);
		mShowAnimation = showAnimation;
		mHideAnimation = hideAnimation;
	}

	@Override
	public void setCanceledOnTouchOutside(boolean cancel) {
		if (cancel) {
			contentFrameLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					PopupViewDialog.this.cancel();
				}
			});
		} else {
			contentFrameLayout.setOnClickListener(null);
		}
		super.setCanceledOnTouchOutside(cancel);
	}

	@Override
	public void setOnShowListener(OnShowListener listener) {
		super.setOnShowListener(listener);
	}

	public void locationWithView(View locationView,
			OnViewLocation locationMethod) {
		locationView.getLocationInWindow(position);
		vw = locationView.getWidth();
		vh = locationView.getHeight();
		mLocation = locationMethod;
	}

}