package com.github.uiautomator;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class FloatView extends FrameLayout {
    private static final String TAG = "FloatView";

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;
    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    private boolean isAnchoring = false;
    private boolean isShowing = false;
    private WindowManager windowManager = null;
    private WindowManager.LayoutParams mParams = null;
    private Context context;

    public FloatView(Context context) {
        super(context);
        this.context = context;

        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ImageView imageView = new ImageView(this.context);
        imageView.setImageResource(R.drawable.icon);
        Log.d(TAG, String.format("imageView size: %dx%d", imageView.getWidth(), imageView.getHeight()));
        addView(imageView);
    }

    public void show() {
        if (this.isShown()) {
            Log.i(TAG, "already shown");
            return;
        }
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.packageName = context.getPackageName();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL // 不拦截触摸事件
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON; // 保持屏幕常亮
        int mType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mType = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        }
        params.type = mType;
        params.format = PixelFormat.RGBA_8888;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = screenWidth - dp2px(context, 100);
        params.y = screenHeight - dp2px(context, 171);
        params.alpha = 0.5f;
        this.setParams(params);
        windowManager.addView(this, params);
    }

    public void hide() {
        if (this.isShown()) {
            windowManager.removeView(this);
        }
    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isAnchoring) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                // Update floatWindow position
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(xDownInScreen - xInScreen) <= ViewConfiguration.get(getContext()).getScaledTouchSlop()
                        && Math.abs(yDownInScreen - yInScreen) <= ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    // 点击效果
                    Toast.makeText(getContext(), "this float window is clicked", Toast.LENGTH_SHORT).show();
                } else {
                    //吸附效果
                    anchorToSide();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void anchorToSide() {
        isAnchoring = true;
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        int middleX = mParams.x + getWidth() / 2;

        int animTime = 0;
        int xDistance = 0;
        int yDistance = 0;

        int dp_25 = dp2px(15);

        //1
        if (middleX <= dp_25 + getWidth() / 2) {
            xDistance = dp_25 - mParams.x;
        }
        //2
        else if (middleX <= screenWidth / 2) {
            xDistance = dp_25 - mParams.x;
        }
        //3
        else if (middleX >= screenWidth - getWidth() / 2 - dp_25) {
            xDistance = screenWidth - mParams.x - getWidth() - dp_25;
        }
        //4
        else {
            xDistance = screenWidth - mParams.x - getWidth() - dp_25;
        }

        //1
        if (mParams.y < dp_25) {
            yDistance = dp_25 - mParams.y;
        }
        //2
        else if (mParams.y + getHeight() + dp_25 >= screenHeight) {
            yDistance = screenHeight - dp_25 - mParams.y - getHeight();
        }
        Log.d(TAG, "xDistance  " + xDistance + "   yDistance" + yDistance);

        animTime = Math.abs(xDistance) > Math.abs(yDistance) ? (int) (((float) xDistance / (float) screenWidth) * 600f)
                : (int) (((float) yDistance / (float) screenHeight) * 900f);
        this.post(new AnchorAnimRunnable(Math.abs(animTime), xDistance, yDistance, System.currentTimeMillis()));
    }

    public int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private class AnchorAnimRunnable implements Runnable {

        private int animTime;
        private long currentStartTime;
        private Interpolator interpolator;
        private int xDistance;
        private int yDistance;
        private int startX;
        private int startY;

        public AnchorAnimRunnable(int animTime, int xDistance, int yDistance, long currentStartTime) {
            this.animTime = animTime;
            this.currentStartTime = currentStartTime;
            interpolator = new AccelerateDecelerateInterpolator();
            this.xDistance = xDistance;
            this.yDistance = yDistance;
            startX = mParams.x;
            startY = mParams.y;
        }

        @Override
        public void run() {
            if (System.currentTimeMillis() >= currentStartTime + animTime) {
                if (mParams.x != (startX + xDistance) || mParams.y != (startY + yDistance)) {
                    mParams.x = startX + xDistance;
                    mParams.y = startY + yDistance;
                    windowManager.updateViewLayout(FloatView.this, mParams);
                }
                isAnchoring = false;
                return;
            }
            float delta = interpolator.getInterpolation((System.currentTimeMillis() - currentStartTime) / (float) animTime);
            int xMoveDistance = (int) (xDistance * delta);
            int yMoveDistance = (int) (yDistance * delta);
            Log.d(TAG, "delta:  " + delta + "  xMoveDistance  " + xMoveDistance + "   yMoveDistance  " + yMoveDistance);
            mParams.x = startX + xMoveDistance;
            mParams.y = startY + yMoveDistance;
            windowManager.updateViewLayout(FloatView.this, mParams);
            FloatView.this.postDelayed(this, 16);
        }
    }

    private void updateViewPosition() {
        //增加移动误差
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        Log.d(TAG, "x  " + mParams.x + "   y  " + mParams.y);
        windowManager.updateViewLayout(this, mParams);
    }
}
