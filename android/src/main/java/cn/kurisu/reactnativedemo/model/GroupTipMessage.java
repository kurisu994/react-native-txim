package cn.kurisu.reactnativedemo.model;

import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMMessage;

import java.util.Iterator;
import java.util.Map;

import cn.kurisu.reactnativedemo.IMApplication;
import cn.kurisu.reactnativedemo.R;

/**
 * 群tips消息
 */
public class GroupTipMessage extends Message {


    public GroupTipMessage(TIMMessage message) {
        this.message = message;
    }


    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        final TIMGroupTipsElem e = (TIMGroupTipsElem) message.getElement(0);
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Map.Entry<String, TIMGroupMemberInfo>> iterator = e.getChangedGroupMemberInfo().entrySet().iterator();
        switch (e.getTipsType()) {
            case CancelAdmin:
            case SetAdmin:
                return IMApplication.getContext().getString(R.string.summary_group_admin_change);
            case Join:
                while (iterator.hasNext()) {
                    Map.Entry<String, TIMGroupMemberInfo> item = iterator.next();
                    stringBuilder.append(getName(item.getValue()));
                    stringBuilder.append(" ");
                }
                return stringBuilder +
                        IMApplication.getContext().getString(R.string.summary_group_mem_add);
            case Kick:
                return e.getUserList().get(0) +
                        IMApplication.getContext().getString(R.string.summary_group_mem_kick);
            case ModifyMemberInfo:
                while (iterator.hasNext()) {
                    Map.Entry<String, TIMGroupMemberInfo> item = iterator.next();
                    stringBuilder.append(getName(item.getValue()));
                    stringBuilder.append(" ");
                }
                return stringBuilder +
                        IMApplication.getContext().getString(R.string.summary_group_mem_modify);
            case Quit:
                return e.getOpUser() +
                        IMApplication.getContext().getString(R.string.summary_group_mem_quit);
            case ModifyGroupInfo:
                return IMApplication.getContext().getString(R.string.summary_group_info_change);
        }
        return "";
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    @Override
    public String getMsgType() {
        return "GroupTips";
    }

    private String getName(TIMGroupMemberInfo info) {
        if (info.getNameCard().equals("")) {
            return info.getUser();
        }
        return info.getNameCard();
    }
}
