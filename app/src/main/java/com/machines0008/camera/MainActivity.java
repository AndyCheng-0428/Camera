package com.machines0008.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.machines0008.camera.filter.ImageFilter;
import com.machines0008.camera.filter.ScanRayFilter;
import com.machines0008.camera.filter.WaterWaveFilter;
import com.machines0008.camera.filter.WaveFilter;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Handler handler;
    private GLSurfaceView surfaceView;
    private SeekBar thresholdSeekBar;
    private SeekBar timeSeekBar;
    private ImageFilter renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        HandlerThread handlerThread = new HandlerThread("123456");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        surfaceView = findViewById(R.id.surfaceView);
        surfaceView.setEGLContextClientVersion(2);
        try {
            renderer = new ScanRayFilter(BitmapFactory.decodeStream(getAssets().open("texture/img_1.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        surfaceView.setRenderer(renderer);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        thresholdSeekBar = findViewById(R.id.thresholdSeekBar);
        thresholdSeekBar.setMax(50000000);
        thresholdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int seekBarValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int seekBarValue, boolean b) {
                this.seekBarValue = seekBarValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.post(() -> {
//                    renderer.updateThreshold(seekBarValue);
                    surfaceView.requestRender();
                });

            }
        });
        timeSeekBar = findViewById(R.id.timeSeekBar);
        timeSeekBar.setMax(10 * 1000);
        timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                renderer.updateTime(i);
                surfaceView.requestRender();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        surfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceView.onResume();
    }
}