package com.mtcent.funnymeet.ui.activity.discovery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import mtcent.funnymeet.R;

public class DiscussActivity extends BaseActivity {


    LayoutInflater inflater;
    JSONArray listUserDiscussionZoneSource;
    ListView discuss_list;
    DiscussListAdapter adapter;
    JSONObject jsonObject = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_find_discuss_group_list);
        init();
        requestListUserDiscussionZone();
    }

    private void requestListUserDiscussionZone() {

        RequestHelper.Pdtask task = new RequestHelper.Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);

        task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
        task.addParam("method", "listUserDiscussionZone");

        SOApplication.getDownLoadManager().startTask(task);
        showWait();

    }

    @Override
    public void onFinish(RequestHelper.Pdtask t) {

        if (t.getParam("method").equals("listUserDiscussionZone")) {
            boolean succ = false;
            final JSONObject json = t.json;
            JSONArray ja = null;

            if (json != null) {
                if ("ok".equals(json.optString("status"))) {

                    ja = json.optJSONArray("results");

                    succ = ja != null;

                }

            }

            if (succ) {

                listUserDiscussionZoneSource = ja;
                DiscussActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setSource(listUserDiscussionZoneSource);
                        adapter.notifyDataSetChanged();
                    }
                });

            } else {
                StrUtil.showMsg(mActivity, "失败");

            }
            hideWait();
        } else if (t.getParam("method").equals("listUserBoards")) {

            boolean succ = false;

            JSONArray jsonArray = t.json.optJSONArray("results");

            if (jsonArray != null && jsonObject != null) {

                Intent intent = new Intent();
                intent.setClass(DiscussActivity.this, DiscussTopicListActivity.class);
                intent.putExtra("jsonObject", jsonObject.toString());
                intent.putExtra("jsonArray", jsonArray.toString());
                hideWait();
                startActivity(intent);
            }

        }


    }


    private void requestListUserBoards(String diszone_guid) {

        RequestHelper.Pdtask task = new RequestHelper.Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);

        task.addParam("diszone_guid", diszone_guid);
        task.addParam("method", "listUserBoards");

        SOApplication.getDownLoadManager().startTask(task);
        showWait();


    }

    private void init() {

        adapter = new DiscussListAdapter();

        listUserDiscussionZoneSource = new JSONArray();

        final TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("讨论区 | 选择俱乐部");

        inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        adapter.setInflater(inflater);
        adapter.setSource(listUserDiscussionZoneSource);

        discuss_list = (ListView) this.findViewById(R.id.discuss_list);


        discuss_list.setAdapter(adapter);

        discuss_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                                    jsonObject = (JSONObject) discuss_list.getItemAtPosition(i);
                                                    String diszone_guid = jsonObject.optString("guid");
                                                    requestListUserBoards(diszone_guid);
                                                }
                                            }

        );

        final PtrClassicFrameLayout mPtrFrame = (PtrClassicFrameLayout) this.findViewById(R.id.rotate_header_list_view_frame);


        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
// default is false
        mPtrFrame.setPullToRefresh(false);
// default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);


    }


}




