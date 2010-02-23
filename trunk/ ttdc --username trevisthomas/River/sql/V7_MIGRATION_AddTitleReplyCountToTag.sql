--V7 This query adds the number of thread replies to the tag mass.
-- you thought about keeping this seperate but decided that that was pointless
-- Trevis in Nov 2009 you decided not to use the post counts to make a composite mass for tags.  It is not 
-- useful in tag browser to have them

--DEPRECATED

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
	select @tagMass = count(*) from association_post_tag where tag_guid = @tagId
	select @total = @assMass + @tagMass
	-- print @total
	
	update tag set mass=@total where guid=@tagId

	fetch next from myCursor into @assId, @postId, @tagId
END

--ROLLBACK 
close myCursor
DEALLOCATE myCursor


