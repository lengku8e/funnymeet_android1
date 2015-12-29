package com.mtcent.funnymeet.ui.view.control;


import android.app.Activity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;

public class FunnymeetBaseView implements DownBack {
	protected Activity mActivity;
	protected View mainView;
	protected LayoutInflater inflater;

	public FunnymeetBaseView(Activity activity) {
		mActivity = (Activity)activity;
		inflater = LayoutInflater.from(mActivity);
	}

	public View getMainView() {
		return mainView;
	}

	public void onHide() {

	}

	public void onShow() {

	}
	
	
	public void onDestroy() {

	}
	
	public boolean onKey(int arg1, KeyEvent arg2)
	{
		return false;
	}
	
	@Override
	public void onFinish(Pdtask t) {
		

	}

	@Override
	public void onUpdate(Pdtask t) {
		

	}

	public Activity getmActivity() {
		return mActivity;
	}

	public void setmActivity(Activity mActivity) {
		this.mActivity = mActivity;
	}
	
	
}
