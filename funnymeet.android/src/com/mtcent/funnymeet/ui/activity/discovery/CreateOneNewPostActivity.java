package com.mtcent.funnymeet.ui.activity.discovery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class CreateOneNewPostActivity extends BaseActivity {

    Intent get_intent;
    JSONArray titleList;
    JSONObject jsonObject;
    String diszone_guid = null;
    String clubGuid = null;
    String board_guid = null;
    JSONObject board_selected_info = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_one_new_post);
        init();
    }

    private void init() {

        View v = findViewById(R.id.left_backLayout);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });


        get_intent = this.getIntent();
        if (get_intent != null) {

            try {
                titleList = new JSONArray(get_intent.getStringExtra("titleList"));
//                [{"guid":"86625db3-646f-4a89-a693-ab1777b48f01","clubGuid":
// "102bcc69-5c2a-4793-8e72-36156b3d9ab1","diszoneGuid":"85ce51ec-4237-4f67-9639-caf7b3ccd278",
// "name":"主板块","createUser":"","mainFlag":"Y","createDate":"2015-06-08"}]
                jsonObject = new JSONObject(get_intent.getStringExtra("jsonObject"));


//        {"guid":"85ce51ec-4237-4f67-9639-caf7b3ccd278","name":"归根结底很多好地方",
// "clubGuid":"102bcc69-5c2a-4793-8e72-36156b3d9ab1","status":"0",
// "creator":"","createDate":"2015-06-03","postCount":0}

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonObject != null) {
                diszone_guid = jsonObject.optString("guid");
                clubGuid = jsonObject.optString("clubGuid");
            }

            if (titleList == null) {
                titleList = new JSONArray();
            } else if (titleList.length() != 0) {
                board_selected_info = titleList.optJSONObject(0);//暂时先获取主板块
                if (board_selected_info != null) {
                    board_guid = board_selected_info.optString("guid");
                }
            }

        }

        final EditText post_content = (EditText) this.findViewById(R.id.content);
        final EditText post_title = (EditText) this.findViewById(R.id.post_title);

        post_content.requestFocus();


        final TextView finishbutton = (TextView) this.findViewById(R.id.finishbutton);
        finishbutton.setVisibility(View.VISIBLE);
        finishbutton.setText("发布");

        finishbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String content = post_content.getText().toString();
                final String title = post_title.getText().toString();
                if (content.length() > 0 && title.length() > 0) {

                    if (board_guid != null && clubGuid != null && diszone_guid != null) {
                        commitNewPost(title, content, board_guid, "");
                    } else {
                        StrUtil.showMsg(CreateOneNewPostActivity.this, "网络错误");
                    }
                } else {
                    if (content.length() > 0) {
                        StrUtil.showMsg(CreateOneNewPostActivity.this, "标题不能为空");
                    } else {
                        StrUtil.showMsg(CreateOneNewPostActivity.this, "内容不能为空");
                    }

                }

            }
        });

    }


    private void commitNewPost(String title, String content, String board_guid, String keywords) {


//        参数：
//        "app_id",：产品编号（必须 00:运营平台01:趣聚02:趣金融：注：这个可能会变更！）
//        "club_guid"：俱乐部（必须）
//        "diszone_guid"：讨论区（必须）
//        "board_guid"：板块（必须）
//        "parent_post_id"：主贴号，如果本帖是主贴，那么主帖号为0（必须）
//        "create_user_guid"：发帖人（必须）
//        "keywords"：关键字（必须，后续可以通过关键字查询，如果没有关键字，可以给出空串）
//        "title"：帖子标题，只有主贴才有标题[可选，如果是跟贴，帖子标题没有意义，可以设为空串或者不设置]
//        "content"：帖子内容，目前只支持文字（必须）


        RequestHelper.Pdtask task = new RequestHelper.Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);

        task.addParam("method", "createPost");
        task.addParam("club_guid", clubGuid);
        task.addParam("app_id", "02");
        task.addParam("diszone_guid", diszone_guid);
        task.addParam("board_guid", board_guid);
        task.addParam("parent_post_id", "0");
        task.addParam("create_user_guid", UserMangerHelper.getDefaultUserGuid());
        task.addParam("keywords", keywords);
        task.addParam("title", title);
//        task.addParam("update_user_guid", UserMangerHelper.getDefaultUserGuid());
        task.addParam("content", content);

        SOApplication.getDownLoadManager().startTask(task);
        showWait();
    }


    @Override
    public void onFinish(RequestHelper.Pdtask t) {

        boolean succ = false;
        JSONObject jsonObject = null;
        JSONObject json = t.json;

        if (json != null && "ok".equals(json.optString("status"))) {

            jsonObject = json.optJSONObject("results");
            if (jsonObject != null && "1".equals(jsonObject.optString("success"))) {
                succ = true;
            }
        }

        if (succ) {
            StrUtil.showMsg(CreateOneNewPostActivity.this, jsonObject.optString("message"));
            hideWait();
            setResult(DiscussTopicListActivity.CREATE_ONE_NEW_POST_REQUESTCODE);
            CreateOneNewPostActivity.this.finish();

        } else {
            StrUtil.showMsg(CreateOneNewPostActivity.this, "发布失败");
            hideWait();
        }


    }
}
