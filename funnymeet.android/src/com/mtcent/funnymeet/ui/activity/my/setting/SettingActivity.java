package com.mtcent.funnymeet.ui.activity.my.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.MainActivity;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;

import mtcent.funnymeet.R;

public class SettingActivity extends Activity {
	public static final int ID = SettingActivity.class.hashCode();	
	TextView titleTextView;
	LinearLayout about;// 关于趣聚
	LinearLayout myaccountsetting;// 我的账号设置
	LinearLayout setting_exit;
	CustomDialog dialog;
	LinearLayout setting_click_exit;
	LinearLayout setting_click_logout;

	LinearLayout setting_version;
	TextView currentVersionType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);// 默认登录界面
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
		about = (LinearLayout) findViewById(R.id.about);
		about.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this,
						AboutActivity.class);
				startActivityForResult(intent, 256);
			}
		});
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("设置");

		myaccountsetting = (LinearLayout) findViewById(R.id.myaccountsetting);
		myaccountsetting.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this,
						SettingMyAccountSettingActivity.class);
				startActivityForResult(intent, 24);
			}
		});


		currentVersionType = (TextView)findViewById(R.id.current_version_type);
		currentVersionType.setText(Constants.CURRENT_VERSION_TYPE);

		setting_version = (LinearLayout)findViewById(R.id.setting_version);

		/**
		 * 切换版本 屏蔽掉
		 */
		/*setting_version.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (dialog == null) {
					dialog = new CustomDialog(SettingActivity.this);
					dialog.setContentView(R.layout.setting_switch_version);
					dialog.setCancelable(true);
					//设置开发版选项事件响应
					LinearLayout setting_version_dev =  (LinearLayout)dialog.findViewById(R.id.setting_version_dev);
					setting_version_dev.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							dialog.dismiss();
							//设置后台连接为开发版
							Constants.switchVersionToDev();
							setResult(MainActivity.NeedLogin);
							finish();
						}
					});
					//设置内测版选项事件响应
					LinearLayout setting_version_alpha =  (LinearLayout)dialog.findViewById(R.id.setting_version_alpha);
					setting_version_alpha.setOnClickListener(new View.OnClickListener(){
						public void onClick(View v){
							dialog.dismiss();
							//设置后台连接为测试版
							Constants.switchVersionToAlpha();
							setResult(MainActivity.NeedLogin);
							finish();
						}
					});
				}
				dialog.show();

			}
		});*/


		setting_exit = (LinearLayout) findViewById(R.id.setting_exit);
		setting_exit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (dialog == null) {
					dialog = new CustomDialog(SettingActivity.this);
					dialog.setContentView(R.layout.setting_exit_dialog);
					dialog.setCancelable(true);
					dialogfunction();
				}
				dialog.show();

			}
		});
	}

	protected void dialogfunction() {
		setting_click_exit = (LinearLayout) dialog
				.findViewById(R.id.setting_click_exit);
		setting_click_logout = (LinearLayout) dialog
				.findViewById(R.id.setting_click_logout);
		setting_click_exit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				setResult(MainActivity.Exit);
				finish();
			}
		});
		setting_click_logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.hide();
				UserMangerHelper.cleanDefaultUserLogin();
				setResult(MainActivity.NeedLogin);
				finish();
			}
		});

	}

}
