 -- not fully tested yet, but this is for the forum project
 -- delete non root topic tag associations
 delete from ASSOCIATION_POST_TAG where ASSOCIATION_POST_TAG.GUID in (
 select a.GUID from ASSOCIATION_Post_Tag as a inner join POST as p on p.GUID = a.POST_GUID
 inner join TAG as t on t.GUID=a.TAG_GUID where p.PARENT_GUID is not null and t.TYPE = 'TOPIC')