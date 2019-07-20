//
//  IMMessageListener.m
//  RCTTxim
//
//  Created by 张建军 on 2019/5/5.
//  Copyright © 2019 feewee. All rights reserved.
//

#import "IMMessageListener.h"
#import "IMMessageBuilder.h"
#import "IMManager.h"

@implementation IMMessageListener

- (void)onNewMessage:(NSArray<TIMMessage *> *)msgs {
  NSUInteger count = [msgs count];
  IM_LOG_TAG_INFO(@"新消息", @"收到%@条消息", @([msgs count]));
  if (count == 0) {
    return;
  }
  NSMutableArray *array = [NSMutableArray new];
  [msgs enumerateObjectsUsingBlock:^(TIMMessage *obj, NSUInteger idx, BOOL *stop) {
    [array addObject:[[IMMessageBuilder buildMessageWithTIMMessage:obj] toDict]];
  }];
  [super.module sendEvent:super.eventName body:array];

  [[IMManager getInstance] playSound];
}

@end
