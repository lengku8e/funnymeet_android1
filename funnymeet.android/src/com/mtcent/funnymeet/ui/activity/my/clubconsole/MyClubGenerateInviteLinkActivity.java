package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.util.NetUtil;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import mtcent.funnymeet.R;

@SuppressLint("HandlerLeak")
public class MyClubGenerateInviteLinkActivity extends BaseActivity {

	public static final String EXTRA_PARAM_CLUBID = "EXTRA_PARAM_CLUBID";

	private TextView my_clubs_invitelink;
	private TextView copylink;
	private TextView close_generatelink;

	private String clubId;
	private Intent get_intent;

	private String dwz = null;

	public static final int ID = MyClubGenerateInviteLinkActivity.class
			.hashCode();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_club_generate_invitelink);
		init();
		new Thread(networkTask).start();
	}

	protected void init() {
		my_clubs_invitelink = (TextView) findViewById(R.id.my_clubs_invitelink);
		copylink = (TextView) findViewById(R.id.copylink);
		close_generatelink = (TextView) findViewById(R.id.close_generatelink);

		get_intent = this.getIntent();
		if (get_intent != null) {

			clubId = get_intent.getStringExtra(EXTRA_PARAM_CLUBID);
			my_clubs_invitelink
					.setText(Constants.SERVER_INVITE_LINK
							+ clubId);
		}

		TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("生成邀请链接");

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		copylink.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 得到剪贴板管理器
				ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("邀请链接",
						my_clubs_invitelink.getText());
				cmb.setPrimaryClip(clip);
				StrUtil.showMsg(MyClubGenerateInviteLinkActivity.this,
						"邀请链接已经复制。");
			}
		});
		close_generatelink.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
	}

	Runnable networkTask = new Runnable() {

		@Override
		public void run() {
			setDwz();
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("dwz", dwz);
			msg.setData(data);
			dwzHandler.sendMessage(msg);
		}
	};

	Handler dwzHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			dwz = data.getString("dwz");
			if (dwz != null && dwz.length() > 1) {
				my_clubs_invitelink.setText(dwz);
			}
		}
	};

	private void setDwz() {
		String lurl = Constants.SERVER_INVITE_LINK + clubId  + "&test=0";
		String encodedUrl = null;
		try {
			encodedUrl = URLEncoder.encode(lurl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			encodedUrl = lurl;
		}

		String uri = "http://dwz.cn/create.php";
		String jsonStr = NetUtil.sendPost(uri, "url=" + encodedUrl);
		JSONObject jsonDwz = null;
		try {
			jsonDwz = new JSONObject(jsonStr);
			dwz = jsonDwz.optString("tinyurl");
		} catch (JSONException e1) {
			dwz = lurl;
			e1.printStackTrace();
		}
	}

}
