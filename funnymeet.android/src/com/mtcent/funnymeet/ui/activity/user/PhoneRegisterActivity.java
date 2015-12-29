package com.mtcent.funnymeet.ui.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.activity.my.clubconsole.SelectImageActivity;
import com.mtcent.funnymeet.util.StrUtil;
import com.qiniu.auth.JSONObjectRet;
import com.qiniu.io.IO;
import com.qiniu.io.PutExtra;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import mtcent.funnymeet.R;

public class PhoneRegisterActivity extends Activity implements DownBack {
	EditText nickname;// 昵称
	ImageView register_headicon;// 头像
	TextView register_countryandregion_selected;// 国家和地区显示
	LinearLayout countryandregionframe;// 国家和地区选择
	EditText reg_regioncode_edit;// 编辑区域编码
	EditText reg_phone;// 注册手机号码
	ImageView reg_clear_regphone_button; // 清除手机号码、
	EditText reg_password; // 注册密码
	ImageView reg_showpassword_button; // 显示密码
	TextView reg_button; // 注册按钮
	LinearLayout reg_regphone_frame;
	static int showOrNot = 0;
	TextView reg_password_title;
	static String nicknameStr, regPasswordStr, regPhoneStr;
	TextView titleTextView;
	String imageFilePath;
	String imageFileHash = null;
	LinearLayout reg_password_frame;
	private static final char SEPARATOR = ' ';
	private static final int FIRST_SEPARATOR_POSITION = 3;
	private static final int SECOND_SEPARATOR_POSITION = 7;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginandregister_reg_phone);// 手机注册界面
		//初始化界面控件
		initViewControl();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 556 && resultCode == RESULT_FIRST_USER) {
			setResult(RESULT_FIRST_USER);
			finish();
		} else if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 123 && null != data) {
				imageFilePath = data.getStringExtra("path");
				imageFileHash = null;
				Options options = new Options();
				Bitmap bitmap = BitmapFactory
						.decodeFile(imageFilePath, options);// 解码图片
				register_headicon.setImageBitmap(bitmap);
			}
		}

	}

	/*
		初始化视图控件
	 */
	protected void initViewControl() {
		waitDialog = CustomDialog.createWaitDialog(PhoneRegisterActivity.this,
				"正在提交", false);
		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("使用手机注册");

		//设置注册按钮响应事件
		setRegisterButtonEvent();
		//设置注册区域响应事件
		setRegisterAreaEvent();
	}

	/*
		设置注册区域响应事件
	 */
	private void setRegisterAreaEvent() {
		reg_phone = (EditText) findViewById(R.id.reg_phone);
		reg_regioncode_edit = (EditText) findViewById(R.id.reg_regioncode_edit);
		reg_regphone_frame = (LinearLayout) findViewById(R.id.reg_regphone_frame);
		reg_phone.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				reg_regioncode_edit
						.setBackgroundResource(R.drawable.input_bar_bg_active);
				reg_regphone_frame
						.setBackgroundResource(R.drawable.input_bar_bg_active);
				if (reg_phone.hasFocus() == false) {
					reg_regioncode_edit
							.setBackgroundResource(R.drawable.input_bar_bg_normal);
					reg_regphone_frame
							.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}
			}
		});

		reg_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				Editable phoneNumberEditable = reg_phone.getEditableText();

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

			}

			@Override
			public void afterTextChanged(Editable s) {
				nicknameStr = nickname.getText().toString();
				regPasswordStr = reg_password.getText().toString();
				if (s.length() != 0 && !(nicknameStr.isEmpty())
						&& !(regPasswordStr.isEmpty())) {
					reg_button
							.setBackgroundResource(R.drawable.loginandregister_loginbuttonstyle);
					reg_clear_regphone_button.setVisibility(View.VISIBLE);
				} else if (s.length() != 0) {
					reg_clear_regphone_button.setVisibility(View.VISIBLE);
				} else {
					reg_clear_regphone_button.setVisibility(View.GONE);
					reg_button
							.setBackgroundResource(R.drawable.mm_btn_green_disable);
				}

			}
		});

		reg_clear_regphone_button = (ImageView) findViewById(R.id.reg_clear_regphone_button);
		reg_clear_regphone_button
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						reg_phone.setText("");
					}
				});

		reg_showpassword_button = (ImageView) findViewById(R.id.reg_showpassword_button);
		reg_showpassword_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (showOrNot == 0) {
					// 显示密码
					showOrNot = 1;
					reg_password
							.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					reg_showpassword_button
							.setImageResource(R.drawable.login_showpassword_icon_activa);
				} else if (showOrNot == 1) {

					showOrNot = 0;
					reg_password.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					reg_showpassword_button
							.setImageResource(R.drawable.login_showpassword_icon);
				}

			}
		});

		reg_password = (EditText) findViewById(R.id.reg_password);
		reg_password.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				nicknameStr = nickname.getText().toString();
				regPhoneStr = reg_phone.getText().toString();

				// TODO Auto-generated method stub
				if (s.length() != 0 && !(nicknameStr.isEmpty())
						&& !(regPhoneStr.isEmpty())) {
					reg_button
							.setBackgroundResource(R.drawable.loginandregister_loginbuttonstyle);
					reg_showpassword_button.setVisibility(View.VISIBLE);
				} else if (s.length() != 0) {
					reg_showpassword_button.setVisibility(View.VISIBLE);

				} else {
					reg_showpassword_button.setVisibility(View.GONE);
					reg_button
							.setBackgroundResource(R.drawable.mm_btn_green_disable);
				}

			}
		});

		reg_password_title = (TextView) findViewById(R.id.reg_password_title);
		reg_password_frame = (LinearLayout) findViewById(R.id.reg_password_frame);
		reg_password.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				reg_password_title
						.setBackgroundResource(R.drawable.input_bar_bg_active);
				reg_password_frame
						.setBackgroundResource(R.drawable.input_bar_bg_active);
				if (reg_password.hasFocus() == false) {
					reg_password_title
							.setBackgroundResource(R.drawable.input_bar_bg_normal);
					reg_password_frame
							.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}
			}
		});

		nickname = (EditText) findViewById(R.id.nickname);
		nickname.addTextChangedListener(new TextWatcher() {

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
				regPasswordStr = reg_password.getText().toString();
				regPhoneStr = reg_phone.getText().toString();
				if (s.length() != 0 && !(regPasswordStr.isEmpty())
						&& !(regPhoneStr.isEmpty())) {
					reg_button
							.setBackgroundResource(R.drawable.loginandregister_loginbuttonstyle);
				} else {
					reg_button
							.setBackgroundResource(R.drawable.mm_btn_green_disable);
				}
			}
		});

		register_headicon = (ImageView) findViewById(R.id.register_headicon);
		register_headicon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PhoneRegisterActivity.this,
						SelectImageActivity.class);
				intent.putExtra("path", imageFilePath);
				intent.putExtra("w", 400);
				intent.putExtra("h", 400);
				startActivityForResult(intent, 123);

			}
		});
	}

	/*
		设置注册按钮响应事件
	 */
	private void setRegisterButtonEvent() {
		reg_button = (TextView) findViewById(R.id.reg_button);
		reg_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nicknameStr = nickname.getText().toString();
				regPasswordStr = reg_password.getText().toString();
				regPhoneStr = reg_phone.getText().toString();

				if (!(nicknameStr.isEmpty()) && !(regPasswordStr.isEmpty())
						&& !(regPhoneStr.isEmpty())
						&& (regPhoneStr.length() == 13)) {
					String[] temp = regPhoneStr.split(" ");
					regPhoneStr = temp[0] + temp[1] + temp[2];
					Log.d("this",regPhoneStr);
					requestToken();

				} else {
					return;
				}

			}
		});
	}

	CustomDialog waitDialog;

	void uploadFace(final String token) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (token == null) {
					requestAuth();
				} else {
					String uptoken = token;
					String key = IO.UNDEFINED_KEY;
					PutExtra extra = new PutExtra();
					extra.params = new HashMap<String, String>();
					extra.params.put("x:a", "");
					Uri uri = Uri.fromFile(new File(imageFilePath));
					IO.putFile(PhoneRegisterActivity.this, uptoken, key, uri, extra,
							new JSONObjectRet() {
								@Override
								public void onProcess(long current, long total) {

									StrUtil.showMsg(PhoneRegisterActivity.this,
											"上传" + current + "/" + total);
								}

								@Override
								public void onSuccess(JSONObject resp) {
									String hash = resp.optString("hash", "");
									String value = resp.optString("x:a", "");
									StrUtil.showMsg(PhoneRegisterActivity.this,
											"上传完成\n" + hash + "\n" + value);
									imageFileHash = hash;
									requestAuth();
								}

								@Override
								public void onFailure(Exception ex) {
									StrUtil.showMsg(PhoneRegisterActivity.this,
											"上传失败\n");
									imageFileHash = null;
									requestAuth();
								}
							});
				}
			}
		});
	}

	/*
		请求文件上传Token
	 */
	void requestToken() {
		//如果图片文件存在
		if (imageFilePath != null && new File(imageFilePath).isFile()
				&& imageFileHash == null) {

			Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST,
					null, RequestHelper.Type_PostParam, null, 0, true);
			task.addParam("method", "getFileCloudToken");
			SOApplication.getDownLoadManager().startTask(task);
			if (!waitDialog.isShowing()) {
				waitDialog.show();
			}
		} else {
			requestAuth();
		}
	}

	void requestAuth() {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "registerByPhone");// 页码
		task.addParam("phone", regPhoneStr);// 页码
		task.addParam("password", regPasswordStr);// 页码
		task.addParam("nickname", nicknameStr);// 页码
		task.addParam("country", "china");// 页码
		task.addParam("facehash", imageFileHash);// 页码
		//执行注册
		SOApplication.getDownLoadManager().startTask(task);

		if (!waitDialog.isShowing()) {
			waitDialog.show();
		}
	}

	/*
	 重载接口方法：处理注册请求
	 */
	@Override
	public void onFinish(Pdtask t) {
		//使用手机注册用户
		if (t.getParam("method").equals("registerByPhone")) {
			boolean isSucceed = false;
			String msg = "提交失败";

			JSONObject user = null;
			if (t.json != null) {
				JSONObject results = t.json.optJSONObject("results");
				if (results != null) {
					user = results.optJSONObject("user");
					int su = results.optInt("success");
					if (su == 1) {
						isSucceed = true;
					} else if (results.has("msg")) {
						msg = results.optString("msg");
					}
				}
			}

			if (isSucceed && user != null && user.has("mobilePhone")) {
				Intent intent = new Intent();
				intent.setClass(PhoneRegisterActivity.this,
						PhoneRegisterSmsCodeActivity.class);
				intent.putExtra("user", user.toString());
				startActivityForResult(intent, 556);
			} else {
				StrUtil.showMsg(this, msg);
			}
			waitDialog.dismiss();
		} else if (t.getParam("method").equals("getFileCloudToken")) {
			String token = null;
			if (t.json != null) {
				JSONObject results = t.json.optJSONObject("results");
				if (results != null && results.optInt("success", 0) == 1) {
					token = results.optString("token", null);
				}
			}
			uploadFace(token);
		}

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
