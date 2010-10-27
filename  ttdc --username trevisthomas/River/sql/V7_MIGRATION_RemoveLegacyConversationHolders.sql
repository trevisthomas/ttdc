-- New for V7  Removes legacy posts from hierarchy
DECLARE myCursor Cursor FOR select POST_GUID from ASSOCIATION_POST_TAG where TAG_GUID = 'B024D33D-D25C-4042-9930-1B6A7A2FEAD3'
open myCursor
declare 
@rootId uniqueidentifier,
@postId uniqueidentifier


--BEGIN TRAN UpdateTransaction
fetch next from myCursor into @postId
While (@@FETCH_STATUS <> -1)
BEGIN
	IF (@@FETCH_STATUS <> -2)
	
		select @rootId = p.ROOT_GUID from POST p where GUID = @postId
		update POST set PARENT_GUID = @rootId where PARENT_GUID = @postId

	fetch next from myCursor into @postId
END


delete from ENTRY where POST_GUID in(select POST_GUID from ASSOCIATION_POST_TAG where TAG_GUID = 'B024D33D-D25C-4042-9930-1B6A7A2FEAD3')
delete from ASSOCIATION_POST_TAG where POST_GUID in(select POST_GUID from ASSOCIATION_POST_TAG where TAG_GUID = 'B024D33D-D25C-4042-9930-1B6A7A2FEAD3')
delete from POST where GUID not in (select post_guid from entry)
--ROLLBACK 
close myCursor
DEALLOCATE myCursor
