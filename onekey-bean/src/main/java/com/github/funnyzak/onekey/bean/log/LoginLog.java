package com.github.funnyzak.onekey.bean.log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funnyzak.onekey.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;

import java.util.Date;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Table("potato_user_login")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
public class LoginLog extends PotatoEntity {

	private static final Long serialVersionUID = 1L;

	@Column("login_user_id")
	@Comment("登录用户 id")
	private Long addUserId;

	private NutMap addUser;

	@Column("login_time")
	@Comment("登录时间")
	private Date loginTime = Times.now();

	@Column("login_ip")
	@Comment("登录 ip")
	@ColDefine(width = 32)
	private String ip;

	/**
	 * 国家地域
	 */
	private NutMap location;
}
