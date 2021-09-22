package cn.kurisu.txim.business.conversation;


import java.util.ArrayList;
import java.util.List;


public class ConversationProvider implements IConversationProvider {

    private ArrayList<ConversationInfo> mDataSource = new ArrayList<>();

    @Override
    public List<ConversationInfo> getDataSource() {
        return mDataSource;
    }

    /**
     * 设置会话数据源
     *
     * @param dataSource
     */
    public void setDataSource(List<ConversationInfo> dataSource) {
        this.mDataSource.clear();
        this.mDataSource.addAll(dataSource);
        //todo 更新ui updateAdapter();
    }


    /**
     * 批量添加会话数据
     *
     * @param conversations 会话数据集合
     * @return
     */
    @Override
    public boolean addConversations(List<ConversationInfo> conversations) {
        if (conversations.size() == 1) {
            ConversationInfo conversation = conversations.get(0);
            for (int i = 0; i < mDataSource.size(); i++) {
                if (mDataSource.get(i).getId().equals(conversation.getId()))
                    return true;
            }
        }
        boolean flag = mDataSource.addAll(conversations);
        if (flag) {
            //todo 更新ui  updateAdapter();
        }
        return flag;
    }

    /**
     * 批量删除会话数据
     *
     * @param conversations 会话数据集合
     * @return
     */
    @Override
    public boolean deleteConversations(List<ConversationInfo> conversations) {
        List<Integer> removeIndexs = new ArrayList();
        for (int i = 0; i < mDataSource.size(); i++) {
            for (int j = 0; j < conversations.size(); j++) {
                if (mDataSource.get(i).getId().equals(conversations.get(j).getId())) {
                    removeIndexs.add(i);
                    conversations.remove(j);
                    break;
                }
            }

        }
        if (removeIndexs.size() > 0) {
            for (int i = 0; i < removeIndexs.size(); i++) {
                mDataSource.remove(removeIndexs.get(i));
            }
            //todo 更新ui updateAdapter();
            return true;
        }
        return false;
    }

    /**
     * 删除单个会话数据
     *
     * @param index 会话在数据源集合的索引
     * @return
     */
    public void deleteConversation(int index) {
        if (mDataSource.remove(index) != null) {
            //todo 更新ui updateAdapter();
        }

    }

    /**
     * 删除单个会话数据
     *
     * @param conversationID 会话ID
     * @return
     */
    public void deleteConversation(String conversationID) {
        for (int i = 0; i < mDataSource.size(); i++) {
            if (mDataSource.get(i).getConversationId().equals(conversationID)) {
                if (mDataSource.remove(i) != null) {
                    //todo 更新ui updateAdapter();
                }
                return;
            }
        }
    }

    /**
     * 批量更新会话
     *
     * @param conversations 会话数据集合
     * @return
     */
    @Override
    public boolean updateConversations(List<ConversationInfo> conversations) {
        boolean flag = false;
        for (int i = 0; i < mDataSource.size(); i++) {
            for (int j = 0; j < conversations.size(); j++) {
                ConversationInfo update = conversations.get(j);
                if (mDataSource.get(i).getId().equals(update.getId())) {
                    mDataSource.remove(i);
                    mDataSource.add(i, update);
                    conversations.remove(j);
                    flag = true;
                    break;
                }
            }

        }
        if (flag) {
            //todo 更新ui updateAdapter();
            return true;
        } else {
            return false;
        }

    }

    /**
     * 清空会话
     */
    public void clear() {
        mDataSource.clear();
        //todo 更新ui
    }

}
