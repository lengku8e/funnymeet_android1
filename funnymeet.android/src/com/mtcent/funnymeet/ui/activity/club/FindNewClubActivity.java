package com.mtcent.funnymeet.ui.activity.club;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.util.SearchUtilStack;
import com.mtcent.funnymeet.ui.activity.discovery.search.SearchActivity;

import mtcent.funnymeet.R;

public class FindNewClubActivity extends Activity {

	LinearLayout addressbook_findclub_to_search;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addressbook_findclub);
		SearchUtilStack.searchUtilStack.add(this);
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

		TextView tv = (TextView) findViewById(R.id.titleTextView);
		tv.setText("俱乐部搜索");

		addressbook_findclub_to_search = (LinearLayout) findViewById(R.id.addressbook_findclub_to_search);
		addressbook_findclub_to_search
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(FindNewClubActivity.this,
								SearchActivity.class);
						startActivity(intent);
					}
				});
	}

}
