package cn.kurisu.reactnativedemo;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMManagerExt;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.kurisu.reactnativedemo.business.InitBusiness;
import cn.kurisu.reactnativedemo.business.LoginBusiness;
import cn.kurisu.reactnativedemo.event.MessageEvent;
import cn.kurisu.reactnativedemo.presenter.ChatPresenter;
import cn.kurisu.reactnativedemo.presenter.ConversationPresenter;
import cn.kurisu.reactnativedemo.utils.PushUtil;
import cn.kurisu.reactnativedemo.utils.ReactCache;

import static android.content.Context.NOTIFICATION_SERVICE;
import static cn.kurisu.reactnativedemo.utils.ReactCache.observeRecentContact;


public class TXImModule extends ReactContextBaseJavaModule {
    private static final String TAG = TXImModule.class.getSimpleName();
    private static final String NAME = "TXIm";
    private TIMConversation conversation;
    private static boolean isInit = false;
    private static int pushNum = 0;
    private final int pushId = 1;
    public static ChatPresenter presenter;
    public ConversationPresenter conversationPresenter = new ConversationPresenter();

    public TXImModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Map<String, Object> getConstants() {
        //让js那边能够使用这些常量
        Map<String, Object> constants = new HashMap<>();
        constants.put(TIMLogLevel.OFF.getDescr(), TIMLogLevel.OFF.getIntLevel());
        constants.put(TIMLogLevel.DEBUG.getDescr(), TIMLogLevel.DEBUG.getIntLevel());
        constants.put(TIMLogLevel.INFO.getDescr(), TIMLogLevel.INFO.getIntLevel());
        constants.put(TIMLogLevel.WARN.getDescr(), TIMLogLevel.WARN.getIntLevel());
        constants.put(TIMLogLevel.ERROR.getDescr(), TIMLogLevel.ERROR.getIntLevel());
        return constants;
    }
    /********************************初始化 登陆***************************************************/
    /**
     * 初始化SDK
     *
     * @param logLevel 日志打印等级
     */
    @ReactMethod
    public void init(int logLevel, Promise promise) {
        clearNotification();
        if (isInit) {
            promise.resolve(true);
            return;
        }
        Context context = getReactApplicationContext().getApplicationContext();
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            int appid = info.metaData.getInt("IM_APPID");
            int type = info.metaData.getInt("IM_ACCOUNT_TYPE");
            //初始化用户配置
            InitBusiness instance = InitBusiness.getInstance();
            instance.initUserConfig();
            //初始化IMSDK
            boolean b = instance.initImsdk(appid, context, logLevel);
            if (b) {
                isInit = true;
                promise.resolve(true);
            } else {
                promise.reject("-1", "初始化失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject("-1", e.getMessage());
        }
    }

    /**
     * 登陆
     *
     * @param identify 用户账号
     * @param userSig  用户签名
     * @param promise  异步方法
     */
    @ReactMethod
    public void login(String identify, String userSig, Promise promise) {
        Context context = getReactApplicationContext().getApplicationContext();
        if (identify.equals("") || userSig.equals("")) {
            promise.reject("70002", "用户名或者签名不能为空");
            return;
        }
        String loginUser = TIMManager.getInstance().getLoginUser();
        if (identify.equals(loginUser)) {
            promise.resolve(true);
            return;
        }
        LoginBusiness.loginIm(identify, userSig, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                switch (i) {
                    case 6208:
                        //离线状态下被其他终端踢下线
                        show("离线状态下被其他终端踢下线");
                        break;
                    case 6200:
                        Toast.makeText(context, context.getString(R.string.login_error_timeout), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(context, context.getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                        break;
                }
                promise.reject(String.valueOf(i), s);
            }

            @Override
            public void onSuccess() {
                String deviceMan = android.os.Build.MANUFACTURER;
                //初始化程序后台后消息推送
                PushUtil.getInstance();
                MessageEvent.getInstance();
                //注册小米和华为推送
                if (deviceMan.equals("Xiaomi") && shouldMiInit()) {
                    MiPushClient.registerPush(getReactApplicationContext().getApplicationContext(), "2882303761517480335", "5411748055335");
                } else if (deviceMan.equals("HUAWEI")) {
                    PushManager.requestToken(getReactApplicationContext().getApplicationContext());
                }
                //魅族推送只适用于Flyme系统,因此可以先行判断是否为魅族机型，再进行订阅，避免在其他机型上出现兼容性问题
                if (MzSystemUtils.isBrandMeizu(getReactApplicationContext().getApplicationContext())) {
                    com.meizu.cloud.pushsdk.PushManager.register(getReactApplicationContext().getApplicationContext(), "112662", "3aaf89f8e13f43d2a4f97a703c6f65b3");
                }
                promise.resolve(true);
            }
        });
    }

    @ReactMethod
    public void logout(Promise promise) {
        LoginBusiness.logout(new TIMCallBack() {
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


    /********************************消息发送****************************************/
    /**
     * 最近会话列表
     * 初始化或者刷新
     */
    @ReactMethod
    public void initConversationList() {
        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
        for (TIMConversation conversation : list) {
            if (conversation.getType() == TIMConversationType.System) continue;
            TIMConversationExt conversationExt = new TIMConversationExt(conversation);
            conversationExt.getLocalMessage(10, null, new TIMValueCallBack<List<TIMMessage>>() {
                @Override
                public void onError(int i, String s) {
                }

                @Override
                public void onSuccess(List<TIMMessage> timMessages) {
                    WritableMap map = ReactCache.createMessage(timMessages);
                    String peer = conversation.getPeer();
                    TIMFriendshipManager.getInstance().getUsersProfile(Collections.singletonList(peer), new TIMValueCallBack<List<TIMUserProfile>>() {
                        @Override
                        public void onError(int i, String s) {

                        }

                        @Override
                        public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                            String avatar = timUserProfiles.get(0).getFaceUrl();
                            String nickName = timUserProfiles.get(0).getNickName();
                            String account = timUserProfiles.get(0).getIdentifier();
                            map.putString("from_avatar", avatar);
                            map.putString("from_nickName", nickName);
                            map.putString("from_account", account);
                            //todo
                            sendEvent(observeRecentContact, map);
                        }
                    });
                }
            });
        }
    }


    /**
     * 新建会话
     *
     * @param type
     * @param peer
     */
    @ReactMethod
    public void getConversation(int type, String peer, Promise promise) {
        try {
            presenter = new ChatPresenter(peer, TIMConversationType.values()[type]);
            presenter.start();
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("-1", "新建会话失败");
        }
    }

    /**
     * 注销会话
     */
    @ReactMethod
    public void destroyConversation() {
        presenter.stop();
        presenter = null;
    }

    @ReactMethod
    public void sendTextMsg(@NonNull String text, Promise promise) {
        //构造一条消息
        TIMMessage msg = new TIMMessage();
        //添加文本内容
        TIMTextElem elem = new TIMTextElem();
        elem.setText(text);
        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            promise.reject("-1", "消息发送失败，请重试");
            return;
        }
        presenter.sendMessage(msg, promise);
    }

    @ReactMethod
    public void show(String msg) {
        Toast.makeText(getReactApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * 发送事件
     *
     * @param eventName
     * @param params
     */
    public void sendEvent(String eventName, @Nullable WritableMap params) {
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    @Override
    public boolean canOverrideExistingModule() {
        return true;
    }

    /**
     * 判断小米推送是否已经初始化
     */
    private boolean shouldMiInit() {
//        ActivityManager am = ((ActivityManager) getReactApplicationContext().getSystemService(Context.ACTIVITY_SERVICE));
//        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
//        String mainProcessName = getPackageName();
//        int myPid = android.os.Process.myPid();
//        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
//            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
//                return true;
//            }
//        }
        return false;
    }


    public static void resetPushNum() {
        pushNum = 0;
    }

    public void reset() {
        NotificationManager notificationManager = (NotificationManager) IMApplication.getContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(pushId);
    }

    /**
     * 清楚所有通知栏通知
     */
    private static void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) IMApplication.getContext()
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        MiPushClient.clearNotification(IMApplication.getContext());
    }
}
