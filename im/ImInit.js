import { NativeModules } from 'react-native';
const { TXIm } = NativeModules;

export function init(logLevel) {
  let level = 0;
  if (logLevel) {
    level = logLevel;
  }
  return TXIm.init(level);
}


/**
 * 登陆
 * @param identify 用户账号
 * @param userSig 用户签名
 * @returns {*} @see observeOnlineStatus 用户登陆状态监听
 */

export function login(identify, userSig) {
  return TXIm.login(identify, userSig).then(res => {
    if (res) {
      isInit = true;
    }
    return true;
  });
}

/**
 * 退出
 * @returns {*}
 */
export function logout() {
  return TXIm.logout()
}

export function getInitStatus() {
  return isInit;
}

export default {
  init, login, logout, getInitStatus
}