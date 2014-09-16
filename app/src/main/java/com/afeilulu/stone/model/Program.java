package com.afeilulu.stone.model;

import java.io.Serializable;
import java.util.List;

/**
 * 节目
 *
 * @author CHC
 */
public class Program implements Serializable {
    private static final long serialVersionUID = -4872709295748385896L;
    private String id;
    private String name;
    private String channel;
    private String category;
    private String year;
    private String area;
    private int total;
    private int current;
    private String present;
    /**
     * 海报
     */
    private String poster;
    private String description;
    private List<String> directors;
    private List<String> actors;
    private String score;
    private String present_time;
    private String premiere_date;
    private String present_number;
    private String region;
    private String quality;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public List<String> getDirectors() {
        return directors;
    }

    public void setDirectors(List<String> directors) {
        this.directors = directors;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPremiere_date() {
        return premiere_date;
    }

    public void setPremiere_date(String premiere_date) {
        this.premiere_date = premiere_date;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getPresent_time() {
        return present_time;
    }

    public void setPresent_time(String present_time) {
        this.present_time = present_time;
    }

    public String getPresent_number() {
        return present_number;
    }

    public void setPresent_number(String present_number) {
        this.present_number = present_number;
    }
}
