package com.github.funnyzak.onekey.bean.open;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.funnyzak.onekey.bean.PotatoEntity;
import com.github.funnyzak.onekey.bean.open.enums.ConnectorPermission;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import com.github.funnyzak.onekey.common.utils.DateUtils;
import com.github.funnyzak.onekey.common.utils.PUtils;
import com.github.funnyzak.onekey.common.utils.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/18 10:30 AM
 * @description Connector
 */
@Table("potato_connector")
@Comment("连接器，用对外提供的服务验证")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
@TableIndexes({@Index(name = "secretId_unique", fields = {"secretId"}, unique = true)})
public class Connector extends PotatoEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 连接器对应的APP的ID唯一标识，一般指某个业务APP。由英文、数字、下划线构成。由用户自定义，不能重复。
     */
    @Column("c_app_id")
    @Comment("连接器对应的APP的ID唯一标识，一般指某个业务APP。由英文、数字、下划线构成。由用户自定义，不能重复。")
    @ColDefine(width = 64)
    private String appId;

    @Column("c_name")
    @Comment("连接器名称")
    @ColDefine(width = 128)
    private String name;

    @Column("c_intro")
    @Comment("连接器介绍")
    @ColDefine(width = 2048)
    private String intro;

    /**
     * 连接器Key
     */
    @Name
    @Column("c_secret_id")
    @ColDefine(width = 32)
    @Comment("连接器Key")
    private String secretId;

    /**
     * 连接器密钥
     */
    @Column("c_secret_key")
    @ColDefine(width = 64)
    @Comment("连接器密钥")
    private String secretKey;

    /**
     * 每分钟限制API的次数，0为不限制
     */
    @Column("c_limit_api_count_minute")
    @Comment("每分钟限制API的次数，0为不限制")
    private Integer limitApiCountMinute = 0;

    @Column("c_add_user_id")
    private Long addUserId;

    private NutMap addUser;

    @Column("c_add_time")
    @Comment("添加时间")
    private Long addTime = DateUtils.getTS();

    @Column("c_update_user")
    private Long updateUserId;

    private NutMap updateUser;

    @Column("c_update_time")
    @Comment("编辑时间")
    private Long updateTime = DateUtils.getTS();

    @Column("c_verify_app_sign")
    @Comment("是否验证签名")
    private Boolean verifyAppSign = true;

    /**
     * 是否验证时间戳
     */
    @Column("c_verify_timestamp")
    @Comment("是否验证时间戳")
    private Boolean verifyTS = true;

    /**
     * IP限制列表，空为不限制。多个请用换行符分割
     */
    @Column("c_white_ip_list")
    @Comment("IP白名单列表，空为不限制。多个请用换行符分割")
    @ColDefine(width = 1024)
    private String whiteIps;

    @JsonProperty("whiteIpList")
    public List<String> whiteIpList() {
        return whiteIps == null || whiteIps.equals("") ? null : Lang.array2list(whiteIps.split("\n"));
    }

    /**
     * 连接器前端权限配置权限代码列表，多个请用半角逗号分割
     */
    @Column("c_front_permission")
    @Comment("连接器前端权限配置")
    @ColDefine(width = 2048)
    @JsonIgnore
    private String permissions;

    private List<ConnectorPermission> permissionList;

    public List<ConnectorPermission> getPermissionList() {
        return StringUtils.isNullOrEmpty(this.permissions) ? null : PUtils.enums2List(ConnectorPermission.class, this.permissions);
    }

    public void setPermissionList(List<ConnectorPermission> list) {
        if (list == null || list.size() == 0) {
            this.permissions = null;
        } else {
            this.permissions = list.stream().map(v -> v.toString()).collect(Collectors.joining(","));
        }
    }

    /**
     * 是否启用
     */
    @Column("c_enable")
    @Comment("是否启用")
    private Boolean enable = true;

    /**
     * 是否删除
     */
    @Column("c_del")
    @Comment("是否删除")
    private Boolean del = false;
}