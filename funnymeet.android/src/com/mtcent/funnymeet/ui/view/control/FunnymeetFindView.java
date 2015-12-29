package com.mtcent.funnymeet.ui.view.control;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.activity.discovery.CategoryActivity;
import com.mtcent.funnymeet.ui.activity.discovery.DiscussActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import mtcent.funnymeet.R;


public class FunnymeetFindView extends FunnymeetBaseView {
    ListView mListView;
    BaseAdapter adapter;
    ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();

    public FunnymeetFindView(Activity activity) {
        super(activity);
        mainView = inflater.inflate(R.layout.somain_find_parent_list, null);
        init();
        requestData();
    }

    void init() {
        mListView = (ListView) mainView.findViewById(R.id.parentList);
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

                    if (arg0 == 0 || arg0 == 5) {
                        p.height = 1;
                    } else {
                        p.height = 1;
                    }
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
                // intent.setClass(mActivity, HDCircleActivity.class);
                // mActivity.startActivity(intent);
                // } else if (json != null && arg2 == 1) {
                // Intent intent = new Intent();
                // intent.setClass(mActivity, CategoryActivity.class);
                // mActivity.startActivity(intent);
                // } else if (json != null && arg2 == 2) {
                // Intent intent = new Intent();
                // intent.setClass(mActivity, ScanQRCodeActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // mActivity.startActivityForResult(intent,
                // SOMainActivity.SCANNIN_GREQUEST_CODE);
                // }
                if (json != null && arg2 == 0) {
                    Intent intent = new Intent();
                    intent.setClass(mActivity, CategoryActivity.class);
                    mActivity.startActivity(intent);
                } else if (json != null && arg2 == 1) {

                    Intent intent = new Intent();
                    intent.setClass(mActivity, DiscussActivity.class);
                    mActivity.startActivity(intent);
                }
            }
        });

    }

    public void onScannin(String text, Bitmap bitmap) {
        StrUtil.showMsg(mActivity, text);
    }

    @Override
    public void onShow() {
        resetView();
        super.onShow();
    }

    @Override
    public void onFinish(Pdtask t) {

        super.onFinish(t);
    }

    void resetView() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    void requestData() {
        try {
            dataList = new ArrayList<JSONObject>();

            // dataList.add(new JSONObject(
            // "{'name':'活动圈','picurl':'local:circle.png','id':0}"));

            //dataList.add(new JSONObject(
            //        "{'name':'分类浏览','picurl':'local:box.png','id':1}"));

            dataList.add(new JSONObject(
                    "{'name':'讨论区','picurl':'local:calendar.png','id':2}"));

            // dataList.add(new JSONObject(
            // "{'name':'时间','picurl':'local:calendar.png','id':0}"));
            // dataList.add(new JSONObject(
            // "{'name':'地点','picurl':'local:buildings.png','id':1}"));
            // dataList.add(new JSONObject(
            // "{'name':'人物','picurl':'local:people.png','id':2}"));
            //
            // dataList.add(new JSONObject(
            // "{'name':'主题','picurl':'local:activity.png','id':3}"));
            //
            // dataList.add(new JSONObject(
            // "{'name':'品牌','picurl':'local:brand.png','id':4}"));
            //
            // dataList.add(new JSONObject(
            // "{'name':'扫一扫','picurl':'local:binarycode.png','id':2}"));

            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
