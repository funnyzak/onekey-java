/* 根据条件获取资源类型，以及它对应的数量 */
/* resource.cate.count.stat.by.condition */
SELECT
    r.res_cate `name`,
    count(r.res_cate) `count`,
    sum(r.res_size) `size`
FROM
    potato_resource_info r
    $condition
GROUP BY
    r.res_cate

/* 根据资源条件和业务关系表获取资源类型，以及它对应的数量 */
/* resource.cate.count.stat.by.condition.and.relation */
SELECT
    r.res_cate `name`,
    count( r.res_cate ) `count`,
    sum( r.res_size ) `size`
FROM
    potato_resource_info r
    INNER JOIN potato_relation rn ON r.id = rn.cr_relation_id
  $condition
  AND rn.cr_type = @relation_type
  AND rn.cr_type_id = @relation_type_id
GROUP BY
    r.res_cate



/* 根据条件获取各文件类型统计图表 */
/* resource.suffix.count.stat.by.condition */
SELECT
    r.res_suffix 'name',
    count(r.res_suffix) `count`,
    sum(r.res_size) `size`
FROM
    potato_resource_info r
    $condition
GROUP BY
    r.res_suffix

/* 根据条件获取文件汇总信息 */
/* resource.count.stat.by.condition */
SELECT
    count(*) `count`,
    sum(res_size) `size`
FROM
    potato_resource_info r
    $condition
