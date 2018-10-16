export function init(logLevel: number): Promise<boolean>//logLevel[OFF,DEBUG,INFO,WARN.ERROR]

export function login(identify: string, userSign: string): Promise<boolean>

export function logout(): Promise<boolean>

export interface EventListenter {
    onlineStatus: "observeOnlineStatus", //在线状态
    receiveMessage: "observeReceiveMessage",//接受新消息
    currentMessage: "observeCurrentMessage",//接受当前会话人的新消息
    receiveSystemMsg: "observeReceiveSystemMsg",//系统通知（腾讯官方的）
    blackList: "observeBlackList",//黑名单
    userStatus: "observeUserStatus",//用户状态（6023:其他设备登陆,70001:签名过期）
}

export const event: EventListener;

type ConversationType = 1 | 2;//1：单聊 2：群聊

export function getConversation(type: ConversationType, peer: string): Promise<boolean>

export function destroyConversation(): void

export function sendTextMsg(text: string): Promise<boolean> // 其他参考文档错误码