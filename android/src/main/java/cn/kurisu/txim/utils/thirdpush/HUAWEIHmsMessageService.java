package cn.kurisu.txim.utils.thirdpush;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

import cn.kurisu.txim.utils.BrandUtil;
import cn.kurisu.txim.utils.TIMLog;

public class HUAWEIHmsMessageService extends HmsMessageService {

    private static final String TAG = HUAWEIHmsMessageService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage message) {
        TIMLog.i(TAG, "onMessageReceived message=" + message);
    }

    @Override
    public void onMessageSent(String msgId) {
        TIMLog.i(TAG, "onMessageSent msgId=" + msgId);
    }

    @Override
    public void onSendError(String msgId, Exception exception) {
        TIMLog.i(TAG, "onSendError msgId=" + msgId);
    }

    @Override
    public void onNewToken(String token) {
        TIMLog.i(TAG, "onNewToken token=" + token);
        ThirdPushTokenMgr.getInstance().setThirdPushToken(token);
        ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
    }

    @Override
    public void onTokenError(Exception exception) {
        TIMLog.i(TAG, "onTokenError exception=" + exception);
    }

    @Override
    public void onMessageDelivered(String msgId, Exception exception) {
        TIMLog.i(TAG, "onMessageDelivered msgId=" + msgId);
    }


    public static void updateBadge(final Context context, final int number) {
        if (!BrandUtil.isBrandHuawei()) {
            return;
        }
        TIMLog.i(TAG, "huawei badge = " + number);
        try {//todo
            Bundle extra = new Bundle();
            extra.putString("package", "com.tencent.qcloud.tim.tuikit");
            extra.putString("class", "com.tencent.qcloud.tim.demo.SplashActivity");
            extra.putInt("badgenumber", number);
            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, extra);
        } catch (Exception e) {
            TIMLog.w(TAG, "huawei badge exception: " + e.getLocalizedMessage());
        }
    }
}
