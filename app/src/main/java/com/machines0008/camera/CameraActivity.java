package com.machines0008.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.machines0008.camera.view.CameraView;

public class CameraActivity extends AppCompatActivity {
    private CameraView cameraView;
    private ImageView imageView;
    private FloatingActionButton fabCamera;
    private static final int CAMERA_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            return;
        }
        setContentView(R.layout.activity_camera);
        initView();
        initListener();
    }

    private void initListener() {
        fabCamera.setOnClickListener(v -> cameraView.takePicture((width, height, data) -> runOnUiThread(() -> {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(data);
            Matrix matrix = new Matrix();
            matrix.setScale(1, -1); //垂直翻轉
            Bitmap adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            imageView.setImageBitmap(adjustedBitmap);
            data.clear();
        })));
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
    protected void onDestroy() {
        super.onDestroy();
        cameraView.onDestroyed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.onResume();
    }
}