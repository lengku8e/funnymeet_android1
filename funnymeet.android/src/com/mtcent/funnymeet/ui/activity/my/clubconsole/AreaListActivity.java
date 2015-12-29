package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.view.fragment.AreaListFragment;

import mtcent.funnymeet.R;

public class AreaListActivity extends SingleFragmentActivity {

	private AreaListFragment fragment;
	private TextView titleTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	/**
	 * 标题栏设定
	 */
	private void init() {
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("选择地区");
		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (fragment.getCurrContent() == AreaListFragment.CURR_CONTENT_PROVINCE) {
					finish();
				} else {
					fragment.rollbackContent();
				}
			}
		});
	}

	@Override
	protected Fragment createFragment() {
		this.fragment = new AreaListFragment();
		return fragment;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 监控/拦截/屏蔽返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (this.fragment.getCurrContent() == AreaListFragment.CURR_CONTENT_PROVINCE) {
				return super.onKeyDown(keyCode, event);
			} else {
				this.fragment.rollbackContent();
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
