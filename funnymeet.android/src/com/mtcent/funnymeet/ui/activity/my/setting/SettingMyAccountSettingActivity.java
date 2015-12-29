package com.mtcent.funnymeet.ui.activity.my.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONObject;

import mtcent.funnymeet.R;

public class SettingMyAccountSettingActivity extends Activity implements
		DownBack {

	TextView titleTextView;
	LinearLayout setting_modify_sohuodongid;
	LinearLayout setting_modify_qq;
	LinearLayout setting_modify_mobile;
	LinearLayout setting_modify_email;
	LinearLayout setting_modify_weixin;
	LinearLayout setting_modify_password;
	TextView cancelchange;
	TextView confirmchange;
	CustomDialog passwordHintdialog;
	EditText oldpassword;
	Activity mActivity = this;

	TextView qq;
	TextView weixin;
	TextView name;
	TextView phone;
	TextView email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_myaccountsetting);// 默认登录界面
		init();
		requestData();
		resetView();
	}

	@Override
	protected void onResume() {
		resetView();
		super.onResume();
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
		titleTextView.setText("我的账号");

		qq = (TextView) findViewById(R.id.qq);
		name = (TextView) findViewById(R.id.name);
		weixin = (TextView) findViewById(R.id.weixin);
		phone = (TextView) findViewById(R.id.phone);
		email = (TextView) findViewById(R.id.email);

		setting_modify_sohuodongid = (LinearLayout) findViewById(R.id.setting_modify_sohuodongid);
		setting_modify_sohuodongid
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(SettingMyAccountSettingActivity.this,
								UpdateFunnymeetIdActivity.class);
						startActivityForResult(intent, 127);
					}
				});

		setting_modify_qq = (LinearLayout) findViewById(R.id.setting_modify_qq);
		setting_modify_qq.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SettingMyAccountSettingActivity.this,
						UpdateQQActivity.class);
				startActivityForResult(intent, 129);
			}
		});

		setting_modify_mobile = (LinearLayout) findViewById(R.id.setting_modify_mobile);
		setting_modify_mobile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SettingMyAccountSettingActivity.this,
						UpdateMobileActivity.class);
				startActivityForResult(intent, 131);
			}
		});

		setting_modify_email = (LinearLayout) findViewById(R.id.setting_modify_email);
		setting_modify_email.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SettingMyAccountSettingActivity.this,
						UpdateEmailActivity.class);
				startActivityForResult(intent, 132);
			}
		});

		setting_modify_weixin = (LinearLayout) findViewById(R.id.setting_modify_weixin);
		setting_modify_weixin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SettingMyAccountSettingActivity.this,
						UpdateWeixinActivity.class);
				startActivityForResult(intent, 133);
			}
		});

		setting_modify_password = (LinearLayout) findViewById(R.id.setting_modify_password);
		setting_modify_password.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (passwordHintdialog == null) {
					passwordHintdialog = new CustomDialog(
							SettingMyAccountSettingActivity.this);
					passwordHintdialog
							.setContentView(R.layout.setting_myaccountsetting_modifypsw_dialog);
					passwordHintdialog.setCancelable(true);
					dialogfunction();
				}
				passwordHintdialog.show();

			}
		});

	}

	void resetView() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				JSONObject user = UserMangerHelper.getDefaultUser();
				String txt = user.optString("qq", "");
				if (txt.isEmpty()) {
					txt = "未绑定";
				}

				qq.setText(txt);

				txt = user.optString("weixin", "");
				if (txt.isEmpty()) {
					txt = "未绑定";
				}
				weixin.setText(txt);

				txt = user.optString("accountName", "");
				if (txt.isEmpty()) {
					txt = "未设置";
				}
				name.setText(txt);


				phone.setText(user.optString("mobilePhone"));
			}
		});
	}

	void requestData() {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_DownJsonString, null, 0, true);
		task.addParam("method", "getUserInfoByGuid");// 页码
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());// 页码
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());// 页码
		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	CustomDialog waitDialog = null;

	void showWait() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (waitDialog == null) {
					waitDialog = new CustomDialog(mActivity);
					waitDialog.setContentView(R.layout.dialog_wait);
				}
				waitDialog.show();
			}
		});
	}

	void hideWait() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				waitDialog.dismiss();
			}
		});
	}

	@Override
	public void onFinish(Pdtask t) {

		boolean succ = false;
		String msg = "获取个人信息失败";
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

		if (t.getParam("method").equals("getUserInfoByGuid")) {
			if (succ && user != null && user.has("mobilePhone")) {
				UserMangerHelper.saveDefaultUser(user);
				resetView();
			} else {
				StrUtil.showMsg(this, msg);
			}

		} else if (t.getParam("method").equals("getPrivileged")) {
			if (succ && user != null) {
				hideWait();
				Intent intent = new Intent();
				intent.setClass(SettingMyAccountSettingActivity.this,
						UpdatePasswordActivity.class);
				intent.putExtra("user", user.toString());
				startActivityForResult(intent, UpdatePasswordActivity.ID);
			} else {
				StrUtil.showMsg(this, msg);
			}
		}
		hideWait();
	}

	@Override
	public void onUpdate(Pdtask t) {
		//
	}

	protected void dialogfunction() {
		cancelchange = (TextView) passwordHintdialog.findViewById(R.id.cancel);
		confirmchange = (TextView) passwordHintdialog
				.findViewById(R.id.confirm);
		oldpassword = (EditText) passwordHintdialog
				.findViewById(R.id.inputEditText);

		confirmchange.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				passwordHintdialog.cancel();
				String password = oldpassword.getText().toString();
				oldpassword.setText("");
				Pdtask task = new Pdtask(SettingMyAccountSettingActivity.this,
						SettingMyAccountSettingActivity.this,
						Constants.SERVICE_HOST, null,
						RequestHelper.Type_PostParam, null, 0, true);

				task.addParam("method", "getPrivileged");
				task.addParam("user_guid",
						UserMangerHelper.getDefaultUserGuid());
				task.addParam("password", password);
				task.addParam("long_session",
						UserMangerHelper.getDefaultUserLongsession());

				SOApplication.getDownLoadManager().startTask(task);
				showWait();

			}
		});

		cancelchange.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				oldpassword.setText("");
				passwordHintdialog.cancel();
			}
		});

	}

}
