select count(*) from post (nolock) where path <> ''
update post set path = ''

select * from post (nolock) where root_guid='BCAF5553-27BE-469A-90A5-57CEF155611D' order by path

select * from 

select * from post where path='' and parent_guid is not null

select * from post where root_guid='BCAF5553-27BE-469A-90A5-57CEF155611D' order by path

select count(guid) from post where root_guid='BCAF5553-27BE-469A-90A5-57CEF155611D' 


88BB9D2B-9C25-46F1-B234-28B1934D5AF3
select 
--select * from post where root_guid='' and path like '%'
select root_guid,path from post where guid='C8CD7CFD-4D17-4B30-94C4-ADF61572ED08'
select count(*) from post where root_guid='BCAF5553-27BE-469A-90A5-57CEF155611D' and path like '00032.00000.00001%'
select * from post where root_guid='BCAF5553-27BE-469A-90A5-57CEF155611D' and path like '00032.00000.00001%' order by path

