//
//  IMManager.h
//  RCTTxim
//
//  Created by 张建军 on 2019/5/5.
//  Copyright © 2019 feewee. All rights reserved.
//

#import <ImSDK/IMMessageExt.h>

@class IMMessageInfo;

/**
 *  消息发送成功回调
 *
 *  @param msg 消息
 */
typedef void (^IMSendMsgSucc)(IMMessageInfo *_Nonnull msg);

NS_ASSUME_NONNULL_BEGIN

@interface IMManager : NSObject

/**
 * 获取实例
 */
+ (instancetype)getInstance;

/**
 * 初始化SDK
 */
- (BOOL)initSdk;

/**
 * 初始化SDK
 * @param configFilePath 配置文件路径，默认为mainBundle下的txim.plist
 */
- (BOOL)initSdk:(NSString *_Nullable)configFilePath;

/**
 * 设置连接通知监听器
 * @param listener 监听器
 */
- (void)setConnListener:(id <TIMConnListener>)listener;

/**
 * 设置用户在线状态通知监听器
 * @param listener 监听器
 */
- (void)setUserStatusListener:(id <TIMUserStatusListener>)listener;

/**
 * 添加消息监听器
 * @param listener 监听器
 */
- (void)addMessageListener:(id <TIMMessageListener>)listener;

/**
 * 用户登录
 * @param identify identify
 * @param userSig userSig
 * @param succ 成功回调
 * @param fail 失败回调
 */
- (void)loginWithIdentify:(NSString *)identify
                  userSig:(NSString *)userSig
                     succ:(TIMSucc)succ
                     fail:(TIMFail)fail;

/**
 * 用户登出
 * @param succ 成功回调
 * @param fail 失败回调
 */
- (void)logoutWithSucc:(TIMSucc)succ fail:(TIMFail)fail;

/**
 * 获取会话
 * @param type 会话类型
 * @param receiver 会话接收者
 * @param succ 成功回调
 * @param fail 失败回调
 */
- (void)getConversationWithType:(NSInteger)type
                       receiver:(NSString *)receiver
                           succ:(TIMSucc)succ
                           fail:(TIMFail)fail;

/**
 * 设置消息为已读状态
 * @param message 消息
 */
- (void)setMessageRead:(TIMMessage *)message;

/**
 * 发送消息
 * 参数option:
 * 视频消息参数: (NSString *)imgPath (NSInteger)width (NSInteger)height (NSInteger)duration
 * 图片消息参数: (BOOL)compressed
 * 地理位置参数: (CGFloat)latitude (CGFloat)longitude
 * @param type 消息类型
 * @param content 消息内容
 * @param option 消息参数
 * @param succ 成功回调
 * @param fail 失败回调
 */
- (void)sendMessage:(int)type
            content:(NSString *)content
             option:(NSDictionary *)option
               succ:(IMSendMsgSucc)succ
               fail:(TIMFail)fail;

/**
 * 销毁会话
 */
- (void)destroyConversation;

/**
 * 获取未读消息数量
 * @return 数量
 */
- (int)getUnReadCount;

/**
 * 配置设备token
 * @param token token
 */
- (void)configDeviceToken:(NSData *)token;

/**
 * 切换后台
 */
- (void)switchBackground;

/**
 * 切换前台
 */
- (void)switchForeground;

/**
 * 播放声音
 */
- (void)playSound;

@end

NS_ASSUME_NONNULL_END
