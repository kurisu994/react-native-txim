import { NativeModules } from 'react-native'

const { TXIm } = NativeModules;



export const event = {
  recentContact: "observeRecentContact",
  onlineStatus: "observeOnlineStatus",
  friend: "observeFriend",
  team: "observeTeam",
  receiveMessage: "observeReceiveMessage",
  currentMessage: "observeCurrentMessage",
  deleteMessage: "observeDeleteMessage",
  receiveSystemMsg: "observeReceiveSystemMsg",
  msgStatus: "observeMsgStatus",
  audioRecord: "observeAudioRecord",
  unreadCountChange: "observeUnreadCountChange",
  blackList: "observeBlackList",
  attachmentProgress: "observeAttachmentProgress",
  onKick: "observeOnKick",
  accountNotice: "observeAccountNotice",
  launchPushEvent: "observeLaunchPushEvent",
  backgroundPushEvent: "observeBackgroundPushEvent"

}


/**
 * 新建会话
 * @param type
 * @param peer
 * @returns {*}
 */

export function getConversation(type, peer) {
  return TXIm.getConversation(type, peer);
}



export function destroyConversation() {
  return TXIm.destroyConversation();
}

/**
 * 发送文本消息
 */
export function sendTextMsg(text){
  return TXIm.sendTextMsg(text);
}

export default {
  getConversation, destroyConversation, sendTextMsg, event
}