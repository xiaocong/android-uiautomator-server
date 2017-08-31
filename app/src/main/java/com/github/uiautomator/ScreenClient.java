package com.github.uiautomator;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by hzsunshx on 2017/8/30.
 */

public class ScreenClient {
    private static final String PROCESS_NAME = "screen.cli";
    private static final String VERSION = "1.0";

    public static void main(String[] args) {
        setArgV0(PROCESS_NAME);

        // Requires SDK >= 18
        Method injector = null;
        try {
            injector = Class.forName("android.view.SurfaceControl").getDeclaredMethod("screenshot", new Class[]{Integer.TYPE, Integer.TYPE});
            Bitmap bmp = (Bitmap) injector.invoke(null, new Object[]{Integer.valueOf(0), Integer.valueOf(0)});

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 75, bout);
            System.out.println("screenshot success " + bmp.getHeight() + ", " + bmp.getWidth());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }

    private static void setArgV0(String text) {
        try {
            Method setter = android.os.Process.class.getMethod("setArgV0", String.class);
            setter.invoke(android.os.Process.class, text);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
