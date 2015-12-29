package com.mtcent.funnymeet.ui.activity.club;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.util.SearchUtilStack;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.activity.my.clubconsole.MyClubManagementActivity;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog.OnConfirmListern;
import com.mtcent.funnymeet.ui.view.control.CustomImageView;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import mtcent.funnymeet.R;

public class ClubInfoActivity extends BaseActivity {

	public static String EXTRA_KEY_CLUB_GUID = "EXTRA_KEY_CLUB_GUID";
	//0 已申请待审核
	public static int USER_STATEID_IN_CLUB_INREVIEW = 0;
	// public static String EXTRA_KEY_CLUB_USER_INFO =
	// "EXTRA_KEY_CLUB_USER_INFO";
	public static String EXTRA_KEY_CLUB_USER_INFO_USER_JOINED_CLUB = "EXTRA_KEY_CLUB_USER_INFO_USER_JOINED_CLUB";
	public static String EXTRA_KEY_CLUB_USER_INFO_USER_FOCUSED_CLUB = "EXTRA_KEY_CLUB_USER_INFO_USER_FOCUSED_CLUB";
	public static String EXTRA_KEY_CLUB_USER_INFO_CLUB_JSON_STRING = "EXTRA_KEY_CLUB_USER_INFO_CLUB_JSON_STRING";

	Intent get_intent;
	TextView club_title;
	TextView club_brief;
	CustomImageView club_icon;
	String icon;
	String brief;
	String title;
	TextView exit_club;
	TextView apply_takepartin_club;
	private TextView mTitle;
	LinearLayout club_info_view_homepage;

	ToggleButton mTogBtn;
	
	private TextView mInChecking;

	/**
	 * 俱乐部编号
	 */
	private String mClubGuid;

	/**
	 * 俱乐部对象
	 */
	private JSONObject mClub;

	/**
	 * 俱乐部成员
	 */
	private JSONObject mClubMember;

	/**
	 * 当前用户是否已经加入俱乐部 1:画面初始化时从系统中查询
	 */
	private boolean isUserJoinedClub = false;
	private int mUserStateIdInClud = -1;

	/**
	 * 当前用户是否关注俱乐部
	 */
	private boolean isUserFocusedClub = false;
	private LinearLayout mUpdateClubNick;

	// private CustomDialog updateClubNickNameDialog = null;
	private TextView mTVClubNickName;
	private String userClubNickName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.club_clubinfo);
		SearchUtilStack.searchUtilStack.add(this);
		init();
		requestData();
	}

	/**
	 * 从Server端取得信息
	 */
	private void requestData() {
		requestClubMemberInf();
		requestJoinedClubInf();
		requestFocusedClubInf();
	}

	/**
	 * 从Server端取得俱乐部信息
	 */
	private void requestClubMemberInf() {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "findClubMemberByGuid");
		task.addParam("club_guid", mClubGuid);
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	/**
	 * 从Server端取得信息，判断用户是否加入俱乐部
	 */
	private void requestJoinedClubInf() {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "getUserStateIdInClub");
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("club_guid", mClubGuid);

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	/**
	 * 从Server端取得信息，判断用户是否关注俱乐部
	 */
	private void requestFocusedClubInf() {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "isUserFocusedClub");
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("club_guid", mClubGuid);

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	void focusOnClub(String command) {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);

		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());
		task.addParam("club_guid", this.mClubGuid);

		if (command.equals("focus")) {

			task.addParam("method", "favorClub");

		} else if (command.equals("unfocus")) {

			task.addParam("method", "unfavorClub");

		}

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	@Override
	public void onFinish(Pdtask t) {
		boolean succ = false;
		boolean closeActivity = false;
		if (t.getParam("method").equals("favorClub")
				|| t.getParam("method").equals("unfavorClub")) {
			JSONObject result = null;
			if (t.json != null) {
				result = t.json.optJSONObject("results");
				if (result != null) {
					succ = true;
				}
			}
		} else if (t.getParam("method").equals("signPublicClub")) {
			if (t.json != null) {
				succ = true;
				closeActivity = true;
			}
		} else if (t.getParam("method").equals("quitClub")) {
			if (t.json != null) {
				succ = true;
				closeActivity = true;
				SOApplication.setClubUpdated(true);
			}
		} else if (t.getParam("method").equals("findClubByGuid")) {
			if (t.json != null) {
				succ = true;
				mClub = t.json.optJSONObject("results");
				updateClubInf();
			}
		} else if (t.getParam("method").equals("getUserStateIdInClub")) {
			if (t.json != null) {
				succ = true;
				int jsonSuccess = t.json.optJSONObject("results")
						.optInt("success");
				this.mUserStateIdInClud = t.json.optJSONObject("results")
						.optInt("intExtra");
				this.isUserJoinedClub = jsonSuccess == Constants.FIND_RESULT_EXIST && mUserStateIdInClud == 1;
				updateJoinedClubInf();
			}
		} else if (t.getParam("method").equals("isUserFocusedClub")) {
			if (t.json != null) {
				succ = true;
//				this.isUserFocusedClub = (t.json.optJSONObject("results")
//						.optInt("success") == Constants.FIND_RESULT_EXIST);
//				updateFocusedClubInf();
			}
		} else if ("setUserClubNickName".equals(t.getParam("method"))) {
			if (t.json != null) {
				JSONObject result = t.json.optJSONObject("results");
				if (result != null) {
					if (result.optInt("success") == 1) {
						userClubNickName = result.optString("stringExtra");
						updateUserClubNickName();
					}
				}

			}
		} else if ("findClubMemberByGuid".equals(t.getParam("method"))) {
			if (t.json != null) {
				JSONObject result = t.json.optJSONObject("results");
				if (result != null) {
					this.mClubMember = result;
					userClubNickName = mClubMember.optString("nickname");
					updateUserClubNickName();
				}

			}
		}

		if (succ) {
			if (t.json.optJSONObject("results") == null) {
				hideWait();
				StrUtil.showMsg(ClubInfoActivity.this, "此俱乐部已经关闭");
				quitClub();
				finish();
			}
			// StrUtil.showMsg(ClubInfoActivity.this,
			// t.json.optJSONObject("results").optString("message"));

			if (closeActivity) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						for (Iterator<Activity> iter = SearchUtilStack.searchUtilStack
								.iterator(); iter.hasNext();) {
							iter.next().finish();
						}
					}
				});
			}

		}
		hideWait();
		super.onFinish(t);
	}

	private void updateUserClubNickName() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (userClubNickName == null) {
					mTVClubNickName.setText("尚未加入该俱乐部");
					mTitle.setText("查看俱乐部信息");
				} else {
					mTitle.setText("会员设置");
					mTVClubNickName.setText(userClubNickName);
				}

				// mTVClubNickName.setText(userClubNickName);
			}
		});
	}

	/**
	 * 显示用户是否关注俱乐部
	 * 
	 * @param isUserFocusedClub2
	 */
	private void updateFocusedClubInf() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (ClubInfoActivity.this.isUserFocusedClub) {
					mTogBtn.setChecked(true);
				} else {
					mTogBtn.setChecked(false);
				}
			}
			
		});
	}

	/**
	 * 显示用户是否已经加入俱乐部
	 * 
	 * @param isUserJoinedClub2
	 */
	private void updateJoinedClubInf() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (isUserJoinedClub) {
					apply_takepartin_club.setVisibility(View.GONE);
					mInChecking.setVisibility(View.GONE);
					exit_club.setVisibility(View.VISIBLE);
					return;
				}
				if (mUserStateIdInClud == USER_STATEID_IN_CLUB_INREVIEW) {
					apply_takepartin_club.setVisibility(View.GONE);
					exit_club.setVisibility(View.GONE);
					mInChecking.setVisibility(View.VISIBLE);
					return;
				}
				exit_club.setVisibility(View.GONE);
				mInChecking.setVisibility(View.GONE);
				apply_takepartin_club.setVisibility(View.VISIBLE);
			}

		});
	}

	private void updateClubInf() {
		if (this.mClub == null) {
			return;
		} else {
			title = mClub.optString("name");
		}
		club_title.setText(mClub.optString("name"));
		// club_title.setText("会员设置");
		club_brief.setText(mClub.optString("name"));
		club_icon.setImageUrl(mClub.optString("logoUrl"));
		// userClubNickName = mClub.optString("name");
		// TextView tv = (TextView) findViewById(R.id.titleTextView);
		// tv.setText(title);
		TextView tvClubId = (TextView) findViewById(R.id.club_info_club_id);
		tvClubId.setText("俱乐部号：" + mClub.optString("id"));
		if (this.userClubNickName == null) {
			mTVClubNickName.setText("尚未加入该俱乐部");
			// mTitle.setText("查看俱乐部信息");
		} else {
			mTitle.setText("会员设置");
			mTVClubNickName.setText(this.userClubNickName);
		}
	}

	// 退出俱乐部
	void quitClub() {

		// quitClub&user_guid=*&user_session_guid=*&....
		// String[] keys = { "club_guid" };
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "quitClub");// 页码
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());
		task.addParam("club_guid", this.mClubGuid);

		SOApplication.getDownLoadManager().startTask(task);
		showWait();

	}

	// 申请加入公众俱乐部（直接加入）
	void signPublicClub() {

		// signPublicClub&user_guid=*&user_session_guid=*&....
		// String[] keys = { "club_guid" };
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "signPublicClub");// 页码
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());
		task.addParam("club_guid", this.mClubGuid);

		SOApplication.getDownLoadManager().startTask(task);
		showWait();

	}

	/**
	 * 申请加入会员俱乐部（需审核）
	 */
	void signPublicClubInView() {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "signPublicClubInView");
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());
		task.addParam("club_guid", this.mClubGuid);

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	protected void init() {
		// 设置俱乐部昵称
		setUpdateNickHandler();
		mTitle = (TextView) findViewById(R.id.titleTextView);

		mTogBtn = (ToggleButton) findViewById(R.id.mTogBtn);
		this.mInChecking = (TextView) findViewById(R.id.inchecking);
		//

		mTogBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					focusOnClub("focus");
				} else {
					focusOnClub("unfocus");
				}
			}
		});

		exit_club = (TextView) findViewById(R.id.exit_club);
		apply_takepartin_club = (TextView) findViewById(R.id.apply_takepartin_club);

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		get_intent = this.getIntent();

		// 申请加入俱乐部
		apply_takepartin_club.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int typeId = mClub.optInt("typeId");
				if (typeId == Constants.CLUB_TYPE_ID_PUBLIC) {
					signPublicClub();
				} else {
					signPublicClubInView();
				}
			}

		});
		club_info_view_homepage = (LinearLayout) findViewById(R.id.club_info_view_homepage);
		club_info_view_homepage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(
						MyClubManagementActivity.EXTRA_PARAM_JSONOBJECT,
						mClub.toString());
				//
				intent.putExtra(MyClubManagementActivity.EXTRA_PARAM_CLUB_TYPE,
						MyClubManagementActivity.CLUB_MANAGER_TYPE_USER);
				intent.setClass(mActivity, MyClubManagementActivity.class);
				mActivity.startActivity(intent);
			}
		});

		// 退出俱乐部
		exit_club.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				quitClub();
			}
		});

		club_title = (TextView) findViewById(R.id.club_title);
		club_brief = (TextView) findViewById(R.id.club_brief);
		club_icon = (CustomImageView) findViewById(R.id.club_icon);
		mTVClubNickName = (TextView) findViewById(R.id.club_nickname);

		if (get_intent != null) {
			mClubGuid = get_intent.getStringExtra(EXTRA_KEY_CLUB_GUID);
			isUserFocusedClub = get_intent.getBooleanExtra(
					EXTRA_KEY_CLUB_USER_INFO_USER_FOCUSED_CLUB, false);
			isUserJoinedClub = get_intent.getBooleanExtra(
					EXTRA_KEY_CLUB_USER_INFO_USER_JOINED_CLUB, false);
			try {
				mClub = new JSONObject(
						get_intent
								.getStringExtra(EXTRA_KEY_CLUB_USER_INFO_CLUB_JSON_STRING));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			updateJoinedClubInf();
			updateFocusedClubInf();
			updateClubInf();
		}

	}

	@SuppressLint("InflateParams")
	private void setUpdateNickHandler() {

		mUpdateClubNick = (LinearLayout) findViewById(R.id.update_club_nickname);
		mUpdateClubNick.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CustomDialog.createConfirmDialog(ClubInfoActivity.this,
						"请输入在本俱乐部的昵称", true, new OnConfirmListern() {

							@Override
							public void onConfirm(String nickName) {
								if (nickName != null
										&& nickName.trim().length() > 0) {
									Pdtask task = new Pdtask(mActivity,
											mActivity, Constants.SERVICE_HOST,
											null,
											RequestHelper.Type_PostParam,
											null, 0, true);
									task.addParam("method",
											"setUserClubNickName");
									task.addParam("user_guid", UserMangerHelper
											.getDefaultUserGuid());
									task.addParam("club_guid",
											ClubInfoActivity.this.mClubGuid);

									task.addParam("user_club_nickname",
											nickName);

									SOApplication.getDownLoadManager()
											.startTask(task);
									showWait();
								} else {

									StrUtil.showMsg(mActivity,
											"由于没有输入有效昵称,当前的群昵称不会改变");
								}
							}

							@Override
							public void onCancle() {
								//
							}
						}).show();
			}

		});
	}
}
