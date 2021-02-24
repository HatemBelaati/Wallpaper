package com.hbapp.carsappwallpaper;

import java.io.Serializable;

public class WallpaperModel implements Serializable {
    private int id;
    private String originalUrl;
    private String mediumUrl;
    private String largeUrl;
    private String large2x;

    public String getLarge2x() {
        return large2x;
    }

    public void setLarge2x(String large2x) {
        this.large2x = large2x;
    }


    public String getLargeUrl() {
        return largeUrl;
    }

    public void setLargeUrl(String largeUrl) {
        this.largeUrl = largeUrl;
    }



    private boolean post_ads = false;

    public boolean isPost_ads() {
        return post_ads;
    }

    public void setPost_ads(boolean post_ads) {
        this.post_ads = post_ads;
    }


    public WallpaperModel() {
    }

    public WallpaperModel(int id, String originalUrl, String mediumUrl, String largeUrl, String large2x) {
        this.id = id;
        this.large2x = large2x;
        this.largeUrl = largeUrl;
        this.originalUrl = originalUrl;
        this.mediumUrl = mediumUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getMediumUrl() {
        return mediumUrl;
    }

    public void setMediumUrl(String mediumUrl) {
        this.mediumUrl = mediumUrl;
    }
}
