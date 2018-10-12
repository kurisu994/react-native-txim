package cn.kurisu.reactnativedemo.presenter;

import com.facebook.react.bridge.WritableMap;
import com.tencent.imsdk.TIMMessage;

import java.util.Observable;
import java.util.Observer;

import cn.kurisu.reactnativedemo.TXImModule;
import cn.kurisu.reactnativedemo.TXImPackage;
import cn.kurisu.reactnativedemo.event.FriendshipEvent;
import cn.kurisu.reactnativedemo.event.GroupEvent;
import cn.kurisu.reactnativedemo.event.MessageEvent;
import cn.kurisu.reactnativedemo.event.RefreshEvent;
import cn.kurisu.reactnativedemo.utils.ReactCache;

import static cn.kurisu.reactnativedemo.utils.ReactCache.observeReceiveMessage;

/**
 * 会话界面逻辑
 */
public class ConversationPresenter implements Observer {

    private static final String TAG = "ConversationPresenter";

    public ConversationPresenter() {
        //注册消息监听
        MessageEvent.getInstance().addObserver(this);
        //注册刷新监听
        RefreshEvent.getInstance().addObserver(this);
        //注册好友关系链监听
        FriendshipEvent.getInstance().addObserver(this);
        //注册群关系监听
        GroupEvent.getInstance().addObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent) {
            if (data instanceof TIMMessage) {
                TIMMessage msg = (TIMMessage) data;
                WritableMap writableMap = ReactCache.createMessage(msg);
                if (writableMap != null) {
                    if (TXImModule.presenter == null || !TXImModule.presenter.getConversation().getPeer().equals(msg.getConversation().getPeer())) {
                        TXImPackage.txImModule.sendEvent(observeReceiveMessage, writableMap);
                    }
                }
            }
        } else if (observable instanceof FriendshipEvent) {
            FriendshipEvent.NotifyCmd cmd = (FriendshipEvent.NotifyCmd) data;
            switch (cmd.type) {
                case ADD_REQ:
                case READ_MSG:
                case ADD:
                    //todo
                    break;
            }
        } else if (observable instanceof GroupEvent) {
            GroupEvent.NotifyCmd cmd = (GroupEvent.NotifyCmd) data;
            switch (cmd.type) {
                case UPDATE:
                case ADD:
                    //todo
                    break;
                case DEL:
                    //todo
                    break;

            }
        }
    }

}
