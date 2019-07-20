//
//  RCTEventEmitter+IMBaseModule.m
//  RCTTxim
//
//  Created by 张建军 on 2019/5/6.
//  Copyright © 2019 feewee. All rights reserved.
//

#import "RCTEventEmitter+IMBaseModule.h"
#import <objc/runtime.h>
#import <React/RCTLog.h>

const char *key = "hasListeners";

@implementation RCTEventEmitter (IMBaseModule)

- (instancetype)init {
  self = [super init];
  if (self && [self respondsToSelector:@selector(configListener)]) {
    [self configListener];
  }
  return self;
}

- (void)sendEvent:(NSString *)eventName body:(id)body {
  if (self.hasListeners) {
    [self sendEventWithName:eventName body:body];
  } else {
    [self ignoreEventWithName:eventName body:body];
  }
}

- (void)sendEvent:(NSString *)name code:(int)code msg:(NSString *)msg {
  [self sendEvent:name body:@{
    @"code": @(code),
    @"msg": msg,
  }];
}

+ (BOOL)requiresMainQueueSetup {
  return NO;
}

- (void)setHasListeners:(BOOL)hasListeners {
  objc_setAssociatedObject(self, key, @(hasListeners), OBJC_ASSOCIATION_ASSIGN);
}

- (BOOL)hasListeners {
  id value = objc_getAssociatedObject(self, key);
  if (value) {
    return [value boolValue];
  }
  return NO;
}

- (void)ignoreEventWithName:(NSString *)name body:(id)body {
  RCTLog(@"忽略事件发送%@, %@", name, body);
}

@end
