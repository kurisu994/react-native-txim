export function init(logLevel: number): Promise<boolean>//logLevel[OFF,DEBUG,INFO,WARN.ERROR]

export function login(identify: string, userSign: string): Promise<boolean>

export function logout(): Promise<boolean>

export interface EventListener {
    /**
     * 在线状态
     */
    onlineStatus: string,
    /**
     * 接受新消息
     */
    receiveMessage: string,
    /**
     * 接受当前会话人的新消息
     */
    currentMessage: string,
    /**
     * 系统通知（腾讯官方的）
     */
    receiveSystemMsg: string,
    /**
     * 黑名单
     */
    blackList: string,
    /**
     * 用户状态（6023:其他设备登陆,70001:签名过期）
     */
    userStatus: string,
}

export const event: EventListener;

type ConversationType = 1 | 2;//1：单聊 2：群聊

export function getConversation(type: ConversationType, peer: string): Promise<boolean>

export function destroyConversation(): void

export function sendTextMsg(text: string): Promise<boolean> // 其他参考文档错误码