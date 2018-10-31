package cn.kurisu.rnim.utils;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.ext.message.TIMMessageExt;

import java.util.List;

import cn.kurisu.rnim.model.CustomMessage;
import cn.kurisu.rnim.model.Message;
import cn.kurisu.rnim.model.MessageFactory;
import cn.kurisu.rnim.model.TextMessage;

public class ReactCache {
    public final static String observeRecentContact = "observeRecentContact";//'最近会话'
    public final static String observeOnlineStatus = "observeOnlineStatus";//'在线状态'
    public final static String observeFriend = "observeFriend";//'联系人'
    public final static String observeTeam = "observeTeam";//'群组'
    public final static String observeReceiveMessage = "observeReceiveMessage";//'接收消息'
    public final static String observeCurrentMessage = "observeCurrentMessage";//'接收当前聊天人的消息'

    public final static String observeDeleteMessage = "observeDeleteMessage";//'撤销后删除消息'
    public final static String observeReceiveSystemMsg = "observeReceiveSystemMsg";//'系统通知'
    public final static String observeMsgStatus = "observeMsgStatus";//'发送消息状态变化'
    public final static String observeAudioRecord = "observeAudioRecord";//'录音状态'
    public final static String observeUnreadCountChange = "observeUnreadCountChange";//'未读数变化'
    public final static String observeBlackList = "observeBlackList";//'黑名单'
    public final static String observeAttachmentProgress = "observeAttachmentProgress";//'上传下载进度'
    public final static String observeAccountNotice = "observeAccountNotice";//'账户变动通知'
    public final static String observeLaunchPushEvent = "observeLaunchPushEvent";//''
    public final static String observeBackgroundPushEvent = "observeBackgroundPushEvent";//''

    public static WritableMap createMessage(TIMMessage timMsg) {
        WritableMap map = Arguments.createMap();
        TIMMessageExt ext = new TIMMessageExt(timMsg);
        TIMMessageStatus status = timMsg.status();
        if (status.equals(TIMMessageStatus.HasDeleted)) {
            return null;
        }
        boolean read = ext.isRead();
        if (status.equals(TIMMessageStatus.HasRevoked)) {
            read = true;
        }
        Message message = MessageFactory.getMessage(timMsg);
        String timestamp = String.valueOf(timMsg.timestamp()*1000);
        map.putBoolean("isSelf", timMsg.isSelf());
        if (!timMsg.isSelf()) {
            TIMUserProfile senderProfile = timMsg.getSenderProfile();
            String avatar = senderProfile.getFaceUrl();
            String nickName = senderProfile.getNickName();
            String account = senderProfile.getIdentifier();
            map.putString("from_avatar", avatar);
            map.putString("from_nickName", nickName);
            map.putString("from_account", account);
        }
        map.putString("send_time", timestamp);
        map.putString("msgId", timMsg.getMsgId());
        map.putString("msgType", message.getMsgType());
        if (message instanceof TextMessage) {
            TIMTextElem element = (TIMTextElem) timMsg.getElement(0);
            String text = element.getText();
            map.putString("text", text);
            map.putString("summary", message.getSummary());
        } else if (message instanceof CustomMessage) {
            TIMCustomElem element = (TIMCustomElem) timMsg.getElement(0);
            map.putString("data", new String(element.getData()));
            map.putString("ext", new String(element.getExt()));
            map.putString("text", message.getDesc());
            map.putString("summary", message.getSummary());
        }
        map.putBoolean("isRead", read);
        return map;
    }

    public static WritableMap createMessage(List<TIMMessage> list) {
        WritableMap map = Arguments.createMap();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                TIMMessage timMsg = list.get(i);
                TIMMessageExt ext = new TIMMessageExt(timMsg);
                TIMMessageStatus status = timMsg.status();
                if (status.equals(TIMMessageStatus.HasDeleted)) {
                    continue;
                }
                boolean read = ext.isRead();
                if (status.equals(TIMMessageStatus.HasRevoked)) {
                    read = true;
                }
                Message message = MessageFactory.getMessage(timMsg);
                String timestamp = String.valueOf(timMsg.timestamp()*1000);
                map.putBoolean("isSelf", timMsg.isSelf());
                if (!timMsg.isSelf()) {
                    TIMUserProfile senderProfile = timMsg.getSenderProfile();
                    String avatar = senderProfile.getFaceUrl();
                    String nickName = senderProfile.getNickName();
                    String account = senderProfile.getIdentifier();
                    map.putString("from_avatar", avatar);
                    map.putString("from_nickName", nickName);
                    map.putString("from_account", account);
                }
                map.putString("send_time", timestamp);
                map.putString("msgId", timMsg.getMsgId());
                map.putString("msgType", message.getMsgType());
                if (message instanceof TextMessage) {
                    TIMTextElem element = (TIMTextElem) timMsg.getElement(0);
                    String text = element.getText();
                    map.putString("text", text);
                    map.putString("summary", message.getSummary());
                } else if (message instanceof CustomMessage) {
                    TIMCustomElem element = (TIMCustomElem) timMsg.getElement(0);
                    map.putString("data", new String(element.getData()));
                    map.putString("ext", new String(element.getExt()));
                    map.putString("text", message.getDesc());
                    map.putString("summary", message.getSummary());
                }
                map.putBoolean("isRead", read);
                break;
            }
        }
        return map;
    }
}
