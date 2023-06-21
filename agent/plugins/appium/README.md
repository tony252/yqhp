## 初始化

### 方式1

```groovy
import com.yqhp.plugin.appium.*;

var d = new AppiumDriverWrapper(yqhp.appiumDriver());
```

### 方式2（推荐）

> 扩展AppiumDriverWrapper，根据团队需要，提供更多API

```groovy
import com.yqhp.plugin.appium.*;

class CustomAppiumDriver extends AppiumDriverWrapper {

    CustomAppiumDriver() {
        super(yqhp.appiumDriver());
    }

    void pressAndroidKey(AndroidKey key) {
        androidDriver().pressKey(new KeyEvent(key));
    }

    void pressHome() {
        pressAndroidKey(AndroidKey.HOME);
    }

    void pressBack() {
        pressAndroidKey(AndroidKey.BACK);
    }

    /**
     * 启动app
     *
     * @param appId  android: package name | ios: bundle id
     */
    void startApp(String appId) {
        ((InteractsWithApps) driver()).activateApp(appId);
    }

    /**
     * 停止app
     * 
     * @param appId  android: package name | ios: bundle id
     */
    boolean stopApp(String appId) {
        return ((InteractsWithApps) driver()).terminateApp(appId);
    }
    
    /**
     * 安装app
     * 
     * @param appUri app url or filePath
     */
    void installApp(String appUri) {
        yqhp.installApp(appUri);
    }

    /**
     * app是否已安装
     * 
     * @param appId android: package name | ios: bundle id
     */
    boolean isAppInstalled(String appId) {
        return ((InteractsWithApps) driver()).isAppInstalled(appId);
    }

    /**
     * 卸载app
     *
     * @param appId android: package name | ios: bundle id
     */
    boolean uninstallApp(String appId) {
        return ((InteractsWithApps) driver()).removeApp(appId);
    }

    /**
     * 清除apk数据, 相当于重新安装了app
     *
     * @param pkg  package name
     */
    void clearApkData(String pkg) {
        yqhp.androidShell("pm clear " + pkg);
    }
}

var d = new CustomAppiumDriver();
```

## API

[查看](https://github.com/yqhp/yqhp/blob/main/agent/plugins/appium/src/main/java/com/yqhp/plugin/appium/AppiumDriverWrapper.java)
 
