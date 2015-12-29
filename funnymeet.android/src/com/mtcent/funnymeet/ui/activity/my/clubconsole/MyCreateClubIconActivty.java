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

public class MyCreateClubIconActivty extends Activity {

	TextView titleTextView;
	String imageFilePath = "";
	String imageFileHash = null;
	TextView my_clubs_create_chooseicon;
	XVURLImageView my_clubs_create_realicon;
	ImageView my_clubs_create_fakeicon;
	TextView next_step;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_clubs_create_icon);
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

		next_step = (TextView) findViewById(R.id.next_step);
		next_step.setVisibility(View.VISIBLE);
		next_step.setText("提交");
		next_step.setTextColor(0xffffffff);

		next_step.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MyCreateClubIconActivty.this,
						MyCreateClubFinishActivity.class);
				startActivity(intent);
				finish();
			}
		});

		my_clubs_create_realicon = (XVURLImageView) findViewById(R.id.my_clubs_create_realicon);
		my_clubs_create_fakeicon = (ImageView) findViewById(R.id.my_clubs_create_fakeicon);

		my_clubs_create_chooseicon = (TextView) findViewById(R.id.my_clubs_create_chooseicon);
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("设置俱乐部标识");

		my_clubs_create_chooseicon
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(MyCreateClubIconActivty.this,
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
				my_clubs_create_realicon.setImageUrl(null);
				my_clubs_create_realicon.setImageBitmap(bitmap);
				my_clubs_create_realicon.setVisibility(View.VISIBLE);
				my_clubs_create_fakeicon.setVisibility(View.GONE);
			}
		}

	}
}
