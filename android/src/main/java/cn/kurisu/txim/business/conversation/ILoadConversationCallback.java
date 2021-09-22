package cn.kurisu.txim.business.conversation;


public interface ILoadConversationCallback {
    void onSuccess(ConversationProvider provider, boolean isFinished, long nextSeq);

    void onError(String module, int errCode, String errMsg);
}
