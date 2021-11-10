package com.github.funnyzak.onekey.bean.config;

import com.github.funnyzak.onekey.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;


@Table("potato_sys_config")
@TableMeta("{mysql-charset:'utf8mb4'}")
@Comment("城市表")
@Data
@EqualsAndHashCode(callSuper = false)
public class Config extends PotatoEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Name
	@Column("cfg_key")
	@Comment("配置键")
	private String name;

	@Column("cfg_value")
	@Comment("配置值")
	@ColDefine(type = ColType.TEXT)
	private String value;

	@Column("cfg_description")
	@Comment("配置说明")
	@ColDefine(width = 250)
	private String description;

	@Column("cfg_installed")
	@Comment("是否内置标识")
	private Boolean installed = true;

}
