update post set meta_mask=0

select * from post where guid='0D193A06-64B4-4280-9A03-22A69E056009' or guid='4BDCB2AA-CA83-48D6-8B24-00007BBCD698'


select distinct type from tag

RATABLE
LEGACY_THREAD
DISPLAY
REVIEW
MOVIE

-- Selects the tags that are replaced by bitmasks
select distinct value,guid from tag where type not in ('WEEK_OF_YEAR','RELEASE_YEAR','DATE_DAY','DATE_YEAR','DATE_MONTH','TOPIC','SORT_TITLE','URL','AVERAGE_RATING','EARMARK','CREATOR','RATING')

-- tag values that become masks
DELETED=1
INF=2
Legacy Thread=4
LINK=8
MOVIE=16
NWS=32
PRIVATE=64
RATABLE=128
REVIEW=256

DELETED:	8DCD1926-C95A-4B84-960C-F22F2B38AE4B
INF:	79E55E63-0D09-407A-A357-E057403C1A98
Legacy Thread:	B024D33D-D25C-4042-9930-1B6A7A2FEAD3
LINK:	C06CCF5A-01BC-4236-A241-6FAA93FA3E67
MOVIE:	E9ECF7AF-6406-4BDE-A396-145CE256ABD2
NWS	42D4DA1D-F82E-45A5-9166-5A0A3AAC6002
PRIVATE:	9EB01159-4527-46E8-A7E1-D1C680938369
RATABLE:	64446667-B75E-439B-B523-C6CBE99454CD
REVIEW:	7CD560A1-192C-435A-AB9D-0278CB7A397C

select 32|16|4
select 8&4

--test for legacy
select * from post where meta_mask&4=4
select * from association_post_tag where tag_guid='B024D33D-D25C-4042-9930-1B6A7A2FEAD3'

select t.guid from association_post_tag a inner join tag t on a.tag_guid=t.guid where t.value='MOVIE' --and a.post_guid=@postId