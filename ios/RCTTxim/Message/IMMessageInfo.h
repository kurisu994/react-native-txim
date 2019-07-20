//
//  IMMessageInfo.h
//  RCTTxim
//
//  Created by 张建军 on 2019/5/5.
//  Copyright © 2019 feewee. All rights reserved.
//

#import <ImSDK/ImSDK.h>

/// 消息类型
typedef NS_ENUM(int, IMMessageType) {
  IMMessageTypeText = 1,/// 文本类型消息
  IMMessageTypeImage,/// 语音类型消息
  IMMessageTypeAudio,/// 语音类型消息
  IMMessageTypeVideo,/// 视频类型消息
  IMMessageTypeFile,/// 文件类型消息
  IMMessageTypeLocation,/// 位置类型消息
  IMMessageTypeCustomFace,/// 自定义图片类型消息
  IMMessageTypeCustom,/// 自定义消息
};


NS_ASSUME_NONNULL_BEGIN

@interface IMMessageInfo : NSObject

/**
 * 消息
 */
@property(nonatomic, strong) TIMMessage *msg;
/**
 * 消息id
 */
@property(nonatomic, strong) NSString *msgId;
/**
 * 消息类型
 */
@property(nonatomic, assign) IMMessageType msgType;
/**
 * 消息时间
 */
@property(nonatomic, assign) double msgTime;
/**
 * 消息是否已读
 */
@property(nonatomic, assign) BOOL isRead;
/**
 * 消息状态
 */
@property(nonatomic, assign) NSInteger status;
/**
 * 消息发送方是否为自己
 */
@property(nonatomic, assign) BOOL isSelf;
/**
 * 消息发送方id
 */
@property(nonatomic, strong) NSString *sender;
/**
 * 消息发送方头像
 */
@property(nonatomic, strong) NSString *senderAvatar;
/**
 * 消息发送方昵称
 */
@property(nonatomic, strong) NSString *senderNickName;
/**
 * 消息接收方id
 */
@property(nonatomic, strong) NSString *receiver;
/**
 * 扩展信息
 */
@property(nonatomic, strong) NSString *extra;
/**
 * 描述信息
 */
@property(nonatomic, strong) NSString *desc;

/**
 * 根据消息类型初始化消息
 * @param type 消息类型
 * @return 消息
 */
- (instancetype)initWithType:(IMMessageType)type;

/**
 * 消息转化为字典对象
 * @return 字典对象
 */
- (NSDictionary *)toDict;

@end

NS_ASSUME_NONNULL_END
