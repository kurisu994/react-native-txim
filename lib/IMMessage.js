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
   * 发送语音消息（还没测试过）
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
