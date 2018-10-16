import { NativeModules } from 'react-native'

const { TXIm } = NativeModules;



export const event = {
  onlineStatus: "observeOnlineStatus",
  receiveMessage: "observeReceiveMessage",
  currentMessage: "observeCurrentMessage",
  receiveSystemMsg: "observeReceiveSystemMsg",
  blackList: "observeBlackList",
  userStatus: "observeUserStatus",
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