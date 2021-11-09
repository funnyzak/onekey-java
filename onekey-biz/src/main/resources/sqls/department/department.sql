/* find.departments.with.user.powerd.info.by.user.id */
SELECT
    d.id,
    d.d_num AS num,
    d.d_name AS `name`,
    d.d_description AS description,
    CASE
        sur.id IS NULL
        WHEN 1 THEN
            FALSE ELSE TRUE
        END AS selected
FROM
    potato_department d
        LEFT JOIN (
        SELECT
            *
        FROM
            potato_user_department
        WHERE
                u_id = @id
    ) sur ON d.id = sur.dept_id


/* find.users.with.department.powerd.info.by.department.id */
SELECT
    u.id AS uid,
    u.u_name AS `name`,
    u.u_real_name AS realName,
    CASE
        ud.u_id IS NULL
        WHEN 1 THEN
            FALSE ELSE TRUE
        END AS selected
FROM
    potato_user u
        LEFT JOIN (
        SELECT
            ud.u_id
        FROM
            potato_user_department ud
        WHERE
           ud.dept_id = @deptId
    ) ud ON ud.u_id = u.id