--V7 Adding the thread mass to conversation/thread posts! I'm doing this initially for thread view but posts will need this for dispaly

DECLARE myCursor Cursor FOR select GUID from post where thread_guid=guid
open myCursor
declare @assId uniqueidentifier, @postId uniqueidentifier, @count integer

--BEGIN TRAN UpdateTransaction

fetch next from myCursor into @postId
While (@@FETCH_STATUS <> -1)
BEGIN
	IF (@@FETCH_STATUS <> -2)
	
	--print convert(varchar(50),@postId)
	
	select @count = count(*) from post where thread_guid=@postId AND thread_guid <> guid
	
	update post set mass=@count where guid=@postId

	fetch next from myCursor into @postId
END

--ROLLBACK 
close myCursor
DEALLOCATE myCursor



