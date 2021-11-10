package com.github.funnyzak.onekey.bean.acl;

import com.github.funnyzak.onekey.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;


@Table("potato_role_permission")
@Comment("角色权限关系表")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class RolePermission extends PotatoEntity {

	private static final Long serialVersionUID = 1L;

	@Column("r_id")
	@Comment("角色id")
	private Long roleId;

	@Column("p_id")
	@Comment("权限id")
	private Long permissionId;
}
