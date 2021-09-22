package cn.kurisu.txim.configs;

import com.tencent.imsdk.v2.V2TIMSDKConfig;

/**
 * TUIKit的通用配置，比如可以设置日志打印、音视频录制时长等
 */
public class GeneralConfig {

    public final static int DEFAULT_AUDIO_RECORD_MAX_TIME = 60;
    public final static int DEFAULT_VIDEO_RECORD_MAX_TIME = 15;
    private static final String TAG = GeneralConfig.class.getSimpleName();
    private String appCacheDir;
    /**
     * 录音最大时长
     */
    private int audioRecordMaxTime = DEFAULT_AUDIO_RECORD_MAX_TIME;
    /**
     * 录像最大时长
     */
    private int videoRecordMaxTime = DEFAULT_VIDEO_RECORD_MAX_TIME;
    /**
     * 日志等级
     */
    private int logLevel = V2TIMSDKConfig.V2TIM_LOG_INFO;
    /**
     * 开启日志打印
     */
    private boolean enableLogPrint = true;
    /**
     * 是否展示已读
     */
    private boolean showRead = false;
    /**
     * 是否是测试环境
     */
    private boolean testEnv = false;
    /**
     * appid
     */
    private int sdkAppId = 0;
    /**
     * 用户id
     */
    private String userId = "";
    /**
     * 用户签名
     */
    private String userSig = "";
    /**
     * 用户昵称
     */
    private String userNickname = "";

    private boolean excludedFromUnreadCount;
    private boolean excludedFromLastMessage;

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserFaceUrl() {
        return userFaceUrl;
    }

    public void setUserFaceUrl(String userFaceUrl) {
        this.userFaceUrl = userFaceUrl;
    }

    private String userFaceUrl = "";

    /**
     * 获取是否打印日志
     *
     * @return
     */
    public boolean isLogPrint() {
        return enableLogPrint;
    }

    /**
     * 设置是否打印日志
     *
     * @param enableLogPrint
     */
    public void enableLogPrint(boolean enableLogPrint) {
        this.enableLogPrint = enableLogPrint;
    }

    /**
     * 获取打印的日志级别
     *
     * @return
     */
    public int getLogLevel() {
        return logLevel;
    }

    /**
     * 设置打印的日志级别
     *
     * @param logLevel
     */
    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * 获取TUIKit缓存路径
     *
     * @return
     */
    public String getAppCacheDir() {
        return appCacheDir;
    }

    /**
     * 设置TUIKit缓存路径
     *
     * @param appCacheDir
     * @return
     */
    public GeneralConfig setAppCacheDir(String appCacheDir) {
        this.appCacheDir = appCacheDir;
        return this;
    }

    /**
     * 获取录音最大时长
     *
     * @return
     */
    public int getAudioRecordMaxTime() {
        return audioRecordMaxTime;
    }

    /**
     * 录音最大时长
     *
     * @param audioRecordMaxTime
     * @return
     */
    public GeneralConfig setAudioRecordMaxTime(int audioRecordMaxTime) {
        this.audioRecordMaxTime = audioRecordMaxTime;
        return this;
    }

    /**
     * 获取录像最大时长
     *
     * @return
     */
    public int getVideoRecordMaxTime() {
        return videoRecordMaxTime;
    }

    /**
     * 摄像最大时长
     *
     * @param videoRecordMaxTime
     * @return
     */
    public GeneralConfig setVideoRecordMaxTime(int videoRecordMaxTime) {
        this.videoRecordMaxTime = videoRecordMaxTime;
        return this;
    }

    /**
     * 对方已读的 view 是否展示
     *
     * @return
     */
    public boolean isShowRead() {
        return showRead;
    }

    /**
     * 设置对方已读的 view 是否展示
     *
     * @return
     */
    public void setShowRead(boolean showRead) {
        this.showRead = showRead;
    }

    public boolean isTestEnv() {
        return testEnv;
    }

    public void setTestEnv(boolean testEnv) {
        this.testEnv = testEnv;
    }

    public void setSDKAppId(int sdkAppId) {
        this.sdkAppId = sdkAppId;
    }

    public int getSDKAppId() {
        return sdkAppId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }

    public boolean isExcludedFromUnreadCount() {
        return excludedFromUnreadCount;
    }

    public void setExcludedFromUnreadCount(boolean excludedFromUnreadCount) {
        this.excludedFromUnreadCount = excludedFromUnreadCount;
    }

    public boolean isExcludedFromLastMessage() {
        return excludedFromLastMessage;
    }

    public void setExcludedFromLastMessage(boolean excludedFromLastMessage) {
        this.excludedFromLastMessage = excludedFromLastMessage;
    }
}
