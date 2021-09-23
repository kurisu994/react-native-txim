package cn.kurisu.txim.module;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.multidex.MultiDex;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import cn.kurisu.txim.business.conversation.ConversationManagerKit;
import cn.kurisu.txim.configs.GeneralConfig;
import cn.kurisu.txim.configs.TIMConfigs;
import cn.kurisu.txim.constants.IMEventNameConstant;
import cn.kurisu.txim.business.config.BaseConfigs;
import cn.kurisu.txim.business.InitializeBusiness;
import cn.kurisu.txim.business.config.CustomFaceGroupConfigs;
import cn.kurisu.txim.business.config.FaceConfig;
import cn.kurisu.txim.listener.ConnListener;
import cn.kurisu.txim.listener.GroupEventListener;
import cn.kurisu.txim.listener.MessageEventListener;
import cn.kurisu.txim.listener.MessageRevokedListener;
import cn.kurisu.txim.listener.RefreshListener;
import cn.kurisu.txim.listener.UserStatusListener;
import cn.kurisu.txim.pojo.ResultVO;
import cn.kurisu.txim.utils.BrandUtil;
import cn.kurisu.txim.utils.TIMInitUtil;
import cn.kurisu.txim.utils.TIMLog;
import cn.kurisu.txim.utils.messageUtils.MessageInfo;
import cn.kurisu.txim.utils.thirdpush.ConstantsKey;
import cn.kurisu.txim.utils.thirdpush.ThirdPushTokenMgr;

import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.heytap.msp.push.HeytapPushManager;
import com.huawei.hms.push.HmsMessaging;
import com.meizu.cloud.pushsdk.PushManager;
import com.meizu.cloud.pushsdk.util.MzSystemUtils;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMSDKConfig;
import com.vivo.push.PushClient;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;


/**
 * @author kurisu
 */

public class InitializeModule extends BaseModule {

    private static final String TAG = "InitializeModule";

    private final ReactApplicationContext context;

    public InitializeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    @Nonnull
    @Override
    public String getName() {
        return "IMInitializeModule";
    }

    /**
     * 用户在向自己的业务服务器登录完成后，在成功的回调中进行im的登录
     */
    @ReactMethod
    private void imLogin(String account, String userSig) {
        final WritableMap map = Arguments.createMap();
        InitializeBusiness.login(account, userSig, new TIMCallBack() {

            @Override
            public void onError(int errCode, String errMsg) {
                //错误码 errCode 和错误描述 errMsg，可用于定位请求失败原因
                //错误码 errCode 列表请参见错误码表
                map.putInt("code", errCode);
                map.putString("msg", errMsg);

                Toast.makeText(getReactApplicationContext(), String.valueOf(errCode), Toast.LENGTH_SHORT).show();
                QLog.e("登录信息", "code:" + errCode + "    " + "msg:" + errMsg);
                sendEvent(IMEventNameConstant.LOGIN_STATUS, map);
            }

            @Override
            public void onSuccess() {
                map.putInt("code", 0);
                map.putString("msg", "登录成功!");
                TIMOfflinePushSettings settings = new TIMOfflinePushSettings();
                settings.setEnabled(true);
                TIMManager.getInstance().setOfflinePushSettings(settings);

                ThirdPushTokenMgr.getInstance().setIsLogin(true);
                ThirdPushTokenMgr.getInstance().setPushTokenToTIM();

                Toast.makeText(getReactApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                sendEvent(IMEventNameConstant.LOGIN_STATUS, map);
            }
        });
    }

    /**
     * 退出登录
     *
     * @param promise
     */
    @ReactMethod
    public void logout(Promise promise) {
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                promise.reject(String.valueOf(i), s);
            }

            @Override
            public void onSuccess() {
                promise.resolve(true);
            }
        });
    }

    /**
     * 初始化函数
     *
     */

    public void init(int logLevel, Promise promise) {
        try {
            MultiDex.install(context);
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            setPushKey(info);
            int im_appidd = info.metaData.getInt("IM_APPID");
            TIMConfigs configs = TIMConfigs.getConfigs();
            V2TIMSDKConfig sdkConfig = new V2TIMSDKConfig();
            sdkConfig.setLogLevel(logLevel);
            configs.setSdkConfig(sdkConfig);
            TIMInitUtil.init(context, im_appidd, configs);
            HeytapPushManager.init(this.getReactApplicationContext(), true);
            //添加自定初始化配置
            customConfig();
            setPushConfig();
            promise.resolve(new ResultVO(1000, "Initialization Succeed"));

        } catch (Exception e) {
            promise.reject("0", e.getMessage(), e);
        }
    }

    private void customConfig() {
        if (InitializeBusiness.getBaseConfigs() != null) {
            TIMUserConfig userConfig = new TIMUserConfig();
            //用户状态改变监听
            userConfig.setUserStatusListener(new UserStatusListener(this));
            //连接状态改变监听
            userConfig.setConnectionListener(new ConnListener(this));
            //消息刷新的监听
            userConfig.setRefreshListener(new RefreshListener(this));
            //收到群消息的监听
            userConfig.setGroupEventListener(new GroupEventListener(this));
            //收到消息的监听
            TIMManager.getInstance().addMessageListener(new MessageEventListener(this));
            userConfig.setMessageRevokedListener(new MessageRevokedListener(this));
            userConfig.disableAutoReport(false);
            //开启消息已读回执
            userConfig.enableReadReceipt(true);

            TIMManager.getInstance().setUserConfig(userConfig);
        }
    }


    private void setPushConfig() {
        if (BrandUtil.isBrandXiaoMi()) {
            // 小米离线推送
            MiPushClient.registerPush(this.getReactApplicationContext(), ConstantsKey.XM_PUSH_APPID, ConstantsKey.XM_PUSH_APPKEY);
        } else if (BrandUtil.isBrandHuawei()) {
            // 华为离线推送，设置是否接收Push通知栏消息调用示例
            HmsMessaging.getInstance(this.getReactApplicationContext()).turnOnPush().addOnCompleteListener(new com.huawei.hmf.tasks.OnCompleteListener<Void>() {
                @Override
                public void onComplete(com.huawei.hmf.tasks.Task<Void> task) {
                    if (task.isSuccessful()) {
                        TIMLog.i(TAG, "huawei turnOnPush Complete");
                    } else {
                        TIMLog.e(TAG, "huawei turnOnPush failed: ret=" + task.getException().getMessage());
                    }
                }
            });
        } else if (MzSystemUtils.isBrandMeizu(this.getReactApplicationContext())) {
            // 魅族离线推送
            PushManager.register(this.getReactApplicationContext(), ConstantsKey.MZ_PUSH_APPID, ConstantsKey.MZ_PUSH_APPKEY);
        } else if (BrandUtil.isBrandVivo()) {
            // vivo离线推送
            PushClient.getInstance(this.getReactApplicationContext()).initialize();
        } else if (HeytapPushManager.isSupportPush()) {
            // oppo离线推送，因为需要登录成功后向我们后台设置token，所以注册放在MainActivity中做
        } else if (BrandUtil.isGoogleServiceSupport(this.getReactApplicationContext())) {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                TIMLog.w(TAG, "getInstanceId failed exception = " + task.getException());
                                return;
                            }
                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            TIMLog.i(TAG, "google fcm getToken = " + token);

                            ThirdPushTokenMgr.getInstance().setThirdPushToken(token);
                        }
                    });
        };
    }

    private void setPushKey(ApplicationInfo info) {
        ConstantsKey.XM_PUSH_BUZID = info.metaData.getInt("XM_PUSH_BUZID", 0);
        ConstantsKey.XM_PUSH_APPID = info.metaData.getString("XM_PUSH_APPID", "").trim();
        ConstantsKey.XM_PUSH_APPKEY = info.metaData.getString("XM_PUSH_APPKEY", "").trim();

        ConstantsKey.HW_PUSH_BUZID = info.metaData.getInt("HW_PUSH_BUZID", 0);

        ConstantsKey.VIVO_PUSH_BUZID = info.metaData.getInt("VIVO_PUSH_BUZID", 0);

        ConstantsKey.MZ_PUSH_APPID = info.metaData.getString("MZ_PUSH_APPID", "").trim();
        ConstantsKey.MZ_PUSH_APPKEY = info.metaData.getString("MZ_PUSH_APPKEY", "").trim();
        ConstantsKey.MZ_PUSH_BUZID = info.metaData.getInt("MZ_PUSH_BUZID", 0);
    }


    @Override
    public Map<String, Object> getConstants() {
        //让js那边能够使用这些常量
        // 事件名称
        Map<String, Object> constants = new HashMap<>();
        constants.put("userStatus", IMEventNameConstant.USER_STATUS_CHANGE);
        constants.put("initializeStatus", IMEventNameConstant.INITIALIZE_STATUS);
        constants.put("loginStatus", IMEventNameConstant.LOGIN_STATUS);
        constants.put("onNewMessage", IMEventNameConstant.ON_NEW_MESSAGE);
        constants.put("sendStatus", IMEventNameConstant.SEND_STATUS);
        constants.put("conversationStatus", IMEventNameConstant.CONVERSATION_STATUS);
        constants.put("conversationListStatus", IMEventNameConstant.CONVERSATION_LIST_STATUS);
        constants.put("onConversationRefresh", IMEventNameConstant.ON_CONVERSATION_REFRESH);
        constants.put("onMessageQuery", IMEventNameConstant.ON_MESSAGE_QUERY);

        //消息类型
        constants.put("Text", MessageInfo.MSG_TYPE_TEXT);
        constants.put("Image", MessageInfo.MSG_TYPE_IMAGE);
        constants.put("Sound", MessageInfo.MSG_TYPE_AUDIO);
        constants.put("Video", MessageInfo.MSG_TYPE_VIDEO);
        constants.put("File", MessageInfo.MSG_TYPE_FILE);
        constants.put("Location", MessageInfo.MSG_TYPE_LOCATION);
        constants.put("Face", MessageInfo.MSG_TYPE_CUSTOM_FACE);
        constants.put("Custom", MessageInfo.MSG_TYPE_CUSTOM);
        return constants;
    }


    public TIMConfigs getConfigs() {
        GeneralConfig config = new GeneralConfig();
        // 显示对方是否已读的view将会展示
        config.setShowRead(true);
        config.setAppCacheDir(getReactApplicationContext().getFilesDir().getPath());
        if (new File(Environment.getExternalStorageDirectory() + "/TXIM").exists()) {
            config.setTestEnv(true);
        }
        TIMInitUtil.getConfigs().setGeneralConfig(config);
        TIMInitUtil.getConfigs().setCustomFaceConfig(initCustomFaceConfig());
        return TIMInitUtil.getConfigs();
    }

    class StatisticActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {
        private int foregroundActivities = 0;
        private boolean isChangingConfiguration;
        private IMEventListener mIMEventListener = new IMEventListener() {
            @Override
            public void onNewMessage(V2TIMMessage msg) {
                String imSdkVersion = V2TIMManager.getInstance().getVersion();
                // IMSDK 5.0.1及以后版本 doBackground 之后同时会离线推送
                if (TUIKitUtils.compareVersion(imSdkVersion, "5.0.1") < 0) {
                    MessageNotification notification = MessageNotification.getInstance();
                    notification.notify(msg);
                }
            }
        };

        private ConversationManagerKit.MessageUnreadWatcher mUnreadWatcher = new ConversationManagerKit.MessageUnreadWatcher() {
            @Override
            public void updateUnread(int count) {
                // 华为离线推送角标
                HUAWEIHmsMessageService.updateBadge(DemoApplication.this, count);
            }
        };

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            DemoLog.i(TAG, "onActivityCreated bundle: " + bundle);
            if (bundle != null) { // 若bundle不为空则程序异常结束
                // 重启整个程序
                Intent intent = new Intent(activity, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            foregroundActivities++;
            if (foregroundActivities == 1 && !isChangingConfiguration) {
                // 应用切到前台
                DemoLog.i(TAG, "application enter foreground");
                V2TIMManager.getOfflinePushManager().doForeground(new V2TIMCallback() {
                    @Override
                    public void onError(int code, String desc) {
                        DemoLog.e(TAG, "doForeground err = " + code + ", desc = " + desc);
                    }

                    @Override
                    public void onSuccess() {
                        DemoLog.i(TAG, "doForeground success");
                    }
                });
                TUIKit.removeIMEventListener(mIMEventListener);
                ConversationManagerKit.getInstance().removeUnreadWatcher(mUnreadWatcher);
                MessageNotification.getInstance().cancelTimeout();
            }
            isChangingConfiguration = false;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            foregroundActivities--;
            if (foregroundActivities == 0) {
                // 应用切到后台
                DemoLog.i(TAG, "application enter background");
                int unReadCount = ConversationManagerKit.getInstance().getUnreadTotal();
                V2TIMManager.getOfflinePushManager().doBackground(unReadCount, new V2TIMCallback() {
                    @Override
                    public void onError(int code, String desc) {
                        DemoLog.e(TAG, "doBackground err = " + code + ", desc = " + desc);
                    }

                    @Override
                    public void onSuccess() {
                        DemoLog.i(TAG, "doBackground success");
                    }
                });
                // 应用退到后台，消息转化为系统通知
                TUIKit.addIMEventListener(mIMEventListener);
                ConversationManagerKit.getInstance().addUnreadWatcher(mUnreadWatcher);
            }
            isChangingConfiguration = activity.isChangingConfigurations();
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
