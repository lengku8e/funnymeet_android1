package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.view.control.XVURLImageView;

import org.json.JSONException;
import org.json.JSONObject;

import mtcent.funnymeet.R;

public class MyClubDetailInfoQrCodeActivity extends Activity {

	Intent get_intent;
	XVURLImageView my_clubs_clubinfo_qrcode_clubicon;
	TextView my_clubs_clubinfo_qrcode_clubname;
	TextView titleTextView;
	JSONObject clubJson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_clubs_clubinfo_qrcode);
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
		titleTextView.setText("俱乐部二维码");

		get_intent = this.getIntent();
		if (get_intent != null) {
			try {
				clubJson = new JSONObject(get_intent.getStringExtra("clubJson"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		my_clubs_clubinfo_qrcode_clubicon = (XVURLImageView) findViewById(R.id.my_clubs_clubinfo_qrcode_clubicon);

		my_clubs_clubinfo_qrcode_clubname = (TextView) findViewById(R.id.my_clubs_clubinfo_qrcode_clubname);

		if (clubJson != null) {
			my_clubs_clubinfo_qrcode_clubicon.setImageUrl(clubJson
					.optString("logoUrl"));
			my_clubs_clubinfo_qrcode_clubname.setText(clubJson
					.optString("name"));
		}

	}
}
