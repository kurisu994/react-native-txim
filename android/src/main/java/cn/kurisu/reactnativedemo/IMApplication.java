package cn.kurisu.reactnativedemo;

import android.content.Context;

import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.TIMManager;


public class IMApplication {

    private static Context context;

    public static void pushInit(Context  context) {
        context = IMApplication.context;
        Context finalContext = context;
        TIMManager.getInstance().setOfflinePushListener(notification -> {
            if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify) {
                //消息被设置为需要提醒
                notification.doNotify(finalContext, R.mipmap.ic_launcher);
            }
        });
    }

    public static Context getContext() {
        return context;
    }
}

