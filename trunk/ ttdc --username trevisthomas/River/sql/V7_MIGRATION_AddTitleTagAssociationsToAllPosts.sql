--New for V7.  This proc creates tag associations for thread titles for all posts. 
-- i think that v7's architechure will make this feasable now!
DECLARE myCursor Cursor FOR select GUID, ROOT_GUID, CREATOR_GUID, DATE from  post
open myCursor
declare 
@postId uniqueidentifier,
@rootId uniqueidentifier,
@creatorId uniqueidentifier,
@date datetime,
@tagId uniqueidentifier,
@newId uniqueidentifier 


--association_post_tag
--BEGIN TRAN UpdateTransaction
fetch next from myCursor into @postId, @rootId, @creatorId, @date
While (@@FETCH_STATUS <> -1)
BEGIN
	IF (@@FETCH_STATUS <> -2)
	
	--print convert(varchar(50),@rootId ) +' ' + @threadPath
	SELECT @newId = newid()	

	SELECT @tagId = tag_guid from association_post_tag where title = 1 AND post_guid=@rootId
	INSERT INTO association_post_tag  (guid,date, tag_guid, creator_guid, post_guid, title)
	VALUES (@newId,@date,@tagId,@creatorId,@postId,1)

	--print convert(varchar(50),@threadId)
	
	fetch next from myCursor into @postId, @rootId, @creatorId, @date
END

--ROLLBACK 
close myCursor
DEALLOCATE myCursor


