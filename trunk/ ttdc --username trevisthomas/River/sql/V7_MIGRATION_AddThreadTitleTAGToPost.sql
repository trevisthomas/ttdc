--This query populates the TITLE_TAG_GUID field in the root post and conversation starter posts.  

DECLARE myCursor Cursor FOR select a.tag_guid, a.post_guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where title=1
open myCursor
declare 
@postId uniqueidentifier,
@tagId uniqueidentifier


--BEGIN TRAN UpdateTransaction
fetch next from myCursor into @tagId, @postId
While (@@FETCH_STATUS <> -1)
BEGIN
	IF (@@FETCH_STATUS <> -2)
	
	--print convert(varchar(50),@rootId ) +' ' + @threadPath
	

	-- if you later decide that you dont want titles on conversation starters here is where you make that change.
	-- just remove the or parent_guid	
	-- update post set TITLE_TAG_GUID=@tagId where (guid=@postId or parent_guid=@postId) AND (guid=root_guid OR guid=thread_guid)
	update post set TITLE_TAG_GUID=@tagId where root_guid=@postId --Just put the title tag on every post.
	

	fetch next from myCursor into @tagId, @postId
END

--ROLLBACK 
close myCursor
DEALLOCATE myCursor


