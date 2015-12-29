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
public class ChildDiscussionListViewAdapter extends BaseAdapter {
    JSONArray source;
    LayoutInflater inflater;

    public int getLastItemIndex() {
        return getCount() + 1;
    }

    public ChildDiscussionListViewAdapter(JSONArray source, LayoutInflater inflater) {
        this.source = source;
        this.inflater = inflater;
    }

    public void setSource(JSONArray source) {
        this.source = source;
    }

    public void setInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    class Tag {
        TextView title;
        TextView content;
    }

    @Override
    public int getCount() {
        return source.length();
    }

    @Override
    public Object getItem(int i) {
        return source.optJSONObject(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Tag tag = null;
        if (view == null) {
            view = inflater.inflate(R.layout.new_topic_list_item, null);
            tag = new Tag();

            tag.content = (TextView) view.findViewById(R.id.topic_content);
            tag.title = (TextView) view.findViewById(R.id.topic_title);
            view.setTag(tag);
        } else {
            tag = (Tag) view.getTag();
        }

        JSONObject jsonObject = (JSONObject) getItem(i);

        tag.content.setText(jsonObject.optString("content"));
        tag.title.setText(jsonObject.optString("title"));
        return view;
    }
}
