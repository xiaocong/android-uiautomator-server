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
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;

import java.util.regex.Pattern;

public class Selector {
	private String   _text;
	private String   _textContains;
	private String   _textMatches;
	private String   _textStartsWith;
	private String   _className;
	private String   _classNameMatches;
	private String   _description;
	private String   _descriptionContains;
	private String   _descriptionMatches;
	private String   _descriptionStartsWith;
	private boolean  _checkable;
	private boolean  _checked;
	private boolean  _clickable;
	private boolean  _longClickable;
	private boolean  _scrollable;
	private boolean  _enabled;
	private boolean  _focusable;
	private boolean  _focused;
	private boolean  _selected;
	private String   _packageName;
	private String   _packageNameMatches;
	private String   _resourceId;
	private String   _resourceIdMatches;
	private int      _index;
	private int      _instance;
	private Selector[] _childOrSiblingSelector = new Selector[]{};
	private String[] _childOrSibling = new String[]{};

	private long     _mask;

	public static final long MASK_TEXT = 0x01;
	public static final long MASK_TEXTCONTAINS = 0x02;
	public static final long MASK_TEXTMATCHES = 0x04;
	public static final long MASK_TEXTSTARTSWITH = 0x08;
	public static final long MASK_CLASSNAME = 0x10;
	public static final long MASK_CLASSNAMEMATCHES = 0x20;
	public static final long MASK_DESCRIPTION = 0x40;
	public static final long MASK_DESCRIPTIONCONTAINS = 0x80;
	public static final long MASK_DESCRIPTIONMATCHES = 0x0100;
	public static final long MASK_DESCRIPTIONSTARTSWITH = 0x0200;
	public static final long MASK_CHECKABLE = 0x0400;
	public static final long MASK_CHECKED = 0x0800;
	public static final long MASK_CLICKABLE = 0x1000;
	public static final long MASK_LONGCLICKABLE = 0x2000;
	public static final long MASK_SCROLLABLE = 0x4000;
	public static final long MASK_ENABLED = 0x8000;
	public static final long MASK_FOCUSABLE = 0x010000;
	public static final long MASK_FOCUSED = 0x020000;
	public static final long MASK_SELECTED = 0x040000;
	public static final long MASK_PACKAGENAME = 0x080000;
	public static final long MASK_PACKAGENAMEMATCHES = 0x100000;
	public static final long MASK_RESOURCEID = 0x200000;
	public static final long MASK_RESOURCEIDMATCHES = 0x400000;
	public static final long MASK_INDEX = 0x800000;
	public static final long MASK_INSTANCE = 0x01000000;

	private UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

	public UiSelector toUiSelector() {
		UiSelector s = new UiSelector();
		if ((getMask() & Selector.MASK_CHECKABLE) > 0 && android.os.Build.VERSION.SDK_INT >= 18)
			s = s.checkable(this.isCheckable());
		if ((getMask() & Selector.MASK_CHECKED) > 0)
			s = s.checked(isChecked());
		if ((getMask() & Selector.MASK_CLASSNAME) > 0)
			s = s.className(getClassName()); // API level 16 should support it.... wrong in Android Java Doc
		if ((getMask() & Selector.MASK_CLASSNAMEMATCHES) > 0 && android.os.Build.VERSION.SDK_INT >= 17)
			s = s.classNameMatches(getClassNameMatches());
		if ((getMask() & Selector.MASK_CLICKABLE) > 0)
			s = s.clickable(isClickable());
		if ((getMask() & Selector.MASK_DESCRIPTION) > 0)
			s = s.description(getDescription());
		if ((getMask() & Selector.MASK_DESCRIPTIONCONTAINS) > 0)
			s = s.descriptionContains(getDescriptionContains());
		if ((getMask() & Selector.MASK_DESCRIPTIONMATCHES) > 0 && android.os.Build.VERSION.SDK_INT >= 17)
			s = s.descriptionMatches(getDescriptionMatches());
		if ((getMask() & Selector.MASK_DESCRIPTIONSTARTSWITH) > 0)
			s = s.descriptionStartsWith(getDescriptionStartsWith());
		if ((getMask() & Selector.MASK_ENABLED) > 0)
			s = s.enabled(isEnabled());
		if ((getMask() & Selector.MASK_FOCUSABLE) > 0)
			s = s.focusable(isFocusable());
		if ((getMask() & Selector.MASK_FOCUSED) > 0)
			s = s.focused(isFocused());
		if ((getMask() & Selector.MASK_INDEX) > 0)
			s = s.index(getIndex());
		if ((getMask() & Selector.MASK_INSTANCE) > 0)
			s = s .instance(getInstance());
		if ((getMask() & Selector.MASK_LONGCLICKABLE) > 0 && android.os.Build.VERSION.SDK_INT >= 17)
			s = s.longClickable(isLongClickable());
		if ((getMask() & Selector.MASK_PACKAGENAME) > 0)
			s = s.packageName(getPackageName());
		if ((getMask() & Selector.MASK_PACKAGENAMEMATCHES) > 0 && android.os.Build.VERSION.SDK_INT >= 17)
			s = s.packageNameMatches(getPackageNameMatches());
		if ((getMask() & Selector.MASK_RESOURCEID) > 0 && android.os.Build.VERSION.SDK_INT >= 18)
			s = s.resourceId(getResourceId());
		if ((getMask() & Selector.MASK_RESOURCEIDMATCHES) > 0 && android.os.Build.VERSION.SDK_INT >= 18)
			s = s.resourceIdMatches(getResourceIdMatches());
		if ((getMask() & Selector.MASK_SCROLLABLE) > 0)
			s = s.scrollable(isScrollable());
		if ((getMask() & Selector.MASK_SELECTED) > 0)
			s = s.selected(isSelected());
		if ((getMask() & Selector.MASK_TEXT) > 0)
			s = s.text(getText());
		if ((getMask() & Selector.MASK_TEXTCONTAINS) > 0)
			s = s.textContains(getTextContains());
		if ((getMask() & Selector.MASK_TEXTSTARTSWITH) > 0)
			s = s.textStartsWith(getTextStartsWith());
		if ((getMask() & Selector.MASK_TEXTMATCHES) > 0 && android.os.Build.VERSION.SDK_INT >= 17)
			s = s.textMatches(getTextMatches());

        for (int i = 0; i < this.getChildOrSibling().length && i < this.getChildOrSiblingSelector().length; i++) {
            if (this.getChildOrSibling()[i].toLowerCase().equals("child"))
                s = s.childSelector(getChildOrSiblingSelector()[i].toUiSelector());
            else if (this.getChildOrSibling()[i].toLowerCase().equals("sibling"))
                s = s.fromParent((getChildOrSiblingSelector()[i].toUiSelector()));
        }

		return s;
	}

	public BySelector toBySelector() {
		BySelector s = null;
		if ((getMask() & Selector.MASK_CHECKABLE) > 0 && android.os.Build.VERSION.SDK_INT >= 18)
			s = By.checkable(this.isCheckable());
		if ((getMask() & Selector.MASK_CHECKED) > 0) {
            if (s == null)
                s = By.checked(isChecked());
            else
                s = s.checkable(isChecked());
        }
		if ((getMask() & Selector.MASK_CLASSNAME) > 0) {
            if (s == null)
                s = By.clazz(getClassName());
            else
                s = s.clazz(getClassName());
        }
		if ((getMask() & Selector.MASK_CLASSNAMEMATCHES) > 0) {
            if (s == null)
                s = By.clazz(Pattern.compile(getClassNameMatches()));
            else
                s = s.clazz(Pattern.compile(getClassNameMatches()));
        }
		if ((getMask() & Selector.MASK_CLICKABLE) > 0) {
            if (s == null)
                s = By.clickable(isClickable());
            else
                s = s.clickable(isClickable());
        }
		if ((getMask() & Selector.MASK_DESCRIPTION) > 0) {
            if (s == null)
                s = By.desc(getDescription());
            else
                s = s.desc(getDescription());
        }
		if ((getMask() & Selector.MASK_DESCRIPTIONCONTAINS) > 0) {
            if (s == null)
                s = By.descContains(getDescriptionContains());
            else
                s = s.descContains(getDescriptionContains());
        }
		if ((getMask() & Selector.MASK_DESCRIPTIONMATCHES) > 0) {
            if (s == null)
                s = By.desc(Pattern.compile(getDescriptionMatches()));
            else
                s = s.desc(Pattern.compile(getDescriptionMatches()));
        }
		if ((getMask() & Selector.MASK_DESCRIPTIONSTARTSWITH) > 0) {
            if (s == null)
                s = By.descStartsWith(getDescriptionStartsWith());
            else
                s = s.descStartsWith(getDescriptionStartsWith());
        }
		if ((getMask() & Selector.MASK_ENABLED) > 0) {
            if (s == null)
                s = By.enabled(isEnabled());
            else
                s = s.enabled(isEnabled());
        }
		if ((getMask() & Selector.MASK_FOCUSABLE) > 0) {
            if (s == null)
                s = By.focusable(isFocusable());
            else
                s = s.focusable(isFocusable());
        }
		if ((getMask() & Selector.MASK_FOCUSED) > 0) {
            if (s == null)
                s = By.focused(isFocused());
            else
                s = s.focused(isFocused());
        }
		if ((getMask() & Selector.MASK_LONGCLICKABLE) > 0) {
            if (s == null)
                s = By.longClickable(isLongClickable());
            else
                s = s.longClickable(isLongClickable());
        }
		if ((getMask() & Selector.MASK_PACKAGENAME) > 0) {
            if (s == null)
                s = By.pkg(getPackageName());
            else
                s = s.pkg(getPackageName());
        }
		if ((getMask() & Selector.MASK_PACKAGENAMEMATCHES) > 0) {
            if (s == null)
                s = By.pkg(Pattern.compile(getPackageNameMatches()));
            else
                s = s.pkg(Pattern.compile(getPackageNameMatches()));
        }
		if ((getMask() & Selector.MASK_RESOURCEID) > 0) {
            if (s == null)
                s = By.res(getResourceId());
            else
                s = s.res(getResourceId());
        }
		if ((getMask() & Selector.MASK_RESOURCEIDMATCHES) > 0) {
            if (s == null)
                s = By.res(Pattern.compile(getResourceIdMatches()));
            else
                s = s.res(Pattern.compile(getResourceIdMatches()));
        }
		if ((getMask() & Selector.MASK_SCROLLABLE) > 0) {
            if (s == null)
                s = By.scrollable(isScrollable());
            else
                s = s.scrollable(isScrollable());
        }
		if ((getMask() & Selector.MASK_SELECTED) > 0) {
            if (s == null)
                s = By.selected(isSelected());
            else
                s = s.selected(isSelected());
        }
		if ((getMask() & Selector.MASK_TEXT) > 0) {
            if (s == null)
                s = By.text(getText());
            else
                s = s.text(getText());
        }
		if ((getMask() & Selector.MASK_TEXTCONTAINS) > 0) {
            if (s == null)
                s = By.textContains(getTextContains());
            else
                s = s.textContains(getTextContains());
        }
		if ((getMask() & Selector.MASK_TEXTSTARTSWITH) > 0) {
            if (s == null)
                s = By.textStartsWith(getTextStartsWith());
            else
                s = s.textStartsWith(getTextStartsWith());
        }
		if ((getMask() & Selector.MASK_TEXTMATCHES) > 0) {
            if (s == null)
                s = By.text(Pattern.compile(getTextMatches()));
            else
                s = s.text(Pattern.compile(getTextMatches()));
        }

		return s;
	}

	public boolean checkBySelectorNull(Selector s){
		if((s.getMask() & Selector.MASK_INDEX)>0||(s.getMask() & Selector.MASK_INSTANCE)>0){
			return true;
		}
		if(s.toBySelector()==null){
			return true;
		}
		return false;
	}

	public UiObject2 toUiObject2() {
		if (checkBySelectorNull(this)) return null;

		UiObject2 obj2 = device.findObject(toBySelector());
		if(this.getChildOrSibling().length>0){
			return null;
		}
		return obj2;
	}

	public String getText() {
		return _text;
	}
	
	public void setText(String text) {
		this._text = text;
	}
	
	public String getClassName() {
		return _className;
	}
	
	public void setClassName(String className) {
		this._className = className;
	}
	
	public String getDescription() {
		return _description;
	}
	
	public void setDescription(String description) {
		this._description = description;
	}

	public String getTextContains() {
		return _textContains;
	}

	public void setTextContains(String _textContains) {
		this._textContains = _textContains;
	}

	public String getTextMatches() {
		return _textMatches;
	}

	public void setTextMatches(String _textMatches) {
		this._textMatches = _textMatches;
	}

	public String getTextStartsWith() {
		return _textStartsWith;
	}

	public void setTextStartsWith(String _textStartsWith) {
		this._textStartsWith = _textStartsWith;
	}

	public String getClassNameMatches() {
		return _classNameMatches;
	}

	public void setClassNameMatches(String _classNameMatches) {
		this._classNameMatches = _classNameMatches;
	}

	public String getDescriptionContains() {
		return _descriptionContains;
	}

	public void setDescriptionContains(String _descriptionContains) {
		this._descriptionContains = _descriptionContains;
	}

	public String getDescriptionMatches() {
		return _descriptionMatches;
	}

	public void setDescriptionMatches(String _descriptionMatches) {
		this._descriptionMatches = _descriptionMatches;
	}

	public String getDescriptionStartsWith() {
		return _descriptionStartsWith;
	}

	public void setDescriptionStartsWith(String _descriptionStartsWith) {
		this._descriptionStartsWith = _descriptionStartsWith;
	}

	public boolean isCheckable() {
		return _checkable;
	}

	public void setCheckable(boolean _checkable) {
		this._checkable = _checkable;
	}

	public boolean isChecked() {
		return _checked;
	}

	public void setChecked(boolean _checked) {
		this._checked = _checked;
	}

	public boolean isClickable() {
		return _clickable;
	}

	public void setClickable(boolean _clickable) {
		this._clickable = _clickable;
	}

	public boolean isScrollable() {
		return _scrollable;
	}

	public void setScrollable(boolean _scrollable) {
		this._scrollable = _scrollable;
	}

	public boolean isLongClickable() {
		return _longClickable;
	}

	public void setLongClickable(boolean _longClickable) {
		this._longClickable = _longClickable;
	}

	public boolean isEnabled() {
		return _enabled;
	}

	public void setEnabled(boolean _enabled) {
		this._enabled = _enabled;
	}

	public boolean isFocusable() {
		return _focusable;
	}

	public void setFocusable(boolean _focusable) {
		this._focusable = _focusable;
	}

	public boolean isFocused() {
		return _focused;
	}

	public void setFocused(boolean _focused) {
		this._focused = _focused;
	}

	public boolean isSelected() {
		return _selected;
	}

	public void setSelected(boolean _selected) {
		this._selected = _selected;
	}

	public String getPackageName() {
		return _packageName;
	}

	public void setPackageName(String _packageName) {
		this._packageName = _packageName;
	}

	public String getPackageNameMatches() {
		return _packageNameMatches;
	}

	public void setPackageNameMatches(String _packageNameMatches) {
		this._packageNameMatches = _packageNameMatches;
	}

	public String getResourceId() {
		return _resourceId;
	}

	public void setResourceId(String _resourceId) {
		this._resourceId = _resourceId;
	}

	public String getResourceIdMatches() {
		return _resourceIdMatches;
	}

	public void setResourceIdMatches(String _resourceIdMatches) {
		this._resourceIdMatches = _resourceIdMatches;
	}

	public int getIndex() {
		return _index;
	}

	public void setIndex(int _index) {
		this._index = _index;
	}

	public int getInstance() {
		return _instance;
	}

	public void setInstance(int _instance) {
		this._instance = _instance;
	}

	public long getMask() {
		return _mask;
	}

	public void setMask(long _mask) {
		this._mask = _mask;
	}

    public Selector[] getChildOrSiblingSelector() {
        return _childOrSiblingSelector;
    }

    public void setChildOrSiblingSelector(Selector[] _childOrSiblingSelector) {
        this._childOrSiblingSelector = _childOrSiblingSelector;
    }

    public String[] getChildOrSibling() {
        return _childOrSibling;
    }

    public void setChildOrSibling(String[] _childOrSibling) {
        this._childOrSibling = _childOrSibling;
    }
}
