package com.machines0008.camera.view;

import android.Manifest;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import androidx.annotation.RequiresPermission;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Project Name: Camera
 * Created By: user
 * Created On: 2022/5/18
 * Usage: 使用本類別須有相機權限
 **/

public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private CameraDrawer cameraDrawer;

    @RequiresPermission(value = Manifest.permission.CAMERA)
    public CameraView(Context context) {
        super(context);
        init();
    }

    @RequiresPermission(value = Manifest.permission.CAMERA)
    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        cameraDrawer = new CameraDrawer();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        cameraDrawer.onSurfaceCreated(gl, config);
        cameraDrawer.getSurfaceTexture().setOnFrameAvailableListener((listener) -> requestRender());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        cameraDrawer.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        cameraDrawer.onDrawFrame(gl);
    }

    public void takePicture(CameraDrawer.Callback callback) {
        cameraDrawer.takePicture(callback);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraDrawer.onResume();
    }

    public void onDestroyed() {
        cameraDrawer.onDestroyed();
    }
}
