package cn.kurisu.rnim.presenter;

import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMMessageDraft;
import com.tencent.imsdk.ext.message.TIMMessageLocator;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import cn.kurisu.rnim.TXImPackage;
import cn.kurisu.rnim.event.MessageEvent;
import cn.kurisu.rnim.event.RefreshEvent;
import cn.kurisu.rnim.utils.ReactCache;

import static cn.kurisu.rnim.utils.ReactCache.observeCurrentMessage;

/**
 * 聊天界面逻辑
 */
public class ChatPresenter implements Observer {

    private TIMConversation conversation;
    private boolean isGetingMessage = false;
    private final int LAST_MESSAGE_NUM = 20;
    private final static String TAG = "ChatPresenter";

    public ChatPresenter(String identify, TIMConversationType type) {
        conversation = TIMManager.getInstance().getConversation(type, identify);
    }


    /**
     * 加载页面逻辑
     */
    public void start() {
        System.out.println(TIMManager.getInstance().getMode());
        //注册消息监听
        MessageEvent.getInstance().addObserver(this);
        RefreshEvent.getInstance().addObserver(this);
        //getMessage(null);
        TIMConversationExt timConversationExt = new TIMConversationExt(conversation);
        if (timConversationExt.hasDraft()) {
            WritableMap writableMap = Arguments.createMap();
            TIMTextElem textElem = (TIMTextElem) timConversationExt.getDraft().getElems().get(0);
            writableMap.putString("text", textElem.getText());
            TXImPackage.txImModule.sendEvent(observeCurrentMessage, writableMap);
        }
    }


    /**
     * 中止页面逻辑
     */
    public void stop() {
        //注销消息监听
        MessageEvent.getInstance().deleteObserver(this);
        RefreshEvent.getInstance().deleteObserver(this);
    }

    /**
     * 获取聊天TIM会话
     */
    public TIMConversation getConversation() {
        return conversation;
    }

    /**
     * 发送消息
     *
     * @param message 发送的消息
     */
    public void sendMessage(final TIMMessage message, Promise promise) {
        conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 含义请参见错误码表
                promise.reject(String.valueOf(code), desc);
            }
            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                promise.resolve(true);
            }
        });
        //message对象为发送中状态
        MessageEvent.getInstance().onNewMessage(message);
    }


    /**
     * 发送在线消息
     *
     * @param message 发送的消息
     */
    public void sendOnlineMessage(final TIMMessage message) {
        conversation.sendOnlineMessage(message, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
            }
            @Override
            public void onSuccess(TIMMessage message) {
            }
        });
    }

    /**
     * 撤消消息
     *
     * @param message
     */
    public void revokeMessage(final TIMMessage message) {
        TIMConversationExt timConversationExt = new TIMConversationExt(conversation);
        timConversationExt.revokeMessage(message, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "revoke error " + i);
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "revoke success");
                MessageEvent.getInstance().onNewMessage(null);
            }
        });
    }


    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent) {
            if (data instanceof TIMMessage) {
                TIMMessage msg = (TIMMessage) data;
                if (msg.isSelf()||(msg.getConversation().getPeer().equals(conversation.getPeer()) && msg.getConversation().getType() == conversation.getType())) {
                    //当前聊天界面已读上报，用于多终端登录时未读消息数同步
                    WritableMap writableMap = ReactCache.createMessage(msg);
                    if (writableMap != null) {
                        TXImPackage.txImModule.sendEvent(observeCurrentMessage, writableMap);
                    }
                    readMessages();
                }
            } else if (data instanceof TIMMessageLocator) {
                TIMMessageLocator msg = (TIMMessageLocator) data;
            }
        } else if (observable instanceof RefreshEvent) {
            //getMessage(null);
        }
    }


    /**
     * 获取消息
     *
     * @param message 最后一条消息
     */
    public void getMessage(@Nullable TIMMessage message) {
        if (!isGetingMessage) {
            isGetingMessage = true;
            TIMConversationExt timConversationExt = new TIMConversationExt(conversation);
            timConversationExt.getMessage(LAST_MESSAGE_NUM, message, new TIMValueCallBack<List<TIMMessage>>() {
                @Override
                public void onError(int i, String s) {
                    isGetingMessage = false;
                    Log.e(TAG, "get message error" + s);
                }

                @Override
                public void onSuccess(List<TIMMessage> timMessages) {
                    isGetingMessage = false;
                    WritableMap writableMap = ReactCache.createMessage(timMessages);
                    if (writableMap != null) {
                        TXImPackage.txImModule.sendEvent(observeCurrentMessage, writableMap);
                    }
                }
            });
        }

    }

    /**
     * 设置会话为已读
     */
    public void readMessages() {
        TIMConversationExt timConversationExt = new TIMConversationExt(conversation);
        timConversationExt.setReadMessage(null, null);
    }


    /**
     * 保存草稿
     *
     * @param message 消息数据
     */
    public void saveDraft(TIMMessage message) {
        TIMConversationExt timConversationExt = new TIMConversationExt(conversation);
        timConversationExt.setDraft(null);
        if (message != null && message.getElementCount() > 0) {
            TIMMessageDraft draft = new TIMMessageDraft();
            for (int i = 0; i < message.getElementCount(); ++i) {
                draft.addElem(message.getElement(i));
            }
            timConversationExt.setDraft(draft);
        }

    }
}
