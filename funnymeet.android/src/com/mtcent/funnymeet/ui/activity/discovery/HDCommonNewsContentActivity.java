package com.mtcent.funnymeet.ui.activity.discovery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import mtcent.funnymeet.R;

public class HDCommonNewsContentActivity extends Activity {

	TextView titleTextView;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_common_news_content);
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

		intent = this.getIntent();
		String title = intent.getStringExtra("title");

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText(title);

	}
}
