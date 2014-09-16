package com.afeilulu.stone.net;

import android.view.ViewGroup;

import com.afeilulu.stone.StoneApp;
import com.androidquery.AQuery;

import java.lang.ref.WeakReference;

import com.afeilulu.stone.model.SearchPage;
import com.afeilulu.stone.net.impl.QSearch;
import com.afeilulu.stone.net.impl.QVideo;

public class QueryFacade {
    private static QueryFacade qf;
    private AQuery aq;

    private QueryFacade() {
        aq = new AQuery(StoneApp.getAppContext());
    }

    public static QueryFacade getInstance() {
        if (qf == null) {
            qf = new QueryFacade();
        }
        return qf;
    }

/*    public void channels(IQChannel.Result cr) {
        IQChannel channel = new QChannel(aq);
//        channel.list(cr, Constant.Att().ChannelsURL);
    }

    public void channel(IQChannel.Result cr) {
        IQChannel channel = new QChannel(aq);
//        channel.detail(cr, Constant.Att().ChannelURL);
    }

    public void videos(IQVideo.Result cr) {
        IQVideo video = new QVideo(aq);
//        video.list(cr, Constant.Att().VideosURL);
    }

    public void albumList(IQVideo.Result cr, String album) {
        IQVideo video = new QVideo(aq);
//        video.list(cr, Constant.Att().AlbumURL_1 + album + ".json");
    }
        public void classify(IQClasset.Result cr) {
        IQClasset classet = new QClasset(aq);
//        classet.detail(cr, Constant.Att().Classify1);
    }
    */

    public void video(IQVideo.Result cr, String vid) {
        IQVideo video = new QVideo(aq);
        video.detail(cr, Constant.Att().VideoURL, vid);
    }

    public void attribute(IQSearch.Result qr, int count, int index, String menuids, WeakReference<ViewGroup> weakReference) {
        IQSearch search = new QSearch(aq, weakReference);
        SearchPage req = new SearchPage();
        req.setAccount(Constant.Att().Account);
        req.setCount(count);
        req.setIndex(index);
        req.setPinyin(menuids);
        search.detail(qr, Constant.Att().SearchIP, req);
    }

    public void search(IQSearch.Result qr, int count, int index, String pinyi) {
        IQSearch search = new QSearch(aq, null);
        SearchPage req = new SearchPage();
        req.setAccount(Constant.Att().Account);
        req.setCount(count);
        req.setIndex(index);
        req.setPinyin(pinyi);
        search.list(qr, Constant.Att().SearchIP, req);
    }

    public void source(IQVideo.Result qr, int count, int index, long vid) {
        IQVideo video = new QVideo(aq);
        video.source(qr, Constant.Att().SearchIP, vid);
    }

    public void recommend(IQVideo.Result qr, int type, String name) {
        IQVideo video = new QVideo(aq);
        video.recommend(qr, Constant.Att().SearchIP, type, name);
    }
}
