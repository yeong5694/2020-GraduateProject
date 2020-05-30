package com.graduate.a2020_graduateproject;

public class Upload {

    private String imageUrl;

    public Upload() {
        // empty constructor needed
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

}
