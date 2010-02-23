--This query populates the thread_guid's on posts which is a new thing for the Thread/ThreadSet concept being developed for v7

DECLARE myCursor Cursor FOR select GUID,ROOT_GUID, SUBSTRING(path, 1, 5) as ThreadPath from post where DATALENGTH(path) > 5 
open myCursor
declare @rootId uniqueidentifier,
@threadPath varchar(100),
@threadId uniqueidentifier,
@postId uniqueidentifier


--BEGIN TRAN UpdateTransaction
fetch next from myCursor into @postId, @rootId, @threadPath
While (@@FETCH_STATUS <> -1)
BEGIN
	IF (@@FETCH_STATUS <> -2)
	
	--print convert(varchar(50),@rootId ) +' ' + @threadPath

	select @threadId = guid from post where root_guid=@rootId and path=@threadPath

	--print convert(varchar(50),@threadId)
	
	update post set thread_guid=@threadId where guid=@postId

	fetch next from myCursor into @postId, @rootId, @threadPath
END

--ROLLBACK 
close myCursor
DEALLOCATE myCursor


update post set thread_guid=guid where parent_guid=root_guid