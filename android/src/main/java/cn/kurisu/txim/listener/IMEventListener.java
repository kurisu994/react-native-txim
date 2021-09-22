package cn.kurisu.txim.listener;

import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMMessage;

import java.util.List;

import cn.kurisu.txim.utils.TIMLog;

/**
 * IM事件监听
 */

public abstract class IMEventListener {
    private final static String TAG = IMEventListener.class.getSimpleName();

    /**
     * 被踢下线时回调
     */
    public void onForceOffline() {
        TIMLog.v(TAG, "onForceOffline");
    }

    /**
     * 用户票据过期
     */
    public void onUserSigExpired() {
        TIMLog.v(TAG, "onUserSigExpired");
    }

    /**
     * 连接建立
     */
    public void onConnected() {
        TIMLog.v(TAG, "onConnected");
    }

    /**
     * 连接断开
     *
     * @param code 错误码
     * @param desc 错误描述
     */
    public void onDisconnected(int code, String desc) {
        TIMLog.v(TAG, "onDisconnected, code:" + code + "|desc:" + desc);
    }

    /**
     * WIFI需要验证
     *
     * @param name wifi名称
     */
    public void onWifiNeedAuth(String name) {
        TIMLog.v(TAG, "onWifiNeedAuth, wifi name:" + name);
    }

    /**
     * 部分会话刷新（包括多终端已读上报同步）
     *
     * @param conversations 需要刷新的会话列表
     */
    public void onRefreshConversation(List<V2TIMConversation> conversations) {
        TIMLog.v(TAG, "onRefreshConversation, size:" + (conversations != null ? conversations.size() : 0));
    }

    /**
     * 收到新消息回调
     *
     * @param v2TIMMessage 收到的新消息
     */
    public void onNewMessage(V2TIMMessage v2TIMMessage) {
        TIMLog.v(TAG, "onNewMessage, msgID:" + (v2TIMMessage != null ? v2TIMMessage.getMsgID() : ""));
    }
}
