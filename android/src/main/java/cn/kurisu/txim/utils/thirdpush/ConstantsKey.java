package cn.kurisu.txim.utils.thirdpush;

/**
 * Created by kurisu
 */

public class ConstantsKey {

    // 根据各个厂商的文档，集成离线推送，华为和魅族的sdk是在app的build.gradle中集成，小米和 vivo 是用jar包方式集成。注意：华为还要在project的build.gradle中集成他们的maven仓库；com.huawei 文件夹下的代码是根据华为推送平台集成文档，用华为提供的工具自动生成的
    // thirdpush 包下的 receiver 是各厂商的接收器。
    // ThirdPushTokenMgr 用来保存厂商注册离线推送token的管理类示例，当登陆im后，通过 setOfflinePushToken 上报证书 ID 及设备 token 给im后台。开发者可以根据自己的需求灵活实现
    // 各个厂商都建议在 application 初始化推送，小米和魅族初始化后可以在对应的 receiver 中拿到 token；华为和 vivo 需要再主动调用下获取接口，比如 Demo 是在 loginActivity 的 onCreate 中获取的。

    /****** 华为离线推送参数start ******/
    // 在腾讯云控制台上传第三方推送证书后分配的证书ID
    public static long HW_PUSH_BUZID = 0;
    /****** 华为离线推送参数end ******/

    /****** 小米离线推送参数start ******/
    // 在腾讯云控制台上传第三方推送证书后分配的证书ID
    public static long XM_PUSH_BUZID = 0;
    // 小米开放平台分配的应用APPID及APPKEY
    public static String XM_PUSH_APPID = "";
    public static String XM_PUSH_APPKEY = "";
    /****** 小米离线推送参数end ******/

    /****** 魅族离线推送参数start ******/
    public static long MZ_PUSH_BUZID = 5558;
    // 魅族开放平台分配的应用APPID及APPKEY
    public static String MZ_PUSH_APPID = "118863";
    public static String MZ_PUSH_APPKEY = "d9c7628144e541c1a6446983531467c8";
    /****** 魅族离线推送参数end ******/

    /****** vivo离线推送参数start ******/
    public static long VIVO_PUSH_BUZID = 0;
    // vivo开放平台分配的应用APPID及APPKEY
    public static String VIVO_PUSH_APPID = ""; // 见清单文件
    public static String VIVO_PUSH_APPKEY = ""; // 见清单文件
    /****** vivo离线推送参数end ******/

    /****** oppo ******/
    public static long OPPO_PUSH_BUZID = 0L;

    /****** google ******/
    public static long GOOGLE_FCM_PUSH_BUZID = 0L;
}
