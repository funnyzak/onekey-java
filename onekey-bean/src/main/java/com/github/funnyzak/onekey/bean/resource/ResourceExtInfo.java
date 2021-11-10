package com.github.funnyzak.onekey.bean.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/12/13 1:53 下午
 * @description ResourceExtInfo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceExtInfo {
    /**
     * 用于图片的EXIF信息
     */
    private Map<String, Object> exif;

    /**
     * 三维模型数据
     */
    private ThreeModel threeModel;
}