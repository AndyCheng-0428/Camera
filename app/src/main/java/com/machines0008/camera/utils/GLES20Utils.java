package com.machines0008.camera.utils;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Project Name: Camera
 * Created By: user
 * Created On: 2022/5/9
 * Usage:
 **/
public class GLES20Utils {
    private GLES20Utils() {}

    public static int genShader(int createShader, String shaderCode) {
        int shader = GLES20.glCreateShader(createShader);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public static int linkProgramShader(int vs, int fs) {
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vs);
        GLES20.glAttachShader(program, fs);
        GLES20.glLinkProgram(program);
        GLES20.glDeleteShader(vs);
        GLES20.glDeleteShader(fs);
        return program;
    }

    public static FloatBuffer genBuffer(float[] source) {
        ByteBuffer bb = ByteBuffer.allocateDirect(source.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(source);
        fb.position(0);
        return fb;
    }
}
