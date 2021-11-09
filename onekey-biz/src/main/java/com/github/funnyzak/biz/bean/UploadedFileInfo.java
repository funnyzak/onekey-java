package com.github.funnyzak.biz.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import com.github.funnyzak.common.utils.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Leon Yang
 * @date 2019/07/27
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadedFileInfo {

    /**
     * 文件host
     */
    private String host;

    /**
     * 文件地址
     */
    private String path;

    /**
     * 原始文件名
     */
    private String originName;

    /**
     * 新的文件名
     */
    private String newName;

    /**
     * 文件MIME
     */
    private String mime;

    /**
     * 文件大小 字节
     */
    private Long size;

    /**
     * 文件后缀
     */
    private String suffix;

    /**
     * 该文件URL访问地址
     */
    private String localUrl;

    @JsonIgnore
    private String savePath;

    /**
     * 相对路径(unix路径格式，不包含配置路径前缀)
     */
    private String relativePath;

    private Integer width;

    private Integer height;

    /**
     * 文件md5
     */
    private String md5;

    /**
     * Exif
     * IPTC
     * XMP
     * JFIF / JFXX
     * ICC Profiles
     * Photoshop fields
     * WebP properties
     * WAV properties
     * AVI properties
     * PNG properties
     * BMP properties
     * GIF properties
     * ICO properties
     * PCX properties
     * QuickTime properties
     * MP4 properties
     */
    private Map<String, Object> exif;

    /**
     * 缩率图
     */
    private List<String> Thumbs;

    private String cloudKey;

    private String cloudDomain;

    private String cloudUrl;

    public String getCloudUrl() {
        return !StringUtils.isNullOrEmpty(this.cloudDomain) && !StringUtils.isNullOrEmpty(this.cloudKey) ? (this.cloudDomain + "/" + this.cloudKey) : null;
    }

    @JsonProperty("url")
    public String getUrl() {
        return StringUtils.isNullOrEmpty(this.getCloudUrl()) ? this.localUrl : this.getCloudUrl();
    }


}