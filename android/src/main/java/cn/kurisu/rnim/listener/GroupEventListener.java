package cn.kurisu.txim.listener;

import cn.kurisu.txim.module.BaseModule;
import com.tencent.imsdk.TIMGroupEventListener;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.log.QLog;

public class GroupEventListener extends BaseListener implements TIMGroupEventListener {

    public GroupEventListener(BaseModule module) {
        super(module);
    }

    /**
     * 群Tips事件通知回调
     *
     * @param elem 群tips消息
     */
    public void onGroupTipsEvent(TIMGroupTipsElem elem){
        QLog.i("收到消息", "recv onGroupTipsEvent, groupid: "+ elem.getGroupId() + "|type: " + elem.getTipsType());
    }
}
