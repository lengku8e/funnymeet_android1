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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.model.FakeHDCircleInfo;
import com.mtcent.funnymeet.model.HDInfo;
import com.mtcent.funnymeet.model.ShareInfo;
import com.mtcent.funnymeet.ui.view.control.AutoGridView;
import com.mtcent.funnymeet.ui.view.control.XVURLImageView;

import java.util.ArrayList;
import java.util.Iterator;

import mtcent.funnymeet.R;

public class HDCircleActivity extends Activity {

	public static final int ID = HDCircleActivity.class.hashCode();

	ListView findhdcirclelist;
	View headViewFrame;
	RelativeLayout headView;
	LayoutInflater layoutInflater;
	ArrayList<String> singleSentence;

	View hdcircle_miniHDlist_item;
	int lineNumber;

	HDInfo h1, h2, h3, h4;
	ShareInfo s1, s2, s3, s4;

	FakeHDCircleInfo f1, f2, f3, f4;

	ArrayList<String> previewList;
	ArrayList<FakeHDCircleInfo> fakeHDCircleInfoList;
	ArrayList<String> fakeShareMiniHDList1, fakeShareMiniHDList2,
			fakeShareMiniHDList3, fakeShareMiniHDList4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_hdcircle_activity);
		init();

	}

	private View generateMiniHDList(String info) {

		layoutInflater = LayoutInflater.from(this);

		hdcircle_miniHDlist_item = layoutInflater.inflate(
				R.layout.find_hdcircle_hdsharelist_item, null);

		TextView hdTitle = (TextView) hdcircle_miniHDlist_item
				.findViewById(R.id.hdTitle);
		TextView days = (TextView) hdcircle_miniHDlist_item
				.findViewById(R.id.miniListDay);

		TextView month = (TextView) hdcircle_miniHDlist_item
				.findViewById(R.id.miniListMonth);

		String[] tmp = info.split(",");
		hdTitle.setText(tmp[1]);
		String[] dates = tmp[0].split("-");
		if (dates.length == 1) {
			month.setText("今");
			days.setText("天");
		} else {
			days.setText(dates[1] + "日");
			month.setText(dates[0] + "月");
		}

		return hdcircle_miniHDlist_item;

	}

	protected void init() {

		fakeShareMiniHDList1 = new ArrayList<String>();

		fakeShareMiniHDList1.add("08-31,intel Core i7 高端推荐会");
		fakeShareMiniHDList1.add("08-25,华硕高端型号主板产品发布会");
		fakeShareMiniHDList1.add("ing,intel高级总裁网友在线活动，请大家关注新浪微博");

		previewList = new ArrayList<String>();
		previewList
				.add("http://www.sinaimg.cn/dy/slidenews/21_img/2013_34/1604_2332841_749994.jpg");
		previewList
				.add("http://userserve-ak.last.fm/serve/500/77736250/Mass+Effect+3.jpg");
		previewList
				.add("http://static.cnbetacdn.com/newsimg/100615/07085501134103954.jpg");
		previewList
				.add("http://img4.imgtn.bdimg.com/it/u=4135954156,3162132280&fm=23&gp=0.jpg");
		previewList
				.add("http://img1.imgtn.bdimg.com/it/u=3842670699,3735848990&fm=23&gp=0.jpg");
		previewList.add("http://cyberzone.mobie.in/images/realracing3/r34.jpg");

		h1 = new HDInfo(fakeShareMiniHDList1, null, null);

		fakeShareMiniHDList2 = new ArrayList<String>();
		fakeShareMiniHDList2.add("08-25,周鸿祎接受冰桶挑战，点名马化腾");

		h2 = new HDInfo(fakeShareMiniHDList2, null, null);

		fakeShareMiniHDList3 = new ArrayList<String>();
		fakeShareMiniHDList3.add("ing,老罗讲堂第一季进行中");
		fakeShareMiniHDList3.add("09-01,老罗讲堂第二季宣讲会");
		h3 = new HDInfo(fakeShareMiniHDList3, null, null);

		fakeShareMiniHDList4 = new ArrayList<String>();
		fakeShareMiniHDList4.add("09-02,Oracle招聘会明天举行");
		h4 = new HDInfo(fakeShareMiniHDList4, previewList, null);

		s1 = new ShareInfo("local:intel.png", "3天前", "英特尔", "发布了一个活动",
				"酷睿i7免费送");
		s2 = new ShareInfo(
				"http://wenwen.sogou.com/p/20120120/20120120165059-1991262835.jpg",
				"1小时前", "360科技", "发布了一个活动", "冰桶挑战");

		s3 = new ShareInfo("local:chuizilogo.jpg", "1分钟前", "锤子科技", "发布了一个活动",
				"老罗讲堂");

		s4 = new ShareInfo("http://img.cool80.com/i/logo/mixed/yidian.jpg",
				"1分钟前", "EA", "分享了活动内容", "EA新品发布会现场不错哦，感谢趣聚赠送的门票");

		f1 = new FakeHDCircleInfo(s1, h1);
		f2 = new FakeHDCircleInfo(s2, h2);
		f3 = new FakeHDCircleInfo(s3, h3);
		f4 = new FakeHDCircleInfo(s4, h4);

		fakeHDCircleInfoList = new ArrayList<FakeHDCircleInfo>();
		fakeHDCircleInfoList.add(f1);
		fakeHDCircleInfoList.add(f2);
		fakeHDCircleInfoList.add(f3);
		fakeHDCircleInfoList.add(f4);

		// -----------------------------------------------------------------------------------------

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		layoutInflater = LayoutInflater.from(this);
		headViewFrame = (View) layoutInflater.inflate(
				R.layout.find_hdcircle_head, null);

		findhdcirclelist = (ListView) findViewById(R.id.findhdcirclelist);
		findhdcirclelist.addHeaderView(headViewFrame);
		findhdcirclelist.setAdapter(new myHdCircleListAdapter());
		TextView tv = (TextView) findViewById(R.id.titleTextView);
		tv.setText("活动圈");

	}

	class myHdCircleListAdapter extends BaseAdapter {

		class Tag {
			TextView pureText;
			TextView shareType;
			TextView shareTime;
			ImageView hdSingleImage;
			AutoGridView hdcirclePreview;
			MyGridViewAdapter myAdapter;
			TextView shareOwner;
			LinearLayout hdcircle_miniHDlist;
			XVURLImageView shareOwnerImage;
		}

		class MyGridViewAdapter extends BaseAdapter {
			class Tag {
				XVURLImageView picsPreview;
			}

			ArrayList<String> list;

			public void setList(ArrayList<String> list) {
				this.list = list;
				this.notifyDataSetChanged();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				Tag tag = null;

				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater) HDCircleActivity.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					// 使用View的对象itemView与R.layout.item关联
					convertView = inflater.inflate(
							R.layout.find_common_picspreview_item, null);
					tag = new Tag();
					tag.picsPreview = (XVURLImageView) convertView
							.findViewById(R.id.picsItem);

					convertView.setTag(tag);
				} else {
					tag = (Tag) convertView.getTag();
				}
				tag.picsPreview.setImageUrl((String) getItem(position));

				return convertView;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Object getItem(int position) {

				// TODO Auto-generated method stub
				return list.get(position);
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				if (list != null) {
					return list.size();
				} else {
					return 0;
				}

			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fakeHDCircleInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return fakeHDCircleInfoList.get(position);
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

			FakeHDCircleInfo fake = (FakeHDCircleInfo) getItem(position);
			ShareInfo shareInfo = fake.getShareInfo();
			final HDInfo hdInfo = fake.getHdInfo();

			if (convertView == null) {

				LayoutInflater inflater = (LayoutInflater) HDCircleActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.find_hdcircle_item,
						null);

				tag = new Tag();
				tag.pureText = (TextView) convertView
						.findViewById(R.id.pureText);

				tag.shareOwnerImage = (XVURLImageView) convertView
						.findViewById(R.id.shareOwnerImage);

				tag.shareOwner = (TextView) convertView
						.findViewById(R.id.shareOwner);

				tag.shareTime = (TextView) convertView
						.findViewById(R.id.shareTime);

				tag.shareType = (TextView) convertView
						.findViewById(R.id.shareType);

				tag.hdSingleImage = (ImageView) convertView
						.findViewById(R.id.hdSingleImage);
				tag.hdcirclePreview = (AutoGridView) convertView
						.findViewById(R.id.hdcirclePreview);

				tag.hdcircle_miniHDlist = (LinearLayout) convertView
						.findViewById(R.id.hdcircle_miniHDlist);

				tag.myAdapter = new MyGridViewAdapter();
				tag.hdcirclePreview.setAdapter(tag.myAdapter);

				tag.hdcirclePreview
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								Intent intent = new Intent();
								intent.putStringArrayListExtra("image",
										hdInfo.getPreviewList());
								intent.putExtra("index", arg2);
								intent.setClass(HDCircleActivity.this,
										PreviewImageActivity.class);
								startActivity(intent);
							}

						});
				for (Iterator<String> iter = hdInfo.getMiniHDList().iterator(); iter
						.hasNext();) {

					View vTmp = generateMiniHDList(iter.next());

					tag.hdcircle_miniHDlist.addView(vTmp);
				}
				convertView.setTag(tag);

			} else {
				tag = (Tag) convertView.getTag();
			}

			tag.pureText.setText(shareInfo.shareBrief);
			tag.shareOwnerImage.setImageUrl(shareInfo.getLogoUrl());
			tag.shareOwner.setText(shareInfo.getShareOwner());
			tag.shareTime.setText(shareInfo.shareTime);
			// tag.hdTitle.setText(hdInfo.getHdTitle());
			tag.shareType.setText(shareInfo.shareType);
			// tag.hddays.setText(hdInfo.getDays());

			if (shareInfo.shareType.equals("发布了一个活动")) {

				if (hdInfo.getMiniHDList().size() > 1) {
					tag.shareType.setText("发布了一组活动");
				}

				tag.hdSingleImage.setVisibility(View.GONE);
				tag.hdcirclePreview.setVisibility(View.GONE);
				tag.hdcircle_miniHDlist.setVisibility(View.VISIBLE);

			} else if (shareInfo.shareType.equals("分享了活动内容")) {

				tag.hdcircle_miniHDlist.setVisibility(View.GONE);

				if (hdInfo.getPreviewList().size() == 1) {
					tag.hdSingleImage.setVisibility(View.VISIBLE);
					tag.hdcirclePreview.setVisibility(View.GONE);
				} else {

					tag.hdSingleImage.setVisibility(View.GONE);
					tag.hdcirclePreview.setVisibility(View.VISIBLE);
				}

			}
			tag.myAdapter.setList(hdInfo.getPreviewList());
			return convertView;
		}

	}
}




