package com.github.funnyzak.onekey.bean.acl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funnyzak.onekey.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;

import java.util.Comparator;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/21 1:46 PM
 * @description DataRule
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Table("potato_data_rule")
@Comment("数据权限配置")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
@TableIndexes({@Index(name = "type_unique", fields = {"module", "ruleType"}, unique = true)})
public class DataRule extends PotatoEntity {
    private static final Long serialVersionUID = 1L;

    @Column("dr_name")
    @Comment("名称")
    @ColDefine(width = 128)
    private String name;

    @Column("dr_module")
    @Comment("数据权限所配置的系统模块")
    @ColDefine(width = 64)
    private DataRuleModule module;

    @Column("dr_description")
    @Comment("描述")
    @ColDefine(width = 256)
    private String description;

    @Column("dr_rule_type")
    @Comment("数据权限所配置的权限规则")
    @ColDefine(width = 64)
    private DataRuleDataPermissionType ruleType;

    @Column("dr_add_time")
    private Long addTime = System.currentTimeMillis() / 1000;

    public static class DataRuleComparator implements Comparator<DataRule> {
        @Override
        public int compare(DataRule o1, DataRule o2) {
            return o2.getRuleType().getOrder() - o1.getRuleType().getOrder();
        }
    }
}
