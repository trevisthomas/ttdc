--V7 This query sets the mass of tags.

DECLARE myCursor Cursor FOR select GUID from tag 
open myCursor
declare @total integer, @tagId uniqueidentifier

--BEGIN TRAN UpdateTransaction

fetch next from myCursor into @tagId
While (@@FETCH_STATUS <> -1)
BEGIN
	IF (@@FETCH_STATUS <> -2)
	
	--print convert(varchar(50),@postId)

	select @total = count(*) from association_post_tag where tag_guid = @tagId

	--print convert(varchar(50),@postId ) +' ' + @total
	
	--update association_post_tag set mass=@total where guid=@assId
	update tag set mass=@total where guid=@tagId

	fetch next from myCursor into @tagId
END

--ROLLBACK 
close myCursor
DEALLOCATE myCursor


