package cn.kurisu.txim.utils.thirdpush;

import com.google.firebase.messaging.FirebaseMessagingService;

import cn.kurisu.txim.utils.TIMLog;

public class GoogleFCMMsgService extends FirebaseMessagingService {
    private final String TAG = GoogleFCMMsgService.class.getSimpleName();

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        TIMLog.i(TAG, "google fcm onNewToken : " + token);

        ThirdPushTokenMgr.getInstance().setThirdPushToken(token);
        ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
    }
}
