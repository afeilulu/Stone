package com.afeilulu.stone.net.impl;

import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.afeilulu.stone.model.Channel;
import com.afeilulu.stone.net.IQChannel;

public class QChannel implements IQChannel {
    private static final String TAG = "QChannel";
    private AQuery aq;

    public QChannel(AQuery aq) {
        this.aq = aq;
    }

    @Override
    public void list(final Result res, String url) {
        aq.ajax(url, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                List<Channel> list = new ArrayList<Channel>();
                try {
                    if (json != null) {
                        Log.i(TAG, "code:" + status.getCode());
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject jo = json.getJSONObject(i);
                            Channel cha = new Channel();
                            cha.setName(jo.getString("name"));
                            cha.setBg(jo.getString("url"));
                            list.add(cha);
                        }
                    } else {
                        Log.i(TAG, "Error:" + status.getCode());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
                res.list(list);
            }
        });
    }

    @Override
    public void detail(final Result res, String url) {
        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                Channel cha = null;
                try {
                    if (json != null) {
                        Log.i(TAG, "code:" + status.getCode());
                        cha = new Channel();
                        cha.setName(json.getString("name"));
                        cha.setBg(json.getString("url"));
                    } else {
                        Log.i(TAG, "Error:" + status.getCode());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
                res.detail(cha);
            }
        });
    }

}
