package com.machines0008.camera.filter;

import android.graphics.Bitmap;

/**
 * Project Name: Camera
 * Created By: user
 * Created On: 2022/5/9
 * Usage: 掃描射線
 **/
public class ScanRayFilter extends ImageFilter {

    public ScanRayFilter(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public String getVertexShaderCode() {
        return "attribute vec4 vPosition;" +
                "attribute vec2 vCoordinate;" +
                "uniform mat4 vMatrix;" + //變換矩陣
                "varying vec2 aCoordinate;" +
                "void main() {" +
                "   gl_Position = vMatrix * vPosition;" +
                "   aCoordinate = vCoordinate;" +
                "}";
    }

    @Override
    public String getFragmentShaderCode() {
        return "" +
                "precision mediump float;" +
                "uniform sampler2D vTexture;" +
                "uniform float vAmplitude;" +
                "uniform float vFrequency;" +
                "uniform float vDistance;" +
                "uniform float u_time;" +
                "varying vec2 aCoordinate;" +
                "float hash21(float x, float y) {" +
                "   return fract(80.5453 * sin(dot(vec2(x, y), vec2(122316.9898, 182.233))));" +
                "}" +
                "void main() {" +
                "   float x = aCoordinate.x;" +
                "   float y = aCoordinate.y;" +
                "   float jitter = hash21(y, u_time) * 2. - 1.;" +
                "   jitter *= step(1.0, abs(jitter));" +
                "   jitter = 2.*hash21(jitter, 0.1 *y);" +
                "   jitter *= step(0.9, abs(jitter));" +
                "   float scanDrift = sin(u_time * 666. + vAmplitude) * vDistance * .04;" +
                "   vec4 color1 = texture2D(vTexture, vec2(x + .05 * fract(u_time) * jitter + 0.5 *scanDrift, y));" +
                "   vec4 color2 = texture2D(vTexture, vec2(x - .05 * fract(u_time) * jitter, y));" +
                "   gl_FragColor = vec4(color1.r, color2.g, color1.b, 1.0);" +
                "}";
    }
}
