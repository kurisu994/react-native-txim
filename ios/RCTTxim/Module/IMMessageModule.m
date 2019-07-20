//
//  IMMessageModule.m
//  RCTTxim
//
//  Created by 张建军 on 2019/5/5.
//  Copyright © 2019 feewee. All rights reserved.
//

#import "IMMessageModule.h"
#import "IMEventNameConstant.h"
#import "IMManager.h"
#import "IMMessageInfo.h"

@implementation IMMessageModule

#pragma mark - RCTEventEmitter

- (NSArray<NSString *> *)supportedEvents {
  return @[EventNameOnNewMessage];
}

- (void)startObserving {
  [self setHasListeners:YES];
}

- (void)stopObserving {
  [self setHasListeners:NO];
}

#pragma mark - RCTBridgeModule

+ (BOOL)requiresMainQueueSetup {
  return NO;
}

/// 导出模块名称
RCT_EXPORT_MODULE(IMMessageModule);

/// 获取会话
// @formatter:off
RCT_REMAP_METHOD(getConversation,
                 getConversationWithType:(NSInteger)type
                 receiver:(NSString *)receiver
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
  // @formatter:on
  IMManager *manager = [IMManager getInstance];
  [manager getConversationWithType:type receiver:receiver
                              succ:^{
                                resolve(@{
                                  @"code": @(0),
                                  @"msg": @"获取会话成功!",
                                });
                              }
                              fail:^(int code, NSString *msg) {
                                reject([NSString stringWithFormat:@"%d", code], msg, nil);
                              }];
}

/// 发送消息
// @formatter:off
RCT_REMAP_METHOD(sendMessage,
                 sendMessage:(int)type
                 content:(NSString *)content
                 option:(NSDictionary *)option
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
  // @formatter:on
  IMManager *manager = [IMManager getInstance];
  __weak typeof(self) weakSelf = self;
  [manager sendMessage:type content:content option:option
                  succ:^(IMMessageInfo *msg) {
                    IM_LOG_TAG_INFO(@"发送消息", @"消息发送成功");
                    [weakSelf sendEvent:EventNameOnNewMessage body:@[[msg toDict]]];
                    resolve(@{
                      @"code": @(0),
                      @"msg": @"获取会话成功!",
                    });
                  }
                  fail:^(int code, NSString *msg) {
                    IM_LOG_TAG_ERROR(@"发送消息", @"消息发送失败，错误码：%d，原因：%@", code, msg);
                    reject([NSString stringWithFormat:@"%d", code], msg, nil);
                  }];
}

/// 销毁会话
// @formatter:off
RCT_EXPORT_METHOD(destroyConversation) {
  // @formatter:on
  IMManager *manager = [IMManager getInstance];
  [manager destroyConversation];
}

@end
