package cn.kurisu.txim.listener;


import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import cn.kurisu.txim.constants.IMEventNameConstant;
import cn.kurisu.txim.module.BaseModule;
import cn.kurisu.txim.utils.PushUtil;
import cn.kurisu.txim.utils.messageUtils.MessageInfo;
import cn.kurisu.txim.utils.messageUtils.MessageInfoUtil;

import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.log.QLog;

import java.util.ArrayList;
import java.util.List;

public class MessageEventListener extends BaseListener implements TIMMessageListener {
    private static TIMFriendshipManager instance;

    public MessageEventListener(BaseModule module) {
        super(module);
    }

    /**
     * 收到新消息回调
     *
     * @param msgs 收到的新消息
     */
    public boolean onNewMessages(List<TIMMessage> msgs) {
        List<MessageInfo> messageInfos = MessageInfoUtil.TIMMessages2MessageInfos(msgs, false);
        MessageInfo info = messageInfos.get(0);
        QLog.i("收到消息", "recv onNewMessages, size " + (msgs != null ? msgs.size() : 0));
        WritableArray writableArray = messageAnalysis(messageInfos);
        module.sendEvent(IMEventNameConstant.ON_NEW_MESSAGE, writableArray);
        int type = info.getMsgType();
        if (MessageInfo.MSG_TYPE_CUSTOM == type) {
            PushUtil.getInstance().PushNotify(info.getData(), info.getDesc());
        } else {
            PushUtil.getInstance().PushNotify(info.getNickName(), info.getExtra() == null ? "" : info.getExtra().toString());
        }
        return true;
    }

    /**
     * 消息解析
     *
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
        map.putString("dataUri", info.getDataUri() == null ? "" : info.getDataUri().toString());
        map.putString("msgTime", String.valueOf(info.getMsgTime()));
        map.putString("extra", info.getExtra() == null ? "" : info.getExtra().toString());
        map.putInt("status", info.getStatus());
        map.putInt("msgType", info.getMsgType());
        map.putInt("imgWithd", info.getImgHeight());
        map.putInt("imgHeight", info.getImgWithd());
        map.putString("nickName", info.getNickName());
        TIMUserProfile userProfile = queryProfile(info.getFromUser());
        map.putString("senderAvatar", userProfile.getFaceUrl());
        map.putString("data", info.getData());
        map.putDouble("lat", info.getLat());
        map.putDouble("lng", info.getLng());
        map.putString("desc", info.getDesc());
        return map;
    }

    /**
     * 同步资料
     *
     * @param id
     */
    public static TIMUserProfile queryProfile(String id) {
        if (instance == null) {
            instance = TIMFriendshipManager.getInstance();
        }
        TIMUserProfile userProfile = instance.queryUserProfile(id);
        if (userProfile != null) {
            return userProfile;
        }
        List<String> list = new ArrayList<>();
        list.add(id);
        instance.getUsersProfile(list, true, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                QLog.e("MessageEventListener", "getUsersProfile failed: " + i + " desc：" + s);
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                QLog.e("MessageEventListener", "getUsersProfile succ");
            }
        });
        return new TIMUserProfile();
    }

}
