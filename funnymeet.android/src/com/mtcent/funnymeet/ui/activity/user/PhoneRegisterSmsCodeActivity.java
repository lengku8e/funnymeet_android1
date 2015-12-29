package com.mtcent.funnymeet.ui.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONException;
import org.json.JSONObject;

import mtcent.funnymeet.R;

public class PhoneRegisterSmsCodeActivity extends Activity implements DownBack {

	EditText reg_smsidentifycode; // 验证码
	TextView reg_nextstep; // 下一步
	TextView reg_problems; // 注册遇到问题
	TextView reg_show_phone_regioncode; // 区域代码
	TextView reg_show_phone_phonesegment; // 号段
	TextView reg_show_phone_prefour; // 前四位
	TextView reg_show_phone_postfour;// 后四位
	TextView titleTextView;
	JSONObject userJson;
	String phone;
	MyCountTimer countTimer;

	void doIntent(Intent intent) {
		try {
			if (intent != null) {
				String userStr = intent.getStringExtra("user");
				userJson = new JSONObject(userStr);
				phone = userJson.optString("mobilePhone");
			}
		} catch (JSONException e) {
			finish();
			e.printStackTrace();
		}

	}

	class MyCountTimer extends CountDownTimer {
		public MyCountTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			// onFinish()中的代码是计时器结束的时候要做的事情
			reg_problems.setText("收不到验证码?");
			reg_problems.setTextColor(0xff516a8f);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// 代码是你倒计时开始时要做的事情，参数m是直到完成的时间
			reg_problems
					.setText("接收短信大约需要" + millisUntilFinished / 1000 + "秒钟");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.loginandregister_reg_identifycode);
		doIntent(getIntent());
		init();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 888 && resultCode == RESULT_FIRST_USER) {
			setResult(RESULT_FIRST_USER);
			finish();
		}

	}

	protected void init() {

		countTimer = new MyCountTimer(30000, 1000); // 倒计时30秒

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		reg_problems = (TextView) findViewById(R.id.reg_problems);
		countTimer.start();
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("填写短信验证码");

		TextView reg_show_phone_phonesegment = (TextView) findViewById(R.id.reg_show_phone_phonesegment);
		TextView reg_show_phone_prefour = (TextView) findViewById(R.id.reg_show_phone_prefour);
		TextView reg_show_phone_postfour = (TextView) findViewById(R.id.reg_show_phone_postfour);
		if (phone != null && phone.length() >= 11) {
			reg_show_phone_phonesegment.setText(phone.substring(0, 3));
			reg_show_phone_prefour.setText(phone.substring(3, 7));
			reg_show_phone_postfour.setText(phone.substring(7));
		}
		reg_nextstep = (TextView) findViewById(R.id.reg_nextstep);
		reg_smsidentifycode = (EditText) findViewById(R.id.reg_smsidentifycode);
		reg_nextstep.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent();
				// intent.setClass(PhoneRegisterSmsCodeActivity.this,
				// SOMainActivity.class);
				// startActivityForResult(intent, 888);
				if (!(reg_smsidentifycode.getText().toString().isEmpty())) {
					sendAuth(reg_smsidentifycode.getText().toString());
					// setResult(RESULT_FIRST_USER);
					// finish();
				} else {
					return;
				}

			}
		});
		reg_smsidentifycode.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() != 0) {
					reg_nextstep
							.setBackgroundResource(R.drawable.green_btn_style);
				} else {
					reg_nextstep
							.setBackgroundResource(R.drawable.green_btn_disable);
				}
			}
		});
	}

	CustomDialog waitDialog;

	void sendAuth(String auth) {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "authRegisterByPhone");// 页码
		task.addParam("user_guid", userJson.optString("guid"));// 页码
		task.addParam("auth_code", auth);// 页码
		SOApplication.getDownLoadManager().startTask(task);

		if (waitDialog == null) {
			waitDialog = CustomDialog.createWaitDialog(this, "正在提交验证码", false);
		}
		waitDialog.show();
	}

	@Override
	public void onFinish(Pdtask t) {
		if (t.getParam("method").equals("authRegisterByPhone")) {
			boolean succ = false;
			String msg = "验证失败";
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

			if (succ && user != null && user.has("mobilePhone")) {
				UserMangerHelper.saveDefaultUser(user);
				StrUtil.showMsg(this, "注册成功");
				setResult(RESULT_FIRST_USER);
				finish();
			} else {
				StrUtil.showMsg(this, msg);
			}

		}
		waitDialog.dismiss();
	}

	@Override
	public void onUpdate(Pdtask t) {
		// TODO Auto-generated method stub

	}

}
