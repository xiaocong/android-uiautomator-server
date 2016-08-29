# Purpose

[UIAutomator](http://developer.android.com/tools/testing/testing_ui.html) is a
great tool to perform Android UI testing, but to do it, you have to write java
code, compile it, install the jar, and run. It's a complex steps for all
testers...

This project is to build a light weight jsonrpc server in Android device, so
that we can just write PC side script to write UIAutomator tests.

# Build

- Run command:

        $ ./gradlew build
        $ ./gradlew packageDebugAndroidTest

- Run the jsonrcp server on Android device

        $ ./gradlew cC
        $ adb forward tcp:9008 tcp:9008 # tcp forward

# How to use

```python
from uiautomator import device as d

d.screen.on()
d(text="Settings").click()
d(scrollable=True).scroll.vert.forward()
```

Refer to python wrapper library [uiautomator](https://github.com/xiaocong/uiautomator).

# Notes

If you have any idea, please email xiaocong@gmail.com or [submit tickets](https://github.com/xiaocong/uiautomator/issues/new).

# Dependencies

- [nanohttpd](https://github.com/NanoHttpd/nanohttpd)
- [jsonrpc4j](https://github.com/briandilley/jsonrpc4j)
- [jackson](https://github.com/FasterXML/jackson)

# TODO

- support unicode input
