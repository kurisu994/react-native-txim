package cn.kurisu.txim.utils.thirdpush;

import android.text.TextUtils;

import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMOfflinePushConfig;

import cn.kurisu.txim.utils.BrandUtil;
import cn.kurisu.txim.utils.TIMLog;

/**
 * 用来保存厂商注册离线推送token的管理类示例，当登陆im后，通过 setOfflinePushToken 上报证书 ID 及设备 token 给im后台。开发者可以根据自己的需求灵活实现
 */

public class ThirdPushTokenMgr {

    public static final boolean USER_GOOGLE_FCM = false;
    private static final String TAG = ThirdPushTokenMgr.class.getSimpleName();
    private String mThirdPushToken;

    public static ThirdPushTokenMgr getInstance() {
        return ThirdPushTokenHolder.instance;
    }

    public String getThirdPushToken() {
        return mThirdPushToken;
    }

    public void setThirdPushToken(String mThirdPushToken) {
        this.mThirdPushToken = mThirdPushToken;
    }

    public void setPushTokenToTIM() {
        String token = ThirdPushTokenMgr.getInstance().getThirdPushToken();
        if (TextUtils.isEmpty(token)) {
            TIMLog.i(TAG, "setPushTokenToTIM third token is empty");
            return;
        }
        V2TIMOfflinePushConfig v2TIMOfflinePushConfig = null;
        if (BrandUtil.isBrandXiaoMi()) {
            v2TIMOfflinePushConfig = new V2TIMOfflinePushConfig(ConstantsKey.XM_PUSH_BUZID, token);
        } else if (BrandUtil.isBrandHuawei()) {
            v2TIMOfflinePushConfig = new V2TIMOfflinePushConfig(ConstantsKey.HW_PUSH_BUZID, token);
        } else if (BrandUtil.isBrandMeizu()) {
            v2TIMOfflinePushConfig = new V2TIMOfflinePushConfig(ConstantsKey.MZ_PUSH_BUZID, token);
        } else if (BrandUtil.isBrandOppo()) {
            v2TIMOfflinePushConfig = new V2TIMOfflinePushConfig(ConstantsKey.OPPO_PUSH_BUZID, token);
        } else if (BrandUtil.isBrandVivo()) {
            v2TIMOfflinePushConfig = new V2TIMOfflinePushConfig(ConstantsKey.VIVO_PUSH_BUZID, token);
        } else {
            v2TIMOfflinePushConfig = new V2TIMOfflinePushConfig(ConstantsKey.GOOGLE_FCM_PUSH_BUZID, token);
        }

        V2TIMManager.getOfflinePushManager().setOfflinePushConfig(v2TIMOfflinePushConfig, new V2TIMCallback() {
            @Override
            public void onError(int code, String desc) {
                TIMLog.d(TAG, "setOfflinePushToken err code = " + code);
            }

            @Override
            public void onSuccess() {
                TIMLog.d(TAG, "setOfflinePushToken success");
            }
        });
    }

    private static class ThirdPushTokenHolder {
        private static final ThirdPushTokenMgr instance = new ThirdPushTokenMgr();
    }
}
