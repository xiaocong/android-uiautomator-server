package com.github.uiautomator.stub;

/**
 * Created with IntelliJ IDEA.
 * User: xiaocong@gmail.com
 * Date: 8/13/13
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class Point {
    private int _x;
    private int _y;

    public int getX() {
        return _x;
    }

    public void setX(int x) {
        this._x = x;
    }

    public int getY() {
        return _y;
    }

    public void setY(int y) {
        this._y = y;
    }

    public android.graphics.Point toPoint() {
        return new android.graphics.Point(_x, _y);
    }
}
