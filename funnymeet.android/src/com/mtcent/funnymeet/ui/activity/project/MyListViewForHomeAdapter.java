package com.mtcent.funnymeet.ui.activity.project;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.view.control.XVURLImageView;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import mtcent.funnymeet.R;

/**
 * Created by Administrator on 2015/8/16.
 */
public class MyListViewForHomeAdapter extends BaseAdapter {
    // 1 筹备 2 报名 3 进行中 4 结束 5 暂停 6 终止
    public static final String HD_STATUS_STARTING = "[筹备中]";
    public static final String HD_STATUS_JOINING = "[报名中]";
    public static final String HD_STATUS_PROCESSING = "[进行中]";
    public static final String HD_STATUS_CLOSED = "[已结束]";
    public static final String HD_STATUS_PAUSE = "[已暂停]";
    public static final String HD_STATUS_ABORTED = "[已中止]";

    LayoutInflater inflater;
    JSONArray infoSource;
    int istodayColor = Color.WHITE;

    public void setInfoSource(JSONArray infoSource) {
        this.infoSource = infoSource;
    }

    public void setInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public int getLastItemIndex() {
        return getCount() + 1;
    }

    class Tag {

        LinearLayout new_meeting_date_indicator_frame;
        TextView new_meeting_date;
        TextView new_meeting_days;
        TextView new_meeting_week;
        XVURLImageView new_meeting_icon;
        TextView new_meeting_title;
        ImageView new_meeting_status;
        TextView new_meeting_city;
        TextView new_meeting_building;
        TextView new_meeting_ground;
        View divider;

        ArrayList<Integer> ticketsPrice = new ArrayList<Integer>();
        String id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tag tag = null;

        if (convertView == null) {
            tag = new Tag();
            convertView = inflater
                    .inflate(R.layout.new_meeting_list_item, null);

            tag.new_meeting_date_indicator_frame = (LinearLayout) convertView
                    .findViewById(R.id.new_meeting_date_indicator_frame);
            tag.divider = convertView.findViewById(R.id.divider);
            tag.new_meeting_date = (TextView) convertView
                    .findViewById(R.id.new_meeting_date);
            tag.new_meeting_days = (TextView) convertView
                    .findViewById(R.id.new_meeting_days);
            tag.new_meeting_week = (TextView) convertView
                    .findViewById(R.id.new_meeting_week);
            tag.new_meeting_icon = (XVURLImageView) convertView
                    .findViewById(R.id.new_meeting_icon);

            tag.new_meeting_title = (TextView) convertView
                    .findViewById(R.id.new_meeting_title);

            tag.new_meeting_status = (ImageView) convertView
                    .findViewById(R.id.new_meeting_status);
            tag.new_meeting_city = (TextView) convertView
                    .findViewById(R.id.new_meeting_city);
            tag.new_meeting_building = (TextView) convertView
                    .findViewById(R.id.new_meeting_building);
            tag.new_meeting_ground = (TextView) convertView
                    .findViewById(R.id.new_meeting_ground);

            convertView.setTag(tag);
        } else {
            tag = (Tag) convertView.getTag();
        }

        JSONObject json = (JSONObject) getItem(position);
        if (json != null) {

            // String result = "";
            String rawDate = "";
            String leftDays = "";
            String week = "";
            boolean isSameYear = false;
            JSONObject previousItemJson = (JSONObject) getItem(position - 1);

            // 加入俱乐部的名字

            String clubLogoUrl = json.optString("clubLogoUrl");
            if (clubLogoUrl != null && clubLogoUrl.length() > 10) {
                // tag.new_meeting_icon.setImageUrl(clubLogoUrl);
            }
            tag.new_meeting_title.setText(json.optString("name"));
            tag.new_meeting_city.setText(json.optString("city"));
            tag.new_meeting_building.setText(json.optString("building"));
            tag.new_meeting_ground.setText(json.optString("location"));
            tag.new_meeting_icon.setImageUrl(clubLogoUrl);

            rawDate = new String(json.optString("startDate"));
            if (rawDate == null || rawDate.equals("")
                    || "null".endsWith(rawDate)) {
                rawDate = "1970-1-1";
            }

            try {
                week = StrUtil.dayForWeek(rawDate);
                Date todayDate = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
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

                    tempDays = StrUtil.daysBetween(previousItemDate, rawDate);

                }

                if (tempDays == 0) {

                    tag.new_meeting_date_indicator_frame
                            .setVisibility(View.GONE);
                    tag.divider.setVisibility(View.VISIBLE);
                } else {

                    tag.new_meeting_date_indicator_frame
                            .setVisibility(View.VISIBLE);
                    tag.divider.setVisibility(View.GONE);

                    if (days < 0) {
                        days = -1 * days;

                        leftDays = "-" + String.valueOf(days);
                    } else if (days == 1) {
                        leftDays = "明天";
                    } else if (days == 2) {
                        leftDays = "后天";
                    } else if (days > 2 && days <= 7) {
                        if (((todaysWeek.equals("星期六")) || (todaysWeek
                                .equals("星期日")))
                                && (week.equals("星期六") || (week.equals("星期日")))) {

                            leftDays = "+" + String.valueOf(days) + "天";

                        } else if (week.equals("星期六")) {
                            leftDays = "本周六";

                        } else if (week.equals("星期日")) {
                            leftDays = "本周日";

                        } else {
                            leftDays = "+" + String.valueOf(days) + "天";
                        }

                    } else if (days == 0) {

                        leftDays = "今天";
                    } else {
                        if (week.equals("星期日") || week.equals("星期六")) {

                        } else {
                        }

                        leftDays = "+" + String.valueOf(days) + "天";

                    }
                }
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
                tag.new_meeting_date.setText(rawDate);
            } else {
                rawDate = year + month + day;
                tag.new_meeting_date.setText(rawDate);
            }

            tag.new_meeting_days.setText(leftDays);
            tag.new_meeting_week.setText(week);

            int status = json.optInt("stateId");

        }
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= getCount()) {
            return 0;
        } else {
            return position;
        }
    }

    @Override
    public Object getItem(int position) {
        if (position < 0 || position >= getCount()) {
            return null;
        } else {
            return infoSource.optJSONObject(position);
        }
    }

    @Override
    public int getCount() {
        return infoSource.length();
    }
}
