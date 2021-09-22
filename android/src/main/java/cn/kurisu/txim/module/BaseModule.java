package cn.kurisu.txim.module;


import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * Created by lovebing on 2016/10/28.
 */
abstract public class BaseModule extends ReactContextBaseJavaModule {

    public BaseModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    /**
     * @param eventName
     * @param params
     */
    public void sendEvent(String eventName, @Nullable WritableMap params) {
        this.getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    /**
     * @param eventName
     * @param params
     */
    public void sendEvent(String eventName, @Nullable WritableArray params) {
        this.getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

}
