package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.control.AutoGridView;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.view.dialog.PopupViewDialog;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.ui.view.control.XVURLImageView;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mtcent.funnymeet.R;

public class MyClubHdDetailActivity extends BaseActivity {
	
	TextView hdname;
	XVURLImageView topimage;
	Intent get_intent;
	TextView titleTextView;

	private ImageView ivLikeIcon;
	private ImageView ivJoinIcon;
	Animation animation;
	int isLike;

	JSONObject hdJsonInfo;
	JSONObject clubInfoJson;
	JSONObject hdDetailJson;

	TextView hdaddress;// 活动场馆地址
	TextView hdbuilding;// 活动场馆名称
	TextView hdlocation;// 活动场所
	TextView hdstartdate;// 活动开始日期
	TextView hdenddate;// 活动结束时间
	//TextView hdallprice;// 全部价格
	//TextView hdbrief;// 活动简介
	//TextView hdwarning;// 活动注意事项
	//TextView hdactor;// 活动嘉宾
	//TextView traffic;// 活动交通指南
	//TextView hdfitpeople;// 活动适合人群
	//TextView hdschedulecontent;// 活动日程
	TextView hdstarttime;
	TextView hdendtime;
	TextView hdDuring;
	
	TextView tvFavourCount; //关注人数
	TextView tvSignCount;   //参加人数
	AutoGridView tags;

	ListView menuListView;
	
	private boolean isFocused;
	private boolean isJoined;

	View menuButton;
	PopupViewDialog menuPopupDialog;
	
	private int favourCount = 0;
	private int signCount = 0;

	String from = "";

	class ItemData {
		int iconId = 0;
		String name = "";

		public ItemData(int id, String namestr) {
			iconId = id;
			name = namestr;
		}
	}

	final ItemData[] menus = new ItemData[] {

	new ItemData(R.drawable.ofm_remarks_icon, "修改"),
			new ItemData(R.drawable.ofm_blacklist_icon, "暂停"),
			new ItemData(R.drawable.ofm_reportstop_icon, "关闭"), };

	void initMenu() {
		menuButton = findViewById(R.id.menu);
		menuButton.setVisibility(View.VISIBLE);
		menuButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				menuPopupDialog.show();
			}
		});

		menuPopupDialog = new PopupViewDialog(this);

		// 菜单显示时的动画
		ScaleAnimation showScale = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
				0.0f);
		showScale.setDuration(200);
		AlphaAnimation showAlpha = new AlphaAnimation(0.0f, 1.0f);
		showAlpha.setStartOffset(100);
		showAlpha.setDuration(100);
		AnimationSet showSet = new AnimationSet(true);
		showSet.addAnimation(showScale);
		showSet.addAnimation(showAlpha);

		// 菜单隐藏时的动画
		ScaleAnimation hideScale = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
				0.0f);
		hideScale.setDuration(200);
		AlphaAnimation hideAlpha = new AlphaAnimation(1.0f, 0.0f);
		hideAlpha.setDuration(100);
		AnimationSet hideSet = new AnimationSet(true);
		hideSet.addAnimation(hideScale);
		hideSet.addAnimation(hideAlpha);

		menuPopupDialog.getLayoutInflater().inflate(R.layout.menu,
				menuPopupDialog.getRootView());
		View menuListLayout = menuPopupDialog.findViewById(R.id.menuListLayout);
		menuPopupDialog.setAnimation(menuListLayout, showSet, hideSet);

		menuListView = (ListView) menuPopupDialog.findViewById(R.id.menuList);

		menuListView.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				if (arg1 == null) {
					arg1 = inflater.inflate(R.layout.menuitem, null);
				}
				TextView tv = (TextView) arg1.findViewById(R.id.name);
				tv.setTextColor(Color.WHITE);
				ItemData data = ((ItemData) getItem(arg0));
				String name = data.name;
				tv.setText(name);

				ImageView iv = (ImageView) arg1.findViewById(R.id.icon);
				iv.setImageResource(data.iconId);
				return arg1;
			}

			@Override
			public long getItemId(int arg0) {
				return arg0;
			}

			@Override
			public Object getItem(int arg0) {
				if (getCount() > arg0) {
					return menus[arg0];
				}
				return null;
			}

			@Override
			public int getCount() {
				return menus.length;
			}
		});
		menuListView.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ItemData data = (ItemData) arg0.getItemAtPosition(arg2);

				if (data.name.equals("暂停")) {
					clubHdManager("pause");
				} else if (data.name.equals("修改")) {
					editHdData();
				} else if (data.name.equals("关闭")) {
					clubHdManager("close");
				}

				menuPopupDialog.dismiss();
			}

		});
	}

	private void editHdData() {
		Intent intent = new Intent();
		intent.putExtra(MyClubNewProjectActivity.EXTRA_PARAM_CLUB_JSON_INFO,
				clubInfoJson.toString());
		intent.putExtra(MyClubNewProjectActivity.EXTRA_PARAM_HD_JSON_INFO,
				hdJsonInfo.toString());
		intent.putExtra(MyClubNewProjectActivity.EXTRA_PARAM_HANDLE_TYPE,
				MyClubNewProjectActivity.HANDLE_TYPE_EDIT);
		intent.setClass(MyClubHdDetailActivity.this,
				MyClubNewProjectActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_clubs_hd_detail);
		init();

		requestClubHdDetail();
		requestClubFavourCount();
		requestClubSignCount();
		requestUserSignFavorProject();

		initMenu();
	}

	// 来来来！！ 这里是俱乐部活动管理Dialog处理~，~
	void clubHdManager(String target) {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());
		task.addParam("project_guid", hdJsonInfo.optString("guid"));

		if (target.equals("modify")) {
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

	void requestClubHdDetail() {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);

		task.addParam("method", "findProjectByGuid");
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());
		task.addParam("project_guid", hdJsonInfo.optString("guid"));

		SOApplication.getDownLoadManager().startTask(task);
	}

	private void requestClubFavourCount() {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);

		task.addParam("method", "getProjectFavourCount");
		task.addParam("project_guid", hdJsonInfo.optString("guid"));

		SOApplication.getDownLoadManager().startTask(task);
	}

	private void requestUserSignFavorProject() {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST,
				null, RequestHelper.Type_DownJsonString, null, 0, true);
		task.addParam("method", "findUserSignFavorProject");
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("project_guid", hdJsonInfo.optString("guid"));
		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	private void requestClubSignCount() {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);

		task.addParam("method", "getProjectSignCount");
		task.addParam("project_guid", hdJsonInfo.optString("guid"));

		SOApplication.getDownLoadManager().startTask(task);
	}

	void commit(String target) {
		// favorProject&user_guid=*&user_session_guid=*&....
		// String[] keys = { "project_guid" }

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);

		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());
		task.addParam("project_guid", hdJsonInfo.optString("guid"));

		if (target.equals("like")) {
			task.addParam("method", "favorProject");// 页码
		} else if (target.equals("unlike")) {
			task.addParam("method", "unfavorProject");// 页码
		}

		SOApplication.getDownLoadManager().startTask(task);
		// showWait();

	}

	@Override
	public void onFinish(Pdtask t) {
		boolean succ = false;
		if (t.getParam("method").equals("favorProject")) {

			if (t.json != null) {
				final JSONObject result = t.json.optJSONObject("results");
				String msg = result.optString("message");
				StrUtil.showMsg(MyClubHdDetailActivity.this, msg);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if ("1".equals(result.optString("success"))) {
							isFocused = true;
							favourCount++;
							tvFavourCount.setText("" + favourCount);
							ivLikeIcon.startAnimation(animation);
							ivLikeIcon.setImageResource(R.drawable.friendactivity_comment_likeicon_havon);
						} else {
							isFocused = false;
							favourCount--;
							tvFavourCount.setText("" + favourCount);
							ivLikeIcon.setImageResource(R.drawable.friendactivity_comment_likeicon_normal);
						}
					}
				});
				hideWait();
			}

		} else if (t.getParam("method").equals("unfavorProject")) {
			if (t.json != null) {
				final JSONObject result = t.json.optJSONObject("results");
				String msg = result.optString("message");
				StrUtil.showMsg(MyClubHdDetailActivity.this, msg);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if ("1".equals(result.optString("success"))) {
							isFocused = false;
							favourCount--;
							tvFavourCount.setText("" + favourCount);
							ivLikeIcon.setImageResource(R.drawable.friendactivity_comment_likeicon_normal);
						} else {
							isFocused = true;
							favourCount++;
							tvFavourCount.setText("" + favourCount);
							ivLikeIcon.setImageResource(R.drawable.friendactivity_comment_likeicon_havon);
						}
					}
				});
				hideWait();
			}

		} else if (t.getParam("method").equals("signProject")) {
			if (t.json != null) {
				final JSONObject result = t.json.optJSONObject("results");
				String msg = result.optString("message");
				StrUtil.showMsg(MyClubHdDetailActivity.this, msg);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if ("1".equals(result.optString("success"))) {
							isJoined = true;
							signCount++;
							tvSignCount.setText("" + signCount);
							ivJoinIcon.startAnimation(animation);
							ivJoinIcon.setImageResource(R.drawable.huodong_joined);
						} else {
							isJoined = false;
							ivJoinIcon.setImageResource(R.drawable.huodong_notjoined);
						}
					}
				});
				hideWait();
			}

		}  else if (t.getParam("method").equals("unsignProject")) {
			if (t.json != null) {
				final JSONObject result = t.json.optJSONObject("results");
				String msg = result.optString("message");
				StrUtil.showMsg(MyClubHdDetailActivity.this, msg);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if ("1".equals(result.optString("success"))) {
							isJoined = false;
							signCount--;
							tvSignCount.setText("" + signCount);
							ivJoinIcon.setImageResource(R.drawable.huodong_notjoined);
						} else {
							isJoined = true;
							ivJoinIcon.setImageResource(R.drawable.huodong_joined);
						}
					}
				});
				hideWait();
			}

		} else if (t.getParam("method").equals("findProjectByGuid")) {
			if (t.json != null) {
				// JSONObject result = t.json.optJSONObject("results");
				// String msg = result.optString("message");
				// StrUtil.showMsg(MyClubHdDetailActivity.this, msg);
				// hideWait();
				succ = true;
				hdDetailJson = t.json.optJSONObject("results");
			}
		} else if (t.getParam("method").equals("pauseProject")) {

			if (t.json != null) {
				StrUtil.showMsg(MyClubHdDetailActivity.this,
						t.json.optJSONObject("results").optString("message"));
				hideWait();
			}
		} else if (t.getParam("method").equals("resumeProject")) {
			if (t.json != null) {
				StrUtil.showMsg(MyClubHdDetailActivity.this,
						t.json.optJSONObject("results").optString("message"));
				hideWait();
			}

		} else if (t.getParam("method").equals("stopProject")) {

			if (t.json != null) {
				StrUtil.showMsg(MyClubHdDetailActivity.this,
						t.json.optJSONObject("results").optString("message"));
				hideWait();
			}

		} else if (t.getParam("method").equals("getProjectFavourCount")) {

			if (t.json != null) {
				this.favourCount = t.json.optJSONObject("results").optInt("intExtra");
				hideWait();
			}

		} else if (t.getParam("method").equals("getProjectSignCount")) {

			if (t.json != null) {
				this.signCount = t.json.optJSONObject("results").optInt("intExtra");
				hideWait();
			}

		} else if ("findUserSignFavorProject".equals(t.getParam("method"))) {
			if (t.json != null) {
				if (t.json != null) {
					final JSONObject j = t.json;
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// 0 未关注未参加
							// 1 参加未关注
							// 10关注未参加
							// 11关注参加
							int success = Integer.parseInt(j.optJSONObject("results").optString("success"));
							switch (success) {
							case 0:
								ivLikeIcon.setImageResource(R.drawable.friendactivity_comment_likeicon_normal);
								isFocused = false;
								ivJoinIcon.setImageResource(R.drawable.huodong_notjoined);
								isJoined = false;
								break;
							case 10:
								ivLikeIcon.setImageResource(R.drawable.friendactivity_comment_likeicon_havon);
								isFocused = true;
								ivJoinIcon.setImageResource(R.drawable.huodong_notjoined);
								isJoined = false;
								break;
							case 1:
								ivLikeIcon.setImageResource(R.drawable.friendactivity_comment_likeicon_normal);
								isFocused = false;
								ivJoinIcon.setImageResource(R.drawable.huodong_joined);
								isJoined = true;
								break;
							case 11:
								ivLikeIcon.setImageResource(R.drawable.friendactivity_comment_likeicon_havon);
								isFocused = true;
								ivJoinIcon.setImageResource(R.drawable.huodong_joined);
								isJoined = true;
								break;
							}
								
						}
					});
				}
				hideWait();
			}
		}

		if (succ) {
			setViewContent();
		}
		super.onFinish(t);
	}

	protected void init() {

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		isLike = 0;

		animation = AnimationUtils.loadAnimation(MyClubHdDetailActivity.this,
				R.anim.likeiconscale);

		ivLikeIcon = (ImageView) findViewById(R.id.my_club_hd_detail_likeicon);
		ivLikeIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isFocused) {
					commit("like");
					isLike++;

				} else {
					commit("unlike");
					isLike--;
				}
				requestUserSignFavorProject();
			}
		});

		ivJoinIcon = (ImageView) findViewById(R.id.my_club_hd_detail_joinicon);
		ivJoinIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				takePartInOrWithdraw();
			}

		});

		
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("活动详情");

		get_intent = getIntent();
		hdname = (TextView) findViewById(R.id.hdname);
		topimage = (XVURLImageView) findViewById(R.id.topimage);

		hdaddress = (TextView) findViewById(R.id.hdaddress);
		hdbuilding = (TextView) findViewById(R.id.hdbuilding);
		hdlocation = (TextView) findViewById(R.id.hdlocation);
		hdstartdate = (TextView) findViewById(R.id.hdstartdate);
		//hdallprice = (TextView) findViewById(R.id.ticketprice);
		//hdbrief = (TextView) findViewById(R.id.hdbriefcontent);
//		hdwarning = (TextView) findViewById(R.id.warningcontent);
//		hdactor = (TextView) findViewById(R.id.hdactor);
//		traffic = (TextView) findViewById(R.id.hdtraffic);
//		hdfitpeople = (TextView) findViewById(R.id.fitpeoplerange);
//		hdschedulecontent = (TextView) findViewById(R.id.hdschedulecontent);
		tags = (AutoGridView) findViewById(R.id.tagsgrid);
		hdstarttime = (TextView) findViewById(R.id.hdstarttime);
		hdendtime = (TextView) findViewById(R.id.hdendtime);
		hdenddate = (TextView) findViewById(R.id.hdenddate);
		tvFavourCount = (TextView) findViewById(R.id.favour_count);
		tvSignCount = (TextView) findViewById(R.id.sign_count);
		hdDuring = (TextView) findViewById(R.id.hd_during);

		try {
			hdJsonInfo = new JSONObject(get_intent.getStringExtra("hdJsonInfo"));
			clubInfoJson = new JSONObject(get_intent.getStringExtra("clubInfoJson"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	void takePartInOrWithdraw() {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);

		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());
		task.addParam("project_guid", hdJsonInfo.optString("guid"));

		if (this.isJoined) {
			task.addParam("method", "unsignProject");
		} else {
			task.addParam("method", "signProject");
			ivJoinIcon.startAnimation(animation);
		}

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}


	void setViewContent() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// 
				tvFavourCount.setText("" + favourCount);
				tvSignCount.setText("" + signCount);
				hdname.setText(new String(hdDetailJson.optString("name"))
						.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "")
						.replaceAll("</[a-zA-Z]+[1-9]?>", "").trim());
				hdstartdate.setText(hdDetailJson.optString("startYear") + "/"
						+ hdDetailJson.optString("startMonth") + "/"
						+ hdDetailJson.optString("startDay"));
				hdstarttime.setText(hdDetailJson.optString("startHour") + ":"
						+ hdDetailJson.optString("startMinute"));

				hdenddate.setText(hdDetailJson.optString("endYear") + "/"
						+ hdDetailJson.optString("endMonth") + "/"
						+ hdDetailJson.optString("endDay"));
				hdendtime.setText(hdDetailJson.optString("endHour") + ":"
						+ hdDetailJson.optString("endMinute"));

				String during = getHdDuring();
				hdDuring.setText(during);
				hdbuilding.setText(new String(hdDetailJson
						.optString("building"))
						.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "")
						.replaceAll("</[a-zA-Z]+[1-9]?>", "").trim());
				hdlocation.setText(new String(hdDetailJson
						.optString("location"))
						.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "")
						.replaceAll("</[a-zA-Z]+[1-9]?>", "").trim());
				hdaddress
						.setText((hdDetailJson.optString("province") + " "
								+ hdDetailJson.optString("city") + " "
								+ hdDetailJson.optString("district") + " " + hdDetailJson
								.optString("address"))
								.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "")
								.replaceAll("</[a-zA-Z]+[1-9]?>", "").trim());

//				hdbrief.setText(new String(hdDetailJson.optString("detail"))
//						.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "")
//						.replaceAll("</[a-zA-Z]+[1-9]?>", "").trim());
			}

			private String getHdDuring() {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
				Date start;
				Date end;
				String startMonth = hdDetailJson.optString("startMonth");
				if (startMonth.length() == 1) {
					startMonth = "0" + startMonth;
				}
				String startDay = hdDetailJson.optString("startDay");
				if (startDay.length() == 1) {
					startDay = "0" + startDay;
				}
				String startHour = hdDetailJson.optString("startHour");
				if (startHour.length() == 1) {
					startHour = "0" + startHour;
				}
					
				try {
					start = df.parse(hdDetailJson.optString("startYear") + "-"
							+ startMonth + "-"
							+ startDay + " " 
							+ startHour + ":"
							+ hdDetailJson.optString("startMinute"));
				} catch (ParseException e) {
					start = new Date();
				}

				String endMonth = hdDetailJson.optString("endMonth");
				if (endMonth.length() == 1) {
					endMonth = "0" + endMonth;
				}
				String endDay = hdDetailJson.optString("endDay");
				if (endDay.length() == 1) {
					endDay = "0" + endDay;
				}
				String endHour = hdDetailJson.optString("endHour");
				if (endHour.length() == 1) {
					endHour = "0" + endHour;
				}
				try {
					end = df.parse(hdDetailJson.optString("endYear") + "-"
							+ endMonth + "-"
							+ endDay + " " 
					        + endHour + ":"
							+ hdDetailJson.optString("endMinute"));
				} catch (ParseException e) {
					end = new Date();
				}
				
				long diff = (end.getTime() - start.getTime()) / 1000;   
				long day= diff / (24*60*60);   
				long hour=(diff / (60*60) - day*24);   
				long min=((diff / (60)) - day*24*60 - hour*60);   
				String result = "" + day + "天" + hour + "小时" + min + "分";
				return result;
			}
		});

	}
}
