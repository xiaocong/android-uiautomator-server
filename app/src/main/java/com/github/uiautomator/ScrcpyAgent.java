package com.github.uiautomator;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.Process;
import android.os.SystemClock;

import com.github.uiautomator.compat.WindowManagerWrapper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class ScrcpyAgent {
    private static final String PROCESS_NAME = "scrcpy.cli";
    private static final String VERSION = "1.0";
    private static final String SOCKET_NAME = "scrcpy";
    final WindowManagerWrapper windowManager = new WindowManagerWrapper();
    private int rotation;
    private Point size;

    public static void main(String[] args) {
        /**
         * Usage:
         *     APKPATH=$(adb shell pm path com.github.uiautomator | cut -d: -f2)
         *     adb shell CLASSPATH=$APKPATH exec app_process /system/bin com.github.uiautomator.ScrcpyAgent --help
         */
        setArgV0(PROCESS_NAME);

        Options options = new Options();
        options.addOption("v", "version", false, "show current version");
        options.addOption("h", "help", false, "show this message");
        options.addOption("i", true, "Change the name of of the abtract unix domain socket. (" + SOCKET_NAME + ")");
        options.addOption("d", "dump", false, "dump info for debug");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp(PROCESS_NAME, options);
            System.exit(1);
            return;
        }

        if (cmd.hasOption("help")) {
            formatter.printHelp(PROCESS_NAME, options);
            return;
        }
        if (cmd.hasOption("version")) {
            System.out.println(VERSION);
            return;
        }
        if (cmd.hasOption("dump")) {
            WindowManagerWrapper wm = new WindowManagerWrapper();
            try {
                Point size = wm.getDisplaySize();
                System.out.println("Dump --.");
                System.out.println("Display: " + size.x + "x" + size.y);
                System.out.println("Rotation: " + wm.getRotation());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        String socketName = cmd.getOptionValue("i");
        if (socketName == null) {
            socketName = SOCKET_NAME;
        }
        System.out.println("listen on localabstract:" + socketName);

        ScrcpyAgent agent = new ScrcpyAgent();
        agent.listenLocalSocket(socketName);
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

    public ScrcpyAgent() {
        rotation = windowManager.getRotation();
        windowManager.watchRotation(new WindowManagerWrapper.RotationWatcher() {
            @Override
            public void onRotationChanged(int r) {
                rotation = r;
                System.out.println("Rotation:" + r);
            }
        });

        try {
            size = windowManager.getDisplaySize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenLocalSocket(String socketName) {
        try (LocalServerSocket serverSocket = new LocalServerSocket(socketName)) {
            while (true) {
                try (LocalSocket socket = serverSocket.accept()) {
                    OutputStream output = socket.getOutputStream();
                    rotation = windowManager.getRotation();
                    Point size = windowManager.getDisplaySize();
                    System.out.println("Display: " + size.x + "x" + size.y);
                    System.out.println("Rotation: " + rotation);
                    try {
                        writeBanner(output);
                        pipeImages(output);
                    } catch (IOException e) {
                        System.out.println("socket closed, wait for new request");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeBanner(OutputStream stream) throws IOException {
        final byte BANNER_SIZE = 24;
        final byte version = 1;
        final byte quirks = 2;
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
                    float fps = (float) count / (SystemClock.uptimeMillis() -calulateStartTime) * 1000;
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
            Bitmap bmp = (Bitmap) screenshotMethod.invoke(null, new Object[]{size.x, size.y});
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

}
