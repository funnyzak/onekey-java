package com.github.funnyzak.bean.department;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funnyzak.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/21 1:41 PM
 * @description Department
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Table("potato_department")
@Comment("部门表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
public class Department extends PotatoEntity {
    private static final Long serialVersionUID = 1L;

    @Column("d_name")
    @ColDefine(width = 128)
    private String name;

    @Column("d_description")
    @ColDefine(width = 512)
    private String description;

    @Column("d_num")
    @ColDefine(width = 128)
    private String num;

    @Column("d_parent_id")
    private Long parentId;

    @Column("d_order_id")
    private Integer orderId;

    @Column("d_add_time")
    private Long addTime;

    @Column("d_add_user_id")
    private Long addUserId;

    /**
     * 部门成员数
     */
    private Integer userCount;
}