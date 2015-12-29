package com.mtcent.funnymeet.ui.activity.project;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;

import org.json.JSONObject;

import mtcent.funnymeet.R;

public class SuggestActivity extends Activity implements DownBack {
	public static final int ID = SuggestActivity.class.hashCode();
	private LayoutInflater inflater = null;
	ImageView titleBack;
	CustomDialog dialog = null;
	CustomDialog successConfirmDialog = null;
	TextView titleTextView;
	TextView senterButton;
	EditText normal_input;
	String submitUrl = Constants.SERVICE_HOST
			+ "?method=suggestion&user_id=0&qq=qq&weixin=weixin&email=email&content=我要提建议";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_suggestion);
		setContentView(R.layout.activity_suggestion_special);
		init();
		requestData();
		resetView();
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	void resetView() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

			}
		});
	}

	void requestData() {

	}

	void init() {
		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		normal_input = (EditText) findViewById(R.id.normal_input);
		senterButton = (TextView) findViewById(R.id.senter);
		senterButton.setVisibility(View.VISIBLE);
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("意见反馈");

		senterButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				submit();
			}
		});
		dialog = new CustomDialog(this);
		dialog.setContentView(R.layout.dialog_wait);
		dialog.setCancelable(false);

		successConfirmDialog = new CustomDialog(this);
		successConfirmDialog.setContentView(R.layout.suggestion_finish_dialog);
		successConfirmDialog.setCancelable(false);

	}

	void submit() {
		// String submitUrl = SOApplication.SERVICE_HOST +
		// "api/api.htm?method=suggestion&user_id=0&qq=qq&weixin=weixin&email=email&content=我要提建议";
		String submitUrl = Constants.SERVICE_HOST;

		Pdtask task = new Pdtask(this, this, submitUrl, null,
				RequestHelper.Type_PostParam, "", 0, true);

		JSONObject user = UserMangerHelper.getDefaultUser();
		String userid = user.optString("id");
		String qq = user.optString("qq");
		String weixin = user.optString("weixin");
		String email = user.optString("email");
		String content = normal_input.getText().toString();

		task.addParam("method", "suggestion");
		task.addParam("user_id",
				(userid != null && userid.length() > 0) ? userid : "0");
		task.addParam("qq", (qq != null && qq.length() > 0) ? qq : "0");
		task.addParam("weixin",
				(weixin != null && weixin.length() > 0) ? weixin : "0");
		task.addParam("email", (email != null && email.length() > 0) ? email
				: "0");
		task.addParam("content",
				(content != null && content.length() > 0) ? content : "content");

		if (content == null || content.isEmpty()) {

			showMsg("请填写内容");
		} else {
			showWaitDialog();
			SOApplication.getDownLoadManager().startTask(task);
		}

	}

	void showWaitDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				dialog.show();
			}
		});
	}

	void hideWaitDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				dialog.hide();
			}
		});
	}

	@Override
	public void onFinish(Pdtask t) {
		boolean ret = false;
		try {
			ret = t.json.optString("status").equalsIgnoreCase("ok");
		} catch (Exception e) {
		}
		if (ret) {

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					successConfirmDialog.show();
				}
			});

			TextView confirm = (TextView) successConfirmDialog
					.findViewById(R.id.suggest_confirm);
			confirm.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					successConfirmDialog.dismiss();
					finish();
				}
			});

		} else {
			showMsg("失败");
		}
		resetView();
		hideWaitDialog();

	}

	@Override
	public void onUpdate(Pdtask t) {
		// TODO Auto-generated method stub

	}

	// 弹出消息
	Toast toast = null;

	public void showMsg(final String str) {
		if (!isFinishing()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (toast == null) {
						toast = Toast.makeText(SuggestActivity.this, "",
								Toast.LENGTH_SHORT);
					}
					toast.setText(str);
					toast.show();
				}
			});
		}

	}
}
