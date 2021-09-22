package cn.kurisu.txim.configs;

import com.tencent.imsdk.v2.V2TIMSDKConfig;

public class TIMConfigs {

    private static TIMConfigs sConfigs;
    private GeneralConfig generalConfig;
    private CustomFaceConfig customFaceConfig;
    private V2TIMSDKConfig sdkConfig;

    private TIMConfigs() {

    }

    /**
     * 获取TUIKit的全部配置
     *
     * @return
     */
    public static TIMConfigs getConfigs() {
        if (sConfigs == null) {
            sConfigs = new TIMConfigs();
        }
        return sConfigs;
    }

    /**
     * 获取通用配置
     *
     * @return
     */
    public GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    /**
     * 设置通用配置
     *
     * @param generalConfig
     * @return
     */
    public TIMConfigs setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
        return this;
    }

    /**
     * 获取自定义表情包配置
     *
     * @return
     */
    public CustomFaceConfig getCustomFaceConfig() {
        return customFaceConfig;
    }

    /**
     * 设置自定义表情包配置
     *
     * @param customFaceConfig
     * @return
     */
    public TIMConfigs setCustomFaceConfig(CustomFaceConfig customFaceConfig) {
        this.customFaceConfig = customFaceConfig;
        return this;
    }

    /**
     * 获取IMSDK的配置
     *
     * @return
     */
    public V2TIMSDKConfig getSdkConfig() {
        return sdkConfig;
    }

    /**
     * 设置IMSDK的配置
     *
     * @param timSdkConfig
     * @return
     */
    public TIMConfigs setSdkConfig(V2TIMSDKConfig timSdkConfig) {
        this.sdkConfig = timSdkConfig;
        return this;
    }
}
