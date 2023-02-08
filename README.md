# React Native的腾讯IM插件
[![Badge](https://img.shields.io/badge/link-996.icu-%23FF4D5B.svg?style=flat-square)](https://996.icu/#/zh_CN)
[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg?style=flat-square)](https://github.com/996icu/996.ICU/blob/master/LICENSE)
[![Slack](https://img.shields.io/badge/slack-996icu-green.svg?style=flat-square)](https://join.slack.com/t/996icu/shared_invite/enQtNjI0MjEzMTUxNDI0LTkyMGViNmJiZjYwOWVlNzQ3NmQ4NTQyMDRiZTNmOWFkMzYxZWNmZGI0NDA4MWIwOGVhOThhMzc3NGQyMDBhZDc)
[![HitCount](http://hits.dwyl.io/996icu/996.ICU.svg)](http://hits.dwyl.io/996icu/996.ICU)

# 鉴于官方发布了rn的版本，本项目不在维护。有需要的可以使用 [官方版本](https://github.com/TencentCloud/TIMSDK/tree/master/ReactNative)

## 如何安装

### 1.首先安装npm包

```bash
npm install react-native-txim --save
```

### 2.link
```bash
react-native link react-native-txim
```
##### ios 手动link
```
待补充
```
##### Android 手动link
```
// file: android/settings.gradle
...

include ':react-native-txim'
project(':react-native-txim').projectDir = new File(settingsDir, '../node_modules/react-native-txim/android')
```

```
// file: android/app/build.gradle
...

dependencies {
    ...
    api project(':react-native-txim')
}
```

## 工程配置(重要) 
### android配置
1. 配置appid和离线推送的相关key,如果不需要离线推送那么离线推送部分可以省略
> file: android/app/build.gradle 
```shell
android{

    defaultConfig{
        ...
        manifestPlaceholders = [
                //IM的appid
                IM_APPID        : "xxxx",


                /**离线推送相关 */
                //小米 
                XM_PUSH_APPKEY: "xxxx",
                XM_PUSH_APPID: "xxx",
                XM_PUSH_BUZID: "xxxxxx",
                //华为
                HW_PUSH_BUZID: "xxxxxx",
                HW_PUSH_APPID: 'xxxxxx',
                //魅族
                MZ_PUSH_BUZID: "xxxxxx",
                MZ_PUSH_APPID: '111111',
                MZ_PUSH_APPKEY: 'xxxxx',
                //vivo
                VIVO_PUSH_BUZID: "xxxxx",
                VIVO_PUSH_APPID: 'xxxxxx',
                VIVO_PUSH_APPKEY: 'xxxxxxx'
        ]
    }

    //这个是部分项目兼容性问题
     packagingOptions {
        pickFirst 'lib/x86/libc++_shared.so'
        pickFirst 'lib/x86_64/libc++_shared.so'
        pickFirst 'lib/arm64-v8a/libc++_shared.so'
        pickFirst 'lib/armeabi-v7a/libc++_shared.so'
    }
    
}
```
> file: android/build.gradle 
```shell
allprojects {
    repositories {
        ....
        // 华为离线推送
        maven {url 'http://developer.huawei.com/repo/'}
    }
}
```

> file: android/app/src/AndroidManifest.xml

```xml
<!-- 这里是权限相关配置-->

<!-- ********华为推送权限设置start******** -->
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
    <permission
        android:name="你的包名.permission.PROCESS_PUSH_MSG"
        android:protectionLevel="signatureOrSystem"/>
    <uses-permission android:name="你的包名.permission.PROCESS_PUSH_MSG" />
    <!-- ********华为推送权限设置end******** -->

    <!-- ********小米推送权限设置start******** -->
    <permission
        android:name="你的包名.permission.MIPUSH_RECEIVE"
        android:protectionLevel="signature" />
    <uses-permission android:name="你的包名.permission.MIPUSH_RECEIVE" />
    <!-- ********小米推送权限设置end******** -->

    <!-- ********魅族推送权限设置start******** -->
    <!-- 兼容flyme5.0以下版本，魅族内部集成pushSDK必填，不然无法收到消息-->
    <uses-permission android:name="com.meizu.flyme.push.permission.RECEIVE"></uses-permission>
    <permission
        android:name="你的包名.push.permission.MESSAGE"
        android:protectionLevel="signature"/>
    <uses-permission android:name="你的包名.push.permission.MESSAGE"></uses-permission>
    <!--  兼容flyme3.0配置权限-->
    <uses-permission android:name="com.meizu.c2dm.permission.RECEIVE" />
    <permission
        android:name="你的包名.permission.C2D_MESSAGE"
        android:protectionLevel="signature"></permission>
    <uses-permission android:name="你的包名.permission.C2D_MESSAGE"/>
    <!-- ********魅族推送权限设置end******** -->

<application
...
>
        <meta-data
            android:name="IM_APPID"
            android:value="${IM_APPID}" />

         <!-- 以下为离线推送配置 -->
        <!-- 小米 -->
        <meta-data
            android:name="XM_PUSH_BUZID"
            android:value="${XM_PUSH_BUZID}" />
        <meta-data
            android:name="XM_PUSH_APPID"
            android:value="\ ${XM_PUSH_APPID}" />
        <meta-data
            android:name="XM_PUSH_APPKEY"
            android:value="\ ${XM_PUSH_APPKEY}" />
        <!-- 华为 -->
        <meta-data
            android:name="HW_PUSH_BUZID"
            android:value="${HW_PUSH_BUZID}" />
        <meta-data
            android:name="com.huawei.hms.client.appid"
            android:value="appid=${HW_PUSH_APPID}"/>
        <!--魅族-->
        <meta-data
            android:name="MZ_PUSH_BUZID"
            android:value="${MZ_PUSH_BUZID}" />
        <meta-data
            android:name="MZ_PUSH_APPID"
            android:value="\ ${MZ_PUSH_APPID}" />
        <meta-data
            android:name="MZ_PUSH_APPKEY"
            android:value="\ ${MZ_PUSH_APPKEY}" />
        <!--vivo-->
        <meta-data
            android:name="VIVO_PUSH_BUZID"
            android:value="${VIVO_PUSH_BUZID}" />
        <meta-data
            android:name="com.vivo.push.api_key"
            android:value="${VIVO_PUSH_APPKEY}" />
        <meta-data
            android:name="com.vivo.push.app_id"
            android:value="${VIVO_PUSH_APPID}" />
</application>
```




> android/app/src/main/java/<你的包名>/MainActivity.java

```java
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends ReactActivity {

 ......

@Override
  protected void onResume() {
    super.onResume();
    checkNotifySetting();
  }

private void checkNotifySetting() {
    NotificationManagerCompat manager = NotificationManagerCompat.from(this);
    // areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
    boolean isOpened = manager.areNotificationsEnabled();

    if (!isOpened) {
      //未打开通知
      AlertDialog alertDialog = new AlertDialog.Builder(this)
              .setTitle("提示")
              .setMessage("我们需要您在“通知”中打开通知权限")
              .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              })
              .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                  try {
                    // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
                    Intent intent = new Intent();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                      intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                      intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //5.0
                      intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                      intent.putExtra("app_package", getPackageName());
                      intent.putExtra("app_uid", getApplicationInfo().uid);
                      startActivity(intent);
                    } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {  //4.4
                      intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                      intent.addCategory(Intent.CATEGORY_DEFAULT);
                      intent.setData(Uri.parse("package:" + getPackageName()));
                    } else if (Build.VERSION.SDK_INT >= 15) {
                      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                      intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                      intent.setData(Uri.fromParts("package", getPackageName(), null));
                    }
                    startActivity(intent);
                  } catch (Exception e) {
                    e.printStackTrace();
                    // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
                    Intent intent = new Intent();

                    //下面这种方案是直接跳转到当前应用的设置界面。
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                  }
                }
              })
              .create();
      alertDialog.show();
      alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
      alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }
  }

 ```

`android/app/src/main/java/<你的包名>/MainApplication.java`：

```java
...
import cn.kurisu.txim.IMApplication;
import cn.kurisu.txim.IMPackage;

public class MainApplication extends Application implements ReactApplication {

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    protected boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new IMPackage(), // 然后添加这一行
          new MainReactPackage()
      );
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
      return mReactNativeHost;
  }
   @Override
  public void onCreate() {
   super.onCreate();
    IMApplication.setContext(this,  MainActivity.class);;// 然后添加这一行
    SoLoader.init(this, /* native exopackage */ false);
   ...
  }
}
```
### ios配置  
待补充

## 如何使用  
### 引入包

```
import { XXX } from 'react-native-txim'; //你需要的模块
```

### API

参考[index.js](https://github.com/kurisu994/react-native-txim/blob/master/index.d.ts)

