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

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

public class ObjInfo {

    public static final ObjInfo getObjInfo(UiObject obj) throws UiObjectNotFoundException {
        return new ObjInfo(obj);
    }

    public static final ObjInfo getObjInfo(UiSelector selector) throws UiObjectNotFoundException {
        return new ObjInfo(UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).findObject(selector));
    }

	public static final ObjInfo getObjInfo(UiObject2 obj) {
		return new ObjInfo(obj);
	}

	private ObjInfo(UiObject obj) throws UiObjectNotFoundException {
		this._bounds = Rect.from(obj.getBounds());
		this._checkable = obj.isCheckable();
		this._checked = obj.isChecked();
		this._childCount = obj.getChildCount();
		this._clickable = obj.isClickable();
		this._contentDescription = obj.getContentDescription();
		this._enabled = obj.isEnabled();
		this._focusable = obj.isFocusable();
		this._focused = obj.isFocused();
		this._longClickable = obj.isLongClickable();
		this._packageName = obj.getPackageName();
		this._scrollable = obj.isScrollable();
		this._selected = obj.isSelected();
		this._text = obj.getText();
        this._visibleBounds = Rect.from(obj.getVisibleBounds());
        this._className = obj.getClassName();
	}

	private ObjInfo(UiObject2 obj) {
		this._bounds = Rect.from(obj.getVisibleBounds());
		this._checkable = obj.isCheckable();
		this._checked = obj.isChecked();
		this._childCount = obj.getChildCount();
		this._clickable = obj.isClickable();
		this._contentDescription = obj.getContentDescription();
		this._enabled = obj.isEnabled();
		this._focusable = obj.isFocusable();
		this._focused = obj.isFocused();
		this._longClickable = obj.isLongClickable();
		this._packageName = obj.getApplicationPackage();
		this._scrollable = obj.isScrollable();
		this._selected = obj.isSelected();
		this._text = obj.getText();
		this._visibleBounds = Rect.from(obj.getVisibleBounds());
		this._className = obj.getClassName();
		this._resourceName = obj.getResourceName();
	}

	private Rect _bounds;
	private Rect _visibleBounds;
	private int _childCount;
	private String _className;
	private String _contentDescription;
	private String _packageName;
	private String _text;
	private boolean _checkable;
	private boolean _checked;
	private boolean _clickable;
	private boolean _enabled;
	private boolean _focusable;
	private boolean _focused;
	private boolean _longClickable;
	private boolean _scrollable;
	private boolean _selected;
	private String _resourceName;

	public Rect getBounds() {
		return _bounds;
	}

	public void setBounds(Rect bounds) {
		this._bounds = bounds;
	}

	public Rect getVisibleBounds() {
		return _visibleBounds;
	}

	public void setVisibleBounds(Rect visibleBounds) {
		this._visibleBounds = visibleBounds;
	}

	public int getChildCount() {
		return _childCount;
	}

	public void setChildCount(int childCount) {
		this._childCount = childCount;
	}

	public String getClassName() {
		return _className;
	}

	public void setClassName(String className) {
		this._className = className;
	}

	public String getContentDescription() {
		return _contentDescription;
	}

	public void setContentDescription(String contentDescription) {
		this._contentDescription = contentDescription;
	}

	public String getPackageName() {
		return _packageName;
	}

	public void setPackageName(String packageName) {
		this._packageName = packageName;
	}

	public String getText() {
		return _text;
	}

	public void setText(String text) {
		this._text = text;
	}

	public boolean isCheckable() {
		return _checkable;
	}

	public void setCheckable(boolean checkable) {
		this._checkable = checkable;
	}

	public boolean isChecked() {
		return _checked;
	}

	public void setChecked(boolean checked) {
		this._checked = checked;
	}

	public boolean isClickable() {
		return _clickable;
	}

	public void setClickable(boolean clickable) {
		this._clickable = clickable;
	}

	public boolean isEnabled() {
		return _enabled;
	}

	public void setEnabled(boolean enabled) {
		this._enabled = enabled;
	}

	public boolean isFocusable() {
		return _focusable;
	}

	public void setFocusable(boolean focusable) {
		this._focusable = focusable;
	}

	public boolean isFocused() {
		return _focused;
	}

	public void setFocused(boolean focused) {
		this._focused = focused;
	}

	public boolean isLongClickable() {
		return _longClickable;
	}

	public void setLongClickable(boolean longClickable) {
		this._longClickable = longClickable;
	}

	public boolean isScrollable() {
		return _scrollable;
	}

	public void setScrollable(boolean scrollable) {
		this._scrollable = scrollable;
	}

	public boolean isSelected() {
		return _selected;
	}

	public void setSelected(boolean selected) { this._selected = selected; }

	public String getResourceName() {
		return _resourceName;
	}

	public void setResourceName(String resourceName) {
		this._resourceName = resourceName;
	}
}
