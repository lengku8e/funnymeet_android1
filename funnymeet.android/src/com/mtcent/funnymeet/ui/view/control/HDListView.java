package com.mtcent.funnymeet.ui.view.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.view.control.DropDownListView;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import mtcent.funnymeet.R;

public class HDListView extends DropDownListView {
	Context mContext;
	public HDListAdapter adapter;
	int istodayColor = Color.WHITE;
	ArrayList<ArrayList<JSONObject>> pageList = new ArrayList<ArrayList<JSONObject>>();
	ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// msg.what
			// msg.obj
			ArrayList<JSONObject> list = null;
			if (msg.what == 444) {
				list = (ArrayList<JSONObject>) msg.obj;
				pageList.clear();
				if (list != null) {
					pageList.add(list);
				}
				adapter.notifyDataSetChanged();
				stopHeaderWait(500);
			}

		}

	};

	public HDListView(Context context) {
		super(context);
		init(context);
	}

	public HDListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public HDListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	void init(Context context) {
		mContext = context;
		adapter = new HDListAdapter();
		setDropViewAdaper(adapter);
		setAdapter(adapter);
		setOnItemClickListener(new DropDownListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				onselectHD((int) arg3);
			}
		});
	}

	HDListViewAct act;

	public void setAct(HDListViewAct Act) {
		act = Act;
		fresh();
	}

	public interface HDListViewAct {
		public void loadMore(int pageIndex);

		public void fresh();

		public void onselectHD(int index, ArrayList<String> list);
	}

	void fresh() {
		if (act != null) {
			act.fresh();
		}
	}

	public void updata(final ArrayList<JSONObject> list) {
		Message msg = new Message();
		msg.what = 444;
		msg.obj = list;
		handler.sendMessage(msg);

	}

	void loadMore() {
		if (act != null && adapter.getCount() > 0) {
			act.loadMore(pageList.size() + 1);
		}
	}

	public void addMore(final int pageIndex,
			final ArrayList<JSONObject> moreList) {

		post(new Runnable() {
			@Override
			public void run() {
				if (pageIndex == pageList.size() + 1 && moreList != null) {
					pageList.add(moreList);
					adapter.notifyDataSetChanged();
				}
				stopFooterWait(500);
			}
		});
	}

	void onselectHD(int index) {
		JSONObject json = (JSONObject) adapter.getItem(index);
		int idIndex = index;
		if (act != null && json != null) {
			String id = json.optString("id", "");
			ArrayList<String> IDlist = new ArrayList<String>();
			for (ArrayList<JSONObject> list : pageList) {
				for (JSONObject j : list) {
					String i = j.optString("id", "");
					if (id.equals(i)) {
						idIndex = IDlist.size();
					}
					IDlist.add(i);
				}
			}

			act.onselectHD(idIndex, IDlist);
		}
	}

	public class HDListAdapter extends BaseAdapter implements DropViewAdaper {

		@Override
		public int getCount() {
			int num = 0;
			for (ArrayList<JSONObject> list : pageList) {
				num += list.size();
			}
			return num;
		}

		@Override
		public Object getItem(int arg0) {
			int n = 0;
			if (arg0 >= 0) {
				for (ArrayList<JSONObject> list : pageList) {
					if (n + list.size() > arg0) {
						return list.get(arg0 - n);
					} else {
						n += list.size();
					}
				}
			}
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		private LayoutInflater inflater = null;

		class TagObject {
			TextView nameTextView;
			TextView addrTextView;
			TextView timeTextView;
			TextView cityTextView;
			// TextView ticketsTextView;
			TextView leftDayTextView;
			TextView weekTextView;
			LinearLayout dateweekandleftdays;
			TextView lineTextView;
			TextView istoday;
			TextView leftdaysutiltext;
			TextView clubNameTextView;
			ImageView userJoinedView;
			ImageView userFocusedView;
			ArrayList<Integer> ticketsPrice = new ArrayList<Integer>();
			String id;
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			TagObject tag = null;
			if (arg1 == null) {
				tag = new TagObject();
				arg1 = inflater.inflate(R.layout.item_find_huodong_info, null);
				tag.nameTextView = (TextView) arg1.findViewById(R.id.name);
				tag.addrTextView = (TextView) arg1.findViewById(R.id.addr);
				tag.timeTextView = (TextView) arg1.findViewById(R.id.time);
				tag.cityTextView = (TextView) arg1.findViewById(R.id.city);
				tag.weekTextView = (TextView) arg1.findViewById(R.id.week);
				tag.leftDayTextView = (TextView) arg1
						.findViewById(R.id.leftday);
				// tag.ticketsTextView = (TextView)
				// arg1.findViewById(R.id.price);
				tag.dateweekandleftdays = (LinearLayout) arg1
						.findViewById(R.id.dateweekandleftdays);
				tag.lineTextView = (TextView) arg1
						.findViewById(R.id.hdlistdivider);
				tag.istoday = (TextView) arg1.findViewById(R.id.istoday);
				tag.leftdaysutiltext = (TextView) arg1
						.findViewById(R.id.leftdaysutiltext);
				tag.clubNameTextView = (TextView) arg1.findViewById(R.id.hd_clubname);
				tag.userFocusedView = (ImageView) arg1.findViewById(R.id.hd_list_item_user_focused);
				tag.userJoinedView = (ImageView) arg1.findViewById(R.id.hd_list_item_user_joined);
				arg1.setTag(tag);
			} else {
				tag = (TagObject) arg1.getTag();
			}

			JSONObject json = (JSONObject) getItem(arg0);
			if (json != null) {

				String result = "";
				String rawDate = "";
				String leftDays = "";
				String week = "";
				boolean isSameYear = false;
				JSONObject previousItemJson = (JSONObject) getItem(arg0 - 1);
				tag.timeTextView.setTextColor(0xff4184f5);
				tag.weekTextView.setTextColor(0xff4184f5);
				tag.leftDayTextView.setTextColor(0xff4184f5);
				tag.nameTextView.setText(json.optString("name"));
				tag.addrTextView.setText(json.optString("building"));
				tag.istoday.setBackgroundColor(istodayColor);
				tag.leftdaysutiltext.setTextColor(0xff4184f5);
				rawDate = new String(json.optString("startDate"));
				if (rawDate == null || rawDate.equals("")) {
					rawDate = "1970-1-1";
				}
				LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) tag.istoday
						.getLayoutParams();
				p.height = 2;
				try {
					week = StrUtil.dayForWeek(rawDate);
					Date todayDate = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd");
					String today = formatter.format(todayDate);
					String todaysWeek = StrUtil.dayForWeek(today);
					int days = StrUtil.daysBetween(today, rawDate);
					int tempDays = Integer.MAX_VALUE;
					String rawDateYear = rawDate.subSequence(0, 4).toString();
					if (today.subSequence(0, 4).equals(rawDateYear)) {
						isSameYear = true;
					}

					if (previousItemJson != null) {
						String previousItemDate = new String(
								previousItemJson.optString("startDate"));

						tempDays = StrUtil.daysBetween(previousItemDate,
								rawDate);

					}

					if (tempDays == 0) {
						tag.dateweekandleftdays.setVisibility(View.GONE);
						tag.lineTextView.setVisibility(View.VISIBLE);
					} else {

						tag.dateweekandleftdays.setVisibility(View.VISIBLE);
						tag.lineTextView.setVisibility(View.VISIBLE);

						if (days < 0) {
							days = -1 * days;
							tag.istoday.setBackgroundColor(istodayColor);

							leftDays = "-" + String.valueOf(days);
							tag.leftdaysutiltext.setText("");
						} else if (days == 1) {
							tag.istoday.setBackgroundColor(istodayColor);
							leftDays = "明天";
							tag.leftdaysutiltext.setText("");
						} else if (days == 2) {
							tag.istoday.setBackgroundColor(istodayColor);
							leftDays = "后天";
							tag.leftdaysutiltext.setText("");
						} else if (days > 2 && days <= 7) {
							tag.istoday.setBackgroundColor(istodayColor);
							if (((todaysWeek.equals("星期六")) || (todaysWeek
									.equals("星期日")))
									&& (week.equals("星期六") || (week
											.equals("星期日")))) {

								leftDays = String.valueOf(days) + "天";
								tag.leftdaysutiltext.setText("+");

							} else if (week.equals("星期六")) {
								leftDays = "本周六";
								tag.istoday.setBackgroundColor(istodayColor);
								tag.timeTextView.setTextColor(0xff45c01a);
								tag.weekTextView.setTextColor(0xff45c01a);
								tag.leftDayTextView.setTextColor(0xff45c01a);
								tag.leftdaysutiltext.setTextColor(0xff45c01a);
								tag.leftdaysutiltext.setText("");

							} else if (week.equals("星期日")) {
								leftDays = "本周日";
								tag.istoday.setBackgroundColor(istodayColor);
								tag.timeTextView.setTextColor(0xff45c01a);
								tag.weekTextView.setTextColor(0xff45c01a);
								tag.leftDayTextView.setTextColor(0xff45c01a);
								tag.leftdaysutiltext.setTextColor(0xff45c01a);
								tag.leftdaysutiltext.setText("");
							} else {
								leftDays = String.valueOf(days) + "天";
								tag.leftdaysutiltext.setText("+");
							}

						} else if (days == 0) {
							tag.istoday.setBackgroundColor(istodayColor);
							tag.timeTextView.setTextColor(0xffd6006f);
							tag.weekTextView.setTextColor(0xffd6006f);
							tag.leftDayTextView.setTextColor(0xffd6006f);
							leftDays = "今天";
							p.height = 2;
							tag.leftdaysutiltext.setText("");
						} else {
							if (week.equals("星期日") || week.equals("星期六")) {
								tag.istoday.setBackgroundColor(istodayColor);
								tag.timeTextView.setTextColor(0xff45c01a);
								tag.weekTextView.setTextColor(0xff45c01a);
								tag.leftDayTextView.setTextColor(0xff45c01a);
								tag.leftdaysutiltext.setTextColor(0xff45c01a);
							} else {
								tag.istoday.setBackgroundColor(istodayColor);
							}

							leftDays = String.valueOf(days) + "天";
							tag.leftdaysutiltext.setText("+");

						}
					}
					tag.istoday.setLayoutParams(p);
				} catch (Exception e) {
					e.printStackTrace();
				}

				String[] rawDateDivider = rawDate.split("-");
				if (Integer.valueOf(rawDateDivider[1]) < 10) {
					rawDateDivider[1] = rawDateDivider[1] + " ";
				}
				if (Integer.valueOf(rawDateDivider[2]) < 10) {
					rawDateDivider[2] = " " + rawDateDivider[2];
				}
				String month = rawDateDivider[1] + "月";
				String day = rawDateDivider[2] + "日";
				String year = rawDateDivider[0] + "年";
				if (isSameYear) {

					rawDate = month + day;
					tag.timeTextView.setText(rawDate);
				} else {
					rawDate = year + month + day;
					tag.timeTextView.setText(rawDate);
				}

				tag.weekTextView.setText(week);
				tag.leftDayTextView.setText(leftDays);
				//

				tag.cityTextView.setText(json.optString("city"));
				tag.clubNameTextView.setVisibility(View.GONE);
				tag.userFocusedView.setVisibility(View.GONE);
				tag.userJoinedView.setVisibility(View.GONE);
				
				// tag.img.setImageUrl(json.optString("tickets"));
			}
			return arg1;
		}

		public HDListAdapter() {
			inflater = LayoutInflater.from(mContext);
		}

		public class HeaderViewHolder {
			TextView table = null;
			ImageView p = null;
		}

		void toUpAnimation(HeaderViewHolder holder) {
			RotateAnimation P = new RotateAnimation(0.0F, 180.0F, 1, 0.5F, 1,
					0.5F);
			P.setDuration(150L);
			P.setFillEnabled(true);
			P.setFillAfter(true);
			holder.p.clearAnimation();
			holder.p.startAnimation(P);
		}

		void toDownAnimation(HeaderViewHolder holder) {
			RotateAnimation P = new RotateAnimation(180.0F, 0.0F, 1, 0.5F, 1,
					0.5F);
			P.setDuration(150L);
			P.setFillEnabled(true);
			P.setFillAfter(true);
			holder.p.clearAnimation();
			holder.p.startAnimation(P);
		}

		@Override
		public View doHeaderView(DropDownListView thiz, View convertView,
				int state) {
			HeaderViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.hd_headerview, null);
				holder = new HeaderViewHolder();
				holder.table = (TextView) convertView.findViewById(R.id.table);
				holder.p = (ImageView) convertView
						.findViewById(R.id.indicateImageView);
				convertView.setTag(holder);
			} else {
				holder = (HeaderViewHolder) convertView.getTag();
			}

			if (state == DropDownListView.showView) {

				holder.p.setVisibility(ImageView.VISIBLE);
				holder.table.setText("松手刷新");
				toUpAnimation(holder);
			} else if (state == DropDownListView.showwingView) {
				holder.p.setVisibility(ImageView.VISIBLE);
				holder.table.setText("下拉可以刷新");
				toDownAnimation(holder);
			} else if (state == DropDownListView.waitView) {
				holder.p.clearAnimation();
				holder.p.setVisibility(ImageView.GONE);
				holder.table.setText("正在刷新");
				fresh();
			} else if (state == DropDownListView.waitHideView) {
				holder.p.setVisibility(ImageView.GONE);
				holder.table.setText("刷新完毕");
			} else if (state == DropDownListView.hidingView) {
				holder.p.setVisibility(ImageView.GONE);
			}
			return convertView;
		}

		@Override
		public View doFooterView(DropDownListView thiz, View convertView,
				int state) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.hd_footerview, null);
			}
			if (state == DropDownListView.waitView) {
				loadMore();
			}
			return convertView;
		}

	}
}
