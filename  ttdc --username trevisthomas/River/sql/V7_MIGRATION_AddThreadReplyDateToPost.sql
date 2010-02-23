--This proc adds the THREAD_REPLY_DATE to thread starter posts.  New for V7
DECLARE myCursor Cursor FOR select GUID from post where THREAD_GUID=GUID
open myCursor
declare 
@replyDate datetime,
@postId uniqueidentifier


--BEGIN TRAN UpdateTransaction
fetch next from myCursor into @postId
While (@@FETCH_STATUS <> -1)
BEGIN
	IF (@@FETCH_STATUS <> -2)
	
	--print convert(varchar(50),@rootId ) +' ' + @threadPath

	select top 1 @replyDate = date from Post where THREAD_GUID=@postId order by date desc

	--print convert(varchar(50),@threadId)
	
	update post set THREAD_REPLY_DATE=@replyDate where guid=@postId

	fetch next from myCursor into @postId
END

--ROLLBACK 
close myCursor
DEALLOCATE myCursor
