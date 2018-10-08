package cn.kurisu.reactnativedemo.model;


import com.tencent.imsdk.TIMGroupMemberRoleType;
import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.ext.group.TIMGroupAssistant;
import com.tencent.imsdk.ext.group.TIMGroupCacheInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import cn.kurisu.reactnativedemo.MainApplication;
import cn.kurisu.reactnativedemo.R;
import cn.kurisu.reactnativedemo.event.GroupEvent;
import cn.kurisu.reactnativedemo.event.RefreshEvent;

/**
 * 群数据结构
 */
public class GroupInfo implements Observer {


    private Map<String, List<GroupProfile>> groups;
    public static final String publicGroup = "Public", privateGroup = "Private", chatRoom = "ChatRoom";

    private GroupInfo() {
        groups = new HashMap<>();
        groups.put(publicGroup, new ArrayList<GroupProfile>());
        groups.put(privateGroup, new ArrayList<GroupProfile>());
        groups.put(chatRoom, new ArrayList<GroupProfile>());
        //注册群关系监听
        GroupEvent.getInstance().addObserver(this);
        RefreshEvent.getInstance().addObserver(this);
        refresh();
    }

    private static GroupInfo instance;

    public synchronized static GroupInfo getInstance() {
        if (instance == null) {
            instance = new GroupInfo();
        }
        return instance;
    }

    private void refresh() {
        for (String key : groups.keySet()) {
            groups.get(key).clear();
        }
        List<TIMGroupCacheInfo> groupInfos = TIMGroupAssistant.getInstance().getGroups(null);
        if (groupInfos == null) return;
        for (TIMGroupCacheInfo item : groupInfos) {
            List<GroupProfile> list = groups.get(item.getGroupInfo().getGroupType());
            if (list == null) continue;
            list.add(new GroupProfile(item));
        }
    }

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof GroupEvent) {
            if (data instanceof GroupEvent.NotifyCmd) {
                GroupEvent.NotifyCmd cmd = (GroupEvent.NotifyCmd) data;
                switch (cmd.type) {
                    case REFRESH:
                        refresh();
                        break;
                    case ADD:
                    case UPDATE:
                        updateGroup((TIMGroupCacheInfo) cmd.data);
                        break;
                    case DEL:
                        delGroup((String) cmd.data);
                        break;

                }
            }
        } else if (observable instanceof RefreshEvent) {
            refresh();
        }
    }

    private void updateGroup(TIMGroupCacheInfo info) {
        if (groups == null || groups.get(info.getGroupInfo().getGroupType()) == null) return;
        for (GroupProfile item : groups.get(info.getGroupInfo().getGroupType())) {
            if (item.getIdentify().equals(info.getGroupInfo().getGroupId())) {
                item.setProfile(info);
                return;
            }
        }
        groups.get(info.getGroupInfo().getGroupType()).add(new GroupProfile(info));
    }

    private void delGroup(String id) {
        for (String key : groups.keySet()) {
            Iterator<GroupProfile> iterator = groups.get(key).iterator();
            while (iterator.hasNext()) {
                GroupProfile item = iterator.next();
                if (item.getIdentify().equals(id)) {
                    iterator.remove();
                    return;
                }
            }
        }
    }

    /**
     * 是否在群内
     *
     * @param id 群identify
     */
    public boolean isInGroup(String id) {
        for (String key : groups.keySet()) {
            for (GroupProfile item : groups.get(key)) {
                if (item.getIdentify().equals(id)) return true;
            }
        }
        return false;
    }

    /**
     * 获取在该群身份
     *
     * @param id 群identify
     */
    public TIMGroupMemberRoleType getRole(String id) {
        for (String key : groups.keySet()) {
            for (GroupProfile item : groups.get(key)) {
                if (item.getIdentify().equals(id)) {
                    return item.getRole();
                }
            }
        }
        return TIMGroupMemberRoleType.NotMember;
    }


    /**
     * 获取该群的群消息接收状态
     *
     * @param id 群identify
     */
    public TIMGroupReceiveMessageOpt getMessageOpt(String id) {
        for (String key : groups.keySet()) {
            for (GroupProfile item : groups.get(key)) {
                if (item.getIdentify().equals(id)) {
                    return item.getMessagOpt();
                }
            }
        }
        return TIMGroupReceiveMessageOpt.NotReceive;
    }

    /**
     * 按照群类型获取群
     *
     * @param type 群类型
     */
    public List<ProfileSummary> getGroupListByType(String type) {
        List<ProfileSummary> result = new ArrayList<>();
        if (groups != null && groups.get(type) != null) {
            result.addAll(groups.get(type));
            return result;
        }
        return null;
    }


    public static String getTypeName(String type) {
        if (type.equals(GroupInfo.publicGroup)) {
            return MainApplication.getContext().getString(R.string.public_group);
        } else if (type.equals(GroupInfo.privateGroup)) {
            return MainApplication.getContext().getString(R.string.discuss_group);
        } else if (type.equals(GroupInfo.chatRoom)) {
            return MainApplication.getContext().getString(R.string.chatroom);
        }
        return "";
    }

    /**
     * 通过群id查找群名称
     *
     * @param identify 群id
     */
    public String getGroupName(String identify) {
        for (String key : groups.keySet()) {
            for (GroupProfile item : groups.get(key)) {
                if (item.getIdentify().equals(identify)) return item.getName();
            }
        }
        return "";
    }


    /**
     * 通过群id获取群资料
     *
     * @param type     群类型
     * @param identify 群id
     */
    public GroupProfile getGroupProfile(String type, String identify) {
        for (GroupProfile item : groups.get(type)) {
            if (item.getIdentify().equals(identify)) return item;
        }
        return null;
    }

    /**
     * 清除数据
     */
    public void clear() {
        if (instance == null) return;
        groups.clear();
        instance = null;
    }

}
