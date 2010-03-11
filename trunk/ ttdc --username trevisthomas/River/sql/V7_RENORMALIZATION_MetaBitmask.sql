--RENORMALIZATIONPROJECT Effort for V7 -- setting the meta field!
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

update post set META_MASK=META_MASK|@deleted where guid in (select post_guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='DELETED')
update post set META_MASK=META_MASK|@inf where guid in (select post_guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='INF')
update post set META_MASK=META_MASK|@legacy where guid in (select post_guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='Legacy Thread')
update post set META_MASK=META_MASK|@link where guid in (select post_guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='LINK')
update post set META_MASK=META_MASK|@movie where guid in (select post_guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='MOVIE')
update post set META_MASK=META_MASK|@nws where guid in (select post_guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='NWS')
update post set META_MASK=META_MASK|@private where guid in (select post_guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='PRIVATE')
update post set META_MASK=META_MASK|@ratable where guid in (select post_guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='RATABLE')
update post set META_MASK=META_MASK|@review where guid in (select post_guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='REVIEW')

