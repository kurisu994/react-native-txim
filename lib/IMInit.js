import { DeviceEventEmitter, NativeModules } from 'react-native';
import { EventName } from './constant';

const { IMInitializeModule: module } = NativeModules;

export default {
  /**
   * 添加用户在线状态监听器
   */
  addOnlineStatusListener(listener, context) {
    return DeviceEventEmitter.addListener(EventName.userStatus, listener, context);
  },

  /**
   * 登陆
   * @param identify 用户账号
   * @param userSig 用户签名
   * @returns {*}
   */
  login(identify, userSig) {
    return new Promise((resolve, reject) => {
      try {
        module.imLogin(identify, userSig);
      } catch (e) {
        reject(e);
        return;
      }
      DeviceEventEmitter.once(EventName.loginStatus, resp => {
        resolve(resp);
      });
    });
  },

  /**
   * 退出
   * @returns {*}
   */
  logout() {
    return module.logout();
  },
};
