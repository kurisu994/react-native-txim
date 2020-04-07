import { DeviceEventEmitter, NativeModules } from 'react-native';
import { EventName, MessageType } from './constant';

const { IMMessageModule: module } = NativeModules;

export default {
  /**
   * 添加消息接收监听器
   */
  addMessageReceiveListener(listener, context) {
    return DeviceEventEmitter.addListener(EventName.onNewMessage, listener, context);
  },
  /** 
  * 消息列表发生改变时的监听
 */
  addConversationRefreshListener(listener, context) {
    return DeviceEventEmitter.addListener(EventName.onConversationRefresh, listener, context);
  },

  /**
   * 新建会话
   * @param type
   * @param peer
   * @returns {*}
   */
  getConversation(type, peer) {
    return new Promise((resolve, reject) => {
      try {
        module.getConversation(type, peer);
      } catch (e) {
        reject(e);
        return;
      }
      DeviceEventEmitter.once(EventName.conversationStatus, resp => {
        if (resp.code === 0) {
          resolve(true);
        } else {
          const err = new Error(resp.msg);
          err.code = resp.code;
          reject(err);
        }
      });
    });
  },

  getConversationList() {
    return new Promise((resolve, reject) => {
      try {
        module.getConversationList();
      } catch (e) {
        reject(e);
        return;
      }
      DeviceEventEmitter.once(EventName.conversationListStatus, resp => {
        if (resp.code === 0) {
          resolve(resp);
        } else {
          const err = new Error(resp.msg);
          err.code = resp.code;
          reject(err);
        }
      });
    });
  },

  readMessage() {
    return module.readMessage();
  },

  getMessage(pageSize = 10, type = 1) {
    return new Promise((resolve, reject) => {
      try {
        module.getMessage(pageSize, type);
      } catch (e) {
        reject(e);
        return;
      }
      DeviceEventEmitter.once(EventName.onMessageQuery, resp => {
        if (resp.code === 0) {
          resolve(resp);
        } else {
          const err = new Error(resp.msg);
          err.code = resp.code;
          reject(err);
        }
      });
    });
  },

  destroyConversation() {
    return module.destroyConversation();
  },

  /**
   * 发送文本消息
   */
  sendTextMsg(text) {
    return new Promise((resolve, reject) => {
      try {
        module.sendMessage(MessageType.Text, text, '', 0, 0, 0, true, 0.0, 0.0);
      } catch (e) {
        reject(e);
        return;
      }
      DeviceEventEmitter.once(EventName.sendStatus, resp => {
        if (resp.code === 0) {
          resolve(true);
        } else {
          const err = new Error(resp.msg);
          err.code = resp.code;
          reject(err);
        }
      });
    });
  },
  /**
   * 发送图片消息
   */
  sendImageMsg(path, original = false) {
    return new Promise((resolve, reject) => {
      try {
        module.sendMessage(MessageType.Image, path, '', 0, 0, 0, !original, 0.0, 0.0);
      } catch (e) {
        reject(e);
        return;
      }
      DeviceEventEmitter.once(EventName.sendStatus, resp => {
        if (resp.code === 0) {
          resolve(true);
        } else {
          const err = new Error(resp.msg);
          err.code = resp.code;
          reject(err);
        }
      });
    });
  },

  /**
   * 发送语音消息
   */
  sendAudioMsg(path, duration) {
    return new Promise((resolve, reject) => {
      try {
        module.sendMessage(MessageType.Sound, path, '', 0, 0, duration, true, 0.0, 0.0);
      } catch (e) {
        reject(e);
        return;
      }
      DeviceEventEmitter.once(EventName.sendStatus, resp => {
        if (resp.code === 0) {
          resolve(true);
        } else {
          const err = new Error(resp.msg);
          err.code = resp.code;
          reject(err);
        }
      });
    });
  },
};
