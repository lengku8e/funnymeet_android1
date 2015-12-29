package com.mtcent.funnymeet.ui.activity.discovery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import mtcent.funnymeet.R;

/**
 * Created by Administrator on 2015/8/15.
 */
public class CommentsListAdapter extends BaseAdapter {
    LayoutInflater inflater;
    JSONArray commentList;

    public void setInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void setCommentList(JSONArray commentList) {
        this.commentList = commentList;
    }

    @Override
    public int getCount() {
        return commentList.length() - 1;
    }

    @Override
    public Object getItem(int i) {
        return commentList.optJSONObject(i + 1);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        class ViewHolder {
            TextView reply_user_nickname;
            TextView reply_floor;
            TextView reply_date;
            TextView reply_content;
        }

        ViewHolder viewHolder = null;

        if (view == null) {

            viewHolder = new ViewHolder();

            view = inflater.inflate(R.layout.new_comment_list_item, null);
            viewHolder.reply_content = (TextView) view.findViewById(R.id.reply_content);
            viewHolder.reply_floor = (TextView) view.findViewById(R.id.reply_floor);
            viewHolder.reply_date = (TextView) view.findViewById(R.id.reply_date);
            viewHolder.reply_user_nickname = (TextView) view.findViewById(R.id.reply_user_nickname);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        JSONObject jsonObject = (JSONObject) getItem(i);

        int replyParentPostId = Integer.valueOf(jsonObject.optString("parentPostId"));
        int currentReplyPostId = Integer.valueOf(jsonObject.optString("postId"));
        int floor = currentReplyPostId - replyParentPostId;
        viewHolder.reply_content.setText(jsonObject.optString("content"));
        viewHolder.reply_date.setText(jsonObject.optString("createTime"));
        viewHolder.reply_user_nickname.setText("昵称");
        if (floor > 0) {
            viewHolder.reply_floor.setText("第" + floor + "楼");
        } else {
            viewHolder.reply_floor.setText("数据异常");
        }

        return view;
    }
}
