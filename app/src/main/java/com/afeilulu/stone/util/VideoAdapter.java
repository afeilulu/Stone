package com.afeilulu.stone.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afeilulu.stone.R;
import com.afeilulu.stone.model.Program;
import com.afeilulu.stone.model.SearchPage;
import com.afeilulu.stone.net.Constant;
import com.afeilulu.stone.net.IQSearch;
import com.afeilulu.stone.net.QueryFacade;
import com.androidquery.AQuery;

import java.lang.ref.WeakReference;
import java.util.List;

import xinli.moho.protocol.BoxSearch;

public class VideoAdapter extends BaseAdapter {
    private static final String TAG = LogUtil.makeLogTag(VideoAdapter.class);
    // 定义Context
    private Context mContext;
    private List<Program> mList;
    private int mFilterId;
    private QueryFacade qf;
    private IQSearch.Result searchRes = new IQSearch.Result() {

        @Override
        public void list(SearchPage res) {

        }

        @Override
        public void detail(SearchPage sp, WeakReference<ViewGroup> wr) {
            if (sp == null || wr == null)
                return;

            final ViewGroup vg = (ViewGroup) wr.get();
            TextView textView = (TextView) vg.findViewById(R.id.updateInfo);
            if (sp.getResult() != null && textView != null) {
                String menuId = textView.getTag().toString();
                String epCount = sp.getVIDAttribute(menuId, Constant.AttrType.atEpCount);
                String epTime = sp.getVIDAttribute(menuId, Constant.AttrType.atLastEpTime);
                String epTotal = sp.getVIDAttribute(menuId, Constant.AttrType.atEpTotal);
                final String HDFlag = sp.getVIDAttribute(menuId, Constant.AttrType.atHDFlag);

//                Log.e("", epTime + ":" + epCount + ":" + epTotal);

                String text = "";
                if (epCount != null && !"0".equals(epCount)) {
                    if (epTotal != null && !"0".equals(epTotal) && epCount.equals(epTotal)) {
                        text = "全" + epCount + "集";
                    } else {
                        text = "更新至" + epCount + "集";
                    }
                }
                if (epTime != null && !"0000-00-00".equals(epTime)) {
                    text = epTime + "期";
                }

                final String str = text;
                vg.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = (TextView) vg.findViewById(R.id.updateInfo);
                        if (!str.isEmpty()) {
                            textView.setText(str);
                            textView.setVisibility(View.VISIBLE);
                        } else
                            textView.setVisibility(View.GONE);
                    }
                });
            }
        }

    };

    public VideoAdapter(Context context, List<Program> list) {
        mContext = context;
        mList = list;
    }

    public VideoAdapter(Context context, List<Program> list, int filterId) {
        mContext = context;
        mList = list;
        mFilterId = filterId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.video_list_item, null);
            holder = new ViewHolder();
            holder.poster = (ImageView) convertView.findViewById(R.id.poster);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        Program c = mList.get(position);
        holder.title.setText(c.getName());

        AQuery aq = new AQuery(convertView);
        aq.id(holder.poster).image(c.getPoster(), true, true, 90, 0, null, AQuery.FADE_IN_NETWORK, 4.0f/3.0f);

//        if (mFilterId != 1) {
//            updateInfo.setTag(c.getId());
//            qf = QueryFacade.getInstance();
//            WeakReference<ViewGroup> weakReference = new WeakReference<ViewGroup>(vg);
//            qf.attribute(searchRes, 1, 1, c.getId(), weakReference);
//        }

        return convertView;
    }

    private class ViewHolder{
        TextView title;
        ImageView poster;
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
