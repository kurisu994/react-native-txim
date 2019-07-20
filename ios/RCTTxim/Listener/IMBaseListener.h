//
//  IMBaseListener.h
//  RCTTxim
//
//  Created by 张建军 on 2019/5/5.
//  Copyright © 2019 feewee. All rights reserved.
//

#import <ImSDK/ImSDK.h>
#import "RCTEventEmitter+IMBaseModule.h"

NS_ASSUME_NONNULL_BEGIN

@interface IMBaseListener : NSObject

/**
 * 模块
 */
@property(nonatomic, weak, readonly) RCTEventEmitter *module;
/**
 * 监听的事件名称
 */
@property(nonatomic, strong, readonly) NSString *eventName;

/**
 * 根据模块初始化
 * @param module 模块
 * @param eventName 事件名称
 * @return 监听器
 */
- (instancetype)initWithModule:(RCTEventEmitter *_Nonnull)module eventName:(NSString *_Nullable)eventName;

/**
 * 发送事件消息
 * @param code 消息代码
 * @param msg 消息内容
 */
- (void)sendEventWithCode:(int)code msg:(NSString *)msg;

@end

NS_ASSUME_NONNULL_END
