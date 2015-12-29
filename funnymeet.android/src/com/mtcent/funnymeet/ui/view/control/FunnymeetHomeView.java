package com.mtcent.funnymeet.ui.view.control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.model.WeekCalendar;
import com.mtcent.funnymeet.ui.activity.discovery.HDDetailsActivity;
import com.mtcent.funnymeet.ui.activity.project.MyListViewForHomeAdapter;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import mtcent.funnymeet.R;

@SuppressLint("InflateParams")
public class FunnymeetHomeView extends FunnymeetBaseView {

    XVScrollHPageView picScrollHPage;

    // TextView hostcityTextView;
    // JSONObject hostCity;
    PullToRefreshListView mainList;
    ListView realList;
    private LinearLayout emptyPlacehold;
    LayoutInflater myinflater;
    public MyListViewForHomeAdapter adapter;
    // private final String[] monthday_fields = new String[] { "",
    // "sunday_monthday", "monday_monthday", "tuesday_monthday",
    // "wendesday_monthday", "thursday_monthday", "friday_monthday",
    // "saturday_monthday", };
    private TextView monday_month;
    private TextView tuesday_month;
    private TextView wendesday_month;
    private TextView thursday_month;
    private TextView friday_month;
    private TextView saturday_month;
    private TextView sunday_month;
    TextView[] tvs = {sunday_month, monday_month, tuesday_month,
            wendesday_month, thursday_month, friday_month, saturday_month,};

    private LinearLayout monday;
    private LinearLayout tuesday;
    private LinearLayout wendesday;
    private LinearLayout thursday;
    private LinearLayout friday;
    private LinearLayout saturday;
    private LinearLayout sunday;
    int pageSize = 40;
    int currentIndex = 0;
    private boolean isFilterMenuOpen = false;
    PopupWindow popMenu;
    TextView switch_title;
    ImageView arrowIcon;
    private TextView tmpTextViewForRecordDay = null;

    private String selectedDay = null;

    JSONArray infoSource;

    public static final int TO_FORCED_CLUBHD_DETAILACTIVITY = 1717;

    public void onShow() {
        adapter.notifyDataSetChanged();
    }

    public FunnymeetHomeView(Activity activity) {
        super(activity);

        mainView = inflater.inflate(R.layout.hoster_list, null);
        init();
        requestListUserProject();

    }

    // 请求所有用户关注俱乐部的活动

    /**
     * 限定在用户加入的俱乐部的所有未结束活动 改善 #854
     */
    public void requestListUserProject() {

        Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);
        task.addParam("method", "listUserProject");// 页码
        task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
        task.addParam("user_session_guid",
                UserMangerHelper.getDefaultUserLongsession());
        task.addParam("page", String.valueOf(1));
        task.addParam("page_size", String.valueOf(pageSize));

        SOApplication.getDownLoadManager().startTask(task);

    }

    void loadMoreUserProject(int index) {

        Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);
        task.addParam("method", "listUserProject");
        task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
        task.addParam("user_session_guid",
                UserMangerHelper.getDefaultUserLongsession());
        task.addParam("page", String.valueOf(index));
        task.addParam("page_size", String.valueOf(pageSize));

        SOApplication.getDownLoadManager().startTask(task);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    void init() {

        final LinearLayout switchButton = (LinearLayout) mainView
                .findViewById(R.id.switch_button);
        arrowIcon = (ImageView) mainView.findViewById(R.id.arrow);

        switch_title = (TextView) switchButton.findViewById(R.id.switch_title);

        switchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (!isFilterMenuOpen) {
                    isFilterMenuOpen = true;
                    arrowIcon.setImageResource(R.drawable.up_arrow);
                    initPopMenu(v);

                } else {
                    isFilterMenuOpen = false;

                    arrowIcon
                            .setImageResource(R.drawable.abc_ic_go_search_api_holo_light);
                    if (popMenu != null) {
                        popMenu.dismiss();
                    }
                }

            }
        });

        WeekCalendar wc = new WeekCalendar();
        Map<String, String> weekDays = wc.getMonthDays(new Date());
        int[] viewIds = {R.id.sunday_monthday, R.id.monday_monthday,
                R.id.tuesday_monthday, R.id.wendesday_monthday,
                R.id.thursday_monthday, R.id.friday_monthday,
                R.id.saturday_monthday,};
        int[] llIds = {R.id.sunday, R.id.monday, R.id.tuesday, R.id.wendesday,
                R.id.thursday, R.id.friday, R.id.saturday,};

        LinearLayout[] lls = {sunday, monday, tuesday, wendesday, thursday,
                friday, saturday,};

        Calendar c = Calendar.getInstance();
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        final int year = c.get(Calendar.YEAR);
        for (int i = 0; i < 7; i++) {
            tvs[i] = (TextView) mainView.findViewById(viewIds[i]);
            tvs[i].setText(weekDays.get(WeekCalendar.monthday_fields[i + 1]));

            lls[i] = (LinearLayout) mainView.findViewById(llIds[i]);

            lls[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    tmpTextViewForRecordDay = (TextView) ((LinearLayout) v)
                            .getChildAt(0);
                    if (tmpTextViewForRecordDay != null) {
                        String day = String.valueOf(year) + "-"
                                + tmpTextViewForRecordDay.getText().toString();
                        selectedDay = day;
                        // StrUtil.showMsg(mActivity, selectedDay);
                        getCurrentDayActivityList(day);
                    } else {
                        selectedDay = null;
                    }
                }
            });
            // if (weekday == )
            if ((weekday - 1) == i) {
                lls[i].setBackgroundResource(R.drawable.weekday_today);
            } else {
                lls[i].setBackgroundResource(R.drawable.weekday_normal);
            }
        }

        // end of create weekcalander

        myinflater = inflater;
        adapter = new MyListViewForHomeAdapter();

        infoSource = new JSONArray();

        mainList = (PullToRefreshListView) mainView
                .findViewById(R.id.hosterlist);

        mainList.setMode(Mode.BOTH);

        mainList.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多...");
        mainList.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载...");

        mainList.setOnRefreshListener(new OnRefreshListener2() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase refreshView) {
                String label = DateUtils.formatDateTime(
                        mActivity.getApplicationContext(),
                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                requestListUserProject();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase refreshView) {
                loadMoreUserProject(adapter.getLastItemIndex());
            }
        });

        // 存放数据的listview
        realList = mainList.getRefreshableView();
        emptyPlacehold = (LinearLayout) mainView.findViewById(R.id.hosterempty);
        realList.setEmptyView(emptyPlacehold);

        adapter.setInflater(myinflater);
        adapter.setInfoSource(infoSource);
        realList.setAdapter(adapter);

        mainList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent();
                JSONObject hdJson = (JSONObject) realList
                        .getItemAtPosition(arg2);
                intent.putExtra("from", "HomepageHDList");
                intent.putExtra("hdJson", hdJson.toString());
                intent.setClass(mActivity, HDDetailsActivity.class);
                mActivity.startActivityForResult(intent,
                        TO_FORCED_CLUBHD_DETAILACTIVITY);
            }
        });

    }

    // 获取本周活动列表
    private void getCurrentWeekActivityList() {

        Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);
        task.addParam("method", "listUserProjectCurrWeek");// 页码
        task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
        task.addParam("user_session_guid",
                UserMangerHelper.getDefaultUserLongsession());
        task.addParam("page", String.valueOf(1));
        task.addParam("page_size", String.valueOf(pageSize));

        SOApplication.getDownLoadManager().startTask(task);

    }

    // 获取本月活动列表
    private void getCurrentMonthActivityList() {

        Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);
        task.addParam("method", "listUserProjectCurrMonth");// 页码
        task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
        task.addParam("user_session_guid",
                UserMangerHelper.getDefaultUserLongsession());
        task.addParam("page", String.valueOf(1));
        task.addParam("page_size", String.valueOf(pageSize));

        SOApplication.getDownLoadManager().startTask(task);

    }

    // 获取具体某日活动列表
    private void getCurrentDayActivityList(String day) {

        Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);
        task.addParam("method", "listUserProjectOneDay");// 页码
        task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
        task.addParam("user_session_guid",
                UserMangerHelper.getDefaultUserLongsession());
        task.addParam("page", String.valueOf(1));
        task.addParam("page_size", String.valueOf(pageSize));
        task.addParam("day", day);

        SOApplication.getDownLoadManager().startTask(task);

    }

    // 加载更多本周活动列表
    private void loadMoreCurrentWeekActivity(int index) {

        Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);
        task.addParam("method", "listUserProjectCurrWeek");
        task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
        task.addParam("user_session_guid",
                UserMangerHelper.getDefaultUserLongsession());
        task.addParam("page", String.valueOf(index));
        task.addParam("page_size", String.valueOf(pageSize));

        SOApplication.getDownLoadManager().startTask(task);
    }

    // 加载更多本月活动列表
    private void loadMoreCurrentMonthActivity(int index) {

        Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);
        task.addParam("method", "listUserProjectCurrWeek");
        task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
        task.addParam("user_session_guid",
                UserMangerHelper.getDefaultUserLongsession());
        task.addParam("page", String.valueOf(index));
        task.addParam("page_size", String.valueOf(pageSize));

        SOApplication.getDownLoadManager().startTask(task);
    }

    // 加载等多具体某一天的活动列表
    private void loadMoreCurrentDayActivity(int index) {

        if (selectedDay != null) {
            Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                    RequestHelper.Type_PostParam, null, 0, true);
            task.addParam("method", "listUserProjectCurrWeek");
            task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
            task.addParam("user_session_guid",
                    UserMangerHelper.getDefaultUserLongsession());
            task.addParam("page", String.valueOf(index));
            task.addParam("page_size", String.valueOf(pageSize));
            task.addParam("day", selectedDay);

            SOApplication.getDownLoadManager().startTask(task);
        }

    }

    private void initPopMenu(View trigger) {

        int popMenuWidth = trigger.getWidth();
        View popMenuframe = inflater.inflate(R.layout.popmenu_layout, null);
        popMenu = new PopupWindow(popMenuframe, popMenuWidth,
                LayoutParams.WRAP_CONTENT);

        final TextView currentWeek = (TextView) popMenuframe
                .findViewById(R.id.currentWeek);
        final TextView cuttentMonth = (TextView) popMenuframe
                .findViewById(R.id.currentMonth);

        final TextView allActivities = (TextView) popMenuframe
                .findViewById(R.id.all);

        allActivities.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch_title.setText(((TextView) v).getText());
                popMenu.dismiss();
                isFilterMenuOpen = false;
                arrowIcon
                        .setImageResource(R.drawable.abc_ic_go_search_api_holo_light);
                requestListUserProject();
            }
        });

        currentWeek.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch_title.setText(((TextView) v).getText());
                popMenu.dismiss();
                isFilterMenuOpen = false;
                arrowIcon
                        .setImageResource(R.drawable.abc_ic_go_search_api_holo_light);
                getCurrentWeekActivityList();
            }
        });

        cuttentMonth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch_title.setText(((TextView) v).getText());
                popMenu.dismiss();
                isFilterMenuOpen = false;
                arrowIcon
                        .setImageResource(R.drawable.abc_ic_go_search_api_holo_light);
                getCurrentMonthActivityList();

            }
        });

        popMenu.setFocusable(true);
        popMenu.setBackgroundDrawable(new ColorDrawable(0xffffffff));
        popMenu.showAsDropDown(trigger);
        popMenu.setOutsideTouchable(false);
        popMenu.update();

        popMenu.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                isFilterMenuOpen = false;
                arrowIcon
                        .setImageResource(R.drawable.abc_ic_go_search_api_holo_light);
            }
        });
    }

    @Override
    public void onFinish(Pdtask t) {
        boolean succ = false;
        if (t.getParam("method").equals("listUserProject")
                && t.getParam("page").equals("1")) {
            if (t.json != null) {
                infoSource = t.json.optJSONArray("results");
                succ = true;
            }
        } else if (t.getParam("method").equals("listUserProject")
                && !t.getParam("page").equals("1")) {
            if (t.json != null) {
                JSONArray tempSource = t.json.optJSONArray("results");
                if (tempSource != null && tempSource.length() != 0) {
                    for (int i = 0; i < tempSource.length(); i++) {
                        infoSource.put(tempSource.optJSONObject(i));
                    }
                    succ = true;

                } else if (tempSource != null && tempSource.length() == 0) {
                    StrUtil.showMsg(mActivity, "没有更多了");
                    succ = true;
                }
            }
        }
        if (t.getParam("method").equals("listUserProjectCurrWeek")
                && t.getParam("page").equals("1")) {

            if (t.json != null) {
                infoSource = t.json.optJSONArray("results");
                succ = true;
            }
        } else if (t.getParam("method").equals("listUserProjectCurrWeek")
                && !t.getParam("page").equals("1")) {
            if (t.json != null) {
                JSONArray tempSource = t.json.optJSONArray("results");
                if (tempSource != null && tempSource.length() != 0) {
                    for (int i = 0; i < tempSource.length(); i++) {
                        infoSource.put(tempSource.optJSONObject(i));
                    }
                    succ = true;

                } else if (tempSource != null && tempSource.length() == 0) {
                    StrUtil.showMsg(mActivity, "没有更多了");
                    succ = true;
                }
            }

        }

        if (t.getParam("method").equals("listUserProjectCurrMonth")
                && t.getParam("page").equals("1")) {

            if (t.json != null) {
                infoSource = t.json.optJSONArray("results");
                succ = true;
            }
        } else if (t.getParam("method").equals("listUserProjectCurrMonth")
                && !t.getParam("page").equals("1")) {
            if (t.json != null) {
                JSONArray tempSource = t.json.optJSONArray("results");
                if (tempSource != null && tempSource.length() != 0) {
                    for (int i = 0; i < tempSource.length(); i++) {
                        infoSource.put(tempSource.optJSONObject(i));
                    }
                    succ = true;

                } else if (tempSource != null && tempSource.length() == 0) {
                    StrUtil.showMsg(mActivity, "没有更多了");
                    succ = true;
                }
            }

        }

        if (t.getParam("method").equals("listUserProjectOneDay")
                && t.getParam("page").equals("1")) {

            if (t.json != null) {
                infoSource = t.json.optJSONArray("results");
                succ = true;
            }
        } else if (t.getParam("method").equals("listUserProjectOneDay")
                && !t.getParam("page").equals("1")) {
            if (t.json != null) {
                JSONArray tempSource = t.json.optJSONArray("results");
                if (tempSource != null && tempSource.length() != 0) {
                    for (int i = 0; i < tempSource.length(); i++) {
                        infoSource.put(tempSource.optJSONObject(i));
                    }
                    succ = true;

                } else if (tempSource != null && tempSource.length() == 0) {
                    StrUtil.showMsg(mActivity, "没有更多了");
                    succ = true;
                }
            }

        }

        if (succ) {

            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    adapter.setInfoSource(infoSource);
                    mainList.onRefreshComplete();
                    adapter.notifyDataSetChanged();
                }
            });

        }

        super.onFinish(t);
    }
}

