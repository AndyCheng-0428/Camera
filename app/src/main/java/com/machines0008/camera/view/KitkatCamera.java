package com.machines0008.camera.view;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Project Name: Camera
 * Created By: user
 * Created On: 2022/5/18
 * Usage:
 **/
public class KitkatCamera {
    private Camera camera;
    private Camera.Size pictureSize;
    private Camera.Size previewSize;
    private Point picturePoint;
    private Point previewPoint;

    public boolean open(int cameraId) {
        camera = Camera.open(cameraId);
        if (null == camera) {
            return false;
        }
        Camera.Parameters parameters = camera.getParameters();
        pictureSize = parameters.getPictureSize();
        previewSize = parameters.getPreviewSize();
        picturePoint = new Point(pictureSize.height, pictureSize.width);
        previewPoint = new Point(previewSize.height, previewSize.width);
        return true;
    }

    public void preview() {
        if (null == camera) {
            return;
        }
        camera.startPreview();
        focus();
    }

    public void close() {
        if (null == camera) {
            return;
        }
        camera.stopPreview();
        camera.release();
    }

    public void focus() {
        if (null == camera) {
            return;
        }
        camera.cancelAutoFocus();
        camera.autoFocus((success, camera) -> Log.i("Camera", success? "對焦成功": "對焦失敗"));
    }

    public void setPreviewTexture(SurfaceTexture surfaceTexture) {
        if (null == camera) {
            return;
        }
        try {
            camera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Point getPreviewPoint() {
        return previewPoint;
    }
}
