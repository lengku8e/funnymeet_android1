package com.mtcent.funnymeet.ui.activity.club;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.view.control.XVURLImageView;

import mtcent.funnymeet.R;

public class PersonInfoActivity extends Activity {

	TextView person_info_nickname;
	XVURLImageView person_info_headImage;
	Intent intent;
	String nickName;
	String headImageUrl;
	TextView person_info_sentmessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addressbook_person_info);
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

		person_info_nickname = (TextView) findViewById(R.id.person_info_nickname);
		person_info_headImage = (XVURLImageView) findViewById(R.id.person_info_headimage);
		person_info_sentmessage = (TextView) findViewById(R.id.person_info_sentmessage);

		person_info_sentmessage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("nickname", nickName);
				intent.putExtra("imageUrl", headImageUrl);
				intent.setClass(PersonInfoActivity.this, HostChatActivity.class);
				PersonInfoActivity.this.startActivity(intent);
			}
		});

		intent = this.getIntent();

		if (intent != null) {

			nickName = intent.getStringExtra("nickname");
			headImageUrl = intent.getStringExtra("imageUrl");
			person_info_nickname.setText(nickName);
			person_info_headImage.setImageUrl(headImageUrl);

		}

		TextView tv = (TextView) findViewById(R.id.titleTextView);
		tv.setText("详细资料 ");

	}
}
