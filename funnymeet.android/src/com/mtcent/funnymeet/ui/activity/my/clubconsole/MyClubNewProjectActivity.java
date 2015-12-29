package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mtcent.funnymeet.R;

public class MyClubNewProjectActivity extends BaseActivity {
	/**
	 * 地区选择时的请求代码
	 */
	private final int FIRST_REQUEST_CODE = 1;

	/**
	 * 新建活动
	 */
	public static final int HANDLE_TYPE_NEW = 1;
	
	public static final String FEE_TYPE_FEE = "收费";
	
	public static final String FEE_TYPE_FREE = "免费";

	/**
	 * 修改活动
	 */
	public static final int HANDLE_TYPE_EDIT = 2;

	private int mHdHandleType = HANDLE_TYPE_NEW;

	public static final String EXTRA_PARAM_HANDLE_TYPE = "EXTRA_PARAM_HANDLE_TYPE";

	public static final String EXTRA_PARAM_HD_JSON_INFO = "EXTRA_PARAM_HD_JSON_INFO";

	public static final String EXTRA_PARAM_CLUB_JSON_INFO = "jsonobjc";

	public static final String EXTRA_PARAM_ADDRESS = "EXTRA_PARAM_ADDRESS";

	TextView titleTextView;
	// LinearLayout my_clubs_organise_hd_stardate;
	// LinearLayout my_clubs_organise_hd_enddate;
	EditText club_hd_start_date_year;
	EditText club_hd_start_date_month;
	EditText club_hd_start_date_day;
	EditText club_hd_end_date_year;
	EditText club_hd_end_date_month;
	EditText club_hd_end_date_day;
	EditText club_hd_start_time_h;
	EditText club_hd_start_time_m;
	EditText club_hd_end_time_h;
	EditText club_hd_end_time_m;

	// TextView my_clubs_organisehd_date;
	Intent get_intent;

	LinearLayout start_picker_container;
	LinearLayout end_picker_container;

	CustomDialog startTimeDialog;
	CustomDialog endTimeDialog;
	
	CustomDialog dialog;

	TimePicker timePic1;

	LayoutInflater inflater;

	TimePicker club_start_timepick;
	DatePicker club_start_datepick;

	TimePicker club_end_timepick;
	DatePicker club_end_datepick;

	TextView my_clubs_organise_hd_stardate_show;
	TextView my_clubs_organise_hd_enddate_show;
	TextView my_clubs_club_organise_timepicker_confirm;

	EditText club_hd_title;
	// EditText club_hd_ticketprice;
	TextView club_hd_fee_type;
	// EditText club_hd_communication;

	// TextView club_hd_province;
	// EditText club_hd_city;
	TextView club_hd_district;
	EditText club_hd_address;
	EditText club_hd_building;
	EditText club_hd_location;

	EditText club_hd_brief;
	EditText club_hd_detailinfo;

	JSONObject clubInfoJson;
	JSONObject hdJsonInfo;

	ImageView feeSelected;
	ImageView freeSelected;

	public static final int CREATE_CLUBHD_COMPLETED = 1557;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_clubs_club_organise_hd);
		init();
	}

	/*
		发起新活动
	 */
	void commit(String club_guid, String name, String start_year,
			String start_month, String start_day, String start_hour,
			String start_minute, String end_year, String end_month,
			String end_day, String end_hour, String end_minute,
			String province, String city, String district, String address,
			String building, String location, String detail, String feeType) {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		if (this.mHdHandleType == HANDLE_TYPE_EDIT) {
			task.addParam("method", "updateProject");
			task.addParam("project_guid", this.hdJsonInfo.optString("guid"));
		} else {
			task.addParam("method", "insertProject");
		}
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());
		task.addParam("club_guid", club_guid);
		task.addParam("name", name);
		task.addParam("start_year", start_year);
		task.addParam("start_month", start_month);
		task.addParam("start_day", start_day);
		task.addParam("start_hour", start_hour);
		task.addParam("start_minute", start_minute);
		task.addParam("end_year", end_year);
		task.addParam("end_month", end_month);
		task.addParam("end_day", end_day);
		task.addParam("end_hour", end_hour);
		task.addParam("end_minute", end_minute);
		task.addParam("province", province);
		task.addParam("city", city);
		task.addParam("district", district);
		task.addParam("address", address);
		task.addParam("building", building);
		task.addParam("location", location);
		task.addParam("detail", detail);
		task.addParam("is_free", feeType);

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	@Override
	public void onFinish(Pdtask t) {
		boolean succ = false;
		if (t.getParam("method").equals("insertProject")) {
			if (t.json != null && "ok".equals(t.json.optString("status"))) {
				succ = true;
			}
		} else if ("updateProject".equals(t.getParam("method"))) {
			if (t.json != null && "ok".equals(t.json.optString("status"))) {
				succ = true;
			}
		}

		if (succ) {
			setResult(CREATE_CLUBHD_COMPLETED);
			finish();
		} else {
			StrUtil.showMsg(MyClubNewProjectActivity.this, "发布失败");
			hideWait();
		}
		super.onFinish(t);
	}

	void showStartTimeDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				startTimeDialog.show();
			}
		});
	}

	void hideStartTimeDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				startTimeDialog.hide();
			}
		});
	}

	void showEndTimeDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				endTimeDialog.show();
			}
		});
	}

	void hideEndTimeDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				endTimeDialog.hide();
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == FIRST_REQUEST_CODE
				&& resultCode == MyClubNewProjectActivity.RESULT_FIRST_USER) {
			if (data != null) {
				club_hd_district.setText(data
						.getStringExtra(EXTRA_PARAM_ADDRESS));
			}
		}

	}

	@SuppressLint("NewApi")
	protected void init() {

		inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		//加粗字体
		TextView addressLabel = (TextView)findViewById(R.id.club_hd_address_label);
		TextPaint tp = addressLabel.getPaint(); 
		tp.setFakeBoldText(true);
		
		// 活动的一些属性
		club_hd_title = (EditText) findViewById(R.id.club_hd_title);
		// club_hd_ticketprice = (EditText)
		// findViewById(R.id.club_hd_ticketprice);
		club_hd_fee_type = (TextView) findViewById(R.id.club_hd_fee_type);
		club_hd_fee_type.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog == null) {
					dialog = new CustomDialog(MyClubNewProjectActivity.this);
					dialog.setContentView(R.layout.my_club_fee_type_dialog);
					dialog.setCancelable(true);
					dialogfunction();
				}
				dialog.show();

			}
		});
		// club_hd_communication = (EditText)
		// findViewById(R.id.club_hd_communication);

		// 地区
		club_hd_district = (TextView) findViewById(R.id.club_hd_district);
		club_hd_district.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MyClubNewProjectActivity.this,
						AreaListActivity.class);
				startActivityForResult(intent, FIRST_REQUEST_CODE);

			}
		});
		// club_hd_city = (EditText) findViewById(R.id.club_hd_city);
		// club_hd_district = (EditText) findViewById(R.id.club_hd_district);
		club_hd_address = (EditText) findViewById(R.id.club_hd_address);
		club_hd_building = (EditText) findViewById(R.id.club_hd_building);
		club_hd_location = (EditText) findViewById(R.id.club_hd_location);

		// club_hd_brief = (EditText) findViewById(R.id.club_hd_brief);
		// club_hd_detailinfo = (EditText)
		// findViewById(R.id.club_hd_detailinfo);

		// ---------------活动开始时间对话框----------------------------------

		// ---------------活动开始时间对话框----------------------------------

		// ---------------活动结束时间对话框----------------------------------

		// endTime

		// ---------------活动结束时间对话框----------------------------------

		get_intent = getIntent();
		if (get_intent != null) {
			// my_clubs_organisehd_date.setText(get_intent
			// .getStringExtra("selectedDate"));
			try {
				if (get_intent.hasExtra("jsonobjc")) {
					clubInfoJson = new JSONObject(
							get_intent.getStringExtra("jsonobjc"));
				}
				if (get_intent.hasExtra(EXTRA_PARAM_HANDLE_TYPE)) {
					this.mHdHandleType = get_intent.getIntExtra(
							EXTRA_PARAM_HANDLE_TYPE, HANDLE_TYPE_NEW);
				}
				if (get_intent.hasExtra(EXTRA_PARAM_HD_JSON_INFO)) {
					hdJsonInfo = new JSONObject(
							get_intent.getStringExtra(EXTRA_PARAM_HD_JSON_INFO));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		club_hd_start_date_year = (EditText) findViewById(R.id.club_hd_start_date_year);
		club_hd_start_date_year.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				this.temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (this.temp.length() >= 4) {
					club_hd_start_date_month.requestFocus();
				}
			}

		});
		club_hd_start_date_month = (EditText) findViewById(R.id.club_hd_start_date_month);
		club_hd_start_date_month.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				this.temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (this.temp.length() >= 2) {
					club_hd_start_date_day.requestFocus();
				}
			}

		});
		club_hd_start_date_day = (EditText) findViewById(R.id.club_hd_start_date_day);
		club_hd_start_date_day.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				this.temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (this.temp.length() >= 2) {
					club_hd_start_time_h.requestFocus();
				}
			}

		});
		club_hd_end_date_year = (EditText) findViewById(R.id.club_hd_end_date_year);
		club_hd_end_date_year.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				this.temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (this.temp.length() >= 4) {
					club_hd_end_date_month.requestFocus();
				}
			}

		});
		club_hd_end_date_month = (EditText) findViewById(R.id.club_hd_end_date_month);
		club_hd_end_date_month.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				this.temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (this.temp.length() >= 2) {
					club_hd_end_date_day.requestFocus();
				}
			}

		});
		club_hd_end_date_day = (EditText) findViewById(R.id.club_hd_end_date_day);
		club_hd_end_date_day.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				this.temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (this.temp.length() >= 2) {
					club_hd_end_time_h.requestFocus();
				}
			}

		});
		club_hd_start_time_h = (EditText) findViewById(R.id.club_hd_start_time_h);
		club_hd_start_time_h.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				this.temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (this.temp.length() >= 2) {
					club_hd_start_time_m.requestFocus();
				}
			}

		});
		club_hd_start_time_m = (EditText) findViewById(R.id.club_hd_start_time_m);
		club_hd_start_time_m.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				this.temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (this.temp.length() >= 2) {
					club_hd_end_date_year.requestFocus();
				}
			}

		});

		club_hd_end_time_h = (EditText) findViewById(R.id.club_hd_end_time_h);
		club_hd_end_time_h.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				this.temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (this.temp.length() >= 2) {
					club_hd_end_time_m.requestFocus();
				}
			}

		});		
		club_hd_end_time_m = (EditText) findViewById(R.id.club_hd_end_time_m);

		TextView finishButton = (TextView) findViewById(R.id.createhd);
		finishButton.setVisibility(View.VISIBLE);
		finishButton.setText("发起活动");
		finishButton.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onClick(View v) {
				String start_year = "";
				String start_month = "";
				String start_day = "";
				String start_hour = "";
				String start_minute = "";
				String end_year = "";
				String end_month = "";
				String end_day = "";
				String end_hour = "";
				String end_minute = "";
				String title = club_hd_title.getText().toString();
				if ("".equals(title)) {
					StrUtil.showMsg(MyClubNewProjectActivity.this, "活动标题不能为空");
					return;
				}
				String isFree = FEE_TYPE_FREE.equals(club_hd_fee_type.getText()) ? "Y" : "N";
				String startDateText = club_hd_start_date_year.getText()
						.toString()
						+ "-"
						+ club_hd_start_date_month.getText().toString()
						+ "-"
						+ club_hd_start_date_day.getText().toString();
				String startTimeText = club_hd_start_time_h.getText()
						.toString()
						+ ":"
						+ club_hd_start_time_m.getText().toString();
				Date startDate = null;
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
				try {
					dfDate.parse(startDateText);
				} catch (ParseException e) {
					StrUtil.showMsg(MyClubNewProjectActivity.this,
							"请输入正确的日期,比如2015-01-01");
					return;
				}
				SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm");
				try {
					dfTime.parse(startTimeText);
				} catch (ParseException e) {
					StrUtil.showMsg(MyClubNewProjectActivity.this,
							"请输入正确的时间,比如08:10");
					return;
				}
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				try {
					startDate = df.parse(startDateText + " " + startTimeText);
				} catch (ParseException e1) {
					// already checked.
				}
				cal.setTime(startDate);
				start_year = Integer.toString(cal.get(Calendar.YEAR));
				start_month = Integer.toString(cal.get(Calendar.MONTH) + 1);
				start_day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
				start_hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
				start_minute = Integer.toString(cal.get(Calendar.MINUTE));

				String endDateText = club_hd_end_date_year.getText().toString()
						+ "-" + club_hd_start_date_month.getText().toString()
						+ "-" + club_hd_start_date_day.getText().toString();
				String endTimeText = club_hd_end_time_h.getText().toString()
						+ ":" + club_hd_end_time_m.getText().toString();
				Date endDate = null;
				try {
					dfDate.parse(endDateText);
				} catch (ParseException e) {
					StrUtil.showMsg(MyClubNewProjectActivity.this,
							"请输入正确的日期,比如2015-01-01");
					return;
				}
				try {
					dfTime.parse(endTimeText);
				} catch (ParseException e) {
					StrUtil.showMsg(MyClubNewProjectActivity.this,
							"请输入正确的时间,比如18:10");
					return;
				}
				try {
					endDate = df.parse(endDateText + " " + endTimeText);
				} catch (ParseException e) {
					// already checked
				}
				cal.setTime(endDate);
				end_year = Integer.toString(cal.get(Calendar.YEAR));
				end_month = Integer.toString(cal.get(Calendar.MONTH) + 1);
				end_day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
				end_hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
				end_minute = Integer.toString(cal.get(Calendar.MINUTE));

				String districtOrg = club_hd_district.getText().toString();
				String[] proCityDistes = districtOrg.split(" ");
				String province = null;
				if (proCityDistes != null && proCityDistes.length >= 1) {
					province = proCityDistes[0];
				} else {
					StrUtil.showMsg(MyClubNewProjectActivity.this, "省份不能为空");
					return;
				}
				String city = club_hd_district.getText().toString();
				if (proCityDistes != null && proCityDistes.length >= 2) {
					city = proCityDistes[1];
				} else {
					StrUtil.showMsg(MyClubNewProjectActivity.this, "城市不能为空");
					return;
				}
				String district = club_hd_district.getText().toString();
				if (proCityDistes != null && proCityDistes.length >= 3) {
					district = proCityDistes[2];
				} else {
					StrUtil.showMsg(MyClubNewProjectActivity.this, "区县不能为空");
					return;
				}
				String address = club_hd_address.getText().toString();
				if ("".equals(address)) {
					StrUtil.showMsg(MyClubNewProjectActivity.this, "地址不能为空");
					return;
				}
				String building = club_hd_building.getText().toString();
				String location = club_hd_location.getText().toString();

				String guid = "";
				if (guid.equals(clubInfoJson.optString("clubGuid"))) {
					guid = clubInfoJson.optString("guid");
				} else {
					guid = clubInfoJson.optString("clubGuid");
				}
				commit(guid, title, start_year, start_month, start_day,
						start_hour, start_minute, end_year, end_month, end_day,
						end_hour, end_minute, province, city, district,
						address, building, location, "", isFree);
			}

		});
		if (mHdHandleType == HANDLE_TYPE_EDIT) {
			fillValues();
		}

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("发起活动");
	}

	private void dialogfunction() {

		feeSelected = (ImageView) dialog.findViewById(R.id.fee_selected);
		freeSelected = (ImageView) dialog.findViewById(R.id.free_selected);

		if (FEE_TYPE_FEE.equals(club_hd_fee_type.getText().toString())) {
			feeSelected.setVisibility(View.VISIBLE);
		} else {
			freeSelected.setVisibility(View.VISIBLE);
		}

		LinearLayout feeTypeFee = (LinearLayout) dialog
				.findViewById(R.id.fee_type_fee);
		LinearLayout feeTypeFree = (LinearLayout) dialog
				.findViewById(R.id.fee_type_free);
		feeTypeFee.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				feeSelected.setVisibility(View.VISIBLE);
				freeSelected.setVisibility(View.GONE);
				club_hd_fee_type.setText(FEE_TYPE_FEE);
				dialog.dismiss();
			}
		});

		feeTypeFree.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				freeSelected.setVisibility(View.VISIBLE);
				feeSelected.setVisibility(View.GONE);
				club_hd_fee_type.setText(FEE_TYPE_FREE);
				dialog.dismiss();
			}
		});

	}
	/**
	 * 
	 * @param hdJsonInfo2
	 */
	private void fillValues() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				club_hd_title.setText(hdJsonInfo.optString("name"));
				//

				club_hd_start_date_year.setText(hdJsonInfo
						.optString("startYear"));
				club_hd_start_date_month.setText(hdJsonInfo
						.optString("startMonth"));
				club_hd_start_date_day
						.setText(hdJsonInfo.optString("startDay"));
				club_hd_start_time_h.setText(hdJsonInfo.optString("startHour"));
				club_hd_start_time_m.setText(hdJsonInfo
						.optString("startMinute"));

				club_hd_end_date_year.setText(hdJsonInfo.optString("endYear"));
				club_hd_end_date_month
						.setText(hdJsonInfo.optString("endMonth"));
				club_hd_end_date_day.setText(hdJsonInfo.optString("endDay"));
				club_hd_end_time_h.setText(hdJsonInfo.optString("endHour"));
				club_hd_end_time_m.setText(hdJsonInfo.optString("endMinute"));

				String province = hdJsonInfo.optString("province");
				String city = hdJsonInfo.optString("city");
				String d = hdJsonInfo.optString("district");
				club_hd_district.setText(province + " " + city + " " + d);
				club_hd_address.setText(hdJsonInfo.optString("address"));
				club_hd_building.setText(hdJsonInfo.optString("building"));
				club_hd_location.setText(hdJsonInfo.optString("location"));
				// detail
				//
				if ("Y".equals(hdJsonInfo.optString("isFree"))) {
					club_hd_fee_type.setText(FEE_TYPE_FREE);
				} else {
					club_hd_fee_type.setText(FEE_TYPE_FEE);
				}
			}
		});
	}
}
