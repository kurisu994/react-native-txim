'use strict';
import { NativeModules, Platform, DeviceEventEmitter } from 'react-native'
const { TXIm } = NativeModules;

const listeners = {};

const listenerRecentContact = "observeRecentContact";//'最近会话'
const listenerOnlineStatus = "observeOnlineStatus";//'在线状态'
const listenerFriend = "observeFriend";//'联系人'
const listenerTeam = "observeTeam";//'群组'
const listenerReceiveMessage = "observeReceiveMessage";//'接收消息'

const listenerDeleteMessage = "observeDeleteMessage";//'撤销后删除消息'
const listenerReceiveSystemMsg = "observeReceiveSystemMsg";//'系统通知'
const listenerMsgStatus = "observeMsgStatus";//'发送消息状态变化'
const listenerAudioRecord = "observeAudioRecord";//'录音状态'
const listenerUnreadCountChange = "observeUnreadCountChange";//'未读数变化'
const listenerBlackList = "observeBlackList";//'黑名单'
const listenerAttachmentProgress = "observeAttachmentProgress";//'上传下载进度'
const listenerOnKick = "observeOnKick";//'被踢出'
const listenerAccountNotice = "observeAccountNotice";//'账户变动通知'
const listenerLaunchPushEvent = "observeLaunchPushEvent";//''
const listenerBackgroundPushEvent = "observeBackgroundPushEvent";//''


class Message{
    onNewMessage (cb) {
        listeners[cb] = DeviceEventEmitter.addListener(
            listenerReceiveMessage,
            data => {
                cb(data)
            }
        )
    }
}
export default new Message()
