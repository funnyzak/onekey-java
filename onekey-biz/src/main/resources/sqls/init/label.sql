/* 初始化标签表数据 potato_label */
/* label.info.data.init */
INSERT INTO `potato_label` (id, lb_parent_id, lb_system, lb_name,
                                       lb_value, lb_description, lb_type,
                                       lb_del)
VALUES (1, 0, 1, '文章分类1', NULL, '文章分类1', 'TEMP_CATE', 0),
       (2, 0, 1, '文章分类2', NULL, '文章分类2', 'TEMP_CATE', 0),
       (10000, 0, 1, '占位', NULL, '占位', 'NONE', 0)