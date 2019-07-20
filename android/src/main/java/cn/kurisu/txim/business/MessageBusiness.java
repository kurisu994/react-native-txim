package cn.kurisu.txim.business;

import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;

/**
 * @author kurisu
 */
public class MessageBusiness {

    public static TIMConversation getConversation(int type, String peer) {

        return TIMManager.getInstance().getConversation(
                TIMConversationType.values()[type],
                peer);

    }
}
