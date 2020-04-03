package cn.kurisu.txim.constants;

public class IMEventNameConstant {
    /**
     * 用户状态改变
     */
    public final static String USER_STATUS_CHANGE = "userStatus";

    /**
     * 初始化状态
     */
    public final static String INITIALIZE_STATUS = "initializeStatus";
    /**
     * 登录状态
     */
    public final static String LOGIN_STATUS = "loginStatus";
    /**
     * 消息发送状态
     */
    public final static String SEND_STATUS = "sendStatus";

    /**
     * 收到消息时的监听
     */
    public final static String ON_NEW_MESSAGE = "onNewMessage";

    /**
     *
     * 获取会话
     */
    public final static String CONVERSATION_STATUS = "conversationStatus";

     /**
     * 获取会话列表事件
     */
    public final static String CONVERSATION_LIST_STATUS = "conversationListStatus";

     /**
     * 会话列表刷新
     */
    public final static String ON_CONVERSATION_REFRESH = "onConversationRefresh";
}
