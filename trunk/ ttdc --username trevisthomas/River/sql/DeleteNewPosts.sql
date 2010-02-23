--delete posts newer than this date
delete from entry where post_guid in(select guid from post where date > '2009-09-16 07:00:00')
delete from association_post_tag where post_guid in(select guid from post where date > '2009-09-16 07:00:00')
delete from association_post_tag where date > '2009-09-16 07:00:00'
delete from post where date > '2009-09-16 07:00:00'  
delete from entry where date > '2009-09-16 07:00:00'  

-- tags

delete from tag where date > '2009-09-16 07:00:00'  


-- Check
select entry.*,post.* from post inner join entry on post.guid=entry.post_guid where post.date > '2009-09-16 07:00:00'  

select * from tag where date > '2009-09-16 07:00:00'  


