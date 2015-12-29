package com.mtcent.funnymeet.ui.activity.my.myprofile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.discovery.search.SearchActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.ui.view.control.XVURLImageView;
import com.mtcent.funnymeet.util.BitmapUtil;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONException;
import org.json.JSONObject;

import mtcent.funnymeet.R;

public class MemberInformationActivity extends Activity implements DownBack {

	public static final String EXTRA_KEY_MEMBER_JSON = "EXTRA_KEY_MEMBER_JSON";

	public static final int ID = MemberInformationActivity.class.hashCode();

	private String mCurrentClubString;
	private String mCurrentClubGuid;
	JSONObject mCurrentClub;

	TextView titleTextView;
	Activity mActivity = this;
	JSONObject user = new JSONObject();
	XVURLImageView faceImageView;
	TextView nickName;
	TextView account_name;
	TextView myaddr;
	TextView sex;
	TextView area;
	TextView sign;
	TextView mTvRoleStatus;
	TextView mTvAssignAssitant;
	TextView thirdaccounts;
	String imageFilePath = "";
	String imageFileHash = null;
	LinearLayout nickModify;
	LinearLayout myaddrModify;
	LinearLayout signModify;
	LinearLayout sexModify;
	LinearLayout areaModify;
	LinearLayout assign;
	LinearLayout mRoleStatus;
	CustomDialog dialog;
	ImageView femaleSelected;
	ImageView maleSelected;
	boolean faceHasChange = false;
	int gender = 0;

	private String mRoleStatusText;
	private int mRoleId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_myinforamtion);// 默认登录界面

		init();
		requestData();
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
		titleTextView.setText("个人信息");

		// user = UserMangerHelper.getDefaultUser();
		Intent i = this.getIntent();
		if (i.hasExtra(EXTRA_KEY_MEMBER_JSON)) {
			try {
				user = new JSONObject(i.getStringExtra(EXTRA_KEY_MEMBER_JSON));
				this.mCurrentClubString = i
						.getStringExtra(SearchActivity.EXTRA_KEY_CURRENT_CLUB);
				this.mCurrentClubGuid = i
						.getStringExtra(SearchActivity.EXTRA_KEY_CURRENT_CLUB_GUID);
				this.mCurrentClub = new JSONObject(this.mCurrentClubString);
			} catch (JSONException e) {
				//
				return;
			}
		} else {
			return;
		}
		imageFilePath = BitmapUtil.getSaveBitmapFile(
				user.optString("faceUrl", "")).getAbsolutePath();
		faceImageView = (XVURLImageView) findViewById(R.id.faceImageView);
		nickName = (TextView) findViewById(R.id.nickName);

		account_name = (TextView) findViewById(R.id.account_name);
		myaddr = (TextView) findViewById(R.id.myaddr);
		sex = (TextView) findViewById(R.id.sex);
		area = (TextView) findViewById(R.id.area);
		sign = (TextView) findViewById(R.id.sign);
		thirdaccounts = (TextView) findViewById(R.id.thirdaccounts);

		assign = (LinearLayout) findViewById(R.id.assign);
		assign.setVisibility(View.VISIBLE);
		this.mTvAssignAssitant = (TextView) findViewById(R.id.assign_assistant);
		mTvAssignAssitant.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				assignAssistant();
			}
		});

		mRoleStatus = (LinearLayout) findViewById(R.id.roleIdStatus);
		mRoleStatus.setVisibility(View.VISIBLE);
		this.mTvRoleStatus = (TextView) findViewById(R.id.textviewRoleIdStatus);

		areaModify = (LinearLayout) findViewById(R.id.areaModify);

	}

	void assignAssistant() {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "setUserRoleIdForClub");
		task.addParam("user_guid", user.optString("guid", ""));
		task.addParam("club_guid", mCurrentClubGuid);
		task.addParam("role_id", "2");

		SOApplication.getDownLoadManager().startTask(task);
		showWait();

	}

	void resetView() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!faceHasChange) {
					String faceUrl = UserMangerHelper.getDefaultUserFaceUrl();
					if (faceUrl == null || faceUrl.length() == 0) {
						faceUrl = "local:defaultface.png";
					}
					faceImageView.setImageUrl(faceUrl);
				}

				nickName.setText(user.optString("nickname", null));
				String name = user.optString("accountName", "");
				if (name.isEmpty()) {
					name = "未设置";
				}
				account_name.setText(name);

				myaddr.setText(user.optString("address", null));

				sex.setText(user.optString("genderName", null));
				if (user.optString("province", "").equals(
						user.optString("city", null))) {
					area.setText(user.optString("city", null));
				} else {
					area.setText(user.optString("province", "") + " "
							+ user.optString("city", ""));
				}

				sign.setText(user.optString("privateSolgan", null));
				thirdaccounts.setText(user.optString("qq", null));
			}
		});

		String genderTmp = user.optString("genderName", "");
		if (genderTmp.equals("男")) {
			gender = 1;
		} else if (genderTmp.equals("女")) {
			gender = 2;
		} else {
			gender = 0;
		}
	}

	void setRoleStatus() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				MemberInformationActivity.this.mTvRoleStatus
						.setText(MemberInformationActivity.this.mRoleStatusText);
				if (MemberInformationActivity.this.mRoleId == -1
						|| MemberInformationActivity.this.mRoleId == 1) {
					MemberInformationActivity.this.mTvAssignAssitant
							.setFocusable(true);
				} else {
					MemberInformationActivity.this.mTvAssignAssitant
							.setFocusable(false);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// user = UserMangerHelper.getDefaultUser();
		resetView();
		super.onResume();
	}

	void requestData() {

		if (!UserMangerHelper.isDefaultUserChange()) {
			Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST,
					null, RequestHelper.Type_PostParam, null, 0, true);
			task.addParam("method", "getUserRoleInClub");
			task.addParam("user_guid", user.optString("guid", ""));
			task.addParam("club_guid", mCurrentClubGuid);

			SOApplication.getDownLoadManager().startTask(task);
			showWait();
		}
	}

	CustomDialog waitDialog = null;

	void showWait() {
		runOnUiThread(new Runnable() {
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
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				waitDialog.dismiss();
			}
		});
	}

	@Override
	public void onFinish(Pdtask t) {
		if ("getUserRoleInClub".equals(t.getParam("method"))) {
			if (t.json != null) {
				JSONObject results = t.json.optJSONObject("results");
				if (results != null) {
					MemberInformationActivity.this.mRoleId = results
							.optInt("intExtra");
					switch (MemberInformationActivity.this.mRoleId) {
					case -1:
						MemberInformationActivity.this.mRoleStatusText = "当前用户不是该俱乐部成员";
						break;
					case 0:
						MemberInformationActivity.this.mRoleStatusText = "当前用户是该俱乐部创始人";
						break;
					case 1:
						MemberInformationActivity.this.mRoleStatusText = "当前用户是该俱乐部会员";
						break;
					case 2:
						MemberInformationActivity.this.mRoleStatusText = "当前用户已经是该俱乐部助理";
						break;
					case 3:
						MemberInformationActivity.this.mRoleStatusText = "当前用户是该俱乐部管理员";
						break;
					}
				} else {
					//
					MemberInformationActivity.this.mRoleStatusText = "当前用户不是该俱乐部成员";
				}
			}

			setRoleStatus();
			hideWait();
		} else if ("setUserRoleIdForClub".equals(t.getParam("method"))) {
			JSONObject results = t.json.optJSONObject("results");
			if ("ok".equals(t.json.optString("status")) && results != null && "1".equals(results.optString("success"))) {
				StrUtil.showMsg(MemberInformationActivity.this,
						results.optString("message"));
			}
			hideWait();
		}

	}

	@Override
	public void onUpdate(Pdtask t) {

	}

}
