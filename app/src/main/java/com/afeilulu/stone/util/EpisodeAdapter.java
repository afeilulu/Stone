package com.afeilulu.stone.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afeilulu.stone.R;
import com.afeilulu.stone.model.Episode;

import java.util.ArrayList;

public class EpisodeAdapter extends BaseAdapter {
    private static final String TAG = LogUtil.makeLogTag(EpisodeAdapter.class);
    // 定义Context
    private Context mContext;
    private ArrayList<Episode> mList;

    public EpisodeAdapter(Context context, ArrayList<Episode> list) {
        mContext = context;
        mList = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.episode_list_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.episode_title);
            holder.subTitle = (TextView) convertView.findViewById(R.id.episode_sub_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Episode e = mList.get(position);
        holder.title.setText(e.getName());
        holder.subTitle.setText(e.getvTitle());

        return convertView;
    }

    private class ViewHolder {
        TextView title;
        TextView subTitle;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
