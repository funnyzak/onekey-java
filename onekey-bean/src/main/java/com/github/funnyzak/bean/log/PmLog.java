package com.github.funnyzak.bean.log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funnyzak.bean.PotatoEntity;
import com.github.funnyzak.bean.log.enums.PmApp;
import com.github.funnyzak.bean.log.enums.PmType;
import com.github.funnyzak.bean.log.enums.PmUse;
import com.github.funnyzak.bean.log.enums.SmsServerType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.util.NutMap;
import com.github.funnyzak.common.utils.DateUtils;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/8 1:54 PM
 * @description 短消息
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Table("potato_pm_log")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
public class PmLog extends PotatoEntity {
    private static final Long serialVersionUID = 1L;

    @Column("pm_app")
    @Comment("使用短消息的APP")
    @ColDefine(width = 64)
    private String app = PmApp.SELF_CONSOLE.toString();

    @Column("pm_uid")
    @Comment("短消息所关联的标识符，如用户ID、用户名")
    @ColDefine(width = 64)
    private String userId;

    @Column("pm_type")
    @Comment("短消息类型")
    @ColDefine(width = 32)
    private PmType type = PmType.SMS;

    @Column("pm_receive")
    @Comment("短消息接收地址")
    @ColDefine(width = 64)
    private String receive;

    @Column("pm_use")
    @Comment("短消息用途")
    @ColDefine(width = 32)
    private PmUse use = PmUse.FORGET_PASSWORD;

    @Column("pm_code")
    @Comment("验证码")
    @Deprecated
    @ColDefine(width = 32)
    private String code;

    /**
     * 参数 JSON保存
     */
    @Column("pm_data")
    @Comment("动态参数")
    @ColDefine(width = 256)
    private String paramData;

    @Column("pm_content")
    @Comment("短消息内容")
    @ColDefine(width = 1024)
    private String content;

    @Column("pm_add_time")
    private Long addTime = DateUtils.getTS();

    @Column("pm_update_time")
    private Long updateTime = DateUtils.getTS();

    @Column("pm_is_success")
    @Comment("短消息是否发送成功")
    private Boolean success = false;

    @Column("pm_is_verify")
    @Comment("是否验证成功")
    private Boolean verify = false;

    @Column("pm_server")
    @Comment("短消息发送所使用的平台")
    @ColDefine(width = 32)
    private SmsServerType server = SmsServerType.SELF;

    @Column("pm_err_msg")
    @Comment("错误消息")
    @ColDefine(width = 512)
    private String errMsg;

    @Column("pm_ip")
    @Comment("客户端IP")
    @ColDefine(width = 32)
    private String ip;

    /**
     * 国家地域
     */
    private NutMap location;
}