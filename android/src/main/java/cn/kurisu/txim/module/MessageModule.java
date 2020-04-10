package cn.kurisu.txim.module;

import android.net.Uri;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import cn.kurisu.txim.constants.IMEventNameConstant;
import cn.kurisu.txim.listener.MessageEventListener;
import cn.kurisu.txim.utils.messageUtils.MessageInfo;
import cn.kurisu.txim.utils.messageUtils.MessageInfoUtil;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.log.QLog;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Kurisu
 */
public class MessageModule extends BaseModule {
    TIMConversation conversation;
    TIMMessage lastMsg = null;

    public MessageModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Nonnull
    @Override
    public String getName() {
        return "IMMessageModule";
    }

    @ReactMethod
    public void getConversationList() {
        final WritableMap map = Arguments.createMap();
        try {
            List<TIMConversation> list = TIMManager.getInstance().getConversationList();
            WritableArray array = Arguments.createArray();
            if (list != null) {
                for (TIMConversation timConversation : list) {
                    WritableMap objmap = Arguments.createMap();
                    long unreadNum = timConversation.getUnreadMessageNum();
                    TIMConversationType type = timConversation.getType();
                    TIMMessage lastMsg = timConversation.getLastMsg();
                    //解析消息
                    MessageInfo messageInfo = MessageInfoUtil.TIMMessage2MessageInfo(lastMsg, TIMConversationType.Group.equals(type));
                    String peer = timConversation.getPeer();

                    objmap.putString("unread", String.valueOf(unreadNum));
                    if (messageInfo != null) {
                        objmap.putMap("message", MessageEventListener.messageAnalysis(messageInfo));
                    }
                    objmap.putString("peer", peer);
                    objmap.putInt("type", type.value());
                    if (TIMConversationType.Group.equals(type)) {
                        String groupName = timConversation.getGroupName();
                        objmap.putString("name", groupName);
                    } else {
                        TIMUserProfile userProfile = MessageEventListener.queryProfile(peer);
                        objmap.putString("name", userProfile.getNickName());
                    }

                    array.pushMap(objmap);
                }
            }
            map.putInt("code", 0);
            map.putString("msg", "");
            map.putArray("data", array);
            sendEvent(IMEventNameConstant.CONVERSATION_LIST_STATUS, map);
        } catch (Exception e) {
            map.putInt("code", -1);
            map.putString("msg", e.getMessage());
            sendEvent(IMEventNameConstant.CONVERSATION_LIST_STATUS, map);
        }
    }

    @ReactMethod
    public void readMessage() {
        if (this.conversation == null) {
            return;
        }
        TIMMessage msg = conversation.getLastMsg();
        this.conversation.setReadMessage(msg, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                QLog.e(getName(), "设置已读消息 失败, code: " + code + "|desc: " + desc);
            }

            @Override
            public void onSuccess() {
                QLog.d(getName(), "setReadMessage succ");
            }
        });
    }

    @ReactMethod
    public void getMessage(int pageSize, int type) {
        final WritableMap map = Arguments.createMap();
        if (conversation == null) {
            map.putInt("code", -1);
            map.putString("msg", "会话获取失败");
            sendEvent(IMEventNameConstant.ON_MESSAGE_QUERY, map);
            return;
        }

        if (TIMConversationType.C2C.value() == type) { //TODO 群消息拉取
            conversation.getMessage(pageSize, lastMsg, new TIMValueCallBack<List<TIMMessage>>() {
                @Override
                public void onError(int i, String s) {
                    map.putInt("code", i);
                    map.putString("msg", s);
                    sendEvent(IMEventNameConstant.ON_MESSAGE_QUERY, map);
                }

                @Override
                public void onSuccess(List<TIMMessage> timMessages) {
                    if (timMessages != null && timMessages.size() > 0) {
                        lastMsg = timMessages.get(timMessages.size() - 1);
                    }
                    List<MessageInfo> infoList = MessageInfoUtil.TIMMessages2MessageInfos(timMessages, false);
                    WritableArray writableArray = MessageEventListener.messageAnalysis(infoList);
                    map.putInt("code", 0);
                    map.putString("msg", "");
                    map.putArray("data", writableArray);
                    sendEvent(IMEventNameConstant.ON_MESSAGE_QUERY, map);
                }
            });
        }
    }


    @ReactMethod
    public void getConversation(int type, String peer) {
        final WritableMap map = Arguments.createMap();
        try {
            conversation = TIMManager.getInstance().getConversation(
                    TIMConversationType.values()[type],
                    peer);
            map.putInt("code", 0);
            map.putString("msg", "");
            sendEvent(IMEventNameConstant.CONVERSATION_STATUS, map);
        } catch (Exception e) {
            map.putInt("code", -1);
            map.putString("msg", e.getMessage());
            sendEvent(IMEventNameConstant.CONVERSATION_STATUS, map);
        }
    }

    @ReactMethod
    public void sendMessage(Integer type, String content,
                            String imgPath, Integer width, Integer height, Integer duration, //视频消息参数
                            Boolean compressed,  //图片消息
                            Double latitude, Double longitude //地理位置
    ) {
        final WritableMap map = Arguments.createMap();
        if (conversation == null) {
            map.putInt("code", -1);
            map.putString("msg", "当前会话已被销毁，请重新获取");
            sendEvent(IMEventNameConstant.SEND_STATUS, map);
        } else {
            MessageInfo info = newMeassage(type, content, imgPath, width, height, duration, compressed, latitude, longitude);
            if (info == null) {
                map.putInt("code", -1);
                map.putString("msg", "发送失败请重试");
                sendEvent(IMEventNameConstant.SEND_STATUS, map);
                return;
            }

            conversation.sendMessage(info.getTIMMessage(), new TIMValueCallBack<TIMMessage>() {//发送消息回调
                @Override
                public void onError(int code, String desc) {//发送消息失败
                    //错误码 code 含义请参见错误码表
                    QLog.e("发送消息", "send message failed. code: " + code + " errmsg: " + desc);
                    map.putInt("code", code);
                    map.putString("msg", desc);
                    sendEvent(IMEventNameConstant.SEND_STATUS, map);
                }

                @Override
                public void onSuccess(TIMMessage msg) {//发送消息成功
                    QLog.i("发送消息", "SendMsg ok");
                    WritableArray array = Arguments.createArray();
                    MessageInfo messageInfo = MessageInfoUtil.TIMMessage2MessageInfo(msg, info.isGroup());
                    map.putInt("code", 0);
                    map.putString("msg", "SendMsg ok");
                    sendEvent(IMEventNameConstant.SEND_STATUS, map);
                    if (messageInfo == null) {
                        return;
                    }
                    array.pushMap(MessageEventListener.messageAnalysis(messageInfo));
                    sendEvent(IMEventNameConstant.ON_NEW_MESSAGE, array);
                }
            });

        }

    }

    /**
     * 注销会话
     */
    @ReactMethod
    public void destroyConversation() {
        this.lastMsg = null;
        if (conversation != null) {
            this.conversation = null;
        }
    }

    private MessageInfo newMeassage(@Nonnull Integer type, @Nonnull String content,
                                    @Nullable String imgPath, @Nullable Integer width, @Nullable Integer height, @Nullable Integer duration, //视频消息参数
                                    Boolean compressed,  //图片消息
                                    Double latitude, Double longitude //地理位置
    ) {
        //构造一条消息
        MessageInfo messageInfo = null;
        switch (type) {
            case MessageInfo.MSG_TYPE_TEXT:
                messageInfo = MessageInfoUtil.buildTextMessage(content);
                break;
            case MessageInfo.MSG_TYPE_IMAGE:
                Uri uri = Uri.fromFile(new File(content));
                messageInfo = MessageInfoUtil.buildImageMessage(uri, compressed, compressed);
                break;
            case MessageInfo.MSG_TYPE_AUDIO:
                messageInfo = MessageInfoUtil.buildAudioMessage(content, duration);
                break;
            case MessageInfo.MSG_TYPE_VIDEO:
                messageInfo = MessageInfoUtil.buildVideoMessage(imgPath, content, width, height, duration);
                break;
            case MessageInfo.MSG_TYPE_FILE:
                Uri fileUri = Uri.fromFile(new File(content));
                messageInfo = MessageInfoUtil.buildFileMessage(fileUri);
                break;
            case MessageInfo.MSG_TYPE_LOCATION:
                messageInfo = MessageInfoUtil.buildLocationMessage(content, latitude, longitude);
                break;
            case MessageInfo.MSG_TYPE_CUSTOM_FACE:
                //TODO 表情待定
                break;
            default:
                break;
        }

        return messageInfo;
    }
}
