package com.machines0008.camera.filter;

import android.graphics.Bitmap;

/**
 * Project Name: Camera
 * Created By: user
 * Created On: 2022/5/11
 * Usage:
 **/
public class NormalFilter extends ImageFilter {
    public NormalFilter(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public String getVertexShaderCode() {
        return "attribute vec4 vPosition;" +
                "attribute vec2 vCoordinate;" +
                "uniform mat4 vMatrix;" +
                "varying vec2 aCoordinate;" +
                "void main() {" +
                "   gl_Position = vMatrix * vPosition;" +
                "   aCoordinate = vCoordinate;" +
                "}";
    }

    @Override
    public String getFragmentShaderCode() {
        return "precision mediump float;" +
                "uniform sampler2D vTexture;" +
                "varying vec2 aCoordinate;" +
                "void main() {" +
                "   gl_FragColor = texture2D(vTexture, aCoordinate);" +
                "}";
    }
}
