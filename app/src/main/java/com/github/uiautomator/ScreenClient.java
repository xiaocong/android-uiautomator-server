package com.github.uiautomator;

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

        ScreenHttpServer server = new ScreenHttpServer(9010);
        try {
            server.initialize();
            server.start();

            while (server.isAlive()) {
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
            System.out.println("Server stopped");
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
