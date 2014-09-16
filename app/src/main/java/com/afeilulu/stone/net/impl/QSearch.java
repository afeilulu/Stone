package com.afeilulu.stone.net.impl;

import android.util.Log;
import android.view.ViewGroup;

import com.afeilulu.stone.model.Program;
import com.afeilulu.stone.model.SearchAttribute;
import com.afeilulu.stone.model.SearchPage;
import com.afeilulu.stone.net.Constant;
import com.afeilulu.stone.net.IQSearch;
import com.afeilulu.stone.util.IPEntry;
import com.afeilulu.stone.util.SocketQuery;
import com.afeilulu.stone.util.SocketQuery.Query;
import com.androidquery.AQuery;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import xinli.moho.protocol.BoxSearch;
import xinli.moho.protocol.BoxSearch.PinyinSearchWithSDPResp;

public class QSearch implements IQSearch {
    private static final String TAG = "QSearch";
    private AQuery aq;
    private WeakReference<ViewGroup> weakReference;

    public QSearch(AQuery aq, WeakReference<ViewGroup> weakReference) {
        this.aq = aq;
        this.weakReference = weakReference;
    }

    @Override
    public void list(final Result res, IPEntry ip, final SearchPage req) {
        SocketQuery<SearchPage> sq = new SocketQuery<SearchPage>(ip,
                new Query<SearchPage>() {
                    @Override
                    public SearchPage process(DataInputStream in,
                                              DataOutputStream out) throws Exception {
                        BoxSearch.PinyinSearchWithSDP.Builder builder = BoxSearch.PinyinSearchWithSDP
                                .newBuilder();
                        // builder.set
                        builder.setAccount(req.getAccount());
                        builder.setIndex(req.getIndex());
                        builder.setCount(req.getCount());
                        builder.setPinyin(req.getPinyin());
                        BoxSearch.PinyinSearchWithSDP pinyinSerch = builder
                                .build();
                        /**
                         * 如果 是英文 则使用 英文类型
                         */
                        out.writeInt(BoxSearch.SearchPacketType.PinyinReqWithSDP
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
                            // resp.setKeywordsNode(0);// 获取关键字节点个数
                            resp.setResultleft(0);// 获取搜索结果信息
                            resp.setPinyin(req.getPinyin());// 获取搜索拼音
                            resp.setList(new ArrayList<Program>());
                            return resp;
                        } else {
                            BoxSearch.PinyinSearchWithSDPResp response = BoxSearch.PinyinSearchWithSDPResp
                                    .parseFrom(dataini);// 将字节流转换成对象
                            if (response != null) {
                                SearchPage resp = new SearchPage();
                                // resp.setKeywordsNode(response.getNodeCount());//
                                // 获取关键字节点个数
                                resp.setResultleft(response.getResultLeft());// 获取搜索结果信息
                                resp.setPinyin(req.getPinyin());// 获取搜索拼音
                                resp.setList(new ArrayList<Program>());
                                for (int i = 0; i < response.getVideosCount(); i++) {
                                    PinyinSearchWithSDPResp.Video video = response
                                            .getVideos(i);
                                    Program p = new Program();
                                    p.setId("" + video.getVid());
                                    p.setName(video.getVideoName());
                                    p.setPoster(video.getPoster());
                                    p.setScore(video.getScore() + "");
                                    p.setChannel(video.getChannelType() + "");
                                    p.setQuality(video.getDefinition() + "");
                                    resp.getList().add(p);
                                }
                                Log.i(TAG, "size=" + resp.getList().size());
                                return resp;
                            }
                        }
                        return null;
                    }

                    @Override
                    public void callback(SearchPage e) {
                        res.list(e);
                    }

                }
        );
        sq.start();
    }

    @Override
    public void detail(final Result res, IPEntry ip, final SearchPage req) {
        SocketQuery<SearchPage> sq = new SocketQuery<SearchPage>(ip,
                new Query<SearchPage>() {
                    @Override
                    public SearchPage process(DataInputStream in,
                                              DataOutputStream out) throws Exception {
                        BoxSearch.SearchReq.Builder builder = BoxSearch.SearchReq
                                .newBuilder();
                        // builder.set
                        builder.setAccount(req.getAccount());
                        builder.setStart(req.getIndex());
                        builder.setCount(req.getCount());
                        builder.setSearchType(0x20001);// 固定类型
                        String[] menuid = req.getPinyin().split(",");
                        for (String item : menuid) {
                            BoxSearch.Attribute.Builder att = BoxSearch.Attribute
                                    .newBuilder();
                            att.setType(0x10009);// 固定数值
                            att.setValue(item);
                            builder.addFilter(att);
                        }
                        BoxSearch.SearchReq searchReq = builder.build();
                        out.writeInt(BoxSearch.SearchPacketType.NormalReq.getNumber());
                        out.writeInt(searchReq.getSerializedSize());// 传输序列化的字节长度
                        out.write(searchReq.toByteArray());// 将发送内容转换为字节码数组
                        out.flush();// 清空缓存
                        int type = in.readInt();// 获取返回值的长度
                        int length = in.readInt();
                        byte dataini[] = new byte[length];// 设置长度
                        in.readFully(dataini);
                        if (type == BoxSearch.SearchPacketType.SearchErr.getNumber()) {
                            BoxSearch.SearchErrPacket response = BoxSearch.SearchErrPacket
                                    .parseFrom(dataini);
                            Log.i(TAG, "response=" + response.getErrMsg());
                        } else {
                            BoxSearch.SearchResp response = BoxSearch.SearchResp
                                    .parseFrom(dataini);
                            if (response != null) {
                                SearchPage search = new SearchPage();
                                search.setResultleft(response.getResultLeft());
                                search.setResult(new HashMap<String, List<SearchAttribute>>());
                                for (int i = 0; i < response.getResultCount(); i++) {
                                    BoxSearch.SearchResult result = response
                                            .getResult(i);
                                    List<SearchAttribute> list = new ArrayList<SearchAttribute>();
                                    for (int j = 0; j < result.getAttrCount(); j++) {
                                        BoxSearch.Attribute attribute = result
                                                .getAttr(j);
                                        SearchAttribute sa = new SearchAttribute();
                                        sa.setType(attribute.getType());
                                        sa.setValue(attribute.getValue());
                                        list.add(sa);
                                    }
                                    for (SearchAttribute sa : list) {
                                        if (sa.getType() == Constant.AttrType.atVideoID) {
                                            search.getResult().put(sa.getValue(), list);
                                        }
                                    }
                                }
                                return search;
                            }
                        }
                        return null;
                    }

                    @Override
                    public void callback(SearchPage e) {
                        res.detail(e, weakReference);
                    }
                }
        );
        sq.start();
    }
}
