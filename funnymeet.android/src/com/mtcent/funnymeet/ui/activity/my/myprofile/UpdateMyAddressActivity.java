package com.mtcent.funnymeet.ui.activity.my.myprofile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONException;
import org.json.JSONObject;

import mtcent.funnymeet.R;

public class UpdateMyAddressActivity extends Activity implements DownBack {

	public static final int ID = UpdateMyAddressActivity.class.hashCode();
	String currentAddress;
	EditText myinformation_myaddress;
	TextView titleTextView;
	TextView finishButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_myinformation_address);
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
		titleTextView.setText("我的地址");
		myinformation_myaddress = (EditText) findViewById(R.id.myinformation_address);

		Intent intent = this.getIntent();
		currentAddress = intent.getStringExtra("currentAddress");
		myinformation_myaddress.setText(currentAddress);

		finishButton = (TextView) findViewById(R.id.finishbutton);
		finishButton.setVisibility(View.VISIBLE);

		finishButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String newAddress = myinformation_myaddress.getText()
						.toString();
				if (newAddress.equals(currentAddress)) {
					finish();
				} else if (newAddress.isEmpty()) {
					finishButton.setVisibility(View.GONE);
				} else {

					commit(newAddress);
				}
			}
		});

	}

	private void commit(String newAddress) {
		JSONObject user = UserMangerHelper.getDefaultUser();
		try {

			user.put("address", newAddress);
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

	void showWait() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (waitDialog == null) {
					waitDialog = new CustomDialog(
							UpdateMyAddressActivity.this);
					waitDialog.setContentView(R.layout.dialog_wait);
				}
				waitDialog.show();
			}
		});
	}

	void hideWait() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				waitDialog.dismiss();
				finish();
			}
		});
	}
}
