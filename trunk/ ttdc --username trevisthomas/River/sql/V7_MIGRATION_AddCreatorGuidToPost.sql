-- New for V7  Adds the creator id to a post
DECLARE myCursor Cursor FOR select GUID from post 
open myCursor
declare 
@creatorId uniqueidentifier,
@postId uniqueidentifier


--BEGIN TRAN UpdateTransaction
fetch next from myCursor into @postId
While (@@FETCH_STATUS <> -1)
BEGIN
	IF (@@FETCH_STATUS <> -2)
	
		select @creatorId = p.guid from tag inner join association_post_tag as ass on ass.tag_guid=tag.guid 
		inner join person p on p.guid=tag.creator_guid
		where tag.type='CREATOR' and ass.post_guid=@postId

		update post set PERSON_GUID_CREATOR=@creatorId where guid=@postId

	fetch next from myCursor into @postId
END

--ROLLBACK 
close myCursor
DEALLOCATE myCursor


