package com.mtcent.funnymeet.ui.activity.discovery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.activity.discovery.HDThemeHDListActivity.HDInfo;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.view.control.ScrollHPageWithTableView;
import com.mtcent.funnymeet.ui.view.control.ScrollHPageWithTableView.ScrollHPageWithTableAdapter;
import com.mtcent.funnymeet.ui.view.control.ThemeSelectView;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONObject;

import java.util.ArrayList;

import mtcent.funnymeet.R;

public class HDThemeSelectActivity extends Activity implements DownBack,
		ScrollHPageWithTableAdapter {

	TextView titleName;
	View allView;
	View appointView;
	String title;
	protected Activity mActivity;
	protected LayoutInflater inflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.somain_base_tableview);
		mActivity = this;
		inflater = LayoutInflater.from(mActivity);
		init();

	}

	void init() {

		titleName = (TextView) findViewById(R.id.titleTextView);
		title = "主题";
		titleName.setText(title);
		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		ThemeSelectView themeView = new ThemeSelectView(mActivity) {
			@Override
			public void onSelect(JSONObject parentJson, JSONObject childJson,
					ArrayList<JSONObject> dataListItemContent) {
				Intent mIntent = new Intent(mActivity,
						HDThemeHDListActivity.class);
				Bundle mBundle = new Bundle();
				HDInfo info = new HDInfo();
				info.parentJson = parentJson == null ? null : parentJson
						.toString();
				info.childJson = childJson == null ? null : childJson
						.toString();
				info.childJsonList = StrUtil.createJSONObject(
						dataListItemContent).toString();

				mBundle.putSerializable(HDInfo.key, info);
				mIntent.putExtras(mBundle);
				mActivity.startActivity(mIntent);
			}
		};
		allView = themeView.getMainView();

		// initAllview();
		initAppointview();

		ScrollHPageWithTableView scrollHPageWithTableView = (ScrollHPageWithTableView) findViewById(R.id.scrollHPageWithTable);
		scrollHPageWithTableView.setScrollHPageWithTableAdapter(this);

	}

	@Override
	public int getPageCount() {
		return 2;
	}

	@Override
	public String getTableString(int index) {
		String table = "";
		if (index == 0) {
			table = "所有分类";
		} else {
			table = "猜你喜欢";
		}
		return table;
	}

	@Override
	public View getPageView(int index) {
		View v = null;
		if (index == 0) {
			v = allView;
		} else if (index == 1) {
			v = appointView;
		}
		return v;
	}

	@Override
	public void onPageChange(int index) {

	}

	void resetView() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				titleName.setText("主题");
			}
		});
	}

	@Override
	public void onFinish(Pdtask t) {

		resetView();

	}

	@Override
	public void onUpdate(Pdtask t) {
		onFinish(t);
	}

	void initAppointview() {

	}

}
