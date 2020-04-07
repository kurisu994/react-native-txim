import { EmitterSubscription } from 'react-native';

export class IMInitModule {
  /**添加用户在线状态监听器*/
  static addOnlineStatusListener: (listener: (response: IMResponse) => void, context?: any) => EmitterSubscription;
  /**登录*/
  static login: (identify: string, userSign: string) => Promise<IMResponse>;
  /**登出*/
  static logout: () => Promise<boolean>;
}

export class IMChatModule {
  /**添加消息接收监听器*/
  static addMessageReceiveListener: (listener: (messages: IMMessage[]) => void, context?: any) => EmitterSubscription;
  /**消息列表发生改变时的监听 */
  static addConversationRefreshListener: (listener: () => void, context?: any) => EmitterSubscription;
  /**获取会话*/
  static getConversation: (type: ConversationType, peer: string) => Promise<boolean>;
  /**设置消息已读 */
  static readMessage: () => any
  /**获取会话列表 */
  static getConversationList: () => Promise<IMConversationList>;
  /**获取消息列表 */
  static getMessage: (pageSize: number, type: ConversationType) => Promise<IMMessageList>;
  /**销毁会话*/
  static destroyConversation: () => Promise<boolean>;
  /**发送文本消息*/
  static sendTextMsg: (text: string) => Promise<boolean>;
  /**发送图片消息 original:是否发送原图 默认false*/
  static sendImageMsg: (path: string, original?: boolean) => Promise<boolean>;
  /**发送语音消息  duration：时长*/
  static sendAudioMsg: (path: string, duration: number) => Promise<boolean>;
}

/**
 * 消息类型
 */
declare enum IMMessageType {
  Text,
  Image,
  Sound,
  Video,
  File,
  Location,
  Face,
  Custom
}

/**聊天类型*/
declare enum ConversationType {
  /**单聊*/
  C2C,
  /**群聊*/
  GROUP,
}

/**IM响应*/
export interface IMResponse {
  /**代码*/
  code: number,
  /**描述*/
  msg: string
}

/**IM会话列表响应 */
export interface IMConversationList extends IMResponse {
  data: ConversationItem[]
}

/**会话信息 */
export interface ConversationItem {
  /**聊天对象账号 */
  peer: string,
  /**最后一条信息 */
  message?: IMMessage,
  /**会话类型 */
  type: ConversationType,
  /**群名 */
  groupName: string,
  /**未读数 */
  unread: number
}

/**消息列表 */
export interface IMMessageList extends IMResponse {
  data: IMMessage[]
}

export interface IMMessage {
  /**发送方*/
  sender: string,
  /**发送方昵称*/
  nickName: string,
  /**发送方头像*/
  senderAvatar: string
  /**本次聊天的目标对象 对于接收方而言*/
  peer: string,
  /**消息id*/
  msgId: string,
  /**是否是自己发送的*/
  self: boolean,
  /**是否已读*/
  read: boolean,
  /**是否是群消息*/
  group: boolean,
  /**图片或者语音或者视频等的路径*/
  dataPath: string,
  /**图片或者语音或者视频等的uri路径*/
  dataUri: string,
  /**消息时间*/
  msgTime: string,
  /**消息展示项 自定义消息的话就是携带信息*/
  extra: string,
  /**消息状态（发送成功与否）*/
  status: number,
  /**消息类型*/
  msgType: number,
  /**图片宽度*/
  imgWidth: number,
  /**图片高度*/
  imgHeight: number,
  /**自定义消息的展示标题*/
  data: string,
  /**纬度*/
  lat: number,
  /**经度*/
  lng: number,
  /**描述（位置信息、自定义消息）*/
  desc: string

  [key: string]: any
}
