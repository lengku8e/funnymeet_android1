package com.mtcent.funnymeet.ui.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONObject;

import mtcent.funnymeet.R;

public class LoginProblemSmsActivity extends BaseActivity implements DownBack {

	protected static final View login_login_button = null;
	EditText login_problem_phone;
	TextView next_step;
	CustomDialog dialog = null;
	CustomDialog identifyphone_dialog = null;
	int mobilePhoneNumberisNullColor = 0xff909495;
	int mobilePhoneNumberNotNullColor = 0xffffffff;
	private static final char SEPARATOR = ' ';
	private static final int FIRST_SEPARATOR_POSITION = 3;
	private static final int SECOND_SEPARATOR_POSITION = 7;
	LinearLayout login_problem_dialog_frame;
	TextView login_problem_confirmphonenumber;
	TextView login_problem_phone_cancel;
	TextView login_problem_phone_confirm;
	TextView login_problem_identify_phone_confirm;
	LinearLayout login_phone_frame;
	TextView login_regincode;
	String phoneNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginandregister_loginproblem_sms_phone);
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

		login_phone_frame = (LinearLayout) findViewById(R.id.login_phone_frame);
		login_regincode = (TextView) findViewById(R.id.login_regincode);

		login_problem_phone = (EditText) findViewById(R.id.login_problem_phone);

		login_problem_phone
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						// 获得焦点时改变背景

						login_regincode
								.setBackgroundResource(R.drawable.input_bar_bg_active);
						login_phone_frame
								.setBackgroundResource(R.drawable.input_bar_bg_active);

						if (login_problem_phone.hasFocus() == false) {
							login_regincode
									.setBackgroundResource(R.drawable.input_bar_bg_normal);
							login_phone_frame
									.setBackgroundResource(R.drawable.input_bar_bg_normal);
						}

					}
				});

		login_problem_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				Editable phoneNumberEditable = login_problem_phone
						.getEditableText();

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
				if (s.length() > 0) {
					next_step.setTextColor(mobilePhoneNumberNotNullColor);
				} else if (s.length() == 0) {
					next_step.setTextColor(mobilePhoneNumberisNullColor);
				}
			}

		});

		TextView tv = (TextView) findViewById(R.id.titleTextView);
		tv.setText("用短信验证码登陆");

		identifyphone_dialog = new CustomDialog(this);
		identifyphone_dialog
				.setContentView(R.layout.login_problem_identifyphone_dialog);
		identifyphone_dialog.setCancelable(true);

		identifyphone_dialog = new CustomDialog(this);
		identifyphone_dialog
				.setContentView(R.layout.login_problem_identifyphone_dialog);
		identifyphone_dialog.setCancelable(true);
		dialog = new CustomDialog(this);
		dialog.setContentView(R.layout.login_problem_comfirmphone_dialog);
		dialog.setCancelable(true);

		login_problem_identify_phone_confirm = (TextView) identifyphone_dialog
				.findViewById(R.id.login_problem_identify_phone_confirm);

		login_problem_identify_phone_confirm
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						hideIdentifyPhoneDialog();
					}
				});

		login_problem_confirmphonenumber = (TextView) dialog
				.findViewById(R.id.login_problem_confirmphonenumber);
		login_problem_phone_cancel = (TextView) dialog
				.findViewById(R.id.login_problem_phone_cancel);
		login_problem_phone_confirm = (TextView) dialog
				.findViewById(R.id.login_problem_phone_confirm);

		login_problem_phone_confirm
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						phoneNum = login_problem_phone.getText().toString();

						if (phoneNum.length() != 13) {
							showIdentifyPhoneDialog();
							hideConfirmDialog();
						} else {
							String phoneNumUtil[] = phoneNum.split(" ");
							String trimPhoneNum = phoneNumUtil[0]
									+ phoneNumUtil[1] + phoneNumUtil[2];
							identifyPhoneNum(trimPhoneNum);

						}
					}
				});

		login_problem_phone_cancel
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						hideConfirmDialog();
					}
				});

		login_problem_dialog_frame = (LinearLayout) dialog
				.findViewById(R.id.login_problem_dialog_frame);
		login_problem_dialog_frame
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						hideConfirmDialog();
					}
				});

		next_step = (TextView) findViewById(R.id.next_step);
		next_step.setVisibility(View.VISIBLE);
		next_step.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String phoneNum = login_problem_phone.getText().toString();
				if (phoneNum.length() > 0) {
					login_problem_confirmphonenumber.setText("+86 " + phoneNum);
					showConfirmDialog();
				}

			}
		});
	}

	CustomDialog waitDialog;

	void identifyPhoneNum(String loginMobilePhone) {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "sendLoginAuthCode");// 页码
		task.addParam("phone", loginMobilePhone);// 页码
		SOApplication.getDownLoadManager().startTask(task);

		if (waitDialog == null) {
			waitDialog = CustomDialog.createWaitDialog(this, "正在 验证", false);
		}
		waitDialog.show();
	}

	void showIdentifyPhoneDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				identifyphone_dialog.show();
			}
		});
	}

	void hideIdentifyPhoneDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				identifyphone_dialog.hide();
			}
		});
	}

	void showConfirmDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				dialog.show();
			}
		});
	}

	void hideConfirmDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				dialog.hide();
			}
		});
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

	@Override
	public void onFinish(Pdtask t) {
		// TODO Auto-generated method stub

		if (t.getParam("method").equals("sendLoginAuthCode")) {
			boolean succ = false;
			String msg = "手机号尚未注册";
			if (t.json != null) {
				JSONObject results = t.json.optJSONObject("results");
				if (results != null) {
					int su = results.optInt("success");
					if (su == 1) {
						succ = true;
					} else if (results.has("msg")) {
//						msg = results.optString("msg");
						showIdentifyPhoneDialog();
						hideConfirmDialog();
					}
				}
			}

			if (succ) {
				Intent intent = new Intent();

				intent.putExtra("phone", phoneNum);
				intent.setClass(LoginProblemSmsActivity.this,
						LoginProblemSmsIdentifyActivity.class);
				hideConfirmDialog();
				LoginProblemSmsActivity.this.startActivity(intent);
				LoginProblemSmsActivity.this.finish();
				StrUtil.showMsg(this, "验证码已发送");
				finish();
			} else {
//				StrUtil.showMsg(this, msg);
				showIdentifyPhoneDialog();
				hideConfirmDialog();
			}

		}
		waitDialog.dismiss();

	}

	@Override
	public void onUpdate(Pdtask t) {
		// TODO Auto-generated method stub

	}

}
