package com.github.funnyzak.onekey.bean.label;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funnyzak.onekey.bean.PotatoEntity;
import com.github.funnyzak.onekey.bean.label.enums.LabelInfoType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.util.NutMap;
import com.github.funnyzak.onekey.common.utils.StringUtils;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Table("potato_label")
@Comment("标签表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
public class LabelInfo extends PotatoEntity {
    private static final Long serialVersionUID = 1L;

    @Column("lb_parent_id")
    private Long parentId;

    @Column("lb_num")
    private String num = StringUtils.getRandomLetterAndDigital(7);

    /**
     * 是否系统内置
     */
    @Column("lb_system")
    private Boolean system = false;

    @Column("lb_name")
    @ColDefine(width = 128)
    private String name;

    /**
     * 名称对应的值，一般用于 system类型
     */
    @Column("lb_value")
    @ColDefine(type = ColType.TEXT)
    private String value;

    @Column("lb_description")
    @ColDefine(width = 1024)
    private String description;

    @Column("lb_cover")
    @ColDefine(width = 300)
    private String cover;

    @Column("lb_type")
    @ColDefine(width = 32)
    @JsonIgnore
    private LabelInfoType type = LabelInfoType.ARTICLE_CATE;

    @Column("lb_add_user_id")
    private Long addUserId;

    private NutMap addUser;

    @Column("lb_add_time")
    private Long addTime = System.currentTimeMillis() / 1000;

    @Column("lb_update_time")
    private Long updateTime = System.currentTimeMillis() / 1000;

    @Column("lb_update_user")
    private Long updateUserId;

    private NutMap updateUser;

    @Column("lb_order_id")
    private Integer orderId = 0;

    @Column("lb_del")
    @JsonIgnore
    private Boolean del = false;
}