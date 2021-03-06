package com.github.funnyzak.onekey.biz.service;

import com.github.funnyzak.onekey.common.utils.*;
import org.nutz.dao.Cnd;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.plugin.spring.boot.service.BaseService;
import org.nutz.plugin.spring.boot.service.entity.DataBaseEntity;
import com.github.funnyzak.onekey.bean.acl.DataRuleModule;
import com.github.funnyzak.onekey.bean.acl.User;
import com.github.funnyzak.onekey.bean.enums.ReviewAction;
import com.github.funnyzak.onekey.bean.enums.ReviewStatus;
import com.github.funnyzak.onekey.bean.log.OperationLog;
import com.github.funnyzak.onekey.biz.bean.ExcelImportResult;
import com.github.funnyzak.onekey.biz.bean.UploadedFileInfo;
import com.github.funnyzak.onekey.biz.config.geoip.GeoIpManager;
import com.github.funnyzak.onekey.biz.config.geoip.GeoLocation;
import com.github.funnyzak.onekey.biz.config.upload.FileUploadManager;
import com.github.funnyzak.onekey.biz.constant.BizConstants;
import com.github.funnyzak.onekey.biz.dto.common.ReviewDTO;
import com.github.funnyzak.onekey.biz.enums.TimeIntervalType;
import com.github.funnyzak.onekey.biz.exception.BizException;
import com.github.funnyzak.onekey.biz.service.acl.DataRuleRelationService;
import com.github.funnyzak.onekey.biz.service.log.OperationLogService;
import com.github.funnyzak.onekey.biz.utils.ExcelImportCheckInterface;
import com.github.funnyzak.onekey.biz.utils.ExcelImportInfoInterface;
import com.github.funnyzak.onekey.biz.utils.ExcelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/11/29 5:55 ??????
 * @description GeneralService
 */
public class GeneralService<T extends DataBaseEntity> extends BaseService<T> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    OperationLogService operationLogService;

    @Autowired
    FileUploadManager fileUploadManager;

    @Autowired
    GeoIpManager geoIpManager;

    /**
     * ??????????????????????????????ID ??????ID????????????????????????
     *
     * @param list              ????????????
     * @param getIdMethodName   ?????????ID?????????
     * @param setInfoMethodName ?????????????????????
     * @param <T>               ????????????
     * @return
     */
    public <T> List<T> setListInfoByListColumnId(List<T> list, String getIdMethodName, String setInfoMethodName) {
        return setListInfoByListColumnId(list, getIdMethodName, setInfoMethodName, "id,name");
    }

    public <T> List<T> setListInfoByListColumnId(List<T> list, String getIdMethodName, String setInfoMethodName, String fieldsList) {
        return setListInfoByListColumnId(list, "id", getIdMethodName, setInfoMethodName, fieldsList);
    }

    /**
     * ??????????????????????????????ID ??????ID????????????????????????
     *
     * @param list              ????????????
     * @param getIdMethodName   ?????????ID?????????
     * @param setInfoMethodName ?????????????????????
     * @param <T>               ????????????
     * @param fieldsList        ?????????INFO???????????????
     * @return
     */
    public <T> List<T> setListInfoByListColumnId(List<T> list, String sourceColumnName, String getIdMethodName, String setInfoMethodName, String fieldsList) {
        if (list == null || list.size() == 0) {
            return null;
        }

        try {
            List<NutMap> mapList = findInfosByIds(
                    sourceColumnName,
                    PUtils.getStringArrayByListColumn(list
                            , getIdMethodName).stream().map(v -> TypeParse.parseLong(v, null)).filter(v -> v != null).collect(Collectors.toList()),
                    fieldsList);
            return PUtils.setListNutMapColumnByNutMapList(list, mapList, sourceColumnName, getIdMethodName, setInfoMethodName);
        } catch (Exception ex) {
            logger.error("????????????????????????????????????LIST???????????????????????????????????????{}", ex);
            return list;
        }
    }

    /**
     * ??????????????????????????????ID?????? ??????ID??????????????????????????????
     *
     * @param list              ????????????
     * @param getIdsMethodName  ?????????ID??????????????? ?????????ID??????1,2??? ,1,2,3,
     * @param setInfoMethodName ?????????????????????
     * @param <T>               ????????????
     * @return
     */
    public <T> List<T> setListInfoByListColumnIds(List<T> list, String getIdsMethodName, String setInfoMethodName) {
        return setListInfoByListColumnIds(list, getIdsMethodName, setInfoMethodName, "id,name");
    }

    /**
     * ??????????????????????????????ID?????? ??????ID??????????????????????????????
     *
     * @param list              ????????????
     * @param getIdsMethodName  ?????????ID??????????????? ?????????ID??????1,2??? ,1,2,3,
     * @param setInfoMethodName ?????????????????????
     * @param <T>               ????????????
     * @return
     */
    public <T> List<T> setListInfoByListColumnIds(List<T> list, String getIdsMethodName, String setInfoMethodName, String fieldsList) {
        if (list == null || list.size() == 0) {
            return list;
        }

        try {
            List<String> idsList = PUtils.getStringArrayByListColumn(list, getIdsMethodName);
            if (idsList == null || idsList.size() == 0) {
                return list;
            }

            List<NutMap> mapList = findInfosByIds(
                    Numbers.parseNumbersString2LongList(Strings.join(",", idsList)),
                    fieldsList);

            if (mapList == null || mapList.size() == 0) {
                return list;
            }

            for (T info : list) {
                String idString = TypeParse.parseString(PUtils.columnValue(info, getIdsMethodName, null), null);
                List<Long> idList = Numbers.parseNumbersString2LongList(idString);
                if (idList == null || idList.size() == 0) {
                    continue;
                }
                List<NutMap> matchMaps = mapList.stream().filter(v -> idList.contains(v.getLong("id"))).collect(Collectors.toList());
                PUtils.setEntityListNutMapColumn(info, setInfoMethodName, matchMaps);
            }
            return list;
        } catch (Exception ex) {
            logger.error("????????????????????????????????????LIST???????????????????????????????????????{}", ex);
            return list;
        }
    }


    /**
     * ??????????????????
     *
     * @param user        ???????????????
     * @param module      ????????????????????????
     * @param action      ???????????????
     * @param description ??????????????????
     * @return ????????????????????????
     */
    public OperationLog addOperationLog(User user, String module, String action, Object description) {
        OperationLog operationLog = new OperationLog();
        operationLog.setAction(action);
        operationLog.setDescription(description.toString());
        operationLog.setModule(module);
        operationLog.setAddUserId(user.getId());
        return operationLogService.save(operationLog);
    }

    /**
     * ??????ID???????????????????????????????????????????????????id,name???
     *
     * @param ids ID??????
     * @return
     */
    public List<NutMap> findInfosByIds(List<Long> ids) {
        return findInfosByIds(ids, "id,name");
    }

    public List<NutMap> findInfosByIds(List<Long> ids, String fieldsList) {
        return findInfosByIds("id", ids, fieldsList);
    }

    /**
     * ??????ID?????????????????????????????????
     *
     * @param ids ??????ID??????
     * @return ????????????????????????
     */
    public List<NutMap> findInfosByIds(String columnName, List<Long> ids, String fieldsList) {
        List<T> list = query(Cnd.where(columnName, "in", ids));
        if (list == null || list.size() == 0) {
            return null;
        }
        List<NutMap> mapList = list.stream().map(info -> PUtils.entityToNutMap(info, fieldsList)).collect(Collectors.toList());
        return mapList;
    }

    public SqlExpressionGroup cndIdNameLikeGroup(String name, String... values) {
        if (values == null) {
            return null;
        }
        return cndIdNameLikeGroup(name, Lang.array2list(values));
    }

    public SqlExpressionGroup cndIdNameLikeGroup(String name, List<String> values) {
        if (values == null || values.size() == 0) {
            return null;
        }
        return cndNameLikeGroup(name, values.stream().filter(v -> !StringUtils.isNullOrEmpty(v) && TypeParse.parseLong(v, null) != null).map(v -> "," + v + ",").collect(Collectors.toList()));
    }

    public SqlExpressionGroup cndNameLikeGroup(String name, String... values) {
        if (values == null) {
            return null;
        }
        return cndNameLikeGroup(name, Lang.array2list(values));
    }

    public SqlExpressionGroup cndNameLikeGroup(String name, List<String> values) {
        if (values == null || values.size() == 0) {
            return null;
        }
        SqlExpressionGroup exp = Cnd.exps(name, "LIKE", values.get(0));
        for (int i = 1; i < values.size(); i++) {
            exp.and(name, "LIKE", values.get(i));
        }
        return exp;
    }

    /**
     * ????????????????????????????????????ID???????????????
     *
     * @param user                    ?????????????????????
     * @param dataRuleModule          ????????????
     * @param relationId              ???????????????ID
     * @param dataRuleRelationService ??????????????????Service
     * @param userIdField             ?????????????????? ??????????????????addUserId
     * @return
     */
    public boolean hasDataPermission(DataRuleRelationService dataRuleRelationService, User user, DataRuleModule dataRuleModule, Long relationId, String userIdField) {
        if (user != null) {
            Cnd cnd = Cnd.NEW();
            List<Long> uidList = dataRuleRelationService.userIdsByUserDataRule(user, dataRuleModule);
            if (uidList != null && uidList.size() > 0) {
                cnd.andEX(userIdField, "in", uidList);
            }
            return fetch(cnd.andEX("id", "=", relationId)) != null;
        }
        return true;
    }

    public boolean hasDataPermission(DataRuleRelationService dataRuleRelationService, User user, DataRuleModule dataRuleModule, Long relationId) {
        return hasDataPermission(dataRuleRelationService, user, dataRuleModule, relationId, "addUserId");
    }

    /**
     * ??????SQL???????????????????????????
     *
     * @param sql SQL??????
     * @return
     */
    public NutMap executeSqlForStatMap(Sql sql) {
        dao().execute(sql);
        List<NutMap> mapList = sql.getList(NutMap.class);
        if (mapList == null || mapList.size() == 0) {
            return null;
        }

        NutMap nutMap = new NutMap();
        for (NutMap info : mapList) {
            nutMap.setv(info.getString("name"), info.getInt("count"));
        }
        return nutMap;
    }

    public Cnd conditionByTime(Long startTime, Long endTime, String timeField) {
        timeField = StringUtils.isNullOrEmpty(timeField) ? "addTime" : timeField;
        return Cnd.NEW()
                .andEX(timeField, ">=", startTime)
                .andEX(timeField, "<=", endTime);
    }


    public Cnd conditionByTime(Long startTime, Long endTime) {
        return conditionByTime(startTime, endTime, null);
    }

    /**
     * ids?????????, ??????1,2 => ,1,2,
     *
     * @param ids ids
     * @return
     */
    public String idsAddComma(String ids) {
        if (StringUtils.isNullOrEmpty(ids)) {
            return ids;
        }
        ids = !ids.startsWith(",") ? ("," + ids) : ids;
        ids = !ids.endsWith(",") ? (ids + ",") : ids;
        return ids;
    }


    public <T> UploadedFileInfo dataExport(List<T> list, LinkedHashMap<String, String> keyMap) throws Exception {
        String savePath = fileUploadManager.generateSavePath("xlsx");
        try {
            ExcelUtils.list2Excel(list, keyMap, savePath);
            return fileUploadManager.pathFile2Info(savePath);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * ????????????????????????
     */
    public UploadedFileInfo timeStatExport(List<TimeIntervalType.TimePeriod> list) throws Exception {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put("name", "??????");
        hashMap.put("data", "??????");
        return dataExport(list, hashMap);
    }

    public UploadedFileInfo typeStatExport(NutMap nutMap, String name) throws Exception {
        return typeStatExport(nutMap, name, "??????");
    }

    /**
     * ??????????????????????????????
     */
    public UploadedFileInfo typeStatExport(NutMap nutMap, String name, String valueName) throws Exception {
        String savePath = fileUploadManager.generateSavePath("xlsx");
        LinkedHashMap<String, String> keyMap = new LinkedHashMap<>();
        keyMap.put("name", name);
        keyMap.put("data", valueName);

        List<NutMap> list = new ArrayList<>();
        for (String key : nutMap.keySet()) {
            NutMap map = new NutMap();
            map.addv("name", key);
            map.addv("data", nutMap.get(key));
            list.add(map);
        }

        try {
            ExcelUtils.list2Excel(list, keyMap, savePath);
            return fileUploadManager.pathFile2Info(savePath);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * ??????
     *
     * @param excelFile                 ????????????????????????
     * @param dataStartRowIndex         ??????????????????????????????????????????0
     * @param excelImportCheckInterface ???????????????????????????
     * @param excelImportInterface      ?????????????????????
     * @return
     */
    public <T> ExcelImportResult<T> importExcel(String excelFile, Integer columnCount, Integer dataStartRowIndex, ExcelImportCheckInterface excelImportCheckInterface, ExcelImportInfoInterface<T> excelImportInterface, String errorTplPath) {
        try {
            dataStartRowIndex = dataStartRowIndex == null ? 2 : dataStartRowIndex;
            List<Map<String, String>> readRlt = ExcelUtils.readExcel(excelFile, dataStartRowIndex, columnCount);
            List<T> successRecordList = new ArrayList<>();
            List<Map<String, String>> failRecordMapList = new ArrayList<>();
            int totalCount = readRlt.size(), successCount = readRlt.size();
            for (int i = 0; i < readRlt.size(); i++) {
                Map<String, String> mapData = readRlt.get(i);
                mapData.put(BizConstants.ROW_INDEX_NAME, Integer.toString(dataStartRowIndex + i + 1));

                // ?????????????????????????????????
                List<String> errorList = excelImportCheckInterface.checkImport(mapData);
                if (errorList.size() > 0) {
                    mapData.put(BizConstants.ERROR_REASON, String.join("???", errorList) + "???");
                    failRecordMapList.add(mapData);
                    successCount--;
                    continue;
                }

                // ??????????????????
                try {
                    successRecordList.add(excelImportInterface.importInfo(mapData));
                } catch (Exception ex) {
                    mapData.put(BizConstants.ERROR_REASON, ex.getMessage());
                    failRecordMapList.add(mapData);
                    successCount--;
                    continue;
                }
            }

            // ??????????????????????????????
            UploadedFileInfo failFileInfo = null;
            try {
                failFileInfo = exportExcel(failRecordMapList, errorTplPath, dataStartRowIndex);
            } catch (Exception ex) {
                logger.error("????????????????????????????????????==>", ex);
            }

            return new ExcelImportResult(totalCount, successCount, totalCount - successCount, successRecordList, failRecordMapList, failFileInfo);
        } catch (Exception ex) {
            throw new BizException(ex.getMessage());
        }
    }

    public UploadedFileInfo exportExcel(List<Map<String, String>> dataList, String templateExcelPath, Integer dataStartRowIndex) throws Exception {
        return exportExcel(dataList, templateExcelPath, null, dataStartRowIndex);
    }

    /**
     * ????????????????????????
     *
     * @param dataList          ??????????????????
     * @param templateExcelPath ??????????????????
     */
    public UploadedFileInfo exportExcel(List<Map<String, String>> dataList, String templateExcelPath, String saveName, Integer dataStartRowIndex) throws Exception {
        InputStream xlsStream = this.getClass().getResourceAsStream(templateExcelPath);

        String xlsPath = fileUploadManager.generateSavePath("xlsx", (StringUtils.isNullOrEmpty(saveName) ? "excel" : saveName) + "_" + StringUtils.getYYYYMMDDHHmmssmilliSecond());
        int fileNameCharIndex = xlsPath.lastIndexOf(File.separator) + 1;
        FileUtils.saveFile("".getBytes(), xlsPath.substring(0, fileNameCharIndex), xlsPath.substring(fileNameCharIndex));
        Files.copy(xlsStream, Paths.get(xlsPath), StandardCopyOption.REPLACE_EXISTING);

        try {
            ExcelUtils.addExcelData(xlsPath,
                    dataList.stream().map(v -> new ArrayList<>(v.values())).collect(Collectors.toList()),
                    dataStartRowIndex);
            return fileUploadManager.pathFile2Info(xlsPath);
        } catch (Exception ex) {
            logger.error("??????????????????==>", ex);
            throw new BizException(ex);
        }
    }

    public <T extends Enum<T>> NutMap parseNutMapKeyByEnum(@NotNull NutMap nutMap, @NotNull Class<T> enumType) {
        if (nutMap == null || nutMap.size() == 0) {
            return nutMap;
        }

        NutMap parseMap = new NutMap();
        for (String key : nutMap.keySet()) {
            T info = Enum.valueOf(enumType, key);
            parseMap.addv(PUtils.columnValue(info, "getName", String.class), nutMap.get(key));
        }
        return parseMap;
    }

    public String generateTimeNum(String prefix) {
        return String.format("%s%s", prefix, DateUtils.ts2S(DateUtils.getTS(), "yyyyMMddHHmmss"));
    }

    public UploadedFileInfo checkAndUploadExcelFile(MultipartFile file, User user) throws Exception {
        fileUploadManager.checkFileTypeByExt(FileUtils.getFileExt(file.getOriginalFilename()), Arrays.asList("xls", "xlsx"));

        UploadedFileInfo uploadedFileInfo = fileUploadManager.saveFile(file);
        if (uploadedFileInfo == null) {
            throw new BizException("????????????????????????");
        }
        return uploadedFileInfo;
    }

    public void checkReviewDTO(ReviewDTO dto) throws BizException {
        if (dto == null || dto.getAction() == null || dto.getId() == null || dto.getId() <= 0) {
            throw new BizException("??????????????????");
        }
    }

    public List<T> setLocationByIp(List<T> list) {
        return setLocationByIp(list, null, null);
    }

    public List<T> setLocationByIp(List<T> list, String ipPropertyName, String setLocationMethodName) {
        if (list == null || list.size() == 0) {
            return null;
        }

        ipPropertyName = StringUtils.isNullOrEmpty(ipPropertyName) ? "ip" : ipPropertyName;
        setLocationMethodName = StringUtils.isNullOrEmpty(setLocationMethodName) ? "setLocation" : setLocationMethodName;
        for (T t : list) {
            String getIpMethodName = String.format("get%s", StringUtils.firstCodeToUpperCase(ipPropertyName));
            Object ip = PUtils.columnValue(t, getIpMethodName);
            if (ip == null || "127.0.0.1".equals(ip)) {
                continue;
            }
            try {
                GeoLocation location = geoIpManager.search(ip.toString());
                if (location == null) {
                    continue;
                }

                PUtils.setEntityNutMapColumn(t, setLocationMethodName, Json.fromJsonAsArray(NutMap.class, Json.toJson(location)));
            } catch (Exception ex) {
                logger.error("??????IP?????????????????????????????????==>", ex);
            }
        }
        return list;
    }

    /**
     * ???????????????????????????
     *
     * @param dto           ??????????????????
     * @param currentStatus ???????????????????????????????????????????????????????????????????????????????????????
     * @return
     */
    public ReviewAction checkReviewAction(@NotNull ReviewDTO dto, @NotNull String currentStatus) throws Exception {
        boolean isSubmit = ReviewAction.TO_SUBMIT.equals(dto.getAction())
                && (currentStatus.equals(ReviewStatus.WAIT_SUBMIT.toString())
                || currentStatus.equals(ReviewStatus.FAIL.toString()));

        boolean isReview = ReviewAction.TO_REVIEW.equals(dto.getAction())
                && currentStatus.equals(ReviewStatus.REVIEWING.toString())
                && dto.getStatus() != null && (dto.getStatus().equals(ReviewStatus.FAIL)
                || dto.getStatus().equals(ReviewStatus.SUCCESS));


        ReviewAction action = isSubmit ? ReviewAction.TO_SUBMIT : isReview ? ReviewAction.TO_REVIEW : null;

        if (action == null) {
            throw new BizException("???????????????????????????");
        }

        return action;
    }

    public <T, S extends Enum<S>> T parseInfoSubmit(@NotNull T info, @NotNull Class<S> statusType) {
        PUtils.setEntityColumn(info, "setStatus", Enum.valueOf(statusType, "REVIEWING"), statusType);
        PUtils.setEntityColumn(info, "setSubmitTime", DateUtils.getTS(), Long.class);
        PUtils.setEntityColumn(info, "setUpdateTime", DateUtils.getTS(), Long.class);
        return info;
    }

    public <T, S> T parseInfoReview(@NotNull Long uid, @NotNull T info, @NotNull S toStatus, String reason) {
        PUtils.setEntityColumn(info, "setStatus", toStatus, toStatus.getClass());
        PUtils.setEntityColumn(info, "setReviewReason", reason, String.class);
        PUtils.setEntityColumn(info, "setReviewTime", DateUtils.getTS(), Long.class);
        PUtils.setEntityColumn(info, "setReviewUserId", uid, Long.class);
        PUtils.setEntityColumn(info, "setUpdateTime", DateUtils.getTS(), Long.class);
        return info;
    }

    public <T> void checkNull(T info) throws Exception {
        if (info == null) {
            throw new BizException("????????????");
        }
    }

}