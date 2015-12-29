package com.mtcent.funnymeet.ui.activity.club;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog.OnConfirmListern;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONObject;

import mtcent.funnymeet.R;

public class SettingClubNickNameActivity extends BaseActivity {

	TextView titleTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_myaccountsetting_modifyweixin);
		init();

		CustomDialog.createConfirmDialog(mActivity, "请在本俱乐部的昵称", true,
				new OnConfirmListern() {

					@Override
					public void onConfirm(String phone) {
						if (phone != null) {
							Pdtask task = new Pdtask(mActivity, mActivity,
									Constants.SERVICE_HOST, null,
									RequestHelper.Type_PostParam, null, 0,
									true);
							task.addParam("method", "setUserWeixin");
							task.addParam("user_guid",
									UserMangerHelper.getDefaultUserGuid());
							task.addParam("long_session", UserMangerHelper
									.getDefaultUserLongsession());
							task.addParam("weixin", phone);

							SOApplication.getDownLoadManager().startTask(task);
							showWait();
						} else {
							StrUtil.showMsg(mActivity, "手机号不合法");
							finish();
						}
					}

					@Override
					public void onCancle() {
						finish();
					}
				}).show();

	}

	protected void init() {
		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("修改微信号");
		TextView currnetmobile = (TextView) findViewById(R.id.setting_modifymobile_currnetmobile);
		currnetmobile
				.setText("绑定微信：" + UserMangerHelper.getDefaultUserWeixin());
	}

	@Override
	public void onFinish(Pdtask t) {
		boolean succ = false;
		String msg = "绑定失败";
		JSONObject user = null;
		if (t.json != null) {
			JSONObject results = t.json.optJSONObject("results");
			if (results != null) {
				user = results.optJSONObject("user");
				int su = results.optInt("success");
				if (su == 1) {
					succ = true;
				} else if (results.has("msg")) {
					msg = results.optString("msg");
				}
			}
		}

		if (t.getParam("method").equals("setUserWeixin")) {
			if (succ && user != null) {
				UserMangerHelper.saveDefaultUser(user);
			} else {
				StrUtil.showMsg(mActivity, msg);
			}
		}

		hideWait();
		finish();
	}
}
