package com.github.funnyzak.biz.utils;

import java.util.Map;

/**
 * 导入表格行信息
 */
@FunctionalInterface
public interface ExcelImportInfoInterface<T> {
   T importInfo(Map<String, String> mapData) throws Exception;
}
