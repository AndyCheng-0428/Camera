package com.machines0008.camera.filter;

import android.graphics.Bitmap;

/**
 * Project Name: Camera
 * Created By: user
 * Created On: 2022/5/9
 * Usage: 水波紋濾鏡
 **/
public class WaterWaveFilter extends ImageFilter {

    public WaterWaveFilter(Bitmap bitmap) {
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
        return "precision mediump float;\n" +
                "varying vec2 aCoordinate;\n" +
                "uniform sampler2D vTexture;\n" +
                "uniform float u_time;\n" +
                "uniform float u_offset;\n" +
                "uniform float u_radio;\n" +
                "\n" +
                "#define MAX_RADIUS 1\n" +
                "#define DOUBLE_HASH 0\n" +
                "#define HASHSCALE1 .1031\n" +
                "#define HASHSCALE3 vec3 (.1031, .1030, .0973)\n" +
                "float hash12 (vec2 p) {\n" +
                "  vec3 p3 = fract (vec3 (p.xyx) * HASHSCALE1);\n" +
                "  p3 += dot (p3, p3.yzx + 19.19);\n" +
                "  return fract ((p3.x + p3.y) * p3.z);\n" +
                "}\n" +

                "vec2 hash22 (vec2 p) {\n" +
                "  vec3 p3 = fract (vec3 (p.xyx) * HASHSCALE3);\n" +
                "  p3 += dot (p3, p3.yzx + 19.19);\n" +
                "  return fract ((p3.xx + p3.yz) * p3.zy);\n" +
                "}\n" +
                "void main () {\n" +
                "  vec2 frag = aCoordinate;\n" +
                "  frag.x *= u_radio;\n" +
                "  frag = frag * u_offset * 1.5;\n" +
                "  vec2 p0 = floor (frag);\n" +
                "  vec2 circles = vec2 (0.);\n" +
                "  for (int j = -MAX_RADIUS; j <= MAX_RADIUS; ++j) {\n" +
                "    for (int i = -MAX_RADIUS; i <= MAX_RADIUS; ++i) {\n" +
                "      vec2 pi = p0 + vec2 (i, j);\n" +
                "      vec2 hsh = pi;\n" +
                "      vec2 p = pi + hash22(hsh) ;\n" +
                "      // hash12 添加隨機值\n" +
                "      float t = fract (0.3 * u_time + hash12(hsh));\n" +
                "      vec2 v = p - frag;\n" +
                "      // 半徑：\n" +
                "      float d = length (v) - (float (MAX_RADIUS) + 1. )*t  ;\n" +
                "      float h = 1e-3;\n" +
                "      float d1 = d - h;\n" +
                "      float d2 = d + h;\n" +
                "      float p1 = sin (31. * d1) * smoothstep (-0.6, -0.3, d1) * smoothstep (0., -0.3, d1);\n" +
                "      float p2 = sin (31. * d2) * smoothstep (-0.6, -0.3, d2) * smoothstep (0., -0.3, d2);\n" +
                "      circles += 0.5 * normalize (v) * ((p2 - p1) / (2. * h) * (1. - t) * (1. - t));\n" +
                "    }\n" +
                "  }\n" +
                "  // 兩輪循環取得weight個波(取平均)\n" +
                "  float weight = float ((MAX_RADIUS * 2 + 1) * (MAX_RADIUS * 2 + 1));\n" +
                "  circles /= weight;\n" +
                "  float intensity = mix (0.01, 0.05, smoothstep (0.1, 0.6, abs (fract (0.05 * u_time + .5) * 2. - 1.)));\n" +
                "  vec3 n = vec3 (circles, sin ( dot (circles, circles)));\n" +
                "  vec3 colorRipple = texture2D (vTexture, aCoordinate + intensity * n.xy).rgb;\n" +
                "  float colorGloss = 5. * pow (clamp (dot (n, normalize (vec3 (1., 0.7, 0.5))), 0., 1.), 6.);\n" +
                "  vec3 color = colorRipple + vec3(colorGloss);\n" +
                "  gl_FragColor = vec4 (color, 1.0);\n" +
                "}";
    }
}
