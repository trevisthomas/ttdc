--V7 Adding the thread mass to root posts! I'm doing this for search

DECLARE myCursor Cursor FOR select GUID, POST_GUID, TAG_GUID from association_post_tag where title=1
open myCursor
declare @assId uniqueidentifier, @postId uniqueidentifier, @total integer, @tagId uniqueidentifier, @tagMass integer, @assMass integer

--BEGIN TRAN UpdateTransaction

fetch next from myCursor into @assId, @postId, @tagId
While (@@FETCH_STATUS <> -1)
BEGIN
	IF (@@FETCH_STATUS <> -2)
	
	--print convert(varchar(50),@postId)
	
	select @assMass = count(*) from post where root_guid=@postId
	
	update post set mass=@assMass where guid=@postId

	fetch next from myCursor into @assId, @postId, @tagId
END

--ROLLBACK 
close myCursor
DEALLOCATE myCursor

--Set the mass of non root posts to 0. (remember to disallow nulls)
update post set mass = 0 where mass is null


