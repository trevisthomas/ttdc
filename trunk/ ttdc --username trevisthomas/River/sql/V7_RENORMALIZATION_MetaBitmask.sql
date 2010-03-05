--RENORMALIZATIONPROJECT Effort for V7 -- setting the meta field!
DECLARE myCursor Cursor FOR select GUID from post -- where guid='0D193A06-64B4-4280-9A03-22A69E056009' or guid='4BDCB2AA-CA83-48D6-8B24-00007BBCD698'
open myCursor
declare 
@postId uniqueidentifier,
@var uniqueidentifier,
@metamask binary(8),
@deleted bigint,
@inf bigint,
@legacy bigint,
@link bigint,
@movie bigint,
@nws bigint,
@private bigint,
@ratable bigint,
@review bigint


--BEGIN TRAN UpdateTransaction
select @deleted = 1
select @inf = 2
select @legacy = 4
select @link = 8
select @movie = 16
select @nws = 32
select @private = 64
select @ratable = 128
select @review = 256


fetch next from myCursor into @postId
While (@@FETCH_STATUS <> -1)
BEGIN
	IF (@@FETCH_STATUS <> -2)

	select @metamask=0
	select @var=null
	select @var=t.guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='DELETED' and a.post_guid=@postId
	IF(@var is not null)
	BEGIN
		select @metamask=@metamask|@deleted
	END
	
	select @var=null
	select @var=t.guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='INF' and a.post_guid=@postId
	IF(@var is not null)
	BEGIN
		select @metamask=@metamask|@inf
	END

	select @var=null
	select @var=t.guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='Legacy Thread' and a.post_guid=@postId
	IF(@var is not null)
	BEGIN
		select @metamask=@metamask|@legacy
	END

	select @var=null
	select @var=t.guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='LINK' and a.post_guid=@postId
	IF(@var is not null)
	BEGIN
		select @metamask=@metamask|@link
	END

	select @var=null
	select @var=t.guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='MOVIE' and a.post_guid=@postId
	IF(@var is not null)
	BEGIN
		select @metamask=@metamask|@movie
	END

	select @var=null
	select @var=t.guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='NWS' and a.post_guid=@postId
	IF(@var is not null)
	BEGIN
		select @metamask=@metamask|@nws
	END

	select @var=null
	select @var=t.guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='PRIVATE' and a.post_guid=@postId
	IF(@var is not null)
	BEGIN
		select @metamask=@metamask|@private
	END

	select @var=null
	select @var=t.guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='RATABLE' and a.post_guid=@postId
	IF(@var is not null)
	BEGIN
		select @metamask=@metamask|@ratable
	END
	
	select @var=null
	select @var=t.guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='REVIEW' and a.post_guid=@postId
	IF(@var is not null)
	BEGIN
		select @metamask=@metamask|@review
	END
	
	update post set META_MASK=@metamask where guid=@postId

	fetch next from myCursor into @postId
END

--ROLLBACK 
close myCursor
DEALLOCATE myCursor
