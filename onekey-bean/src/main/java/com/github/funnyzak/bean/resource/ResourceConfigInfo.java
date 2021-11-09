package com.github.funnyzak.bean.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/12/13 1:53 下午
 * @description ResourceConfigInfo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceConfigInfo {
    /**
     * JSON字符串配置
     */
    private String jsonSettings;
}