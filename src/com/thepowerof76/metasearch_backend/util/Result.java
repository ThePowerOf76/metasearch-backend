package com.thepowerof76.metasearch_backend.util;

import java.io.Serializable;

public class Result implements Serializable {
    private final String title;
    private final String href;
    private String desc;

    private final int engineID;
    /*
     * 1- Google
     * 2- Bing
     * 3- DuckDuckGo
     * 4- Archive.org
     * 5- Base Search
     * 6- Wiby
     */
    public String getTitle() {
        return title;
    }
    public String getHref() {
        return href;
    }
    public String getDesc() {
        return desc;
    }
    public int getEngineID() {
        return engineID;
    }
    public void setDesc(String d) {
        desc = d;
    }
    public Result(String t, String h, String d, int id) {
        title = t;
        href = h;
        desc = d;
        engineID = id;
    }
}
