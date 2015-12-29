package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.activity.base.BaseActivity;

import org.json.JSONObject;

import mtcent.funnymeet.R;

public class InviteMemberSelectTypeActivity extends BaseActivity {

	public static final int ID = InviteMemberSelectTypeActivity.class
			.hashCode();

	public static final String EXTRA_PARAM_CLUBGUID = "EXTRA_PARAM_CLUBGUID";

	public static final String EXTRA_PARAM_CLUBID = "EXTRA_PARAM_CLUBID";
	
	public static final String EXTRA_PARAM_CLUBNAME = "EXTRA_PARAM_CLUBNAME";

	TextView titleTextView;
	ExpandableListView listview;
	JSONObject[] categoriesForList_temp;
	JSONObject[][] listItemForList_temp;
	JSONObject[] theUsersAnswer_temp;

	private LinearLayout my_club_invite_member_sharelink;
	private LinearLayout my_club_invite_member_sendsms;

	JSONObject user = new JSONObject();
	private String mClubGuid = null;
	private String mClubId = null;
	private String mClubName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_invite_club_member_select_type);
		// requestData();
		init();
	}

	protected void init() {
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("选择邀请方式");

		// get intentdata
		Intent in = this.getIntent();
		this.mClubGuid = in.getStringExtra(EXTRA_PARAM_CLUBGUID);
		this.mClubId = in.getStringExtra(EXTRA_PARAM_CLUBID);
		this.mClubName = in.getStringExtra(EXTRA_PARAM_CLUBNAME);

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		// 生成会员邀请链接
		my_club_invite_member_sharelink = (LinearLayout)findViewById(R.id.my_club_invite_member_sharelink); 
		my_club_invite_member_sharelink
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.putExtra(
								MyClubGenerateInviteLinkActivity.EXTRA_PARAM_CLUBID,
								mClubId);
						intent.setClass(InviteMemberSelectTypeActivity.this,
								MyClubGenerateInviteLinkActivity.class);
						startActivity(intent);
					}

				});
		
		//发送短信
		my_club_invite_member_sendsms  = (LinearLayout)findViewById(R.id.my_club_invite_member_sendsms); 
		my_club_invite_member_sendsms.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("iconUrl", "");
				intent.putExtra(EXTRA_PARAM_CLUBNAME, mClubName);
				//String clubGuid = "";

				intent.putExtra(InviteMemberSelectTypeActivity.EXTRA_PARAM_CLUBGUID, mClubGuid);
				intent.putExtra(InviteMemberSelectTypeActivity.EXTRA_PARAM_CLUBID, mClubId);
				intent.setClass(InviteMemberSelectTypeActivity.this,
						MyClubsInviteMemberActivity.class);

				startActivity(intent);
			}
		});
	}

}
