package com.machines0008.camera.view;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.machines0008.camera.utils.GLES20Utils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Project Name: Camera
 * Created By: user
 * Created On: 2022/5/18
 * Usage:
 **/
public class CameraDrawer implements GLSurfaceView.Renderer {
    private final int[] texture = new int[1];
    private Callback callback;
    private static final String vertexShaderCode = "" +
            "attribute vec4 vPosition;" +
            "attribute vec2 vCoordinate;" +
            "uniform mat4 vMatrix;" +
            "uniform mat4 vCoordMatrix;" +
            "varying vec2 textureCoordinate;" +
            "void main() {" +
            "   gl_Position = vMatrix * vPosition;" +
            "   textureCoordinate = (vCoordMatrix * vec4(vCoordinate, 0, 1)).xy;" +
            "}";
    private static final String fragmentShaderCode = "" +
            "#extension GL_OES_EGL_image_external : require \r\n" +
            "precision mediump float;" +
            "uniform vec3 changeColor;" +
            "varying vec2 textureCoordinate;" +
            "uniform samplerExternalOES vTexture;" +
            "void main() {" +
            "   vec4 nColor = texture2D(vTexture, textureCoordinate);" +
            "   float c = nColor.r * changeColor.r + nColor.g * changeColor.g + nColor.b * changeColor.b;" +
            "   gl_FragColor = texture2D(vTexture, textureCoordinate);" +
            "}";
    private FloatBuffer fbVertex;
    private FloatBuffer fbFragment;
    private final float[] matrix = new float[16];
    private int width, height;
    private int dataWidth, dataHeight;
    private final KitkatCamera camera;
    private SurfaceTexture surfaceTexture;
    private int program;
    private int cameraId = 1;
    private final float[] vertexPosition = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f
    };
    private final float[] textureCoordinate = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    };

    public CameraDrawer() {
        camera = new KitkatCamera();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        createTextureId();
        camera.open(cameraId);
        dataWidth = camera.getPreviewPoint().x;
        dataHeight = camera.getPreviewPoint().y;
        surfaceTexture = new SurfaceTexture(texture[0]);
        camera.setPreviewTexture(surfaceTexture);
        camera.preview();

        program = GLES20Utils.createGlProgram(vertexShaderCode, fragmentShaderCode);
        fbVertex = GLES20Utils.genBuffer(vertexPosition);
        fbFragment = GLES20Utils.genBuffer(textureCoordinate);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
        calculateMatrix();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (null != surfaceTexture) {
            surfaceTexture.updateTexImage();
        }

        ByteBuffer mBuffer = ByteBuffer.allocate(width * height * 4);

        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(program);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        int mHTexture = GLES20.glGetUniformLocation(program, "vTexture");
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glUniform1i(mHTexture, 0);

        int mHPosition = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition, 2, GLES20.GL_FLOAT, false, 0, fbVertex);

        int mHCoord = GLES20.glGetAttribLocation(program, "vCoordinate");
        GLES20.glEnableVertexAttribArray(mHCoord);
        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, fbFragment);

        int glChangeColor = GLES20.glGetUniformLocation(program, "changeColor");

        int mHMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        GLES20.glUniformMatrix4fv(mHMatrix, 1, false, matrix, 0);

        int mHCoordMatrix = GLES20.glGetUniformLocation(program, "vCoordMatrix");
        GLES20.glUniformMatrix4fv(mHCoordMatrix, 1, false, new float[]{
                1, 0, 1, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        }, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoord);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mBuffer);
        if (callback != null) {
            callback.onCall(width, height, mBuffer);
        }
        GLES20.glDeleteTextures(1, texture, 0);
    }

    private void createTextureId() {
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    private void calculateMatrix() {
        float dataRatio = (float) dataWidth / dataHeight;
        float viewRatio = (float) width / height;
        float[] viewMatrix = new float[16];
        float[] projectionMatrix = new float[16];
        if (dataRatio > viewRatio) {
            Matrix.orthoM(projectionMatrix, 0, -viewRatio / dataRatio, viewRatio / dataRatio, -1, 1, 1, 3);
        } else {
            Matrix.orthoM(projectionMatrix, 0, -1, 1, -dataRatio / viewRatio, dataRatio / viewRatio, 1, 3);
        }
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(matrix, 0, projectionMatrix, 0, viewMatrix, 0);
        if (cameraId == 1) {
            Matrix.rotateM(matrix, 0, 90, 0, 0, 1);
        } else {
            Matrix.rotateM(matrix, 0, 270, 0, 0, 1);
        }
    }

    public void takePicture(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onCall(int width, int height, ByteBuffer data);
    }

}
