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

public class UpdateMobileActivity extends BaseActivity {

	TextView titleTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_myaccountsetting_modifymobile);
		init();
		CustomDialog.createConfirmDialog(mActivity, "请输入新的手机号码",true,
				new OnConfirmListern() {

					@Override
					public void onConfirm(String phone) {
						if (phone != null && phone.length() >= 11) {
							Pdtask task = new Pdtask(mActivity, mActivity,
									Constants.SERVICE_HOST, null,
									RequestHelper.Type_PostParam, null, 0,
									true);
							task.addParam("method", "setUserPhoneNum");// 页码
							task.addParam("user_guid",
									UserMangerHelper.getDefaultUserGuid());// 页码
							task.addParam("long_session", UserMangerHelper
									.getDefaultUserLongsession());// 页码
							task.addParam("phone", phone);// 页码
							//task.addParam("auth", phone);// 页码

							SOApplication.getDownLoadManager().startTask(task);
							showWait();
						}else{
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
		titleTextView.setText("修改手机号");
		TextView currnetmobile = (TextView) findViewById(R.id.setting_modifymobile_currnetmobile);
		currnetmobile.setText("绑定的手机号："+UserMangerHelper.getDefaultUserPhone());
	}
	
	@Override
	public void onFinish(Pdtask t) {
		boolean succ = false;
		String msg = "修改失败";
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

		if (t.getParam("method").equals("setUserPhoneNum")) {
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
