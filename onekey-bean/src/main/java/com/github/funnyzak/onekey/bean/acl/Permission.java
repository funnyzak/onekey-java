package com.github.funnyzak.onekey.bean.acl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funnyzak.onekey.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;


/**
 *
 * @author silenceace@gmail.com
 *
 */
@Table("potato_permission")
@Comment("权限表")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
public class Permission extends PotatoEntity {

    /**
     *
     */
    private static final Long serialVersionUID = 1L;

    @Column("p_name")
    @Name
    @Comment("权限名称")
    @ColDefine(width = 64)
    private String name;

    @Column("p_intro")
    @Comment("对权限的具体说明")
    @ColDefine(width = 256)
    private String intro;

    @Column("p_desc")
    @Comment("描述，一般为页面显示名称")
    @ColDefine(width = 32)
    private String description;

    @Column("installed")
    @Comment("内置标识")
    private boolean installed;

    @Column("p_group")
    @Comment("权限分组标识，同一标识为一个分组")
    @ColDefine(width = 64)
    private String group;
}