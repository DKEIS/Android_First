package com.example.hyojong.dkeis.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Item {
    int image;
    String title;
    String imgae_url;

    int getImage() {
        return this.image;
    }
    String getTitle() {
        return this.title;
    }
    String getImgae_url() {
        return this.imgae_url;
    }

    Item(int image, String title, String url) {
        this.image = image;
        this.title = title;
        this.imgae_url = url;
    }
}

