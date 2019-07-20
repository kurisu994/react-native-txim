//
//  IMInitializeModule.m
//  RCTTxim
//
//  Created by 张建军 on 2019/5/5.
//  Copyright © 2019 feewee. All rights reserved.
//

#import "IMInitializeModule.h"
#import "IMManager.h"
#import "IMEventNameConstant.h"
#import "IMConnListener.h"
#import "IMMessageListener.h"
#import "IMUserStatusListener.h"
#import "IMMessageInfo.h"

@implementation IMInitializeModule

#pragma mark - RCTEventEmitter

- (NSArray<NSString *> *)supportedEvents {
  return @[EventNameInitializeStatus, EventNameUserStatusChange, EventNameOnNewMessage];
}

- (void)configListener {
  IMManager *manager = [IMManager getInstance];

  [manager setConnListener:[[IMConnListener alloc] initWithModule:self eventName:nil]];
  [manager setUserStatusListener:[[IMUserStatusListener alloc] initWithModule:self
                                                                    eventName:EventNameUserStatusChange]];
  [manager addMessageListener:[[IMMessageListener alloc] initWithModule:self eventName:EventNameOnNewMessage]];
}

- (void)startObserving {
  [self setHasListeners:YES];
}

- (void)stopObserving {
  [self setHasListeners:NO];
}

#pragma mark - RCTBridgeModule

+ (BOOL)requiresMainQueueSetup {
  return YES;
}

- (NSDictionary *)constantsToExport {
  // 事件名称
  NSDictionary *eventNameDict = @{
    @"loginStatus": EventNameLoginStatus,
    @"initializeStatus": EventNameInitializeStatus,
    @"userStatus": EventNameUserStatusChange,
    @"onNewMessage": EventNameOnNewMessage,
  };
  // 消息类型
  NSDictionary *messageTypeDict = @{
    @"Text": @(IMMessageTypeText),
    @"Image": @(IMMessageTypeImage),
    @"Sound": @(IMMessageTypeAudio),
    @"Video": @(IMMessageTypeVideo),
    @"File": @(IMMessageTypeFile),
    @"Location": @(IMMessageTypeLocation),
    @"Face": @(IMMessageTypeCustomFace),
    @"Custom": @(IMMessageTypeCustom),
  };
  return @{
    @"EventName": eventNameDict,
    @"MessageType": messageTypeDict,
  };
}

/// 导出模块名称
RCT_EXPORT_MODULE(IMInitializeModule);

/// 用户登录
// @formatter:off
RCT_REMAP_METHOD(login,
                 loginWithAccount:(NSString *)account
                 andUserSig:(NSString *)userSig
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
  // @formatter:on
  IMManager *manager = [IMManager getInstance];
  [manager loginWithIdentify:account
                     userSig:userSig
                        succ:^{
                          resolve(@{
                            @"code": @(0),
                            @"msg": @"登录成功!",
                          });
                        }
                        fail:^(int code, NSString *msg) {
                          reject([NSString stringWithFormat:@"%d", code], msg, nil);
                        }];
}

/// 用户注销
// @formatter:off
RCT_REMAP_METHOD(logout,
                 logoutWithResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
  // @formatter:on
  IMManager *manager = [IMManager getInstance];
  [manager logoutWithSucc:^{
    resolve(@(YES));
  }                  fail:^(int code, NSString *msg) {
    reject([NSString stringWithFormat:@"%@", @(code)], msg, nil);
  }];
}

#pragma mark - TIMConnListener


@end
