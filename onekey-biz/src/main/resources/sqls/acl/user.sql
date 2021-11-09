/* list.user.name.by.ids */
SELECT
    u.id AS id,
    u.u_name AS `name`,
    u.u_real_name AS realName,
    u.u_nick_name AS nickName,
    u.u_head_key AS headKey,
    u.u_email AS email,
    u.u_phone AS phone
FROM
    potato_user u
WHERE
        u.id IN ( $idList )