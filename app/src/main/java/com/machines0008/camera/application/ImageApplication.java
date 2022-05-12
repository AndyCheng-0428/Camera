package com.machines0008.camera.application;

import android.app.Application;

/**
 * Project Name: Camera
 * Created By: user
 * Created On: 2022/5/9
 * Usage:
 **/
public class ImageApplication extends Application {
    private static ImageApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


    public static ImageApplication getInstance () {
        return instance;
    }
}
