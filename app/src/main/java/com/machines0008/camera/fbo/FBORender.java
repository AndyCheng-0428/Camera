package com.machines0008.camera.fbo;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.machines0008.camera.utils.GLES20Utils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Project Name: Camera
 * Created By: user
 * Created On: 2022/5/19
 * Usage:
 **/
public class FBORender implements GLSurfaceView.Renderer {
    private int[] frame = new int[1];
    private int[] render = new int[1];
    private int[] texture = new int[2];
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

    private float[] matrix = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    private FloatBuffer vertexFloatBuffer;
    private FloatBuffer textureFloatBuffer;
    private ByteBuffer mBuffer;
    private int program;
    private Bitmap bitmap;
    private Callback mCallback;

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    private final String vertexShaderCode = "" +
            "attribute vec4 vPosition;" +
            "attribute vec2 vCoord;" +
            "uniform mat4 vMatrix;" +
            "varying vec2 textureCoordinate;" +
            "void main() {" +
            "   gl_Position = vMatrix * vPosition;" +
            "   textureCoordinate = vCoord;" +
            "}";

    private final String fragmentShaderCode = "" +
            "precision mediump float;" +
            "varying vec2 textureCoordinate;" +
            "uniform sampler2D vTexture;" +
            "void main() {" +
            "   vec4 color = texture2D(vTexture, textureCoordinate);" +
            "   float rgb = color.g;" +
            "   vec4 nColor = vec4(rgb, rgb, rgb, color.a);" +
            "   gl_FragColor = nColor;" +
            "}";

    private int mHMatrix;
    private int mHCoord;
    private int mHPosition;
    private int mHTexture;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        program = GLES20Utils.createGlProgram(vertexShaderCode, fragmentShaderCode);
        vertexFloatBuffer = GLES20Utils.genBuffer(vertexPosition);
        textureFloatBuffer = GLES20Utils.genBuffer(textureCoordinate);
        mHMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        mHCoord = GLES20.glGetAttribLocation(program, "vCoord");
        mHPosition = GLES20.glGetAttribLocation(program, "vPosition");
        mHTexture = GLES20.glGetUniformLocation(program, "vTexture");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        GLES20.glGenFramebuffers(1, frame, 0);
        GLES20.glGenRenderbuffers(1, render, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, render[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glGenTextures(2, texture, 0);
        for (int i = 0; i < 2; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[i]);
            if (i == 0) {
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap, 0);
            } else {
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            }
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }
        mBuffer = ByteBuffer.allocate(width * height * 4);
        // 綁定FrameBuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frame[0]);
        // 為FrameBuffer掛載texture[1]儲存顏色
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture[1], 0);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, render[0]);
        // 綁定FrameBuffer後的繪製，會繪製到texture[1]
        GLES20.glViewport(0, 0, width, height);

        GLES20.glClearColor(1, 1, 1, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(program);
        GLES20.glUniformMatrix4fv(mHMatrix, 1, false, matrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        GLES20.glUniform1i(mHTexture, 0);
        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition, 2, GLES20.GL_FLOAT, false, 0, vertexFloatBuffer);
        GLES20.glEnableVertexAttribArray(mHCoord);
        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, textureFloatBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoord);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mBuffer);
        if (mCallback != null) {
            mCallback.onCall(mBuffer);
        }
        GLES20.glDeleteTextures(2, texture, 0);
        GLES20.glDeleteRenderbuffers(1, render, 0);
        GLES20.glDeleteFramebuffers(1, frame, 0);
        bitmap.recycle();
    }

    public interface Callback {
        void onCall(ByteBuffer data);
    }
}
