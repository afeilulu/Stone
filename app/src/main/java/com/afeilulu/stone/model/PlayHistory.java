package com.afeilulu.stone.model;

import java.util.Date;

public class PlayHistory extends Program {
    private static final long serialVersionUID = 1L;
    private Date date;
    private String video;
    private int playtime;
    private int part;
    private String post;
    private String text;
    private int playHD;
    private String quality;

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public int getPlayHD() {
        return playHD;
    }

    public void setPlayHD(int playHD) {
        this.playHD = playHD;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public int getPlaytime() {
        return playtime;
    }

    public void setPlaytime(int playtime) {
        this.playtime = playtime;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
