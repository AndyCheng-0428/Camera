package com.machines0008.camera.filter;

import android.graphics.Bitmap;

/**
 * Project Name: Camera
 * Created By: user
 * Created On: 2022/5/10
 * Usage: 漣漪濾鏡
 **/
public class WaveFilter extends ImageFilter {

    public WaveFilter(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public String getVertexShaderCode() {
        return "" +
                "attribute vec4 vPosition;" +
                "uniform mat4 vMatrix;" +
                "varying vec2 aCoordinate;" +
                "attribute vec2 vCoordinate;" +
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
                "uniform float u_offset;" +
                "uniform float u_time;" +
                "uniform float u_radio;" +
                "uniform float lwRatio;" +
                "void main() {" +
                "   float offset = 0.05 * sin(92.5 * length(aCoordinate - vec2(0.5)) - u_time);" +
                "   vec3 color = texture2D(vTexture, aCoordinate + vec2(offset, offset / lwRatio)).rgb;" +
                "   gl_FragColor = vec4(color, 1.0);" +
                "}";
    }
}
