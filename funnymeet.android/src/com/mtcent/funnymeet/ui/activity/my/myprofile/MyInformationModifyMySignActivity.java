package com.mtcent.funnymeet.ui.activity.my.myprofile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONException;
import org.json.JSONObject;

import mtcent.funnymeet.R;

public class MyInformationModifyMySignActivity extends BaseActivity {

	TextView titleTextView;
	TextView finishButton;
	TextView myinformation_signleftwords;
	EditText myinformation_sign;
	String currentSign;
	int leftwords = 30;
	public static final int ID = MyInformationModifyMySignActivity.class.hashCode();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_myinformation_sign);
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
		titleTextView.setText("个性签名");

		myinformation_sign = (EditText) findViewById(R.id.myinformation_sign);
		myinformation_signleftwords = (TextView) findViewById(R.id.myinformation_signleftwords);
		Intent intent = this.getIntent();
		currentSign = intent.getStringExtra("currentSign");

		// myinformation_signleftwords.setText(30 );
		finishButton = (TextView) findViewById(R.id.finishbutton);
		finishButton.setVisibility(View.VISIBLE);

		myinformation_sign.addTextChangedListener(new TextWatcher() {

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
				myinformation_signleftwords.setText("" + number);
				selectionStart = myinformation_sign.getSelectionStart();
				selectionEnd = myinformation_sign.getSelectionEnd();
				if (temp.length() > leftwords) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					myinformation_sign.setText(s);
					myinformation_sign.setSelection(tempSelection);// 设置光标在最后
				}
			}
		});

		finishButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String newSign = myinformation_sign.getText().toString();
				if (newSign.equals(currentSign)) {
					finish();
				} else {
					commit(newSign);
				}

			}
		});

		myinformation_sign.setText(currentSign);

	}

	private void commit(String newSign) {
		JSONObject user = UserMangerHelper.getDefaultUser();
		try {

			user.put("privateSolgan", newSign);
			UserMangerHelper.requestUpDateInfo(user, this, this);
			showWait();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onFinish(Pdtask t) {
		// TODO Auto-generated method stub
		if (t.getParam("method").equals("setUserInfoByGuid")) {
			boolean succ = false;
			String msg = "设置个人信息失败";
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

			if (succ && user != null && user.has("mobilePhone")) {
				UserMangerHelper.setDefaultUserChange(user);

			} else {
				StrUtil.showMsg(this, msg);
			}

		}
		hideWait();
	}

	@Override
	public void onUpdate(Pdtask t) {
		// TODO Auto-generated method stub

	}

	CustomDialog waitDialog = null;

	public void showWait() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (waitDialog == null) {
					waitDialog = new CustomDialog(
							MyInformationModifyMySignActivity.this);
					waitDialog.setContentView(R.layout.dialog_wait);
				}
				waitDialog.show();
			}
		});
	}

	public void hideWait() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				waitDialog.dismiss();
				finish();
			}
		});
	}

}
