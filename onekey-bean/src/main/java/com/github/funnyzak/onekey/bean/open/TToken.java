package com.github.funnyzak.onekey.bean.open;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funnyzak.onekey.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;
import com.github.funnyzak.onekey.common.utils.DateUtils;

@Table("potato_token")
@Comment("令牌信息")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
@TableIndexes({@Index(name = "token", fields = {"csId", "relationId"})})
public class TToken extends PotatoEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 对应的连接器ID
     */
    @Column("p_connector_secret_id")
    @Comment("对应的连接器ID")
    @ColDefine(width = 16)
    private String csId;

    /**
     * 对应的连接器AppId,冗余属性
     */
    @Column("p_app_id")
    @Comment("对应的连接器AppId,冗余属性")
    @ColDefine(width = 64)
    private String appId;

    /**
     * 此令牌对应的业务ID，如：对应前端会员ID
     */
    @Column("p_relation_id")
    @Comment("此令牌对应的业务ID，如：对应前端会员ID")
    private Long relationId;

    @Name
    @Column("p_token")
    @ColDefine(width = 64)
    private String token;

    /**
     * 令牌添加时间
     */
    @Column("p_add_time")
    private Long addTime = DateUtils.getTS();

    /**
     * 令牌更新时间
     */
    @Column("p_update_time")
    @Comment("令牌更新时间")
    private Long updateTime;

    /**
     * 令牌过期时间
     */
    @Column("p_expire_time")
    @Comment("令牌过期时间")
    private Long expireTime;
}