/* list.connector.name.by.ids */
SELECT
    c.id AS id,
    c.c_name AS `name`,
    c.c_intro AS intro,
    c.c_app_id AS appId,
    c.c_secret_id AS `secretId`
FROM
    potato_connector c
WHERE
     c.id IN ( $idList )
     AND
     c.c_del = 0


/* list.connector.name */
SELECT
    c.id AS id,
    c.c_name AS `name`,
    c.c_intro AS intro,
    c.c_app_id AS appId,
    c.c_secret_id AS `secretId`
FROM
    potato_connector c
WHERE
     c.c_del = 0