React Native的腾讯IM插件
#### 注意事项: 
```
1.安装好npm包后把react-native-txim/src/main/jinLibs 复制到 <你的项目>/android/app/src/main/jinLibs
2.// file: android/settings.gradle 
android{

    defaultConfig{
        ...
        manifestPlaceholders = [
                IM_APPID     : xxxxxx, //你的SDKAPPID
                IM_ACCOUNT_TYPE     : xxxx //你的ACCCOUNTTYPE
        ]
        ndk {
            abiFilters "armeabi-v7a", "x86"
        }
    }
    //新增下面代码
     packagingOptions {
        pickFirst 'lib/armeabi-v7a/libgnustl_shared.so'
        pickFirst 'lib/x86/libgnustl_shared.so'
    }
    
    compileOptions {
            targetCompatibility 1.8
            sourceCompatibility 1.8
        }
}

```
## 如何安装

### 1.首先安装npm包

```bash
npm install react-native-txim --save
```

### 2.link
```bash
react-native link react-native-txim
```

#### 手动link~（如果不能够自动link）
##### ios
```
待补充
```
##### Android
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

`android/app/src/main/java/<你的包名>/MainActivity.java`

```java
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends ReactActivity {

 ......

 @Override
 protected void onCreate(Bundle savedInstanceState) {
   final List<String> permissionsList = new ArrayList<>();
   SplashScreen.show(this, true);
   super.onCreate(savedInstanceState);
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
       if ((checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
           permissionsList.add(android.Manifest.permission.READ_PHONE_STATE);
       if ((checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
           permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
       if (permissionsList.size() != 0) {
           requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                   0);
        }
     } 
 }

 ```

`android/app/src/main/java/<你的包名>/MainApplication.java`：

```java
...
import cn.kurisu.rnim.IMApplication;
import cn.kurisu.rnim.TXImPackage;
import cn.kurisu.rnim.utils.Foreground;

public class MainApplication extends Application implements ReactApplication {

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    protected boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new TXImPackage(), // 然后添加这一行
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
    Foreground.init(this); //然后添加这一行
    IMApplication.pushInit(this, MainActivity.class);// 然后添加这一行
    SoLoader.init(this, /* native exopackage */ false);
   ...
  }
}
```


### 3.工程配置
#### iOS配置
```
待补充
```

#### Android配置

在`android/app/build.gradle`里，defaultConfig栏目下添加如下代码：
```
 manifestPlaceholders = [
                IM_APPID     : xxxxxx, //你的SDKAPPID
                IM_ACCOUNT_TYPE     : xxxx //你的ACCCOUNTTYPE
        ]
```

在`AndroidManifest.xml`里，添加如下代码：
```
< manifest

    ......

    <meta-data android:name="IM_APPID"
                android:value="${IM_APPID}"/> 
    
            <meta-data android:name="IM_ACCOUNT_TYPE"
                android:value="${IM_ACCOUNT_TYPE}"/> 

```

## 如何使用

### 引入包

```
import { XXX } from 'react-native-txim'; //你需要的模块
```

### API

参考[index.js](https://github.com/kurisu994/react-native-txim/blob/master/index.js)

#### 监听会话
```
NativeAppEventEmitter.addListener("listenerReceiveMessage",(data)=>{
  console.log(data); //新消息监听事件（非当前聊天人的）
})；

NativeAppEventEmitter.addListener("observeCurrentMessage",(data)=>{
  console.log(data); //新消息监听事件（当前聊天人的）
})；
```
#### 推送
```
待补充

```
