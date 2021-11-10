package com.github.funnyzak.onekey.biz.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.nutz.lang.util.NutMap;
import com.github.funnyzak.onekey.bean.enums.Gender;
import com.github.funnyzak.onekey.biz.exception.BizException;
import com.github.funnyzak.onekey.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/1/3 6:55 下午
 * @description TableUtils
 */
public class ExcelUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * 读取表格数据
     *
     * @param excelPath 表格路径(表格必须有标题列)
     * @return 返回表格数据
     * @throws Exception
     */
    public static List<Map<String, String>> readExcel(@NotNull String excelPath) throws Exception {
        return readExcel(excelPath, 1, null);
    }

    /**
     * 读取表格数据
     *
     * @param excelPath         表格路径(表格必须有标题列)
     * @param dataStartRowIndex 数据开始行索引，一般为第二行:1,第一行为标题行
     * @param readColumnCount   要读取的列数（选填，不填则自动识别）
     * @return 返回表格数据
     * @throws Exception
     */
    public static List<Map<String, String>> readExcel(@NotNull String excelPath, Integer dataStartRowIndex, Integer readColumnCount) throws Exception {
        Workbook workbook = null;
        FileInputStream inputStream = null;

        try {
            // 获取Excel文件
            File excelFile = new File(excelPath);
            if (!excelFile.exists()) {
                throw new BizException("文件不存在");
            }

            // 获取Excel工作簿
            inputStream = new FileInputStream(excelFile);
            workbook = WorkbookFactory.create(inputStream);
            // 读取excel中的数据
            return parseExcel(workbook, dataStartRowIndex, readColumnCount);
        } catch (Exception ex) {
            logger.error("读取Excel失败==>", ex);
            throw ex;
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * 解析Excel数据
     *
     * @param workbook          Excel工作簿对象（表格必须有标题列）
     * @param dataStartRowIndex 数据开始行索引，一般为第二行:1,第一行为标题行
     * @param readColumnCount   要读取的列数（选填，不填则自动识别）
     * @return 解析结果
     */
    private static List<Map<String, String>> parseExcel(Workbook workbook, Integer dataStartRowIndex, Integer readColumnCount) throws Exception {
        List<Map<String, String>> resultDataList = new ArrayList<>();
        dataStartRowIndex = dataStartRowIndex == null ? 1 : dataStartRowIndex;

        // 解析sheet
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);

            // 校验sheet是否合法
            if (sheet == null) {
                continue;
            }

            // 获取第一个有数据的行
            int firstRowNum = sheet.getFirstRowNum();
            Row firstRow = sheet.getRow(firstRowNum);
            if (null == firstRow) {
                // throw new BizException("Excel无数据行");
                continue;
            }

            int lastRowNum = sheet.getLastRowNum();
            // 数据开始行数据或者最后一行行小于数据开始行则返回无数据
            if (null == sheet.getRow(dataStartRowIndex) || lastRowNum < dataStartRowIndex) {
                // throw new BizException("Excel无数据行");
                continue;
            }

            // 获取标题集合
            Row titleRow = sheet.getRow(dataStartRowIndex - 1);
            int dataColumnCount = readColumnCount == null || readColumnCount > titleRow.getLastCellNum() ? titleRow.getLastCellNum() : readColumnCount;
            List<String> keyList = convertRowToList(titleRow, dataColumnCount);


            int rowStart = dataStartRowIndex;
            for (int rowNum = rowStart; rowNum < lastRowNum; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (null == row) {
                    continue;
                }

                List<String> stringList = convertRowToList(row, dataColumnCount);
                Map<String, String> map = new LinkedHashMap<>();

                for (int i = 0; i < dataColumnCount; i++) {
                    map.put(keyList.get(i), stringList.get(i));
                }
                resultDataList.add(map);
            }
        }
        return resultDataList;
    }

    /**
     * 读取行数据
     *
     * @param row 行数据
     * @return 列数据集合
     */
    private static List<String> convertRowToList(@NotNull Row row) {
        return convertRowToList(row, null);
    }

    /**
     * 读取行数据
     *
     * @param row             行数据
     * @param dataColumnCount 数据列数
     * @return 列数据集合
     */
    private static List<String> convertRowToList(@NotNull Row row, @NotNull Integer dataColumnCount) {
        int columnCount = dataColumnCount == null ? row.getLastCellNum() : dataColumnCount;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < columnCount; i++) {
            Cell cell = row.getCell(i);
            list.add(cellValueToString(cell));
        }
        return list;
    }

    /**
     * 将单元格内容转换为字符串
     *
     * @param cell 单元格
     * @return
     */
    private static String cellValueToString(Cell cell) {
        if (cell == null) {
            return null;
        }
        String returnValue = null;
        switch (cell.getCellType()) {
            case NUMERIC:   //数字
                Double doubleValue = cell.getNumericCellValue();
                // 格式化科学计数法，取一位整数
                DecimalFormat df = new DecimalFormat("0");
                returnValue = df.format(doubleValue);
                break;
            case STRING:    //字符串
                returnValue = cell.getStringCellValue();
                break;
            case BOOLEAN:   //布尔
                Boolean booleanValue = cell.getBooleanCellValue();
                returnValue = booleanValue.toString();
                break;
            case FORMULA:   // 公式
                returnValue = cell.getCellFormula();
                break;
            case BLANK:     // 空值
            case ERROR:     // 故障
                break;
            default:
                break;
        }
        return returnValue;
    }

    /**
     * 数据写入到Excel
     *
     * @param list     数据
     * @param keyMap   字段名和标题，关系为 字段名:列标题，字段名需和数据的字段项一致
     * @param savePath 表格要保存的本地路径
     * @param <T>      数据对应类型
     * @throws Exception
     */
    public static <T> void list2Excel(@NotNull List<T> list, @NotNull LinkedHashMap<String, String> keyMap, @NotNull String savePath) throws Exception {
        if (list == null || list.size() == 0) {
            throw new BizException("没有可导出数据");
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("导出数据");
        sheet.setDefaultColumnWidth(20);

        CellStyle cellStyleHead = workbook.createCellStyle();
        cellStyleHead.setAlignment(HorizontalAlignment.CENTER);
        cellStyleHead.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        cellStyleHead.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont headFont = workbook.createFont();
        headFont.setBold(true);
        headFont.setColor(IndexedColors.WHITE.getIndex());
        headFont.setFontHeightInPoints((short) 12);
        cellStyleHead.setFont(headFont);

        XSSFRow xssfRow = sheet.createRow(0);

        int valLoop = 0;
        for (String value : keyMap.values()) {
            XSSFCell headCell = xssfRow.createCell(valLoop);

            headCell.setCellValue(value);
            headCell.setCellStyle(cellStyleHead);
            valLoop++;
        }

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // 添加数据内容
        for (int i = 0; i < list.size(); i++) {
            xssfRow = sheet.createRow(i + 1);
            try {
                int keyLoop = 0;
                for (String key : keyMap.keySet()) {
                    XSSFCell cell = xssfRow.createCell(keyLoop);

                    String valGetString = "";

                    if (list.get(0).getClass().equals(NutMap.class)) {
                        valGetString = ((NutMap) list.get(i)).getString(key);
                    } else {
                        Method methodGet = list.get(0).getClass().getDeclaredMethod("get" + StringUtils.firstCodeToUpperCase(key));
                        Object valGetObj = methodGet.invoke(list.get(i));
                        valGetString = valGetObj == null ? "" : valGetObj.toString();
                        if ("gender".equals(key) && valGetObj != null) {
                            valGetString = ((Gender) valGetObj).getName();
                        }
                    }
                    cell.setCellValue(StringUtils.isNullOrEmpty(valGetString) ? "-" : valGetString);
                    cell.setCellStyle(cellStyle);
                    keyLoop++;
                }
            } catch (Exception e) {
                logger.error("导出Excel出错==>", e);
                throw e;
            }
        }

        try {
            FileOutputStream out = new FileOutputStream(new File(savePath));
            workbook.write(out);
            workbook.close();
        } catch (Exception e) {
            logger.error("导出Excel出错==>", e);
            throw new BizException("导出Excel出错");
        }
    }

    /**
     * 为Excel添加数据
     *
     * @param excelPath        要修改的Excel文件路径
     * @param tableData        要添加的行数据
     * @param addStartRowIndex 从第几行开始添加
     * @throws Exception
     */
    public static void addExcelData(String excelPath, List<List<String>> tableData, Integer addStartRowIndex) throws Exception {
        File file = new File(excelPath);
        FileInputStream inputStream;

        try {
            inputStream = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(inputStream);
            inputStream.close();
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 0; i < tableData.size(); i++) {
                Row row = sheet.createRow(i + addStartRowIndex);

                for (int j = 0; j < tableData.get(i).size(); j++) {
                    Cell cell = row.createCell(j, CellType.STRING);
                    cell.setCellValue(StringUtils.isNullOrEmpty(tableData.get(i).get(j)) ? "-" : tableData.get(i).get(j));
                }
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);

            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            logger.error("更新表格失败==>", e);
            throw new BizException("更新表格失败,原因:" + e.getMessage());
        }
    }
}