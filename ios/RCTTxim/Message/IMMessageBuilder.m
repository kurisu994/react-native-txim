//
//  IMMessageBuilder.m
//  RCTTxim
//
//  Created by 张建军 on 2019/5/5.
//  Copyright © 2019 feewee. All rights reserved.
//

#import "IMMessageBuilder.h"

#define CURRENT_TIMESTAMP [[NSDate date] timeIntervalSince1970] * 1000

@implementation IMMessageBuilder

+ (IMMessageInfo *)buildMessage:(IMMessageType)type content:(NSString *)content option:(NSDictionary *)option {
  switch (type) {
    case IMMessageTypeText:
      return [self buildTextMessage:content];
    default:
      return nil;
  }
}

+ (IMMessageInfo *)buildTextMessage:(NSString *)content {
  IMMessageInfo *info = [[IMMessageInfo alloc] initWithType:IMMessageTypeText];
  TIMMessage *msg = [TIMMessage new];
  TIMTextElem *elem = [TIMTextElem new];
  [elem setText:content];
  [msg addElem:elem];
  info.msg = msg;
  info.msgTime = CURRENT_TIMESTAMP;
  info.extra = content;
  return info;
}

+ (IMMessageInfo *)buildMessageWithTIMMessage:(TIMMessage *)msg {
  if (!msg || msg.status == TIM_MSG_STATUS_HAS_DELETED) {
    return nil;
  }
  if ([msg elemCount] == 0) {
    return nil;
  }
  IMMessageInfo *info = [IMMessageInfo new];
  TIMElem *elem = [msg getElem:0];
  // 消息类型，内容
  if ([elem isKindOfClass:[TIMTextElem class]]) {
    info.msgType = IMMessageTypeText;
    TIMTextElem *textElem = (TIMTextElem *) elem;
    info.extra = [textElem text];
  } else if ([elem isKindOfClass:[TIMCustomElem class]]) {
    info.msgType = IMMessageTypeCustom;
    TIMCustomElem *customElem = (TIMCustomElem *) elem;
    info.extra = [[NSString alloc] initWithData:[customElem data] encoding:NSUTF8StringEncoding];
  } else {
    return nil;
  }
  // 推送信息字段
  TIMOfflinePushInfo *pushInfo = [msg getOfflinePushInfo];
  if (pushInfo) {
    info.desc = [pushInfo desc];
    info.extra = [pushInfo ext];
  }
  // 消息基本信息
  info.msg = msg;
  info.msgId = [msg msgId];
  info.msgTime = [[msg timestamp] timeIntervalSince1970] * 1000;
  info.isSelf = [msg isSelf];
  info.status = [msg status];
  if (info.isSelf) {// 发送消息
    // 发送方信息，这里为自己
    info.sender = [msg sender];
    // 接收方信息
    info.receiver = [[msg getConversation] getReceiver];
    info.isRead = [msg isPeerReaded];
  } else {// 接收消息
    // 发送方信息
    TIMUserProfile *profile = [msg getSenderProfile];
    info.sender = [profile identifier];
    info.senderAvatar = [profile faceURL];
    info.senderNickName = [profile nickname];
    // 接收方信息，这里为自己
    info.receiver = [[msg getConversation] getSelfIdentifier];
    info.isRead = [msg isReaded];
  }
  return info;
}

@end
