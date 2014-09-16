package com.afeilulu.stone.model;

import java.util.List;
import java.util.Map;

public class SearchPage {
    private String account;
    private String pinyin;
    private int index;
    private int count;

    private int resultleft;
    private List<Program> list;
    private Map<String, List<SearchAttribute>> result;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getResultleft() {
        return resultleft;
    }

    public void setResultleft(int resultleft) {
        this.resultleft = resultleft;
    }

    public List<Program> getList() {
        return list;
    }

    public void setList(List<Program> list) {
        this.list = list;
    }

    public Map<String, List<SearchAttribute>> getResult() {
        return result;
    }

    public void setResult(Map<String, List<SearchAttribute>> result) {
        this.result = result;
    }

    public String getVIDAttribute(String vid, int type) {
        if (vid == null) {
            return null;
        }
        if (result.size() <= 0) {
            return null;
        }
        List<SearchAttribute> list = result.get(vid);
        if (list == null) {
            return null;
        }
        for (SearchAttribute sa : list) {
            if (sa.getType() == type) {
                return sa.getValue();
            }
        }
        return null;
    }
}
