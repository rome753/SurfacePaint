package com.example.opengles3camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Utils {

    public static String loadAssets(String name) {
        String s = null;
        try {
            InputStream is = App.Companion.getApp().getAssets().open(name);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            s = new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static Bitmap loadImageAssets(String name) {
        try {
            InputStream is = App.Companion.getApp().getAssets().open(name);
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
