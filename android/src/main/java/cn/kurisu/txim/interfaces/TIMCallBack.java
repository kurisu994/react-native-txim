package cn.kurisu.txim.interfaces;

/**
 * UIKit回调的通用接口类
 */
public interface TIMCallBack {

    void onSuccess(Object data);

    void onError(String module, int errCode, String errMsg);
}
