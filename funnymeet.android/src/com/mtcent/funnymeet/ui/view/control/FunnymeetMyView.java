package com.mtcent.funnymeet.ui.view.control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.my.clubconsole.MyClubListActivity;
import com.mtcent.funnymeet.ui.activity.my.myinterest.MyInterestActivity;
import com.mtcent.funnymeet.ui.activity.my.myprofile.MyInformationActivity;
import com.mtcent.funnymeet.ui.activity.project.SuggestActivity;
import com.mtcent.funnymeet.ui.activity.message.MessageListActivity;
import com.mtcent.funnymeet.ui.activity.my.setting.SettingActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONObject;

import mtcent.funnymeet.R;

public class FunnymeetMyView extends FunnymeetBaseView {

	private LinearLayout mMyAccountLayout;
	private LinearLayout mMyInterestLayout;
	private LinearLayout mMyManageClubLayout;
	private LinearLayout mMyMessageLayout;
	private LinearLayout mMySettingLayout;
	private LinearLayout mMyFeedbackLayout;

	private JSONObject user = new JSONObject();
	private XVURLImageView mFaceImageView;
	private TextView mNickName;
	private TextView mAccount_name;

	@SuppressLint("InflateParams")
	public FunnymeetMyView(Activity activity) {
		super(activity);
		mainView = inflater.inflate(R.layout.somain_my, null);
		init();
		requestData();
	}

	private void init() {
		//账号信息
		mMyAccountLayout = (LinearLayout) mainView
				.findViewById(R.id.my_account);
		mMyAccountLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mActivity, MyInformationActivity.class);
				mActivity.startActivityForResult(intent,
						MyInformationActivity.ID);
			}
		});

		//
		mFaceImageView = (XVURLImageView) mainView
				.findViewById(R.id.my_faceImageView);
		mNickName = (TextView) mainView.findViewById(R.id.my_nickname);
		mAccount_name = (TextView) mainView.findViewById(R.id.my_account_name);
		
		//我的兴趣
		mMyInterestLayout = (LinearLayout) mainView
				.findViewById(R.id.my_interest_layout);
		mMyInterestLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mActivity, MyInterestActivity.class);
				mActivity.startActivityForResult(intent,
						MyInterestActivity.ID);
			}
		});

		//管理我的俱乐部
		mMyManageClubLayout = (LinearLayout) mainView
				.findViewById(R.id.my_manage_club_layout);
		mMyManageClubLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mActivity, MyClubListActivity.class);
				mActivity.startActivityForResult(intent,
						MyClubListActivity.ID);
			}
		});

		//消息
		mMyMessageLayout = (LinearLayout) mainView
				.findViewById(R.id.my_message_layout);
		mMyMessageLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mActivity, MessageListActivity.class);
				mActivity.startActivityForResult(intent,
						MessageListActivity.ID);
			}
		});

		//设置
		mMySettingLayout = (LinearLayout) mainView
				.findViewById(R.id.my_setting_layout);
		mMySettingLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mActivity, SettingActivity.class);
				mActivity.startActivityForResult(intent,
						SettingActivity.ID);
			}
		});

		//意见反馈
		mMyFeedbackLayout = (LinearLayout) mainView
				.findViewById(R.id.my_feedback_layout);
		mMyFeedbackLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mActivity, SuggestActivity.class);
				mActivity.startActivityForResult(intent,
						SuggestActivity.ID);
			}
		});
	} // end of initViewControl

	public void requestData() {

		if (!UserMangerHelper.isDefaultUserChange()) {
			Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST,
					null, RequestHelper.Type_PostParam, null, 0, true);
			task.addParam("method", "getUserInfoByGuid");
			task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
			task.addParam("user_session_guid",
					UserMangerHelper.getDefaultUserLongsession());

			SOApplication.getDownLoadManager().startTask(task);
			showWait();
		}
	}

	CustomDialog waitDialog = null;

	void showWait() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (waitDialog == null) {
					waitDialog = new CustomDialog(mActivity);
					waitDialog.setContentView(R.layout.dialog_wait);
				}
				waitDialog.show();
			}
		});
	}

	void hideWait() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				waitDialog.dismiss();
			}
		});
	}

	public void onShow() {
		resetView();
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
					} else if (results.has("msg")) {
						msg = results.optString("msg");
					}
				}
			}

			if (succ && user != null && user.has("mobilePhone")) {
				this.user = user;
				UserMangerHelper.saveDefaultUser(user);
				resetView();
			} else {
				StrUtil.showMsg(mActivity, msg);
			}
			hideWait();
		}

	}

	public void resetView() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				JSONObject user = UserMangerHelper.getDefaultUser();
				// 头像
				//String faceUrl = UserMangerHelper.getDefaultUserFaceUrl();
				String faceUrl = user.optString("faceUrl");
				if (faceUrl == null || faceUrl.length() == 0) {
					faceUrl = "local:defaultface.png";
				}
				mFaceImageView.setImageUrl(faceUrl);
				mNickName.setText(user.optString("nickname", null));
				String name = user.optString("accountName", null);
				if (name == null || name.isEmpty()) {
					name = "未设置";
				}
				mAccount_name.setText("用户号码：" + name);
			}
		});

	}
	
}
