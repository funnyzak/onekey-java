/* find.datarules.with.master.powerd.info.by.master.id */
SELECT
    dr.id,
    dr.dr_name AS `name`,
    dr.dr_description AS description,
    dr.dr_module AS `module`,
    dr.dr_rule_type AS ruleType,
    CASE
        mdr.id IS NULL
        WHEN 1 THEN
            FALSE ELSE TRUE
        END AS selected
FROM
    potato_data_rule dr
        LEFT JOIN (
        SELECT
            *
        FROM
            potato_data_rule_relation
        WHERE
                dr_master_type = @masterType
          AND dtr_id = @masterId
    ) mdr ON dr.id = mdr.dr_id