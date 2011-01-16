-- Search idea
select p.guid postId, t.VALUE title, e.BODY body,  CASE (p.GUID) WHEN p.ROOT_GUID then 1 ELSE 0 END as root 
from POST p 
inner join ENTRY e on p.GUID=e.POST_GUID 
inner join TAG t on p.TAG_GUID_TITLE = t.GUID
where t.VALUE like '%trevis%' or BODY like '%trevis%'
order by root desc, p.DATE desc

--Autocomplete
select p.guid postId, t.VALUE title from POST p 
inner join TAG t on p.TAG_GUID_TITLE = t.GUID
where t.VALUE like '%tt%' AND p.GUID = p.ROOT_GUID
order by p.MASS desc, p.DATE desc

--Autocomplete postid only
select p.guid postId from POST p 
inner join TAG t on p.TAG_GUID_TITLE = t.GUID
where t.VALUE like 'tre%' OR t.VALUE like '% tre%' AND p.GUID = p.ROOT_GUID
order by p.MASS desc, p.DATE desc