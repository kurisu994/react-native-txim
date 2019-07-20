import { NativeEventEmitter, NativeModules } from 'react-native';
import { EventName } from './constant';

const { IMInitializeModule: module } = NativeModules;
const emitter = new NativeEventEmitter(module);

export default {
  addOnlineStatusListener(listener, context) {
    return emitter.addListener(EventName.userStatus, listener, context);
  },

  login(identify, userSig) {
    try {
      return module.login(identify, userSig);
    } catch (e) {
      return Promise.reject(e);
    }
  },

  logout() {
    try {
      return module.logout();
    } catch (e) {
      return Promise.reject(e);
    }
  },
};
