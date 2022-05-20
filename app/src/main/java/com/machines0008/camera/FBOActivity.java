package com.machines0008.camera;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.machines0008.camera.fbo.FBORender;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Project Name: Camera
 * Created By: user
 * Created On: 2022/5/20
 * Usage:
 **/
public class FBOActivity extends AppCompatActivity implements FBORender.Callback {

    private FBORender mRender;
    private ImageView mImage;
    private GLSurfaceView mGLView;

    private int mBmpWidth, mBmpHeight;
    private String mImgPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbo);
        mGLView = (GLSurfaceView) findViewById(R.id.mGLView);
        mGLView.setEGLContextClientVersion(2);
        mRender = new FBORender();
        mRender.setCallback(this);
        mGLView.setRenderer(mRender);
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mImage = (ImageView) findViewById(R.id.mImage);
    }

    public void onClick(View view) {
        //调用相册
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            mImgPath = c.getString(columnIndex);
            Log.e("wuwang", "img->" + mImgPath);
            Bitmap bmp = BitmapFactory.decodeFile(mImgPath);
            mBmpWidth = bmp.getWidth();
            mBmpHeight = bmp.getHeight();
            mRender.setBitmap(bmp);
            mGLView.requestRender();
            c.close();
        }
    }

    @Override
    public void onCall(final ByteBuffer data) {
        new Thread(() -> {
            Log.e("wuwang", "callback success");
            Bitmap bitmap = Bitmap.createBitmap(mBmpWidth, mBmpHeight, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(data);
            saveBitmap(bitmap);
            data.clear();
        }).start();
    }

    //图片保存
    public void saveBitmap(final Bitmap b) {
        String path = mImgPath.substring(0, mImgPath.lastIndexOf("/") + 1);
        File folder = new File(path);
        if (!folder.exists() && !folder.mkdirs()) {
            runOnUiThread(() -> Toast.makeText(FBOActivity.this, "无法保存照片", Toast.LENGTH_SHORT).show());
            return;
        }
        long dataTake = System.currentTimeMillis();
        final String jpegName = path + dataTake + ".jpg";
        try (FileOutputStream fout = new FileOutputStream(jpegName);
             BufferedOutputStream bos = new BufferedOutputStream(fout)) {
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        runOnUiThread(() -> {
            Toast.makeText(FBOActivity.this, "保存成功->" + jpegName, Toast.LENGTH_SHORT).show();
            mImage.setImageBitmap(b);
        });
    }
}
