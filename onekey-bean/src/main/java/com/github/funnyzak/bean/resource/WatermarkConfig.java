package com.github.funnyzak.bean.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funnyzak.bean.resource.enums.WatermarkPosition;
import com.github.funnyzak.bean.resource.enums.WatermarkWay;
import lombok.Data;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/12/12 5:02 下午
 * @description 水印配置
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WatermarkConfig {
    /**
     * 水印位置
     */
    private WatermarkPosition position = WatermarkPosition.BOTTOM_RIGHT;

    /**
     * 水印方式
     */
    private WatermarkWay way = WatermarkWay.TEXT;

    /**
     * 文字水印文字
     */
    private String text = "Potato";

    /**
     * 文字水印字体颜色
     */
    private String fontColor = "#000000";

    /**
     * 文字水印字体
     */
    private String fontName = "宋体";

    /**
     * 文字水印字体大小
     */
    private Integer fontSize = 16;

    /**
     * 横向间距
     */
    private Integer horizontalPadding = 10;

    /**
     * 纵向间距
     */
    private Integer verticalPadding = 10;

    /**
     * 水印图片地址
     */
    private String watermarkUrl;

    /**
     * 水印不透明度百分比
     */
    private Integer opacity = 35;

}