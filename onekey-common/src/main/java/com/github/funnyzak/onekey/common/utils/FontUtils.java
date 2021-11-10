package com.github.funnyzak.onekey.common.utils;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/12/27 8:06 下午
 * @description Font
 */
public class FontUtils {
    /**
     * 获取系统字体名称列表
     *
     * @return
     */
    public static List<String> getSystemFontNames() {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return Arrays.asList(e.getAvailableFontFamilyNames());
    }

    /**
     * 获取系统字体名称包含中文名称
     *
     * @return
     */
    public static Map<String, String> getSystemFontList() {
        Map<String, String> mapData = new HashMap<>();
        for (String name : getSystemFontNames()) {
            mapData.put(name, getFontChineseName(name));
        }
        return mapData;
    }

    /**
     * 根据字体名称获取中文名称
     *
     * @param fontName
     * @return
     */
    public static String getFontChineseName(String fontName) {
        switch (fontName) {
            case "STHeiti Light [STXihei]":
                return "华文细黑";
            case "STHeiti":
                return "华文黑体";
            case "STKaiti":
                return "华文楷体";
            case "STSong":
                return "华文宋体";
            case "STFangsong":
                return "华文仿宋";
            case "LiHei Pro Medium":
                return "俪黑 Pro";
            case "LiSong Pro Light":
                return "俪宋 Pro";
            case "BiauKai":
                return "标楷体";
            case "Apple LiGothic Medium":
                return "苹果俪中黑";
            case "Apple LiSung Light":
                return "苹果俪细宋";
            case "PMingLiU":
                return "新细明体";
            case "MingLiU":
                return "细明体";
            case "DFKai-SB":
                return "标楷体";
            case "SimHei":
                return "黑体";
            case "SimSun":
                return "宋体";
            case "NSimSun":
                return "新宋体";
            case "FangSong":
                return "仿宋";
            case "KaiTi":
                return "楷体";
            case "FangSong_GB2312":
                return "仿宋_GB2312";
            case "KaiTi_GB2312":
                return "楷体_GB2312";
            case "Microsoft JhengHei":
                return "微软正黑体";
            case "Microsoft YaHei":
                return "微软雅黑体";
            case "LiSu":
                return "隶书";
            case "YouYuan":
                return "幼圆";
            case "STXihei":
                return "华文细黑";
            case "STZhongsong":
                return "华文中宋";
            case "FZShuTi":
                return "方正舒体";
            case "FZYaoti":
                return "方正姚体";
            case "STCaiyun":
                return "华文彩云";
            case "STHupo":
                return "华文琥珀";
            case "STLiti":
                return "华文隶书";
            case "STXingkai":
                return "华文行楷";
            case "STXinwei":
                return "华文新魏";
        }
        return fontName;
    }
}