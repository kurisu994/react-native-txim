package cn.kurisu.txim.listener;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import cn.kurisu.txim.constants.IMEventNameConstant;
import cn.kurisu.txim.module.BaseModule;
import cn.kurisu.txim.utils.PushUtil;
import cn.kurisu.txim.utils.messageUtils.MessageInfo;
import cn.kurisu.txim.utils.messageUtils.MessageInfoUtil;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.log.QLog;

import java.util.List;

public class MessageEventListener extends BaseListener implements TIMMessageListener {
    public MessageEventListener(BaseModule module) {
        super(module);
    }

    /**
     * 收到新消息回调
     *
     * @param msgs 收到的新消息
     */
    public boolean onNewMessages(List<TIMMessage> msgs) {
        QLog.i("收到消息", "recv onNewMessages, size " + (msgs != null ? msgs.size() : 0));
        List<MessageInfo> messageInfos = MessageInfoUtil.TIMMessages2MessageInfos(msgs, false);
        MessageInfo info = messageInfos.get(0);
        WritableArray writableArray = messageAnalysis(messageInfos);
        module.sendEvent(IMEventNameConstant.ON_NEW_MESSAGE, writableArray);
        int type = info.getMsgType();
        if (MessageInfo.MSG_TYPE_CUSTOM == type) {
            PushUtil.getInstance().PushNotify(info.getData(), info.getDesc());
        } else {
            PushUtil.getInstance().PushNotify(info.getNickName(), info.getExtra().toString());
        }

        return false;
    }

    /**
     * 消息解析
     * @param list
     */

    public static WritableArray messageAnalysis(List<MessageInfo> list) {
        WritableArray array = Arguments.createArray();

        for (MessageInfo info : list) {
            WritableMap map = messageAnalysis(info);
            array.pushMap(map);
        }

        return array;
    }

    public static WritableMap messageAnalysis(MessageInfo info) {
        WritableMap map = Arguments.createMap();
        map.putString("sender", info.getFromUser());
        map.putString("peer", info.getPeer());
        map.putString("msgId", info.getMsgId());
        map.putBoolean("self", info.isSelf());
        map.putBoolean("read", info.isRead());
        map.putBoolean("group", info.isGroup());
        map.putString("dataPath", info.getDataPath());
        map.putString("msgTime", String.valueOf(info.getMsgTime()));
        map.putString("extra", info.getExtra().toString());
        map.putInt("status", info.getStatus());
        map.putInt("msgType", info.getMsgType());
        map.putInt("imgWithd", info.getImgHeight());
        map.putInt("imgHeight", info.getImgWithd());
        map.putString("nickName", info.getNickName());
        map.putString("senderAvatar", info.getAvatar());
        map.putString("data", info.getData());
        map.putDouble("lat", info.getLat());
        map.putDouble("lng", info.getLng());
        map.putString("desc", info.getDesc());
        return map;
    }

}
