package cn.kurisu.txim.listener;

import cn.kurisu.txim.module.BaseModule;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.log.QLog;

import java.util.List;

public class RefreshListener extends BaseListener implements TIMRefreshListener {
    public RefreshListener(BaseModule module) {
        super(module);
    }

    @Override
    public void onRefresh() {

    }

    /**
     * 部分会话刷新（包括多终端已读上报同步）
     * @param conversations 需要刷新的会话列表
     */
    public void onRefreshConversation(List<TIMConversation> conversations){
        QLog.d("回话刷新", "recv onRefreshConversation, size " + (conversations != null ? conversations.size() : 0));
    }
}
