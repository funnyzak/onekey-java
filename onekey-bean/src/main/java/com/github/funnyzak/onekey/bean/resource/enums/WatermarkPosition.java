package com.github.funnyzak.onekey.bean.resource.enums;

/**
 * 水印位置
 */
public enum WatermarkPosition {
    TOP_LEFT("左上", 1),
    TOP_MIDDLE("中上", 2),
    TOP_RIGHT("右上", 3),
    MIDDLE_LEFT("左中", 4),
    MIDDLE("正中", 5),
    MIDDLE_RIGHT("右中", 6),
    BOTTOM_LEFT("左下", 7),
    BOTTOM_MIDDLE("中下", 8),
    BOTTOM_RIGHT("右下", 9);

    WatermarkPosition(String name) {
        this.name = name;
    }

    WatermarkPosition(String name, Integer id) {
        this.name = name;
        this.id = id;
    }


    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public static WatermarkPosition fromInteger(Integer x) {
        for (WatermarkPosition wp : WatermarkPosition.values()) {
            if (wp.getId().equals(x)) {
                return wp;
            }
        }
        return BOTTOM_RIGHT;
    }
}
