package com.mtcent.funnymeet.ui.activity.my.setting;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.ui.view.control.XVURLImageView;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONObject;

import mtcent.funnymeet.R;

public class UpdateFunnymeetIdActivity extends BaseActivity {

	TextView titleTextView;
	TextView finishbutton;
	XVURLImageView face;
	TextView nickname;
	TextView name;
	TextView nameEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_myaccountsetting_modifysohuodongid);
		init();
		resetView();
	}

	void resetView() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String faceUrl = UserMangerHelper.getDefaultUserFaceUrl();
				if (faceUrl == null || faceUrl.length() == 0) {
					faceUrl = "local:defaultface.png";
				}
				face.setImageUrl(faceUrl);
				nickname.setText(UserMangerHelper.getDefaultUserNickName());
				name.setText(UserMangerHelper.getDefaultUserAccountName());
			}
		});
	}

	protected void init() {
		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		face = (XVURLImageView) findViewById(R.id.face);
		nickname = (TextView) findViewById(R.id.nickname);
		name = (TextView) findViewById(R.id.nameTextview);
		nameEditText = (TextView) findViewById(R.id.nameEditText);

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("修改用户号码");

		finishbutton = (TextView) findViewById(R.id.finishbutton);
		finishbutton.setVisibility(View.VISIBLE);
		finishbutton.setText("保存");
		finishbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String accountName = nameEditText.getText().toString();
				if (accountName.length() > 0) {
					Pdtask task = new Pdtask(UpdateFunnymeetIdActivity.this,
							UpdateFunnymeetIdActivity.this,
							Constants.SERVICE_HOST, null,
							RequestHelper.Type_PostParam, null, 0, true);
					task.addParam("method", "setUserAccountName");// 页码
					task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());// 页码
					task.addParam("longsession",
							UserMangerHelper.getDefaultUserLongsession());// 页码

					task.addParam("accountName", accountName);// 页码

					SOApplication.getDownLoadManager().startTask(task);
					showWait();
				}
			}
		});
	}

	@Override
	public void onFinish(Pdtask t) {
		boolean succ = false;
		String msg = "修改失败";
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
		if (t.getParam("method").equals("setUserAccountName")) {
			if (succ && user != null && user.has("mobilePhone")) {
				UserMangerHelper.saveDefaultUser(user);
				StrUtil.showMsg(this, "修改成功");
				resetView();
				finish();
			} else {
				StrUtil.showMsg(this, msg);
			}
		}
		hideWait();
	}
}
