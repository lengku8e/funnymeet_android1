package com.mtcent.funnymeet.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.discovery.ChildDiscussionListViewAdapter;
import com.mtcent.funnymeet.ui.activity.discovery.DiscussTopicDetailActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import mtcent.funnymeet.R;


public class ChildDiscussionFragment extends Fragment implements RequestHelper.DownBack {

    int pageSize = 50;
    JSONArray infoSource;
    ChildDiscussionListViewAdapter cll;
    LoadMoreListViewContainer loadMoreListViewContainer;
    public static PtrClassicFrameLayout mPtrFrame;
    String board_guid = null;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View contextView = inflater.inflate(R.layout.fragment_child_discussion, container, false);

        //获取Activity传递过来的参数
        Bundle mBundle = getArguments();
        board_guid = mBundle.getString("board_guid");

        init(inflater, contextView);

        return contextView;
    }


    @Override
    public void onFinish(RequestHelper.Pdtask t) {

        JSONObject json = t.json;
        boolean succ = false;

        if (json != null && "ok".equals(json.optString("status"))) {
            infoSource = json.optJSONArray("results");
            succ = true;
        }

        if (succ) {

            ChildDiscussionFragment.this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cll.setSource(infoSource);
                    cll.notifyDataSetChanged();
                    mPtrFrame.refreshComplete();
                }
            });

        } else {
            StrUtil.showMsg(ChildDiscussionFragment.this.getActivity(), "失败");
        }

    }

    @Override
    public void onUpdate(RequestHelper.Pdtask t) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void init(final LayoutInflater inflater, View contextView) {


        final ListView topic_listview = (ListView) contextView.findViewById(R.id.topic_listview);

        infoSource = new JSONArray();
        cll = new ChildDiscussionListViewAdapter(infoSource, inflater);


        topic_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent();
                JSONObject jsonObject = (JSONObject) topic_listview.getItemAtPosition(i);
                intent.putExtra("jsonObject", jsonObject.toString());
                intent.setClass(ChildDiscussionFragment.this.getActivity(), DiscussTopicDetailActivity.class);
                startActivity(intent);
            }
        });


        // load more container
        loadMoreListViewContainer = (LoadMoreListViewContainer) contextView.findViewById(R.id.load_more_list_view_container);
        loadMoreListViewContainer.useDefaultHeader();

        mPtrFrame = (PtrClassicFrameLayout) contextView.findViewById(R.id.rotate_header_list_view_frame);

        topic_listview.setAdapter(cll);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

                if (board_guid != null) {
                    requestBoardPosts(board_guid);
                }

            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, topic_listview, header);
            }
        });
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.disableWhenHorizontalMove(true);
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);


    }


    public void requestBoardPosts(String board_guid) {

        RequestHelper.Pdtask task = new RequestHelper.Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);
        task.addParam("method", "listUserBoardPosts");// 页码
        task.addParam("board_guid", board_guid);
        task.addParam("page", String.valueOf(1));
        task.addParam("page_size", String.valueOf(pageSize));
        SOApplication.getDownLoadManager().startTask(task);
    }

}







