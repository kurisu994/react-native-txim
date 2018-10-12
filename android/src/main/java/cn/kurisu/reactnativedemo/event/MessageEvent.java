package cn.kurisu.reactnativedemo.event;


import android.util.Log;

import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.ext.message.TIMMessageLocator;
import com.tencent.imsdk.ext.message.TIMMessageReceipt;
import com.tencent.imsdk.ext.message.TIMMessageReceiptListener;
import com.tencent.imsdk.ext.message.TIMMessageRevokedListener;
import com.tencent.imsdk.ext.message.TIMUserConfigMsgExt;

import java.util.List;
import java.util.Observable;

/**
 * 消息通知事件，上层界面可以订阅此事件
 */
public class MessageEvent extends Observable implements TIMMessageListener, TIMMessageRevokedListener, TIMMessageReceiptListener {


    private volatile static MessageEvent instance;

    private MessageEvent() {
        //注册消息监听器
        TIMManager.getInstance().addMessageListener(this);
    }

    public TIMUserConfig init(TIMUserConfig config) {
        return new TIMUserConfigMsgExt(config)
                .enableReadReceipt(true)
                .setMessageRevokedListener(this)
                .setMessageReceiptListener(list -> {
                    //已读回执监听器
                    Log.i(this.getClass().getSimpleName(), "已读消息list长度：" + list.size());
                });
    }

    public static MessageEvent getInstance() {
        if (instance == null) {
            synchronized (MessageEvent.class) {
                if (instance == null) {
                    instance = new MessageEvent();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        System.out.println("收到" + list.size() + "条消息");
        for (TIMMessage item : list) {
            setChanged();
            notifyObservers(item);
        }
        return false;
    }

    /**
     * 主动通知新消息
     */
    public void onNewMessage(TIMMessage message) {
        setChanged();
        notifyObservers(message);
    }

    /**
     * 清理消息监听
     */
    public void clear() {
        instance = null;
    }

    /**
     * 撤回消息
     *
     * @param timMessageLocator
     */
    @Override
    public void onMessageRevoked(TIMMessageLocator timMessageLocator) {
        //TODO
    }

    /**
     * 已读消息监听器
     *
     * @param list
     */
    @Override
    public void onRecvReceipt(List<TIMMessageReceipt> list) {
        //TODO
    }
}
