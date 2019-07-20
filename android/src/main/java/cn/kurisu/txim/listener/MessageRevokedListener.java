package cn.kurisu.txim.listener;

import cn.kurisu.txim.module.BaseModule;
import com.tencent.imsdk.ext.message.TIMMessageLocator;
import com.tencent.imsdk.ext.message.TIMMessageRevokedListener;

import java.util.ArrayList;
import java.util.List;

public class MessageRevokedListener extends BaseListener implements TIMMessageRevokedListener {
    public MessageRevokedListener(BaseModule module) {
        super(module);
    }

    private List<MessageRevokeHandler> mHandlers = new ArrayList<>();

    @Override
    public void onMessageRevoked(TIMMessageLocator locator) {
        for (int i = 0; i < mHandlers.size(); i++) {
            mHandlers.get(i).handleInvoke(locator);
        }
    }

    public void addHandler(MessageRevokeHandler handler) {
        mHandlers.add(handler);
    }

    public void removeHandler(MessageRevokeHandler handler) {
        mHandlers.remove(handler);
    }

    public interface MessageRevokeHandler {
        public void handleInvoke(TIMMessageLocator locator);

    }
}
