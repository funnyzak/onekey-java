package com.github.funnyzak.bean.resource.enums;

import org.nutz.lang.Lang;

import java.util.List;

/**
 * 藏品资源类型
 *
 * @author potato
 */
public enum ResourceCate {
    IMAGE("图片", "jpg,jpeg,bmp,png"),
    ATTACHMENT("附件", "jpg,jpeg,bmp,png,doc,docx,xls,xlsx,ppt,pptx,pdf,txt,pdf,mp4,mp3,zip"),
    THREE_MODEL("模型", "zip");

    ResourceCate(String name, String extList) {
        this.name = name;
        this.extList = Lang.array2list(extList.split(","));
    }

    private String name;

    public List<String> getExtList() {
        return extList;
    }

    public void setExtList(List<String> extList) {
        this.extList = extList;
    }

    private List<String> extList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}