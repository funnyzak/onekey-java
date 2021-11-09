package com.github.funnyzak.bean.acl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funnyzak.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableMeta;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/21 1:46 PM
 * @description DataRuleRelation
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Table("potato_data_rule_relation")
@Comment("数据规则关系表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
public class DataRuleRelation extends PotatoEntity {

    private static final Long serialVersionUID = 1L;

    @Column("dr_id")
    @Comment("关联的数据权限主键ID")
    private Long dataRuleId;

    @Column("dr_master_type")
    @Comment("数据权限关联的主体类型，目前分为 用户和角色")
    private DataRuleRelationMasterType dataRuleRelationMaster;

    @Column("dtr_id")
    @Comment("结合类型相关的业务ID")
    private Long dataRuleRelationId;

}