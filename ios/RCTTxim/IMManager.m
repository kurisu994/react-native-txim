//
//  IMManager.m
//  RCTTxim
//
//  Created by 张建军 on 2019/5/5.
//  Copyright © 2019 feewee. All rights reserved.
//

#import "IMManager.h"
#import "IMMessageBuilder.h"
#import <AVFoundation/AVFoundation.h>

@implementation IMManager {
  /// 是否初始化
  BOOL isInit;
  /// 应用ID
  int sdkAppId;
  /// 会话
  TIMConversation *conversation;
  /// 会话
  NSString *currentReceiver;
  /// 设备token
  NSData *deviceToken;
  /// IM配置
  NSDictionary *configDict;
  /// 声音ID
  SystemSoundID soundID;
}

+ (instancetype)getInstance {
  __strong static IMManager *instance;
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    instance = [self new];
  });
  return instance;
}

- (BOOL)initSdk {
  return [self initSdk:nil];
}

#pragma clang diagnostic push
#pragma ide diagnostic ignored "ResourceNotFoundInspection"

- (BOOL)initSdk:(NSString *)configFilePath {
  if (isInit) {
    return YES;
  }
  DEFINE_TM(tm);
  // 初始化SDK基本配置
  TIMSdkConfig *sdkConfig = [TIMSdkConfig new];
  NSString *path;
  if (configFilePath) {
    path = configFilePath;
  } else {
    path = [[NSBundle mainBundle] pathForResource:@"txim" ofType:@"plist"];
  }
  if (!path) {
    IM_LOG_TAG_ERROR(@"Init", @"未找到IM配置文件");
    return NO;
  }
  configDict = [[NSDictionary alloc] initWithContentsOfFile:path];
  // 用户标识接入SDK的应用ID
  id sdkAppIdValue = [configDict valueForKey:@"sdkAppId"];
  if (!sdkAppIdValue) {
    IM_LOG_TAG_ERROR(@"Init", @"未配置sdkAppId");
    return NO;
  }
  sdkAppId = [sdkAppIdValue intValue];
  sdkConfig.sdkAppId = sdkAppId;
  NSString *accountType = [configDict valueForKey:@"accountType"];
  if (!accountType) {
    IM_LOG_TAG_ERROR(@"Init", @"未配置accountType");
    return NO;
  }
  sdkConfig.accountType = accountType;
  // 是否crash上报
  id disableCrashReportValue = [configDict valueForKey:@"disableCrashReport"];
  if (disableCrashReportValue) {
    sdkConfig.disableCrashReport = [disableCrashReportValue boolValue];
  }
  // 是否允许log打印
  id disableLogPrintValue = [configDict valueForKey:@"disableLogPrint"];
  if (disableLogPrintValue) {
    sdkConfig.disableLogPrint = [disableLogPrintValue boolValue];
  }
  // Log输出级别, 默认DEBUG等级
  id logLevelValue = [configDict valueForKey:@"logLevel"];
  if (logLevelValue) {
    sdkConfig.logLevel = (TIMLogLevel) [logLevelValue integerValue];
  }
  // 消息提示声音
  id soundValue = [configDict valueForKey:@"sound"];
  if (soundValue) {
    NSURL *soundUrl = [[NSBundle mainBundle] URLForResource:soundValue withExtension:nil];
    if (soundUrl) {
      AudioServicesCreateSystemSoundID((__bridge CFURLRef) (soundUrl), &soundID);
    }
  }
  int result = [tm initSdk:sdkConfig];
  if (result != 0) {
    return NO;
  }
  isInit = YES;
  return isInit;
}

- (void)setConnListener:(id <TIMConnListener>)listener {
  if (isInit) {
    DEFINE_TM(tm);
    [[tm getGlobalConfig] setConnListener:listener];
  }
}

- (void)setUserStatusListener:(id <TIMUserStatusListener>)listener {
  if (isInit) {
    DEFINE_TM(tm);
    // 用户配置
    TIMUserConfig *userConfig = [TIMUserConfig new];
    [userConfig setDisableStorage:YES];
    [userConfig setDisableAutoReport:YES];
    [userConfig setUserStatusListener:listener];
    int restlt = [tm setUserConfig:userConfig];
    NSLog(@"off设置Listener:%@", @(restlt));
  }
}

- (void)addMessageListener:(id <TIMMessageListener>)listener {
  if (isInit) {
    DEFINE_TM(tm);
    [tm addMessageListener:listener];
  }
}

#pragma clang diagnostic pop

- (void)loginWithIdentify:(NSString *)identify
                  userSig:(NSString *)userSig
                     succ:(TIMLoginSucc)succ
                     fail:(TIMFail)fail {
  // 登录参数
  TIMLoginParam *loginParam = [TIMLoginParam new];
  loginParam.identifier = identify;
  loginParam.userSig = userSig;
  loginParam.appidAt3rd = [NSString stringWithFormat:@"%d", sdkAppId];
  DEFINE_TM(tm);
  void (^login)(void) = ^(void) {
    int result = [tm login:loginParam
                      succ:^{
                        [self configAppAPNSDeviceToken];
                        succ();
                      } fail:fail];
    if (result != 0) {
      fail(result, @"调用登录失败");
    }
  };
  // 判断是否已经登录
  if ([tm getLoginStatus] == TIM_STATUS_LOGINED) {
    // 判断是否已经登录了当前账号
    if ([[tm getLoginUser] isEqualToString:identify]) {
      login();
    } else {
      // 登出之前的账号
      int result = [tm logout:^{
        login();
      }                  fail:fail];
      if (result != 0) {
        fail(result, @"切换登录失败");
      }
    }
  } else {
    login();
  }
}

- (void)logoutWithSucc:(TIMLoginSucc)succ fail:(TIMFail)fail {
  DEFINE_TM(tm);
  if ([tm getLoginStatus] == TIM_STATUS_LOGOUT) {
    succ();
  } else {
    int result = [tm logout:succ fail:fail];
    if (result != 0) {
      fail(result, @"调用登出失败");
    }
  }
}

- (void)getConversationWithType:(NSInteger)type
                       receiver:(NSString *)receiver
                           succ:(TIMSucc)succ
                           fail:(TIMFail)fail {
  DEFINE_TM(tm);
  conversation = [tm getConversation:(TIMConversationType) type receiver:receiver];
  if (conversation) {
    [conversation setReadMessage:nil succ:nil fail:nil];
    currentReceiver = receiver;
    succ();
  } else {
    fail(-1, @"获取会话失败");
  }
}

- (void)setMessageRead:(TIMMessage *)message {
  if ([[[message getConversation] getReceiver] isEqualToString:[conversation getReceiver]]) {
    [conversation setReadMessage:message succ:nil fail:nil];
  }
}

- (void)sendMessage:(int)type
            content:(NSString *)content
             option:(NSDictionary *)option
               succ:(IMSendMsgSucc)succ
               fail:(TIMFail)fail {
  DEFINE_TM(tm);
  if ([tm getLoginStatus] != TIM_STATUS_LOGINED) {
    fail(-1, @"请先登录");
    return;
  }
  if (!conversation) {
    fail(-1, @"当前会话已被销毁，请重新获取");
    return;
  }
  IMMessageInfo *info = [IMMessageBuilder buildMessage:(IMMessageType) type content:content option:option];
  // info.sender = [conversation getSelfIdentifier];
  // info.receiver = [conversation getReceiver];
  info.sender = [tm getLoginUser];
  info.receiver = currentReceiver;
  info.isSelf = YES;
  int result = [conversation sendMessage:[info msg]
                                    succ:^{
                                      succ(info);
                                    } fail:fail];
  if (result != 0) {
    fail(-1, @"消息发送失败");
  }
}

- (void)destroyConversation {
  if (conversation) {
    DEFINE_TM(tm);
    [tm deleteConversation:[conversation getType] receiver:[conversation getReceiver]];
    conversation = nil;
    currentReceiver = nil;
  }
}

- (int)getUnReadCount {
  __block int unReadCount = 0;
  DEFINE_TM(tm);
  [[tm getConversationList] enumerateObjectsUsingBlock:^(TIMConversation *con, NSUInteger idx, BOOL *stop) {
    if ([con getType] == TIM_C2C) {
      unReadCount += [con getUnReadMessageNum];
    }
  }];
  return unReadCount;
}

- (void)configDeviceToken:(NSData *)token {
  deviceToken = token;
}

- (void)switchBackground {
  DEFINE_TM(tm);
  TIMBackgroundParam *param = [TIMBackgroundParam new];
  [param setC2cUnread:[self getUnReadCount]];
  [tm doBackground:param succ:^{
    IM_LOG_TAG_INFO(@"doBackground", @"doBackground成功");
  }           fail:^(int code, NSString *msg) {
    IM_LOG_TAG_WARN(@"doBackground", @"doBackground失败，错误码：%d，原因：%@", code, msg);
  }];
}

- (void)switchForeground {
  DEFINE_TM(tm);
  [tm doForeground:^{
    IM_LOG_TAG_INFO(@"doForeground", @"doForeground成功");
  }           fail:^(int code, NSString *msg) {
    IM_LOG_TAG_WARN(@"doForeground", @"doForeground失败，错误码：%d，原因：%@", code, msg);
  }];
}

- (void)playSound {
  // 播放
  if (soundID) {
    AudioServicesPlaySystemSound(soundID);
  }
}

/**
 * 配置设备token
 */
- (void)configAppAPNSDeviceToken {
  DEFINE_TM(tm);
  // APNS配置
  TIMAPNSConfig *apnsConfig = [TIMAPNSConfig new];
  [apnsConfig setOpenPush:1];
  [tm setAPNS:apnsConfig succ:^{
    IM_LOG_TAG_INFO(@"APNS", @"APNS配置成功");
  } fail:^(int code, NSString *msg) {
    IM_LOG_TAG_WARN(@"APNS", @"APNS配置失败，错误码：%d，原因：%@", code, msg);
  }];
  NSString *token = [NSString stringWithFormat:@"%@", deviceToken];
  IM_LOG_TAG_INFO(@"SetToken", @"Token is : %@", token);
  TIMTokenParam *param = [TIMTokenParam new];
#if kAppStoreVersion// AppStore 版本
  #if DEBUG
  param.busiId = (uint32_t) [[configDict valueForKey:@"debugBusiId"] unsignedIntegerValue];
#else
  param.busiId = (uint32_t) [[configDict valueForKey:@"busiId"] unsignedIntegerValue];
#endif
#else// 企业证书 ID
  param.busiId = (uint32_t) [[configDict valueForKey:@"busiId"] unsignedIntegerValue];
#endif
  [param setToken:deviceToken];
  [tm setToken:param
          succ:^{
            IM_LOG_TAG_INFO(@"SetToken", @"上传token成功");
          }
          fail:^(int code, NSString *msg) {
            IM_LOG_TAG_WARN(@"SetToken", @"上传token失败，错误码：%d，原因：%@", code, msg);
          }];
}

@end
