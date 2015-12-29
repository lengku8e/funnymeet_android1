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

import org.json.JSONObject;

import mtcent.funnymeet.R;

public class LoginProblemSmsIdentifyActivity extends Activity implements
		DownBack {

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
	Intent get_intent;
	String trimPhoneNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.loginandregister_reg_identifycode);
		init();
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

	protected void init() {

		get_intent = this.getIntent();
		String identifiedPhoneNum = get_intent.getStringExtra("phone");
		String[] utilArray = identifiedPhoneNum.split(" ");
		trimPhoneNum = utilArray[0] + utilArray[1] + utilArray[2];

		reg_show_phone_phonesegment = (TextView) findViewById(R.id.reg_show_phone_phonesegment);
		reg_show_phone_prefour = (TextView) findViewById(R.id.reg_show_phone_prefour);
		reg_show_phone_postfour = (TextView) findViewById(R.id.reg_show_phone_postfour);

		reg_show_phone_phonesegment.setText(utilArray[0]);
		reg_show_phone_prefour.setText(utilArray[1]);
		reg_show_phone_postfour.setText(utilArray[2]);

		reg_smsidentifycode = (EditText) findViewById(R.id.reg_smsidentifycode);
		reg_nextstep = (TextView) findViewById(R.id.reg_nextstep);

		reg_nextstep.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (reg_smsidentifycode.getText().toString().length() != 0) {
					identiftAndLogin(trimPhoneNum, reg_smsidentifycode
							.getText().toString());
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
		titleTextView.setText("填写验证码");
	}

	CustomDialog waitDialog;

	void identiftAndLogin(String loginMobilePhone, String identifyCode) {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "getPrivilegedByAuthCode");// 页码
		task.addParam("phone", loginMobilePhone);// 页码
		task.addParam("auth_code", identifyCode);// 页码
		SOApplication.getDownLoadManager().startTask(task);

		if (waitDialog == null) {
			waitDialog = CustomDialog.createWaitDialog(this, "正在 验证", false);
		}
		waitDialog.show();
	}

	@Override
	public void onFinish(Pdtask t) {
		// TODO Auto-generated method stub
		if (t.getParam("method").equals("getPrivilegedByAuthCode")) {
			boolean succ = false;
			String msg = "由于呆呆没改接口导致网络错误";
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
				// StrUtil.showMsg(this, "验证成功");
				Intent intent = new Intent();
				intent.putExtra("user", user.toString());
				intent.setClass(LoginProblemSmsIdentifyActivity.this,
						LoginProblemModifyPassword.class);
				startActivity(intent);
				// setResult(RESULT_FIRST_USER);
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
