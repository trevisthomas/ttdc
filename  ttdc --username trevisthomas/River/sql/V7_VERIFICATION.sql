-- tests to confirm stuff
select * from POST where TAG_GUID_AVG_RATING is not null
select * from POST where PERSON_GUID_CREATOR is not null
select * from POST where PERSON_GUID_CREATOR is null
select * from POST where PATH = '' -- roots
select * from POST where REPLY_COUNT <> 0
select * from POST where EDIT_DATE is not null
select * from POST where LATEST_ENTRY_GUID is not null
select * from POST where PUBLISH_YEAR is not null
select * from TAG where SORT_VALUE is not null
select * from TAG where SORT_VALUE = null

select * from POST where TAG_GUID_TITLE is null
select * from POST where TAG_GUID_TITLE is not null
