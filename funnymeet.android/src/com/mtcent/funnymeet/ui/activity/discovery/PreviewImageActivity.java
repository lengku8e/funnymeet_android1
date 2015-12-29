package com.mtcent.funnymeet.ui.activity.discovery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.view.control.PreviewImageView;
import com.mtcent.funnymeet.ui.view.control.ScrollHPageView;
import com.mtcent.funnymeet.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

public class PreviewImageActivity extends Activity implements ScrollHPageView.ScrollHPageACT,
		PreviewImageView.PreviewImageListen {
	List<PreviewImageView> listView = new ArrayList<PreviewImageView>();
	List<String> listUrl = new ArrayList<String>();
	TextView numTV = null;
	ScrollHPageView scrollHPageScrollView ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		int index = 0;
		RelativeLayout layout = new RelativeLayout(this);
		ViewGroup.LayoutParams params2 = new ViewGroup.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		super.setContentView(layout, params2);

		scrollHPageScrollView = new ScrollHPageView(this);
		RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout.addView(scrollHPageScrollView, params3);

		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		numTV = new TextView(this);
		layout.addView(numTV, params1);
		int padding = StrUtil.dip2px(this, 15);
		numTV.setPadding(padding, padding, padding, padding);
		numTV.setTextSize(18);
		numTV.setTextColor(0xffffffff);
		numTV.setText("");
		scrollHPageScrollView.setScrollHPageACT(this);
		Intent intent = getIntent();
		if (intent != null) {
			List<String> list = intent.getStringArrayListExtra("image");
			index = intent.getIntExtra("index", 0);
			if (list != null) {
				listUrl = list;
			}
		}
		int count = listUrl.size();

		for (int i = 0; i < count; i++) {
			PreviewImageView pImageView = new PreviewImageView(this);
			pImageView.setListen(this);
			listView.add(pImageView);
			scrollHPageScrollView.addPageView(pImageView);
			if (i == 0) {
				pImageView.setImageUrl(listUrl.get(i));
			}
		}
		if (listView.size() > 0 && listView.size() > index) {
			numTV.setText((index + 1) + "/" + listView.size());
			scrollHPageScrollView.scrollIndex(index);
		}
		// scrollHPageScrollView.setBackgroundColor(0xff303538);
		scrollHPageScrollView.setBackgroundColor(0xff000000);
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageChange(int index) {
		if (index < listView.size() && index < listUrl.size()) {
			PreviewImageView pImageView = listView.get(index);
			if (pImageView != null) {
				pImageView.setImageUrl(listUrl.get(index));
			}
			// 预读下一个
			int nextIndex = index + 1;
			if (nextIndex < listView.size() && nextIndex < listUrl.size()) {
				pImageView = listView.get(nextIndex);
				if (pImageView != null) {
					pImageView.setImageUrl(listUrl.get(nextIndex));
				}
			}
			numTV.setText((index + 1) + "/" + listView.size());
		}
	}

	@Override
	public void onLightClick() {
		finish();
	}

	@Override
	public void onLongClick() {
		//StrUtil.showMsg(this,"长按"+scrollHPageScrollView.getCurIndex());
	}
}
