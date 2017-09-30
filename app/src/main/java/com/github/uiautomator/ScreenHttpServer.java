package com.github.uiautomator;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Surface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by hzsunshx on 2017/8/31.
 */

public class ScreenHttpServer extends NanoHTTPD {
    private static final String TAG = "ScreenHttpServer";
    private static final String MIME_TYPE = "video/avc";

    private int mWidth = 1080;
    private int mHeight = 1920;
    private boolean landscape = false;
    private Boolean recording = false;

    private Class surfaceControl;
    private java.lang.reflect.Method rDestroyDisplay;
    private java.lang.reflect.Method rOpenTransaction;
    private java.lang.reflect.Method rCloseTransaction;
    private java.lang.reflect.Method rCreateDisplay;

    protected static final int TIMEOUT_USEC = 10000;    // 10[msec]

    public ScreenHttpServer(int port) {
        super(port);
    }

    public void initialize() throws Exception {
        this.surfaceControl = Class.forName("android.view.SurfaceControl");
        Bitmap bmp = this.takeScreenshot();
        this.mWidth = bmp.getWidth();
        this.mHeight = bmp.getHeight();
        this.landscape = false;
        bmp.recycle();

        System.out.println("System info:\n" + String.format("\tSDK: %d\n\tDisplay: %dx%d", Build.VERSION.SDK_INT, this.mWidth, this.mHeight));
        if (Build.VERSION.SDK_INT < 21) {
            System.out.println("Screenrecord require SDK >= 21");
        }
    }

    @Override
    public Response serve(String uri, Method method,
                          Map<String, String> headers, Map<String, String> params,
                          Map<String, String> files) {
        Log.d(TAG, String.format("URI: %s, Method: %s, params, %s, files: %s", uri, method, params, files));
        try {
            if ("/stop".equals(uri)) {
                stop();
                return newFixedLengthResponse("Server stopped");
            } else if ("/screenshot".equals(uri)) {
                return handleGetScreenshot();
            } else if ("/screenrecord".equals(uri) && Method.POST == method) {
                return handlePostScreenrecord(params);
            } else if ("/screenrecord".equals(uri) && Method.PUT == method) {
                return handlePutScreenrecord();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Internal Error: " + ex.getMessage());
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, ex.getMessage());
        }
        return newFixedLengthResponse("404 Not found");
    }

    private Response handleGetScreenshot() {
        // Requires SDK >= 21
        java.lang.reflect.Method injector = null;
        try {
            Bitmap bmp = this.takeScreenshot();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 75, bout);
            bmp.recycle();
            return newChunkedResponse(Response.Status.OK, "image/jpeg", new ByteArrayInputStream(bout.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return newFixedLengthResponse("Screenshot exception: " + e.toString());
        }
    }

    private Bitmap takeScreenshot() throws Exception {
        try {
            java.lang.reflect.Method rScreenshot = surfaceControl.getDeclaredMethod("screenshot", Integer.TYPE, Integer.TYPE);
            Bitmap bmp = (Bitmap) rScreenshot.invoke(null, new Object[]{0, 0});

            mWidth = bmp.getWidth();
            mHeight = bmp.getHeight();
            return bmp;
        } catch (Exception e) {
            throw new Exception("Inject SurfaceControl fail", e);
        }
    }

    // Start VideoRecord
    private Response handlePostScreenrecord(Map<String, String> params) throws Exception {
        if (Build.VERSION.SDK_INT < 21) {
            return newFixedLengthResponse("Screenrecord require SDK >= 21");
        }
        if (this.recording) {
            return newFixedLengthResponse("Already started record!");
        }
        this.recording = true;
        this.landscape = "true".equals(params.get("landscape")) || "1".equals(params.get("landscape"));

        String videoPath = params.get("path");
        if (videoPath == null || "".equals(videoPath)) {
            videoPath = "/sdcard/video.mp4";
        }
        new File(videoPath).delete(); // delete file before create

        final MediaCodec avc = createAVC();
        final MediaMuxer muxer = new MediaMuxer(videoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

        final String finalVideoPath = videoPath;
        new Thread("ScreenRecord") {
            @Override
            public void run() {
                IBinder virtualDisplay = null;
                try {
                    virtualDisplay = createVirtualDisplay(avc);
                    System.out.println("> Recording started");
                    startRecording(avc, muxer);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("> Recording finished, saved to " + finalVideoPath);
                    releaseRecording(avc, virtualDisplay, muxer);
                }
            }
        }.start();

        return newFixedLengthResponse("OK");
    }

    // Stop VideoRecord
    private Response handlePutScreenrecord() throws Exception {
        this.recording = false;
        return newFixedLengthResponse("OK");
    }

    @TargetApi(21)
    private void startRecording(MediaCodec avc, MediaMuxer muxer) {
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int track = -1;
        try {
            while (this.recording) {
                // get virtual display data
                int index = avc.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
                if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // Nothing to do here, anyway, deuqueueOutputBuffer will block for 10ms if no buffer
                } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    if (track != -1) {
                        throw new RuntimeException("format changed twice");
                    }
                    // Should only call once
                    MediaFormat format = avc.getOutputFormat();
                    System.out.println("Output format changed to " + format.toString());
                    track = muxer.addTrack(format);
                    muxer.start();
                } else if (index >= 0) {
                    if (track == -1) {
                        throw new Exception("MediaCodec track index is not setted!");
                    }
                    ByteBuffer data = avc.getOutputBuffer(index);
                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        System.out.println("ignoring BUFFER_FLAG_CODEC_CONFIG");
                        bufferInfo.size = 0;
                    }
                    if (bufferInfo.size == 0) {
                        System.out.println("Ignore data(size=0)");
                        data = null;
                    }
                    if (data != null) {
                        data.position(bufferInfo.offset);
                        data.limit(bufferInfo.offset + bufferInfo.size);
                        muxer.writeSampleData(track, data, bufferInfo);
                        avc.releaseOutputBuffer(index, false);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private IBinder createVirtualDisplay(MediaCodec mediaCodec) throws Exception {
        try {
            rCreateDisplay = surfaceControl.getDeclaredMethod("createDisplay", String.class, Boolean.TYPE);
            rOpenTransaction = surfaceControl.getDeclaredMethod("openTransaction");
            rCloseTransaction = surfaceControl.getDeclaredMethod("closeTransaction");
            rDestroyDisplay = surfaceControl.getDeclaredMethod("destroyDisplay", IBinder.class);

            IBinder mDisplay = (IBinder) rCreateDisplay.invoke(null, "UIAutomatorDisplay", Boolean.valueOf(false));
            java.lang.reflect.Method setDisplaySurface = surfaceControl.getDeclaredMethod("setDisplaySurface", IBinder.class, Surface.class);
            java.lang.reflect.Method setDisplayProjection = surfaceControl.getDeclaredMethod("setDisplayProjection", IBinder.class, Integer.TYPE, Rect.class, Rect.class);
            java.lang.reflect.Method setDisplayLayerStack = surfaceControl.getDeclaredMethod("setDisplayLayerStack", IBinder.class, Integer.TYPE);

            Surface surface = mediaCodec.createInputSurface();
            mediaCodec.start(); // TODO

            rOpenTransaction.invoke(null);
            setDisplaySurface.invoke(null, mDisplay, surface);
            setDisplayProjection.invoke(null, mDisplay, 0, getCurrentDisplayRect(), getCurrentDisplayRect()); // make video smaller
            setDisplayLayerStack.invoke(null, mDisplay, 0);
            rCloseTransaction.invoke(null);

            return mDisplay;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("virtual display", ex);
        }
    }

    // avc: Video Encoding is AVC(H.264)
    private void releaseRecording(MediaCodec avc, IBinder bDisplay, MediaMuxer muxer) {
        try {
            avc.stop();
            avc.release();
            rDestroyDisplay.invoke(null, bDisplay);
            muxer.stop();
            // raise Null exception
            // muxer.release();
            // TODO muxer
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private MediaCodec createAVC() throws Exception {
        Rect display = getCurrentDisplayRect();
        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, display.width(), display.height());
        // Set color format
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, 1500000); // bit rate 1500000
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 20); // FPS
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10); // Frame interval, unit seconds
        MediaCodec mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC); // Output encoding
        mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE); // 配置好格式参数
        return mMediaCodec;
    }

    private Rect getCurrentDisplayRect() {
        if (landscape) {
            return new Rect(0, 0, mHeight, mWidth);
        }
        return new Rect(0, 0, mWidth, mHeight);
    }
}
