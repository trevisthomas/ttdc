package org.ttdc.persistence.objects;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FetchType;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;
import org.ttdc.biz.network.services.helpers.PostFormatter;
import org.ttdc.gwt.server.dao.MovieDao;
import org.ttdc.util.ShackTagger;



@Entity
@Table(name="ENTRY")

@NamedQueries({
	@NamedQuery(name="entry.getAll", query="FROM Entry entry"),
	@NamedQuery(name="entry.getByPostIds", query="FROM Entry entry WHERE entry.post.postId in (:postIds)"),
	/*
	@NamedQuery(name="TagSearchDao.BrowseTags", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE ass.tag.type IN ('TOPIC','CREATOR','DATE_MONTH','DATE_YEAR') " +
			"AND ass.tag.tagId NOT IN (:tagIds) " +
			"AND ass.post.postId IN ( SELECT ass.post.postId FROM AssociationPostTag ass " +
			 "WHERE ass.tag.tagId IN (:tagIds) GROUP BY ass.post.postId HAVING count(ass.post.postId) = :count)" +
			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
	*/
	@NamedQuery(name="TagSearchDao.BrowseTagsCustomTypes", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE ass.tag.type IN (:tagTypes) " +
			"AND ass.tag.tagId NOT IN (:unionTagIdList) " +
			"AND ass.post.postId IN ( SELECT ass.post.postId FROM AssociationPostTag ass " +
			 "WHERE ass.tag.tagId IN (:unionTagIdList) GROUP BY ass.post.postId HAVING count(ass.post.postId) = :count)" +
			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
			
	@NamedQuery(name="TagSearchDao.SearchTagsCustomTypesWithUnion", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE ass.tag.type IN (:tagTypes) " +
			"AND ass.tag.tagId NOT IN (:unionTagIdList) " +
			"AND ass.tag.value LIKE (:phrase) " +
			"AND ass.post.postId IN ( SELECT ass.post.postId FROM AssociationPostTag ass " +
			 "WHERE ass.tag.tagId IN (:unionTagIdList) GROUP BY ass.post.postId HAVING count(ass.post.postId) = :count)" +
			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
			
	@NamedQuery(name="TagSearchDao.SearchTagsCustomTypesWithExcludes", query="SELECT count(tag.tagId) as count, tag.tagId, tag.type, tag.value " +
			"FROM Tag tag " +
			"WHERE tag.type IN (:tagTypes) " +
			"AND tag.value LIKE (:phrase) " +
			"AND tag.tagId NOT IN (:excludeTagIdList)" +
			"GROUP BY tag.tagId, tag.type, tag.value"),
			
	@NamedQuery(name="TagSearchDao.SearchTagsTitlesOnly", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE "+
			"ass.title = '1' " +
			"AND ass.tag.value LIKE (:phrase) " +
			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
			
	@NamedQuery(name="TagSearchDao.BrowseTagsCustomTypesMostPopular", query="SELECT tag.mass as count, tag.tagId, tag.type, tag.value " +
			"FROM Tag tag " +
			"WHERE tag.type IN (:tagTypes) order by mass desc"),
			
			
			
	@NamedQuery(name="TagSearchDao.SearchTagsCustomTypes", query="SELECT count(tag.tagId) as count, tag.tagId, tag.type, tag.value " +
			"FROM Tag tag " +
			"WHERE tag.type IN (:tagTypes) " +
			"AND tag.value LIKE (:phrase) " +
			"GROUP BY tag.tagId, tag.type, tag.value"),
			
	@NamedQuery(name="TagSearchDao.SearchTagsCustomTypesExcludeTitles", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE ass.tag.value LIKE (:phrase) " +
			"AND ass.tag.type IN (:tagTypes) " +
			"AND ass.title <> '1' " + 
			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
			
			
	//With dates
			
	@NamedQuery(name="TagSearchDao.BrowseTagsCustomTypesInDateRange", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE ass.tag.type IN (:tagTypes) " +
			"AND ass.tag.tagId NOT IN (:unionTagIdList) " +
			"AND ass.post.postId IN ( SELECT ass.post.postId FROM AssociationPostTag ass " +
			"WHERE ass.tag.tagId IN (:unionTagIdList) " +
			"AND ass.date >= :startDate AND ass.date <= :endDate " +
			"GROUP BY ass.post.postId HAVING count(ass.post.postId) = :count)" +
			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
			
	@NamedQuery(name="TagSearchDao.SearchTagsCustomTypesWithUnionInDateRange", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE ass.tag.type IN (:tagTypes) " +
			"AND ass.tag.tagId NOT IN (:unionTagIdList) " +
			"AND ass.tag.value LIKE (:phrase) " +
			"AND ass.date >= :startDate AND ass.date <= :endDate " +
			"AND ass.post.postId " +
			"IN ( SELECT ass.post.postId FROM AssociationPostTag ass " +
			 "WHERE ass.tag.tagId IN (:unionTagIdList) GROUP BY ass.post.postId HAVING count(ass.post.postId) = :count)" +
			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
			
	@NamedQuery(name="TagSearchDao.SearchTagsCustomTypesWithExcludesInDateRange", query="SELECT count(tag.tagId) as count, tag.tagId, tag.type, tag.value " +
			"FROM Tag tag " +
			"WHERE tag.type IN (:tagTypes) " +
			"AND tag.date >= :startDate AND tag.date <= :endDate " +
			"AND tag.value LIKE (:phrase) " +
			"AND tag.tagId NOT IN (:excludeTagIdList)" +
			"GROUP BY tag.tagId, tag.type, tag.value"),
			
	@NamedQuery(name="TagSearchDao.SearchTagsTitlesOnlyInDateRange", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE "+
			"ass.title = '1' " +
			"AND ass.date >= :startDate AND ass.date <= :endDate " +
			"AND ass.tag.value LIKE (:phrase) " +
			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
			
	@NamedQuery(name="TagSearchDao.BrowseTagsCustomTypesMostPopularInDateRange", query="SELECT tag.mass as count, tag.tagId, tag.type, tag.value " +
			"FROM Tag tag " +
			"WHERE tag.type IN (:tagTypes) " +
			"AND tag.date >= :startDate AND tag.date <= :endDate " +
			"order by mass desc"),
			
			
			
	@NamedQuery(name="TagSearchDao.SearchTagsCustomTypesInDateRange", query="SELECT count(tag.tagId) as count, tag.tagId, tag.type, tag.value " +
			"FROM Tag tag " +
			"WHERE tag.type IN (:tagTypes) " +
			"AND tag.value LIKE (:phrase) " +
			"AND tag.date >= :startDate AND tag.date <= :endDate " +
			"GROUP BY tag.tagId, tag.type, tag.value"),
			
	@NamedQuery(name="TagSearchDao.SearchTagsCustomTypesExcludeTitlesInDateRange", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE ass.tag.value LIKE (:phrase) " +
			"AND ass.date >= :startDate AND ass.date <= :endDate " +
			"AND ass.tag.type IN (:tagTypes) " +
			"AND ass.title <> '1' " + 
			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),			
			
	//
			
	//START PostSearchDao
	
	@NamedQuery(name="PostSearchDao.SearchTagsTitlesOnly", query="SELECT ass.post " +
			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE "+
			"ass.title = '1' " +
			"AND ass.tag.value LIKE (:phrase) " +
			"ORDER BY ass.post.mass desc, ass.post.replyCount desc") ,					
			
			
	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsByDate", query="SELECT post FROM Post post WHERE post.postId IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass " +
			"INNER JOIN ass.post " +
			"WHERE ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.post.postId	" +
			"HAVING count(ass.post.postId) = :count) " +
			"ORDER BY date DESC"),
			
	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsByPopularity", query="SELECT post FROM Post post WHERE post.postId IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass " +
			"INNER JOIN ass.post " +
			"WHERE ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.post.postId	" +
			"HAVING count(ass.post.postId) = :count) " +
			"ORDER BY mass desc, reply_count desc"),
			

	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsCount", query="SELECT post.postId FROM Post post WHERE post.postId IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass " +
			"INNER JOIN ass.post " +
			"WHERE ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.post.postId	" +
			"HAVING count(ass.post.postId) = :count) "),
			
	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsByDateForConversations", query="SELECT post FROM Post post WHERE post.postId IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass " +
			"INNER JOIN ass.post " +
			"WHERE ass.post.postId = ass.post.thread.postId " +
			"AND ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.post.postId	" +
			"HAVING count(ass.post.postId) = :count) " +
			"ORDER BY date DESC"),
			
	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsByPopularityForConversations", query="SELECT post FROM Post post WHERE post.postId IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass " +
			"INNER JOIN ass.post " +
			"WHERE ass.post.postId = ass.post.thread.postId " +
			"AND ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.post.postId	" +
			"HAVING count(ass.post.postId) = :count) " +
			"ORDER BY mass desc, reply_count desc"),
			

	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsCountForConversations", query="SELECT post.postId FROM Post post WHERE post.postId IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass " +
			"INNER JOIN ass.post " +
			"WHERE ass.post.postId = ass.post.thread.postId " +
			"AND ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.post.postId	" +
			"HAVING count(ass.post.postId) = :count) "),			
			
	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsByDateWithExclude", query="SELECT post FROM Post post WHERE post.postId IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass " +
			"INNER JOIN ass.post " +
			"WHERE ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.post.postId	" +
			"HAVING count(ass.post.postId) = :count) " +
			"AND post.postId NOT IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:notTagIds)) "+
			"ORDER BY date DESC"),
			
	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsByPopularityWithExclude", query="SELECT post FROM Post post WHERE post.postId IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass " +
			"INNER JOIN ass.post " +
			"WHERE ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.post.postId	" +
			"HAVING count(ass.post.postId) = :count) " +
			"AND post.postId NOT IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:notTagIds)) "+
			"ORDER BY mass desc, reply_count desc"),
	
	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsCountWithExclude", query="SELECT post.postId FROM Post post WHERE post.postId IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass " +
			"INNER JOIN ass.post " +
			"WHERE ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.post.postId	" +
			"HAVING count(ass.post.postId) = :count) " +
			"AND post.postId NOT IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:notTagIds)) "),
			
	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsByDateWithExcludeForConversations", query="SELECT post FROM Post post WHERE post.postId IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass " +
			"INNER JOIN ass.post " +
			"WHERE ass.post.postId = ass.post.thread.postId " +
			"AND ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.post.postId	" +
			"HAVING count(ass.post.postId) = :count) " +
			"AND post.postId NOT IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:notTagIds)) "+
			"ORDER BY date DESC"),
			
	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsByPopularityWithExcludeForConversations", query="SELECT post FROM Post post WHERE post.postId IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass " +
			"INNER JOIN ass.post " +
			"WHERE ass.post.postId = ass.post.thread.postId " +
			"AND ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.post.postId	" +
			"HAVING count(ass.post.postId) = :count) " +
			"AND post.postId NOT IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:notTagIds)) "+
			"ORDER BY mass desc, reply_count desc"),
	
	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsCountWithExcludeForConversations", query="SELECT post.postId FROM Post post WHERE post.postId IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass " +
			"INNER JOIN ass.post " +
			"WHERE ass.post.postId = ass.post.thread.postId " +
			"AND ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.post.postId	" +
			"HAVING count(ass.post.postId) = :count) " +
			"AND post.postId NOT IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:notTagIds)) "),		
			
     ////// END PostSearchDao
			
	@NamedQuery(name="LatestPostsFlatDao.Flat", query="" +
			"SELECT post FROM Post post WHERE post.postId NOT IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds))" +
			"ORDER BY date DESC"),
			
	@NamedQuery(name="LatestPostsFlatDao.FlatNoFilters", query="SELECT post FROM Post post ORDER BY date DESC"),
	
	@NamedQuery(name="TopicDao.Starters", query="" +
			"SELECT post FROM Post post WHERE post.postId NOT IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds)) AND " +
			"post.parent.postId=:rootId "+
			"ORDER BY date DESC"),		
	
	@NamedQuery(name="TopicDao.StartersNoFilter", query="" +
			"SELECT post FROM Post post WHERE post.parent.postId=:rootId "+
			"ORDER BY date DESC"),	
			
	@NamedQuery(name="TopicDao.Replies", query="" +
			"SELECT post FROM Post post WHERE post.postId NOT IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds)) AND " +
			"post.thread.postId=:postId AND post.postId IS NOT :postId "+
			"ORDER BY date DESC"),		
	
	@NamedQuery(name="TopicDao.RepliesNoFilter", query="" +
			"SELECT post FROM Post post WHERE post.thread.postId=:postId AND post.postId IS NOT :postId "+
			"ORDER BY date DESC"),
			
	//TODO: trevis, i dont see why the and part of the inner query is necessary? WTF?		
	@NamedQuery(name="TopicDao.Flat", query="" +
			"SELECT post FROM Post post WHERE post.root.postId=:rootId AND post.parent.postId IS NOT NULL AND" +
			" post.postId NOT IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds)) AND " +
			"post.root.postId=:rootId AND post.postId IS NOT :rootId "+
			"ORDER BY date DESC"),		
	
	@NamedQuery(name="TopicDao.FlatNoFilter", query="" +
			"SELECT post FROM Post post WHERE post.root.postId=:rootId AND post.parent.postId IS NOT NULL "+
			"ORDER BY date DESC"),		
			
	@NamedQuery(name="TopicDao.Hierarchy", query="" +
			"SELECT post FROM Post post WHERE post.root.postId=:rootId AND" +
			" post.postId NOT IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds)) " +
			"ORDER BY path"),		
	
	@NamedQuery(name="TopicDao.HierarchyNoFilter", query="" +
			"SELECT post FROM Post post WHERE post.root.postId=:rootId "+
			"ORDER BY path"),		
			
	
	@NamedQuery(name="ThreadDao.StartersByReplyDate", query="" +
			"SELECT post FROM Post post WHERE post.root.postId=:postId AND " +
			"post.thread.postId = post.postId AND post.postId NOT IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds))"+
			"ORDER BY threadReplyDate DESC"),			
	
	@NamedQuery(name="ThreadDao.StartersNoFilterByReplyDate", query="" +
			"SELECT post FROM Post post WHERE post.root.postId=:postId AND " +
			"post.thread.postId = post.postId "+
			"ORDER BY threadReplyDate DESC"),
			
	@NamedQuery(name="ThreadDao.StartersByCreateDate", query="" +
			"SELECT post FROM Post post WHERE post.root.postId=:postId AND " +
			"post.thread.postId = post.postId AND post.postId NOT IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds))"+
			"ORDER BY threadReplyDate DESC"),			
	
	@NamedQuery(name="ThreadDao.StartersNoFilterByCreateDate", query="" +
			"SELECT post FROM Post post WHERE post.root.postId=:postId AND " +
			"post.thread.postId = post.postId "+
			"ORDER BY threadReplyDate DESC"),			
			
	//
	@NamedQuery(name="ThreadDao.RepliesInThreads", query="" +
			"SELECT post FROM Post post WHERE post.thread.postId IN(:postIds) " +
			"AND postId <> post.thread.postId " +
			"AND post.postId NOT IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds)) "+
			"ORDER BY path"),		
			
	@NamedQuery(name="ThreadDao.RepliesInThreadsNoFilter", query="" +
			"SELECT post FROM Post post WHERE post.thread.postId IN(:postIds) " +
			"AND postId <> post.thread.postId " +
			"ORDER BY path"),			

	//Remember! TopicDao.Thread sorts the posts backwards to give you the bottom of the list. Remember to reverse the results
	@NamedQuery(name="ThreadDao.Thread", query="" +
			"SELECT post FROM Post post WHERE post.thread.postId=:postId " +
			"AND postId <> post.thread.postId " +
			"AND post.postId NOT IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds)) "+
			"ORDER BY path desc"),		
	//Remember! TopicDao.Thread sorts the posts backwards to give you the bottom of the list. Remember to reverse the results
	@NamedQuery(name="ThreadDao.ThreadNoFilter", query="" +
			"SELECT post FROM Post post WHERE " +
			"postId <> post.thread.postId AND " +
			"post.thread.postId=:postId "+
			"ORDER BY path desc"),			
			
	@NamedQuery(name="LatestPostsDao.NestedNoFilter", query="" +
		"SELECT post FROM Post post WHERE post.threadReplyDate IS NOT NULL " +
		"ORDER BY post.threadReplyDate DESC"),		
		
	@NamedQuery(name="LatestPostsDao.NestedNoFilterCount", query="" +
			"SELECT post.postId FROM Post post WHERE post.threadReplyDate IS NOT NULL " +
			"ORDER BY post.threadReplyDate DESC"),		
		
	@NamedQuery(name="LatestPostsDao.Nested", query="" +
		"SELECT post FROM Post post WHERE post.threadReplyDate IS NOT NULL " +
		"AND post.postId NOT IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds)) "+
		"ORDER BY post.threadReplyDate DESC"),
		
	@NamedQuery(name="LatestPostsDao.NestedCount", query="" +
			"SELECT post.postId FROM Post post WHERE post.threadReplyDate IS NOT NULL " +
			"AND post.postId NOT IN (" +
				"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds)) "+
			"ORDER BY post.threadReplyDate DESC"),	

			
//	@NamedQuery(name="LatestPostsDao.RepliesInThreads", query="" +
//		"SELECT post FROM Post post WHERE post.thread.postId IN(:postIds) " +
//		"AND postId <> post.thread.postId " +
//		"AND post.postId NOT IN (" +
//		"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds)) "+
//		"ORDER BY date DESC"),		
//			
//	@NamedQuery(name="LatestPostsDao.RepliesInThreadsNoFilter", query="" +
//		"SELECT post FROM Post post WHERE post.thread.postId IN(:postIds) " +
//		"AND postId <> post.thread.postId " +
//		"ORDER BY date DESC"),
			
	//Front page hierarchy experiment
	@NamedQuery(name="LatestPostsDao.RepliesInThreads", query="" +
			"SELECT post FROM Post post WHERE post.thread.postId IN(:postIds) " +
			"AND postId <> post.thread.postId " +
			"AND post.postId NOT IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds)) "+
			"ORDER BY path"),		
				
	@NamedQuery(name="LatestPostsDao.RepliesInThreadsNoFilter", query="" +
		"SELECT post FROM Post post WHERE post.thread.postId IN(:postIds) " +
		"AND postId <> post.thread.postId " +
		"ORDER BY path"),		

	@NamedQuery(name="LatestPostsDao.Flat", query="" +
			"SELECT post FROM Post post " +
			"WHERE post.postId NOT IN (" +
				"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds)) "+
			"ORDER BY post.date DESC"),
		
	@NamedQuery(name="LatestPostsDao.FlatCount", query="" +
			"SELECT post.postId FROM Post post " +
			"WHERE post.postId NOT IN (" +
				"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds)) "+
			"ORDER BY post.date DESC"),	
			
	@NamedQuery(name="LatestPostsDao.FlatNoFilter", query="" +
			"SELECT post FROM Post post ORDER BY post.date DESC"),
		
	@NamedQuery(name="LatestPostsDao.FlatNoFilterCount", query="" +
			"SELECT post.postId FROM Post post ORDER BY post.date DESC"),		

	@NamedQuery(name="LatestPostsDao.Threads", query="" +
			"SELECT post FROM Post post " +
			"WHERE post.parent.postId IS NULL AND post.postId NOT IN (" +
				"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds)) "+
			"ORDER BY post.date DESC"),
		
	@NamedQuery(name="LatestPostsDao.ThreadsCount", query="" +
			"SELECT post.postId FROM Post post " +
			"WHERE post.parent.postId IS NULL AND post.postId NOT IN (" +
				"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds)) "+
			"ORDER BY post.date DESC"),	
			
	@NamedQuery(name="LatestPostsDao.ThreadsNoFilter", query="" +
			"SELECT post FROM Post post WHERE post.parent.postId IS NULL ORDER BY post.date DESC"),
		
	@NamedQuery(name="LatestPostsDao.ThreadsNoFilterCount", query="" +
			"SELECT post.postId FROM Post post WHERE post.parent.postId IS NULL ORDER BY post.date DESC"),				
			
			
	@NamedQuery(name="LatestPostsDao.Conversations", query="" +
			"SELECT post FROM Post post " +
			"WHERE post.thread.postId = post.postId AND post.postId NOT IN (" +
				"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds)) "+
			"ORDER BY post.date DESC"),
		
	@NamedQuery(name="LatestPostsDao.ConversationsCount", query="" +
			"SELECT post.postId FROM Post post " +
			"WHERE post.thread.postId = post.postId AND post.postId NOT IN (" +
				"SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:tagIds)) "+
			"ORDER BY post.date DESC"),	
			
	@NamedQuery(name="LatestPostsDao.ConversationsNoFilter", query="" +
			"SELECT post FROM Post post WHERE post.thread.postId = post.postId ORDER BY post.date DESC"),
		
	@NamedQuery(name="LatestPostsDao.ConversationsNoFilterCount", query="" +
			"SELECT post.postId FROM Post post WHERE post.thread.postId = post.postId ORDER BY post.date DESC"),				
	
		
		
			/*
	@NamedQuery(name="postId.moviesByTitle", query="SELECT ass.post.postId FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE ass.tag.type='SORT_TITLE' AND ass.post.postId IN (SELECT ass.post.postId FROM AssociationPostTag ass2 WHERE ass2.tag.type='MOVIE')" +
			"ORDER BY ass.tag.value"),
			
	@NamedQuery(name="postId.moviesByTitleReverse", query="SELECT ass.post.postId FROM AssociationPostTag ass INNER JOIN ass.tag " +
					"WHERE ass.tag.type='SORT_TITLE' AND ass.post.postId IN (SELECT ass.post.postId FROM AssociationPostTag ass2 WHERE ass2.tag.type='MOVIE')" +
					"ORDER BY ass.tag.value DESC"),			
			
	@NamedQuery(name="postId.moviesByYear", query="SELECT p.postId, ass.tag.value,ass3.tag.value,ass2.tag.type FROM Post p, AssociationPostTag ass INNER JOIN ass.tag, AssociationPostTag ass2 INNER JOIN ass2.tag, AssociationPostTag ass3 INNER JOIN ass3.tag " +
					"WHERE ass.tag.type='RELEASE_YEAR' AND ass2.tag.type='MOVIE' AND ass3.tag.type='SORT_TITLE' AND p.postId = ass.post.postId AND p.postId=ass2.post.postId AND p.postId = ass3.post.postId " +
					"ORDER BY ass.tag.value DESC, ass3.tag.value"),
			
	@NamedQuery(name="postId.moviesByYearReverse", query="SELECT p.postId, ass.tag.value,ass3.tag.value,ass2.tag.type FROM Post p, AssociationPostTag ass INNER JOIN ass.tag, AssociationPostTag ass2 INNER JOIN ass2.tag, AssociationPostTag ass3 INNER JOIN ass3.tag " +
					"WHERE ass.tag.type='RELEASE_YEAR' AND ass2.tag.type='MOVIE' AND ass3.tag.type='SORT_TITLE' AND p.postId = ass.post.postId AND p.postId=ass2.post.postId AND p.postId = ass3.post.postId " +
					"ORDER BY ass.tag.value, ass3.tag.value"),		
			
	@NamedQuery(name="postId.moviesForUser", query="SELECT post.postId FROM Post post WHERE " +
			"post.postId IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.type='MOVIE') AND " +
			"post.postId IN (SELECT ass2.post.postId FROM AssociationPostTag ass2 WHERE ass2.tag.type='RATING' AND ass2.creator.personId=:personId)"),
	
	@NamedQuery(name="postId.moviesByRatingForPerson", query="SELECT p.postId FROM Post p, AssociationPostTag ass INNER JOIN ass.tag, AssociationPostTag ass2 INNER JOIN ass2.tag, AssociationPostTag ass3 INNER JOIN ass3.tag " +
					"WHERE ass.creator.personId=:personId AND ass.tag.type='RATING' AND ass2.tag.type='MOVIE' AND ass3.tag.type='SORT_TITLE' AND p.postId = ass.post.postId AND p.postId=ass2.post.postId AND p.postId = ass3.post.postId " +
					"ORDER BY ass.tag.value DESC, ass3.tag.value"),		
					
	@NamedQuery(name="postId.moviesByRatingForPersonReverse", query="SELECT p.postId FROM Post p, AssociationPostTag ass INNER JOIN ass.tag, AssociationPostTag ass2 INNER JOIN ass2.tag, AssociationPostTag ass3 INNER JOIN ass3.tag " +
							"WHERE ass.creator.personId=:personId AND ass.tag.type='RATING' AND ass2.tag.type='MOVIE' AND ass3.tag.type='SORT_TITLE' AND p.postId = ass.post.postId AND p.postId=ass2.post.postId AND p.postId = ass3.post.postId " +
							"ORDER BY ass.tag.value, ass3.tag.value")
							*/					

	

	@NamedQuery(name="MovieDao.peopleWithMovieRatings", query="SELECT distinct ass.creator.personId, count(ass.creator.personId) " +
			"FROM Post p, AssociationPostTag ass INNER JOIN ass.tag, " +
			"AssociationPostTag ass2 INNER JOIN ass2.tag " +
			"WHERE ass.tag.type='RATING' AND ass2.tag.type='MOVIE' " +
			"AND p.postId = ass.post.postId AND p.postId=ass2.post.postId GROUP BY ass.creator.personId"),
			
	@NamedQuery(name="MovieDao.moviesSortedByTitle", query="SELECT p FROM Post p, " +
			"AssociationPostTag ass2 INNER JOIN ass2.tag, AssociationPostTag ass3 INNER JOIN ass3.tag " +
			"WHERE ass2.tag.type='MOVIE' AND ass3.tag.type='SORT_TITLE' " +
			"AND p.postId=ass2.post.postId AND p.postId = ass3.post.postId " +
			"ORDER BY ass3.tag.value"),
			
	@NamedQuery(name="MovieDao.moviesSortedByTitleDesc", query="SELECT p FROM Post p, " +
			"AssociationPostTag ass2 INNER JOIN ass2.tag, AssociationPostTag ass3 INNER JOIN ass3.tag " +
			"WHERE ass2.tag.type='MOVIE' AND ass3.tag.type='SORT_TITLE' " +
			"AND p.postId=ass2.post.postId AND p.postId = ass3.post.postId " +
			"ORDER BY ass3.tag.value DESC"),		
			

	@NamedQuery(name="MovieDao.moviesSortedByTitleForPerson", query="SELECT p FROM Post p, AssociationPostTag ass INNER JOIN ass.tag, " +
			"AssociationPostTag ass2 INNER JOIN ass2.tag, AssociationPostTag ass3 INNER JOIN ass3.tag " +
			"WHERE ass.tag.type='RATING' AND ass2.tag.type='MOVIE' AND ass3.tag.type='SORT_TITLE' " +
			"AND p.postId = ass.post.postId AND p.postId=ass2.post.postId AND p.postId = ass3.post.postId " +
			"AND ass.creator.personId = :guid " +
			"ORDER BY ass3.tag.value"),
			
	@NamedQuery(name="MovieDao.moviesSortedByTitleForPersonDesc", query="SELECT p FROM Post p, AssociationPostTag ass INNER JOIN ass.tag, " +
			"AssociationPostTag ass2 INNER JOIN ass2.tag, AssociationPostTag ass3 INNER JOIN ass3.tag " +
			"WHERE ass.tag.type='RATING' AND ass2.tag.type='MOVIE' AND ass3.tag.type='SORT_TITLE' " +
			"AND p.postId = ass.post.postId AND p.postId=ass2.post.postId AND p.postId = ass3.post.postId " +
			"AND ass.creator.personId = :guid " +
			"ORDER BY ass3.tag.value DESC"),		
			
	@NamedQuery(name="MovieDao.moviesSortedByAverageRating", query="SELECT p FROM Post p, AssociationPostTag ass INNER JOIN ass.tag, " +
			"AssociationPostTag ass2 INNER JOIN ass2.tag, AssociationPostTag ass3 INNER JOIN ass3.tag " +
			"WHERE ass.tag.type='AVERAGE_RATING' AND ass2.tag.type='MOVIE' AND ass3.tag.type='SORT_TITLE' " +
			"AND p.postId = ass.post.postId AND p.postId=ass2.post.postId AND p.postId = ass3.post.postId " +
			"ORDER BY ass.tag.value, ass3.tag.value"),
			
	@NamedQuery(name="MovieDao.moviesSortedByAverageRatingDesc", query="SELECT p FROM Post p, AssociationPostTag ass INNER JOIN ass.tag, " +
			"AssociationPostTag ass2 INNER JOIN ass2.tag, AssociationPostTag ass3 INNER JOIN ass3.tag " +
			"WHERE ass.tag.type='AVERAGE_RATING' AND ass2.tag.type='MOVIE' AND ass3.tag.type='SORT_TITLE' " +
			"AND p.postId = ass.post.postId AND p.postId=ass2.post.postId AND p.postId = ass3.post.postId " +
			"ORDER BY ass.tag.value desc, ass3.tag.value"),		
			

	@NamedQuery(name="MovieDao.moviesSortedByRatingForPerson", query="SELECT p FROM Post p, AssociationPostTag ass INNER JOIN ass.tag, " +
			"AssociationPostTag ass2 INNER JOIN ass2.tag, AssociationPostTag ass3 INNER JOIN ass3.tag " +
			"WHERE ass.tag.type='RATING' AND ass2.tag.type='MOVIE' AND ass3.tag.type='SORT_TITLE' " +
			"AND p.postId = ass.post.postId AND p.postId=ass2.post.postId AND p.postId = ass3.post.postId " +
			"AND ass.creator.personId = :guid " +
			"ORDER BY ass.tag.value, ass3.tag.value"),
			
	@NamedQuery(name="MovieDao.moviesSortedByRatingForPersonDesc", query="SELECT p FROM Post p, AssociationPostTag ass INNER JOIN ass.tag, " +
			"AssociationPostTag ass2 INNER JOIN ass2.tag, AssociationPostTag ass3 INNER JOIN ass3.tag " +
			"WHERE ass.tag.type='RATING' AND ass2.tag.type='MOVIE' AND ass3.tag.type='SORT_TITLE' " +
			"AND p.postId = ass.post.postId AND p.postId=ass2.post.postId AND p.postId = ass3.post.postId " +
			"AND ass.creator.personId = :guid " +
			"ORDER BY ass.tag.value desc, ass3.tag.value"),
			
	@NamedQuery(name="MovieDao.moviesSortedByYear", query="SELECT p FROM Post p, AssociationPostTag ass INNER JOIN ass.tag, " +
			"AssociationPostTag ass2 INNER JOIN ass2.tag, AssociationPostTag ass3 INNER JOIN ass3.tag " +
			"WHERE ass.tag.type='RELEASE_YEAR' AND ass2.tag.type='MOVIE' AND ass3.tag.type='SORT_TITLE' " +
			"AND p.postId = ass.post.postId AND p.postId=ass2.post.postId AND p.postId = ass3.post.postId " +
			"ORDER BY ass.tag.value, ass3.tag.value"),
			
	@NamedQuery(name="MovieDao.moviesSortedByYearDesc", query="SELECT p FROM Post p, AssociationPostTag ass INNER JOIN ass.tag, " +
			"AssociationPostTag ass2 INNER JOIN ass2.tag, AssociationPostTag ass3 INNER JOIN ass3.tag " +
			"WHERE ass.tag.type='RELEASE_YEAR' AND ass2.tag.type='MOVIE' AND ass3.tag.type='SORT_TITLE' " +
			"AND p.postId = ass.post.postId AND p.postId=ass2.post.postId AND p.postId = ass3.post.postId " +
			"ORDER BY ass.tag.value desc, ass3.tag.value"),		
			
	@NamedQuery(name="MovieDao.moviesSortedByYearForPerson", query="SELECT p FROM Post p, AssociationPostTag ass INNER JOIN ass.tag, " +
			"AssociationPostTag ass2 INNER JOIN ass2.tag, AssociationPostTag ass3 INNER JOIN ass3.tag, AssociationPostTag ass4 INNER JOIN ass4.tag " +
			"WHERE ass.tag.type='RELEASE_YEAR' AND ass2.tag.type='RATING' AND ass3.tag.type='SORT_TITLE' AND ass4.tag.type='MOVIE' " +
			"AND p.postId = ass.post.postId AND p.postId=ass2.post.postId AND p.postId = ass3.post.postId AND p.postId = ass4.post.postId " +
			"AND ass2.creator.personId = :guid " +
			"ORDER BY ass.tag.value, ass3.tag.value"),
			
	@NamedQuery(name="MovieDao.moviesSortedByYearForPersonDesc", query="SELECT p FROM Post p, AssociationPostTag ass INNER JOIN ass.tag, " +
			"AssociationPostTag ass2 INNER JOIN ass2.tag, AssociationPostTag ass3 INNER JOIN ass3.tag, AssociationPostTag ass4 INNER JOIN ass4.tag " +
			"WHERE ass.tag.type='RELEASE_YEAR' AND ass2.tag.type='RATING' AND ass3.tag.type='SORT_TITLE' AND ass4.tag.type='MOVIE'" +
			"AND p.postId = ass.post.postId AND p.postId=ass2.post.postId AND p.postId = ass3.post.postId AND p.postId = ass4.post.postId " +
			"AND ass2.creator.personId = :guid " +
			"ORDER BY ass.tag.value desc, ass3.tag.value"),
			
	@NamedQuery(name="PersonDao.loadPersonList", query="SELECT person FROM Person person WHERE person.personId IN (:personIds) ORDER BY person.login")
					
			
			
})

@NamedNativeQueries({
//	@NamedNativeQuery(
//		name="CalendarDao.fetchHourly", query="" +
//				"select Year(p.date) as yr, Month(p.date) as mo, Day(p.date) as dd, DATEPART(hour,p.date) as hr, "+
//				"p.date, p.guid as postId, p.root_guid as rootId, titleTag.value as title, c.login, p.creator_guid, '' as summary "+
//				"from post p "+
//				"inner join association_post_tag as titleAss on titleAss.post_guid = p.guid AND titleAss.title='1' "+ 
//				"inner join tag as titleTag on titleTag.guid = titleAss.tag_guid "+
//				"inner join entry as e on e.guid = p.latest_entry_guid "+
//				"inner join person as c on c.guid = p.creator_guid "+
//				"where p.date between :startDate and :endDate order by p.date",
//				resultSetMapping="simplePostMapping"
//	),
//	@NamedNativeQuery(
//		name="CalendarDao.fetchHourlyWithSummary", query="" +
//				"select Year(p.date) as yr, Month(p.date) as mo, Day(p.date) as dd, DATEPART(hour,p.date) as hr, "+
//				"p.date, p.guid as postId, p.root_guid as rootId, titleTag.value as title, c.login, p.creator_guid, SUBSTRING(e.body, 1, 150) as summary "+
//				"from post p "+
//				"inner join association_post_tag as titleAss on titleAss.post_guid = p.root_guid AND titleAss.title='1' "+ 
//				"inner join tag as titleTag on titleTag.guid = titleAss.tag_guid "+
//				"inner join entry as e on e.guid = p.latest_entry_guid "+
//				"inner join person as c on c.guid = p.creator_guid "+
//				"where p.date between :startDate and :endDate order by p.date",
//				resultSetMapping="simplePostMapping"
//	),
	
	@NamedNativeQuery(
		name="CalendarDao.fetchHourly", query="" +
				"select p.date, p.guid as postId, p.root_guid as rootId, titleTag.value as title, c.login, c.guid as creator_guid, '' as summary "+
				"from post p "+
				"inner join association_post_tag as titleAss on titleAss.post_guid = p.guid AND titleAss.title='1' "+ 
				"inner join association_post_tag creatorAss on creatorAss.post_guid=p.guid "+
				"inner join tag creatorTag on creatorTag.guid=creatorAss.tag_guid "+ 
				"inner join tag as titleTag on titleTag.guid = titleAss.tag_guid "+
				"inner join entry as e on e.guid = p.latest_entry_guid "+
				"inner join person as c on c.guid = creatorTag.creator_guid "+
				"where p.date between :startDate and :endDate AND creatorTag.type='CREATOR' order by p.date",
				resultSetMapping="simplePostMapping"
	),
	@NamedNativeQuery(
		name="CalendarDao.fetchHourlyWithSummary", query="" +
				"select p.date, p.guid as postId, p.root_guid as rootId, titleTag.value as title, c.login, " +
				"c.guid as creator_guid, e.summary as summary "+
				"from post p "+
				"inner join association_post_tag as titleAss on titleAss.post_guid = p.guid AND titleAss.title='1' "+
				"inner join association_post_tag creatorAss on creatorAss.post_guid=p.guid "+
				"inner join tag creatorTag on creatorTag.guid=creatorAss.tag_guid "+ 
				"inner join tag as titleTag on titleTag.guid = titleAss.tag_guid "+
				"inner join entry as e on e.guid = p.latest_entry_guid "+
				"inner join person as c on c.guid = creatorTag.creator_guid "+
				"where p.date between :startDate and :endDate AND creatorTag.type='CREATOR' order by p.date",
				resultSetMapping="simplePostMapping"
	),
	@NamedNativeQuery(
		name="CalendarDao.fetchMonth", query=""+
			"select count(p.guid) ct, p.date"+
			", titleTag.value as title, p.root_guid as rootId, uuid() as uniqueId "+
			"from post p "+
			"inner join association_post_tag as titleAss on titleAss.post_guid = p.guid AND titleAss.title='1' "+ 
			"inner join tag as titleTag on titleTag.guid = titleAss.tag_guid "+
			"inner join entry as e on e.guid = p.latest_entry_guid "+
			"where Year(p.date) = :year AND Month(p.date) = :month  "+
			"group by Year(p.date), Month(p.date), Day(p.date), titleTag.value, p.root_guid "+
			"order by Year(p.date), Month(p.date), Day(p.date) ",
			resultSetMapping="threadSummaryMapping"
	),
	
	@NamedNativeQuery(name="CalendarDao.fetchYear", query=
		"select count(p.guid) ct, p.date "+
		", uuid() as uniqueId "+
		"from post p "+
		"where Year(p.date) = :year "+
		"group by Year(p.date), Month(p.date), Day(p.date) "+
		"order by Year(p.date), Month(p.date), Day(p.date)",
		resultSetMapping="daySummaryMapping"),
		
	/*
	 * This simpleMonth version was added late in the game as a way
	 * of getting a months worth of data for the front page calendar
	 *  
	 */
	@NamedNativeQuery(name="CalendarDao.fetchSimpleMonth", query=
		"select count(p.guid) ct, p.date, uuid() as uniqueId "+
		"from post p "+
		"where Year(p.date) = :year AND Month(p.date) = :month "+
		"group by Year(p.date), Month(p.date), Day(p.date) "+
		"order by Year(p.date), Month(p.date), Day(p.date)",
		resultSetMapping="daySummaryMapping")		
		
	
})


@SqlResultSetMappings({
	@SqlResultSetMapping(name="simplePostMapping", entities=
		@EntityResult(entityClass=org.ttdc.persistence.objects.SimplePostEntity.class, fields = {
//	        @FieldResult(name="year", column="yr"),
//	        @FieldResult(name="month", column="mo"),
//	        @FieldResult(name="day", column="dd"),
//	        @FieldResult(name="hour", column="hr"),
	        @FieldResult(name="date", column="date"),
	        @FieldResult(name="creatorLogin", column="login"),
	        @FieldResult(name="creatorId", column="creator_guid"),
	        @FieldResult(name="summary", column="summary"),
	        @FieldResult(name="title", column="title"),
	        @FieldResult(name="rootId", column="rootId"),
	        @FieldResult(name="postId", column="postId")
        
    })),
    @SqlResultSetMapping(name="threadSummaryMapping", entities=
    	@EntityResult(entityClass=org.ttdc.persistence.objects.ThreadSummaryEntity.class, fields = {
//            @FieldResult(name="year", column="yr"),
//            @FieldResult(name="month", column="mo"),
//            @FieldResult(name="day", column="dd"),
            @FieldResult(name="count", column="ct"),
            @FieldResult(name="rootId", column="rootId"),
            @FieldResult(name="title", column="title"),
            @FieldResult(name="date", column="date"),
            @FieldResult(name="uniqueId", column="uniqueId")
        })),    

    @SqlResultSetMapping(name="daySummaryMapping", entities=
    	@EntityResult(entityClass=org.ttdc.persistence.objects.DaySummaryEntity.class, fields = {
//            @FieldResult(name="year", column="yr"),
//            @FieldResult(name="month", column="mo"),
//            @FieldResult(name="day", column="dd"),
    		@FieldResult(name="date", column="date"),
            @FieldResult(name="count", column="ct"),
            @FieldResult(name="uniqueId", column="uniqueId")
        }))      
})



public class Entry implements HasGuid{
	private String entryId;
	private String body;
	private Post post; 
	private String summary;
	
	private Date date = new Date();
	
	@Override
	public String toString() {
		return post.getPostId() +": "+  ((getBody().length() <= 100) ? getBody() : getBody().substring(0, 100));
	}
	@Id @GeneratedValue( generator="system-uuid" )
	@GenericGenerator(name = "system-uuid", strategy = "guid")
	@Column(name="GUID")
	@DocumentId
	public String getEntryId() {
		return entryId;
	}
	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}
	@Transient
	public String getUniqueId() {
		return getEntryId();
	}
	
	
	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
	@JoinColumn(name="POST_GUID")
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		this.post = post;
		//post.setEntry(this);
	}
	@Field(index=Index.TOKENIZED, store=Store.NO)
	public String getBody() {
		return body;
	}
	
//	@Transient
//	public String getBodyFormatted(){
//		return PostFormatter.getInstance().format(body);
//	}
//	@Transient
//	public String getBodySummaryFormatted(){
//		return PostFormatter.getInstance().formatSummary(body);
//	}
//	@Transient
//	public String getBodyFormatted(String embedPrefix){
//		if(embedPrefix == null)
//			return PostFormatter.getInstance().format(body);
//		else{
//			//This is such a lame hack but... i needed a way to change the embeded video/image javascript
//			//to handle flat and hierarchy on the same page.
//			return ShackTagger.getInstance().replace(PostFormatter.getInstance().format(body), "tggle", "tggle"+embedPrefix);
//		}
//		
//	}
	
	public String getSummary() {
		return summary;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
}
