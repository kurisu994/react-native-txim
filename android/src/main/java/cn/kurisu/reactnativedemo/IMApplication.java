package cn.kurisu.reactnativedemo;

import android.content.Context;

import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.TIMManager;


public class IMApplication {

    private static  Context context;

    private static Class mainActivityClass;

    public static void pushInit(final Context context, Class mainActivityClass) {
        IMApplication.context = context.getApplicationContext();
        IMApplication.mainActivityClass = mainActivityClass;
        TIMManager.getInstance().setOfflinePushListener(notification -> {
            if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify) {
                //消息被设置为需要提醒
                notification.doNotify(context.getApplicationContext(), R.mipmap.ic_launcher);
            }
        });
    }

    public static Context getContext() {
        return context;
    }

    public static Class getMainActivityClass() {
        return mainActivityClass;
    }
}

