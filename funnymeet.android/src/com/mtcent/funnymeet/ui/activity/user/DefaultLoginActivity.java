package com.mtcent.funnymeet.ui.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.MainActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONObject;

import mtcent.funnymeet.R;

public class DefaultLoginActivity extends Activity implements DownBack {
	public static final int ID = DefaultLoginActivity.class.hashCode();
	TextView login_country_selected;// 国家和地区选择
	TextView login_regincode;// 区域代码
	EditText login_password;// 输入密码(edittext)
	ImageView login_clearpassword_button;// 清除密码
	EditText login_phone;// 手机号码(edittext)
	ImageView login_clear_phone_button;// 清除输入的手机号码
	TextView login_others_button;// 选择其他登录方式
	TextView login_problem; // 登录遇到问题
	TextView login_login_button;// 登录按钮
	LinearLayout login_countryandregion;
	LinearLayout login_passwordframe;
	TextView titleTextView;
	LinearLayout login_phone_frame;
	private static final char SEPARATOR = ' ';
	private static final int FIRST_SEPARATOR_POSITION = 3;
	private static final int SECOND_SEPARATOR_POSITION = 7;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginandregister_login_default);// 默认登录界面
		init();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_FIRST_USER) {
			setResult(RESULT_FIRST_USER);
			finish();
		}

	}

	protected void init() {

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		login_problem = (TextView) findViewById(R.id.login_problem);
		login_problem.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(DefaultLoginActivity.this,
						LoginProblemSmsActivity.class);
				startActivity(intent);
			}
		});

		//
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("使用手机号登录");
		// 选择国家和地区
		login_countryandregion = (LinearLayout) findViewById(R.id.login_countryandregion);

		login_countryandregion.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				login_regincode
						.setBackgroundResource(R.drawable.input_bar_bg_active);
			}
		});
		// 区域代码
		login_regincode = (TextView) findViewById(R.id.login_regincode);
		login_regincode.setBackgroundResource(R.drawable.input_bar_bg_normal);

		// 登录的手机号码
		login_phone = (EditText) findViewById(R.id.login_phone);

		login_clear_phone_button = (ImageView) findViewById(R.id.login_clear_phone_button);
		login_clear_phone_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				login_phone.setText("");
			}
		});
		login_phone_frame = (LinearLayout) findViewById(R.id.login_phone_frame);
		login_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				// 获得焦点时改变背景

				login_regincode
						.setBackgroundResource(R.drawable.input_bar_bg_active);
				login_phone_frame
						.setBackgroundResource(R.drawable.input_bar_bg_active);

				if (login_phone.hasFocus() == false) {
					login_regincode
							.setBackgroundResource(R.drawable.input_bar_bg_normal);
					login_phone_frame
							.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}

			}
		});

		login_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				Editable phoneNumberEditable = login_phone.getEditableText();

				if (before == 1) {
					if ((start == FIRST_SEPARATOR_POSITION)
							|| (start == (SECOND_SEPARATOR_POSITION + 1))) {
						return;
					}
				}

				switch (parsePhoneNumber(phoneNumberEditable.toString())) {
				case 1:
					int oneInvalidSeparatorIndex = getOneInvalidSeparatorIndex(phoneNumberEditable
							.toString());
					phoneNumberEditable.delete(oneInvalidSeparatorIndex,
							oneInvalidSeparatorIndex + 1);// 删除该“-”
					break;

				case 2:
					phoneNumberEditable.insert(FIRST_SEPARATOR_POSITION,
							String.valueOf(SEPARATOR));
					break;

				case 3:
					phoneNumberEditable.insert(SECOND_SEPARATOR_POSITION + 1,
							String.valueOf(SEPARATOR));
					break;

				case 4:
					phoneNumberEditable.delete(
							phoneNumberEditable.length() - 1,
							phoneNumberEditable.length());
					break;

				case -1:
				case 0:
				default:
					break;
				}

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
					login_login_button
							.setBackgroundResource(R.drawable.green_btn_style);
					login_clear_phone_button.setVisibility(View.VISIBLE);

				} else {
					login_clear_phone_button.setVisibility(View.GONE);
					login_login_button
							.setBackgroundResource(R.drawable.green_btn_disable);
				}
			}

		});

		// 登录的密码
		login_password = (EditText) findViewById(R.id.login_password);
		login_passwordframe = (LinearLayout) findViewById(R.id.login_passwordframe);
		login_clearpassword_button = (ImageView) findViewById(R.id.login_clearpassword_button);
		login_clearpassword_button
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						login_password.setText("");
					}
				});
		login_password
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						login_passwordframe
								.setBackgroundResource(R.drawable.input_bar_bg_active);

						if (login_password.hasFocus() == false) {
							login_passwordframe
									.setBackgroundResource(R.drawable.input_bar_bg_normal);
						}

					}
				});

		login_password.addTextChangedListener(new TextWatcher() {

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
					login_clearpassword_button.setVisibility(View.VISIBLE);

				} else {
					login_clearpassword_button.setVisibility(View.GONE);

				}

			}
		});

		// 登录
		login_login_button = (TextView) findViewById(R.id.login_login_button);
		login_login_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String loginPassword = login_password.getText().toString();
				String loginMobilePhone = login_phone.getText().toString();
				if (loginPassword.isEmpty() || loginMobilePhone.isEmpty()) {
					return;
				} else {
					// setResult(RESULT_FIRST_USER);
					// finish();
					String[] temp = loginMobilePhone.split(" ");
					loginMobilePhone = temp[0] + temp[1] + temp[2];
					login(loginMobilePhone, loginPassword);
				}

			}
		});

		// 第三方登录
		login_others_button = (TextView) findViewById(R.id.login_others_button);
		login_others_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();

				intent.setClass(DefaultLoginActivity.this,
						OthersLoginActivity.class);
				startActivityForResult(intent, 345);

			}
		});

		View login_regs_button = findViewById(R.id.login_regs_button);
		login_regs_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();

				intent.setClass(DefaultLoginActivity.this,
						PhoneRegisterActivity.class);
				startActivityForResult(intent, 346);
			}
		});

		login_phone.setText(UserMangerHelper.getDefaultUserPhone());
	}

	CustomDialog waitDialog;

	void login(String loginMobilePhone, String loginPassword) {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "loginByPhone");// 页码
		task.addParam("phone", loginMobilePhone);// 页码
		task.addParam("password", loginPassword);// 页码
		SOApplication.getDownLoadManager().startTask(task);

		if (waitDialog == null) {
			waitDialog = CustomDialog.createWaitDialog(this, "正在登录", false);
		}
		waitDialog.show();
	}

	@Override
	public void onFinish(Pdtask t) {
		if (t.getParam("method").equals("loginByPhone")) {
			boolean succ = false;
			String msg = "登录失败";
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

			if (succ && user != null && user.has("mobile")) {
				UserMangerHelper.saveDefaultUser(user);
				StrUtil.showMsg(this, "登录成功");
				setResult(RESULT_FIRST_USER);
				Intent intent = new Intent();  
	            intent.setAction(MainActivity.INTENT_ACTION_USER_LOGIN);  
	            sendBroadcast(intent);  
				finish();
			} else {
				StrUtil.showMsg(this, msg);
			}

		}
		waitDialog.dismiss();
	}

	@Override
	public void onUpdate(Pdtask t) {

	}

	private int parsePhoneNumber(String phoneNumber) {
		if (phoneNumber == null) {
			return -1;
		}

		if (getOneInvalidSeparatorIndex(phoneNumber) != -1) {// 除index =
																// 3和8是“-”以外，其他位置有“-”时，按1处理
			return 1;
		}

		if ((phoneNumber.length() > FIRST_SEPARATOR_POSITION)// 字符数超3个，同时index=3的字符不是“-”，则按2来处理
				&& (phoneNumber.charAt(FIRST_SEPARATOR_POSITION) != SEPARATOR)) {
			return 2;
		}
		if ((phoneNumber.length() > (SECOND_SEPARATOR_POSITION + 1))// 字符数超8个，同时index=8的字符不是“-”，则按3来处理
				&& (phoneNumber.charAt(SECOND_SEPARATOR_POSITION + 1) != SEPARATOR)) {
			return 3;
		}

		// if ((phoneNumber.length() == (FIRST_SEPARATOR_POSITION + 1))
		// || (phoneNumber.length() == (SECOND_SEPARATOR_POSITION + 1 + 1))) {
		// return 4;
		// }
		if (phoneNumber.length() > 13)// 超过长度，按4处理
		{
			return 4;
		}

		return 0;
	}

	private int getOneInvalidSeparatorIndex(String phoneNumber) {
		if (phoneNumber == null) {
			return -1;
		}

		for (int index = 0; index < phoneNumber.length(); index++) {
			if ((index == FIRST_SEPARATOR_POSITION)
					|| (index == (SECOND_SEPARATOR_POSITION + 1))) {
				continue;
			}

			if (phoneNumber.charAt(index) == SEPARATOR) {
				return index;
			}
		}

		return -1;
	}
}
