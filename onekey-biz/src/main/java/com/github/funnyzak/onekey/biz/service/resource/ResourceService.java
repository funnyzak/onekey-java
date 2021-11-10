package com.github.funnyzak.onekey.biz.service.resource;

import com.github.funnyzak.onekey.bean.enums.TypeRelationEnums;
import com.github.funnyzak.onekey.biz.bean.UploadedFileInfo;
import com.github.funnyzak.onekey.biz.config.upload.FileUploadManager;
import com.github.funnyzak.onekey.biz.config.upload.FileUploadProperties;
import com.github.funnyzak.onekey.biz.constant.BizConstants;
import com.github.funnyzak.onekey.biz.enums.TimeIntervalType;
import com.github.funnyzak.onekey.biz.exception.BizException;
import com.github.funnyzak.onekey.biz.service.GeneralService;
import com.github.funnyzak.onekey.biz.service.TypeRelationService;
import com.github.funnyzak.onekey.common.utils.*;
import net.coobird.thumbnailator.Thumbnails;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.onekey.bean.acl.User;
import com.github.funnyzak.onekey.bean.resource.ResourceExtInfo;
import com.github.funnyzak.onekey.bean.resource.ResourceInfo;
import com.github.funnyzak.onekey.bean.resource.ThreeModel;
import com.github.funnyzak.onekey.bean.resource.WatermarkConfig;
import com.github.funnyzak.onekey.bean.resource.enums.ResourceBelongType;
import com.github.funnyzak.onekey.bean.resource.enums.ResourceCate;
import com.github.funnyzak.onekey.bean.resource.enums.ResourceStatus;
import com.github.funnyzak.onekey.bean.resource.enums.WatermarkWay;
import com.github.funnyzak.onekey.biz.service.acl.UserService;
import com.github.funnyzak.onekey.common.image.ImageUtils;
import com.github.funnyzak.onekey.common.image.Watermark;
import com.github.funnyzak.onekey.common.obj.ObjUtils;
import com.github.funnyzak.onekey.common.obj.ThreeObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/12/6 1:16 下午
 * @description ResourceServiceImpl
 */
@Service
public class ResourceService extends GeneralService<ResourceInfo> {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserService userService;
    private final FileUploadManager fileUploadManager;
    private final TypeRelationService typeRelationService;
    private final FileUploadProperties fileUploadProperties;

    @Value("${server.port:80}")
    private Integer SERVER_PORT;

    @Value("${biz-system.general.image-preview-max-size:314571000}")
    private Integer IMAGE_PREVIEW_MAX_SIZE = 314571000;

    @Autowired
    public ResourceService(UserService userService,
                           TypeRelationService typeRelationService,
                           FileUploadProperties fileUploadPropertie,
                           FileUploadManager fileUploadManager) {
        this.userService = userService;
        this.fileUploadManager = fileUploadManager;
        this.fileUploadProperties = fileUploadPropertie;
        this.typeRelationService = typeRelationService;
    }

    public Cnd addExistCondition(Cnd resourceCondition) {
        return (resourceCondition == null ? Cnd.NEW() : resourceCondition).and("del", "=", false);
    }

    public Cnd addSuccessCondition(Cnd resourceCondition) {
        return addExistCondition((resourceCondition == null ? Cnd.NEW() : resourceCondition).and("status", "=", ResourceStatus.SUCCESS));
    }

    public PageredData<ResourceInfo> pagerByRelation(Integer pageNumber, Integer pageSize, TypeRelationEnums relationType, Long typeId, Cnd resourceCondition) {
        return typeRelationService.pagerByRelation("resource.info.list.by.relation.condition",
                "resource.info.list.count.by.relation.condition",
                ResourceInfo.class, relationType,
                addExistCondition(resourceCondition),
                typeId, pageNumber, pageSize);
    }

    public <T> List<T> setListResourceInfo(List<T> list) {
        return setListInfoByListColumnId(list, "getResourceId", "setResource", BizConstants.ResourceConst.SIMPLE_INFO_FIELD_NAME_LIST);
    }

    public <T> List<T> setListResourceInfo(List<T> list, String getIdMethodName, String setInfoMethodName) {
        return setListInfoByListColumnId(list, getIdMethodName, setInfoMethodName, BizConstants.ResourceConst.SIMPLE_INFO_FIELD_NAME_LIST);
    }

    /**
     * 类型数组转为泛型
     *
     * @param cateArray 类型数组
     * @return
     */
    public List<ResourceCate> cateArray2List(String cateArray) {
        List<ResourceCate> cateList = new ArrayList<>();
        if (!StringUtils.isNullOrEmpty(cateArray)) {
            for (String cateString : cateArray.split(",")) {
                try {
                    cateList.add(ResourceCate.valueOf(cateString));
                } catch (Exception ex) {
                    logger.error("转换资源类型失败，错误信息：", ex);
                }
            }
        }
        return cateList;
    }

    /**
     * 图片增加水印
     *
     * @param imageUrl        图片外链
     * @param watermarkConfig 水印配置
     * @return
     */
    public String imageLinkAddWatermark(String imageUrl, WatermarkConfig watermarkConfig) {
        if (StringUtils.isNullOrEmpty(imageUrl)) {
            return null;
        }
        String sourceImagePath = fileLinkSaveLocal(imageUrl);
        return imageAddWatermark(sourceImagePath, watermarkConfig);
    }

    /**
     * 图片增加水印
     *
     * @param sourceImagePath 图片文件
     * @param watermarkConfig 水印配置
     * @return
     */
    public String imageAddWatermark(String sourceImagePath, WatermarkConfig watermarkConfig) {

        String outputPath = fileUploadManager.generateSavePath(FileUtils.getFileExt(sourceImagePath));
        try {

            if (!ImageUtils.isImageFile(sourceImagePath)) {
                return sourceImagePath;
            }

            if (watermarkConfig.getWay().equals(WatermarkWay.IMAGE) && !StringUtils.isNullOrEmpty(watermarkConfig.getWatermarkUrl())) {
                try {
                    String waterImagePath = fileLinkSaveLocal(watermarkConfig.getWatermarkUrl());
                    if (StringUtils.isNullOrEmpty(waterImagePath)) {
                        logger.error("下载水印图片，请设置对应的水印");
                        return sourceImagePath;
                    }

                    Watermark.imgWatermark(sourceImagePath, waterImagePath, outputPath, watermarkConfig.getPosition().getId(), watermarkConfig.getHorizontalPadding(), watermarkConfig.getVerticalPadding(), watermarkConfig.getOpacity() / 100F);
                } catch (Exception ex) {
                    logger.error("图片水印添加失败，请检查==>config:{}, 原图：{}, 原始错误信息：{}", Json.toJson(watermarkConfig), sourceImagePath, ex.toString());
                    return sourceImagePath;
                }

            } else {
                try {
                    Watermark.textWatermark(sourceImagePath, watermarkConfig.getText(), outputPath, watermarkConfig.getPosition().getId(), watermarkConfig.getFontColor(), watermarkConfig.getFontName(), Font.BOLD, watermarkConfig.getFontSize(), watermarkConfig.getHorizontalPadding(), watermarkConfig.getVerticalPadding(), watermarkConfig.getOpacity() / 100F);
                } catch (Exception ex) {
                    logger.error("文字水印添加失败，请检查==>config:{}, 原图：{}, 原始错误信息：{}", Json.toJson(watermarkConfig), sourceImagePath, ex.toString());
                    return sourceImagePath;
                }
            }
            return outputPath;
        } catch (Exception ex) {
            logger.error("添加图片水印失败，请检查==>config:{}, 原图：{}, 原始错误信息：{}", Json.toJson(watermarkConfig), sourceImagePath, ex.toString());
            return sourceImagePath;
        }
    }

    /**
     * 根据URL下载相应文件并返回保存的本地绝对路径
     *
     * @param fileLink 文件外链
     * @return 本地路径
     */
    public String fileLinkSaveLocal(String fileLink) {
        if (StringUtils.isNullOrEmpty(fileLink)) {
            return null;
        }
        try {
            String realUrl = fileLink.startsWith(fileUploadProperties.getVirtualPath()) ? String.format("http://localhost:%s%s", SERVER_PORT, fileLink) : fileLink;
            URL url = new URL( realUrl);

            String savePath = (url.getPath().split("/").length > 1 ? url.getPath().substring(0, url.getPath().lastIndexOf("/")) : "/download");
            String saveFileName = url.getFile().substring(url.getPath().lastIndexOf("/") + 1, url.getPath().lastIndexOf("."));

            String saveFilePath = fileUploadManager.generateSavePath(FileUtils.getFileExt(realUrl), savePath, saveFileName);
            if (!new File(saveFilePath).exists()) {
                Download.downloadByUrl(realUrl, saveFilePath);
            }
            return saveFilePath;
        } catch (Exception ex) {
            logger.error("获取URL本地路径失败，错误信息：", ex);
            throw new BizException("源文件不存在");
        }
    }

    /**
     * 根据条件获取各类型资源的上传的数量统计信息
     *
     * @param directCnd 资源的筛选条件
     * @return 返回类型和类型所对应的数据Map集合
     */
    public List<NutMap> cateStatByDirectCondition(Cnd directCnd) {
        return mapListByCondition("resource.cate.count.stat.by.condition", directCnd);
    }

    /**
     * 根据条件获取业务数据所关联的资源信息数量统计
     *
     * @param directCnd    资源的实体的筛选踢哦啊件
     * @param relationType 业务类型
     * @param typeId       业务数据ID
     * @return
     */
    public List<NutMap> cateStatByRelationCondition(Cnd directCnd, TypeRelationEnums relationType, Long typeId) {
        return mapListBySql(
                conditionSql("resource.cate.count.stat.by.condition.and.relation", directCnd.and("size", ">", 0))
                        .setParam("relation_type", relationType)
                        .setParam("relation_type_id", typeId)
        );
    }

    public List<NutMap> mapListByCondition(String sqlName, Cnd cnd) {
        return mapListBySql(conditionSql(sqlName, cnd));
    }

    private List<NutMap> mapListBySql(Sql sql) {
        dao().execute(sql);
        return sql.getList(NutMap.class);
    }

    private Sql conditionSql(String sqlName, Cnd cnd) {
        Sql sql = dao().sqls().create(sqlName);
        sql.setEntity(dao().getEntity(ResourceInfo.class))
                .setCondition(addExistCondition(cnd))
                .setCallback(Sqls.callback.maps());
        return sql;
    }

    public NutMap size2countCalcCondition(Cnd cnd) {
        Sql sql = dao().sqls().create("resource.count.stat.by.condition");
        sql.setEntity(dao().getEntity(ResourceInfo.class))
                .setCondition(addExistCondition(cnd))
                .setCallback(Sqls.callback.map());
        dao().execute(sql);
        return sql.getObject(NutMap.class);
    }

    public <T> List<T> setListResourceStatInfo(List<T> list, ResourceBelongType belongType) {
        return setListResourceStatInfo(list, belongType, "getId", "setResourceStat");
    }

    public <T> List<T> setListResourceStatInfo(List<T> list, ResourceBelongType belongType, String getIdMethodName, String setInfoMethodName) {
        if (list == null || list.size() == 0) {
            return list;
        }
        return list.stream().map(v -> setResourceStatInfo(v, belongType, getIdMethodName, setInfoMethodName)).collect(Collectors.toList());
    }

    public <T> T setResourceStatInfo(T info, ResourceBelongType belongType) {
        return setResourceStatInfo(info, belongType, "getId", "setResourceStat");
    }

    public <T> T setResourceStatInfo(T info, ResourceBelongType belongType, String getIdMethodName, String setInfoMethodName) {
        return PUtils.setEntityNutMapColumn(info, setInfoMethodName, getResourceStat(info, belongType, getIdMethodName));
    }

    public <T> List<T> setListRelationResourceStatInfo(List<T> list, TypeRelationEnums relationType) {
        return setListRelationResourceStatInfo(list, relationType, "getId", "setResourceStat");
    }

    public <T> List<T> setListRelationResourceStatInfo(List<T> list, TypeRelationEnums relationType, String getIdMethodName, String setInfoMethodName) {
        if (list == null || list.size() == 0) {
            return list;
        }
        return list.stream().map(v -> setRelationResourceStatInfo(v, relationType, getIdMethodName, setInfoMethodName)).collect(Collectors.toList());
    }

    /**
     * 获取符合和资源关联关系的资源统计信息
     */
    public <T> T setRelationResourceStatInfo(T info, TypeRelationEnums relationType, String getIdMethodName, String setInfoMethodName) {
        return PUtils.setEntityNutMapColumn(info, setInfoMethodName, getResourceStat(info, relationType, getIdMethodName));
    }

    /**
     * 获取符合和资源关联关系的资源统计信息
     */
    public <T> T setRelationResourceStatInfo(T info, TypeRelationEnums relationType) {
        return setRelationResourceStatInfo(info, relationType, "getId", "setResourceStat");
    }

    public List<NutMap> cateStatByRelationCondition(TypeRelationEnums relationType, Long typeId) {
        try {
            return cateStatByRelationCondition(baseCondition(), relationType, typeId);
        } catch (Exception ex) {
            logger.error("获取资源统计信息失败==>", ex);
            return null;
        }
    }

    /**
     * 根据业务和资源管理统计资源信息
     */
    public <T> NutMap getResourceStat(T info, TypeRelationEnums relationType, String getIdMethodName) {
        Long id = PUtils.columnValue(info, getIdMethodName, Long.class);
        if (id == null || id <= 0) {
            return null;
        }
        return parseResourceStat(cateStatByRelationCondition(relationType, id));
    }

    /**
     * 根据业务上传统计所有资源信息
     */
    public <T> NutMap getResourceStat(T info, ResourceBelongType belongType, String getIdMethodName) {
        Long id = PUtils.columnValue(info, getIdMethodName, Long.class);
        if (id == null || id <= 0) {
            return null;
        }
        return parseResourceStat(cateStatByDirectCondition(belongType, id, null));
    }

    public <T> NutMap parseResourceStat(List<NutMap> list) {
        int totalSize = 0;
        int totalCount = 0;

        NutMap statMap = new NutMap();
        if (list != null && list.size() > 0) {
            totalCount = (int) PUtils.sumListColumnValue(list, "get", "count");
            totalSize = (int) PUtils.sumListColumnValue(list, "get", "size");
        }
        statMap.addv("totalSize", totalSize);
        statMap.addv("totalCount", totalCount);
        statMap.addv("detail", list);
        return statMap;
    }

    /**
     * 获取资源数量统计
     */
    public List<NutMap> resourceStatByBelong(Long id, ResourceBelongType belongType) {
        return cateStatByDirectCondition(Cnd.where("belong", "=", belongType).andEX("relationId", "=", id));
    }

    public Cnd baseCondition() {
        return condition(null, null, null, null, null);
    }

    /**
     * 根据条件获取各类型业务上传的资源数量统计信息
     *
     * @param belong     属于类型
     * @param relationId 相关业务ID
     * @param cate       属于什么类型
     * @return
     */
    public List<NutMap> cateStatByDirectCondition(Cnd cnd, User currentUser, ResourceBelongType belong, Long relationId, ResourceCate cate) {
        return cateStatByDirectCondition(condition(cnd, currentUser, belong, relationId, cate));
    }

    public List<NutMap> cateStatByDirectCondition(ResourceBelongType belong, Long relationId, ResourceCate cate) {
        try {
            return cateStatByDirectCondition(null, null, belong, relationId, cate);
        } catch (Exception ex) {
            logger.error("获取资源统计信息失败==>", ex);
            return null;
        }
    }

    /**
     * 用户删除资源
     *
     * @param user 操作的用户
     * @param id   要删除的资源ID
     * @return 返回失败与否
     */
    public void userDel(@NotNull User user, ResourceBelongType belongType, Long id) throws Exception {
        ResourceInfo info = fetch(id);
        if (info == null) {
            throw new BizException("资源不存在");
        }

        if (belongType != null && !info.getBelong().equals(belongType)) {
            throw new BizException("非法操作");
        }

        if (delete(id) <= 0) {
            throw new BizException("资源删除失败");
        }
        addOperationLog(user, BizConstants.ResourceConst.NAME, "删除资源", info.toString());
    }

    public ResourceInfo userEdit(User user, ResourceBelongType belongType, ResourceInfo info) throws Exception {
        return userEdit(user, belongType, info, BizConstants.ResourceConst.RESOURCE_INFO_CAN_EDIT_FIELDS);
    }

    /**
     * 用户编辑资源
     *
     * @param user 操作用户
     * @param info 要编辑的资源
     * @return 返回编辑结果
     * @throws Exception 失败时，返回对应message
     */
    public ResourceInfo userEdit(User user, ResourceBelongType belongType, ResourceInfo info, String editFields) throws Exception {
        ResourceInfo realInfo = fetch(info.getId());

        if (realInfo == null) {
            throw new BizException("资源不存在");
        }
        if (!realInfo.getBelong().equals(belongType)) {
            throw new BizException("非法操作");
        }

        info.setUpdateTime(DateUtils.getTS());
        info.setUpdateUserId(user.getId());

        if (!update(info, editFields.split(","))) {
            throw new BizException("编辑操作失败");
        }

        addOperationLog(user, BizConstants.ResourceConst.NAME, "编辑资源", info.toString());

        return fetch(info.getId());
    }

    public ResourceInfo userDetail(User user, ResourceBelongType belongType, Long id) throws Exception {
        ResourceInfo info = fetch(id);

        if (info == null) {
            throw new BizException("资源不存在");
        }

        if (!info.getBelong().equals(belongType)) {
            throw new BizException("非法操作");
        }

        return info;
    }

    private String getType(Object object) {
        String typeName = object.getClass().getName();
        int length = typeName.lastIndexOf(".");
        String type = typeName.substring(length + 1);
        return type;
    }

    /**
     * 上传资源附件
     *
     * @param partFile 文件对象 BASE64字符或者MultipartFile
     * @param info     附件附加信息设置
     * @param user     上传的用户
     * @return 成功后返回资源信息
     */
    public ResourceInfo userUpload(Object partFile, ResourceInfo info, User user) throws Exception {
        if (partFile == null) {
            throw new BizException("请选择文件");
        }

        Object fileObj = partFile;
        boolean isBase64 = getType(partFile).indexOf("String") >= 0;

        if (isBase64) {
            try {
                fileObj = Images.GenerateImage(partFile.toString(), new File(fileUploadManager.generateSavePath("png")));
            } catch (Exception ex) {
                logger.error("转换Base64图片失败，===>", ex);
                throw new BizException("转换Base64图片失败");
            }
        } else {
            // 如文件已上传过，则使用旧文件地址
            String md5 = fileUploadManager.md5((MultipartFile) partFile);
            if (!StringUtils.isNullOrEmpty(md5)) {
                ResourceInfo existInfo = fetchByHash(md5, null);
                if (existInfo != null) {
                    UploadedFileInfo uploadedFileInfo = fileUploadManager.pathFile2Info((fileUploadProperties.getLocalSavePath() + existInfo.getSavePath()).replaceAll("/", File.separator),false,true);
                    if (uploadedFileInfo != null)
                        return saveFile(uploadedFileInfo, info, user);
                }
            }
        }

        String fileName = isBase64 ? ((File) fileObj).getName() : ((MultipartFile) fileObj).getOriginalFilename();
        Long fileSize = isBase64 ? ((File) fileObj).length() : ((MultipartFile) fileObj).getSize();

        // 检查文件类型是否允许类型
        fileUploadManager.checkFileTypeByExt(FileUtils.getFileExt(fileName), info.getCate().getExtList());

        // 检查文件大小
        fileUploadManager.checkFileSize(fileSize);

        UploadedFileInfo uploadedFileInfo = fileUploadManager.saveFile(fileObj);
        return saveFile(uploadedFileInfo, info, user);
    }

    public ResourceInfo saveFile(UploadedFileInfo uploadedFileInfo, User user, String ip) {
        return saveFile(uploadedFileInfo, new ResourceInfo(ResourceCate.ATTACHMENT, ip), user);
    }

    public ResourceInfo saveFile(UploadedFileInfo uploadedFileInfo, ResourceInfo info, User user) {
        if (uploadedFileInfo == null) {
            throw new BizException("请选择上传文件");
        }
        if (info == null) {
            info = new ResourceInfo();
        }
        ResourceExtInfo extInfo = new ResourceExtInfo();
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setBelong(info.getBelong());
        resourceInfo.setRelationId(info.getRelationId());
        resourceInfo.setCate(info.getCate());
        resourceInfo.setName(uploadedFileInfo.getOriginName());
        resourceInfo.setDescription(uploadedFileInfo.getOriginName());
        resourceInfo.setKey(uploadedFileInfo.getLocalUrl());
        resourceInfo.setSize(uploadedFileInfo.getSize());
        resourceInfo.setContentType(uploadedFileInfo.getMime());
        resourceInfo.setSuffix(uploadedFileInfo.getSuffix());
        resourceInfo.setMd5(uploadedFileInfo.getMd5());
        resourceInfo.setSavePath(uploadedFileInfo.getRelativePath());
        resourceInfo.setAddUserId(user != null ? user.getId() : null);
        resourceInfo.setUpdateUserId(user != null ? user.getId() : null);
        resourceInfo.setCover(ImageUtils.isImage(uploadedFileInfo.getSavePath()) ? uploadedFileInfo.getLocalUrl() : null);
        resourceInfo.setIp(info.getIp());
        resourceInfo.setWidth(uploadedFileInfo.getWidth());
        resourceInfo.setHeight(uploadedFileInfo.getHeight());
        resourceInfo.setSource(info.getSource());
        resourceInfo.setDescription(StringUtils.isNullOrEmpty(info.getDescription()) ? uploadedFileInfo.getOriginName() : info.getDescription());
        if (uploadedFileInfo.getExif() != null) {
            extInfo.setExif(uploadedFileInfo.getExif());
            resourceInfo.setExtInfo(extInfo);
        }

        if (resourceInfo.getCate().equals(ResourceCate.THREE_MODEL)) {
            resourceInfo.setStatus(ResourceStatus.PROGRESSING);
            try {
                extInfo.setThreeModel(convertZip2Model(uploadedFileInfo));
                resourceInfo.setExtInfo(extInfo);
                resourceInfo.setStatus(ResourceStatus.SUCCESS);
            } catch (Exception ex) {
                resourceInfo.setStatus(ResourceStatus.FAIL);
                resourceInfo.setReason(ex.getMessage());
            }
        } else {
            resourceInfo.setStatus(ResourceStatus.SUCCESS);
        }

        if (ImageUtils.isImage(uploadedFileInfo.getSavePath())) {
            resourceInfo.setCover(resourceInfo.getKey());
        }

        ResourceInfo savedInfo = save(resourceInfo);
        if (user != null && savedInfo != null) {
            addOperationLog(user, BizConstants.ResourceConst.NAME, "上传附件", savedInfo.toString());
        }

        return savedInfo;
    }

    /**
     * ZIP压缩包转为模型资源类型
     *
     * @param fileInfo
     * @return
     * @throws Exception
     */
    public ThreeModel convertZip2Model(UploadedFileInfo fileInfo) throws Exception {
        ThreeModel model = new ThreeModel();
        String unZipPath = fileInfo.getSavePath().substring(0, fileInfo.getSavePath().lastIndexOf(fileInfo.getSuffix()) - 1);
        String unZipLinkPath = fileInfo.getLocalUrl().substring(0, fileInfo.getLocalUrl().lastIndexOf(fileInfo.getSuffix()) - 1);
        FileUtils.mkDir(unZipPath);
        ZipUtils.decompress(fileInfo.getSavePath(), unZipPath, false);

        List<File> dirFiles = FileUtils.searchFiles("mtl,obj,jpeg,jpg,png,bmp,tga", unZipPath);
        if (dirFiles.size() < 3) {
            throw new BizException("模型文件数量不正确");
        }

        List<String> textureFiles = new ArrayList<>();
        long totalSize = 0L;
        long textureSize = 0L;
        Collection mtlFiles = Collections.EMPTY_LIST;
        String mtlFileName = "";

        // 循环编辑设置 模型、MTL、贴图文件
        for (File file : dirFiles) {
            String fileExt = FileUtils.getFileExt(file.getName()).toLowerCase();
            String relativePath = file.getAbsolutePath().replaceAll(StringUtils.escapeExprSpecialWord(unZipPath), "");

            String relativeUrlPath = unZipLinkPath + relativePath.replaceAll(File.separator, "/");
            long fileSize = file.length();
            if (fileExt.equals("mtl")) {
                mtlFileName = file.getName();
                model.setMtl(relativeUrlPath);
                model.setMtlSize(fileSize);
            } else if ("obj".equals(fileExt)) {
                model.setObj(relativeUrlPath);
                model.setObjSize(fileSize);
                try {
                    ThreeObject threeObj = ObjUtils.read(file.getAbsolutePath());
                    model.setVerticesCount(threeObj.getVerticesCount());
                    model.setFacesCount(threeObj.getFacesCount());
                    model.setMaterialGroupCount(threeObj.getMaterialGroupCount());
                    model.setMaterialGroups(threeObj.getMaterialGroups());
                    model.setMeshGroups(threeObj.getMeshGroups());
                    model.setMeshGroupCount(threeObj.getMeshGroupCount());
                    model.setTexCoordsCount(threeObj.getTexCoordsCount());
                    model.setNormalsCount(threeObj.getNormalsCount());
                    mtlFiles = threeObj.getMtlFiles();
                } catch (Exception ex) {
                    logger.error("读取.OBJ文件信息失败，错误信息：", ex);
                }
            } else {
                textureFiles.add(relativeUrlPath);
                textureSize += fileSize;
                model.setTextures(textureFiles);
                model.setTexturesSize(textureSize);
            }
            totalSize += fileSize;
        }
        model.setTexturesCount(textureFiles.size());

        if (mtlFiles.size() == 0 || !mtlFiles.contains(mtlFileName)) {
            throw new BizException(".OBJ文件和MTL不匹配，请检查");
        }

        // 模型文件集合重新打包
        String newZipPath = fileUploadManager.generateSavePath("zip");
        ZipUtils.zipFiles(dirFiles.stream().map(file -> file.getAbsolutePath()).collect(Collectors.toList()), newZipPath);
        UploadedFileInfo newZipInfo = fileUploadManager.pathFile2Info(newZipPath);
        model.setZipSize(newZipInfo.getSize());
        model.setZip(newZipInfo.getLocalUrl());
        model.setTotalFileSize(totalSize);
        /**
         * 生成随机顶点距离
         */
        model.setVerticesDistance((new Random().nextInt(100 - 70) + 70) / 100 * 0.01);
        return model;
    }

    public PageredData<ResourceInfo> pager(Integer page, Integer pageSize, ResourceCate cate, ResourceBelongType belongType, Long relationId, String key, User user, Boolean isSetUserInfo) throws Exception {
        List<ResourceCate> cateList = new ArrayList<>();
        if (cate != null) {
            cateList.add(cate);
        }
        return pager(page, pageSize, cateList, belongType, relationId, key, user, isSetUserInfo, null, null, null);
    }

    public Cnd condition(Cnd cnd, User currentUser, ResourceBelongType belong, Long relationId, ResourceCate cate) {
        List<ResourceCate> cateList = new ArrayList<>();
        if (null != cate) {
            cateList.add(cate);
        }
        return condition(cnd, cateList, null, belong, relationId, null, currentUser, null, null, null);
    }

    public Cnd condition(Cnd cnd, List<ResourceCate> cateList, List<String> suffixList, ResourceBelongType belongType, Long relationId, String key, User user, Long minSize, Long maxSize, ResourceStatus resourceStatus) {
        cnd = PUtils.cndBySearchKey(cnd, key, "name", "description");

        cnd.andEX("belong", "=", belongType)
                .andEX("relationId", "=", relationId)
                .andEX("size", ">", minSize)
                .andEX("size", "<", maxSize)
                .andEX("status", "=", resourceStatus);

        if (cateList != null && cateList.size() > 0) {
            if (cateList.size() == 1) {
                cnd.andEX("cate", "=", cateList.get(0));
            } else {
                cnd.andEX("cate", "in", cateList);
            }
        }
        if (suffixList != null && suffixList.size() > 0) {
            if (suffixList.size() == 1) {
                cnd.andEX("suffix", "=", suffixList.get(0));
            } else {
                cnd.andEX("suffix", "in", suffixList);
            }
        }
        return addExistCondition(cnd);
    }

    /**
     * 资源分页数据
     *
     * @param page           页码
     * @param pageSize       页大小
     * @param cateList       资源类型分类
     * @param belongType     所属资源类别
     * @param relationId     相关业务ID
     * @param key            搜索关键字
     * @param isSetUserInfo  是否设置列用户基础信息
     * @param minSize        最小大小
     * @param maxSize        最大大小
     * @param user           当前操作用户
     * @param resourceStatus 资源状态
     */
    public PageredData<ResourceInfo> pager(Cnd cnd, Integer page, Integer pageSize, List<ResourceCate> cateList, ResourceBelongType belongType, Long relationId, String key, User user, Boolean isSetUserInfo, Long minSize, Long maxSize, ResourceStatus resourceStatus) {
        return pager(cnd, page, pageSize, cateList, null, belongType, relationId, key, user, isSetUserInfo, minSize, maxSize, resourceStatus);
    }

    public PageredData<ResourceInfo> pager(Cnd cnd, Integer page, Integer pageSize, List<ResourceCate> cateList, List<String> suffixList, ResourceBelongType belongType, Long relationId, String key, User user, Boolean isSetUserInfo, Long minSize, Long maxSize, ResourceStatus resourceStatus) {

        PageredData<ResourceInfo> pager = searchByPage(page, pageSize, condition(cnd, cateList, suffixList, belongType, relationId, key, user, minSize, maxSize, resourceStatus).desc("id"));

        if (isSetUserInfo) {
            pager.setDataList(userService.setListAddUserInfo(pager.getDataList()));
        }

        return pager;

    }

    public PageredData<ResourceInfo> pagerByBelongType(Integer page, Integer pageSize, ResourceBelongType belongType, List<ResourceCate> cateList, List<String> suffixList, List<Long> collIdList, String key, User user, Boolean isSetUserInfo, Long minSize, Long maxSize, ResourceStatus resourceStatus) {
        Cnd cnd = collIdList != null && collIdList.size() > 0 ? Cnd.NEW().andEX("relationId", "in", collIdList) : null;
        return pager(cnd, page, pageSize, cateList, suffixList, belongType, null, key, user, isSetUserInfo, minSize, maxSize, resourceStatus);
    }

    public List<NutMap> cateStatByBelongType(User currentUser, ResourceBelongType belongType, List<Long> collIdList, ResourceCate cate) throws Exception {
        Cnd cnd = collIdList != null && collIdList.size() > 0 ? Cnd.NEW().andEX("relationId", "in", collIdList) : null;
        return cateStatByDirectCondition(cnd, currentUser, belongType, null, cate);
    }

    public PageredData<ResourceInfo> pager(Integer page, Integer pageSize, List<ResourceCate> cateList, ResourceBelongType belongType, Long relationId, String key, User user, Boolean isSetUserInfo, Long minSize, Long maxSize, ResourceStatus resourceStatus) {
        return pager(null, page, pageSize, cateList, belongType, relationId, key, user, isSetUserInfo, minSize, maxSize, resourceStatus);
    }

    public List<NutMap> cateStat(ResourceBelongType belongType, Long startTime, Long endTime) throws Exception {
        return cateStat(belongType, null, startTime, endTime);
    }

    public List<NutMap> cateStat(ResourceBelongType belongType, Long collId, Long startTime, Long endTime) throws Exception {
        return cateStatByDirectCondition(conditionByTime(startTime, endTime).and("belong", "=", belongType).andEX("relationId", "=", collId));
    }

    public UploadedFileInfo cateStatExport(ResourceBelongType belongType, Long startTime, Long endTime) throws Exception {
        List<NutMap> list = cateStat(belongType, startTime, endTime);
        LinkedHashMap<String, String> keyMap = new LinkedHashMap<>();
        keyMap.put(BizConstants.PROPERTY_NAME, "类型");
        keyMap.put(BizConstants.COUNT_NAME, "数量");
        keyMap.put(BizConstants.SIZE_NAME, "总大小");
        return dataExport(list, keyMap);
    }

    public NutMap cateStatMap(ResourceBelongType belongType, Long startTime, Long endTime, String valuePropertyName) throws Exception {
        List<NutMap> list = cateStat(belongType, startTime, endTime);
        if (list == null || list.size() == 0) {
            return null;
        }

        NutMap parseMap = new NutMap();
        for (NutMap map : list) {
            parseMap.addv(ResourceCate.valueOf(map.getString(BizConstants.PROPERTY_NAME)).getName(), map.getLong(valuePropertyName));
        }
        return parseMap;
    }

    public NutMap cateStatBySize(ResourceBelongType belongType, Long startTime, Long endTime) throws Exception {
        return cateStatMap(belongType, startTime, endTime, BizConstants.SIZE_NAME);
    }

    public UploadedFileInfo cateStatBySizeExport(ResourceBelongType belongType, Long startTime, Long endTime) throws Exception {
        try {
            return typeStatExport(cateStatBySize(belongType, startTime, endTime), "资源类型", "字节大小");
        } catch (Exception ex) {
            throw ex;
        }
    }

    public NutMap cateStatByCount(ResourceBelongType belongType, Long startTime, Long endTime) throws Exception {
        return cateStatMap(belongType, startTime, endTime, BizConstants.COUNT_NAME);

    }

    public UploadedFileInfo cateStatByCountExport(ResourceBelongType belongType, Long startTime, Long endTime) throws Exception {
        return typeStatExport(cateStatByCount(belongType, startTime, endTime), "资源类型");
    }

    public List<NutMap> suffixStat(ResourceBelongType belongType, Long startTime, Long endTime) throws Exception {
        return mapListByCondition("resource.suffix.count.stat.by.condition", conditionByTime(startTime, endTime).and("belong", "=", belongType));
    }

    public UploadedFileInfo suffixStatExport(ResourceBelongType belongType, Long startTime, Long endTime) throws Exception {
        List<NutMap> list = suffixStat(belongType, startTime, endTime);
        LinkedHashMap<String, String> keyMap = new LinkedHashMap<>();
        keyMap.put(BizConstants.PROPERTY_NAME, "文件类型");
        keyMap.put(BizConstants.COUNT_NAME, "数量");
        keyMap.put(BizConstants.SIZE_NAME, "总大小");
        return dataExport(list, keyMap);
    }

    public NutMap suffixStatMap(ResourceBelongType belongType, Long startTime, Long endTime, String valuePropertyName) throws Exception {
        List<NutMap> list = suffixStat(belongType, startTime, endTime);
        if (list == null || list.size() == 0) {
            return null;
        }

        NutMap parseMap = new NutMap();
        for (NutMap map : list) {
            parseMap.addv(map.getString(BizConstants.PROPERTY_NAME).toUpperCase(), map.getLong(valuePropertyName));
        }
        return parseMap;
    }

    public NutMap suffixStatByCount(ResourceBelongType belongType, Long startTime, Long endTime) throws Exception {
        return suffixStatMap(belongType, startTime, endTime, BizConstants.COUNT_NAME);
    }

    public UploadedFileInfo suffixStatByCountExport(ResourceBelongType belongType, Long startTime, Long endTime) throws Exception {
        return typeStatExport(suffixStatByCount(belongType, startTime, endTime), "文件类型");
    }

    public NutMap suffixStatBySize(ResourceBelongType belongType, Long startTime, Long endTime) throws Exception {
        return suffixStatMap(belongType, startTime, endTime, BizConstants.SIZE_NAME);
    }

    public UploadedFileInfo suffixStatBySizeExport(ResourceBelongType belongType, Long startTime, Long endTime) throws Exception {
        return typeStatExport(suffixStatByCount(belongType, startTime, endTime), "文件类型", "字节大小");
    }

    public List<NutMap> sizeRangeStat(ResourceBelongType belongType, User currentUser, Long startTime, Long endTime) throws Exception {
        List<NutMap> list = new ArrayList<>();
        Cnd cnd = conditionByTime(startTime, endTime).and("belong", "=", belongType);
        list.add(calcSizeRangeStat("0-1M", cnd, 0, 1));
        list.add(calcSizeRangeStat("1-5M", cnd, 1, 5));
        list.add(calcSizeRangeStat("5-10M", cnd, 5, 10));
        list.add(calcSizeRangeStat("10-30M", cnd, 10, 30));
        list.add(calcSizeRangeStat("30M+", cnd, 30, 10000));
        return list;
    }

    private NutMap calcSizeRangeStat(String name, Cnd cnd, Integer minSize, Integer maxSize) {
        NutMap map = size2countCalcCondition(cnd.andEX("size", ">=", minSize * 1024 * 1024).andEX("size", "<", maxSize * 1024 * 1024));
        map.put(BizConstants.PROPERTY_NAME, name);
        return map;
    }

    public UploadedFileInfo sizeRangeStatExport(User currentUser, ResourceBelongType belongType, Long startTime, Long endTime) throws Exception {
        List<NutMap> list = sizeRangeStat(belongType, currentUser, startTime, endTime);
        LinkedHashMap<String, String> keyMap = new LinkedHashMap<>();
        keyMap.put(BizConstants.PROPERTY_NAME, "范围");
        keyMap.put(BizConstants.COUNT_NAME, "数量");
        keyMap.put(BizConstants.SIZE_NAME, "总大小");
        return dataExport(list, keyMap);
    }

    public NutMap sizeRangeStatByCount(User currentUser, ResourceBelongType belongType, Long startTime, Long endTime) throws Exception {
        List<NutMap> list = sizeRangeStat(belongType, currentUser, startTime, endTime);
        if (list == null || list.size() == 0) {
            return null;
        }

        NutMap parseMap = new NutMap();
        for (NutMap map : list) {
            parseMap.addv(map.getString(BizConstants.PROPERTY_NAME), map.getLong(BizConstants.COUNT_NAME));
        }
        return parseMap;
    }

    public String downloadResource(User currentUser, Long id, Boolean watermark) throws Exception {
        ResourceInfo info = fetch(id);

        String downloadUrl = info.getKey();
        String filePath = fileLinkSaveLocal(downloadUrl);

        Boolean isImg = ImageUtils.isImageFile(filePath);
        if (isImg && watermark != null && watermark) {
            return imageAddWatermark(filePath, new WatermarkConfig());
        }
        return filePath;
    }

    public UploadedFileInfo sizeRangeStatByCountExport(User currentUser, ResourceBelongType belongType, Long startTime, Long endTime) throws Exception {
        return typeStatExport(sizeRangeStatByCount(currentUser, belongType, startTime, endTime), "范围");
    }

    public List<TimeIntervalType.TimePeriod> countStat(ResourceBelongType belongType, Long startTime, Long endTime, TimeIntervalType intervalType) throws Exception {
        List<ResourceInfo> list = query(addExistCondition(conditionByTime(startTime, endTime).and("belong", "=", belongType)));

        List<TimeIntervalType.TimePeriod> timeIntervals = TimeIntervalType.calcTimePeriod(intervalType, startTime, endTime);
        for (TimeIntervalType.TimePeriod timeInterval : timeIntervals) {
            List<ResourceInfo> matchRecords = list == null || list.size() == 0 ? null : list.stream()
                    .filter(v -> v.getAddTime() >= timeInterval.getStartTime() && v.getAddTime() < timeInterval.getEndTime())
                    .collect(Collectors.toList());
            timeInterval.setData(matchRecords == null ? 0L : matchRecords.size());
        }
        return timeIntervals;
    }

    public UploadedFileInfo countStatExport(User currentUser, ResourceBelongType belongType, Long startTime, Long endTime, TimeIntervalType intervalType) throws Exception {
        return timeStatExport(countStat(belongType, startTime, endTime, intervalType));
    }

    /**
     * 根据文件Key查找Resource
     *
     * @param key
     * @return
     */
    public ResourceInfo fetchByKey(String key) {
        return fetch(Cnd.where("key", "=", key).and("del", "=", false));
    }

    public ResourceInfo fetchByHash(String hash, Boolean del) {
        return fetch(Cnd.where("md5", "=", hash).andEX("del", "=", del));
    }

    public UploadedFileInfo localPreview(User currentUser, ResourceInfo resourceInfo,boolean origin, Integer width, Integer height) throws Exception {
        if (resourceInfo.getSize() > IMAGE_PREVIEW_MAX_SIZE) {
            throw new BizException("超出可预览最大文件大小");
        }

        boolean isImg = !Arrays.asList("png,jpeg,jpg,bmp,gif".split(",")).contains(resourceInfo.getExt());

        if (isImg) {
            if(origin){
                return fileUploadManager.pathFile2Info((fileUploadProperties.getLocalSavePath()+resourceInfo.getSavePath()).replaceAll("/", File.separator));
            }

            String previewCachePath = getResCachePreviewImagePath(resourceInfo, width, height);

            // 检查文件是否存在
            if (new File(previewCachePath).exists()) {
                return fileUploadManager.pathFile2Info(previewCachePath);
            }

            try {
                Thumbnails.of(ImageIO.read(new File((fileUploadProperties.getLocalSavePath() + "/" + resourceInfo.getSavePath()).replaceAll("/", File.separator))))
                        .size(width, height)
                        .toFile(previewCachePath);
                return fileUploadManager.pathFile2Info(previewCachePath);
            } catch (Exception ex) {
                logger.error(String.format("缩率图生成失败，文件信息：%s, 错误信息==>", previewCachePath), ex);
                return null;
            }
        }
        return null;
    }

    /**
     * 封面预览（返回本地图片存储对象）
     *
     * @param currentUser
     * @param resNum
     * @return
     */
    public UploadedFileInfo localPreview(User currentUser, String resNum,boolean origin, Integer width, Integer height) throws Exception {
        if (StringUtils.isNull(resNum)) {
            throw new BizException("资源编号有误！");
        }
        ResourceInfo resourceInfo = fetch(resNum);
        return localPreview(currentUser, resourceInfo,origin, width, height);
    }

    public String getResCacheSavePath(ResourceInfo resourceInfo, boolean createDir) {
        Date addTime = DateUtils.ts2D(resourceInfo.getAddTime());
        String cachePath = String.format("%s/cache/%s/%s/%s", fileUploadProperties.getLocalSavePath(), String.format("%tY", addTime), String.format("%tm", addTime), String.format("%td", addTime) + (StringUtils.isNullOrEmpty(resourceInfo.getMd5()) ? "" : ("/" + resourceInfo.getMd5())));

        cachePath = cachePath.replaceAll("/", File.separator);
        if (createDir)
            FileUtils.mkDir(cachePath);

        return cachePath;
    }

    public String getResCachePreviewImagePath(ResourceInfo resourceInfo, Integer width, Integer height) {
        return ImageUtils.isImage(resourceInfo.getKey()) ? String.format("%s%spreview_%s_%s.%s", getResCacheSavePath(resourceInfo, true), File.separator, width, height, resourceInfo.getSuffix()) : null;
    }

    public UploadedFileInfo getFileInfo(ResourceInfo resourceInfo) {
        return fileUploadManager.pathFile2Info((fileUploadProperties.getLocalSavePath() + resourceInfo.getSavePath()).replaceAll("/", File.separator));
    }
}