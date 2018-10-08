package cn.kurisu.reactnativedemo;

import android.app.Application;
import android.content.Context;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.TIMManager;
import com.tencent.qalsdk.sdk.MsfSdkUtils;

import java.util.Arrays;
import java.util.List;

import cn.kurisu.reactnativedemo.utils.Foreground;


public class MainApplication extends Application implements ReactApplication {


    public static final TXImPackage TX_IM_PACKAGE = new TXImPackage();

    private static Context context;

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {

        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.asList(
                    new MainReactPackage(),
                    TX_IM_PACKAGE
            );
        }

        @Override
        protected String getJSMainModuleName() {
            return "index";
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Foreground.init(this);
        context = getApplicationContext();
        if (MsfSdkUtils.isMainProcess(this)) {
            TIMManager.getInstance().setOfflinePushListener(notification -> {
                if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify) {
                    //消息被设置为需要提醒
                    notification.doNotify(getApplicationContext(), R.mipmap.ic_launcher);
                }
            });
        }
        SoLoader.init(this, /* native exopackage */ false);
    }

    public static Context getContext() {
        return context;
    }


}
