package com.github.funnyzak.onekey.bean.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funnyzak.onekey.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;
import com.github.funnyzak.onekey.bean.enums.Gender;
import com.github.funnyzak.onekey.common.utils.DateUtils;
import com.github.funnyzak.onekey.common.utils.StringUtils;

@Table("potato_member")
@Comment("会员信息")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
@TableIndexes({@Index(name = "username", fields = {"appId", "username"})})
public class MemberInfo extends PotatoEntity {

    private static final long serialVersionUID = 1L;

    @Column("mi_app_id")
    @Comment("所属APP")
    @ColDefine(width = 64)
    private String appId;

    @Column("mi_user_name")
    @Comment("用户名")
    @ColDefine(width = 32)
    private String username = "pid_" + StringUtils.getNumber(17);

    @Column("mi_nick_name")
    @Comment("昵称")
    @ColDefine(width = 128)
    private String nickName;

    @Column("mi_id_num")
    @Comment("身份证号")
    @ColDefine(width = 24)
    private String idNum;

    @Column("mi_email")
    @Comment("邮箱")
    @ColDefine(width = 100)
    private String email;

    @Column("mi_birth_day")
    @Comment("出生日期")
    private Long birthDay;

    @Column("mi_phone")
    @Comment("手机号（登陆用）")
    @ColDefine(width = 18)
    private String phone;

    @Column("mi_pwd")
    @Comment("密码")
    @ColDefine(width = 32)
    private String pwd;

    @Getter
    private Boolean isPwd;

    private Boolean getIsPwd() {
        return !StringUtils.isNullOrEmpty(this.pwd);
    }

    @Column("mi_register_time")
    @Comment("注册时间")
    private Long registerTime = DateUtils.getTS();

    @Column("mi_register_ip")
    @Comment("注册IP")
    @ColDefine(width = 64)
    private String registerIp;

    @Column("mi_add_user_id")
    @Comment("添加用户")
    private Long addUserId;

    @Column("mi_add_time")
    @Comment("添加时间")
    private Long addTime = DateUtils.getTS();

    @Column("mi_update_time")
    @Comment("修改时间")
    private Long updateTime = DateUtils.getTS();

    @Column("mi_update_user_id")
    @Comment("更新用户")
    private Long updateUserId;

    @Column("mi_last_login_time")
    @Comment("上次登陆时间")
    private Long lastLoginTime = DateUtils.getTS();

    @Column("mi_last_login_ip")
    @Comment("上次登陆IP")
    @ColDefine(width = 64)
    private String lastLoginIp;

    @Column("mi_real_name")
    @Comment("真实姓名")
    @ColDefine(width = 128)
    private String realName;

    @Column("mi_signature")
    @Comment("个性签名")
    @ColDefine(width = 512)
    private String signature;

    @Column("mi_avatar")
    @Comment("头像")
    @ColDefine(width = 300)
    private String avatar;

    @Column("mi_gender")
    @Comment("性别")
    @ColDefine(width = 32)
    private Gender gender = Gender.MAN;

    @Column("mi_wechat_union_id")
    @Comment("微信联合ID")
    @ColDefine(width = 64)
    private String weUnionId;

    @Column("mi_we_app_open_id")
    @Comment("微信小程序开放ID")
    @ColDefine(width = 64)
    private String weAppOpenId;

    @Column("mi_we_mp_open_id")
    @Comment("微信公众号开放ID")
    @ColDefine(width = 64)
    private String weMpOpenId;

    @Column("mi_locked")
    @Comment("是否锁定")
    private Boolean locked = false;

    @Column("mi_del")
    @Comment("是否删除")
    @JsonIgnore
    private Boolean del = false;
}