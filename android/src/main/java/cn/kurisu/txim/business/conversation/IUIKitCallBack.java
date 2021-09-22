package cn.kurisu.txim.business.conversation;

/**
 * UIKit回调的通用接口类
 */
public interface IUIKitCallBack {

    void onSuccess(Object data);

    void onError(String module, int errCode, String errMsg);
}
