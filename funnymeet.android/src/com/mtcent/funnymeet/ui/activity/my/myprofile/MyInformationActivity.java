package com.mtcent.funnymeet.ui.activity.my.myprofile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.my.setting.UpdateSingleItemActivity;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.activity.my.clubconsole.SelectImageActivity;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.ui.view.control.XVURLImageView;
import com.mtcent.funnymeet.util.BitmapUtil;
import com.mtcent.funnymeet.util.StrUtil;
import com.qiniu.auth.JSONObjectRet;
import com.qiniu.io.IO;
import com.qiniu.io.PutExtra;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import mtcent.funnymeet.R;

public class MyInformationActivity extends Activity implements DownBack {
	public static final int ID = MyInformationActivity.class.hashCode();
	TextView titleTextView;
	Activity mActivity = this;
	JSONObject user = new JSONObject();
	XVURLImageView faceImageView;
	TextView nickName;
	TextView account_name;
	TextView myaddr;
	TextView sex;
	TextView area;
	TextView sign;
	TextView thirdaccounts;
	String imageFilePath = "";
	String imageFileHash = null;
	LinearLayout nickModify;
	LinearLayout myaddrModify;
	LinearLayout signModify;
	LinearLayout sexModify;
	LinearLayout areaModify;
	private LinearLayout setting_modify_realname;
	private LinearLayout setting_modify_company;

	CustomDialog dialog;
	ImageView femaleSelected;
	ImageView maleSelected;
	boolean faceHasChange = false;
	int gender = 0;
	private TextView realname;
	private TextView company;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_myinforamtion);// 默认登录界面

		init();
		requestData();
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
		titleTextView.setText("个人信息");

		user = UserMangerHelper.getDefaultUser();
		imageFilePath = BitmapUtil.getSaveBitmapFile(
				user.optString("faceUrl", "")).getAbsolutePath();
		faceImageView = (XVURLImageView) findViewById(R.id.faceImageView);
		nickName = (TextView) findViewById(R.id.nickName);

		account_name = (TextView) findViewById(R.id.account_name);
		myaddr = (TextView) findViewById(R.id.myaddr);
		sex = (TextView) findViewById(R.id.sex);
		area = (TextView) findViewById(R.id.area);
		sign = (TextView) findViewById(R.id.sign);
		thirdaccounts = (TextView) findViewById(R.id.thirdaccounts);
		realname = (TextView) findViewById(R.id.realname);
		company = (TextView) findViewById(R.id.company);

		areaModify = (LinearLayout) findViewById(R.id.areaModify);
		areaModify.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MyInformationActivity.this,
						MyInformationProvinceAndRegionActivity.class);

				startActivityForResult(intent,
						MyInformationProvinceAndRegionActivity.ID);
			}
		});

		findViewById(R.id.faceModify).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(MyInformationActivity.this,
						SelectImageActivity.class);
				intent.putExtra("path", imageFilePath);
				intent.putExtra("w", 400);
				intent.putExtra("h", 400);
				startActivityForResult(intent, 123);
			}
		});
		nickModify = (LinearLayout) findViewById(R.id.nickModify);
		nickModify.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String currentNickname = user.optString("nickname", null);
				Intent intent = new Intent();
				intent.setClass(MyInformationActivity.this,
						MyInformationModifyNicknameActivity.class);

				intent.putExtra("currentNickname", currentNickname);
				startActivityForResult(intent,
						MyInformationModifyNicknameActivity.ID);
				// nickName
			}
		});
		// 设置真实姓名
		setting_modify_realname = (LinearLayout) findViewById(R.id.setting_modify_realname);
		setting_modify_realname.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MyInformationActivity.this,
						UpdateSingleItemActivity.class);
				intent.putExtra(UpdateSingleItemActivity.EXTRA_PARAM_SINGLEITEM,
						UserMangerHelper.getDefaultUser().optString("realname", ""));
				intent.putExtra(UpdateSingleItemActivity.EXTRA_PARAM_FIELDNAME,
						"realname");
				intent.putExtra(UpdateSingleItemActivity.EXTRA_PARAM_TITLE,
						"真实姓名");
				startActivity(intent);
			}
		});

		// 设置公司
		setting_modify_company = (LinearLayout) findViewById(R.id.setting_modify_company);
		setting_modify_company.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MyInformationActivity.this,
						UpdateSingleItemActivity.class);
				intent.putExtra(UpdateSingleItemActivity.EXTRA_PARAM_SINGLEITEM,
						UserMangerHelper.getDefaultUser().optString("company", ""));
				intent.putExtra(UpdateSingleItemActivity.EXTRA_PARAM_FIELDNAME,
						"company");
				intent.putExtra(UpdateSingleItemActivity.EXTRA_PARAM_TITLE,
						"公司");
				startActivity(intent);
			}
		});


		myaddrModify = (LinearLayout) findViewById(R.id.myaddrModify);
		myaddrModify.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String currentAddress = user.optString("address", null);
				Intent intent = new Intent();
				intent.setClass(MyInformationActivity.this,
						UpdateMyAddressActivity.class);

				intent.putExtra("currentAddress", currentAddress);
				startActivityForResult(intent, UpdateMyAddressActivity.ID);

			}
		});

		signModify = (LinearLayout) findViewById(R.id.signModify);
		signModify.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String currentSign = user.optString("privateSolgan", null);
				Intent intent = new Intent();
				intent.setClass(MyInformationActivity.this,
						MyInformationModifyMySignActivity.class);

				intent.putExtra("currentSign", currentSign);
				startActivityForResult(intent, MyInformationModifyMySignActivity.ID);
			}
		});

		sexModify = (LinearLayout) findViewById(R.id.sexModify);
		sexModify.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog == null) {
					dialog = new CustomDialog(MyInformationActivity.this);
					dialog.setContentView(R.layout.my_myinformation_genderdialog);
					dialog.setCancelable(true);
					dialogfunction();
				}
				dialog.show();

			}
		});
	}

	void resetView() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!faceHasChange) {
					String faceUrl = UserMangerHelper.getDefaultUserFaceUrl();
					if (faceUrl == null || faceUrl.length() == 0) {
						faceUrl = "local:defaultface.png";
					}
					faceImageView.setImageUrl(faceUrl);
				}

				nickName.setText(user.optString("nickname", null));
				String name = user.optString("accountName", "");
				if(name.isEmpty()){
					name="未设置";
				}
				account_name.setText(name);
				
				String txt = user.optString("realname", "");
				if (txt.isEmpty()) {
					txt = "未设置";
				}
				realname.setText(txt);

				txt = user.optString("company", "");
				if (txt.isEmpty()) {
					txt = "未设置";
				}
				company.setText(txt);

				myaddr.setText(user.optString("address", null));

				sex.setText(user.optString("genderName", null));
				if (user.optString("province", "").equals(
						user.optString("city", null))) {
					area.setText(user.optString("city", null));
				} else {
					area.setText(user.optString("province", "") + " "
							+ user.optString("city", ""));
				}

				sign.setText(user.optString("privateSolgan", null));
				thirdaccounts.setText(user.optString("qq", null));
			}
		});

		String genderTmp = user.optString("genderName", "");
		if (genderTmp.equals("男")) {
			gender = 1;
		} else if (genderTmp.equals("女")) {
			gender = 2;
		} else {
			gender = 0;
		}
	}

	@Override
	protected void onResume() {
		user = UserMangerHelper.getDefaultUser();
		resetView();
		super.onResume();
	}

	void requestData() {

		if (!UserMangerHelper.isDefaultUserChange()) {
			Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST,
					null, RequestHelper.Type_PostParam, null, 0, true);
			task.addParam("method", "getUserInfoByGuid");// 页码
			task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());// 页码
			task.addParam("user_session_guid",
					UserMangerHelper.getDefaultUserLongsession());// 页码

			SOApplication.getDownLoadManager().startTask(task);
			showWait();
		}
	}

	CustomDialog waitDialog = null;

	void showWait() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (waitDialog == null) {
					waitDialog = new CustomDialog(mActivity);
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
			}
		});
	}

	@Override
	public void onFinish(Pdtask t) {
		if (t.getParam("method").equals("getUserInfoByGuid")) {
			boolean succ = false;
			String msg = "获取个人信息失败";
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
				this.user = user;
				UserMangerHelper.saveDefaultUser(user);
				resetView();
			} else {
				StrUtil.showMsg(this, msg);
			}
			hideWait();
		} else if (t.getParam("method").equals("setUserInfoByGuid")) {
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
				UserMangerHelper.setDefaultUserFaceUrl(user
						.optString("faceUrl"));
				UserMangerHelper.setDefaultUserChange(user);
				resetView();
			} else {
				StrUtil.showMsg(this, msg);
			}
			hideWait();
		} else if (t.getParam("method").equals("getFileCloudToken")) {
			String token = null;
			if (t.json != null) {
				JSONObject results = t.json.optJSONObject("results");
				if (results != null && results.optInt("success", 0) == 1) {
					token = results.optString("token", null);
				}
			}
			uploadFace(token);
		}

	}

	@Override
	public void onUpdate(Pdtask t) {

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
				faceHasChange = true;
				faceImageView.setImageUrl(null);
				faceImageView.setImageBitmap(bitmap);
				requestToken();
			}
		}

	}

	void requestUpdateFace() {
		if (imageFileHash != null) {
			try {
				user.put("faceHash", imageFileHash);
				// user.putOpt("faceHash", imageFileHash);
				UserMangerHelper.requestUpDateInfo(user, this, this);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	void requestToken() {
		if (imageFilePath != null && new File(imageFilePath).isFile()
				&& imageFileHash == null) {

			Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST,
					null, RequestHelper.Type_PostParam, null, 0, true);
			task.addParam("method", "getFileCloudToken");
			SOApplication.getDownLoadManager().startTask(task);
		} else {
			requestUpdateFace();
		}
		showWait();
	}

	void uploadFace(final String token) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (token == null) {
					requestUpdateFace();
				} else {
					String uptoken = token;
					String key = IO.UNDEFINED_KEY;
					PutExtra extra = new PutExtra();
					extra.params = new HashMap<String, String>();
					extra.params.put("x:a", "");
					Uri uri = Uri.fromFile(new File(imageFilePath));
					IO.putFile(MyInformationActivity.this, uptoken, key, uri,
							extra, new JSONObjectRet() {
								@Override
								public void onProcess(long current, long total) {

								}

								@Override
								public void onSuccess(JSONObject resp) {
									String hash = resp.optString("hash", "");
									imageFileHash = hash;
									requestUpdateFace();
								}

								@Override
								public void onFailure(Exception ex) {
									imageFileHash = null;
									requestUpdateFace();
								}
							});
				}

			}
		});

	}

	private void dialogfunction() {

		femaleSelected = (ImageView) dialog.findViewById(R.id.femalebeselected);
		maleSelected = (ImageView) dialog.findViewById(R.id.malebeselected);

		if (gender == 1) {
			maleSelected.setVisibility(View.VISIBLE);
		} else if (gender == 2) {
			femaleSelected.setVisibility(View.VISIBLE);
		}

		LinearLayout gender_selected_male = (LinearLayout) dialog
				.findViewById(R.id.gender_selected_male);
		LinearLayout gender_selected_female = (LinearLayout) dialog
				.findViewById(R.id.gender_selected_female);
		gender_selected_male.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				maleSelected.setVisibility(View.VISIBLE);
				femaleSelected.setVisibility(View.GONE);
				commit("男");
			}
		});

		gender_selected_female.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				femaleSelected.setVisibility(View.VISIBLE);
				maleSelected.setVisibility(View.GONE);
				commit("女");
			}
		});

	}

	private void commit(String gender) {
		user = UserMangerHelper.getDefaultUser();
		try {
			user.put("genderName", gender);
			UserMangerHelper.requestUpDateInfo(user, this, this);
			showWait();
			dialog.dismiss();
			resetView();

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
