package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.model.ClubActivityList;
import com.mtcent.funnymeet.model.FakeClubHD;
import com.mtcent.funnymeet.model.FakeMemberInfo;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.util.SearchUtilStack;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.activity.club.ClubInfoActivity;
import com.mtcent.funnymeet.ui.activity.club.PersonInfoActivity;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.view.control.CustomImageView;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.view.control.IndexableListView;
import com.mtcent.funnymeet.ui.view.control.ScrollHPageWithTableView;
import com.mtcent.funnymeet.ui.view.control.ScrollHPageWithTableView.ScrollHPageWithTableAdapter;
import com.mtcent.funnymeet.util.StringMatcher;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.ui.view.control.XVURLImageView;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import mtcent.funnymeet.R;

@SuppressLint({ "InflateParams", "ClickableViewAccessibility",
		"SimpleDateFormat" })
public class MyClubManagementActivity extends BaseActivity implements
		ScrollHPageWithTableAdapter {
	/**
	 * 俱乐部区分--管理者
	 */
	public static final String CLUB_MANAGER_TYPE_MANAGER = "CLUB_MANAGER_TYPE_MANAGER";

	/**
	 * 俱乐部区分--普通用户
	 */
	public static final String CLUB_MANAGER_TYPE_USER = "CLUB_MANAGER_TYPE_USER";

	/**
	 * Intent中传递过来的JSON对象
	 */
	public static final String EXTRA_PARAM_JSONOBJECT = "jsonobject";

	/**
	 * Intent中传递过来的俱乐部区分
	 */
	public static final String EXTRA_PARAM_CLUB_TYPE = "EXTRA_PARAM_CLUB_TYPE";

	/**
	 * 俱乐部区分
	 */
	private String mManageType = CLUB_MANAGER_TYPE_MANAGER;

	// 1 筹备 2 报名 3 进行中 4 结束 5 暂停 6 终止
	public static final String HD_STATUS_STARTING = "[筹备中]";
	public static final String HD_STATUS_JOINING = "[报名中]";
	public static final String HD_STATUS_PROCESSING = "[进行中]";
	public static final String HD_STATUS_CLOSED = "[已结束]";
	public static final String HD_STATUS_PAUSE = "[已暂停]";
	public static final String HD_STATUS_ABORTED = "[已中止]";

	private String mClubGuid;

	ScrollHPageWithTableView scrollHPageWithTableView;
	PullToRefreshListView mPullToRefreshListView;
	LinearLayout refreshListViewFrame;
	ListView club_hd_listview;
	IndexableListView clubmemberlist;
	LinearLayout addNewMember;
	RelativeLayout manage_member;
	ListView my_clubs_manage_member;
	LayoutInflater inflater;
	MyIndexableListViewAdapter adapter;
	LinearLayout search;
	ArrayList<FakeMemberInfo> fakeUncheckedMemberList;
	CustomDialog LongClickDialog = null;
	LinearLayout my_clubs_member_dialog_frame;
	LinearLayout my_clubs_admin_dialog_frame;
	LinearLayout club_setting_button;
	TextView titleTextView;
	String iconUrl;
	String clubName;
	CustomDialog clubHDLongClick;
	JSONArray clubHdList;
	JSONArray clubManagerList;
	ClubHDListViewAdapter club_hd_listviewAdapter;
	LinearLayout addressBookRightList;
	TextView addressbook_index;
	AlphaAnimation letterAnimation;
	View headerView;
	LinearLayout starMemberList;
	LinearLayout createClubHd;
	LinearLayout my_clubs_hd_dialog_frame;
	private LinearLayout my_clubs_assign_assitant;
	private LinearLayout my_clubs_check_member;
	private LinearLayout my_clubs_invite_member;
	// private LinearLayout my_clubs_generate_invitelink;
	// private TextView member_seperator0;
	private TextView member_seperator1;
	private TextView member_seperator2;
	private TextView member_seperator3;

	TextView my_club_hd_longclick_close;
	TextView my_club_hd_longclick_pause;
	TextView my_club_hd_longclick_modify;

	JSONObject currentLongClickedProject;

	int istodayColor = Color.WHITE;

	Intent get_intent;
	private Vibrator vibrator;
	JSONObject clubInfoJson;
	JSONArray clubAddedMemberList;

	private ClubUserInfo mClubUserInfo = new ClubUserInfo();

	public static final int TO_MYCLUBDETAILACTIVITY = 1445;
	public static final int TO_ORGANISEHDACTIVITY = 1549;
	public static final int CLOSE_CLUB_REFRESH_CLUBLIST = 1641;
	public static final int CLOSE_CLUBMANAGEMENT_TO_CLUBLIST = 1145;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_clubs_club_management);
		SearchUtilStack.searchUtilStack.add(this);
		init();
		requestData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TO_MYCLUBDETAILACTIVITY) {
			if (resultCode == MyClubDetailActivity.EXIT_CLUB_CODE) {
				setResult(CLOSE_CLUB_REFRESH_CLUBLIST);
				finish();
			} else if (resultCode == MyClubDetailActivity.CLOSE_CLUB_CODE) {
				setResult(CLOSE_CLUB_REFRESH_CLUBLIST);
				finish();
			}
		}

		if (requestCode == TO_ORGANISEHDACTIVITY) {
			if (resultCode == MyClubNewProjectActivity.CREATE_CLUBHD_COMPLETED) {
				requestData();
			}
		}

	}

	public void onResume() {
		super.onResume();
		requestData();
	}

	void showMemberLongClickDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				LongClickDialog.show();
			}
		});
	}

	void hideMemberLongClickDialog() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				LongClickDialog.hide();
			}
		});
	}

	void showClubHDLongClickDialog(JSONObject projectJson) {

		currentLongClickedProject = projectJson;
		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				clubHDLongClick.show();
			}
		});
	}

	void hideClubHDLongClickDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				clubHDLongClick.hide();
			}
		});
	}

	@Override
	public void onFinish(Pdtask t) {
		boolean succ = false;
		if (t.getParam("method").equals("listClubProject")) {

			if (t.json != null) {

				if (t.json.optJSONArray("results") != null
						&& t.json.optString("status").equals("ok")) {
					clubHdList = t.json.optJSONArray("results");
					succ = true;
				}

			}

		} else if (t.getParam("method").equals("listValidClubMember")) {

			if (t.json != null) {

				if (t.json.optJSONArray("results") != null
						&& t.json.optString("status").equals("ok")) {
					clubAddedMemberList = t.json.optJSONArray("results");
					succ = true;
				}

			}
		} else if (t.getParam("method").equals("pauseProject")) {
			if (t.json != null) {
				JSONObject resultJson = t.json.optJSONObject("results");
				if (resultJson.optString("success").equals("1")) {
					hideWait();
					hideClubHDLongClickDialog();
					StrUtil.showMsg(MyClubManagementActivity.this,
							resultJson.optString("message"));
				}
			}
		} else if (t.getParam("method").equals("stopProject")) {
			if (t.json != null) {
				JSONObject resultJson = t.json.optJSONObject("results");
				if (resultJson.optString("success").equals("1")) {
					hideWait();
					hideClubHDLongClickDialog();
					StrUtil.showMsg(MyClubManagementActivity.this,
							resultJson.optString("message"));
				}
			}
		} else if (t.getParam("method").equals("resumeProject")) {
			if (t.json != null) {
				JSONObject resultJson = t.json.optJSONObject("results");
				if (resultJson.optString("success").equals("1")) {
					hideWait();
					hideClubHDLongClickDialog();
					StrUtil.showMsg(MyClubManagementActivity.this,
							resultJson.optString("message"));
				}
			}
		} else if (t.getParam("method").equals("listValidClubManager")) {
			if (t.json != null) {
				JSONArray resultJson = t.json.optJSONArray("results");
				if (resultJson != null) {
					hideWait();
					clubManagerList = resultJson;
					String userGuid = UserMangerHelper.getDefaultUserGuid();
					// boolean isManager = false;
					for (int i = 0; i < resultJson.length(); i++) {
						try {
							JSONObject o = (JSONObject) resultJson.get(i);
							String managerGuid = o.optString("userGuid");
							if (userGuid != null
									&& userGuid.equals(managerGuid)) {
								// isManager = true;
							}
						} catch (JSONException e) {
							// do nothing
						}
					}
					if (CLUB_MANAGER_TYPE_USER.equals(this.mManageType)) {
						setViewContent("updateSettingClick");
					}
					succ = true;
				}
			}
		} else if (t.getParam("method").equals("findClubByGuid")) {
			if (t.json != null) {
				succ = true;
				mClubUserInfo.setClubInfoJson(t.json.optJSONObject("results"));
				// updateClubInf();
			}
		} else if (t.getParam("method").equals("isUserJoinedClub")) {
			if (t.json != null) {
				succ = true;
				mClubUserInfo.setUserJoinedClub(t.json.optJSONObject("results")
						.optInt("success") == Constants.FIND_RESULT_EXIST);
				// this.isUserJoinedClub = ();
				// updateJoinedClubInf();
			}
		} else if (t.getParam("method").equals("isUserFocusedClub")) {
			if (t.json != null) {
				succ = true;
				mClubUserInfo
						.setUserFocusedClub(t.json.optJSONObject("results")
								.optInt("success") == Constants.FIND_RESULT_EXIST);
				// this.isUserFocusedClub = ();
				// updateFocusedClubInf();
			}
		}

		if (succ) {

			// bothSucc++;

			if (t.getParam("method").equals("listValidClubMember")) {

				setViewContent("listValidClubMember");
			} else if (t.getParam("method").equals("listClubProject")) {
				setViewContent("listClubProject");
			} else if (t.getParam("method").equals("listValidClubManager")) {
				setViewContent("listValidClubManager");
			}

			hideWait();

		}

		super.onFinish(t);
	}

	void setViewContent(String methodName) {

		if ("updateSettingClick".equals(methodName)) {
			club_setting_button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra(ClubInfoActivity.EXTRA_KEY_CLUB_GUID,
							mClubGuid);
					intent.putExtra(
							ClubInfoActivity.EXTRA_KEY_CLUB_USER_INFO_USER_JOINED_CLUB,
							mClubUserInfo.isUserJoinedClub());
					intent.putExtra(
							ClubInfoActivity.EXTRA_KEY_CLUB_USER_INFO_USER_FOCUSED_CLUB,
							mClubUserInfo.isUserFocusedClub());
					intent.putExtra(
							ClubInfoActivity.EXTRA_KEY_CLUB_USER_INFO_CLUB_JSON_STRING,
							mClubUserInfo.getClubInfoJson().toString());

					intent.setClass(MyClubManagementActivity.this,
							ClubInfoActivity.class);
					startActivity(intent);
				}
			});
			my_clubs_manage_member
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// do nothing
						}
					});
			my_clubs_manage_member
					.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							//
							return true;
						}

					});
			// MyClubManagementActivity.this
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// if not manager, set manage control invisiable
					TextView[] tvs = scrollHPageWithTableView.getTableList();
					tvs[0].setText("活动");
					tvs[1].setText("会员");
					adapter.notifyDataSetChanged();
				}
			});

		} else if (methodName != null
				&& methodName.equals("listValidClubMember")) {

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// if not manager, set manage control invisiable
					if (CLUB_MANAGER_TYPE_USER.equals(mManageType)) {
						// todo
						createClubHd.setVisibility(View.GONE);
						my_clubs_assign_assitant.setVisibility(View.GONE);
						my_clubs_check_member.setVisibility(View.GONE);
						my_clubs_invite_member.setVisibility(View.GONE);
						// my_clubs_generate_invitelink.setVisibility(View.GONE);
						// member_seperator0.setVisibility(View.GONE);
						member_seperator1.setVisibility(View.GONE);
						member_seperator2.setVisibility(View.GONE);
						member_seperator3.setVisibility(View.GONE);
					} else {
						createClubHd.setVisibility(View.VISIBLE);
						my_clubs_assign_assitant.setVisibility(View.VISIBLE);
						my_clubs_check_member.setVisibility(View.VISIBLE);
						my_clubs_invite_member.setVisibility(View.VISIBLE);
						// my_clubs_generate_invitelink
						// .setVisibility(View.VISIBLE);
						// member_seperator0.setVisibility(View.VISIBLE);
						member_seperator1.setVisibility(View.VISIBLE);
						member_seperator2.setVisibility(View.VISIBLE);
						member_seperator3.setVisibility(View.VISIBLE);
					}

					adapter.notifyDataSetChanged();
				}
			});

		} else if (methodName != null && methodName.equals("listClubProject")) {

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					club_hd_listviewAdapter.notifyDataSetChanged();
					mPullToRefreshListView.onRefreshComplete();

				}
			});
		} else if (methodName != null
				&& methodName.equals("listValidClubManager")) {

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					starMemberList.removeAllViews();
					for (int i = 0; i < clubManagerList.length(); i++) {

						View headItem = inflater.inflate(
								R.layout.my_clubs_memberlist_item, null);
						headItem.setClickable(true);
						// TextView ismembertakepartinhd = (TextView) headItem
						// .findViewById(R.id.ismembertakepartinhd);
						CustomImageView memberIcon = (CustomImageView) headItem
								.findViewById(R.id.memberIcon);
						TextView memberName = (TextView) headItem
								.findViewById(R.id.memberName);
						TextView memberinclubtype = (TextView) headItem
								.findViewById(R.id.memberinclubtype);

						JSONObject fi = null;
						try {
							fi = clubManagerList.getJSONObject(i);
						} catch (JSONException e) {
							e.printStackTrace();
						}

						if (fi != null) {
							memberIcon.setImageUrl(fi.optString("faceUrl"));
							memberName.setText(fi.optString("nickname"));
							String memberType = null;
							if ("0".equals(fi.optString("roleId"))) {
								memberType = "创始人";
							} else if ("1".equals(fi.optString("roleId"))) {
								memberType = "会员";
							} else if ("2".equals(fi.optString("roleId"))) {
								memberType = "助理";
							} else if ("3".equals(fi.optString("roleId"))) {
								memberType = "管理员";
							}
							memberinclubtype.setText(memberType);
						}

						headItem.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {

								// Intent intent = new Intent();

								// intent.putExtra("nickname",
								// fi.getMemberName());
								// intent.putExtra("imageUrl",
								// fi.getImageUrl());
								// intent.setClass(MyClubManagementActivity.this,
								// PersonInfoActivity.class);
								// startActivity(intent);
							}
						});
						starMemberList.addView(headItem);
						// starMemberList
					}
				}
			});

		}
	}

	void requestData() {

		requestClubManager();
		requestAddedMemberList();
		requestHdList();
		//
		requestClubInf();
		requestJoinedClubInf();
		requestFocusedClubInf();
	}

	/**
	 * 从Server端取得俱乐部信息
	 */
	private void requestClubInf() {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "findClubByGuid");
		task.addParam("club_guid", mClubGuid);

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	/**
	 * 从Server端取得信息，判断用户是否加入俱乐部
	 */
	private void requestJoinedClubInf() {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "isUserJoinedClub");
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

	void requestHdList() {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);

		String guid = "";

		if (guid.equals(clubInfoJson.optString("clubGuid"))) {
			guid = clubInfoJson.optString("guid");
		} else {
			guid = clubInfoJson.optString("clubGuid");
		}

		task.addParam("method", "listClubProject");// 页码
		task.addParam("user_guid", clubInfoJson.optString("userGuid"));
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());

		task.addParam("club_guid", guid);
		mClubGuid = guid;

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	void requestAddedMemberList() {

		String guid = "";

		if (guid.equals(clubInfoJson.optString("clubGuid"))) {
			guid = clubInfoJson.optString("guid");
		} else {
			guid = clubInfoJson.optString("clubGuid");
		}
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "listValidClubMember");// 页码
		task.addParam("user_guid", clubInfoJson.optString("userGuid"));
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());

		task.addParam("club_guid", guid);

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	void requestClubManager() {

		String guid = "";

		if (guid.equals(clubInfoJson.optString("clubGuid"))) {
			guid = clubInfoJson.optString("guid");
		} else {
			guid = clubInfoJson.optString("clubGuid");
		}

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "listValidClubManager");// 页码
		task.addParam("user_guid", clubInfoJson.optString("userGuid"));
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());

		task.addParam("club_guid", guid);

		SOApplication.getDownLoadManager().startTask(task);
		showWait();

	}

	// 来来来！！ 这里是俱乐部活动管理Dialog处理~，~
	void clubHdManager(String target) {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());
		task.addParam("project_guid",
				currentLongClickedProject.optString("guid"));

		if (target.equals("modify")) {

			// Intent intent = new Intent();

		} else if (target.equals("pause")) {

			task.addParam("method", "pauseProject");
		} else if (target.equals("resume")) {

			task.addParam("method", "resumeProject");

		} else if (target.equals("close")) {

			task.addParam("method", "stopProject");

		}

		SOApplication.getDownLoadManager().startTask(task);
		showWait();

	}

	protected void init() {

		clubManagerList = new JSONArray();

		for (int i = ClubActivityList.myActivityList.size() - 1; i >= 0; i--) {
			String str = ClubActivityList.myActivityList.get(i)
					.getClass().toString();
			if (str.equals("class com.sohuodong.My.MyCreateClubTypeActivity")) {
				setResult(CLOSE_CLUBMANAGEMENT_TO_CLUBLIST);
			}
			ClubActivityList.myActivityList.get(i).finish();
		}

		clubHdList = new JSONArray();
		clubAddedMemberList = new JSONArray();

		get_intent = this.getIntent();
		if (get_intent.hasExtra(EXTRA_PARAM_CLUB_TYPE)) {
			this.mManageType = get_intent.getStringExtra(EXTRA_PARAM_CLUB_TYPE);
		} else {
			this.mManageType = CLUB_MANAGER_TYPE_MANAGER;
		}

		try {
			clubInfoJson = new JSONObject(
					get_intent.getStringExtra("jsonobject"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		iconUrl = get_intent.getStringExtra("");
		clubName = clubInfoJson.optString("clubName");
		if (clubName == null || "".equals(clubName)) {
			clubName = clubInfoJson.optString("name");
		}
		if ("".equals(clubName)) {
			clubName = get_intent.getStringExtra("groupName");
		}

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		club_setting_button = (LinearLayout) findViewById(R.id.club_setting_button);
		club_setting_button.setVisibility(View.VISIBLE);

		club_setting_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MyClubManagementActivity.this,
						MyClubDetailActivity.class);
				intent.putExtra("clubInfoJson", clubInfoJson.toString());
				startActivityForResult(intent, TO_MYCLUBDETAILACTIVITY);

			}
		});

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText(clubName);

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		final long[] pattern = { 100, 100, 0, 0 };

		clubHDLongClick = new CustomDialog(this);
		clubHDLongClick.setContentView(R.layout.my_clubs_hd_longclick_dialog);
		clubHDLongClick.setCancelable(true);

		// 管理活动Dialog的三个按钮
		// 修改
		my_club_hd_longclick_modify = (TextView) clubHDLongClick
				.findViewById(R.id.my_club_hd_longclick_modify);

		my_club_hd_longclick_modify
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {

					}
				});
		// 暂停
		my_club_hd_longclick_pause = (TextView) clubHDLongClick
				.findViewById(R.id.my_club_hd_longclick_pause);

		my_club_hd_longclick_pause
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						clubHdManager("pause");
					}
				});
		// 关闭
		my_club_hd_longclick_close = (TextView) clubHDLongClick
				.findViewById(R.id.my_club_hd_longclick_close);

		my_club_hd_longclick_close
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {

						clubHdManager("close");
					}
				});

		my_clubs_hd_dialog_frame = (LinearLayout) clubHDLongClick
				.findViewById(R.id.my_clubs_hd_dialog_frame);

		my_clubs_hd_dialog_frame.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				hideClubHDLongClickDialog();
			}
		});

		LongClickDialog = new CustomDialog(this);
		LongClickDialog
				.setContentView(R.layout.my_clubs_longclick_member_dialog);
		my_clubs_member_dialog_frame = (LinearLayout) LongClickDialog
				.findViewById(R.id.my_clubs_member_dialog_frame);
		my_clubs_member_dialog_frame
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						hideMemberLongClickDialog();
					}
				});
		LongClickDialog.setCancelable(true);

		// Collections.sort(fakeMemberList, new MyComparator());
		// 快来看啊，inflater在这里！！！！！
		inflater = (LayoutInflater) MyClubManagementActivity.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		createClubHd = (LinearLayout) inflater.inflate(
				R.layout.my_clubs_clubhd_list_headview, null);
		createClubHd.setVisibility(View.GONE);

		refreshListViewFrame = (LinearLayout) inflater.inflate(
				R.layout.my_clubs_club_hd_list, null);

		mPullToRefreshListView = (PullToRefreshListView) refreshListViewFrame
				.findViewById(R.id.mPullToRefreshListView);

		mPullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {

						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						// Do work to refresh the list here.
						requestData();
					}
				});

		// 俱乐部活动的listview

		club_hd_listview = mPullToRefreshListView.getRefreshableView();
		club_hd_listviewAdapter = new ClubHDListViewAdapter();
		club_hd_listview.setAdapter(club_hd_listviewAdapter);
		if (CLUB_MANAGER_TYPE_MANAGER.equals(mManageType)) {
			club_hd_listview.addHeaderView(createClubHd);
		}

		club_hd_listview
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {

						vibrator.vibrate(pattern, -1);
						JSONObject projectJson = (JSONObject) club_hd_listview
								.getItemAtPosition(arg2);
						showClubHDLongClickDialog(projectJson);
						return true;
					}
				});

		createClubHd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.putExtra("jsonobjc", clubInfoJson.toString());
				intent.setClass(MyClubManagementActivity.this,
						MyClubNewProjectActivity.class);
				startActivityForResult(intent,
						MyClubManagementActivity.TO_ORGANISEHDACTIVITY);
			}
		});

		club_hd_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Intent intent = new Intent();
				JSONObject jo = (JSONObject) club_hd_listview
						.getItemAtPosition(arg2);
				intent.putExtra("hdJsonInfo", jo.toString());
				intent.putExtra("clubInfoJson", clubInfoJson.toString());
				intent.setClass(MyClubManagementActivity.this,
						MyClubHdDetailActivity.class);
				startActivity(intent);
			}

		});

		manage_member = (RelativeLayout) inflater.inflate(
				R.layout.addressbook_list, null);
		my_clubs_manage_member = (ListView) manage_member
				.findViewById(R.id.addressbook);

		adapter = new MyIndexableListViewAdapter();
		headerView = inflater
				.inflate(R.layout.my_clubs_memberlist_header, null);
		my_clubs_manage_member.addHeaderView(headerView);
		my_clubs_manage_member.setAdapter(adapter);

		// 指定活动助理
		my_clubs_assign_assitant = (LinearLayout) headerView
				.findViewById(R.id.my_clubs_assign_assistant);

		my_clubs_invite_member = (LinearLayout) headerView
				.findViewById(R.id.my_clubs_invite_member);

		// my_clubs_generate_invitelink = (LinearLayout) headerView
		// .findViewById(R.id.my_clubs_generate_invitelink);

		my_clubs_check_member = (LinearLayout) headerView
				.findViewById(R.id.my_clubs_check_member);
		// member_seperator0 = (TextView) headerView
		// .findViewById(R.id.member_seperator0);
		member_seperator1 = (TextView) headerView
				.findViewById(R.id.member_seperator1);
		member_seperator2 = (TextView) headerView
				.findViewById(R.id.member_seperator2);
		member_seperator3 = (TextView) headerView
				.findViewById(R.id.member_seperator3);

		// 生成会员邀请链接
		// my_clubs_generate_invitelink
		// .setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent();
		// intent.putExtra(
		// MyClubGenerateInviteLinkActivity.EXTRA_PARAM_CLUBID,
		// mClubUserInfo.getClubInfoJson().optString("id"));
		// intent.setClass(MyClubManagementActivity.this,
		// MyClubGenerateInviteLinkActivity.class);
		// startActivity(intent);
		// }
		//
		// });

		// 指定活动助理
		my_clubs_assign_assitant.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("jsonObject", clubInfoJson.toString());
				intent.setClass(MyClubManagementActivity.this,
						MyClubAssignAssitantActivity.class);
				// intent.putExtra(SearchActivity.EXTRA_KEY_SEARCH_TYPE,
				// SearchActivity.SEARCH_TYPE_MEMBER);
				// intent.putExtra(SearchActivity.EXTRA_KEY_CURRENT_CLUB,
				// clubInfoJson.toString());
				// String guid = "";
				//
				// if (guid.equals(clubInfoJson.optString("clubGuid"))) {
				// guid = clubInfoJson.optString("guid");
				// } else {
				// guid = clubInfoJson.optString("clubGuid");
				// }
				// intent.putExtra(SearchActivity.EXTRA_KEY_CURRENT_CLUB_GUID,
				// guid);
				startActivity(intent);
			}
		});

		// 待审核的会员

		my_clubs_check_member.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.putExtra("jsonObject", clubInfoJson.toString());
				intent.setClass(MyClubManagementActivity.this,
						MyClubCheckMemberActivity.class);
				startActivity(intent);
			}
		});

		my_clubs_invite_member.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("iconUrl", iconUrl);
				intent.putExtra("clubName", clubName);
				String clubGuid = "";

				if (clubGuid.equals(clubInfoJson.optString("clubGuid"))) {
					clubGuid = clubInfoJson.optString("guid");
				} else {
					clubGuid = clubInfoJson.optString("clubGuid");
				}
				intent.putExtra(
						InviteMemberSelectTypeActivity.EXTRA_PARAM_CLUBGUID,
						clubGuid);
				intent.putExtra(
						InviteMemberSelectTypeActivity.EXTRA_PARAM_CLUBID,
						mClubUserInfo.getClubInfoJson().optString("id"));
				intent.putExtra(
						InviteMemberSelectTypeActivity.EXTRA_PARAM_CLUBNAME,
						clubInfoJson.optString("clubName"));
				intent.setClass(MyClubManagementActivity.this,
				// MyClubsInviteMemberActivity.class);
						InviteMemberSelectTypeActivity.class);
				startActivity(intent);
			}
		});

		starMemberList = (LinearLayout) headerView
				.findViewById(R.id.starmember);

		addressBookRightList = (LinearLayout) manage_member
				.findViewById(R.id.addressbook_rightlist);

		addressbook_index = (TextView) manage_member
				.findViewById(R.id.addressbook_index);

		letterAnimation = new AlphaAnimation(1f, 1f);
		letterAnimation.setDuration(300);
		letterAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				addressbook_index.setVisibility(View.INVISIBLE);
			}
		});

		inflater.inflate(R.layout.addressbook_rightlist_item,
				addressBookRightList);
		TextView text = (TextView) addressBookRightList
				.getChildAt(addressBookRightList.getChildCount() - 1);
		text.setText("↑");

		inflater.inflate(R.layout.addressbook_rightlist_item,
				addressBookRightList);
		text = (TextView) addressBookRightList.getChildAt(addressBookRightList
				.getChildCount() - 1);
		text.setText("☆");

		char letterA = 'A';
		for (int i = 0; i < 26; i++) {

			inflater.inflate(R.layout.addressbook_rightlist_item,
					addressBookRightList);
			text = (TextView) addressBookRightList
					.getChildAt(addressBookRightList.getChildCount() - 1);
			char letter = (char) (letterA + i);
			final String textTmp = String.valueOf(letter);
			text.setText(textTmp);

		}

		inflater.inflate(R.layout.addressbook_rightlist_item,
				addressBookRightList);
		text = (TextView) addressBookRightList.getChildAt(addressBookRightList
				.getChildCount() - 1);
		text.setText("#");

		addressBookRightList
				.setOnTouchListener(new LinearLayout.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {

						if (event.getAction() == MotionEvent.ACTION_DOWN
								|| event.getAction() == MotionEvent.ACTION_MOVE) {
							float dy = event.getY();
							int count = 29;
							int vh = addressBookRightList.getHeight();
							int index = (int) (dy * count / vh);
							if (count > 0) {
								int childcount = addressBookRightList
										.getChildCount();
								if (index < 0) {
									index = 0;
								}
								if (index >= childcount) {
									index = childcount - 1;
								}

								TextView t = (TextView) addressBookRightList
										.getChildAt(index);

								if (index != 0 && index != 1
										&& index != childcount - 1) {
									addressbook_index.setText(t.getText()
											.toString());

									scrllto(t.getText().toString());

									if (addressbook_index.getVisibility() != View.VISIBLE) {
										addressbook_index
												.setVisibility(View.VISIBLE);
										addressbook_index.clearAnimation();
										addressbook_index
												.startAnimation(letterAnimation);
									}
								}
							}
						}
						return true;
					}

				});

		scrollHPageWithTableView = (ScrollHPageWithTableView) findViewById(R.id.my_clubs_club_management_table);

		my_clubs_manage_member
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						Intent intent = new Intent();
						JSONObject jo = (JSONObject) adapter
								.getItem((int) arg3);
						FakeMemberInfo f = new FakeMemberInfo(jo
								.optString("nickname"), "", "", "");
						intent.putExtra("nickname", f.getMemberName());
						intent.putExtra("imageUrl", f.getImageUrl());
						intent.setClass(MyClubManagementActivity.this,
								PersonInfoActivity.class);
						startActivity(intent);

					}
				});
		my_clubs_manage_member
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// my_clubs_member_dialog_frame.setFocusable(true);
						// my_clubs_member_dialog_frame
						// .setFocusableInTouchMode(true);
						// my_clubs_member_dialog_frame.requestFocus();
						// my_clubs_member_dialog_frame.requestFocusFromTouch();

						String memberType = null;

						TextView power = (TextView) LongClickDialog
								.findViewById(R.id.my_clubs_member_powergone);
						TextView admin = (TextView) LongClickDialog
								.findViewById(R.id.my_clubs_member_admin);
						TextView assistant = (TextView) LongClickDialog
								.findViewById(R.id.my_clubs_member_assistant);

						JSONObject jo = (JSONObject) my_clubs_manage_member
								.getItemAtPosition(arg2);

						if ("0".equals(jo.optString("roleId"))) {
							memberType = "创始人";
						} else if ("1".equals(jo.optString("roleId"))) {
							memberType = "会员";
						} else if ("2".equals(jo.optString("roleId"))) {
							memberType = "助理";
						} else if ("3".equals(jo.optString("roleId"))) {
							memberType = "管理员";
						}

						FakeMemberInfo ff = new FakeMemberInfo(jo
								.optString("nickname"), jo
								.optString("faceUrl"), memberType, "");
						if (ff.getMemberType().equals("创始人")) {
							return false;
						} else if (ff.getMemberType().equals("")) {
							return false;
						} else if (ff.getMemberType().equals("管理员")) {

							admin.setVisibility(View.GONE);
							assistant.setVisibility(View.VISIBLE);
							power.setVisibility(View.VISIBLE);
						} else if (ff.getMemberType().equals("助理")) {
							admin.setVisibility(View.VISIBLE);
							assistant.setVisibility(View.GONE);
							power.setVisibility(View.VISIBLE);
						} else {
							admin.setVisibility(View.VISIBLE);
							assistant.setVisibility(View.VISIBLE);
							power.setVisibility(View.GONE);
						}
						vibrator.vibrate(pattern, -1);
						TextView title = (TextView) LongClickDialog
								.findViewById(R.id.memberName_dialogtitle);
						TextView memberTakePartIn_dialogtitle = (TextView) LongClickDialog
								.findViewById(R.id.memberTakePartIn_dialogtitle);

						if (ff.getIsTakePartIn().equals("仅关注")) {
							memberTakePartIn_dialogtitle.setText("尚未参加俱乐部活动");
						} else if (ff.getIsTakePartIn().equals("参加")) {
							memberTakePartIn_dialogtitle.setText("参与过俱乐部活动");
						} else if (ff.getIsTakePartIn().equals("组织")) {
							memberTakePartIn_dialogtitle.setText("组织过活动");
						} else if (ff.getIsTakePartIn().equals("协助")) {
							memberTakePartIn_dialogtitle.setText("协助组织过活动");
						} else {
							memberTakePartIn_dialogtitle.setText("尚未作出任何贡献");
						}
						title.setText(ff.getMemberName());
						showMemberLongClickDialog();
						return true;
					}

				});

		scrollHPageWithTableView.setScrollHPageWithTableAdapter(this);

	}

	void scrllto(String s) {
		int index = 0;
		String memberType = null;
		if (s != null) {
			if (s.equals("↑")) {
				my_clubs_manage_member.setSelection(0);
			} else {

				int count = adapter.getCount();
				for (int i = 0; i < count; i++) {

					JSONObject current_jo = clubAddedMemberList
							.optJSONObject(i);

					if ("0".equals(current_jo.optString("roleId"))) {
						memberType = "创始人";
					} else if ("1".equals(current_jo.optString("roleId"))) {
						memberType = "会员";
					} else if ("2".equals(current_jo.optString("roleId"))) {
						memberType = "助理";
					} else if ("3".equals(current_jo.optString("roleId"))) {
						memberType = "管理员";
					}
					FakeMemberInfo current_fi = new FakeMemberInfo(
							current_jo.optString("nickname"),
							current_jo.optString("faceUrl"), memberType, "");

					if (current_fi.getFirstLetter().equals(s)) {

						index = i;

						break;
					}

				}
				my_clubs_manage_member.setSelection(index);
			}
		}

	}

	// 活动listview的adapter
	private class ClubHDListViewAdapter extends BaseAdapter {

		class Tag {
			TextView nameTextView;
			TextView addrTextView;
			TextView timeTextView;
			TextView cityTextView;
			TextView leftDayTextView;
			TextView weekTextView;
			LinearLayout dateweekandleftdays;
			TextView lineTextView;
			TextView istoday;
			TextView leftdaysutiltext;
			XVURLImageView clubLogo;
			TextView status;
			TextView clubNameTextView;
			ImageView userJoinedView;
			ImageView userFocusedView;
		}

		@Override
		public int getCount() {
			return clubHdList.length();
		}

		@Override
		public Object getItem(int position) {
			if (position < 0 || position >= getCount()) {
				return null;
			} else {
				return clubHdList.optJSONObject(position);
			}

		}

		@Override
		public long getItemId(int position) {

			if (position < 0 || position >= getCount()) {
				return 0;
			} else {
				return position;
			}

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Tag tag = null;
			if (convertView == null) {
				tag = new Tag();
				convertView = inflater.inflate(R.layout.item_find_huodong_info,
						null);

				tag.nameTextView = (TextView) convertView
						.findViewById(R.id.name);
				tag.addrTextView = (TextView) convertView
						.findViewById(R.id.addr);
				tag.timeTextView = (TextView) convertView
						.findViewById(R.id.time);
				tag.cityTextView = (TextView) convertView
						.findViewById(R.id.city);
				tag.weekTextView = (TextView) convertView
						.findViewById(R.id.week);
				tag.leftDayTextView = (TextView) convertView
						.findViewById(R.id.leftday);
				// tag.ticketsTextView = (TextView)
				// arg1.findViewById(R.id.price);
				tag.dateweekandleftdays = (LinearLayout) convertView
						.findViewById(R.id.dateweekandleftdays);
				tag.lineTextView = (TextView) convertView
						.findViewById(R.id.hdlistdivider);
				tag.istoday = (TextView) convertView.findViewById(R.id.istoday);
				tag.leftdaysutiltext = (TextView) convertView
						.findViewById(R.id.leftdaysutiltext);
				tag.status = (TextView) convertView
						.findViewById(R.id.hd_status);
				tag.clubNameTextView = (TextView) convertView
						.findViewById(R.id.hd_clubname);
				tag.userFocusedView = (ImageView) convertView
						.findViewById(R.id.hd_list_item_user_focused);
				tag.userJoinedView = (ImageView) convertView
						.findViewById(R.id.hd_list_item_user_joined);
				tag.clubLogo = (XVURLImageView) convertView
						.findViewById(R.id.club_logo);
				convertView.setTag(tag);
			} else {
				tag = (Tag) convertView.getTag();
			}

			JSONObject jo = (JSONObject) getItem(position);

			// String date, String previewUrl, String title,
			// String city, String building
			FakeClubHD fch = new FakeClubHD(jo.optString("startDate"), "",
					jo.optString("name"), jo.optString("city"),
					jo.optString("building"), jo.optString("location"));

			// get status
			int status = jo.optInt("stateId");

			if (status == 1) {
				tag.status.setText(HD_STATUS_STARTING);
			} else if (status == 2) {
				tag.status.setText(HD_STATUS_JOINING);
			} else if (status == 3) {
				tag.status.setText(HD_STATUS_PROCESSING);
			} else if (status == 4) {
				tag.status.setText(HD_STATUS_CLOSED);
			} else if (status == 5) {
				tag.status.setText(HD_STATUS_PAUSE);
			} else if (status == 6) {
				tag.status.setText(HD_STATUS_ABORTED);
			} else {
				tag.status.setText("[已经发布]");
			}

			if (fch != null) {

				// String result = "";
				String rawDate = "";
				String leftDays = "";
				String week = "";
				boolean isSameYear = false;
				JSONObject jo_previous = (JSONObject) getItem(position - 1);
				FakeClubHD previousItemJson;
				if (jo_previous == null) {
					previousItemJson = null;
				} else {
					previousItemJson = new FakeClubHD(
							jo_previous.optString("startDate"), "",
							jo_previous.optString("name"),
							jo_previous.optString("city"),
							jo_previous.optString("building"),
							jo_previous.optString("location"));
				}
				tag.timeTextView.setTextColor(0xff4184f5);
				tag.weekTextView.setTextColor(0xff4184f5);
				tag.leftDayTextView.setTextColor(0xff4184f5);
				tag.nameTextView.setText(fch.getTitle());
				tag.addrTextView.setText(fch.getBuilding() + " "
						+ fch.getLocation());
				tag.istoday.setBackgroundColor(istodayColor);
				tag.leftdaysutiltext.setTextColor(0xff4184f5);
				tag.clubLogo.setVisibility(View.GONE);
				rawDate = new String(fch.getDate());
				if (rawDate == null || rawDate.equals("")) {
					rawDate = "1970-1-1";
				}
				LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) tag.istoday
						.getLayoutParams();
				p.height = 2;
				try {
					week = StrUtil.dayForWeek(rawDate);
					Date todayDate = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd");
					String today = formatter.format(todayDate);
					String todaysWeek = StrUtil.dayForWeek(today);
					int days = StrUtil.daysBetween(today, rawDate);
					int tempDays = Integer.MAX_VALUE;
					String rawDateYear = rawDate.subSequence(0, 4).toString();
					if (today.subSequence(0, 4).equals(rawDateYear)) {
						isSameYear = true;
					}

					if (previousItemJson != null) {
						String previousItemDate = new String(
								previousItemJson.getDate());

						tempDays = StrUtil.daysBetween(previousItemDate,
								rawDate);

					}

					if (tempDays == 0) {
						tag.dateweekandleftdays.setVisibility(View.GONE);
						tag.lineTextView.setVisibility(View.VISIBLE);
					} else {

						tag.dateweekandleftdays.setVisibility(View.VISIBLE);
						tag.lineTextView.setVisibility(View.VISIBLE);

						if (days < 0) {
							days = -1 * days;
							tag.istoday.setBackgroundColor(istodayColor);

							leftDays = "-" + String.valueOf(days);
							tag.leftdaysutiltext.setText("");
						} else if (days == 1) {
							tag.istoday.setBackgroundColor(istodayColor);
							leftDays = "明天";
							tag.leftdaysutiltext.setText("");
						} else if (days == 2) {
							tag.istoday.setBackgroundColor(istodayColor);
							leftDays = "后天";
							tag.leftdaysutiltext.setText("");
						} else if (days > 2 && days <= 7) {
							tag.istoday.setBackgroundColor(istodayColor);
							if (((todaysWeek.equals("星期六")) || (todaysWeek
									.equals("星期日")))
									&& (week.equals("星期六") || (week
											.equals("星期日")))) {

								leftDays = String.valueOf(days) + "天";
								tag.leftdaysutiltext.setText("+");

							} else if (week.equals("星期六")) {
								leftDays = "本周六";
								tag.istoday.setBackgroundColor(istodayColor);
								tag.timeTextView.setTextColor(0xff45c01a);
								tag.weekTextView.setTextColor(0xff45c01a);
								tag.leftDayTextView.setTextColor(0xff45c01a);
								tag.leftdaysutiltext.setTextColor(0xff45c01a);
								tag.leftdaysutiltext.setText("");

							} else if (week.equals("星期日")) {
								leftDays = "本周日";
								tag.istoday.setBackgroundColor(istodayColor);
								tag.timeTextView.setTextColor(0xff45c01a);
								tag.weekTextView.setTextColor(0xff45c01a);
								tag.leftDayTextView.setTextColor(0xff45c01a);
								tag.leftdaysutiltext.setTextColor(0xff45c01a);
								tag.leftdaysutiltext.setText("");
							} else {
								leftDays = String.valueOf(days) + "天";
								tag.leftdaysutiltext.setText("+");
							}

						} else if (days == 0) {
							tag.istoday.setBackgroundColor(istodayColor);
							tag.timeTextView.setTextColor(0xffd6006f);
							tag.weekTextView.setTextColor(0xffd6006f);
							tag.leftDayTextView.setTextColor(0xffd6006f);
							leftDays = "今天";
							p.height = 2;
							tag.leftdaysutiltext.setText("");
						} else {
							if (week.equals("星期日") || week.equals("星期六")) {
								tag.istoday.setBackgroundColor(istodayColor);
								tag.timeTextView.setTextColor(0xff45c01a);
								tag.weekTextView.setTextColor(0xff45c01a);
								tag.leftDayTextView.setTextColor(0xff45c01a);
								tag.leftdaysutiltext.setTextColor(0xff45c01a);
							} else {
								tag.istoday.setBackgroundColor(istodayColor);
							}

							leftDays = String.valueOf(days) + "天";
							tag.leftdaysutiltext.setText("+");

						}
					}
					tag.istoday.setLayoutParams(p);
				} catch (Exception e) {
					e.printStackTrace();
				}

				String[] rawDateDivider = rawDate.split("-");
				if (Integer.valueOf(rawDateDivider[1]) < 10) {
					rawDateDivider[1] = rawDateDivider[1] + " ";
				}
				if (Integer.valueOf(rawDateDivider[2]) < 10) {
					rawDateDivider[2] = " " + rawDateDivider[2];
				}
				String month = rawDateDivider[1] + "月";
				String day = rawDateDivider[2] + "日";
				String year = rawDateDivider[0] + "年";
				if (isSameYear) {

					rawDate = month + day;
					tag.timeTextView.setText(rawDate);
				} else {
					rawDate = year + month + day;
					tag.timeTextView.setText(rawDate);
				}

				tag.weekTextView.setText(week);
				tag.leftDayTextView.setText(leftDays);
				//

				tag.cityTextView.setText(fch.getCity());
				tag.clubNameTextView.setVisibility(View.GONE);
				tag.userFocusedView.setVisibility(View.GONE);
				tag.userJoinedView.setVisibility(View.GONE);
			}

			return convertView;
		}

	}

	private class MyIndexableListViewAdapter extends BaseAdapter implements
			SectionIndexer {

		private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		class Tag {
			TextView ismembertakepartinhd;
			CustomImageView memberIcon;
			TextView memberName;
			TextView memberinclubtype;
			TextView firstLetter;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Tag tag = null;
			FakeMemberInfo pre_fi = null;
			String memberType = null;
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.my_clubs_memberlist_item, null);
				tag = new Tag();
				tag.ismembertakepartinhd = (TextView) convertView
						.findViewById(R.id.ismembertakepartinhd);
				tag.memberIcon = (CustomImageView) convertView
						.findViewById(R.id.memberIcon);
				tag.memberinclubtype = (TextView) convertView
						.findViewById(R.id.memberinclubtype);
				tag.memberName = (TextView) convertView
						.findViewById(R.id.memberName);
				tag.firstLetter = (TextView) convertView
						.findViewById(R.id.firstLetter);

				convertView.setTag(tag);
			} else {
				tag = (Tag) convertView.getTag();
			}

			JSONObject current_jo = (JSONObject) getItem(position);
			JSONObject pre_jo = (JSONObject) getItem(position - 1);

			if (pre_jo == null) {
				pre_fi = null;
			} else {
				// String nickName, String imageUrl, String memberType,
				// String isTakePartIn
				if ("0".equals(pre_jo.optString("roleId"))) {
					memberType = "创始人";
				} else if ("1".equals(pre_jo.optString("roleId"))) {
					memberType = "会员";
				} else if ("2".equals(pre_jo.optString("roleId"))) {
					memberType = "助理";
				} else if ("3".equals(pre_jo.optString("roleId"))) {
					memberType = "管理员";
				}
				pre_fi = new FakeMemberInfo(pre_jo.optString("nickname"),
						pre_jo.optString("faceUrl"), memberType, "");
				memberType = null;
			}

			if ("0".equals(current_jo.optString("roleId"))) {
				memberType = "创始人";
			} else if ("1".equals(current_jo.optString("roleId"))) {
				memberType = "会员";
			} else if ("2".equals(current_jo.optString("roleId"))) {
				memberType = "助理";
			} else if ("3".equals(current_jo.optString("roleId"))) {
				memberType = "管理员";
			}
			FakeMemberInfo current_fi = new FakeMemberInfo(
					current_jo.optString("nickname"),
					current_jo.optString("faceUrl"), memberType, "");

			if (pre_fi != null) {
				if (current_fi.getFirstLetter().equals(pre_fi.getFirstLetter())) {
					tag.firstLetter.setVisibility(View.GONE);
				} else {
					tag.firstLetter.setText(current_fi.getFirstLetter());
					tag.firstLetter.setVisibility(View.VISIBLE);

				}
			} else {
				tag.firstLetter.setText(current_fi.getFirstLetter());
				tag.firstLetter.setVisibility(View.VISIBLE);
			}

			tag.memberName.setText(current_fi.getMemberName());
			tag.ismembertakepartinhd.setText(current_fi.getIsTakePartIn());
			tag.memberIcon.setImageUrl(current_fi.getImageUrl());
			tag.memberinclubtype.setText(current_fi.getMemberType());

			return convertView;
		}

		@Override
		public long getItemId(int position) {

			if (position >= getCount() || position < 0) {
				return 0;
			}
			return position;
		}

		@Override
		public Object getItem(int position) {

			if (position >= getCount() || position < 0) {
				return null;
			} else {
				return clubAddedMemberList.optJSONObject(position);
			}

		}

		@Override
		public int getCount() {

			return clubAddedMemberList.length();
		}

		@Override
		public int getPositionForSection(int section) {
			// If there is no item for current section, previous section will be
			// selected
			for (int i = section; i >= 0; i--) {
				for (int j = 0; j < getCount(); j++) {
					if (i == 0) {
						// For numeric section
						for (int k = 0; k <= 9; k++) {
							// JSONObject jo = (JSONObject) getItem(j);
							FakeMemberInfo f = new FakeMemberInfo("nickName",
									"imageUrl", "memberType", "isTakePartIn");
							if (StringMatcher.match(
									String.valueOf(f.getFirstLetter()),
									String.valueOf(k)))
								return j;
						}
					} else {
						// JSONObject jo = (JSONObject) getItem(j);
						FakeMemberInfo f = new FakeMemberInfo("nickName",
								"imageUrl", "memberType", "isTakePartIn");
						if (StringMatcher.match(
								String.valueOf(f.getFirstLetter()),
								String.valueOf(mSections.charAt(i))))
							return j;
					}
				}
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			return position;
		}

		@Override
		public Object[] getSections() {
			String[] sections = new String[mSections.length()];
			for (int i = 0; i < mSections.length(); i++)
				sections[i] = String.valueOf(mSections.charAt(i));
			return sections;
		}

	}

	@Override
	public int getPageCount() {

		return 2;
	}

	@Override
	public String getTableString(int index) {

		String table = "";
		if (index == 0) {
			table = "活动管理";
		} else if (index == 1) {
			table = "会员管理";
		}
		if (CLUB_MANAGER_TYPE_USER.equals(this.mManageType)) {
			if (index == 0) {
				table = "活动";
			} else if (index == 1) {
				table = "会员";
			}
		}
		return table;
	}

	@Override
	public View getPageView(int index) {

		View v = null;
		if (index == 0) {
			v = refreshListViewFrame;
		} else if (index == 1) {
			v = manage_member;
		}
		return v;
	}

	@Override
	public void onPageChange(int index) {

	}

	@Override
	protected void onStop() {

		super.onStop();
		vibrator.cancel();
	}

	public class ClubUserInfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8404568979058916897L;

		private boolean isUserJoinedClub = false;
		private boolean isUserFocusedClub = false;
		private JSONObject clubInfoJson = null;

		public boolean isUserJoinedClub() {
			return isUserJoinedClub;
		}

		public void setUserJoinedClub(boolean isUserJoinedClub) {
			this.isUserJoinedClub = isUserJoinedClub;
		}

		public boolean isUserFocusedClub() {
			return isUserFocusedClub;
		}

		public void setUserFocusedClub(boolean isUserFocusedClub) {
			this.isUserFocusedClub = isUserFocusedClub;
		}

		public JSONObject getClubInfoJson() {
			return clubInfoJson;
		}

		public void setClubInfoJson(JSONObject clubInfoJson) {
			this.clubInfoJson = clubInfoJson;
		}
	}
}






