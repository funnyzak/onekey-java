package com.github.funnyzak.onekey.bean.department;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funnyzak.onekey.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/21 1:43 PM
 * @description UserDepartment
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Table("potato_user_department")
@Comment("用户和部门关机表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
@TableIndexes({@Index(name = "idx_unique", fields = {"userId", "departmentId"}, unique = true)})
public class UserDepartment extends PotatoEntity {
    private static final Long serialVersionUID = 1L;

    @Column("u_id")
    private Long userId;

    @Column("dept_id")
    private Long departmentId;
}