package cn.kurisu.txim.listener;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import cn.kurisu.txim.constants.IMEventNameConstant;
import cn.kurisu.txim.module.BaseModule;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.log.QLog;

public class UserStatusListener extends BaseListener implements TIMUserStatusListener {

    public UserStatusListener(BaseModule module) {
        super(module);
    }


    @Override
    public void onForceOffline() {
        //被其他终端踢下线
        QLog.e("用户状态改变", "被其他终端踢下线");
        WritableMap map = Arguments.createMap();
        map.putInt("code", 6208);
        map.putString("msg", "被其他终端踢下线");
        module.sendEvent(IMEventNameConstant.USER_STATUS_CHANGE, map);
    }

    @Override
    public void onUserSigExpired() {
        //用户签名过期了，需要刷新 userSig 重新登录 SDK
        QLog.e("用户状态改变", "onUserSigExpired");
        WritableMap map = Arguments.createMap();
        map.putInt("code", 6206);
        map.putString("msg", "onUserSigExpired");
        module.sendEvent(IMEventNameConstant.USER_STATUS_CHANGE, map);
    }
}
