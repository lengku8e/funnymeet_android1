package com.mtcent.funnymeet.ui.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.MainActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;

import org.json.JSONObject;

import mtcent.funnymeet.R;

public class StartupActivity extends Activity implements DownBack {
	public static final int ID = 123;

	TextView loginButton;// 登录按钮
	TextView registerButton;// 注册按钮
	LinearLayout sohuodongLogo;
	View loginandregisterbuttonframe;
	int waitFinishNum = 0;
	int result = RESULT_FIRST_USER;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startup_main);// 闪屏之后第一个界面
		init();
	}

	void requestData() {

		if (UserMangerHelper.isDefaultUserLogin()
				&& !UserMangerHelper.isDefaultUserChange()) {
			Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST,
					null, RequestHelper.Type_PostParam, null, 0, true);
			task.addParam("method", "getUserInfoByGuid");
			task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
			task.addParam("user_session_guid",
					UserMangerHelper.getDefaultUserLongsession());

			waitFinishNum++;
			SOApplication.getDownLoadManager().startTask(task);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if ((requestCode == 123 || requestCode == 234)
				&& resultCode == RESULT_FIRST_USER) {
			setResult(RESULT_FIRST_USER);
			finish();
		}

	}

	protected void init() {

		sohuodongLogo = (LinearLayout) findViewById(R.id.sohuodongLogo);
		loginButton = (TextView) findViewById(R.id.login_main_loginbutton);// 初始化登录按钮
		registerButton = (TextView) findViewById(R.id.login_main_regbutton);// 初始化注册按钮
		loginandregisterbuttonframe = findViewById(R.id.loginandregisterbuttonframe);
		loginandregisterbuttonframe.setVisibility(View.INVISIBLE);
		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();

				intent.setClass(StartupActivity.this,
						DefaultLoginActivity.class);
				startActivityForResult(intent, 123);

			}
		});

		registerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();

				intent.setClass(StartupActivity.this,
						PhoneRegisterActivity.class);
				startActivityForResult(intent, 234);

			}
		});

		// 2014年12月3日 10:30:13 拆分逻辑
		if (UserMangerHelper.isDefaultUserLogin()) {
			justLogoAnimation();
			requestData();
		} else {
			loginFrameAndLogoAnimation();
		}

	}

	// 拥有登录身份时 不显示登录按钮只显示logo动画
	public void justLogoAnimation() {
		waitFinishNum++;
		Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(1000);
		alphaAnimation.setFillAfter(true);
		loginandregisterbuttonframe.setVisibility(View.INVISIBLE);
		sohuodongLogo.startAnimation(alphaAnimation);

		alphaAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				toNextIfNeed();
			}
		});

	}

	// 没有登录身份时 显示logo和登录按钮的渐隐动画
	public void loginFrameAndLogoAnimation() {

		Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(1500);
		alphaAnimation.setFillAfter(true);
		loginandregisterbuttonframe.startAnimation(alphaAnimation);
		sohuodongLogo.startAnimation(alphaAnimation);

	}

	@Override
	public void onFinish(Pdtask t) {

		if (t.getParam("method").equals("getUserInfoByGuid")) {
			boolean succ = false;
			String msg = "获取个人信息失败";
			JSONObject user = null;
			if (t.json != null) {
				JSONObject results = t.json.optJSONObject("results");
				if (results != null) {
					user = results.optJSONObject("user");
					int su = results.optInt("success");
					if (su == 1) {
						succ = true;
					} else
					// if (results.has("err"))
					{
						// msg = results.optString("msg");
						// UserMangerHelper.cleanDefaultUserLogin();
						// result = SOMainActivity.NeedLogin;
					}
				}
			}

			if (succ && user != null && user.has("mobilePhone")) {
				result = RESULT_FIRST_USER;
				UserMangerHelper.saveDefaultUser(user);
				UserMangerHelper.setDefaultUserChange(false);
				//
				Intent intent = new Intent();  
	            intent.setAction(MainActivity.INTENT_ACTION_USER_LOGIN);  
	            sendBroadcast(intent);  
			}
		}

		toNextIfNeed();
	}

	void toNextIfNeed() {
		waitFinishNum--;
		if (waitFinishNum <= 0) {
			setResult(result);
			finish();
		}
	}

	@Override
	public void onUpdate(Pdtask t) {

	}
}
