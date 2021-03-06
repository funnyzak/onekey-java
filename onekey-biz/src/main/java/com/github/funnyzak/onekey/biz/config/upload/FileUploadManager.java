package com.github.funnyzak.onekey.biz.config.upload;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.github.funnyzak.onekey.biz.service.CloudStorageService;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.digest.DigestUtils;
import org.nutz.lang.Lang;
import com.github.funnyzak.onekey.biz.bean.CloudStorageObject;
import com.github.funnyzak.onekey.biz.bean.UploadedFileInfo;
import com.github.funnyzak.onekey.biz.exception.BizException;
import com.github.funnyzak.onekey.common.image.ImageUtils;
import com.github.funnyzak.onekey.common.utils.DateUtils;
import com.github.funnyzak.onekey.common.utils.FileUtils;
import com.github.funnyzak.onekey.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Leon Yang
 * @date 2019/07/27
 */
@Configuration
@EnableConfigurationProperties(FileUploadProperties.class)
@ConditionalOnProperty(value = "file-upload", matchIfMissing = true)
public class FileUploadManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final FileUploadProperties fileUploadProperties;
    private final CloudStorageService cloudStorageService;

    public FileUploadManager(FileUploadProperties fileUploadProperties
            , @Qualifier("AliOssFileManager") @Autowired(required = false) CloudStorageService aliCloudStorageService
            , @Qualifier("TencloudStorageService") @Autowired(required = false) CloudStorageService tenCloudStorageService) {
        this.fileUploadProperties = fileUploadProperties;
        this.cloudStorageService = fileUploadProperties.getCloud() != null && fileUploadProperties.getCloud().equals("cos") ? tenCloudStorageService : aliCloudStorageService;
    }

    public UploadedFileInfo uploadCloud(UploadedFileInfo uploadedFile) {
        try {
            if (cloudStorageService == null) return uploadedFile;

            CloudStorageObject cosResult = cloudStorageService.upload(uploadedFile.getSavePath(), uploadedFile.getPath() + uploadedFile.getNewName());
            uploadedFile.setCloudKey(cosResult.getKey());
            uploadedFile.setCloudDomain(cosResult.getDomain());
            return uploadedFile;
        } catch (Exception ex) {
            logger.error("??????????????????????????????????????????", ex);
        }
        return uploadedFile;
    }

    /**
     * ????????????
     *
     * @param file
     * @return
     * @throws IOException
     */
    public UploadedFileInfo uploadImage(MultipartFile file) throws Exception {
        checkFileType(file.getContentType(), fileUploadProperties.getLimitImageType());
        checkFileSize(file.getSize());

        return saveFile(file);
    }

    /**
     * ??????????????????????????????????????????????????????
     *
     * @param fileSize
     * @return
     */
    public void checkFileSize(Long fileSize) throws Exception {
        if (fileSize > fileUploadProperties.getLimitOtherSize()) {
            throw new BizException(String.format("?????????????????????????????????%s", FileUtils.formatFileSize(fileUploadProperties.getLimitOtherSize())));
        }
    }

    /**
     * ????????????MD5
     *
     * @param filePath
     * @return
     */
    public String md5(String filePath) {
        try {
            return DigestUtils.md5Hex(new FileInputStream(filePath));
        } catch (Exception ex) {
            logger.error(String.format("????????????MD5????????????????????????%s???????????????=>", filePath), ex);
            return null;
        }
    }

    /**
     * ?????????????????????md5
     *
     * @param file
     * @return
     * @throws IOException
     */
    public String md5(MultipartFile file) {
        try {
            byte[] uploadBytes = file.getBytes();
            //file->byte[],??????md5
            String md5Hex = DigestUtils.md5Hex(uploadBytes);
            //file->InputStream,??????md5
            String md5Hex1 = DigestUtils.md5Hex(file.getInputStream());
            //??????????????????md5
            String s = DigestUtils.md5Hex("?????????");
            return md5Hex;
        } catch (Exception ex) {
            logger.error(String.format("????????????MD5??????????????????%s???????????????=>", file.getName()), ex);
        }
        return null;
    }

    /**
     * ??????????????????????????????fileInfo
     *
     * @param savePath
     * @return
     * @throws Exception
     */
    public UploadedFileInfo pathFile2Info(String savePath, Boolean thumbImg, Boolean getImgInfo, Boolean uploadCloud) {
        try {
            File file = new File(savePath);
            if (!file.exists()) {
                logger.error("?????????????????????????????????{}", savePath);
                return null;
            }

            String realPrefixPath = Lang.isWin() ? fileUploadProperties.getLocalSavePath().substring(1).replace("/", "\\") : fileUploadProperties.getLocalSavePath();
            String unixPath = savePath.replaceAll(realPrefixPath, "").replace("\\", "/");
            String fileVirtualPath = fileUploadProperties.getVirtualPath() + unixPath;
            fileVirtualPath = fileVirtualPath.substring(0, fileVirtualPath.lastIndexOf("/") + 1);

            UploadedFileInfo uploadedFile = new UploadedFileInfo();
            uploadedFile.setSuffix(FileUtils.getFileExt(file.getName()));
            uploadedFile.setOriginName(file.getName());
            uploadedFile.setMime(FileUtils.getContentType(FileUtils.getFileExt(file.getName())));
            uploadedFile.setSize(file.length());
            uploadedFile.setHost(fileUploadProperties.getVirtualHost());
            uploadedFile.setPath(fileVirtualPath);
            uploadedFile.setNewName(file.getName());
            uploadedFile.setSavePath(savePath);
            uploadedFile.setMd5(md5(savePath));
            uploadedFile.setRelativePath(unixPath);
            uploadedFile.setLocalUrl(fileUploadProperties.getVirtualHost() + fileVirtualPath + file.getName());
            if (getImgInfo != null && getImgInfo) {
                uploadedFile = setImgFileSize(uploadedFile);
                uploadedFile = setImgExif(uploadedFile);
            }
            if (thumbImg && ImageUtils.isImageFile(file)) {
                uploadedFile = thumbGenerate(uploadedFile);
            }
            if (uploadCloud != null && uploadCloud) {
                uploadedFile = setUploadCloud(uploadedFile);
            }
            return uploadedFile;
        } catch (Exception ex) {
            logger.error("????????????????????????????????????{}", ex.toString());
            return null;
        }
    }

    public UploadedFileInfo pathFile2Info(String savePath, Boolean thumbImg, Boolean getImgInfo) {
        return pathFile2Info(savePath, thumbImg, getImgInfo, false);
    }

    /**
     * ???????????????????????????
     *
     * @param ext ???????????????ext
     * @return
     */
    public String generateSavePath(String ext) {
        return generateSavePath(ext, null);
    }

    public String generateSavePath(String ext, String filename) {
        return generateSavePath(ext, null, filename);
    }

    /**
     * ???????????????????????????
     *
     * @param ext      ???????????????ext
     * @param filename ?????????
     * @return
     */
    public String generateSavePath(String ext, String path, String filename) {
        // ????????????????????????
        String webSubPath = StringUtils.isNull(path) ? ("/" + DateUtils.format("yyyy/MM/dd", new Date())) : path;
        // ?????????????????????????????????????????????
        String savePath = fileUploadProperties.getLocalSavePath() + webSubPath + "/";
        String realSavePath = Lang.isWin() ? savePath.substring(1).replaceAll("/", "\\\\") : savePath;
        FileUtils.mkDir(realSavePath);
        return String.format("%s%s.%s", realSavePath, filename == null ? UUID.randomUUID().toString().replaceAll("-", "") : filename, ext);
    }

    public UploadedFileInfo pathFile2Info(String savePath) {
        return pathFile2Info(savePath, false, false);
    }

    public UploadedFileInfo saveFile(Object file) throws Exception {
        return saveFile(file, null);
    }

    private String getType(Object object) {
        String typeName = object.getClass().getName();
        int length = typeName.lastIndexOf(".");
        String type = typeName.substring(length + 1);
        return type;
    }

    /**
     * ?????????????????????
     *
     * @param fileObj ???????????? ?????????File??????MultipartFile
     * @return
     * @throws IOException
     */
    public UploadedFileInfo saveFile(Object fileObj, String prefixPathName) throws Exception {
        if (fileObj == null) {
            return null;
        }

        boolean isMultipartFile = getType(fileObj).indexOf("MultipartFile") >= 0;
        String fileExt = null;
        Long fileSize = 0L;
        String fileMime = null;
        String originalFileName = null;

        if (isMultipartFile) {
            MultipartFile file = (MultipartFile) fileObj;
            originalFileName = file.getOriginalFilename();
            fileExt = FileUtils.getFileExt(originalFileName).toLowerCase();
            fileSize = file.getSize();
            fileMime = file.getContentType();
        } else {
            File file = (File) fileObj;
            originalFileName = file.getName();
            fileExt = FileUtils.getFileExt(originalFileName).toLowerCase();
            fileSize = file.length();
            fileMime = FileUtils.getContentType(fileExt);
        }

        // ????????????????????????
        String webSubPath = (StringUtils.isNullOrEmpty(prefixPathName) ? "" : ("/" + prefixPathName)) + "/" + DateUtils.format("yyyy/MM/dd", new Date());
        // ????????????????????????
        String fileNameNoExt = UUID.randomUUID().toString().replaceAll("-", "");
        String fileName = String.format("%s.%s", fileNameNoExt, fileExt);
        // ??????????????????????????????????????????
        String fileVirtualPath = fileUploadProperties.getVirtualPath() + webSubPath + "/";

        // ?????????????????????????????????????????????
        String savePath = fileUploadProperties.getLocalSavePath() + webSubPath + "/";
        String realSavePath = Lang.isWin() ? savePath.substring(1).replaceAll("/", "\\\\") : savePath;

        try {
            if (isMultipartFile) {
                if (!FileUtils.saveFile(((MultipartFile) fileObj).getBytes(), realSavePath, fileName)) {
                    throw new BizException("??????????????????!");
                }
            } else {
                FileUtils.saveFile(Files.readAllBytes(((File) fileObj).toPath()), realSavePath, fileName);
            }
        } catch (Exception ex) {
            logger.error("????????????????????????????????????{}", ex);
            throw ex;
        }

        UploadedFileInfo uploadedFile = new UploadedFileInfo();
        uploadedFile.setMd5(md5(realSavePath + fileName));
        uploadedFile.setSuffix(fileExt);
        uploadedFile.setOriginName(originalFileName.substring(0, originalFileName.length() - fileExt.length() - 1));
        uploadedFile.setMime(fileMime);
        uploadedFile.setSize(fileSize);
        uploadedFile.setHost(fileUploadProperties.getVirtualHost());
        uploadedFile.setPath(fileVirtualPath);
        uploadedFile.setRelativePath(webSubPath + "/" + fileName);
        uploadedFile.setNewName(fileName);
        uploadedFile.setSavePath(realSavePath + fileName);
        uploadedFile.setLocalUrl(fileUploadProperties.getVirtualHost() + fileVirtualPath + fileName);

        uploadedFile = setImgFileSize(uploadedFile);

        /********??????EXIF??????*********/
        uploadedFile = setImgExif(uploadedFile);

        /*********start: ?????????????????????************/
        if (fileUploadProperties.isThumbImage() && ImageUtils.isImage(originalFileName) && fileUploadProperties.getImageThumbSize().size() > 0) {
            uploadedFile = thumbGenerate(uploadedFile);
        }
        /*********end: ?????????????????????************/

        /********????????????*********/
        uploadedFile = setUploadCloud(uploadedFile);

        logger.info("??????????????????????????????????????????{}", uploadedFile.toString());

        return uploadedFile;
    }

    /**
     * ??????????????????
     *
     * @param uploadedFileInfo
     * @return
     */
    public UploadedFileInfo setUploadCloud(UploadedFileInfo uploadedFileInfo) {
        if (StringUtils.isNullOrEmpty(fileUploadProperties.getCloud()) || cloudStorageService == null) {
            return uploadedFileInfo;
        }
        uploadedFileInfo = uploadCloud(uploadedFileInfo);
        return uploadedFileInfo;
    }

    public UploadedFileInfo setImgFileSize(UploadedFileInfo uploadedFile) {
        if (ImageUtils.isImage(uploadedFile.getSavePath())) {
            try {
                BufferedImage sourceImg = ImageIO.read(new FileInputStream(uploadedFile.getSavePath()));
                uploadedFile.setWidth(sourceImg.getWidth());
                uploadedFile.setHeight(sourceImg.getHeight());
            } catch (Exception ex) {
                logger.error("????????????????????????????????????????????????:", ex);
            }
        }
        return uploadedFile;
    }

    public UploadedFileInfo setImgExif(UploadedFileInfo uploadedFile) {
        if (ImageUtils.isImage(uploadedFile.getSavePath())) {
            try {
                Metadata metadata = ImageMetadataReader.readMetadata(new File(uploadedFile.getSavePath()));
                ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

                if (directory != null) {
                    Map<String, Object> exifList = new HashMap<>();
                    for (Tag tag : directory.getTags()) {
                        exifList.put(tag.getTagName(), tag.getDescription());
                    }
                    uploadedFile.setExif(exifList);
                }

            } catch (Exception ex) {
                logger.error("????????????Exif??????????????????????????????{}", ex.toString());
            }
        }
        return uploadedFile;
    }

    public UploadedFileInfo thumbGenerate(UploadedFileInfo file) {
        if (!ImageUtils.isImage(file.getSavePath())) {
            return file;
        }

        String realThumbSavePath = file.getSavePath().substring(0, file.getSavePath().lastIndexOf(file.getSuffix()) - 1) + (Lang.isWin() ? "\\" : "/");
        String thumbVirtualPath = file.getLocalUrl().substring(0, file.getLocalUrl().lastIndexOf(file.getSuffix()) - 1) + "/";
        List<String> thumbList = new ArrayList<>();

        FileUtils.mkDir(realThumbSavePath);

        for (Integer thumbSize : fileUploadProperties.getImageThumbSize()) {
            try {
                Thumbnails.of(ImageIO.read(new File(file.getSavePath())))
                        .size(thumbSize.intValue(), thumbSize.intValue())
                        .toFile(realThumbSavePath + thumbSize.toString() + "." + file.getSuffix());
            } catch (Exception ex) {
                logger.error("???????????????????????????????????????{}, ???????????????{}", file, ex);
            }

            thumbList.add(String.format("%s%s.%s", thumbVirtualPath, thumbSize, file.getSuffix()));
        }
        file.setThumbs(thumbList);
        return file;
    }

    /**
     * ???????????????????????????????????????
     *
     * @param fileMime
     * @return
     */
    public void defaultAllowType(String fileMime) throws Exception {
        checkFileType(fileMime, fileUploadProperties.getLimitImageType());
        checkFileType(fileMime, fileUploadProperties.getLimitOtherType());
    }

    /**
     * ?????????????????????
     */
    public void isImageFile(String fileMime) throws Exception {
        checkFileType(fileMime, Lang.array2list("jpg,jpeg,png,bmp,gif".split(",")));
    }

    /**
     * ?????????????????????????????????
     *
     * @param fileMime
     * @param extList  ??????????????????,??? jpg???png???jpeg
     * @return
     */
    public void checkFileType(String fileMime, List<String> extList) throws Exception {
        if (extList.stream().filter(ext -> fileMime.toLowerCase().equals(FileUtils.getContentType(ext))).collect(Collectors.toList()).size() > 0) {
            return;
        }

        throw new BizException(String.format("?????????????????????????????????%s", String.join("|", extList)));
    }

    public void checkFileTypeByExt(String fileExt, List<String> extList) throws Exception {
        if (extList.stream().filter(ext -> fileExt.toLowerCase().equals(ext)).collect(Collectors.toList()).size() > 0) {
            return;
        }

        throw new BizException(String.format("?????????????????????????????????%s", String.join("|", extList)));
    }

}