package com.github.funnyzak.onekey.bean.cms;

import com.github.funnyzak.onekey.bean.PotatoEntity;
import com.github.funnyzak.onekey.bean.cms.enums.ArticleType;
import com.github.funnyzak.onekey.bean.label.LabelInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.util.NutMap;
import com.github.funnyzak.onekey.common.utils.StringUtils;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/5/31 6:14 上午
 * @description Article
 */
@Table("biz_article")
@TableMeta("{mysql-charset:'utf8mb4'}")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class Article extends PotatoEntity {
    private static final long serialVersionUID = 1L;

    @Column("a_num")
    @ColDefine(width = 32)
    private String num = StringUtils.getUUIDNumberOnly();

    @Column("a_type")
    @ColDefine(width = 32)
    private ArticleType type;

    @Column("a_relation_id")
    @Comment("功能类型对应的相关ID")
    private Integer relationId;

    @Column("a_cate_id")
    @Comment("分类ID")
    private Long cateId;

    private LabelInfo cateInfo;

    @Column("a_title")
    @Comment("标题")
    @ColDefine(width = 128)
    private String title;

    @Column("a_sub_title")
    @Comment("子标题")
    @ColDefine(width = 100)
    private String subTitle;

    @Column("a_description")
    @Comment("短介绍")
    @ColDefine(width = 200)
    private String description;

    @Column("a_pic")
    @Comment("图片地址")
    @ColDefine(width = 300)
    private String pic;

    @Column("a_audio")
    @Comment("相关音频")
    @ColDefine(width = 300)
    private String audio;

    @Column("a_video")
    @Comment("相关视频")
    @ColDefine(width = 300)
    private String video;

    @Column("a_add_time")
    @Comment("添加时间")
    private Long addTime;

    @Column("a_add_user_id")
    @Comment("添加用户")
    private Long addUserId;

    private NutMap addUser;

    @Column("a_update_user_id")
    @Comment("更新用户")
    private Long updateUserId;

    private NutMap updateUser;

    @Column("a_update_time")
    @Comment("更新时间")
    private Long updateTime;

    @Column("a_author")
    @Comment("作者")
    @ColDefine(width = 32)
    private String author;

    @Column("a_source")
    @Comment("来源")
    @ColDefine(width = 32)
    private String source;

    @Column("a_view_count")
    @Comment("查看数")
    private Integer viewCount = 0;

    @Column("a_dig_count")
    @Comment("点赞数")
    private Integer digCount = 0;

    @Column("a_published")
    @Comment("是否发布")
    private Boolean published = false;

    @Column("a_content")
    @Comment("详细内容")
    @ColDefine(type = ColType.TEXT)
    private String content;

    @Column("a_config")
    @Comment("其他配置")
    @ColDefine(type = ColType.TEXT)
    private String configS;

    @Column("a_del")
    private Boolean del = false;
}