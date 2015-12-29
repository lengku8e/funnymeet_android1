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
public class DiscussListAdapter extends BaseAdapter {
    LayoutInflater inflater;
    JSONArray listUserDiscussionZoneSource;


    class Tag {
        TextView groupName;
        TextView topicNum;
    }

    public void setInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void setSource(JSONArray source) {
        this.listUserDiscussionZoneSource = source;
    }

    @Override
    public int getCount() {

        return listUserDiscussionZoneSource.length();
    }

    @Override
    public Object getItem(int i) {
        return listUserDiscussionZoneSource.optJSONObject(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Tag tag = null;
        if (view == null) {
            tag = new Tag();
            view = inflater.inflate(R.layout.new_discuss_group_list_item, null);

            tag.groupName = (TextView) view.findViewById(R.id.discuss_group_name);
            tag.topicNum = (TextView) view.findViewById(R.id.discuss_topics_number);
            view.setTag(tag);
        } else {
            tag = (Tag) view.getTag();
        }

        JSONObject discussGropInfo = (JSONObject) getItem(i);

        tag.groupName.setText(discussGropInfo.optString("name"));
//                tag.topicNum.setText(String.valueOf(fdg.getGroupTopicNumber()));

        return view;
    }
}
