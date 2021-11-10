package com.github.funnyzak.onekey.biz.bean;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/2/7 10:53 上午
 * @description Excel表格导入结果
 */
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
public class ExcelImportResult<T> {
    /**
     * 识别记录数
     */
    private Integer total;

    /**
     * 成功导入数
     */
    private Integer success;

    /**
     * 失败数
     */
    private Integer fail;

    /**
     * 成功记录
     */
    private List<T> successRecordList;

    /**
     * 失败记录集
     */
    private List<Map<String, String>> failRecordList;


    /**
     * 失败的记录集表格
     */
    private UploadedFileInfo failRecordFile;
}