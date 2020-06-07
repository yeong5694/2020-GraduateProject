package com.graduate.a2020_graduateproject.memo;

public class memoItem {

    private String id;
    private String content;
    private String thumnail;
    public String time;

    public memoItem(String id, String content, String thumnail, String time) {

        this.id = id;
        this.time = time;
        this.content = content;
        this.thumnail = thumnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setThumnail(String thumnail) {
        this.thumnail = thumnail;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public String getThumnail() {
        return thumnail;
    }

    public String getTime() {
        return time;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

