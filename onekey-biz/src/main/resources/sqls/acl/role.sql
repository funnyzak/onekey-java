/* list.role.by.user.id */
SELECT
    r.*
FROM
    potato_role r
        LEFT JOIN potato_user_role ur ON r.id = ur.r_id
WHERE
        ur.u_id = @userId

/* find.roles.with.user.powerd.info.by.user.id */
SELECT
    r.id,
    r.r_desc AS description,
    r.r_name AS `name`,
    CASE
        sur.id IS NULL
        WHEN 1 THEN
            FALSE ELSE TRUE
        END AS selected
FROM
    potato_role r
        LEFT JOIN ( SELECT * FROM potato_user_role WHERE u_id = @id ) sur ON r.id = sur.r_id