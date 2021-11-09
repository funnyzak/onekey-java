package com.github.funnyzak.bean.relation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.plugin.spring.boot.service.entity.DataBaseEntity;
import com.github.funnyzak.bean.enums.TypeRelationEnums;

import java.io.Serializable;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/2/3 3:35 下午
 * @description CollectionRelation
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Table("potato_relation")
@Comment("业务关系表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
@TableIndexes({@Index(name = "relation", fields = {"relationId", "type", "typeId"}, unique = true)})
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
public class TypeRelation extends DataBaseEntity implements Serializable {
    @Column("cr_relation_id")
    @Comment("类型所关联的业务ID")
    private Long relationId;

    @Column("cr_type")
    @Comment("关联的业务类型")
    private TypeRelationEnums type;

    @Column("cr_type_id")
    @Comment("类型所对应的类型主ID")
    private Long typeId;

    @Column("cr_add_time")
    private Long addTime;

    @Column("cr_update_time")
    private Long updateTime;
}