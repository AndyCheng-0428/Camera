package com.machines0008.camera.filter;

import android.graphics.Bitmap;

/**
 * Project Name: Camera
 * Created By: user
 * Created On: 2022/5/10
 * Usage: GlinnPhong反射
 **/
public class GlinnPhongFilter extends ImageFilter {

    public GlinnPhongFilter(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public String getVertexShaderCode() {
        return "attribute vec4 vPosition;" +
                "uniform mat4 vMatrix;" +
                "attribute vec2 vCoordinate;" +
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
                "   vec3 color = texture2D(vTexture, aCoordinate).rgb;" +
                "   vec3 n = vec3(1.0, 1.0, sin(4.));" +
                "   float colorGloss = 10.0 * pow(clamp(dot(n, normalize(vec3(0.05, 0.7, 0.5))), 0.6, 0.8), 6.9);" +
                "   gl_FragColor = vec4(color + vec3(colorGloss), 1.0);" +
                "}";
    }
}
