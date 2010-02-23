--This query populates the thread_guid's on posts which is a new thing for the Thread/ThreadSet concept being developed for v7
DECLARE myCursor Cursor FOR select ass.post_guid from association_post_tag ass inner join tag t on ass.tag_guid=t.guid 
inner join post p on p.guid = ass.post_guid
where t.type='REVIEW' AND p.parent_guid != root_guid order by p.date desc 

open myCursor
declare 
@postId uniqueidentifier,
@parentId uniqueidentifier


--BEGIN TRAN UpdateTransaction
fetch next from myCursor into @postId
While (@@FETCH_STATUS <> -1)
BEGIN
	IF (@@FETCH_STATUS <> -2)
	
	
	

	--print convert(varchar(50),@rootId ) +' ' + @threadPath

	select @parentId = guid from post where guid=@postId

	update post set parent_guid = root_guid, thread_guid = guid, path='00000' where guid=@postId
	
	delete from association_post_tag where post_guid=@parentId
	delete from Entry where post_guid=@parentId
	delete from post where guid=@parentId
	

	fetch next from myCursor into @postId
END

--ROLLBACK 
close myCursor
DEALLOCATE myCursor

--delete from post where guid not in (select post_guid from association_post_tag)
