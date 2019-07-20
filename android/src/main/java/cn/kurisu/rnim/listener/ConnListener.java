package cn.kurisu.txim.listener;


import cn.kurisu.txim.module.BaseModule;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.log.QLog;

public class ConnListener extends  BaseListener implements TIMConnListener {
    public ConnListener(BaseModule module) {
        super(module);
    }

    /**
     * 连接建立
     */
    public void onConnected(){
        QLog.i("连接状态改变", "recv onConnected");
    }

    /**
     * 连接断开
     *
     * @param code 错误码
     * @param desc 错误描述
     */
    public void onDisconnected(int code, String desc){
        QLog.i("连接状态改变", "recv onDisconnected, code " + code + "|desc " + desc);
    }

    /**
     * WIFI需要验证
     *
     * @param name wifi名称
     */
    public void onWifiNeedAuth(String name){
        QLog.d("连接状态改变", "recv onWifiNeedAuth, wifi name " + name);
    }
}
