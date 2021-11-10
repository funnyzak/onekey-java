package com.github.funnyzak.onekey.bean.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/12/12 4:59 下午
 * @description 藏品资源设置
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceBaseConfig {
    /**
     * 水印配置
     */
    private WatermarkConfig watermarkConfig;

    public WatermarkConfig getWatermarkConfig() {
        if (this.watermarkConfig == null) {
            return new WatermarkConfig();
        }
        return this.watermarkConfig;
    }
}