--New for V7.  This proc puts a reply count on every post.  Mostly handy for search displays
DECLARE myCursor Cursor FOR select GUID from post 
open myCursor
declare 
@postId uniqueidentifier,
@count int


--BEGIN TRAN UpdateTransaction
fetch next from myCursor into @postId
While (@@FETCH_STATUS <> -1)
BEGIN
	IF (@@FETCH_STATUS <> -2)
	
	--print convert(varchar(50),@rootId ) +' ' + @threadPath

	select @count = count(*) from post where parent_guid = @postId
	
	--print convert(varchar(50),@threadId)
	
	update post set REPLY_COUNT=@count where guid=@postId

	fetch next from myCursor into @postId
END

--ROLLBACK 
close myCursor
DEALLOCATE myCursor


