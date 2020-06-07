package com.graduate.a2020_graduateproject;

import com.google.firebase.database.Exclude;

public class Upload {

    private String imageUrl;
    private String key;

    public Upload(String imageUrl, String key) {
        this.imageUrl = imageUrl;
        this.key = key;
    }

    /*
    public Upload(String imageUri) {
        if(!imageTitle.trim().equals("")) {
            this.imageUri = imageUri;
        }
    }
    */
    public Upload(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
