package com.github.funnyzak.bean.log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funnyzak.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import com.github.funnyzak.common.utils.DateUtils;

/**
 * @author Leon Yang
 * @date 2019/07/29
 */
@Data
@Table("potato_web_log")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
@EqualsAndHashCode(callSuper = true)
public class WebLog extends PotatoEntity {

    private static final Long serialVersionUID = 1L;

    @Column("wl_controller_name")
    @Comment("控制器")
    @ColDefine(width = 256)
    private String controllerName;

    @Column("wl_operation_name")
    @Comment("操作名称")
    @ColDefine(width = 256)
    private String operationName;

    @Column("wl_action_time")
    @Comment("动作时间")
    private Long actionTime = DateUtils.getTS();

    @Column("wl_take_time")
    @Comment("执行时间")
    private Long takeTime;

    @Column("wl_error")
    @Comment("是否出错")
    private boolean error = false;

    @Column("wl_request")
    @Comment("请求参数")
    @ColDefine(type = ColType.TEXT)
    private String request;

    @Column("wl_response")
    @Comment("返回数据")
    @ColDefine(type = ColType.TEXT)
    private String response;

    @Column("wx_stack_trace")
    @Comment("错误信息")
    @ColDefine(type = ColType.TEXT)
    private String stackTrace;
}
