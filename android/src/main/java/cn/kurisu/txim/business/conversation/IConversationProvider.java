package cn.kurisu.txim.business.conversation;


import java.util.List;

/**
 * 会话列表数据源
 */

public interface IConversationProvider {

    /**
     * 获取具体的会话数据集合，ConversationContainer依据该数据集合展示会话列表
     *
     * @return
     */
    List<ConversationInfo> getDataSource();

    /**
     * 批量添加会话条目
     *
     * @param conversations 会话数据集合
     * @return
     */
    boolean addConversations(List<ConversationInfo> conversations);

    /**
     * 删除会话条目
     *
     * @param conversations 会话数据集合
     * @return
     */
    boolean deleteConversations(List<ConversationInfo> conversations);

    /**
     * 更新会话条目
     *
     * @param conversations 会话数据集合
     * @return
     */
    boolean updateConversations(List<ConversationInfo> conversations);

}
