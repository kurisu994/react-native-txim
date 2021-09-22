package cn.kurisu.txim.utils;

import static cn.kurisu.txim.utils.NetWorkUtils.sIMSDKConnected;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMSDKConfig;
import com.tencent.imsdk.v2.V2TIMSDKListener;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.kurisu.txim.interfaces.TIMCallBack;
import cn.kurisu.txim.business.conversation.ConversationManagerKit;
import cn.kurisu.txim.business.manager.FaceManager;
import cn.kurisu.txim.configs.GeneralConfig;
import cn.kurisu.txim.configs.TIMConfigs;
import cn.kurisu.txim.listener.IMEventListener;

public class TIMInitUtil {

    private static final String TAG = "TIMInitUtil";

    private static Context sAppContext;
    private static TIMConfigs sConfigs;
    private static List<IMEventListener> sIMEventListeners = new ArrayList<>();

    /**
     * TUIKit的初始化函数
     *
     * @param context  应用的上下文，一般为对应应用的ApplicationContext
     * @param sdkAppID 您在腾讯云注册应用时分配的sdkAppID
     * @param configs  TUIKit的相关配置项，一般使用默认即可，需特殊配置参考API文档
     */
    public static void init(Context context, int sdkAppID, TIMConfigs configs) {
        sAppContext = context;
        sConfigs = configs;

        if (sConfigs.getGeneralConfig() == null) {
            GeneralConfig generalConfig = new GeneralConfig();
            sConfigs.setGeneralConfig(generalConfig);
        }
        sConfigs.getGeneralConfig().setSDKAppId(sdkAppID);
        String dir = sConfigs.getGeneralConfig().getAppCacheDir();
        if (TextUtils.isEmpty(dir)) {
            TIMLog.e(TAG, "appCacheDir is empty, use default dir");
            sConfigs.getGeneralConfig().setAppCacheDir(context.getFilesDir().getPath());
        } else {
            File file = new File(dir);
            if (file.exists()) {
                if (file.isFile()) {
                    TIMLog.e(TAG, "appCacheDir is a file, use default dir");
                    sConfigs.getGeneralConfig().setAppCacheDir(context.getFilesDir().getPath());
                } else if (!file.canWrite()) {
                    TIMLog.e(TAG, "appCacheDir can not write, use default dir");
                    sConfigs.getGeneralConfig().setAppCacheDir(context.getFilesDir().getPath());
                }
            } else {
                boolean ret = file.mkdirs();
                if (!ret) {
                    TIMLog.e(TAG, "appCacheDir is invalid, use default dir");
                    sConfigs.getGeneralConfig().setAppCacheDir(context.getFilesDir().getPath());
                }
            }
        }
        initTUIKitLive(context);
        initIM(context, sdkAppID);
        BackgroundTasks.initInstance();
        FileUtil.initPath(); // 取决于app什么时候获取到权限，即使在application中初始化，首次安装时，存在获取不到权限，建议app端在activity中再初始化一次，确保文件目录完整创建
        FaceManager.loadFaceFiles();
    }

    public static void login(final String userid, final String usersig, final TIMCallBack callback) {
        TIMConfigs.getConfigs().getGeneralConfig().setUserId(userid);
        TIMConfigs.getConfigs().getGeneralConfig().setUserSig(usersig);
        V2TIMManager.getInstance().login(userid, usersig, new V2TIMCallback() {
            @Override
            public void onError(int code, String desc) {
                callback.onError(TAG, code, desc);
            }

            @Override
            public void onSuccess() {
                loginTUIKitLive(TIMConfigs.getConfigs().getGeneralConfig().getSDKAppId(),
                        userid,
                        usersig);
                callback.onSuccess(null);
            }
        });
    }

    public static void logout(final TIMCallBack callback) {
        V2TIMManager.getInstance().logout(new V2TIMCallback() {
            @Override
            public void onError(int code, String desc) {
                callback.onError(TAG, code, desc);
            }

            @Override
            public void onSuccess() {
                callback.onSuccess(null);
                logoutTUIKitLive();
            }
        });
    }

    private static void initIM(final Context context, int sdkAppID) {
        V2TIMSDKConfig sdkConfig = sConfigs.getSdkConfig();
        if (sdkConfig == null) {
            sdkConfig = new V2TIMSDKConfig();
            sConfigs.setSdkConfig(sdkConfig);
        }
        GeneralConfig generalConfig = sConfigs.getGeneralConfig();
        sdkConfig.setLogLevel(generalConfig.getLogLevel());
        V2TIMManager.getInstance().initSDK(context, sdkAppID, sdkConfig, new V2TIMSDKListener() {
            @Override
            public void onConnecting() {
                // 正在连接到腾讯云服务器
            }

            @Override
            public void onConnectSuccess() {
                // 已经成功连接到腾讯云服务器
                sIMSDKConnected = true;
            }

            @Override
            public void onConnectFailed(int code, String error) {
                sIMSDKConnected = false;
                //连接腾讯云服务器失败
                //可以提示用户当前网络连接不可用。

            }

            @Override
            public void onKickedOffline() {
                //当前用户被踢下线
            }

            @Override
            public void onUserSigExpired() {
                //登录票据已经过期

            }

            @Override
            public void onSelfInfoUpdated(V2TIMUserFullInfo info) {
                //当前用户的资料发生了更新
            }
        });


    }

    public static void unInit() {
        ConversationManagerKit.getInstance().destroyConversation();
        unInitTUIKitLive();
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static TIMConfigs getConfigs() {
        if (sConfigs == null) {
            sConfigs = TIMConfigs.getConfigs();
        }
        return sConfigs;
    }

    public static void addIMEventListener(IMEventListener listener) {
        TIMLog.i(TAG, "addIMEventListener:" + sIMEventListeners.size() + "|l:" + listener);
        if (listener != null && !sIMEventListeners.contains(listener)) {
            sIMEventListeners.add(listener);
        }
    }

    public static void removeIMEventListener(IMEventListener listener) {
        TIMLog.i(TAG, "removeIMEventListener:" + sIMEventListeners.size() + "|l:" + listener);
        if (listener == null) {
            sIMEventListeners.clear();
        } else {
            sIMEventListeners.remove(listener);
        }
    }

    private static void initTUIKitLive(Context context) {
        try {
            Class<?> classz = Class.forName("com.tencent.qcloud.tim.tuikit.live.TUIKitLive");
            Method method = classz.getMethod("init", Context.class);
            method.invoke(null, context);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            TIMLog.e(TAG, "initTUIKitLive error: " + e.getMessage());
        }
    }

    private static void unInitTUIKitLive() {
        try {
            Class<?> classz = Class.forName("com.tencent.qcloud.tim.tuikit.live.TUIKitLive");
            Method method = classz.getMethod("unInit");
            method.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            TIMLog.e(TAG, "unInitTUIKitLive error: " + e.getMessage());
        }
    }

    private static void loginTUIKitLive(int sdkAppid, String userId, String userSig) {
        try {
            Class<?> classz = Class.forName("com.tencent.qcloud.tim.tuikit.live.TUIKitLive");
            Class<?> tClazz = Class.forName("com.tencent.qcloud.tim.tuikit.live.TUIKitLive$LoginCallback");

            // 反射修改isAttachedTUIKit的值
            Field field = classz.getDeclaredField("sIsAttachedTUIKit");
            field.setAccessible(true);
            field.set(null, true);

            Method method = classz.getMethod("login", int.class, String.class, String.class, tClazz);
            method.invoke(null, sdkAppid, userId, userSig, null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            TIMLog.e(TAG, "loginTUIKitLive error: " + e.getMessage());
        }
    }

    private static void logoutTUIKitLive() {
        try {
            Class<?> classz = Class.forName("com.tencent.qcloud.tim.tuikit.live.TUIKitLive");
            Method method = classz.getMethod("logout");
            method.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            TIMLog.e(TAG, "logoutTUIKitLive error: " + e.getMessage());
        }
    }
}
