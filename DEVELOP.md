## minitouch

测试数据


```
d 0 800 1000 50
c
u 0
c
```

d 0 600 800 50
c
u 0
c


运行

```bash
APK=$(adb shell pm path com.github.uiautomator | cut -d: -f2)
adb shell export CLASSPATH="$APK"\; \
    exec app_process /system/bin com.github.uiautomator.MinitouchAgent
```

测试

```bash
adb forward tcp:7788 localabstract:minitouchagent
nc localhost 7788
```
