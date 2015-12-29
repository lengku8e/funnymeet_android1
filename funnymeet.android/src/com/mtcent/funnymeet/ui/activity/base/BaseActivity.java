package com.mtcent.funnymeet.ui.activity.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;

import mtcent.funnymeet.R;

public class BaseActivity extends Activity implements DownBack {
	protected BaseActivity mActivity;
	protected LayoutInflater inflater;

	private long mkeyTime;
	String backMsg = null;

	public void setOnDoubleBack(String msg) {
		backMsg = msg;
	}

	public void setOnDoubleBack() {
		backMsg = "再按一次退出程序";
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && backMsg != null) {
			if ((System.currentTimeMillis() - mkeyTime) > 2000) {
				mkeyTime = System.currentTimeMillis();
				Toast.makeText(this, backMsg, Toast.LENGTH_LONG).show();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = this;
		inflater = LayoutInflater.from(mActivity);
	}

	@Override
	public void onFinish(Pdtask t) {

	}

	@Override
	public void onUpdate(Pdtask t) {

	}

	CustomDialog waitDialog = null;

	public void showWait() {
		showWait("加载中...");
	}

	public void showWait(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (waitDialog == null) {
					waitDialog = CustomDialog.createWaitDialog(mActivity, msg,
							false);
				}
				waitDialog.setCancelable(false);
				waitDialog.findViewById(R.id.outside).setOnClickListener(null);
				waitDialog.findViewById(R.id.progressBar1).setVisibility(
						View.VISIBLE);

				((TextView) waitDialog.findViewById(R.id.textView1))
						.setText(msg);
				waitDialog.show();
			}
		});
	}

	public void showWaitToMsg(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (waitDialog == null) {
					waitDialog = CustomDialog.createWaitDialog(mActivity, msg,
							false);
				}
				waitDialog.setCancelable(true);
				waitDialog.findViewById(R.id.outside).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								waitDialog.dismiss();
							}
						});
				waitDialog.findViewById(R.id.progressBar1).setVisibility(
						View.GONE);
				((TextView) waitDialog.findViewById(R.id.textView1))
						.setText(msg);
				waitDialog.show();
			}
		});
	}

	public void hideWait() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (waitDialog != null) {
					waitDialog.dismiss();
				}
			}
		});
	}
}
