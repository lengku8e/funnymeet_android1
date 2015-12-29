package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import mtcent.funnymeet.R;

public class MyCreateClubFinishActivity extends Activity {

	TextView my_clubs_create_finish;
	TextView titleTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_clubs_create_finish);
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

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("创建成功");

		my_clubs_create_finish = (TextView) findViewById(R.id.my_clubs_create_finish);
		my_clubs_create_finish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MyCreateClubFinishActivity.this,
						MyCreateClubCompleteInfoActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

}
