package com.machines0008.camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.machines0008.camera.view.CameraView;

import java.io.IOException;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private CameraView cameraView;
    private ImageView imageView;
    private FloatingActionButton fabCamera;
    private static final int CAMERA_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            return;
        }
        setContentView(R.layout.activity_camera);
        initView();
        initListener();
    }

    private void initListener() {
        fabCamera.setOnClickListener(v -> {
            cameraView.takePicture(() -> {

            }, (data, camera) -> {

            }, (data, camera) -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix rotateMatrix = new Matrix();
                rotateMatrix.postRotate(-90.0f);
                imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, true));
                bitmap.recycle();
            });

        });
    }

    private void initView() {
        cameraView = findViewById(R.id.cameraView);
        fabCamera = findViewById(R.id.fabCamera);
        imageView = findViewById(R.id.imageView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.onResume();
    }
}