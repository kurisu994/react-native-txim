export function init(logLevel: number): Promise<boolean>

export function login(identify: string, userSign: string): Promise<boolean>

export function logout(): void

export interface EventListenter {
  recentContact: "observeRecentContact",
  onlineStatus: "observeOnlineStatus",
  friend: "observeFriend",
  team: "observeTeam",
  receiveMessage: "observeReceiveMessage",
  currentMessage: "observeCurrentMessage",

  deleteMessage: "observeDeleteMessage",
  receiveSystemMsg: "observeReceiveSystemMsg",
  msgStatus: "observeMsgStatus",
  audioRecord: "observeAudioRecord",
  unreadCountChange: "observeUnreadCountChange",
  blackList: "observeBlackList",
  attachmentProgress: "observeAttachmentProgress",
  onKick: "observeOnKick",
  accountNotice: "observeAccountNotice",
  launchPushEvent: "observeLaunchPushEvent",
  backgroundPushEvent: "observeBackgroundPushEvent"
}

export const event: EventListener;

type ConversationType = 1 | 2;

export function getConversation(type: ConversationType, peer: string): Promise<any>

export function destroyConversation(): void

export function sendTextMsg(text: string): Promise<any>