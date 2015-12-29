package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.view.control.XVURLImageView;

import mtcent.funnymeet.R;

public class MyCreateClubCompleteInfoActivity extends Activity {

	XVURLImageView my_clubs_create_complete_newicon;
	ImageView my_clubs_create_complete_chooseicon;
	ImageView my_clubs_create_complete_fakeicon;
	String imageFilePath = "";
	String imageFileHash = null;
	TextView titleTextView;
	TextView next_step;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_clubs_create_completeinfo);
		init();
	}

	protected void init() {

		next_step = (TextView) findViewById(R.id.next_step);
		next_step.setVisibility(View.VISIBLE);
		next_step.setText("保存");
		next_step.setTextColor(0xffffffff);

		next_step.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MyCreateClubCompleteInfoActivity.this,
						MyClubListActivity.class);
				startActivity(intent);
				finish();
			}
		});

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("完善俱乐部资料");

		my_clubs_create_complete_newicon = (XVURLImageView) this
				.findViewById(R.id.my_clubs_create_complete_newicon);
		my_clubs_create_complete_fakeicon = (ImageView) findViewById(R.id.my_clubs_create_complete_fakeicon);

		my_clubs_create_complete_newicon
				.setImageUrl("http://img00.hc360.com/led/201405/201405151427485568.jpg");

		my_clubs_create_complete_chooseicon = (ImageView) this
				.findViewById(R.id.my_clubs_create_complete_chooseicon);
		my_clubs_create_complete_chooseicon
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(MyCreateClubCompleteInfoActivity.this,
								SelectImageActivity.class);
						intent.putExtra("path", imageFilePath);
						intent.putExtra("w", 400);
						intent.putExtra("h", 400);
						startActivityForResult(intent, 123);
					}
				});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 556 && resultCode == RESULT_FIRST_USER) {
			setResult(RESULT_FIRST_USER);
			finish();
		} else if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 123 && null != data) {
				imageFilePath = data.getStringExtra("path");
				imageFileHash = null;
				Options options = new Options();
				Bitmap bitmap = BitmapFactory
						.decodeFile(imageFilePath, options);// 解码图片
				my_clubs_create_complete_newicon.setImageUrl(null);
				my_clubs_create_complete_fakeicon.setImageBitmap(bitmap);
				my_clubs_create_complete_newicon.setVisibility(View.GONE);
				my_clubs_create_complete_fakeicon.setVisibility(View.VISIBLE);

			}
		}

	}

}
