package com.machines0008.camera.view;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

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

    /**
     * 開啟相機
     * @param cameraId 0:主鏡頭 1:前鏡頭
     */
    public void open(int cameraId) {
        camera = Camera.open(cameraId);
        if (null == camera) {
            return;
        }
        Camera.Parameters parameters = camera.getParameters();
        pictureSize = parameters.getPictureSize();
        previewSize = parameters.getPreviewSize();
        picturePoint = new Point(pictureSize.height, pictureSize.width);
        previewPoint = new Point(previewSize.height, previewSize.width);
    }

    /**
     * 相機預覽
     */
    public void preview() {
        if (null == camera) {
            return;
        }
        camera.startPreview();
        focus();
    }

    /**
     * 相機關閉
     */
    public void close() {
        if (null == camera) {
            return;
        }
        camera.stopPreview();
        camera.release();
    }

    /**
     * 鏡頭對焦
     */
    public void focus() {
        if (null == camera) {
            return;
        }
        camera.cancelAutoFocus();
        camera.autoFocus((success, camera) -> Log.i("Camera", success ? "對焦成功" : "對焦失敗"));
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
