
--remove the title asses
delete from association_post_tag where title='1'

--remove the asses that are not ratings or topic tags
delete from association_post_tag where tag_guid in
(select distinct guid from tag where type not in ('RATING','TOPIC'))

-- remove unused tags
delete from tag where type in ('RATABLE',
'LEGACY_THREAD',
'DISPLAY',
'CREATOR',
'RELEASE_YEAR',
'SORT_TITLE',
'WEEK_OF_YEAR',
'DATE_MONTH',
'URL',
'DATE_DAY',
'DATE_YEAR',
'EARMARK',
'REVIEW',
'MOVIE')

--the only tag's i'm keeping.  (note, the avg rating asses are gone because the avg ratings are attached directly to the post)
--'RATING'
--'TOPIC'
--'AVERAGE_RATING'

