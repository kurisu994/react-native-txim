package cn.kurisu.txim.module;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import cn.kurisu.huawei.android.hms.agent.HMSAgent;
import cn.kurisu.huawei.android.hms.agent.common.handler.ConnectHandler;
import cn.kurisu.huawei.android.hms.agent.push.handler.GetTokenHandler;
import cn.kurisu.txim.IMApplication;
import cn.kurisu.txim.constants.IMEventNameConstant;
import cn.kurisu.txim.R;
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
import cn.kurisu.txim.utils.messageUtils.MessageInfo;
import cn.kurisu.txim.utils.thirdpush.ConstantsKey;
import cn.kurisu.txim.utils.thirdpush.ThirdPushTokenMgr;
import com.meizu.cloud.pushsdk.PushManager;
import com.meizu.cloud.pushsdk.util.MzSystemUtils;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMOfflinePushSettings;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.log.QLog;
import com.tencent.imsdk.session.SessionWrapper;
import com.tencent.imsdk.utils.IMFunc;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;


/**
 * @author kurisu
 */

public class InitializeModule extends BaseModule {

    private Context context;

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

    public void init(int logLevel) {
        WritableMap map = Arguments.createMap();
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            int appid = info.metaData.getInt("IM_APPID");
            this.setPushKey(info);
            boolean init = InitializeBusiness.init(context, appid, BaseConfigs.getDefaultConfigs());
            //添加自定初始化配置
            customConfig();
            setPushConfig();

            if(IMFunc.isBrandHuawei()){
                // 华为离线推送
                HMSAgent.connect(getCurrentActivity(), new ConnectHandler() {
                    @Override
                    public void onConnect(int rst) {
                        QLog.i("huaweipush", "HMS connect end:" + rst);
                    }
                });
                getHuaWeiPushToken();
            }
            if(IMFunc.isBrandVivo()){
                // vivo离线推送
                PushClient.getInstance(IMApplication.getContext()).turnOnPush(new IPushActionListener() {
                    @Override
                    public void onStateChanged(int state) {
                        if(state == 0){
                            String regId = PushClient.getInstance(IMApplication.getContext()).getRegId();
                            QLog.i("vivopush", "open vivo push success regId = " + regId);
                            ThirdPushTokenMgr.getInstance().setThirdPushToken(regId);
                            ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
                        }else {
                            // 根据vivo推送文档说明，state = 101 表示该vivo机型或者版本不支持vivo推送，链接：https://dev.vivo.com.cn/documentCenter/doc/156
                            QLog.i("vivopush", "open vivo push fail state = " + state);
                        }
                    }
                });
            }

            QLog.i("初始化", "结果：" + init);
            if (init) {
                map.putInt("code", 0);
                map.putString("msg", "IM初始化成功");
                sendEvent(IMEventNameConstant.INITIALIZE_STATUS, map);
            } else {
                map.putInt("code", -1);
                map.putString("msg", "IM初始化失败: 未知错误");
                sendEvent(IMEventNameConstant.INITIALIZE_STATUS, map);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            map.putInt("code", -1);
            map.putString("msg", e.getMessage());
            sendEvent(IMEventNameConstant.INITIALIZE_STATUS, map);

        } catch (Exception ex) {
            ex.printStackTrace();
            map.putInt("code", -1);
            map.putString("msg", ex.getMessage());
            sendEvent(IMEventNameConstant.INITIALIZE_STATUS, map);
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
            userConfig.enableReadReceipt(true);
            userConfig.disableStorage();
            
            TIMManager.getInstance().setUserConfig(userConfig);
        }
    }

    private ArrayList<CustomFaceGroupConfigs> initCustomConfig() {
        ArrayList<CustomFaceGroupConfigs> groupFaces = new ArrayList<>();
        //创建一个表情组对象
        CustomFaceGroupConfigs faceConfigs = new CustomFaceGroupConfigs();
        //设置表情组每页可显示的表情列数
        faceConfigs.setPageColumnCount(5);
        //设置表情组每页可显示的表情行数
        faceConfigs.setPageRowCount(2);
        //设置表情组号
        faceConfigs.setFaceGroupId(1);
        //设置表情组的主ICON
        faceConfigs.setFaceIconPath("4349/xx07@2x.png");
        //设置表情组的名称
        faceConfigs.setFaceIconName("4350");
        for (int i = 1; i <= 15; i++) {
            //创建一个表情对象
            FaceConfig faceConfig = new FaceConfig();
            String index = "" + i;
            if (i < 10)
                index = "0" + i;
            //设置表情所在Asset目录下的路径
            faceConfig.setAssetPath("4349/xx" + index + "@2x.png");
            //设置表情所名称
            faceConfig.setFaceName("xx" + index + "@2x");
            //设置表情宽度
            faceConfig.setFaceWidth(240);
            //设置表情高度
            faceConfig.setFaceHeight(240);
            faceConfigs.addFaceConfig(faceConfig);
        }
        groupFaces.add(faceConfigs);


        faceConfigs = new CustomFaceGroupConfigs();
        faceConfigs.setPageColumnCount(5);
        faceConfigs.setPageRowCount(2);
        faceConfigs.setFaceGroupId(1);
        faceConfigs.setFaceIconPath("4350/tt01@2x.png");
        faceConfigs.setFaceIconName("4350");
        for (int i = 1; i <= 16; i++) {
            FaceConfig faceConfig = new FaceConfig();
            String index = "" + i;
            if (i < 10)
                index = "0" + i;
            faceConfig.setAssetPath("4350/tt" + index + "@2x.png");
            faceConfig.setFaceName("tt" + index + "@2x");
            faceConfig.setFaceWidth(240);
            faceConfig.setFaceHeight(240);
            faceConfigs.addFaceConfig(faceConfig);
        }
        groupFaces.add(faceConfigs);


        return groupFaces;
    }


    private void setPushConfig() {
        if (SessionWrapper.isMainProcess(context)) {
            TIMManager.getInstance().setOfflinePushListener(notification -> {
                //消息被设置为需要提醒
                notification.doNotify(context.getApplicationContext(), R.drawable.fw_ic_launcher);
            });

            if (IMFunc.isBrandXiaoMi()) {
                // 小米离线推送
                MiPushClient.registerPush(context, ConstantsKey.XM_PUSH_APPID, ConstantsKey.XM_PUSH_APPKEY);
            }
            if (IMFunc.isBrandHuawei()) {
                // 华为离线推送
                HMSAgent.init((Application) IMApplication.getContext());
            }
            if (MzSystemUtils.isBrandMeizu(context)) {
                // 魅族离线推送
                PushManager.register(context, ConstantsKey.MZ_PUSH_APPID, ConstantsKey.MZ_PUSH_APPKEY);
            }
            if (IMFunc.isBrandVivo()) {
                // vivo离线推送
                PushClient.getInstance(IMApplication.getContext()).initialize();
            }
        }
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

    private void getHuaWeiPushToken() {
        HMSAgent.Push.getToken(new GetTokenHandler() {
            @Override
            public void onResult(int rtnCode) {
                QLog.i("huaweipush", "get token: end" + rtnCode);
            }
        });
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
}
