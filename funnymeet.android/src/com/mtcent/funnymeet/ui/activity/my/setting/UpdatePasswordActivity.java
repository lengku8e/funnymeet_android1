package com.mtcent.funnymeet.ui.activity.my.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONException;
import org.json.JSONObject;

import mtcent.funnymeet.R;

public class UpdatePasswordActivity extends BaseActivity {
	public final static int ID = UpdatePasswordActivity.class.hashCode();
	TextView titleTextView;
	TextView finishbutton;
	JSONObject user = new JSONObject();
	EditText password1;
	EditText password2;

	void doIntent() {
		try {
			Intent intent = getIntent();
			String ustr = intent.getStringExtra("user");
			user = new JSONObject(ustr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (user == null) {
			user = new JSONObject();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		doIntent();
		setContentView(R.layout.setting_myaccountsetting_modifypsw);
		init();

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
		titleTextView.setText("修改密码");

		((TextView) findViewById(R.id.sohuoddongid)).setText(user
				.optString("accountName"));

		finishbutton = (TextView) findViewById(R.id.finishbutton);
		finishbutton.setVisibility(View.VISIBLE);
		finishbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				String p1 = password1.getText().toString();
				String p2 = password2.getText().toString();
				if (p1.length() < 6) {
					CustomDialog.createMsgDialog(mActivity, "密码不能小于6位", null).show();
				} else if (p1.equals(p2)) {
					Pdtask task = new Pdtask(mActivity, mActivity,
							Constants.SERVICE_HOST, null,
							RequestHelper.Type_PostParam, null, 0, true);

					task.addParam("method", "updateUserPassWordByPrivileged");
					task.addParam("user_guid", user.optString("guid", null));
					task.addParam("password", p1);
					task.addParam("privileged", user.optString("privileged", null));
					task.addParam("long_session", user.optString("longSession", null));

					SOApplication.getDownLoadManager().startTask(task);
					showWait();
				} else {
					CustomDialog.createMsgDialog(mActivity, "帐号密码不一致", null).show();
				}
			}
		});
		password1 = (EditText) findViewById(R.id.password1);
		password2 = (EditText) findViewById(R.id.password2);

	}

	@Override
	public void onFinish(Pdtask t) {
		boolean succ = false;
		String msg = "失败";
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
		
		if (t.getParam("method").equals("updateUserPassWordByPrivileged")) {
			if (succ && user != null) {
				hideWait();
				finish();
			} else {
				StrUtil.showMsg(mActivity, msg);
			}

		}
		hideWait();
	}
}
