//
//  IMMessageBuilder.h
//  RCTTxim
//
//  Created by 张建军 on 2019/5/5.
//  Copyright © 2019 feewee. All rights reserved.
//

#import "IMMessageInfo.h"

NS_ASSUME_NONNULL_BEGIN

@interface IMMessageBuilder : NSObject

/**
 * 构建消息
 * @param type 消息类型
 * @param content 消息内容
 * @return 消息
 */
+ (IMMessageInfo *)buildMessage:(IMMessageType)type content:(NSString *)content option:(NSDictionary *)option;

/**
 * 构建文本消息
 * @param content 消息内容
 * @return 消息
 */
+ (IMMessageInfo *)buildTextMessage:(NSString *)content;

/**
 * 根据TIMMessage构建消息
 * @param msg 消息
 * @return 消息
 */
+ (IMMessageInfo *)buildMessageWithTIMMessage:(TIMMessage *)msg;

@end

NS_ASSUME_NONNULL_END
