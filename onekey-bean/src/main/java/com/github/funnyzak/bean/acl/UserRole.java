package com.github.funnyzak.bean.acl;

import com.github.funnyzak.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;


@Table("potato_user_role")
@Comment("用户角色关系表")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class UserRole extends PotatoEntity {

	/**
	 * 
	 */
	private static final Long serialVersionUID = 1L;

	@Column("u_id")
	@Comment("用户id")
	private Long userId;

	@Column("r_id")
	@Comment("角色id")
	private Long roleId;

}
