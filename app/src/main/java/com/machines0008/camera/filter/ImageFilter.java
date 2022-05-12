package com.machines0008.camera.filter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.machines0008.camera.application.ImageApplication;
import com.machines0008.camera.utils.GLES20Utils;

import java.io.IOException;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Project Name: Camera
 * Created By: user
 * Created On: 2022/5/9
 * Usage:
 **/
public abstract class ImageFilter implements GLSurfaceView.Renderer {

    private float[] vertexPosition = {
            -1.0f, 1.0f, //左上
            -1.0f, -1.0f, //左下
            1.0f, 1.0f, //右上
            1.0f, -1.0f //右下
    };

    private float[] textureCoordinate = {
            0.0f, 0.0f, //左下
            0.0f, 1.0f, //左上
            1.0f, 0.0f, //右下
            1.0f, 1.0f  //右上
    };

    private FloatBuffer vertexFloatBuffer;
    private FloatBuffer textureFloatBuffer;
    private int program;
    private Bitmap sourceImage;
    private Bitmap handleImage;
    private float[] mvpMatrix = new float[16];

    public ImageFilter(Bitmap bitmap) {
        sourceImage = bitmap;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        handleImage = sourceImage;
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        int vs = GLES20Utils.genShader(GLES20.GL_VERTEX_SHADER, getVertexShaderCode());
        int fs = GLES20Utils.genShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShaderCode());
        program = GLES20Utils.linkProgramShader(vs, fs);
        vertexFloatBuffer = GLES20Utils.genBuffer(vertexPosition);
        textureFloatBuffer = GLES20Utils.genBuffer(textureCoordinate);
        GLES20.glUseProgram(program);

        if (null != handleImage && !handleImage.isRecycled()) {
            int[] texture = new int[1];
            GLES20.glGenTextures(1, texture, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, handleImage, 0);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        int bitmapWidth = sourceImage.getWidth();
        int bitmapHeight = sourceImage.getHeight();

        float bitmapRatio = bitmapWidth / (float) bitmapHeight;
        float viewportRatio = width / (float) height;
        float[] projectMatrix = new float[16];
        float[] viewMatrix = new float[16];
        if (width > height) {
            if (bitmapRatio > viewportRatio) {
                Matrix.orthoM(projectMatrix, 0, -viewportRatio * bitmapRatio, viewportRatio * bitmapRatio, -1, 1, 3, 7);
            } else {
                Matrix.orthoM(projectMatrix, 0, -viewportRatio / bitmapRatio, viewportRatio / bitmapRatio, -1, 1, 3, 7);
            }
        } else {
            if (bitmapRatio > viewportRatio) {
                Matrix.orthoM(projectMatrix, 0, -1, 1, -1 / viewportRatio * bitmapRatio, 1 / viewportRatio * bitmapRatio, 3, 7);
            } else {
                Matrix.orthoM(projectMatrix, 0, -1, 1, -viewportRatio / bitmapRatio, viewportRatio / bitmapRatio, 3, 7);
            }
        }
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 5, 0, 0, 0, 0, 1.0f, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectMatrix, 0, viewMatrix, 0);
    }

    private float time;

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        int glHPosition = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(glHPosition);
        GLES20.glVertexAttribPointer(glHPosition, 2, GLES20.GL_FLOAT, false, 0, vertexFloatBuffer);

        int glHCoordinate = GLES20.glGetAttribLocation(program, "vCoordinate");
        GLES20.glEnableVertexAttribArray(glHCoordinate);
        GLES20.glVertexAttribPointer(glHCoordinate, 2, GLES20.GL_FLOAT, false, 0, textureFloatBuffer);

        int glHMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        GLES20.glUniformMatrix4fv(glHMatrix, 1, false, mvpMatrix, 0);

        int glHTexture = GLES20.glGetUniformLocation(program, "vTexture");
        GLES20.glUniform1i(glHTexture, 0);

        int vAmplitude = GLES20.glGetUniformLocation(program, "vAmplitude");
        float amplitude = 0.4f;
        GLES20.glUniform1f(vAmplitude, amplitude);

        int vDistance = GLES20.glGetUniformLocation(program, "vDistance");
        float distance = 1.28f;
        GLES20.glUniform1f(vDistance, distance);

        int vFrequency = GLES20.glGetUniformLocation(program, "vFrequency");
        float frequency = 0.73f;
        GLES20.glUniform1f(vFrequency, frequency);

        int lhRatio = GLES20.glGetUniformLocation(program, "lwRatio");
        float ratio = (float) sourceImage.getWidth() / sourceImage.getHeight();
        GLES20.glUniform1f(lhRatio, ratio);

        int vTime = GLES20.glGetUniformLocation(program, "u_time");
        time += 0.01;
        GLES20.glUniform1f(vTime, time);

        int vOffset = GLES20.glGetUniformLocation(program, "u_offset");
        float offset = 2.5f;
        GLES20.glUniform1f(vOffset, offset);

        int vRadio = GLES20.glGetUniformLocation(program, "u_radio");
        float radio = 0.6f;
        GLES20.glUniform1f(vRadio, radio);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    public abstract String getVertexShaderCode();

    public abstract String getFragmentShaderCode();

    public void updateTime(int ms) {
        time = (float) ms / 1000;
    }
}
