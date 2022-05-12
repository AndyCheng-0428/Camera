package com.machines0008.camera.utils.image;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Project Name: Camera
 * Created By: user
 * Created On: 2022/5/11
 * Usage: Sobel邊緣檢測法
 **/
public class SobelUtils {
    private static final int[] xArray = new int[]{
            1, 0, 1,
            -2, 0, 2,
            -1, 0, 1
    };

    private static final int[] yArray = new int[]{
            -1, -2, -1,
            0, 0, 0,
            1, 2, 1
    };

    public static Bitmap execute(Bitmap source) {
        return execute(source, 0);
    }

    /**
     * 執行Sobel邊緣檢測法
     * @param source 原始圖檔
     * @param threshold
     * @return 僅有黑白兩色經索伯算法之徒刑
     */
    public static Bitmap execute(Bitmap source, int threshold) {
        Bitmap grayBitmap = ImageUtils.toGrayScale(source); //先將圖片轉為灰階
        int width = grayBitmap.getWidth();
        int height = grayBitmap.getHeight();

        int scale = width * height;
        int[] mMap = new int[scale];
        double[] tMap = new double[scale];
        int[] cMap = new int[scale];

        grayBitmap.getPixels(mMap, 0, width, 0, 0, width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int index = y * width + x;
                double[] gArray = genGxGy(x, y, grayBitmap);
                double g = Math.sqrt(gArray[0] * gArray[0] + gArray[1] * gArray[1]); //將Gx分量與Gy分量結合為梯度G
                tMap[index] = g;
                cMap[index] = tMap[index] > threshold ? Color.BLACK : Color.WHITE;
            }
        }
        return Bitmap.createBitmap(cMap, width, height, Bitmap.Config.ARGB_8888);
    }

    /**
     * 取得Gx及Gy
     *
     * @param x      像素點x座標
     * @param y      像素點y座標
     * @param source 原始圖片
     * @return
     */
    private static double[] genGxGy(int x, int y, Bitmap source) {
        final double[] pixelArray = {
                getPixel(x - 1, y + 1, source), getPixel(x + 0, y + 1, source), getPixel(x + 1, y + 1, source),
                getPixel(x - 1, y + 0, source), getPixel(x + 0, y + 0, source), getPixel(x + 1, y + 0, source),
                getPixel(x - 1, y - 1, source), getPixel(x + 0, y - 1, source), getPixel(x + 1, y - 1, source)
        };
        double gx = 0;
        double gy = 0;
        for (int i = 0, size = pixelArray.length; i < size; i++) {
            gx += xArray[i] * pixelArray[i];
            gy += yArray[i] * pixelArray[i];
        }
        return new double[]{gx, gy};
    }

    private static double getPixel(int x, int y, Bitmap source) {
        if (x < 0 || y < 0) {
            return 0;
        }
        if (x >= source.getWidth() || y >= source.getHeight()) {
            return 0;
        }
        return source.getPixel(x, y);
    }
}
