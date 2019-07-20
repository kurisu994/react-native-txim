package cn.kurisu.txim.business;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import cn.kurisu.txim.business.config.BaseConfigs;
import cn.kurisu.txim.business.config.FaceManager;
import cn.kurisu.txim.utils.BackgroundTasks;
import cn.kurisu.txim.utils.FileUtil;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMLogListener;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMSdkConfig;

import java.io.File;


/**
 * @author kurisu
 */
public class InitializeBusiness {

    private static Context appContext;
    private static BaseConfigs baseConfigs;

    /**
     * TUIKit的初始化函数
     *
     * @param context  应用的上下文，一般为对应应用的ApplicationContext
     * @param sdkAppID 您在腾讯云注册应用时分配的sdkAppID
     * @param configs  TUIKit的相关配置项，一般使用默认即可，需特殊配置参考API文档
     */
    public static boolean init(Context context, int sdkAppID, BaseConfigs configs) {
        appContext = context;
        baseConfigs = configs;
        baseConfigs.setAppCacheDir(context.getFilesDir().getPath());
        long current = System.currentTimeMillis();

        boolean init = initIM(context, sdkAppID);
        System.out.println("IMSDK初始化耗时>>>>>>>>>>>>>>>>>>" + (System.currentTimeMillis() - current));

        BackgroundTasks.initInstance();
        FileUtil.initPath(); // 取决于app什么时候获取到权限，即使在application中初始化，首次安装时，存在获取不到权限，建议app端在activity中再初始化一次，确保文件目录完整创建
        FaceManager.loadFaceFiles();
        return init;
    }


    private static boolean initIM(Context context, int sdkAppID) {
        TIMSdkConfig config = getBaseConfigs().getTIMSdkConfig();
        if (config == null) {
            config = new TIMSdkConfig(sdkAppID)
                    .setLogLevel(TIMLogLevel.ERROR)
                    .setLogCallbackLevel(TIMLogLevel.ERROR)
                    .setLogPath(Environment.getExternalStorageDirectory().getPath() + File.separator + context.getPackageName() + File.separator + "TXIM");
            config.setLogListener(new TIMLogListener() {
                @Override
                public void log(int level, String tag, String msg) {
                    Log.e("InitializeBusiness", "test session wrapper jni, msg = " + msg);
                }
            });
            config.enableLogPrint(true);
        }
        return TIMManager.getInstance().init(context, config);
    }


    public static void login(String userid, String usersig, TIMCallBack callBack) {
        TIMManager.getInstance().login(userid, usersig, callBack);
    }


    public static Context getAppContext() {
        return appContext;
    }

    public static BaseConfigs getBaseConfigs() {
        return baseConfigs;
    }
}
