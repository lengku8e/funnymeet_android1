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
import android.widget.GridView;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.view.control.XVURLImageView;

import java.util.ArrayList;

import mtcent.funnymeet.R;

public class PicsPreviewActivity extends Activity {

	ArrayList<String> fakePicsUrl = new ArrayList<String>();
	GridView gv;
	MyGridViewAdapter myGridViewAdapter;
	TextView howmanyPics;
	public static final int ID = PicsPreviewActivity.class.hashCode();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_common_picspreview);
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
		tv.setText("图片预览");
		howmanyPics = (TextView) findViewById(R.id.howmanyPics);

		for (int i = 0; i < 1; i++) {

			fakePicsUrl
					.add("http://zcimg.zcool.com.cn/zcimg/m_6a2f53d0d21800000115b7ce0845.jpg");
			fakePicsUrl
					.add("http://zcimg.zcool.com.cn/zcimg/m_328453d0d11100000115b72aaceb.jpg");
			fakePicsUrl
					.add("http://zcimg.zcool.com.cn/zcimg/m_564553d0d21e00000115b783471b.jpg");
			fakePicsUrl
					.add("http://zcimg.zcool.com.cn/zcimg/m_26de53d0d21b00000115b7145bf8.jpg");
			fakePicsUrl
					.add("http://zcimg.zcool.com.cn/zcimg/m_f57253d0d22200000115b70c5b75.jpg");
			fakePicsUrl
					.add("http://zcimg.zcool.com.cn/zcimg/m_fcd153d0d14b00000115b7bb4734.jpg");
			fakePicsUrl
					.add("http://zcimg.zcool.com.cn/zcimg/m_663d53d0d15d00000115b7bb5fe0.jpg");
			fakePicsUrl
					.add("http://zcimg.zcool.com.cn/zcimg/m_637053d0d16c00000115b75d5759.jpg");
			fakePicsUrl
					.add("http://zcimg.zcool.com.cn/zcimg/m_2d5053d0d1a000000115b7b00eba.jpg");

			fakePicsUrl
					.add("http://zcimg.zcool.com.cn/zcimg/m_4f3b535953590000016fe2280065.jpg");
			fakePicsUrl
					.add("http://zcimg.zcool.com.cn/zcimg/m_afb253a265f1000001791fa4c395.jpg");
			fakePicsUrl
					.add("http://zcimg.zcool.com.cn/zcimg/m_427453a265af000001791f43914d.jpg");
			fakePicsUrl
					.add("http://zcimg.zcool.com.cn/zcimg/m_83b053a265bf000001791feb474f.jpg");
			fakePicsUrl
					.add("http://zcimg.zcool.com.cn/zcimg/m_d64253a265b9000001791f2f6108.jpg");

		}

		gv = (GridView) findViewById(R.id.picsPreview);
		myGridViewAdapter = new MyGridViewAdapter();
		gv.setAdapter(myGridViewAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				Intent intent = new Intent();
				intent.putStringArrayListExtra("image", fakePicsUrl);
				intent.putExtra("index", arg2);
				intent.setClass(PicsPreviewActivity.this,
						PreviewImageActivity.class);
				startActivity(intent);

			}

		});

	}

	class MyGridViewAdapter extends BaseAdapter {

		class Tag {
			XVURLImageView picsPreview;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fakePicsUrl.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return fakePicsUrl.get(arg0);
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
				LayoutInflater inflater = (LayoutInflater) PicsPreviewActivity.this
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
			String imageUrl = (String) getItem(position);
			tag.picsPreview.setImageUrl(imageUrl);
			howmanyPics.setText(getCount() + " 张照片");
			return convertView;
		}

	}

}
