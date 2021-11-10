package com.github.funnyzak.onekey.bean.acl;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;
import com.github.funnyzak.onekey.bean.PotatoEntity;


/**
 * 角色表
 */
@Table("potato_role")
@Comment("角色表")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class Role extends PotatoEntity {

    private static final Long serialVersionUID = 1L;

    @Column("r_name")
    @Comment("角色名称")
    @Name
    @ColDefine(width = 64)
    private String name;

    @Column("r_desc")
    @Comment("描述")
    @ColDefine(width = 256)
    private String description;

    @Column("r_installed")
    @Comment("是否内置角色标识")
    private boolean installed = false;

    public String ToString() {
        return String.format("name:%s,description:%s", name, description);
    }

}
