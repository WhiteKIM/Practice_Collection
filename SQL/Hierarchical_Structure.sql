-- postgreSQL 계층형 쿼리 연습장
-- 늦게 생성된 내용을 먼저 보여주고, 해당 글에 달린 자식 데이터를 출력
with recursive replylist(id, content, pid, grp, level, root, depth) as (
  select
			id,
			content,
			pid,
			id as grp,
			1 as level,
			id as root,
			id::varchar as depth
		from reply
		where pid is null
		union
		select
			c.id,
			c.content,
			c.pid,
			p.id as grp,
			p.level + 1 as level,
			p.root,
			p.depth || '-' || c.id
		from reply c join replylist p on c.pid = p.id
  )
	
select * from replylist_view
order by root desc, level
