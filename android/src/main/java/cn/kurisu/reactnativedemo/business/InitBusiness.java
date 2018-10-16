package cn.kurisu.reactnativedemo.business;

import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;

import cn.kurisu.reactnativedemo.TXImPackage;
import cn.kurisu.reactnativedemo.event.FriendshipEvent;
import cn.kurisu.reactnativedemo.event.GroupEvent;
import cn.kurisu.reactnativedemo.event.MessageEvent;
import cn.kurisu.reactnativedemo.event.RefreshEvent;
import tencent.tls.platform.TLSAccountHelper;
import tencent.tls.platform.TLSLoginHelper;

import static cn.kurisu.reactnativedemo.utils.ReactCache.observeUserStatus;
import static cn.kurisu.reactnativedemo.utils.ReactCache.observeOnlineStatus;


/**
 * 初始化
 * 包括imsdk等
 */
public class InitBusiness {

    private static final String TAG = InitBusiness.class.getSimpleName();

    private static InitBusiness initBusiness;

    private InitBusiness() {
    }

    public static InitBusiness getInstance() {
        if (initBusiness == null) {
            initBusiness = new InitBusiness();
        }
        return initBusiness;
    }

    /**
     * 初始化imsdk
     */
    public boolean initImsdk(int sdkAppId, Context context, int logLevel) {
        TIMSdkConfig config = new TIMSdkConfig(sdkAppId).enableCrashReport(false)
                .enableLogPrint(true)
                .setLogLevel(TIMLogLevel.values()[logLevel]);
        //初始化imsdk
        boolean result = false;
        int a= 0;
        while (a < 10){
            a++;
            TIMManager.getInstance().setMode(1);
            result = TIMManager.getInstance().init(context, config);
            if (result){
                break;
            }
        }
        return result;
    }

    /**
     * 初始化用户配置
     */
    public void initUserConfig() {
        //登录之前要初始化群和好友关系链缓存
        TIMUserConfig userConfig = new TIMUserConfig();
        userConfig.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                Log.d(TAG, "被其他终端踢下线了");
                WritableMap map = Arguments.createMap();
                map.putInt("code", 6023);
                TXImPackage.txImModule.sendEvent(observeUserStatus, map);
            }

            @Override
            public void onUserSigExpired() {
                Log.i(TAG, "onUserSigExpired: 需要重新登陆");
                WritableMap map = Arguments.createMap();
                map.putInt("code", 70001);
                TXImPackage.txImModule.sendEvent(observeUserStatus, map);
            }
        }).setConnectionListener(new TIMConnListener() {
            @Override
            public void onConnected() {
                Log.i(TAG, "onConnected");
                TXImPackage.txImModule.show("连接成功");
                WritableMap map = Arguments.createMap();
                map.putInt("code", 10000);
                TXImPackage.txImModule.sendEvent(observeOnlineStatus, map);
            }

            @Override
            public void onDisconnected(int code, String desc) {
                Log.i(TAG, "onDisconnected");
                WritableMap map = Arguments.createMap();
                map.putInt("code", 10001);
                TXImPackage.txImModule.sendEvent(observeOnlineStatus, map);
            }

            @Override
            public void onWifiNeedAuth(String name) {
                Log.i(TAG, "onWifiNeedAuth");
                WritableMap map = Arguments.createMap();
                map.putInt("code", 10010);
                TXImPackage.txImModule.sendEvent(observeOnlineStatus, map);
            }
        });

        //设置刷新监听
        RefreshEvent.getInstance().init(userConfig);
        userConfig = FriendshipEvent.getInstance().init(userConfig);
        userConfig = GroupEvent.getInstance().init(userConfig);
        userConfig = MessageEvent.getInstance().init(userConfig);
        TIMManager.getInstance().setUserConfig(userConfig);
    }

    /**
     * @param context: 关联的activity
     * @function: 初始化TLS SDK, 必须在使用TLS SDK相关服务之前调用
     */
    public static void initTlsSdk(Context context) {
        TLSLoginHelper loginHelper = TLSLoginHelper.getInstance().init(context.getApplicationContext(),
                TLSConfiguration.SDK_APPID, TLSConfiguration.ACCOUNT_TYPE, TLSConfiguration.APP_VERSION);
        loginHelper.setTimeOut(TLSConfiguration.TIMEOUT);
        loginHelper.setLocalId(TLSConfiguration.LANGUAGE_CODE);
        loginHelper.setTestHost("", true);                   // 走sso
        TLSAccountHelper accountHelper = TLSAccountHelper.getInstance().init(context.getApplicationContext(),
                TLSConfiguration.SDK_APPID, TLSConfiguration.ACCOUNT_TYPE, TLSConfiguration.APP_VERSION);
        accountHelper.setCountry(Integer.parseInt(TLSConfiguration.COUNTRY_CODE)); // 存储注册时所在国家，只须在初始化时调用一次
        accountHelper.setTimeOut(TLSConfiguration.TIMEOUT);
        accountHelper.setLocalId(TLSConfiguration.LANGUAGE_CODE);
        accountHelper.setTestHost("", true);                 // 走sso
    }
}
