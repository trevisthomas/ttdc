--This query populates the thread titles in the root post and conversation starter posts.  (Created Feb 6, depracated Feb 11!!)

DECLARE myCursor Cursor FOR select t.value,a.post_guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where title=1
open myCursor
declare @rootId uniqueidentifier,
@threadPath varchar(100),
@threadId uniqueidentifier,
@postId uniqueidentifier,
@title varchar(255)


--BEGIN TRAN UpdateTransaction
fetch next from myCursor into @title, @postId
While (@@FETCH_STATUS <> -1)
BEGIN
	IF (@@FETCH_STATUS <> -2)
	
	--print convert(varchar(50),@rootId ) +' ' + @threadPath
	

	-- if you later decide that you dont want titles on conversation starters here is where you make that change.
	-- just remove the or parent_guid	
	update post set title=@title where guid=@postId or parent_guid=@postId 

	fetch next from myCursor into @title, @postId
END

--ROLLBACK 
close myCursor
DEALLOCATE myCursor


