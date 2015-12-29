package com.mtcent.funnymeet.ui.activity.my.setting;

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

public class UpdateQQActivity extends BaseActivity {

	TextView titleTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_myaccountsetting_modifyqq);
		init();
		CustomDialog.createConfirmDialog(mActivity, "请输入新的QQ号码",true,
				new OnConfirmListern() {

					@Override
					public void onConfirm(String text) {
						if (text.length() > 0) {
							Pdtask task = new Pdtask(mActivity, mActivity,
									Constants.SERVICE_HOST, null,
									RequestHelper.Type_PostParam, null, 0,
									true);
							task.addParam("method", "setUserQQ");// 页码
							task.addParam("user_guid",
									UserMangerHelper.getDefaultUserGuid());// 页码
							task.addParam("long_session", UserMangerHelper
									.getDefaultUserLongsession());// 页码
							task.addParam("qq", text);// 页码
							//task.addParam("qq_openid", text);// 页码
							//task.addParam("access_token", text);// 页码

							SOApplication.getDownLoadManager().startTask(task);
							showWait();
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
		titleTextView.setText("QQ号码");
		TextView currnetmobile = (TextView) findViewById(R.id.setting_modifymobile_currnetmobile);
		currnetmobile.setText("绑定的QQ："+UserMangerHelper.getDefaultUserQQ());
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

		if (t.getParam("method").equals("setUserQQ")) {
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
