package com.mtcent.funnymeet.ui.activity.my.myinterest;

import android.content.Intent;
import android.os.Bundle;
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

public class MyInterestModifyActivity extends BaseActivity {

	TextView titleTextView;
	TextView finishbutton;
	EditText normal_input;
	public static final int ID = MyInterestModifyActivity.class.hashCode();
	JSONObject question;
	JSONObject answer;
	String gpAndcp;
	String interest_answer;
	Intent intent = getIntent();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myinterest_edit);
		try {
			Bundle extras = getIntent().getExtras();
			question = new JSONObject(extras.getString("questions"));
			if (extras.getString("answers").equals("null")) {
				answer = null;
			} else {
				answer = new JSONObject(extras.getString("answers"));
			}

			gpAndcp = extras.getString("gpAndcp");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		titleTextView.setText(question.optString("question"));

		finishbutton = (TextView) findViewById(R.id.finishbutton);
		finishbutton.setVisibility(View.VISIBLE);

		normal_input = (EditText) findViewById(R.id.normal_input);

		if (answer != null) {
			try {
				normal_input.setText(answer.getString("answer"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		finishbutton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String interest_category_guid = "";
				String interest_question_guid = "";
				interest_category_guid = question.optString("categoryGuid");
				interest_question_guid = question.optString("guid");
				interest_answer = normal_input.getEditableText().toString();
				commit(interest_category_guid, interest_question_guid,
						interest_answer);
			}
		});
	}

	private void commit(String interest_category_guid,
			String interest_question_guid, String interest_answer) {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "setInterest");
		task.addParam("interest_category_guid", interest_category_guid);
		task.addParam("interest_question_guid", interest_question_guid);
		task.addParam("interest_answer", interest_answer);
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("long_session",
				UserMangerHelper.getDefaultUserLongsession());
		SOApplication.getDownLoadManager().startTask(task);
		showWait();

	}

	@Override
	public void onFinish(Pdtask t) {
		if (t.getParam("method").equals("setInterest")) {
			boolean succ = false;
			String msg = "设置失败";
			JSONObject user = null;
			if (t.json != null) {
				JSONObject results = t.json.optJSONObject("results");
				if (results != null) {
					user = results.optJSONObject("user");
					int su = results.optInt("success");
					if (su == 1) {
						succ = true;
					} else if (results.has("msg")) {
						msg = results.optString("msg");
					}
				}
			}

			if (succ == false) {
				StrUtil.showMsg(this, msg);
			}else{
				hideWait();
				Intent intent = new Intent();
				intent.putExtra("gpAndcp", gpAndcp);
				intent.putExtra("newAnswer", interest_answer);
				setResult(RESULT_OK, intent);
				finish();
			}

		}
		hideWait();

	}
}
