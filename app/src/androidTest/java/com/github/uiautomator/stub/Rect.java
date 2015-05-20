package com.github.uiautomator.stub;

/**
 * Created with IntelliJ IDEA.
 * User: b036
 * Date: 8/13/13
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class Rect {
    private int _top;
    private int _bottom;
    private int _left;
    private int _right;

    public static Rect from(android.graphics.Rect r) {
        Rect rect = new Rect();
        rect._top = r.top;
        rect._bottom = r.bottom;
        rect._left = r.left;
        rect._right = r.right;

        return rect;
    }

    public int getTop() {
        return _top;
    }

    public void setTop(int top) {
        this._top = top;
    }

    public int getBottom() {
        return _bottom;
    }

    public void setBottom(int bottom) {
        this._bottom = bottom;
    }

    public int getLeft() {
        return _left;
    }

    public void setLeft(int left) {
        this._left = left;
    }

    public int getRight() {
        return _right;
    }

    public void setRight(int right) {
        this._right = right;
    }

    public android.graphics.Rect toRect() {
        return new android.graphics.Rect(_left, _top, _right, _bottom);
    }
}
