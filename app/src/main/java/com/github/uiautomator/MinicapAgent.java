package com.github.uiautomator;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

import com.github.uiautomator.compat.WindowManagerWrapper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class MinicapAgent extends Thread {
    private static final String PROCESS_NAME = "minicap.cli";
    private static final String VERSION = "1.0";
    private static final String DEFAULT_SOCKET_NAME = "minicapagent";
    private static final String TAG = "minicap";

    final WindowManagerWrapper windowManager = new WindowManagerWrapper();
    private int width;
    private int height;
    private int rotation;
    private String socketName;


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

    public MinicapAgent(int width, int height, String socketName) {
        this.width = width;
        this.height = height;
        this.rotation = windowManager.getRotation();
        this.socketName = socketName;
        windowManager.watchRotation(r -> {
            rotation = r;
            System.out.println("Rotation:" + r);
        });
    }

    private void manageClientConnection(LocalServerSocket serverSocket) {
        while (true) {
            System.out.printf("Listening on localabstract:%s\n", socketName);

            // python3 -m adbutils.pidcat -t scrcpy
            Log.i(TAG, String.format("Listening on %s", socketName));
            try (LocalSocket socket = serverSocket.accept()) {
                Log.d(TAG, "client connected");

                OutputStream output = socket.getOutputStream();
                rotation = windowManager.getRotation();
                Point size = windowManager.getDisplaySize();
                System.out.println("Display: " + size.x + "x" + size.y);
                System.out.println("Rotation: " + rotation);
                try {
                    writeBanner(output);
                    pipeImages(output);
                } catch (Exception e) {
                    System.out.println("socket closed, exception: " + e);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeBanner(OutputStream stream) throws IOException {
        final byte BANNER_SIZE = 24;
        final byte version = 1;
        final byte quirks = 1; // QUIRK_DUMB
        int pid = Process.myPid();

        ByteBuffer b = ByteBuffer.allocate(BANNER_SIZE);
        b.order(ByteOrder.LITTLE_ENDIAN);
        b.put((byte) version); // version
        b.put(BANNER_SIZE);//banner size
        b.putInt(pid);//pid
        b.putInt(0);//real width
        b.putInt(0);//real height
        b.putInt(0);//desired width
        b.putInt(0);//desired height
        b.put((byte) rotation);//orientation
        b.put((byte) quirks);//quirks
        byte[] array = b.array();
        stream.write(array);
    }

    private void pipeImages(OutputStream stream) throws Exception {
        final int quality = 70;
        final int maxFPS = 20;
        final long interval = 1000 / maxFPS;

        long count = 0;
        long lastStartTime = 0;
        long calulateStartTime = SystemClock.uptimeMillis();

        while (true) {
            long waitMillis = lastStartTime + interval - SystemClock.uptimeMillis();
            if (waitMillis > 0) {
                SystemClock.sleep(waitMillis);
            }
            lastStartTime = SystemClock.uptimeMillis();
            Bitmap bmp = takeScreenshot();
            try {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, quality, buf);
                byte[] jpegData = buf.toByteArray();

                ByteBuffer b = ByteBuffer.allocate(4);
                b.order(ByteOrder.LITTLE_ENDIAN);
                b.putInt(jpegData.length);
                stream.write(b.array()); // write image size
                stream.write(jpegData);  // write image data

                count++;
                if (count == 30) {
                    float fps = (float) count / (SystemClock.uptimeMillis() - calulateStartTime) * 1000;
                    System.out.println("FPS: " + fps);
                    calulateStartTime = SystemClock.uptimeMillis();
                    count = 0;
                }
            } finally {
                bmp.recycle();
            }
        }
    }

    private Bitmap takeScreenshot() throws Exception {
        Class surfaceControl = Class.forName("android.view.SurfaceControl");
        Method screenshotMethod = surfaceControl.getDeclaredMethod("screenshot", Integer.TYPE, Integer.TYPE);
        try {
            Bitmap bmp = (Bitmap) screenshotMethod.invoke(null, new Object[]{width, height});
            if (rotation == 0) {
                return bmp;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(-90 * this.rotation);
            Bitmap rotatedBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
            bmp.recycle();
            return rotatedBmp;
        } catch (Exception e) {
            throw new Exception("Inject SurfaceControl fail", e);
        }
    }

    public static void main(String[] args) {
        /**
         * Usage:
         *     APKPATH=$(adb shell pm path com.github.uiautomator | cut -d: -f2)
         *     adb shell CLASSPATH=$APKPATH exec app_process /system/bin com.github.uiautomator.MinicapAgent --help
         */
        setArgV0(PROCESS_NAME);

        WindowManagerWrapper wm = new WindowManagerWrapper();
        try {
            System.out.println("Dump --.");
            System.out.println("Rotation: " + wm.getRotation());
            Point size = wm.getDisplaySize();
            if (size != null) {
                System.out.println("Display: " + size.x + "x" + size.y);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Point size = wm.getDisplaySize();
        if (size == null) {
            System.err.println("Unable to get screen resolution");
            System.exit(1);
        }

        MinicapAgent agent = new MinicapAgent(size.x, size.y, DEFAULT_SOCKET_NAME);
        agent.run();
    }

    @Override
    public void run() {
        try (LocalServerSocket serverSocket = new LocalServerSocket(socketName)) {
            manageClientConnection(serverSocket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
