package cn.kurisu.reactnativedemo;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.huawei.android.pushagent.PushManager;
import com.meizu.cloud.pushsdk.util.MzSystemUtils;
import com.tencent.cos.utils.StringUtils;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
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
import cn.kurisu.reactnativedemo.business.TLSConfiguration;
import cn.kurisu.reactnativedemo.event.MessageEvent;
import cn.kurisu.reactnativedemo.model.Message;
import cn.kurisu.reactnativedemo.model.MessageFactory;
import cn.kurisu.reactnativedemo.utils.Foreground;
import cn.kurisu.reactnativedemo.utils.ReactCache;

import static android.content.Context.NOTIFICATION_SERVICE;
import static cn.kurisu.reactnativedemo.utils.ReactCache.observeRecentContact;
import static com.tencent.open.utils.Global.getPackageName;


public class TXImModule extends ReactContextBaseJavaModule {
    private static final String TAG = TXImModule.class.getSimpleName();
    private static final String NAME = "TXIm";
    private TIMConversation conversation;
    private static int pushNum = 0;
    private final int pushId = 1;

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
     * @param appId       appid
     * @param accountType accountType
     * @param logLevel    日志打印等级
     */
    @ReactMethod
    public void init(int appId, int accountType, int logLevel) {
        clearNotification();
        Context context = getReactApplicationContext().getApplicationContext();
        SharedPreferences pref = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        int loglvl = pref.getInt("loglvl", logLevel);
        //初始化IMSDK
        InitBusiness.getInstance().initImsdk(appId, context, loglvl);
        TLSConfiguration.setSdkAppid(appId);
        TLSConfiguration.setAccountType(accountType);
        //初始化TLS
        InitBusiness.initTlsSdk(context);
        //初始化用户配置
        InitBusiness.getInstance().initUserConfig();
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
        if (StringUtils.getEmptyString(identify).equals("") || StringUtils.getEmptyString(userSig).equals("")) {
            promise.reject("70002", "用户名或者签名不能为空");
            return;
        }
        String loginUser = TIMManager.getInstance().getLoginUser();
        if (identify.equals(loginUser)) {
            promise.resolve("0");
            return;
        }
        LoginBusiness.loginIm(identify, userSig, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                promise.reject(String.valueOf(i), s);
            }

            @Override
            public void onSuccess() {
                //初始化消息监听
                MessageEvent.getInstance();
                String deviceMan = android.os.Build.MANUFACTURER;
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
                promise.resolve("0");
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
                promise.resolve("0");
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


    @ReactMethod
    public void getConversation(int type, String peer) {
        this.conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.values()[type],    //会话类型：1单聊 2群聊
                peer);
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
        //发送消息
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 含义请参见错误码表
                promise.reject(String.valueOf(code), desc);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                promise.resolve(0);
            }
        });
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
        ActivityManager am = ((ActivityManager) getReactApplicationContext().getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 清楚所有通知栏通知
     */
    private void clearNotification() {
        Context context = getReactApplicationContext().getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        MiPushClient.clearNotification(context);
    }

    private void PushNotify(TIMMessage msg) {
        //系统消息，自己发的消息，程序在前台的时候不通知
        if (msg == null || Foreground.get().isForeground() ||
                (msg.getConversation().getType() != TIMConversationType.Group &&
                        msg.getConversation().getType() != TIMConversationType.C2C) ||
                msg.isSelf() ||
                msg.getRecvFlag() == TIMGroupReceiveMessageOpt.ReceiveNotNotify) return;

        String senderStr, contentStr;
        Message message = MessageFactory.getMessage(msg);
        if (message == null) return;
        senderStr = message.getSender();
        contentStr = message.getSummary();
        Log.d(TAG, "recv msg " + contentStr);
        NotificationManager mNotificationManager = (NotificationManager) MainApplication.getContext().getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainApplication.getContext());
        Intent notificationIntent = getReactApplicationContext().getApplicationContext().getPackageManager().getLaunchIntentForPackage(getReactApplicationContext().getApplicationContext().getPackageName());
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(MainApplication.getContext(), 0,
                notificationIntent, 0);
        mBuilder.setContentTitle(senderStr)//设置通知栏标题
                .setContentText(contentStr)
                .setContentIntent(intent) //设置通知栏点击意图
                .setNumber(++pushNum) //设置通知集合的数量
                .setTicker(senderStr + ":" + contentStr) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
        Notification notify = mBuilder.build();
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(pushId, notify);
        //TODO
    }

    public static void resetPushNum() {
        pushNum = 0;
    }

    public void reset() {
        NotificationManager notificationManager = (NotificationManager) MainApplication.getContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(pushId);
    }

}
