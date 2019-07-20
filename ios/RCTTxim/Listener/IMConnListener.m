//
//  IMConnListener.m
//  RCTTxim
//
//  Created by 张建军 on 2019/5/5.
//  Copyright © 2019 feewee. All rights reserved.
//

#import "IMConnListener.h"

#define LOG_TAG @"连接状态改变"
#define IM_LOG_INFO(MSG, ...) IM_LOG_TAG_INFO(LOG_TAG, MSG, ##__VA_ARGS__)
#define IM_LOG_WARN(MSG, ...) IM_LOG_TAG_WARN(LOG_TAG, MSG, ##__VA_ARGS__)

@implementation IMConnListener

#pragma mark - TIMConnListener

- (void)onConnSucc {
  IM_LOG_INFO(@"网络连接成功");
}

- (void)onConnFailed:(int)code err:(NSString *)err {
  IM_LOG_WARN(@"网络连接失败，错误码：%@，原因：%@", @(code), err);
}

- (void)onDisconnect:(int)code err:(NSString *)err {
  IM_LOG_WARN(@"网络连接断开，错误码：%@，原因：%@", @(code), err);
}

- (void)onConnecting {
  IM_LOG_INFO(@"网络连接中");
}

@end
