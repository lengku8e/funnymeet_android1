package com.mtcent.funnymeet.ui.activity.club;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.model.ClubWithCreatorInfo;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.activity.discovery.search.SearchActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.ui.view.control.XVURLImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mtcent.funnymeet.R;

public class FindNewClub2Activity extends BaseActivity {

	private LinearLayout addressbook_findclub_to_search;
	private LinearLayout addressbook_newclub_add_phone_club;
	private LinearLayout addressbook_newclub_add_qq_club;
	private LinearLayout add_club;

	private TextView addClubs;
	private ListView new_club_list;
	private NewClubsAdapter adapter;
	private List<JSONObject> newClubsArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addressbook_newclub);
		// SearchUtilStack.searchUtilStack.add(this);
		init();
		requestNewClubs();
	}

	private void requestNewClubs() {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "listUserNewClubs");
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		// task.addParam("phones", "18641150136");
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	protected void init() {
		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

        add_club = (LinearLayout) findViewById(R.id.add_club);
        add_club.setVisibility(View.VISIBLE);/*设置控件可见*/

		addClubs = (TextView) findViewById(R.id.addClubs);
		addClubs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(FindNewClub2Activity.this, FindNewClubActivity.class);
				startActivity(intent);
			}
		});

		TextView tv = (TextView) findViewById(R.id.titleTextView);
		tv.setText("新的俱乐部");

		addressbook_findclub_to_search = (LinearLayout) findViewById(R.id.addressbook_findclub_to_search);
		addressbook_findclub_to_search
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(FindNewClub2Activity.this, SearchActivity.class);
						startActivity(intent);
					}
				});

		// 添加手机好友俱乐部
		addressbook_newclub_add_phone_club = (LinearLayout) findViewById(R.id.addressbook_newclub_add_phone_club);
		addressbook_newclub_add_phone_club
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(FindNewClub2Activity.this,
								ClubFromPhoneActivity.class);
						startActivity(intent);
					}
				});
		// 添加QQ好友俱乐部
		addressbook_newclub_add_qq_club = (LinearLayout) findViewById(R.id.addressbook_newclub_add_qq_club);
		addressbook_newclub_add_qq_club
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(FindNewClub2Activity.this,
								ClubFromPhoneActivity.class);
						startActivity(intent);
					}
				});

		new_club_list = (ListView) findViewById(R.id.new_club_list);
	}

	@Override
	public void onFinish(Pdtask t) {
		boolean succ = false;
		if (t.getParam("method").equals("listUserNewClubs")) {

			if (t.json != null) {

				if (t.json.optJSONArray("results") != null) {

					newClubsArray = toJSONObjectList(t.json
							.optJSONArray("results"));
					adapter = new NewClubsAdapter(this,
							R.layout.club_from_phone_list_item, newClubsArray);

					succ = true;
				}
			}
		} else if ("signPublicClub".equals(t.getParam("method"))) {
			requestNewClubs();
			succ = true;
		}
		if (succ) {
			setViewContent();
		}
		hideWait();
		super.onFinish(t);
	}

	private List<JSONObject> toJSONObjectList(JSONArray optJSONArray) {
		List<JSONObject> result = new ArrayList<JSONObject>();
		for (int i = 0; i < optJSONArray.length(); i++) {
			try {
				result.add(optJSONArray.getJSONObject(i));
			} catch (JSONException e) {
			}
		}
		return result;
	}

//	private List<ClubWithCreatorInfo> jsonArray2PhoneClubInfoList(
//			JSONArray myClubListSource) {
//		List<ClubWithCreatorInfo> result = new ArrayList<ClubWithCreatorInfo>();
//		for (int i = 0; i < myClubListSource.length(); i++) {
//			JSONObject o = null;
//			try {
//				o = (JSONObject) myClubListSource.get(i);
//			} catch (JSONException e) {
//
//			}
//			result.add(json2ClubWithCreatorInfo(o));
//		}
//		return result;
//	}

	private ClubWithCreatorInfo json2ClubWithCreatorInfo(JSONObject o) {
		String phoneUserName = o.optString("createUserName");
		String clubname = o.optString("clubName");
		String logourl = o.optString("logoUrl");
		String status = o.optString("userStateId");
		String clubGuid = o.optString("guid");
		ClubWithCreatorInfo cpi = new ClubWithCreatorInfo(phoneUserName,
				clubGuid, clubname, logourl, status);
		return cpi;
	}

	protected void onResume() {
		super.onResume();
		requestNewClubs();
	}
	protected void setViewContent() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				new_club_list.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		});

	}

	protected void joinClub(ClubWithCreatorInfo ci) {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "signPublicClub");
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());
		task.addParam("club_guid", ci.getClubGuid());

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	@SuppressLint("InflateParams")
	class NewClubsAdapter extends ArrayAdapter<JSONObject> {
		public NewClubsAdapter(Context context, int resource,
				List<JSONObject> list) {
			super(context, resource, list);
		}

		class Tag {
			TextView firstLetter;
			XVURLImageView clubicon;
			TextView clubname;
			TextView phoneUserName;
			TextView addButton;
			TextView addedView;
			TextView divider;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Tag tag = null;
			if (convertView == null) {
				tag = new Tag();
				LayoutInflater inflater = ((Activity) this.getContext())
						.getLayoutInflater();
				convertView = inflater.inflate(
						R.layout.club_from_phone_list_item, null);
				tag.firstLetter = (TextView) convertView
						.findViewById(R.id.firstLetter);
				tag.clubicon = (XVURLImageView) convertView
						.findViewById(R.id.clubImage);
				tag.clubname = (TextView) convertView
						.findViewById(R.id.clubName);
				tag.phoneUserName = (TextView) convertView
						.findViewById(R.id.phone_user_name);
				tag.addButton = (TextView) convertView
						.findViewById(R.id.addbutton);
				tag.addedView = (TextView) convertView.findViewById(R.id.added);
				tag.divider = (TextView) convertView
						.findViewById(R.id.addressBookItemDivider);
				convertView.setTag(tag);
			} else {
				tag = (Tag) convertView.getTag();
			}
			final JSONObject json = getItem(position);

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("jsonobjc", json.toString());
					intent.putExtra("from", "search_result_list");
					intent.putExtra(ClubInfoActivity.EXTRA_KEY_CLUB_USER_INFO_CLUB_JSON_STRING, json.toString());
					intent.putExtra(ClubInfoActivity.EXTRA_KEY_CLUB_GUID, json.optString("guid"));
					intent.setClass(mActivity, ClubInfoActivity.class);

//					intent.putExtra(MyClubManagementActivity.EXTRA_PARAM_JSONOBJECT, json.toString());
//					//
//					intent.putExtra(MyClubManagementActivity.EXTRA_PARAM_CLUB_TYPE, MyClubManagementActivity.CLUB_MANAGER_TYPE_USER);
//					intent.setClass(mActivity, MyClubManagementActivity.class);

					startActivity(intent);

				}

			});

			tag.divider.setVisibility(View.VISIBLE);
			tag.firstLetter.setVisibility(View.GONE);

			final ClubWithCreatorInfo ci = json2ClubWithCreatorInfo(json);
			// tag.my_clubs_clubicon.setImageUrl(fi.optString());
			tag.clubname.setText(ci.getClubname());
			String logoUrl = ci.getLogourl();
			if (logoUrl != null & logoUrl.length() > 10) {
				tag.clubicon.setImageUrl(logoUrl);
			} else {
				//
			}
			String createUserName = ci.getCreateUserName();
			tag.phoneUserName.setText("创始人:" + createUserName);

			if (ClubFromPhoneActivity.PHONE_CLUB_ADDED.equals(ci.getStatus())) {
				tag.addButton.setVisibility(View.GONE);
				tag.addedView.setVisibility(View.VISIBLE);
				tag.addedView.setText("已加入");
			} else if (ClubFromPhoneActivity.PHONE_CLUB_INREVIEW.equals(ci
					.getStatus())) {
				tag.addButton.setVisibility(View.GONE);
				tag.addedView.setVisibility(View.VISIBLE);
				tag.addedView.setText("审核中");
			} else {
				tag.addButton.setVisibility(View.VISIBLE);
				tag.addButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						joinClub(ci);

					}
				});
				tag.addedView.setVisibility(View.GONE);
			}

			return convertView;
		}

	}
}
