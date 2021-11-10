package com.github.funnyzak.onekey.bean.acl;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;
import com.github.funnyzak.onekey.bean.PotatoEntity;
import com.github.funnyzak.onekey.bean.enums.UserStatus;
import com.github.funnyzak.onekey.common.utils.DateUtils;


@Table("potato_user")
@Comment("用户表")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
public class User extends PotatoEntity {

    private static final Long serialVersionUID = 1L;

    @Column("u_name")
    @Name
    @Comment("用户登录名")
    @ColDefine(width = 100)
    private String name;

    @Column("u_real_name")
    @Comment("用户真实姓名")
    @ColDefine(width = 32)
    private String realName;

    @Column("u_nick_name")
    @Comment("用户昵称")
    @ColDefine(width = 64)
    private String nickName;

    @Column("u_pwd")
    @Comment("用户登录密码")
    @ColDefine(width = 32)
    private String password;

    @Column("u_phone")
    @Comment("用户手机号")
    @ColDefine(width = 17)
    private String phone;

    @Column("u_email")
    @Comment("用户邮箱")
    @ColDefine(width = 100)
    private String email;

    @Column("u_head_key")
    @Comment("用户头像")
    @ColDefine(width = 200)
    private String headKey;

    @Column("u_create_time")
    @Comment("用户创建时间")
    private Long createTime = DateUtils.getTS();

    @Column("u_update_time")
    @Comment("用户编辑时间")
    private Long updateTime = System.currentTimeMillis() / 1000;

    @Column("u_status")
    @Comment("用户状态")
    @ColDefine(width = 24)
    private UserStatus status = UserStatus.ACTIVE;

    public boolean isAvailable() {
        return status == UserStatus.ACTIVE;
    }
}