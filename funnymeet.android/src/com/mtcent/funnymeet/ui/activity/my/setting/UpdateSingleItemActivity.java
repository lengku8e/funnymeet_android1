package com.mtcent.funnymeet.ui.activity.my.setting;

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

public class UpdateSingleItemActivity extends BaseActivity {
	public static final String EXTRA_PARAM_SINGLEITEM = "EXTRA_PARAM_SINGLEITEM";
	
	public static final String EXTRA_PARAM_FIELDNAME = "EXTRA_PARAM_FIELDNAME";
	
	public static final String EXTRA_PARAM_TITLE = "EXTRA_PARAM_TITLE";

	private TextView titleTextView;
	private TextView finishButton;
	private TextView itemleftwords;
	private EditText item;
	private String currentItem;
	private String fieldName; 
	private String title;
	private int leftwords = 30;
	public static final int ID = UpdateSingleItemActivity.class.hashCode();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_myaccountsetting_modify_singleitem);
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

		item = (EditText) findViewById(R.id.setting_modify_item);
		itemleftwords = (TextView) findViewById(R.id.item_leftwords);
		Intent intent = this.getIntent();
		currentItem = intent.getStringExtra(EXTRA_PARAM_SINGLEITEM);
		fieldName = intent.getStringExtra(EXTRA_PARAM_FIELDNAME);
		title = intent.getStringExtra(EXTRA_PARAM_TITLE);
		finishButton = (TextView) findViewById(R.id.finishbutton);
		finishButton.setVisibility(View.VISIBLE);

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText(title);

		item.addTextChangedListener(new TextWatcher() {

			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				temp = s;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				int number = leftwords - s.length();
				itemleftwords.setText("" + number);
				selectionStart = item.getSelectionStart();
				selectionEnd = item.getSelectionEnd();
				if (temp.length() > leftwords) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					item.setText(s);
					item.setSelection(tempSelection);// 设置光标在最后
				}
			}
		});

		finishButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String newItem = item.getText().toString();
				if (newItem.equals(currentItem)) {
					finish();
				} else {
					commit(newItem);
				}
			}
		});
		item.setText(currentItem);
	}

	private void commit(String newItem) {
		JSONObject user = UserMangerHelper.getDefaultUser();
		try {
			user.put(fieldName, newItem);
			UserMangerHelper.requestUpDateInfo(user, this, this);
			showWait();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFinish(Pdtask t) {
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
	}

	CustomDialog waitDialog = null;

	public void showWait() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (waitDialog == null) {
					waitDialog = new CustomDialog(UpdateSingleItemActivity.this);
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
