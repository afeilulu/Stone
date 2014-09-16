package com.afeilulu.stone.net;

import com.afeilulu.stone.util.IPEntry;

public class Constant {
    private static Constant mConstant;
    public final String Account = "29_222222";
    public final String EPGServer = "epg.funhd.cn";
    public final String VideoURL = "http://" + EPGServer + "/epg/videos/";
    public final String EPGUrl = "http://" + EPGServer + "/epg/current/";
    public final String RecommendXmlUrl = EPGUrl + "channels-categories.xml";
    public final String EPGManagementServer = "http://" + EPGServer + "/management";
    public final String FilterJsonUrl = "http://" + EPGServer + "/searchrequest";
    public final String SearchServer = "search.funhd.cn";
    public final IPEntry SearchIP = new IPEntry("search", SearchServer, 5405);
    public final int FilterPort = 5252;
    public final String FragmentJsonUrl = "http://www.funhd.cn:8917/epg/commahomepage/default.json";

    private Constant() {
    }

    public static Constant Att() {
        if (mConstant == null) {
            mConstant = new Constant();
        }
        return mConstant;
    }

    public static class AttrType {
        public static final int atAreaName = 0x10001;
        public static final int atAreaID = 0x10002;
        public static final int atActorName = 0x10003;
        public static final int atActorID = 0x10004;
        public static final int atCategoryName = 0x10005;
        public static final int atCategoryID = 0x10006;
        public static final int atVideoName = 0x10007;
        public static final int atVideoID = 0x10008;
        public static final int atVideoSID = 0x10009;
        public static final int atFirstLetter = 0x1000a;
        public static final int atVideoXml = 0x1000b;
        public static final int atCount = 0x10000c;
        public static final int atStart = 0x10000d;
        public static final int atHDFlag = 0x20003;
        public static final int atMarkNum = 0x20004;
        public static final int atEpCount = 0x20005;
        public static final int atLastEpTime = 0x20006;
        public static final int atEpTotal = 0x20009;
    }

}
