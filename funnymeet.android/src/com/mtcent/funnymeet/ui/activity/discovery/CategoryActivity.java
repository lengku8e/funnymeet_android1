package com.mtcent.funnymeet.ui.activity.discovery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.view.control.XVURLImageView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import mtcent.funnymeet.R;

public class CategoryActivity extends Activity {

	ListView mListView;
	BaseAdapter adapter;
	LayoutInflater inflater;
	ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_category);
		requestData();
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

		TextView tv = (TextView) findViewById(R.id.titleTextView);
		tv.setText("分类浏览");

		inflater = (LayoutInflater) CategoryActivity.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		mListView = (ListView) findViewById(R.id.categoryList);

		adapter = new BaseAdapter() {
			class TagObject {
				TextView textView;
				String id;
				TextView find_date_oncalendar;
				XVURLImageView image;
				TextView divider;
			}

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				TagObject tag = null;
				if (arg1 == null) {
					tag = new TagObject();
					arg1 = inflater.inflate(R.layout.item_find_parent, null);
					tag.textView = (TextView) arg1.findViewById(R.id.name);
					tag.image = (XVURLImageView) arg1.findViewById(R.id.pic);
					tag.find_date_oncalendar = (TextView) arg1
							.findViewById(R.id.find_date_oncalendar);
					tag.divider = (TextView) arg1
							.findViewById(R.id.dividerForFind);
					arg1.setTag(tag);
				} else {
					tag = (TagObject) arg1.getTag();
				}

				JSONObject json = (JSONObject) getItem(arg0);
				if (json != null) {
					tag.id = json.optString("id");
					tag.textView.setText(json.optString("name"));
					if (json.optString("name").equals("时间")) {
						Date todayDate = new Date();
						SimpleDateFormat formatter = new SimpleDateFormat(
								"yyyy-MM-dd");
						String today = formatter.format(todayDate);
						String[] temp = today.split("-");
						tag.find_date_oncalendar.setText(Integer.valueOf(
								temp[2]).toString());

					} else {
						tag.find_date_oncalendar.setText("");
					}
					String url = json.optString("picurl");
					if (url == null || url.isEmpty()) {
						url = "local:pic.png";
					}
					tag.image.setImageUrl(url);
					LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) tag.divider
							.getLayoutParams();
					p.height = 1;

				}
				return arg1;
			}

			@Override
			public long getItemId(int arg0) {
				return arg0;
			}

			@Override
			public Object getItem(int arg0) {
				if (arg0 < getCount()) {
					return dataList.get(arg0);
				}

				return null;
			}

			@Override
			public int getCount() {
				return dataList.size();
			}
		};

		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				JSONObject json = (JSONObject) adapter.getItem(arg2);
				// if (json != null && arg2 == 0) {
				// Intent intent = new Intent();
				// intent.setClass(CategoryActivity.this,
				// HDTimeSelectActivity.class);
				// CategoryActivity.this.startActivity(intent);
				// }
				//
				// else if (json != null && arg2 == 1) {
				// Intent intent = new Intent();
				// intent.setClass(CategoryActivity.this,
				// HDAddressSelectActivity.class);
				// CategoryActivity.this.startActivity(intent);
				//
				// } else if (json != null && arg2 == 2) {
				//
				// Intent intent = new Intent();
				// intent.setClass(CategoryActivity.this,
				// HDHumanSelectActivity.class);
				// CategoryActivity.this.startActivity(intent);
				//
				// } else if (json != null && arg2 == 3) {
				// Intent intent = new Intent();
				// intent.setClass(CategoryActivity.this,
				// HDThemeSelectActivity.class);
				// CategoryActivity.this.startActivity(intent);
				//
				// } else if (json != null && arg2 == 4) {
				//
				// Intent intent = new Intent();
				// intent.setClass(CategoryActivity.this,
				// HDBrandSelectActivity.class);
				// CategoryActivity.this.startActivity(intent);
				// }
				if (json != null && arg2 == 0) {
					Intent intent = new Intent();
					intent.setClass(CategoryActivity.this,
							HDThemeSelectActivity.class);
					CategoryActivity.this.startActivity(intent);
				}
			}
		});
	}

	void requestData() {
		try {
			dataList = new ArrayList<JSONObject>();
			//
			// dataList.add(new JSONObject(
			// "{'name':'时间','picurl':'local:calendar.png','id':0}"));
			// dataList.add(new JSONObject(
			// "{'name':'地点','picurl':'local:buildings.png','id':1}"));
			// dataList.add(new JSONObject(
			// "{'name':'人物','picurl':'local:people.png','id':2}"));

			dataList.add(new JSONObject(
					"{'name':'主题','picurl':'local:activity.png','id':3}"));
			//
			// dataList.add(new JSONObject(
			// "{'name':'品牌','picurl':'local:brand.png','id':4}"));

			adapter.notifyDataSetChanged();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
