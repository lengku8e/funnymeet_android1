package com.mtcent.funnymeet.ui.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtcent.funnymeet.util.BitmapUtil;
import com.mtcent.funnymeet.util.StrUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;

import mtcent.funnymeet.R;

public class OthersLoginActivity extends Activity {

	EditText login_others_mailbox; // 邮箱
	EditText login_others_password; // 密码
	TextView login_others_login_button;// 第三方登录
	TextView login_problem;// 登录遇到问题
	TextView login_others_password_title;// 登录密码title
	ImageView login_others_clear_password;// 清除输入的密码
	TextView titleTextView;
	ImageView others_weixin_login;
	private static final String APP_ID = "wx8e27041fdd4dfeba";
	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginandregister_login_others);
		init();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 345 && resultCode == RESULT_FIRST_USER) {
			setResult(RESULT_FIRST_USER);
			finish();
		}

	}

	public boolean shareToTencentWeiXin(String title, String summar,
			String targetUrl, String imageUrl) {
		if (api != null) {
			if (api.isWXAppInstalled() == false) {
				return false;
			}
			WXMediaMessage localWXMediaMessage = new WXMediaMessage();

			localWXMediaMessage.title = title;
			localWXMediaMessage.description = summar;

			Bitmap bitmap = BitmapUtil.getThumbImage(imageUrl, 480);
			if (bitmap != null) {
				localWXMediaMessage.setThumbImage(bitmap);
			}

			com.tencent.mm.sdk.openapi.WXWebpageObject localWXWebpageObject = new com.tencent.mm.sdk.openapi.WXWebpageObject();
			localWXWebpageObject.webpageUrl = targetUrl;
			localWXMediaMessage.mediaObject = localWXWebpageObject;

			SendMessageToWX.Req localReq = new SendMessageToWX.Req();
			localReq.transaction = StrUtil.buildTransaction("text");
			localReq.message = localWXMediaMessage;
			localReq.scene = SendMessageToWX.Req.WXSceneSession;// 对话框
			api.sendReq(localReq);
		}
		return true;
	}

	private void regToWx() {

		api = WXAPIFactory.createWXAPI(this, APP_ID, true);
		api.registerApp(APP_ID);

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
		titleTextView.setText("登录趣聚");
		login_others_mailbox = (EditText) findViewById(R.id.login_others_mailbox);
		login_others_password = (EditText) findViewById(R.id.login_others_password);
		login_others_login_button = (TextView) findViewById(R.id.login_others_login_button);
		login_problem = (TextView) findViewById(R.id.login_problem);
		login_others_password_title = (TextView) findViewById(R.id.login_others_password_title);
		login_others_clear_password = (ImageView) findViewById(R.id.login_others_clear_password);
		others_weixin_login = (ImageView) findViewById(R.id.others_weixin_login);

		others_weixin_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				regToWx();
				// TODO Auto-generated method stub
				final SendAuth.Req req = new SendAuth.Req();
				req.scope = "snsapi_userinfo";
				req.state = "wechat_sdk_demo_test";
				api.sendReq(req);
				// shareToTencentWeiXin("test", "testText",
				// "http://www.baidu.com",
				// "http://www.baidu.com/img/bdlogo.png");
			}
		});

		login_others_mailbox.addTextChangedListener(new TextWatcher() {

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
				// TODO Auto-generated method stub
				if (s.length() != 0) {
					login_others_login_button
							.setBackgroundResource(R.drawable.loginandregister_loginbuttonstyle);
				} else {
					login_others_login_button
							.setBackgroundResource(R.drawable.mm_btn_green_disable);
				}
			}
		});

		login_others_login_button
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// Intent intent = new Intent();
						// intent.setClass(OthersLoginActivity.this,
						// SOMainActivity.class);
						// startActivityForResult(intent, 1234);
						setResult(RESULT_FIRST_USER);
						finish();
					}
				});

		login_others_password
				.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						login_others_password_title
								.setBackgroundResource(R.drawable.input_bar_bg_active);

						if (login_others_password.hasFocus() == false) {
							login_others_password_title
									.setBackgroundResource(R.drawable.input_bar_bg_normal);
						}
					}

				});

		login_others_password.addTextChangedListener(new TextWatcher() {

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

					login_others_clear_password.setVisibility(View.VISIBLE);

				} else {

					login_others_clear_password.setVisibility(View.GONE);
				}
			}
		});

		login_others_clear_password
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						login_others_password.setText("");
					}
				});
	}
}
