package com.mtcent.funnymeet.ui.activity.my.setting;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.config.CurrentVersion;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog.OnConfirmListern;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.util.StrUtil;
import com.readystatesoftware.viewbadger.BadgeView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import mtcent.funnymeet.R;

public class AboutActivity extends BaseActivity {
	
	TextView titleTextView;
	ImageView copyrighticon;
	TextView version;
	ImageView setting_about_egg;
	ScrollView setting_about_scrollview;
	RelativeLayout setting_about_egg_frame;
	int startOrEnd = 1;
	int showlittleVersion = 0;
	BroadcastReceiver receiver;
	android.app.DownloadManager downloadManager;
	DownloadChangeObserver observer = null;
	long downloadId = -1;
	public static final Uri CONTENT_URI = Uri
			.parse("content://downloads/my_downloads");
	
	private ProgressDialog pBar;
	private Handler handler=new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_about);
		init();
		LinearLayout target = (LinearLayout)findViewById(R.id.checkUpdate);
		//View target = findViewById(R.id.target_view);
		BadgeView badge = new BadgeView(this, target);
		badge.setText("new");
		if (SOApplication.getHasUpdate()) {
		//	badge.show();
		} else {
		//	badge.hide();
		}
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
		titleTextView.setText("关于趣聚");
		setting_about_egg = (ImageView) findViewById(R.id.setting_about_egg);

		copyrighticon = (ImageView) findViewById(R.id.about_copyright_icon);

		version = (TextView) findViewById(R.id.version);
		version.setText("趣聚 " + CurrentVersion.getVerName(this));

		setting_about_scrollview = (ScrollView) findViewById(R.id.setting_about_scrollview);
		setting_about_egg_frame = (RelativeLayout) findViewById(R.id.setting_about_egg_frame);

		copyrighticon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (showlittleVersion == 0) {
					version.setText("趣聚 "  + CurrentVersion.getVerName(AboutActivity.this));

					showlittleVersion++;
				} else if (showlittleVersion == 1) {
					version.setText("趣聚 "  + CurrentVersion.getVerName(AboutActivity.this));
					showlittleVersion--;
				}
				steveJobsThx(1);
			}
		});

		setting_about_egg_frame.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				steveJobsThx(2);
			}
		});

		findViewById(R.id.checkUpdate).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						checkUpdate();
					}
				});
	}

	void checkUpdate() {
		// versionNum当前应用版本号
		// deviceNum系统类型
		// sysNum系统版本号
		String pkName = mActivity.getPackageName();
		int versionNum = 1;
		try {
			versionNum = mActivity.getPackageManager()
					.getPackageInfo(pkName, 0).versionCode;
		} catch (NameNotFoundException e) {
			//e.printStackTrace();
		}

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);

		task.addParam("method", "checkVersionOfAndroidApp");// 页码
		task.addParam("local_version_number", String.valueOf(versionNum));// 1.0

		SOApplication.getDownLoadManager().startTask(task);
		showWait("正在获取版本信息");
	}

	@Override
	public void onFinish(Pdtask t) {
		if (t.getParam("method").equals("checkVersionOfAndroidApp")) {
			boolean succ = false;
			String msg = "没有新版本";
			JSONObject version = null;
			int statue = 0;
			int versionNum = 0;
			String versionName = "";
//			showDownSelect(version, statue);
//			hideWait();
//		
//			return;
			if (t.json != null) {
				JSONObject results = t.json.optJSONObject("results");
				if (results != null) {
					version = results.optJSONObject("appVersion");
					if (version == null) {
						//最新版本
						msg = results.optString("message");
					} else {
						versionNum = version.optInt("versionNum");
						versionName = version.optString("versionName");
						msg = results.optString("message");
						int su = results.optInt("success");
						statue = results.optInt("statue", 0);
						if (su == 1) {
							succ = true;
						} else if (results.has("message")) {
							msg = results.optString("message");
						}
					}
				}
			}

			if (succ && version != null && statue == 1
					&& version.optString("fileUrl") != null) {
				int currentCode = 0;
				try {
					currentCode = CurrentVersion.getVerCode(AboutActivity.this);
				} catch (NameNotFoundException e) {
					//
				}
				if (versionNum > currentCode) {
					//showDownSelect(version, statue);
					showUpdateDialog(versionName);
				} else {
					//showWaitToMsg(msg);
					SOApplication.setHasUpdate(false);
					StrUtil.showMsg(this, msg);
				}
			} else {
				StrUtil.showMsg(this, msg);
				//showWaitToMsg(msg);
			}
		}
		hideWait();
	}

	private void showUpdateDialog(final String versionName) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append("发现新版本：" + versionName);
				sb.append("\n");
				sb.append("是否更新？");
				Dialog dialog = new AlertDialog.Builder(
						AboutActivity.this)
						.setTitle("软件更新")
						.setMessage(sb.toString())
						.setPositiveButton("更新",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										showProgressBar();// 更新当前版本
									}

								})
						.setNegativeButton("暂不更新",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).create();
				dialog.show();
			}
		});
	}
	
	private void showProgressBar() {
		pBar = new ProgressDialog(AboutActivity.this);
		pBar.setTitle("正在下载");
		pBar.setMessage("请稍后...");
		pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		downAppFile(Constants.APP_DOWNLOADPATH + Constants.APP_NAME);
	}

	private void downAppFile(final String url) {
		pBar.show();
		new Thread(){
			public void run(){
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					Log.isLoggable("DownTag", (int) length);
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if(is == null){
						throw new RuntimeException("isStream is null");
					}
					File file = new File(Environment.getExternalStorageDirectory(),Constants.APP_NAME);
					fileOutputStream = new FileOutputStream(file);
					byte[] buf = new byte[1024];
					int ch = -1;
					do{
						ch = is.read(buf);
						if(ch <= 0)break;
						fileOutputStream.write(buf, 0, ch);
					}while(true);
					is.close();
					fileOutputStream.close();
					haveDownLoad();
					}catch(ClientProtocolException e){
						e.printStackTrace();
						}catch(IOException e){
						e.printStackTrace();
						}
				}

			
		}.start();
	}
	
	private void haveDownLoad() {
		handler.post(new Runnable(){
			public void run(){
				pBar.cancel();
				//弹出警告框 提示是否安装新的版本
				Dialog installDialog = new AlertDialog.Builder(AboutActivity.this)
				.setTitle("下载完成")
				.setMessage("是否安装新的应用")
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						installNewApk();
						finish();
						}


					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							finish();
							}
						})
						.create();
				installDialog.show();
				}
			});
	}
	
	private void installNewApk() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(
				new File(Environment.getExternalStorageDirectory(),Constants.APP_NAME)),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	void showDownSelect(final JSONObject version, final int statue) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				String msg = "";
				//final String apkUrl = "http://www.dlyaoju.com/release/dlyaoju_android_build.apk";
				final String apkUrl = version.optString("fileUrl");
				final String title = version.optString("name")
						+ version.optString("versionName");
				msg = "发现新版本：" + title + "\n";

//				if (version.has("releaseDate")) {
//					msg += "更新时间：" + version.optString("releaseDate") + "\n";
//				}
//				if (version.has("fileSize")) {
//					msg += "文件大小："
//							+ StrUtil.byteToKMGB(version.optInt("fileSize", 0))
//							+ "\n";
//				}
//				if (version.has("versionDescription")) {
//					msg += "更新内容：" + "\n"
//							+ version.optString("versionDescription");
//				}

				CustomDialog.createConfirmDialog(mActivity, msg, false, "返回",
						"下载安装", new OnConfirmListern() {
							@Override
							public void onConfirm(String phone) {
								showProgressBar();
								Environment.getExternalStoragePublicDirectory(
										Environment.DIRECTORY_DOWNLOADS)
										.mkdirs();

								downloadManager = (android.app.DownloadManager) getSystemService(DOWNLOAD_SERVICE);
								android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(
										Uri.parse(apkUrl));
								request.setDestinationInExternalPublicDir(
										"sohuodong", Constants.APP_NAME);
								request.setTitle("趣聚");
								request.setDescription("趣聚");
								if (android.os.Build.VERSION.SDK_INT >= 11) {
									request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
								}

								// request.setAllowedNetworkTypes(RequestHelper.Request.NETWORK_WIFI);
								// request.setNotificationVisibility(RequestHelper.Request.VISIBILITY_HIDDEN);
								// request.setMimeType("application/com.trinea.download.file");
								request.setMimeType("application/vnd.android.package-archive");
								downloadId = downloadManager.enqueue(request);
								observer = new DownloadChangeObserver(
										new Handler());
								getContentResolver().registerContentObserver(
										CONTENT_URI, true, observer);
//								Intent intent = new Intent(Intent.ACTION_VIEW);
//								intent.setDataAndType(Uri.fromFile(
//										new File(Environment.getExternalStorageDirectory(),Constants.APP_NAME)),
//										"application/vnd.android.package-archive");
//								startActivity(intent);
//								finish();
							}


							@Override
							public void onCancle() {

							}
						}).show();
			}
		});

	}

	class DownloadChangeObserver extends ContentObserver {

		public DownloadChangeObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			queryDownloadStatus();
		}
	}
	

	private void queryDownloadStatus() {

		android.app.DownloadManager.Query query = new android.app.DownloadManager.Query();
		query.setFilterById(downloadId);
		Cursor c = downloadManager.query(query);
		if (c != null && c.moveToFirst()) {
			int status = c.getInt(c
					.getColumnIndex(android.app.DownloadManager.COLUMN_STATUS));
			// int reasonIdx = c.getColumnIndex(RequestHelper.COLUMN_REASON);
			// int titleIdx = c.getColumnIndex(RequestHelper.COLUMN_TITLE);
			int fileSizeIdx = c
					.getColumnIndex(android.app.DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
			int bytesDLIdx = c
					.getColumnIndex(android.app.DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
			// String title = c.getString(titleIdx);
			int fileSize = c.getInt(fileSizeIdx);
			int bytesDL = c.getInt(bytesDLIdx);
			// int reason = c.getInt(reasonIdx);

			switch (status) {
			case android.app.DownloadManager.STATUS_PAUSED:
			case android.app.DownloadManager.STATUS_PENDING:
				break;
			case android.app.DownloadManager.STATUS_RUNNING:
				// 正在下载，不做任何事情
				if (fileSize > 1) {
					showWait("已完成  " + (bytesDL * 100 / fileSize) + "%");
				}
				break;
			case android.app.DownloadManager.STATUS_SUCCESSFUL:
				// 完成
				// dowanloadmanager.remove(lastDownloadId);
				int fileNameIdx = c
						.getColumnIndex(android.app.DownloadManager.COLUMN_LOCAL_FILENAME);
				String fileName = c.getString(fileNameIdx);
				StrUtil.installApk(mActivity, fileName);
				downloadId = -1;
				hideWait();
				break;
			case android.app.DownloadManager.STATUS_FAILED:
				// 清除已下载的内容，重新下载
				showWaitToMsg("下载失败！");
				break;
			}
			c.close();
		}
	}

	@Override
	protected void onDestroy() {
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		if (observer != null) {
			getContentResolver().unregisterContentObserver(observer);
			observer = null;
		}
		super.onDestroy();
	}

	private void steveJobsThx(int status) {
		if (status == 1) {
			// Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
			// alphaAnimation.setDuration(2000);
			// alphaAnimation.setFillAfter(true);
			// setting_about_egg.startAnimation(alphaAnimation);
			setting_about_scrollview.setVisibility(View.GONE);
			setting_about_egg_frame.setVisibility(View.VISIBLE);
		} else if (status == 2) {
			// Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
			// alphaAnimation.setDuration(2000);
			// alphaAnimation.setFillAfter(true);
			// setting_about_egg.startAnimation(alphaAnimation);
			setting_about_egg_frame.setVisibility(View.GONE);
			setting_about_scrollview.setVisibility(View.VISIBLE);

		}
	}

}
