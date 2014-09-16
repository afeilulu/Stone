package com.afeilulu.stone.util;

public class SourceType {
    private static String name = "未知";

    public static String toName(String type) {
        if ("qiyi".equals(type)) {
            return "奇艺";
        } else if ("youku".equals(type)) {
            return "优酷";
        } else if ("ku6".equals(type)) {
            return "酷6";
        } else if ("tudou".equals(type)) {
            return "土豆";
        } else if ("sohu".equals(type)) {
            return "搜狐";
        } else if ("letv".equals(type)) {
            return "乐视";
        } else if ("qq".equals(type)) {
            return "腾讯";
        } else if ("pptv".equals(type)) {
            return "PPTV";
        } else if ("funshion".equals(type)) {
            return "风行";
        } else {
            return null;
        }
    }

}
