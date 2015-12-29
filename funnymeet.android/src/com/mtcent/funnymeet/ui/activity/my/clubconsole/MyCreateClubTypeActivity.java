package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.model.ClubActivityList;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;

import org.json.JSONArray;
import org.json.JSONObject;

import mtcent.funnymeet.R;

public class MyCreateClubTypeActivity extends BaseActivity {

	TextView titleTextView;
	LinearLayout my_clubs_create_type_private;
	LinearLayout my_clubs_create_type_public;
	JSONArray clubTypeList;
	TextView public_club_title;
	TextView private_club_title;

	TextView public_club_brief;
	TextView private_club_brief;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_clubs_createnewclub);
		init();
		requestData();
	}

	protected void init() {

		ClubActivityList.myActivityList
				.add(MyCreateClubTypeActivity.this);

		public_club_title = (TextView) findViewById(R.id.public_club_title);
		private_club_title = (TextView) findViewById(R.id.private_club_title);
		public_club_brief = (TextView) findViewById(R.id.public_club_brief);
		private_club_brief = (TextView) findViewById(R.id.private_club_brief);

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		my_clubs_create_type_private = (LinearLayout) findViewById(R.id.my_clubs_create_type_private);
		my_clubs_create_type_public = (LinearLayout) findViewById(R.id.my_clubs_create_type_public);

		my_clubs_create_type_private
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(MyCreateClubTypeActivity.this,
								MyCreateClubNameActivity.class);
						JSONObject jsonObject = null;

						for (int i = 0; i < clubTypeList.length(); i++) {

							jsonObject = clubTypeList.optJSONObject(i);

							if (jsonObject.optString("name").equals("私人俱乐部")) {
								break;
							}
						}

						if (jsonObject != null) {

							intent.putExtra("club_type_json",
									jsonObject.toString());
							startActivity(intent);
						}
					}
				});

		my_clubs_create_type_public
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(MyCreateClubTypeActivity.this,
								MyCreateClubNameActivity.class);

						JSONObject jsonObject = null;

						for (int i = 0; i < clubTypeList.length(); i++) {

							jsonObject = clubTypeList.optJSONObject(i);

							if (jsonObject.optString("name").equals("公众俱乐部")) {
								break;
							}
						}

						if (jsonObject != null) {
							intent.putExtra("club_type_json",
									jsonObject.toString());
							startActivity(intent);
						}

					}
				});

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("选择俱乐部类型");

	}

	@Override
	public void onFinish(Pdtask t) {
		// TODO Auto-generated method stub

		boolean succ = false;
		if (t.getParam("method").equals("listClubType")) {

			JSONObject js = t.json;

			if (t.json != null && "ok".equals(t.json.optString("status"))) {

				if (t.json.optJSONArray("results") != null) {
					clubTypeList = t.json.optJSONArray("results");
					succ = true;
				}

			}

		}
		super.onFinish(t);
		if (succ) {
			setViewContent();
		}
		hideWait();

	}

	protected void setViewContent() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				JSONObject jsonObject;

				if (clubTypeList != null) {

					for (int i = 0; i < clubTypeList.length(); i++) {

						jsonObject = clubTypeList.optJSONObject(i);
						if (jsonObject.optString("name").equals("公众俱乐部")) {
							public_club_title.setText(jsonObject
									.optString("name"));
							public_club_brief.setText(jsonObject
									.optString("note"));
						} else if (jsonObject.optString("name").equals("私人俱乐部")) {
							private_club_title.setText(jsonObject
									.optString("name"));
							private_club_brief.setText(jsonObject
									.optString("note"));
						}
					}

				}
			}
		});

	}

	void requestData() {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "listClubType");// 页码
		// task.addParam("guid", UserMangerHelper.getDefaultUserGuid());// 页码
		// task.addParam("longsession",
		// UserMangerHelper.getDefaultUserLongsession());// 页码
		//
		SOApplication.getDownLoadManager().startTask(task);
		showWait();

	}

}
