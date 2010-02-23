-- Somehow i have duplicate tags for some things.  This proc removes duplicate tag associations.

-- TREVIS You added the 'order by date desc' by clause after you ran this in dev, if it causes troule remove it

DECLARE myCursor Cursor FOR select GUID, POST_GUID, TAG_GUID from association_post_tag order by date desc
open myCursor
declare 
@postId uniqueidentifier,
@assId uniqueidentifier,
@tagId uniqueidentifier


--association_post_tag
--BEGIN TRAN UpdateTransaction
fetch next from myCursor into @assId, @postId, @tagId
While (@@FETCH_STATUS <> -1)
BEGIN
	IF (@@FETCH_STATUS <> -2)
	
	--print convert(varchar(50),@rootId ) +' ' + @threadPath
	
	delete association_post_tag where post_guid=@postId AND tag_guid=@tagId AND guid <> @assId

	--print convert(varchar(50),@threadId)
	
	fetch next from myCursor into @assId, @postId, @tagId
END

--ROLLBACK 
close myCursor
DEALLOCATE myCursor


