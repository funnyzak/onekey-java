package com.github.funnyzak.onekey.bean.log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funnyzak.onekey.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.util.NutMap;
import com.github.funnyzak.onekey.common.utils.DateUtils;


@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Table("potato_opt_log")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
public class OperationLog extends PotatoEntity {

    /**
     *
     */
    private static final Long serialVersionUID = 1L;

    @Column("opt_user_id")
    @Comment("操作用户 id")
    private Long addUserId;

    private NutMap addUser;

    @Column("opt_ip")
    @Comment("操作人员 ip 地址")
    @ColDefine(width = 32)
    private String ip;

    @Column("opt_module")
    @Comment("操作功能模块")
    @ColDefine(width = 32)
    private String module;

    @Column("opt_action")
    @Comment("操作的具体功能")
    @ColDefine(width = 32)
    private String action;

    @Column("opt_description")
    @Comment("功能描述")
    @ColDefine(type = ColType.TEXT)
    private String description;

    @Column("opt_action_time")
    @Comment("操作时间")
    private Long actionTime = DateUtils.getTS();

    @Column("opt_execution_time")
    @Comment("方法执行时间")
    private Long operationTime = DateUtils.getTS();

    private NutMap user;
}
