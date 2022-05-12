package com.machines0008.camera.utils.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Project Name: Camera
 * Created By: user
 * Created On: 2022/5/11
 * Usage:
 **/
public class ImageUtils {

    public static Bitmap toGrayScale(Bitmap source) {
        int height = source.getHeight();
        int width = source.getWidth();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0f);
        ColorMatrixColorFilter cmcf = new ColorMatrixColorFilter(colorMatrix); //顏色矩陣顏色濾鏡 通過一個顏色矩陣變換顏色，修改像素的飽和度
        paint.setColorFilter(cmcf);
        canvas.drawBitmap(source, 0, 0, paint); //利用原始圖片搭配畫筆及畫布，繪製在result上
        return result;
    }

    public static Bitmap compress(Bitmap source, int width, int height) {
        int bitmapWidth = source.getWidth();
        int bitmapHeight = source.getHeight();
        if (bitmapHeight < height && bitmapWidth < width) {
            return source;
        }
        float scaleWidth = (float) width / bitmapWidth;
        float scaleHeight = (float) height / bitmapHeight;
        float scale = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap result = Bitmap.createBitmap(source, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
        source.recycle();
        return result;
    }
}
