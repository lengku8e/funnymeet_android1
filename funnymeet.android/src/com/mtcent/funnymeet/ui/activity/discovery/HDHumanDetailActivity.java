package com.mtcent.funnymeet.ui.activity.discovery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.model.FakeAlbum;
import com.mtcent.funnymeet.model.FakeNews;
import com.mtcent.funnymeet.ui.activity.discovery.HDDetailsActivity.HdDetails;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.control.CustomImageView;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.view.control.HDListView;
import com.mtcent.funnymeet.ui.view.control.ScrollHPageWithTableView;
import com.mtcent.funnymeet.ui.view.control.ScrollHPageWithTableView.ScrollHPageWithTableAdapter;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONObject;

import java.util.ArrayList;

import mtcent.funnymeet.R;

public class HDHumanDetailActivity extends Activity implements OnClickListener,
		DownBack, ScrollHPageWithTableAdapter {

	TextView titleTextView;
	ScrollHPageWithTableView scrollHPageWithTableView;
	HDListView activityList;
	View newsFrame;
	View albumFrame;
	ListView news;
	ListView albumList;
	ScrollView humandetailScrollView;
	int pageSize = 10;
	View humandetailScrollViewFrame;
	LinearLayout hdLocationDetail;
	myListViewAdapter newListAdapter;
	myAlbumListAdapter albumListAdapter;
	CustomImageView human_detail_previewImage;
	Bundle bundle;
	Intent intent;

	ArrayList<FakeNews> fakeNewsList = new ArrayList<FakeNews>();
	ArrayList<FakeAlbum> fakeAlbumList = new ArrayList<FakeAlbum>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_human_detail);
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

		intent = this.getIntent();
		bundle = intent.getExtras();

		LayoutInflater layoutInflater = LayoutInflater.from(this);
		newsFrame = layoutInflater.inflate(R.layout.find_news_list, null);
		news = (ListView) newsFrame.findViewById(R.id.findnewslist);

		humandetailScrollViewFrame = layoutInflater.inflate(
				R.layout.find_human_detail_information, null);
		humandetailScrollView = (ScrollView) humandetailScrollViewFrame
				.findViewById(R.id.humandetailScrollView);

		human_detail_previewImage = (CustomImageView) findViewById(R.id.human_detail_previewImage);
		human_detail_previewImage
				.setImageUrl("http://news.cnhubei.com/todaynews/sccmw/jmwb/201005/W020100529378915575989.jpg");

		albumFrame = layoutInflater.inflate(R.layout.find_common_albumlist,
				null);
		albumList = (ListView) albumFrame
				.findViewById(R.id.find_common_albumlist);
		fakeNewsList.add(new FakeNews("探秘马云马化腾背后的爸爸", "网易财经", "约4分钟前"));
		fakeNewsList.add(new FakeNews("搜狗王小川如何搞定马云马化腾", "百度新闻", "约20分钟前"));
		fakeNewsList.add(new FakeNews("茂业物流蹊跷收购 马化腾抽血上市公司？", "和讯", "约12分钟前"));
		fakeNewsList
				.add(new FakeNews("李嘉诚马化腾 不为人知的潮汕商帮到底多强大？", "凤凰资讯", "约8分钟前"));
		fakeNewsList.add(new FakeNews("马云：我的管理和领导方法是中国最好的，胜过马化腾和李彦宏",
				"网易新闻客户端", "约1分钟前"));

		fakeAlbumList.add(new FakeAlbum("人物图片", "32",
				R.drawable.mahuatenghuodong1));
		fakeAlbumList.add(new FakeAlbum("相关活动", "64",
				R.drawable.mahuatenghuodong2));

		findViewById(R.id.menu).setVisibility(View.VISIBLE);
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("人物/" + bundle.getString("title"));

		scrollHPageWithTableView = (ScrollHPageWithTableView) findViewById(R.id.scrollHPageWithTable);
		scrollHPageWithTableView.setTableContextLayoutBackColor(0x80eeeeee);
		newListAdapter = new myListViewAdapter();
		albumListAdapter = new myAlbumListAdapter();
		news.setAdapter(newListAdapter);
		news.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("title", bundle.getString("title"));
				intent.setClass(HDHumanDetailActivity.this,
						HDCommonNewsContentActivity.class);
				startActivity(intent);

			}

		});
		albumList.setAdapter(albumListAdapter);

		albumList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(HDHumanDetailActivity.this,
						PicsPreviewActivity.class);
				startActivityForResult(intent, PicsPreviewActivity.ID);
			}

		});

		activityList = new HDListView(this);
		activityList.setAct(new HDListView.HDListViewAct() {

			@Override
			public void onselectHD(int index, ArrayList<String> list) {
				// TODO Auto-generated method stub
				if (index < list.size()) {
					HdDetails info = new HdDetails();
					info.index = index;
					info.list = list;
					Intent mIntent = new Intent(HDHumanDetailActivity.this,
							HDDetailsActivity.class);
					Bundle mBundle = new Bundle();
					mBundle.putSerializable(HdDetails.key, info);
					mIntent.putExtras(mBundle);
					HDHumanDetailActivity.this.startActivity(mIntent);
				}
			}

			@Override
			public void loadMore(int pageIndex) {
				// TODO Auto-generated method stub
				RequestHelper.DownBack back = new RequestHelper.DownBack() {

					@Override
					public void onFinish(Pdtask t) {
						ArrayList<JSONObject> list = StrUtil
								.getJSONArrayList(t.json);
						// allView.updateCategory(list);
						int pageIndex = Integer.valueOf(t.getParam("page"))
								.intValue();
						if (pageIndex == 1) {

						} else {
							activityList.addMore(pageIndex, list);
						}
						if (list.size() == pageSize) {
							activityList.setLoadMoreAble(true);
						} else {
							activityList.setLoadMoreAble(false);
						}
					}

					@Override
					public void onUpdate(Pdtask t) {
						onFinish(t);
					}

				};
				Pdtask task = new Pdtask(this, back, Constants.SERVICE_HOST,
						null, RequestHelper.Type_DownJsonString, null, 0,
						true);
				task.addParam("method", "listProject")
				// 一级主题id，-1代表全部
						.addParam("parent_subject_id", String.valueOf(-1))
						// 二级主题id，-1代表全部
						.addParam("child_subject_id", String.valueOf(-1))
						// 一级形式 -1代表全部
						.addParam("parent_type_id", String.valueOf(-1))
						// 二级形式 -1代表全部
						.addParam("child_type_id", String.valueOf(-1))

						.addParam("filter_price", "0")// 价格区间id，
						.addParam("start_price", "0")//
						.addParam("end_price", "0")//
						.addParam("filter_sort", "0")//
						// .addParam("start_date", start_date)//
						// .addParam("end_date", end_date)//
						.addParam("filter_date", "0")//
						// -1全部2报名中 3进行中4已结束
						.addParam("state", "-1")
						// 分页大小
						.addParam("page_size", pageSize + "")
						// 页码
						.addParam("page", String.valueOf(pageIndex));
				SOApplication.getDownLoadManager().startTask(task);
			}

			@Override
			public void fresh() {
				// TODO Auto-generated method stub
				RequestHelper.DownBack back = new RequestHelper.DownBack() {

					@Override
					public void onFinish(Pdtask t) {
						ArrayList<JSONObject> list = StrUtil
								.getJSONArrayList(t.json);
						// allView.updateCategory(list);

						activityList.updata(list);

					}

					@Override
					public void onUpdate(Pdtask t) {
						onFinish(t);
					}

				};
				Pdtask task = new Pdtask(this, back, Constants.SERVICE_HOST,
						null, RequestHelper.Type_DownJsonString, null, 0,
						true);
				task.addParam("method", "listProject")
				// 一级主题id，-1代表全部
						.addParam("parent_subject_id", String.valueOf(-1))
						// 二级主题id，-1代表全部
						.addParam("child_subject_id", String.valueOf(-1))
						// 一级形式 -1代表全部
						.addParam("parent_type_id", String.valueOf(-1))
						// 二级形式 -1代表全部
						.addParam("child_type_id", String.valueOf(-1))

						.addParam("filter_price", "0")// 价格区间id，
						.addParam("start_price", "0")//
						.addParam("end_price", "0")//
						.addParam("filter_sort", "0")//
						// .addParam("start_date", start_date)//
						// .addParam("end_date", end_date)//
						.addParam("filter_date", "0")//
						// -1全部2报名中 3进行中4已结束
						.addParam("state", "-1")
						// 分页大小
						.addParam("page_size", pageSize + "")
						// 页码
						.addParam("page", String.valueOf(1));
				SOApplication.getDownLoadManager().startTask(task);
			}
		});
		scrollHPageWithTableView.setScrollHPageWithTableAdapter(this);

	}

	@Override
	public int getPageCount() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public String getTableString(int index) {
		// TODO Auto-generated method stub
		String table = "";
		if (index == 3) {
			table = "活动";
		} else if (index == 1) {
			table = "动态";
		} else if (index == 2) {
			table = "图片";
		} else if (index == 0) {
			table = "简介";
		}
		return table;
	}

	@Override
	public View getPageView(int index) {
		// TODO Auto-generated method stub
		View v = null;
		if (index == 3) {
			v = activityList;
		} else if (index == 1) {
			v = newsFrame;
		} else if (index == 2) {
			v = albumFrame;
		} else if (index == 0) {
			v = humandetailScrollViewFrame;
		}

		return v;
	}

	@Override
	public void onPageChange(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinish(Pdtask t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate(Pdtask t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	class myAlbumListAdapter extends BaseAdapter {

		class Tag {

			TextView albumName;
			TextView nums;
			ImageView albumCover;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fakeAlbumList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return fakeAlbumList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			Tag tag = null;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) HDHumanDetailActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				tag = new Tag();
				// 使用View的对象itemView与R.layout.item关联
				convertView = inflater.inflate(
						R.layout.find_common_albumlist_item, null);
				tag.albumName = (TextView) convertView
						.findViewById(R.id.albumName);
				tag.nums = (TextView) convertView
						.findViewById(R.id.numsOfAlbum);
				tag.albumCover = (ImageView) convertView
						.findViewById(R.id.albumCover);
				convertView.setTag(tag);
			} else {
				tag = (Tag) convertView.getTag();
			}

			FakeAlbum fa = (FakeAlbum) getItem(position);
			tag.albumName.setText(fa.getAlbumName());
			tag.nums.setText(fa.getNumsOfAlbum());
			tag.albumCover.setImageResource(fa.getImageResource());

			return convertView;
		}
	}

	class myListViewAdapter extends BaseAdapter {

		class Tag {

			TextView newsTitile;
			TextView source;
			TextView newsTimeInfo;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fakeNewsList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return fakeNewsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Tag tag = null;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) HDHumanDetailActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				// 使用View的对象itemView与R.layout.item关联
				convertView = inflater.inflate(R.layout.find_news_list_item,
						null);

				tag = new Tag();
				tag.newsTimeInfo = (TextView) convertView
						.findViewById(R.id.newsTimeInfo);
				tag.newsTitile = (TextView) convertView
						.findViewById(R.id.newsTitle);
				tag.source = (TextView) convertView
						.findViewById(R.id.newsSource);
				convertView.setTag(tag);

			} else {

				tag = (Tag) convertView.getTag();
			}

			FakeNews fn = (FakeNews) getItem(position);
			tag.newsTimeInfo.setText(fn.getTimeInfo());
			tag.newsTitile.setText(fn.getTitle());
			tag.source.setText(fn.getSource());

			return convertView;
		}

	}

}
