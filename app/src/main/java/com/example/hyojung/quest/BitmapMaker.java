package com.example.hyojung.quest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class BitmapMaker {

    public static Bitmap resizeForProfile(Bitmap bitmap) {
        int imageWidth = bitmap.getWidth(), imageHeight = bitmap.getHeight();
        int maxLength = Math.max(imageWidth, imageHeight), minLength = Math.min(imageWidth, imageHeight);
        int startPosition = (maxLength - minLength) / 2;
        Bitmap afterBitmap = Bitmap.createBitmap(bitmap,
                imageWidth > imageHeight ? startPosition : 0,
                imageHeight > imageWidth ? startPosition : 0,
                minLength, minLength);
        return afterBitmap;
    }

    public static byte[] BitmapToByteArray(Bitmap bitmap) {
        Bitmap tempBitmap = bitmap.copy(bitmap.getConfig(), true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        tempBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap byteArrayToBitmap(byte[] imageByteArray) {
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
    }
}
