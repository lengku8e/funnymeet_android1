package com.mtcent.funnymeet.ui.activity.discovery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.control.AutoGridView;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import mtcent.funnymeet.R;

public class HDDetailsActivity extends Activity implements DownBack {
	Activity mActivity = this;
	LayoutInflater inflater;
	HdDetails info = new HdDetails();
	JSONObject hdJson = new JSONObject();
	TextView hdname;// 活动名称
	TextView hdaddress;// 活动场馆地址
	TextView hdbuilding;// 活动场馆名称
	TextView hdlocation;// 活动场所
	TextView hdstartdate;// 活动开始日期
	TextView hdDuring;  //活动时长
	TextView hdenddate;// 活动结束时间
	TextView hdallprice;// 全部价格
	TextView hdbrief;// 活动简介
	TextView hdwarning;// 活动注意事项
	//TextView hdactor;// 活动嘉宾
	TextView traffic;// 活动交通指南
	TextView hdfitpeople;// 活动适合人群
	TextView hdschedulecontent;// 活动日程
	TextView hdstarttime;
	TextView hdendtime;
	AutoGridView tags;// 标签
	ArrayList<Integer> ticketsPrice;
	ArrayList<String> dataListItemContent = new ArrayList<String>();
	BaseAdapter adapter;
	ImageView likeicon;
	ImageView joinicon;
	Animation animation;

	Intent get_intent;
	//TextView takepartin;
	int isLike;
	//已经报名参加
	boolean isSigned;
	TextView tvFavourCount; //关注人数
	TextView tvSignCount;   //参加人数
	private int favourCount = 0;
	private int signCount = 0;

	//已经关注活动？
	boolean isFocused; //
	
	//
	//private String mProjectId;
	private String mProjectGuid;

	public static class HdDetails implements Serializable {
		private static final long HdDetails = 1L;
		public static final String key = "hdDetails";
		public String id = "-1";
		public int index = 0;
		public ArrayList<String> list = new ArrayList<String>();

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		doIntent(intent);
		requestData();
		resetView();
	}

	void doIntent(Intent intent) {
		if (intent != null) {
			Bundle mBundle = intent.getExtras();
			Serializable serializable = null;
			if (mBundle != null) {
				serializable = mBundle.getSerializable(HdDetails.key);
			}
			if (serializable != null) {
				info = (HdDetails) serializable;
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hd_detail);
		init();

		if ((get_intent = this.getIntent()) != null) {
			if ("HomepageHDList".equals(get_intent.getStringExtra("from"))) {
				try {
					hdJson = new JSONObject(get_intent.getStringExtra("hdJson"));
					//this.mProjectId = hdJson.optString("id");
					this.mProjectGuid = hdJson.optString("projectGuid");
					requestDataFromProjectGuid(this.mProjectGuid);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				doIntent(get_intent);

			}
			requestData();
		} 

		resetView();

	}

	void takePartInOrWithdraw() {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);

		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());
		task.addParam("project_guid", hdJson.optString("guid"));

		if (this.isSigned) {
			task.addParam("method", "unsignProject");
		} else {
			task.addParam("method", "signProject");
			joinicon.startAnimation(animation);
		}

		SOApplication.getDownLoadManager().startTask(task);
		showWait();

	}

	void commit(String target) {
		// favorProject&user_guid=*&user_session_guid=*&....
		// String[] keys = { "project_guid" }

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);

		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());
		task.addParam("project_guid", hdJson.optString("guid"));

		if (target.equals("like")) {
			task.addParam("method", "favorProject");// 页码
		} else if (target.equals("unlike")) {
			//task.addParam("method", "unfavorClub");// 页码
			task.addParam("method", "unfavorProject");
		}

		SOApplication.getDownLoadManager().startTask(task);
		showWait();

	}

	void init() {

		isLike = 0;

		animation = AnimationUtils.loadAnimation(HDDetailsActivity.this,
				R.anim.likeiconscale);

		TextView tv = (TextView) mActivity.findViewById(R.id.titleTextView);
		tv.setText("活动详情");
		mActivity = this;
		inflater = LayoutInflater.from(mActivity);
		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

//		takepartin = (TextView) findViewById(R.id.takepartin);
		joinicon = (ImageView) findViewById(R.id.hd_detail_joinicon);
		ticketsPrice = new ArrayList<Integer>();
		hdname = (TextView) findViewById(R.id.hdname);
		hdaddress = (TextView) findViewById(R.id.hdaddress);
		hdbuilding = (TextView) findViewById(R.id.hdbuilding);
		hdlocation = (TextView) findViewById(R.id.hdlocation);
		hdstartdate = (TextView) findViewById(R.id.hdstartdate);
		hdallprice = (TextView) findViewById(R.id.ticketprice);
		hdbrief = (TextView) findViewById(R.id.hdbriefcontent);
		hdwarning = (TextView) findViewById(R.id.warningcontent);
		//hdactor = (TextView) findViewById(R.id.hdactor);
		traffic = (TextView) findViewById(R.id.hdtraffic);
		hdfitpeople = (TextView) findViewById(R.id.fitpeoplerange);
		hdschedulecontent = (TextView) findViewById(R.id.hdschedulecontent);
		tags = (AutoGridView) findViewById(R.id.tagsgrid);
		hdstarttime = (TextView) findViewById(R.id.hdstarttime);
		hdendtime = (TextView) findViewById(R.id.hdendtime);
		hdenddate = (TextView) findViewById(R.id.hdenddate);

		// 参加活动的按钮你在哪里呀~，~
//		takepartin.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				// 这里还差取消活动
//				//taktakepartin or withdraw
//				takePartInOrWithdraw();
//			}
//		});
		joinicon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 这里还差取消活动
				//taktakepartin or withdraw
				takePartInOrWithdraw();
			}
		});

		likeicon = (ImageView) findViewById(R.id.likeicon);
		likeicon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isFocused) {
					commit("like");
					likeicon.setImageResource(R.drawable.huodong_focused);
					likeicon.startAnimation(animation);
					isLike++;
				} else { //if (isLike == 1) {
					commit("unlike");
					likeicon.setImageResource(R.drawable.huodong_notfocused);
					isLike--;
				}

			}
		});
		findViewById(R.id.hdallmap).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent mIntent = new Intent(mActivity, HtmlActivity.class);
				mIntent.putExtra("title", "详细地图");
				mIntent.putExtra("html", hdJson.optString("location_map", ""));
				mActivity.startActivity(mIntent);
			}
		});

		findViewById(R.id.hdallcontent).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent mIntent = new Intent(mActivity,
								HtmlActivity.class);
						mIntent.putExtra("title", "图文详情");
						mIntent.putExtra("html",
								hdJson.optString("details", ""));
						mActivity.startActivity(mIntent);
					}
				});

		adapter = new BaseAdapter() {

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {

				if (arg1 == null) {
					arg1 = inflater.inflate(R.layout.hd_detail_tag, null);

				}
				String keytags = (String) getItem(arg0);
				if (!keytags.isEmpty()) {
					TextView findlistitem = (TextView) arg1
							.findViewById(R.id.hdtags);
					findlistitem.setText(keytags);
				}
				return arg1;
			}

			@Override
			public long getItemId(int arg0) {
				return arg0;
			}

			@Override
			public Object getItem(int arg0) {
				if (dataListItemContent.size() > arg0) {
					return dataListItemContent.get(arg0);
				}
				return null;
			}

			@Override
			public int getCount() {

				return dataListItemContent.size();
			}
		};

		tags.setAdapter(adapter);

		findViewById(R.id.uppage).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (info != null && info.index > 0) {
					info.index--;
					requestData();
				} else {
					StrUtil.showMsg(mActivity, "已经是当前页第一条");
				}
			}
		});

		findViewById(R.id.downpage).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (info != null && info.index < info.list.size() - 1) {
					info.index++;
					requestData();
				} else {
					StrUtil.showMsg(mActivity, "已经是当前页最后");
				}
			}
		});
		
		tvFavourCount = (TextView) findViewById(R.id.hd_detail_favour_count);
		tvSignCount = (TextView) findViewById(R.id.hd_detail_sign_count);
		hdDuring = (TextView) findViewById(R.id.hd_detail_during);
	}

	void resetView() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tvFavourCount.setText("" + favourCount);
				tvSignCount.setText("" + signCount);
				// 赋值

				if (hdJson != null) {

					String result = "";
					hdname.setText(new String(hdJson.optString("name"))
							.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "")
							.replaceAll("</[a-zA-Z]+[1-9]?>", "").trim());
//					hdstartdate.setText(new String(hdJson
//							.optString("startDate")));
//					hdstarttime.setText(new String(hdJson
//							.optString("startTime")));
//
//					hdenddate.setText(new String(hdJson.optString("endDate")));
//					hdendtime.setText(new String(hdJson.optString("endTime")));
					hdstartdate.setText(hdJson.optString("startYear") + "/"
							+ hdJson.optString("startMonth") + "/"
							+ hdJson.optString("startDay"));
					hdstarttime.setText(hdJson.optString("startHour") + ":"
							+ hdJson.optString("startMinute"));

					hdenddate.setText(hdJson.optString("endYear") + "/"
							+ hdJson.optString("endMonth") + "/"
							+ hdJson.optString("endDay"));
					hdendtime.setText(hdJson.optString("endHour") + ":"
							+ hdJson.optString("endMinute"));
					
					String during = getHdDuring();
					hdDuring.setText(during);

					String pro_city_distr = hdJson.optString("province") +" "
							+ hdJson.optString("city")  + " "
							+ hdJson.optString("district") + " "
							+ hdJson.optString("address");
					hdaddress.setText(pro_city_distr
							.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "")
							.replaceAll("</[a-zA-Z]+[1-9]?>", "").trim());
					
					String building = hdJson.optString("building");
					hdbuilding.setText(building.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "")
							.replaceAll("</[a-zA-Z]+[1-9]?>", "").trim());

					String location = hdJson.optString("location");
					hdlocation.setText(location.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "")
							.replaceAll("</[a-zA-Z]+[1-9]?>", "").trim());

					String tickectsInfo = new String(
							hdJson.optString("tickets"));
					if (!tickectsInfo.isEmpty()) {
						String[] tickets = tickectsInfo.split("/");
						if (tickets.length == 1 && tickets[0].equals("免费")) {
							result = "免费";
							hdallprice.setText(result);
						} else {
							for (String s : tickets) {

								if (s.matches("[0-9]+")) {
									Integer i = Integer.valueOf(s);
									ticketsPrice.add(i);
								}
							}
							Collections.sort(ticketsPrice);

							for (Iterator<Integer> iter = ticketsPrice
									.iterator(); iter.hasNext();) {
								result = result + iter.next().toString() + " ";
							}
							hdallprice.setText(result);
						}

					} else {
						hdallprice.setText("暂无");
					}

					LinearLayout v;
					if (new String(hdJson.optString("suitAge")).isEmpty()) {
						hdfitpeople.setText("暂无年龄信息");
					} else {

						hdfitpeople.setText(new String(hdJson
								.optString("suitAge"))
								.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "")
								.replaceAll("</[a-zA-Z]+[1-9]?>", "").trim());
					}

//					if (new String(hdJson.optString("guest")).isEmpty()) {
//						hdactor.setText("暂无嘉宾信息");
//					} else {
//						v = (LinearLayout) findViewById(R.id.hdguestframe);
//						v.setVisibility(View.VISIBLE);
//						hdactor.setText(new String(hdJson.optString("guest"))
//								.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "")
//								.replaceAll("</[a-zA-Z]+[1-9]?>", "").trim());
//					}

					if (new String(hdJson.optString("trafficGuide")).isEmpty()) {
						traffic.setText("暂无交通信息");
					} else {

						traffic.setText(new String(hdJson
								.optString("trafficGuide"))
								.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "")
								.replaceAll("</[a-zA-Z]+[1-9]?>", "").trim());
					}

					if (new String(hdJson.optString("notice")).isEmpty()) {
						hdwarning.setText("暂无注意事项");
					} else {

						hdwarning
								.setText(new String(hdJson.optString("notice"))
										.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>",
												"")
										.replaceAll("</[a-zA-Z]+[1-9]?>", "")
										.trim());
					}

					if (new String(hdJson.optString("schedule")).isEmpty()) {
						hdschedulecontent.setText("暂无日程安排信息");
					} else {

						hdschedulecontent.setText(new String(hdJson
								.optString("schedule"))
								.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "")
								.replaceAll("</[a-zA-Z]+[1-9]?>", "").trim());
					}

					if (new String(hdJson.optString("suitDetail")).isEmpty()) {
						hdbrief.setText("暂无活动简介");
					} else {

						hdbrief.setText(new String(hdJson
								.optString("suitDetail")));
					}
					String rawList = new String(
							hdJson.optString("category_tags"));
					String[] tempList = rawList.split(" ");
					dataListItemContent.clear();
					for (String s : tempList) {
						s = s.trim();
						if (!s.isEmpty()) {
							dataListItemContent.add(s);
						}

					}
					adapter.notifyDataSetChanged();
				}
			}

			private String getHdDuring() {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
				Date start;
				Date end;
				String startMonth = hdJson.optString("startMonth");
				if (startMonth.length() == 1) {
					startMonth = "0" + startMonth;
				}
				String startDay = hdJson.optString("startDay");
				if (startDay.length() == 1) {
					startDay = "0" + startDay;
				}
				String startHour = hdJson.optString("startHour");
				if (startHour.length() == 1) {
					startHour = "0" + startHour;
				}
					
				try {
					start = df.parse(hdJson.optString("startYear") + "-"
							+ startMonth + "-"
							+ startDay + " " 
							+ startHour + ":"
							+ hdJson.optString("startMinute"));
				} catch (ParseException e) {
					start = new Date();
				}

				String endMonth = hdJson.optString("endMonth");
				if (endMonth.length() == 1) {
					endMonth = "0" + endMonth;
				}
				String endDay = hdJson.optString("endDay");
				if (endDay.length() == 1) {
					endDay = "0" + endDay;
				}
				String endHour = hdJson.optString("endHour");
				if (endHour.length() == 1) {
					endHour = "0" + endHour;
				}
				try {
					end = df.parse(hdJson.optString("endYear") + "-"
							+ endMonth + "-"
							+ endDay + " " 
					        + endHour + ":"
							+ hdJson.optString("endMinute"));
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

	private void requestClubFavourCount() {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);

		task.addParam("method", "getProjectFavourCount");
		task.addParam("project_guid", this.mProjectGuid);

		SOApplication.getDownLoadManager().startTask(task);
	}

	private void requestClubSignCount() {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);

		task.addParam("method", "getProjectSignCount");
		task.addParam("project_guid", this.mProjectGuid);

		SOApplication.getDownLoadManager().startTask(task);
	}

	/**
	 * 
	 * @param projectGuid
	 */
	private void requestDataFromProjectGuid(String projectGuid) {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST,
				null, RequestHelper.Type_DownJsonString, null, 0, true);
		task.addParam("method", "findProjectByGuid");
		task.addParam("project_guid", projectGuid);
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}
	
	void requestData() {
		if (info != null && info.index < info.list.size()) {
			Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST,
					null, RequestHelper.Type_DownJsonString, null, 0, true);
			task.addParam("method", "findProjectByGuid");
			task.addParam("project_guid", this.mProjectGuid);
			task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
			SOApplication.getDownLoadManager().startTask(task);
			showWait();
		}
		requestClubFavourCount();
		requestClubSignCount();
	}

	@Override
	public void onFinish(Pdtask t) {

		ArrayList<JSONObject> list = StrUtil.getJSONArrayList(t.json);
		if ("findProjectByGuid".equals(t.getParam("method")) && t.json != null
				&& "ok".equals(t.json.optString("status"))) {
			hdJson = t.json.optJSONObject("results");

			if (hdJson == null) {
				hdJson = new JSONObject();
			} else {
				//assert if user attened the project
				Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST,
						null, RequestHelper.Type_DownJsonString, null, 0, true);
				task.addParam("method", "findUserSignFavorProject");// 页码
				task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
				task.addParam("project_guid", hdJson.optString("guid"));
				SOApplication.getDownLoadManager().startTask(task);
				showWait();
				
				//assert if user focused the project
//				Pdtask taskFavorite = new Pdtask(this, this, SOApplication.SERVICE_HOST,
//						null, RequestHelper.Type_DownJsonString, null, 0, true);
//				task.addParam("method", "findUserFavoriteProject");// 页码
//				task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
//				task.addParam("project_guid", hdJson.optString("guid"));
//				SOApplication.getDownLoadManager().startTask(taskFavorite);
//				showWait();
			}

			resetView();

		} else if ("signProject".equals(t.getParam("method"))) {

			// 参加活动的
			if (t.json != null) {
				StrUtil.showMsg(HDDetailsActivity.this,
						t.json.optJSONObject("results").optString("message"));

				final JSONObject j = t.json;
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
//						takepartin.setText(j.optJSONObject("results")
//								.optString("message"));
						if ("1".equals(j.optJSONObject("results").optString("success"))) {
							joinicon.setImageResource(R.drawable.huodong_joined);
							signCount++;
							tvSignCount.setText("" + signCount);
							isSigned = true;
						} else {
							isSigned = false;
							joinicon.setImageResource(R.drawable.huodong_notjoined);
						}
					}
				});
			}
		} else if ("getProjectFavourCount".equals(t.getParam("method"))) {
			if (t.json != null) {
				this.favourCount = t.json.optJSONObject("results").optInt("intExtra");
				hideWait();
			}
		} else if ("getProjectSignCount".equals(t.getParam("method"))) {
			if (t.json != null) {
				this.signCount = t.json.optJSONObject("results").optInt("intExtra");
				hideWait();
			}
		} else if ("findUserSignFavorProject".equals(t.getParam("method"))) {

			// 参加活动?或者关注活动？
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
//							takepartin.setText("报名");
							joinicon.setImageResource(R.drawable.huodong_notjoined);
							isSigned = false;
							likeicon.setImageResource(R.drawable.huodong_notfocused);
							isFocused = false;
							break;
						case 10:
//							takepartin.setText("报名");
							joinicon.setImageResource(R.drawable.huodong_notjoined);
							isSigned = false;
							likeicon.setImageResource(R.drawable.huodong_focused);
							isFocused = true;
							break;
						case 1:
//							takepartin.setText("已报名");
							joinicon.setImageResource(R.drawable.huodong_joined);
							isSigned = true;
							likeicon.setImageResource(R.drawable.huodong_notfocused);
							isFocused = false;
							break;
						case 11:
//							takepartin.setText("已报名");
							joinicon.setImageResource(R.drawable.huodong_joined);
							isSigned = true;
							likeicon.setImageResource(R.drawable.huodong_focused);
							isFocused = true;
							break;
						}
							
					}
				});

			}
		} else if ("findUserFavoriteProject".equals(t.getParam("method"))) {

			// 是否关注活动？
			if (t.json != null) {
				final JSONObject j = t.json;
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// 
						if (!"ok".equals(j.optJSONObject("status"))) {
							return;
						}
						if ("10".equals(j.optJSONObject("results").optString("success")) || 
								"10".equals(j.optJSONObject("results").optString("success"))) {
							isFocused = true;
							likeicon.setImageResource(R.drawable.huodong_focused);
						} else {
							isFocused = false;
							likeicon.setImageResource(R.drawable.huodong_notfocused);
						}
					}
				});
			}
		} else if ("unsignProject".equals(t.getParam("method"))) {

			// 取消活动报名
			if (t.json != null) {
				final JSONObject j = t.json;
				StrUtil.showMsg(HDDetailsActivity.this,
						t.json.optJSONObject("results").optString("message"));
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						
//						takepartin.setText(j.optJSONObject("results")
//								.optString("message"));
						if ("1".equals(j.optJSONObject("results").optString("success"))) {
							isSigned = false;
							signCount--;
							tvSignCount.setText("" + signCount);
							joinicon.setImageResource(R.drawable.huodong_notjoined);
						} else {
							isSigned = true;
							joinicon.setImageResource(R.drawable.huodong_joined);
						}
					}
				});

			}
		}
		if ("favorProject".equals(t.getParam("method"))) {

			if (t.json != null) {
				final JSONObject result = t.json.optJSONObject("results");
				String msg = result.optString("message");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if ("1".equals(result.optString("success"))) {
							isFocused = true;
							favourCount++;
							tvFavourCount.setText("" + favourCount);
						} else {
							isFocused = false;
						}
					}
				});
				StrUtil.showMsg(HDDetailsActivity.this, msg);
			}

		//} else if ("unfavorClub".equals(t.getParam("method"))) {
		} else if ("unfavorProject".equals(t.getParam("method"))) {
			if (t.json != null) {
				final JSONObject result = t.json.optJSONObject("results");
				String msg = result.optString("message");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if ("1".equals(result.optString("success"))) {
							isFocused = false;
							favourCount--;
							tvFavourCount.setText("" + favourCount);
						} else {
							isFocused = true;
						}
					}
				});
				StrUtil.showMsg(HDDetailsActivity.this, msg);
			}

		}
		hideWait();

	}

	@Override
	public void onUpdate(Pdtask t) {
		onFinish(t);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

		}
		return super.onKeyDown(keyCode, event);
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
}
