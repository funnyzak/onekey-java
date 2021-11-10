package com.github.funnyzak.onekey.bean.open;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funnyzak.onekey.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.util.NutMap;

import java.util.UUID;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/18 11:38 AM
 * @description OpenRequestLog
 */
@Table("potato_connector_log")
@Comment("连接器通信日志")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
@TableIndexes({@Index(name = "conn_id", fields = {"connectorId", "addTime"}, unique = true)})
public class ConnectorLog extends PotatoEntity {

    private static final long serialVersionUID = 1L;

    @Column("cl_guid")
    @ColDefine(width = 32)
    private String guid = UUID.randomUUID().toString().replace("-", "");

    @Column("conn_id")
    private Long connectorId;

    private NutMap connector;

    @Column("cl_system")
    @ColDefine(width = 32)
    @Comment("系统")
    private String system;

    @Column("cl_method_name")
    @Comment("请求的函数方法")
    @ColDefine(width = 256)
    private String methodName;

    @Column("cl_agent")
    @Comment("客户端信息")
    @ColDefine(width = 1024)
    private String agent;

    @Column("cl_add_time")
    private Long addTime = System.currentTimeMillis();

    @Column("cl_request_time")
    @Comment("请求时间戳，毫秒")
    private Long requestTime = System.currentTimeMillis();

    @Column("cl_elapsed_time")
    @Comment("耗时时间戳，毫秒")
    private Long elapsedTime;

    @Column("cl_request_ip")
    @Comment("请求来自IP")
    private String ip;

}