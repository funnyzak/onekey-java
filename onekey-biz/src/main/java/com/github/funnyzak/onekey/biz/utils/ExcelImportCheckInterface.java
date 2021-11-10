package com.github.funnyzak.onekey.biz.utils;

import java.util.List;
import java.util.Map;

/**
 * 检查导入的表格行信息
 */
@FunctionalInterface
public interface ExcelImportCheckInterface {
    List<String> checkImport(Map<String, String> mapData) throws Exception;
}
