/* list.direct.permission.by.user.id */
SELECT
    *
FROM
    potato_permission p
        LEFT JOIN potato_user_permission up ON p.id = up.p_id
WHERE
        up.u_id = @userId

/* list.role.permission.by.role.id */
SELECT
    p.p_name
FROM potato_role_permission rp INNER JOIN potato_permission p ON p.id = rp.p_id
WHERE rp.r_id = @roleId

/* list.indirect.permission.by.user.id */
SELECT
    p.*
FROM
    potato_permission p
        LEFT JOIN potato_role_permission rp ON p.id = rp.p_id
        LEFT JOIN potato_user_role ur ON ur.r_id = rp.r_id
WHERE
        ur.u_id = @userId

/* find.permissions.with.user.powered.info.by.user.id */
SELECT
    p.id,
    p.p_name AS `name`,
    p.p_group AS `group`,
    p.p_intro AS intro,
    p.p_desc AS description,
    CASE
        sup.id IS NULL
        WHEN 1 THEN
            FALSE ELSE TRUE
        END AS selected
FROM
    potato_permission p
        LEFT JOIN ( SELECT * FROM potato_user_permission WHERE u_id = @id ) sup ON p.id = sup.p_id

/* find.permissions.with.role.powered.info.by.role.id */
SELECT
    p.id,
    p.p_name AS `name`,
    p.p_desc AS description,
    p.p_group AS `group`,
    p.p_intro AS intro,
    CASE
        srp.id IS NULL
        WHEN 1 THEN
            FALSE ELSE TRUE
        END AS selected
FROM
    potato_permission p
        LEFT JOIN ( SELECT * FROM potato_role_permission WHERE r_id = @id ) srp ON p.id = srp.p_id