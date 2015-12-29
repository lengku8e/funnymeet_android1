package com.mtcent.funnymeet.ui.activity.my.setting;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog.OnConfirmListern;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONObject;

import mtcent.funnymeet.R;

public class UpdateEmailActivity extends BaseActivity {

	TextView titleTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_myaccountsetting_modifyemail);
		init();

		CustomDialog.createConfirmDialog(mActivity, "请输入Email地址",true,
				new OnConfirmListern() {

					@Override
					public void onConfirm(String phone) {
						if (phone != null) {
							Pdtask task = new Pdtask(mActivity, mActivity,
									Constants.SERVICE_HOST, null,
									RequestHelper.Type_PostParam, null, 0,
									true);
							task.addParam("method", "setUserEmail");// 页码
							task.addParam("user_guid",
									UserMangerHelper.getDefaultUserGuid());// 页码
							task.addParam("long_session", UserMangerHelper
									.getDefaultUserLongsession());// 页码
							task.addParam("email", phone);// 页码
							SOApplication.getDownLoadManager().startTask(task);
							showWait();
						} else {
							StrUtil.showMsg(mActivity, "不合法");
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
		titleTextView.setText("修改邮件地址");
		
		TextView email = (TextView) findViewById(R.id.email);
		email.setText(UserMangerHelper.getDefaultUserEmail());
        
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

		if (t.getParam("method").equals("setUserEmail")) {
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
