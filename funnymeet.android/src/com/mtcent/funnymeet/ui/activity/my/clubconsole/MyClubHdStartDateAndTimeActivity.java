package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import mtcent.funnymeet.R;

public class MyClubHdStartDateAndTimeActivity extends Activity {

	TextView titleTextView;
	TimePicker hd_start_timepicke;
	DatePicker hd_start_datepicker;
	Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.my_clubs_club_organise_hd_start_dateandtime);
		setContentView(R.layout.club_hd_start_dateandtime);
		// initViewControl();

	}

	protected void init() {

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		View finishButton = findViewById(R.id.finishbutton);
		finishButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("活动日期和时间");

		// hd_start_timepicke = (TimePicker)
		// findViewById(R.id.hd_start_timepicker);
		//
		// hd_start_timepicke.setIs24HourView(true);
		//
		// hd_start_datepicker = (DatePicker)
		// findViewById(R.id.hd_start_datepicker);

	}

}
