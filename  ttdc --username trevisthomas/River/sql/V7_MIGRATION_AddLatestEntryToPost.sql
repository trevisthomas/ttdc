--This proc adds the entry id and date to the post.  New for V7
DECLARE myCursor Cursor FOR select GUID from post 
open myCursor
declare 
@entryId uniqueidentifier,
@editDate datetime,
@postId uniqueidentifier


--BEGIN TRAN UpdateTransaction
fetch next from myCursor into @postId
While (@@FETCH_STATUS <> -1)
BEGIN
	IF (@@FETCH_STATUS <> -2)
	
	--print convert(varchar(50),@rootId ) +' ' + @threadPath

	select top 1  @entryId = guid, @editDate = date from Entry where post_guid=@postId order by date desc

	--print convert(varchar(50),@threadId)
	
	update post set LATEST_ENTRY_GUID=@entryId, EDIT_DATE=@editDate where guid=@postId

	fetch next from myCursor into @postId
END

--ROLLBACK 
close myCursor
DEALLOCATE myCursor


