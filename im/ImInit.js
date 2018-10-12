'use strict';
import { NativeModules, Platform, DeviceEventEmitter } from 'react-native'
const { TXIm } = NativeModules;

const listeners = {};

class ImInit{

    init(logLevel){
        const le = 0;
        le = logLevel;
        return TXIm.init(le)
    }

    /**
     * 登陆
     * @param identify
     * @param userSig
     * @returns {*} @see observeOnlineStatus 用户登陆状态见他
     */
    login(identify, userSig) {
        return TXIm.login(identify, userSig)
    }
    /**
     * 退出
     * @returns {*}
     */
    logout() {
        return TXIm.logout()
    }
}
export default new ImInit()
