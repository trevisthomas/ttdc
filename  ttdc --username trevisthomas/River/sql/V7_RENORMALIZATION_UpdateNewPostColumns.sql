--RENORMALIZATIONPROJECT Effort for V7
DECLARE myCursor Cursor FOR select GUID, ROOT_GUID from post --where guid='0D193A06-64B4-4280-9A03-22A69E056009' or guid='4BDCB2AA-CA83-48D6-8B24-00007BBCD698'
open myCursor
declare 
@postId uniqueidentifier,
@rootId uniqueidentifier,
@count int,
@titleId uniqueidentifier,
@creatorId uniqueidentifier,
@avgRatingId uniqueidentifier,
@url varchar(500),
@pubYear smallint,
@entryId uniqueidentifier,
@editDate datetime



--BEGIN TRAN UpdateTransaction
fetch next from myCursor into @postId, @rootId
While (@@FETCH_STATUS <> -1)
BEGIN
	IF (@@FETCH_STATUS <> -2)

	select @url=null	
	select @pubYear=null
	select @avgRatingId=null

	select @titleId=t.guid from association_post_tag a inner join tag t on a.tag_guid=t.guid  where a.title=1 AND t.type='TOPIC' and a.post_guid=@rootId
	select @creatorId=a.creator_guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.type='CREATOR' and a.post_guid=@postId
	-- Average rating is already set by the java code which never even bothers to create the AVERAGE_RATING association.  It just adds the tag directly to the post
	--select @avgRatingId=t.guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.type='AVERAGE_RATING' and a.post_guid=@postId
	select @url=t.value from association_post_tag a inner join tag t on a.tag_guid=t.guid  where t.type='URL' and a.post_guid=@postId 
	select @pubYear=convert(smallint,t.value) from association_post_tag a inner join tag t on a.tag_guid=t.guid  where t.type='RELEASE_YEAR' and a.post_guid=@postId 
	select top 1  @entryId = guid, @editDate = date from Entry where post_guid=@postId order by date desc
	select @count = count(*) from post where parent_guid = @postId
	print convert(varchar(50),@postId ) 

	
	--print convert(varchar(50),@threadId)
	
	update post set REPLY_COUNT=@count, LATEST_ENTRY_GUID=@entryId, EDIT_DATE=@editDate, TAG_GUID_TITLE=@titleId, PERSON_GUID_CREATOR=@creatorId, TAG_GUID_AVG_RATING=@avgRatingId, URL=@url, PUBLISH_YEAR=@pubYear where guid=@postId

	fetch next from myCursor into @postId, @rootId
END

--ROLLBACK 
close myCursor
DEALLOCATE myCursor
