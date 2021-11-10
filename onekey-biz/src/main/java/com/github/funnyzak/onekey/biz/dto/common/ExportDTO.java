package com.github.funnyzak.onekey.biz.dto.common;

import com.github.funnyzak.onekey.biz.bean.UploadedFileInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/11/20 11:23 上午
 * @description CollectionExportDTO
 */
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ExportDTO<T> {

    public ExportDTO(UploadedFileInfo excelFile, List<T> previewList) {
        this.excelFile = excelFile;
        this.previewList = previewList;
    }


    private Integer totalCount;

    /**
     * 导出生成的表格
     */
    private UploadedFileInfo excelFile;

    /**
     * 预览列表（非全部）
     */
    private List<T> previewList;
}