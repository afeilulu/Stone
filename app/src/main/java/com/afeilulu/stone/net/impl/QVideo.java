package com.afeilulu.stone.net.impl;

import android.util.Log;

import com.afeilulu.stone.model.Episode;
import com.afeilulu.stone.model.Program;
import com.afeilulu.stone.model.SearchPage;
import com.afeilulu.stone.model.Source;
import com.afeilulu.stone.net.Constant;
import com.afeilulu.stone.net.IQVideo;
import com.afeilulu.stone.util.IPEntry;
import com.afeilulu.stone.util.SocketQuery;
import com.afeilulu.stone.util.SocketQuery.Query;
import com.afeilulu.stone.util.SourceHolder;
import com.afeilulu.stone.util.SourceType;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.XmlDom;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import xinli.moho.protocol.BoxSearch;

public class QVideo implements IQVideo {
    private static final String TAG = "QVideo";
    private AQuery aq;

    public QVideo(AQuery aq) {
        this.aq = aq;
    }

    @Override
    public void list(final Result res, String url) {
        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                List<Program> list = new ArrayList<Program>();
                try {
                    if (json != null) {
                        Log.i(TAG, "code:" + status.getCode());
                        JSONArray ja = json.getJSONArray("list");
                        if (ja != null) {
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject jo = ja.getJSONObject(i);
                                Program cha = new Program();
                                cha.setId(jo.getString("id"));
                                cha.setName(jo.getString("name"));
                                cha.setPoster(jo.getString("poster"));
                                list.add(cha);
                            }
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
    public void detail(final Result res, String url, final String vid) {
        final long start = System.currentTimeMillis();
        String uurl = url + vid + ".xml";
        AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>() {
            @Override
            public void callback(String url, XmlDom xml, AjaxStatus status) {
                Program cha = null;
                try {
                    long time = System.currentTimeMillis() - start;
                    if (time > 3000) {
                        Log.e(TAG, "http get data time=" + time);
                    }
                    if (xml != null) {
                        cha = new Program();
                        cha.setId(vid);
                        cha.setName(xml.attr("name"));
                        cha.setPoster(xml.attr("poster"));
                        cha.setArea(xml.attr("area"));
                        cha.setYear(xml.attr("premiereDate"));
                        cha.setScore(xml.attr("score"));
                        cha.setQuality(xml.attr("qualityLevel"));
                        cha.setDescription(xml.child("summer").text());
                        List<String> director = new ArrayList<String>();
                        List<String> actor = new ArrayList<String>();
                        List<XmlDom> cate = xml.children("category");
                        if (cate.size() > 0) {
                            XmlDom xd = cate.get(0);
                            List<XmlDom> sub = xd.children("subcategory");
                            cha.setChannel(xd.attr("name"));
                            if (sub.size() > 0) {
                                cha.setCategory(sub.get(0).text());
                            }
                        }
                        List<XmlDom> dic = xml.children("director");
                        List<XmlDom> act = xml.children("actor");
                        for (XmlDom x : dic) {
                            director.add(x.text());
                        }
                        for (XmlDom x : act) {
                            actor.add(x.text());
                        }
                        cha.setDirectors(director);
                        cha.setActors(actor);
                    } else {
                        Log.i(TAG, "Error:" + status.getCode());
                    }
                    SourceHolder.getInstance().setProgram(cha);
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
                res.detail(cha);
            }
        };
        cb.url(uurl).type(XmlDom.class).setTimeout(10000);
        aq.ajax(cb);
    }

    @Override
    public void source(final Result res, IPEntry ip, final long vid) {
        SocketQuery<SourceHolder> sq = new SocketQuery<SourceHolder>(ip,
                new Query<SourceHolder>() {
                    @Override
                    public SourceHolder process(DataInputStream in,
                                                DataOutputStream out) throws Exception {
                        BoxSearch.MVidSearch.Builder builder = BoxSearch.MVidSearch
                                .newBuilder();
                        // builder.set
                        builder.setAccount(Constant.Att().Account);
                        builder.setVideoid(vid);
                        builder.setCount(30);
                        builder.setIndex(0);
                        BoxSearch.MVidSearch pinyinSerch = builder.build();
                        out.writeInt(BoxSearch.SearchPacketType.MVidReq
                                .getNumber());
                        out.writeInt(pinyinSerch.getSerializedSize());// 传输序列化的字节长度
                        out.write(pinyinSerch.toByteArray());// 将发送内容转换为字节码数组
                        out.flush();// 清空缓存
                        int type = in.readInt();// 获取返回值的长度
                        int length = in.readInt();
                        byte dataini[] = new byte[length];// 设置长度
                        in.readFully(dataini);
                        in.close();
                        // 查询结果为空
                        if (type == BoxSearch.SearchPacketType.SearchErr
                                .getNumber()) {
                            BoxSearch.SearchErrPacket response = BoxSearch.SearchErrPacket
                                    .parseFrom(dataini);
                            SourceHolder resp = SourceHolder.getInstance();
                            resp.clearSources();
                            resp.setResultCode(response.getError());
                            resp.setResultMsg(response.getErrMsg());
                            return resp;
                        } else {
                            BoxSearch.KeywordSearchResp response = BoxSearch.KeywordSearchResp
                                    .parseFrom(dataini);// 将字节流转换成对象
                            if (response != null) {
                                SourceHolder resp = SourceHolder.getInstance();
                                resp.clearSources();
                                resp.setType(response.getChannelType());
                                // 获取关键字节点个数
                                for (int i = 0; i < response.getVideoCount(); i++) {
                                    BoxSearch.KeywordSearchResp.VideoInfo videoInfo = response
                                            .getVideo(i);
                                    Source ss = xml2Source(videoInfo.getValue());
                                    if (ss != null) {
                                        resp.addSources(ss);
                                    }
                                }
                                resp.sortEpisode();
                                return resp;
                            }
                        }
                        return null;
                    }

                    @Override
                    public void callback(SourceHolder e) {
                        res.source(e);
                    }

                }
        );
        sq.start();
    }

    public Source xml2Source(String xml) {
        try {
            Source s = new Source();
            s.setEpisodes(new ArrayList<Episode>());
            XmlDom xd = new XmlDom(xml);
            String sou = xd.attr("sourceType");
            XmlDom video = xd.child("video");
            List<XmlDom> list = video.children("episode");
            s.setAlias(sou);
            String name = SourceType.toName(s.getAlias());
            if (name == null) {
                return null;
            } else {
                s.setName(name);
                for (XmlDom i : list) {
                    Episode e = new Episode();
                    e.setName(i.attr("name"));
                    e.setUrl(i.attr("url"));
                    e.setvTitle(i.attr("vTitle"));
                    s.getEpisodes().add(e);
                }
                return s;
            }
        } catch (SAXException e) {
            Log.e(TAG, "size=", e);
            return null;
        }
    }

    @Override
    public void recommend(final Result res, IPEntry ip, final int type, final String name) {
        SocketQuery<SearchPage> sq = new SocketQuery<SearchPage>(ip,
                new Query<SearchPage>() {
                    @Override
                    public SearchPage process(DataInputStream in,
                                              DataOutputStream out) throws Exception {
                        BoxSearch.NameSearch.Builder builder = BoxSearch.NameSearch
                                .newBuilder();
                        // builder.set
                        builder.setAccount(Constant.Att().Account);
                        builder.setName(name);
                        builder.setCount(5);
                        builder.setIndex(0);
                        builder.setType(type);
                        BoxSearch.NameSearch pinyinSerch = builder.build();
                        out.writeInt(BoxSearch.SearchPacketType.NewNameReq
                                .getNumber());
                        out.writeInt(pinyinSerch.getSerializedSize());// 传输序列化的字节长度
                        out.write(pinyinSerch.toByteArray());// 将发送内容转换为字节码数组
                        out.flush();// 清空缓存
                        int type = in.readInt();// 获取返回值的长度
                        int length = in.readInt();
                        byte dataini[] = new byte[length];// 设置长度
                        in.readFully(dataini);
                        in.close();
                        // 查询结果为空
                        if (type == BoxSearch.SearchPacketType.SearchErr
                                .getNumber()) {
                            SearchPage resp = new SearchPage();
                            resp.setResultleft(0);// 获取搜索结果信息
                            resp.setPinyin(null);// 获取搜索拼音
                            resp.setList(new ArrayList<Program>());
                            return resp;
                        } else {
                            BoxSearch.NewNameSearchResp response = BoxSearch.NewNameSearchResp
                                    .parseFrom(dataini);// 将字节流转换成对象
                            if (response != null) {
                                SearchPage resp = new SearchPage();
                                // 获取关键字节点个数
                                resp.setResultleft(response.getResultLeft());// 获取搜索结果信息
                                resp.setPinyin(response.getValue());// 获取搜索拼音
                                resp.setList(new ArrayList<Program>());
                                for (int i = 0; i < response.getVideosCount(); i++) {
                                    BoxSearch.NewNameSearchResp.Video video = response
                                            .getVideos(i);
                                    Program p = new Program();
                                    p.setName(video.getVideoName());
                                    p.setPoster(video.getPoster());
                                    p.setScore(video.getScore() + "");
                                    p.setId(String.valueOf(video.getVid()));
                                    resp.getList().add(p);
                                }
                                return resp;
                            }
                        }
                        return null;
                    }

                    @Override
                    public void callback(SearchPage sp) {
                        res.recommend(sp);
                    }

                }
        );
        sq.start();
    }
}
