/*
 * The MIT License (MIT)
 * Copyright (c) 2015 xiaocong@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.uiautomator.stub;

/**
 * Created with IntelliJ IDEA.
 * User: xiaocong@gmail.com
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
