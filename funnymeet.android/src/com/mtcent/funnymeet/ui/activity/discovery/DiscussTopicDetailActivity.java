package com.mtcent.funnymeet.ui.activity.discovery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mtcent.funnymeet.R;

public class DiscussTopicDetailActivity extends BaseActivity {

    Intent get_intent;

    JSONObject topic_info_jsonObject = null;
    JSONArray commentsList = null;
    CommentsListAdapter commentsListAdapter = null;

    TextView topic_owner;
    TextView topic_content;
    TextView topic_title;
    EditText reply_content;
    JSONObject mainPostInfo = null;
    ListView comment_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_topic_detail_activity);
        init();
        if (topic_info_jsonObject != null) {
            requestListPostWithReply(topic_info_jsonObject.optString("postId"));
        }

    }

    @Override
    public void onFinish(RequestHelper.Pdtask t) {

        boolean succ = false;
        JSONObject json = t.json;
        JSONArray jsonArray = null;

        if (t.getParam("method").equals("listPostWithReply")) {

            if (json != null && "ok".equals(json.optString("status"))) {

                jsonArray = json.optJSONArray("results");
                succ = true;
            }

            if (succ) {
                commentsList = jsonArray;
                mainPostInfo = commentsList.optJSONObject(0);
                DiscussTopicDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        topic_content.setText(mainPostInfo.optString("content"));
                        topic_title.setText(mainPostInfo.optString("title"));
                        commentsListAdapter.setCommentList(commentsList);
                        commentsListAdapter.notifyDataSetChanged();
                    }
                });

            }

        } else if (t.getParam("method").equals("replyPost")) {

            //{"status":"ok","results":{"statue":0,"success":1,"error":0,"message":"成功","intExtra":0,"stringExtra":null}}
            if (json != null && "ok".equals(json.optString("status"))) {
                StrUtil.showMsg(DiscussTopicDetailActivity.this, "回复成功");
                DiscussTopicDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(DiscussTopicDetailActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        reply_content.setText("");
                    }
                });
            }
        }


    }

    //    "post_id"：帖子号(必须)
//    "page"：第几页[可选,缺省为1]
//            "page_size"：每页显示帖子数[可选，缺省为10]
    private void requestListPostWithReply(String post_id) {

        RequestHelper.Pdtask task = new RequestHelper.Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_DownJsonString, null, 0, true);
        task.addParam("method", "listPostWithReply");
        task.addParam("post_id", post_id);
        SOApplication.getDownLoadManager().startTask(task);

    }

    private void requestMoreListPostWithReply(String post_id, int page, int page_size) {

    }

    /**
     * "parent_post_id"：主贴号（必须）
     * "create_user_guid"：发帖人（必须）
     * "content"：内容（必须）
     */
    private void replyHost(String replyContent) {


        if (mainPostInfo != null) {

            RequestHelper.Pdtask task = new RequestHelper.Pdtask(this, this, Constants.SERVICE_HOST, null,
                    RequestHelper.Type_DownJsonString, null, 0, true);
            task.addParam("method", "replyPost");
            task.addParam("parent_post_id", mainPostInfo.optString("postId"));
            task.addParam("create_user_guid", UserMangerHelper.getDefaultUserGuid());
            task.addParam("content", replyContent);
            SOApplication.getDownLoadManager().startTask(task);

        }

    }

    private void init() {

        commentsList = new JSONArray();

        get_intent = this.getIntent();

        commentsListAdapter = new CommentsListAdapter();
        commentsListAdapter.setInflater((LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE));
        commentsListAdapter.setCommentList(commentsList);

        try {
            topic_info_jsonObject = new JSONObject(get_intent.getStringExtra("jsonObject"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        topic_owner = (TextView) this.findViewById(R.id.topic_owner);
        topic_content = (TextView) this.findViewById(R.id.topic_content);
        topic_title = (TextView) this.findViewById(R.id.topic_title);

        comment_list = (ListView) this.findViewById(R.id.comment_list);
        comment_list.setFocusable(true);
        comment_list.setAdapter(commentsListAdapter);

        View v = findViewById(R.id.left_backLayout);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        final TextView reply_count = (TextView) this.findViewById(R.id.reply_count);
        reply_content = (EditText) this.findViewById(R.id.reply_content);
        reply_content.setFocusableInTouchMode(true);


        reply_content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b == true) {
                    reply_count.setVisibility(View.GONE);
                } else {
                    reply_count.setVisibility(View.VISIBLE);
                }
            }
        });

        final TextView senter = (TextView) this.findViewById(R.id.sent_reply);
        senter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String replyContent = reply_content.getText().toString();
                if (replyContent.length() > 0) {
                    replyHost(replyContent);
                }
            }
        });

    }


}
