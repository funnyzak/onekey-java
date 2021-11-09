package com.github.funnyzak.bean.cms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funnyzak.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;

/**
 * @author silenceace@gmail.com
 */

@Table("potato_city")
@Comment("城市表")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
public class City extends PotatoEntity {
    private static final long serialVersionUID = 1L;

    @Column("c_parentid")
    private Integer parentId;

    @Column("c_name")
    @Comment("城市名称")
    @ColDefine(width = 30)
    private String name;

    @Column("c_orderid")
    @Comment("排序值")
    private Integer orderId;

    @Column("c_display")
    @Comment("显示")
    private byte display;

    @Column("c_code")
    @Comment("城市代码")
    @ColDefine(width = 20)
    private String code;

    @Column("c_adcode")
    @Comment("城市地址代码")
    @ColDefine(width = 20)
    private String adCode;
}