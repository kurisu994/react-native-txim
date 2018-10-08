package cn.kurisu.reactnativedemo.event;


import com.facebook.react.bridge.WritableMap;
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

import cn.kurisu.reactnativedemo.TXImPackage;
import cn.kurisu.reactnativedemo.model.Message;
import cn.kurisu.reactnativedemo.model.MessageFactory;
import cn.kurisu.reactnativedemo.utils.ReactCache;

import static cn.kurisu.reactnativedemo.utils.ReactCache.observeRecentContact;

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
                .enableAutoReport(true)
                .enableReadReceipt(true)
                .enableRecentContact(true)
                .setMessageRevokedListener(this);
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
        for (TIMMessage item : list) {
            Message message = MessageFactory.getMessage(item);
            WritableMap writableMap = ReactCache.createMessage(list);
            TXImPackage.txImModule.sendEvent(observeRecentContact, writableMap);
        }
        return false;
    }

    /**
     * 主动通知新消息
     */
    public void onNewMessage(TIMMessage message) {

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
