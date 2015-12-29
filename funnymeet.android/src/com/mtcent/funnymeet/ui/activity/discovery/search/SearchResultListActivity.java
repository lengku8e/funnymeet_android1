package com.mtcent.funnymeet.ui.activity.discovery.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.util.SearchUtilStack;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.activity.club.ClubInfoActivity;
import com.mtcent.funnymeet.ui.activity.my.myprofile.MemberInformationActivity;
import com.mtcent.funnymeet.ui.view.control.CustomImageView;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import mtcent.funnymeet.R;

@SuppressLint("InflateParams")
public class SearchResultListActivity extends BaseActivity {

	private int mSearchType;
	
	private String mCurrentClub;
	private String mCurrentClubGuid;
	
	ListView search_result_list;
	BaseAdapter adapter;

	Intent get_intent;
	LayoutInflater inflater;

	JSONArray searchResultList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result_list);
		if (getIntent().hasExtra(SearchActivity.EXTRA_KEY_SEARCH_TYPE)) {
			this.mSearchType = getIntent().getIntExtra(SearchActivity.EXTRA_KEY_SEARCH_TYPE, SearchActivity.SEARCH_TYPE_CLUB);
			if (this.mSearchType == SearchActivity.SEARCH_TYPE_MEMBER) {
				//取得传入的当前俱乐部信息 
				this.mCurrentClub = getIntent().getStringExtra(SearchActivity.EXTRA_KEY_CURRENT_CLUB);
				this.mCurrentClubGuid = getIntent().getStringExtra(SearchActivity.EXTRA_KEY_CURRENT_CLUB_GUID);
			}
		} else {
			this.mSearchType = SearchActivity.SEARCH_TYPE_CLUB;
		}
		SearchUtilStack.searchUtilStack.add(this);
		init();
		commit();
	}

	void commit() {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		if (this.mSearchType == SearchActivity.SEARCH_TYPE_MEMBER) {
			task.addParam("method", "searchSHDMembers");
			task.addParam("parameter", get_intent.getStringExtra("keyword"));
			
		} else {
			task.addParam("method", "searchClubByKeyword");
			task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
			task.addParam("user_session_guid",
					UserMangerHelper.getDefaultUserLongsession());
			task.addParam("keyword", get_intent.getStringExtra("keyword"));
		}

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	void setViewContent() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onFinish(Pdtask t) {
		boolean succ = false;
		if (t.getParam("method").equals("searchClubByKeyword")) {
			if (t.json != null) {
				succ = true;
				searchResultList = t.json.optJSONArray("results");
				hideWait();
			}
		} else if (t.getParam("method").equals("searchSHDMembers")) {
			if (t.json != null) {
				succ = true;
				searchResultList = t.json.optJSONArray("results");
				hideWait();
			}
		}
		
		if (succ) {
			setViewContent();
		}
		super.onFinish(t);
	}

	protected void init() {

		searchResultList = new JSONArray();

		get_intent = this.getIntent();

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		TextView tv = (TextView) findViewById(R.id.titleTextView);
		tv.setText("搜索结果");

		inflater = (LayoutInflater) SearchResultListActivity.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		search_result_list = (ListView) findViewById(R.id.search_result_list);

		adapter = new BaseAdapter() {

			class Tag {

				TextView result_title;
				TextView result_brief;
				CustomImageView result_icon;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				Tag tag = null;
				if (convertView == null) {

					convertView = inflater.inflate(
							R.layout.search_result_list_item, null);

					tag = new Tag();

					tag.result_brief = (TextView) convertView
							.findViewById(R.id.result_brief);
					tag.result_icon = (CustomImageView) convertView
							.findViewById(R.id.result_icon);
					tag.result_title = (TextView) convertView
							.findViewById(R.id.result_title);
					convertView.setTag(tag);

				} else {

					tag = (Tag) convertView.getTag();
				}

				JSONObject fsr = (JSONObject) getItem(position);

				if (fsr != null) {
					setResultText(tag, fsr);
				}
				return convertView;
			}

			private void setResultText(Tag tag, JSONObject fsr) {
				String str = "";

				if (SearchResultListActivity.this.mSearchType == SearchActivity.SEARCH_TYPE_MEMBER) {
					if (fsr.optString("nickname") != null) {
						str = fsr.optString("nickname");
					} 

					tag.result_brief.setText(str);
					// tag.result_icon.setImageUrl(fsr.getImageUr());
					tag.result_title.setText(fsr.optString("accountName") + "[" + fsr.optString("mobilePhone") + "]");
				} else {
					if (fsr.optString("typeId").equals("0")) {
						str = "私人俱乐部";
					} else if (fsr.optString("typeId").equals("1")) {
						str = "公众俱乐部";
					}

					tag.result_brief.setText(str);
					// tag.result_icon.setImageUrl(fsr.getImageUr());
					tag.result_title.setText(fsr.optString("name"));
				}
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				if (position < 0 || position >= getCount()) {
					return null;
				} else {
					return searchResultList.optJSONObject(position);
				}
			}

			@Override
			public int getCount() {
				return searchResultList.length();
			}
		};

		search_result_list.setAdapter(adapter);

		search_result_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				JSONObject fsr = (JSONObject) adapter.getItem((int) arg3);

				if (fsr != null) {

					if (SearchResultListActivity.this.mSearchType == SearchActivity.SEARCH_TYPE_MEMBER) {
						Intent intent = new Intent();

						intent.putExtra(MemberInformationActivity.EXTRA_KEY_MEMBER_JSON, fsr.toString());
						intent.putExtra("from", "search_result_list");
						intent.putExtra(ClubInfoActivity.EXTRA_KEY_CLUB_GUID, fsr.optString("guid"));
						intent.putExtra(ClubInfoActivity.EXTRA_KEY_CLUB_USER_INFO_CLUB_JSON_STRING, fsr.toString());
						intent.putExtra(SearchActivity.EXTRA_KEY_CURRENT_CLUB, SearchResultListActivity.this.mCurrentClub);
						intent.putExtra(SearchActivity.EXTRA_KEY_CURRENT_CLUB_GUID, SearchResultListActivity.this.mCurrentClubGuid);
						intent.setClass(SearchResultListActivity.this,
								MemberInformationActivity.class);
						startActivity(intent);
					} else {
						Intent intent = new Intent();

						intent.putExtra("jsonobjc", fsr.toString());
						intent.putExtra("from", "search_result_list");
						intent.putExtra(ClubInfoActivity.EXTRA_KEY_CLUB_USER_INFO_CLUB_JSON_STRING, fsr.toString());
						intent.putExtra(ClubInfoActivity.EXTRA_KEY_CLUB_GUID, fsr.optString("guid"));
						intent.setClass(SearchResultListActivity.this,
								ClubInfoActivity.class);
						startActivity(intent);
					}
				}
			}
		});
	}
}
