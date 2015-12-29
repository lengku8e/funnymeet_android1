package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONException;
import org.json.JSONObject;

import mtcent.funnymeet.R;

public class MyClubDetailChangeClubNameActivity extends BaseActivity {

	EditText my_clubs_new_name;
	TextView my_clubs_new_name_left;
	TextView next_step;
	int leftwords = 10;
	JSONObject clubJson;
	Intent get_intent;
	String club_name, type_id, is_searchable, profile, logo_url;

	public static final int ID = MyClubDetailChangeClubNameActivity.class
			.hashCode();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_club_setting_change_name);
		init();
	}

	void commit(String name, String type_id, String is_searchable,
			String profile, String logo_url) {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "updateClubSetting");// 页码
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());
		task.addParam("club_guid", clubJson.optString("guid"));
		task.addParam("name", name);
		task.addParam("type_id", type_id);
		task.addParam("is_searchable", is_searchable);
		task.addParam("profile", profile);
		task.addParam("logo_url", logo_url);

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	@Override
	public void onFinish(Pdtask t) {
		// TODO Auto-generated method stub
		super.onFinish(t);

		if (t.getParam("method").equals("updateClubSetting")) {

			JSONObject result = t.json;
			if (result != null) {
				StrUtil.showMsg(MyClubDetailChangeClubNameActivity.this,
						t.json.optString("status"));
				hideWait();
				finish();
			}

		}
	}

	protected void init() {

		get_intent = this.getIntent();

		if (get_intent != null) {
			try {
				clubJson = new JSONObject(get_intent.getStringExtra("clubJson"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		club_name = clubJson.optString("name");
		type_id = clubJson.optString("typeId");
		is_searchable = clubJson.optString("isSearchable");
		profile = clubJson.optString("profile");
		logo_url = clubJson.optString("logoUrl");

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
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String name = my_clubs_new_name.getText().toString();
				if (name.length() != 0) {
					commit(name, type_id, is_searchable, profile, logo_url);
				}

			}
		});

		my_clubs_new_name = (EditText) findViewById(R.id.my_clubs_new_name);
		my_clubs_new_name_left = (TextView) findViewById(R.id.my_clubs_new_name_left);

		my_clubs_new_name.addTextChangedListener(new TextWatcher() {

			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				temp = s;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				int number = leftwords - s.length();
				my_clubs_new_name_left.setText("" + number);
				selectionStart = my_clubs_new_name.getSelectionStart();
				selectionEnd = my_clubs_new_name.getSelectionEnd();
				if (temp.length() > leftwords) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					my_clubs_new_name.setText(s);
					my_clubs_new_name.setSelection(tempSelection);// 设置光标在最后
				}
			}
		});
	}
}
