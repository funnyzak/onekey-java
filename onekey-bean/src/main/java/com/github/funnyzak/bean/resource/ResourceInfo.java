package com.github.funnyzak.bean.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.funnyzak.bean.PotatoEntity;
import com.github.funnyzak.bean.resource.enums.ResourceBelongType;
import com.github.funnyzak.bean.resource.enums.ResourceCate;
import com.github.funnyzak.bean.resource.enums.ResourceStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
import com.github.funnyzak.common.utils.StringUtils;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/11/11 7:25 PM
 * @description Resource
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Table("potato_resource_info")
@Comment("资源表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
public class ResourceInfo extends PotatoEntity {

    public ResourceInfo() {
    }

    public ResourceInfo(String ip) {
        this.ip = ip;
    }

    public ResourceInfo(ResourceCate cate, String ip) {
        this.cate = cate;
        this.ip = ip;
    }

    public ResourceInfo(Long relationId, ResourceBelongType belongType, String ip) {
        this.relationId = relationId;
        this.belong = belongType;
        this.ip = ip;
    }

    public ResourceInfo(Long relationId, ResourceBelongType belongType, ResourceCate cate, String ip) {
        this.cate = cate;
        this.relationId = relationId;
        this.belong = belongType;
        this.ip = ip;
    }

    public ResourceInfo(Long relationId, ResourceBelongType belongType, ResourceCate cate, String description, String ip) {
        this.description = description;
        this.cate = cate;
        this.relationId = relationId;
        this.belong = belongType;
        this.ip = ip;
    }

    public ResourceInfo( Long relationId, ResourceBelongType belongType, ResourceCate cate, String description, String cover, String ip) {
        this.cover = cover;
        this.description = description;
        this.cate = cate;
        this.relationId = relationId;
        this.belong = belongType;
        this.ip = ip;
    }

    private static final Long serialVersionUID = 1L;

    @Name
    @Column("res_num")
    @ColDefine(width = 32)
    private String num = StringUtils.getUUIDNumberOnly();

    /**
     * 资源归属类型
     */
    @Column("res_belong_type")
    @ColDefine(width = 32)
    private ResourceBelongType belong;

    /**
     * 和belongType配合使用，对应的相关ID
     */
    @Column("res_relation_id")
    private Long relationId;

    private NutMap relation;

    /**
     * 资源所属类型
     */
    @Column("res_cate")
    @ColDefine(width = 64)
    private ResourceCate cate = ResourceCate.ATTACHMENT;

    @Column("res_name")
    @ColDefine(width = 200)
    private String name;

    /**
     * 资源保存路径，保存相对路径（包含原始件文件名）
     */
    @Comment("资源保存路径")
    @Column("res_save_path")
    @ColDefine(width = 300)
    @JsonIgnore
    private String savePath;

    /**
     * 资源保存路径，保存相对路径
     */
    @Comment("资源md5")
    @Column("res_md5")
    @ColDefine(width = 32)
    private String md5;

    @Column("res_description")
    @ColDefine(width = 1024)
    private String description;

    /**
     * 资源保存路径
     */
    @Column("res_key")
    @ColDefine(width = 200)
    private String key;

    /**
     * 资源下载地址
     */
    private String downloadUrl;

    /**
     * 资源大小 字节
     */
    @Column("res_size")
    private Long size;

    /**
     * 资源MIME
     */
    @Column("res_content_type")
    @ColDefine(width = 128)
    private String contentType;

    /**
     * 资源后缀
     */
    @Column("res_suffix")
    @ColDefine(width = 24)
    private String suffix;

    @Column("res_add_user")
    private Long addUserId;

    private NutMap addUser;

    @Column("res_add_time")
    private Long addTime = System.currentTimeMillis() / 1000;

    @Column("res_update_user")
    private Long updateUserId;

    @Column("res_update_time")
    private Long updateTime = System.currentTimeMillis() / 1000;

    @Column("res_cover")
    @Comment("资源封面")
    @ColDefine(width = 200)
    private String cover;

    @Column("res_source")
    @ColDefine(width = 32)
    @Comment("资源来源")
    private String source;

    @Column("res_status")
    @Comment("资源状态")
    @ColDefine(width = 32)
    private ResourceStatus status = ResourceStatus.SUCCESS;

    @Column("res_reason")
    @Comment("失败原因")
    @ColDefine(width = 512)
    private String reason;

    @Column("res_width")
    private Integer width;

    @Column("res_height")
    private Integer height;

    /**
     * 保存扩展信息
     */
    @Column("res_ext")
    @ColDefine(type = ColType.TEXT)
    @JsonIgnore
    private String ext;

    private ResourceExtInfo extInfo;

    public ResourceExtInfo getExtInfo() {
        try {
            return StringUtils.isNullOrEmpty(this.ext) ? null : Json.fromJson(ResourceExtInfo.class, this.ext);
        } catch (Exception ex) {
            return null;
        }
    }

    public void setExtInfo(ResourceExtInfo info) {
        this.ext = Json.toJson(info);
    }

    /**
     * 资源相关的配置信息
     */
    @Column("res_config")
    @ColDefine(width = 1024)
    @JsonIgnore
    private String config;

    @JsonProperty("config")
    private ResourceConfigInfo configInfo;

    public ResourceConfigInfo getConfigInfo() {
        try {
            return StringUtils.isNullOrEmpty(this.config) ? null : Json.fromJson(ResourceConfigInfo.class, this.config);
        } catch (Exception ex) {
            return null;
        }
    }

    public void setConfigInfo(ResourceConfigInfo info) {
        this.config = Json.toJson(info);
    }

    @Column("res_ip")
    @ColDefine(width = 48)
    @JsonIgnore
    private String ip;

    /**
     * 是否删除
     */
    @Column("res_del")
    @Comment("是否删除")
    private Boolean del = false;
}