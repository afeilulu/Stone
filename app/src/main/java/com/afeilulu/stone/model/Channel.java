package com.afeilulu.stone.model;

/**
 * 频道 (电影、电视剧等)
 *
 * @author CHC
 */
public class Channel extends ProgramCollection {
    private String alias;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
