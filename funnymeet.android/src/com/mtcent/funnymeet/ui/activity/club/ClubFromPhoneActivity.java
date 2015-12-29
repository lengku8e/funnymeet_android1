package com.mtcent.funnymeet.ui.activity.club;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.model.ClubWithCreatorInfo;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.ui.view.control.XVURLImageView;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mtcent.funnymeet.R;

@SuppressLint("InflateParams")
public class ClubFromPhoneActivity extends BaseActivity {

	public static final String PHONE_CLUB_ADDED = "1";
	public static final String PHONE_CLUB_INREVIEW = "0";
	public static final String PHONE_CLUB_NOTADDED = "-1";

	// private LinearLayout addressbook_findclub_to_search;
	// private LinearLayout add_club;
	//
	// private TextView addClubs;
	// private JSONArray myClubListSource;

	private PhoneClubAdapter adapter;
	// private LayoutInflater inflater;

	private ListView realListView;
	// private List<ClubWithCreatorInfo> phoneClubList;
	private List<JSONObject> newClubsArray;

	// private ArrayList<PhoneClubInfo> contactNameList = new
	// ArrayList<PhoneClubInfo>();
	private ArrayList<String> phoneNumberList = new ArrayList<String>();
	private StringBuffer mSBPhoneList = new StringBuffer();
	private Map<String, String> phoneMap = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.club_from_phone_list);
		// SearchUtilStack.searchUtilStack.add(this);
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

		TextView tv = (TextView) findViewById(R.id.titleTextView);
		tv.setText("查看手机好友俱乐部");
		realListView = (ListView) findViewById(R.id.club_from_phone_list);
		readPhoneList();

	}

	private void requestData() {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "listUserPhoneClub");
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("phones", this.mSBPhoneList.toString());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	private void readPhoneList() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ContentResolver resolver = ClubFromPhoneActivity.this
						.getContentResolver();
				// 获取手机联系人
				Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, null,
						null, null, null); // 传入正确的uri
				if (phoneCursor != null) {
					while (phoneCursor.moveToNext()) {
						int nameIndex = phoneCursor
								.getColumnIndex(Phone.DISPLAY_NAME);
						String name = phoneCursor.getString(nameIndex);
						String phoneNumber = phoneCursor.getString(phoneCursor
								.getColumnIndex(Phone.NUMBER)); // 获取联系人number
						if (TextUtils.isEmpty(phoneNumber)) {
							continue;
						}
						String phone = StrUtil
								.getMobilePhoneNumber(phoneNumber);
						if (phone == null) {
							continue;
						}

						phoneNumberList.add(phone);
						mSBPhoneList.append(phone + ",");
						phoneMap.put(phone, name);
					}
				}

				Uri uri = Uri.parse("content://icc/adn");
				phoneCursor = resolver.query(uri, null, null, null, null);
				if (phoneCursor != null) {
					while (phoneCursor.moveToNext()) {
						String name = phoneCursor.getString(phoneCursor
								.getColumnIndex("name"));
						String phoneNumber = phoneCursor.getString(phoneCursor
								.getColumnIndex("number"));
						if (TextUtils.isEmpty(phoneNumber)) {
							continue;
						}
						String phone = StrUtil
								.getMobilePhoneNumber(phoneNumber);
						if (phone == null) {
							continue;
						}
						phoneNumberList.add(phone);
						mSBPhoneList.append(phone + ",");
						phoneMap.put(phone, name);
					}
				}

			}
		});
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

	@Override
	public void onFinish(Pdtask t) {
		boolean succ = false;
		if (t.getParam("method").equals("listUserPhoneClub")) {

			if (t.json != null) {

				if (t.json.optJSONArray("results") != null) {
					// phoneClubList = jsonArray2PhoneClubInfoList(t.json
					// .optJSONArray("results"));
					newClubsArray = toJSONObjectList(t.json
							.optJSONArray("results"));
					adapter = new PhoneClubAdapter(this,
							R.layout.club_from_phone_list_item, newClubsArray);

					succ = true;
				}
			}
		} else if ("signPublicClub".equals(t.getParam("method"))) {
			requestData();
			succ = true;
		}
		if (succ) {
			setViewContent();
		}
		hideWait();
		super.onFinish(t);
	}


	protected void setViewContent() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				realListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		});

	}

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
	class PhoneClubAdapter extends ArrayAdapter<JSONObject> {

		public PhoneClubAdapter(Context context, int resource,
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
			final ClubWithCreatorInfo ci = json2ClubWithCreatorInfo(json);

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("jsonobjc", json.toString());
					intent.putExtra("from", "search_result_list");
					intent.putExtra(ClubInfoActivity.EXTRA_KEY_CLUB_USER_INFO_CLUB_JSON_STRING, json.toString());
					intent.putExtra(ClubInfoActivity.EXTRA_KEY_CLUB_GUID, json.optString("guid"));
					intent.setClass(mActivity, ClubInfoActivity.class);

//					intent.putExtra(
//							MyClubManagementActivity.EXTRA_PARAM_JSONOBJECT,
//							json.toString());
//					//
//					intent.putExtra(
//							MyClubManagementActivity.EXTRA_PARAM_CLUB_TYPE,
//							MyClubManagementActivity.CLUB_MANAGER_TYPE_USER);
//					intent.setClass(mActivity, MyClubManagementActivity.class);

					startActivity(intent);

				}

			});

			// firstletter
			// PhoneClubInfo current_fi = (PhoneClubInfo) getItem(position);
			ClubWithCreatorInfo pre_fi = null;
			if (position > 0) {
				pre_fi = json2ClubWithCreatorInfo(getItem(position - 1));
			}

			ClubWithCreatorInfo pos_fi = null;
			if (position < this.getCount() - 1) {
				pos_fi = json2ClubWithCreatorInfo(getItem(position + 1));
			}

			if (pos_fi == null) {
				tag.divider.setVisibility(View.VISIBLE);
			} else {
				if (ci.getFirstLetter().equals(pos_fi.getFirstLetter())) {
					tag.divider.setVisibility(View.VISIBLE);
				} else {
					tag.divider.setVisibility(View.GONE);
				}
			}
			if (pre_fi == null) {
				tag.firstLetter.setVisibility(View.VISIBLE);
				tag.firstLetter.setText(ci.getFirstLetter());
			} else {

				if (pre_fi.getFirstLetter().equals(ci.getFirstLetter())) {
					tag.firstLetter.setVisibility(View.GONE);
				} else {
					tag.firstLetter.setVisibility(View.VISIBLE);
					tag.firstLetter.setText(ci.getFirstLetter());
				}

			}

			// tag.my_clubs_clubicon.setImageUrl(fi.optString());
			tag.clubname.setText(ci.getClubname());
			String logoUrl = ci.getLogourl();
			if (logoUrl != null & logoUrl.length() > 10) {
				tag.clubicon.setImageUrl(logoUrl);
			} else {
				//
			}
			String phoneUserName = ci.getCreateUserName();
			tag.phoneUserName.setText("手机好友:" + phoneUserName);

			if (ClubFromPhoneActivity.PHONE_CLUB_ADDED.equals(ci.getStatus())) {
				tag.addButton.setVisibility(View.GONE);
				tag.addedView.setVisibility(View.VISIBLE);
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
