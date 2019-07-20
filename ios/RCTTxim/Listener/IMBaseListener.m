//
//  IMBaseListener.m
//  RCTTxim
//
//  Created by 张建军 on 2019/5/5.
//  Copyright © 2019 feewee. All rights reserved.
//

#import "IMBaseListener.h"

@interface IMBaseListener ()

@property(nonatomic, weak, readwrite) RCTEventEmitter *module;

@end

@implementation IMBaseListener

- (instancetype)initWithModule:(RCTEventEmitter *_Nonnull)module eventName:(NSString *_Nullable)eventName {
  self = [super init];
  if (self) {
    _module = module;
    _eventName = eventName;
  }
  return self;
}

- (void)sendEventWithCode:(int)code msg:(NSString *)msg {
  if (_eventName) {
    [_module sendEvent:_eventName code:code msg:msg];
  }
}

@end
