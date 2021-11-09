package com.github.funnyzak.bean.acl;

import com.github.funnyzak.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;


@Table("potato_user_permission")
@Comment("用户权限关系表")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class UserPermission extends PotatoEntity {
	/**
	 * 
	 */
	private static final Long serialVersionUID = 1L;
	/**
	 * 用户id
	 */
	@Column("u_id")
	@Comment("用户id")
	private Long userId;
	/**
	 * 权限id
	 */
	@Column("p_id")
	@Comment("权限id")
	private Long permissionId;

}