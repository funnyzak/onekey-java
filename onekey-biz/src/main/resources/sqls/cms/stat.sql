/*
cms.stat.article.count
*/
SELECT
	count(id)
FROM
	t_article a
	$condition

/*
cms.stat.place.count
*/
SELECT
	count(id)
FROM
	t_place p
	$condition