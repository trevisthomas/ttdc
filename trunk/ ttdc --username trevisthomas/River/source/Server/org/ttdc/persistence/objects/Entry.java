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
import org.ttdc.gwt.shared.util.PostFlag;
import org.ttdc.gwt.shared.util.PostFlagBitmasks;

//uuid() //MySql
//newid() //MSSql

@Entity
@Table(name="ENTRY")

@NamedQueries({
	@NamedQuery(name="entry.getAll", query="FROM Entry entry"),
	@NamedQuery(name="entry.getByPostIds", query="FROM Entry entry WHERE entry.post.postId in (:postIds)"),
	
//	@NamedQuery(name="TagSearchDao.BrowseTagsCustomTypes", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
//			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
//			"WHERE ass.tag.type IN (:tagTypes) " +
//			"AND ass.tag.tagId NOT IN (:unionTagIdList) " +
//			"AND ass.post.postId IN ( SELECT ass.post.postId FROM AssociationPostTag ass " +
//			 "WHERE ass.tag.tagId IN (:unionTagIdList) GROUP BY ass.post.postId HAVING count(ass.post.postId) = :count)" +
//			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
//			
//	@NamedQuery(name="TagSearchDao.SearchTagsCustomTypesWithUnion", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
//			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
//			"WHERE ass.tag.type IN (:tagTypes) " +
//			"AND ass.tag.tagId NOT IN (:unionTagIdList) " +
//			"AND ass.tag.value LIKE (:phrase) " +
//			"AND ass.post.postId IN ( SELECT ass.post.postId FROM AssociationPostTag ass " +
//			 "WHERE ass.tag.tagId IN (:unionTagIdList) GROUP BY ass.post.postId HAVING count(ass.post.postId) = :count)" +
//			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
//			
//	@NamedQuery(name="TagSearchDao.SearchTagsCustomTypesWithExcludes", query="SELECT count(tag.tagId) as count, tag.tagId, tag.type, tag.value " +
//			"FROM Tag tag " +
//			"WHERE tag.type IN (:tagTypes) " +
//			"AND tag.value LIKE (:phrase) " +
//			"AND tag.tagId NOT IN (:excludeTagIdList)" +
//			"GROUP BY tag.tagId, tag.type, tag.value"),
//			
//	@NamedQuery(name="TagSearchDao.SearchTagsTitlesOnly", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
//			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
//			"WHERE "+
//			"ass.title = '1' " +
//			"AND ass.tag.value LIKE (:phrase) " +
//			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
//			
//	@NamedQuery(name="TagSearchDao.BrowseTagsCustomTypesMostPopular", query="SELECT tag.mass as count, tag.tagId, tag.type, tag.value " +
//			"FROM Tag tag " +
//			"WHERE tag.type IN (:tagTypes) order by mass desc"),
//			
//			
//			
//	@NamedQuery(name="TagSearchDao.SearchTagsCustomTypes", query="SELECT count(tag.tagId) as count, tag.tagId, tag.type, tag.value " +
//			"FROM Tag tag " +
//			"WHERE tag.type IN (:tagTypes) " +
//			"AND tag.value LIKE (:phrase) " +
//			"GROUP BY tag.tagId, tag.type, tag.value"),
//			
//	@NamedQuery(name="TagSearchDao.SearchTagsCustomTypesExcludeTitles", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
//			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
//			"WHERE ass.tag.value LIKE (:phrase) " +
//			"AND ass.tag.type IN (:tagTypes) " +
//			"AND ass.title <> '1' " + 
//			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
//			
			
	//With dates
			
//	@NamedQuery(name="TagSearchDao.BrowseTagsCustomTypesInDateRange", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
//			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
//			"WHERE ass.tag.type IN (:tagTypes) " +
//			"AND ass.tag.tagId NOT IN (:unionTagIdList) " +
//			"AND ass.post.postId IN ( SELECT ass.post.postId FROM AssociationPostTag ass " +
//			"WHERE ass.tag.tagId IN (:unionTagIdList) " +
//			"AND ass.date >= :startDate AND ass.date <= :endDate " +
//			"GROUP BY ass.post.postId HAVING count(ass.post.postId) = :count)" +
//			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
//			
//	@NamedQuery(name="TagSearchDao.SearchTagsCustomTypesWithUnionInDateRange", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
//			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
//			"WHERE ass.tag.type IN (:tagTypes) " +
//			"AND ass.tag.tagId NOT IN (:unionTagIdList) " +
//			"AND ass.tag.value LIKE (:phrase) " +
//			"AND ass.date >= :startDate AND ass.date <= :endDate " +
//			"AND ass.post.postId " +
//			"IN ( SELECT ass.post.postId FROM AssociationPostTag ass " +
//			 "WHERE ass.tag.tagId IN (:unionTagIdList) GROUP BY ass.post.postId HAVING count(ass.post.postId) = :count)" +
//			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
//			
//	@NamedQuery(name="TagSearchDao.SearchTagsCustomTypesWithExcludesInDateRange", query="SELECT count(tag.tagId) as count, tag.tagId, tag.type, tag.value " +
//			"FROM Tag tag " +
//			"WHERE tag.type IN (:tagTypes) " +
//			"AND tag.date >= :startDate AND tag.date <= :endDate " +
//			"AND tag.value LIKE (:phrase) " +
//			"AND tag.tagId NOT IN (:excludeTagIdList)" +
//			"GROUP BY tag.tagId, tag.type, tag.value"),
//			
//	@NamedQuery(name="TagSearchDao.SearchTagsTitlesOnlyInDateRange", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
//			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
//			"WHERE "+
//			"ass.title = '1' " +
//			"AND ass.date >= :startDate AND ass.date <= :endDate " +
//			"AND ass.tag.value LIKE (:phrase) " +
//			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
//			
//	@NamedQuery(name="TagSearchDao.BrowseTagsCustomTypesMostPopularInDateRange", query="SELECT tag.mass as count, tag.tagId, tag.type, tag.value " +
//			"FROM Tag tag " +
//			"WHERE tag.type IN (:tagTypes) " +
//			"AND tag.date >= :startDate AND tag.date <= :endDate " +
//			"order by mass desc"),
//			
//			
//			
//	@NamedQuery(name="TagSearchDao.SearchTagsCustomTypesInDateRange", query="SELECT count(tag.tagId) as count, tag.tagId, tag.type, tag.value " +
//			"FROM Tag tag " +
//			"WHERE tag.type IN (:tagTypes) " +
//			"AND tag.value LIKE (:phrase) " +
//			"AND tag.date >= :startDate AND tag.date <= :endDate " +
//			"GROUP BY tag.tagId, tag.type, tag.value"),
//			
//	@NamedQuery(name="TagSearchDao.SearchTagsCustomTypesExcludeTitlesInDateRange", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
//			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
//			"WHERE ass.tag.value LIKE (:phrase) " +
//			"AND ass.date >= :startDate AND ass.date <= :endDate " +
//			"AND ass.tag.type IN (:tagTypes) " +
//			"AND ass.title <> '1' " + 
//			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),			
			
	//
			
	//START PostSearchDao
	
//	@NamedQuery(name="PostSearchDao.SearchTagsTitlesOnly", query="SELECT ass.post " +
//			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
//			"WHERE "+
//			"ass.title = '1' " +
//			"AND ass.tag.value LIKE (:phrase) " +
//			"ORDER BY ass.post.mass desc, ass.post.replyCount desc") ,					
//			
//			
//	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsByDate", query="SELECT post FROM Post post WHERE post.postId IN (" +
//			"SELECT ass.post.postId FROM AssociationPostTag ass " +
//			"INNER JOIN ass.post " +
//			"WHERE ass.tag.tagId IN (:tagIds)" +
//			"GROUP BY ass.post.postId	" +
//			"HAVING count(ass.post.postId) = :count) " +
//			"ORDER BY date DESC"),
//			
//	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsByPopularity", query="SELECT post FROM Post post WHERE post.postId IN (" +
//			"SELECT ass.post.postId FROM AssociationPostTag ass " +
//			"INNER JOIN ass.post " +
//			"WHERE ass.tag.tagId IN (:tagIds)" +
//			"GROUP BY ass.post.postId	" +
//			"HAVING count(ass.post.postId) = :count) " +
//			"ORDER BY mass desc, reply_count desc"),
//			
//
//	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsCount", query="SELECT post.postId FROM Post post WHERE post.postId IN (" +
//			"SELECT ass.post.postId FROM AssociationPostTag ass " +
//			"INNER JOIN ass.post " +
//			"WHERE ass.tag.tagId IN (:tagIds)" +
//			"GROUP BY ass.post.postId	" +
//			"HAVING count(ass.post.postId) = :count) "),
//			
//	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsByDateForConversations", query="SELECT post FROM Post post WHERE post.postId IN (" +
//			"SELECT ass.post.postId FROM AssociationPostTag ass " +
//			"INNER JOIN ass.post " +
//			"WHERE ass.post.postId = ass.post.thread.postId " +
//			"AND ass.tag.tagId IN (:tagIds)" +
//			"GROUP BY ass.post.postId	" +
//			"HAVING count(ass.post.postId) = :count) " +
//			"ORDER BY date DESC"),
//			
//	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsByPopularityForConversations", query="SELECT post FROM Post post WHERE post.postId IN (" +
//			"SELECT ass.post.postId FROM AssociationPostTag ass " +
//			"INNER JOIN ass.post " +
//			"WHERE ass.post.postId = ass.post.thread.postId " +
//			"AND ass.tag.tagId IN (:tagIds)" +
//			"GROUP BY ass.post.postId	" +
//			"HAVING count(ass.post.postId) = :count) " +
//			"ORDER BY mass desc, reply_count desc"),
//			
//
//	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsCountForConversations", query="SELECT post.postId FROM Post post WHERE post.postId IN (" +
//			"SELECT ass.post.postId FROM AssociationPostTag ass " +
//			"INNER JOIN ass.post " +
//			"WHERE ass.post.postId = ass.post.thread.postId " +
//			"AND ass.tag.tagId IN (:tagIds)" +
//			"GROUP BY ass.post.postId	" +
//			"HAVING count(ass.post.postId) = :count) "),			
//			
//	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsByDateWithExclude", query="SELECT post FROM Post post WHERE post.postId IN (" +
//			"SELECT ass.post.postId FROM AssociationPostTag ass " +
//			"INNER JOIN ass.post " +
//			"WHERE ass.tag.tagId IN (:tagIds)" +
//			"GROUP BY ass.post.postId	" +
//			"HAVING count(ass.post.postId) = :count) " +
//			"AND post.postId NOT IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:notTagIds)) "+
//			"ORDER BY date DESC"),
//			
//	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsByPopularityWithExclude", query="SELECT post FROM Post post WHERE post.postId IN (" +
//			"SELECT ass.post.postId FROM AssociationPostTag ass " +
//			"INNER JOIN ass.post " +
//			"WHERE ass.tag.tagId IN (:tagIds)" +
//			"GROUP BY ass.post.postId	" +
//			"HAVING count(ass.post.postId) = :count) " +
//			"AND post.postId NOT IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:notTagIds)) "+
//			"ORDER BY mass desc, reply_count desc"),
//	
//	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsCountWithExclude", query="SELECT post.postId FROM Post post WHERE post.postId IN (" +
//			"SELECT ass.post.postId FROM AssociationPostTag ass " +
//			"INNER JOIN ass.post " +
//			"WHERE ass.tag.tagId IN (:tagIds)" +
//			"GROUP BY ass.post.postId	" +
//			"HAVING count(ass.post.postId) = :count) " +
//			"AND post.postId NOT IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:notTagIds)) "),
//			
//	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsByDateWithExcludeForConversations", query="SELECT post FROM Post post WHERE post.postId IN (" +
//			"SELECT ass.post.postId FROM AssociationPostTag ass " +
//			"INNER JOIN ass.post " +
//			"WHERE ass.post.postId = ass.post.thread.postId " +
//			"AND ass.tag.tagId IN (:tagIds)" +
//			"GROUP BY ass.post.postId	" +
//			"HAVING count(ass.post.postId) = :count) " +
//			"AND post.postId NOT IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:notTagIds)) "+
//			"ORDER BY date DESC"),
//			
//	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsByPopularityWithExcludeForConversations", query="SELECT post FROM Post post WHERE post.postId IN (" +
//			"SELECT ass.post.postId FROM AssociationPostTag ass " +
//			"INNER JOIN ass.post " +
//			"WHERE ass.post.postId = ass.post.thread.postId " +
//			"AND ass.tag.tagId IN (:tagIds)" +
//			"GROUP BY ass.post.postId	" +
//			"HAVING count(ass.post.postId) = :count) " +
//			"AND post.postId NOT IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:notTagIds)) "+
//			"ORDER BY mass desc, reply_count desc"),
//	
//	@NamedQuery(name="PostSearchDao.BrowseTaggedPostsCountWithExcludeForConversations", query="SELECT post.postId FROM Post post WHERE post.postId IN (" +
//			"SELECT ass.post.postId FROM AssociationPostTag ass " +
//			"INNER JOIN ass.post " +
//			"WHERE ass.post.postId = ass.post.thread.postId " +
//			"AND ass.tag.tagId IN (:tagIds)" +
//			"GROUP BY ass.post.postId	" +
//			"HAVING count(ass.post.postId) = :count) " +
//			"AND post.postId NOT IN (SELECT ass.post.postId FROM AssociationPostTag ass WHERE ass.tag.tagId IN (:notTagIds)) "),		
			
     ////// END PostSearchDao
			
	@NamedQuery(name="TopicDao.Starters", query="" +
			"SELECT post FROM Post post " +
			"WHERE post.parent.postId=:rootId "+
			"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
			"ORDER BY post.date DESC"),
	
	@NamedQuery(name="TopicDao.StartersCount", query="" +
			"SELECT count(post.postId) FROM Post post " +
			"WHERE post.parent.postId=:rootId "+
			"AND bitwise_and( post.metaMask, :filterMask ) = 0 "),
			
	@NamedQuery(name="TopicDao.Replies", query="" +
			"SELECT post FROM Post post " +
			"WHERE post.thread.postId=:postId " +
			"AND post.postId IS NOT :postId " +
			"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
			"ORDER BY post.date DESC"),		
	
	@NamedQuery(name="TopicDao.Flat", query="" +
			"SELECT post FROM Post post " +
			"WHERE post.root.postId=:rootId " +
			"AND post.parent.postId IS NOT NULL "+
			"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
			"ORDER BY post.date DESC"),		
	
	@NamedQuery(name="TopicDao.FlatCount", query="" +
			"SELECT count(post.postId) FROM Post post " +
			"WHERE post.root.postId=:rootId " +
			"AND post.parent.postId IS NOT NULL "+
			"AND bitwise_and( post.metaMask, :filterMask ) = 0 "),		
			
	@NamedQuery(name="TopicDao.Hierarchy", query="" +
			"SELECT post FROM Post post WHERE post.root.postId=:rootId " +
			"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
			"ORDER BY post.path"),		
	
	@NamedQuery(name="TopicDao.HierarchyCount", query="" +
			"SELECT count(post.postId) FROM Post post WHERE post.root.postId=:rootId "+
			"AND bitwise_and( post.metaMask, :filterMask ) = 0 "),		
			
	//ThreadDao
	@NamedQuery(name="ThreadDao.StartersByReplyDate", query="" +
			"SELECT post FROM Post post WHERE post.root.postId=:postId " +
			"AND post.thread.postId = post.postId "+
			"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
			"ORDER BY post.threadReplyDate DESC"),			
	
	@NamedQuery(name="ThreadDao.StartersByCreateDate", query="" +
			"SELECT post FROM Post post WHERE post.root.postId=:postId " +
			"AND post.thread.postId = post.postId " +
			"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
			"ORDER BY post.threadReplyDate DESC"),		
			
	@NamedQuery(name="ThreadDao.StartersCount", query="" +
			"SELECT count(post.postId) FROM Post post WHERE post.root.postId=:postId " +
			"AND post.thread.postId = post.postId " +
			"AND bitwise_and( post.metaMask, :filterMask ) = 0 "),			
			
	@NamedQuery(name="ThreadDao.RepliesInThreads", query="" +
			"SELECT post FROM Post post WHERE post.thread.postId IN(:postIds) " +
			"AND postId <> post.thread.postId " +
			"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
			"ORDER BY post.path"),	
			
	@NamedQuery(name="ThreadDao.RepliesInThreadsByDate", query="" +
			"SELECT post FROM Post post WHERE post.thread.postId IN(:postIds) " +
			"AND postId <> post.thread.postId " +
			"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
			"ORDER BY post.date"),	
			

	//Remember! TopicDao.Thread sorts the posts backwards to give you the bottom of the list. Remember to reverse the results
	@NamedQuery(name="ThreadDao.Thread", query="" +
			"SELECT post FROM Post post WHERE post.thread.postId=:postId " +
			"AND postId <> post.thread.postId " +
			"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
			"ORDER BY post.path desc"),
	
	@NamedQuery(name="ThreadDao.ThreadByDate", query="" +
			"SELECT post FROM Post post WHERE post.thread.postId=:postId " +
			"AND postId <> post.thread.postId " +
			"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
			"ORDER BY post.date"),
			
			
	@NamedQuery(name="ThreadDao.ThreadCount", query="" +
			"SELECT count(post.postId) FROM Post post WHERE post.thread.postId=:postId " +
			"AND postId <> post.thread.postId " +
			"AND bitwise_and( post.metaMask, :filterMask ) = 0 "),				
			
	// Latest Post		
	@NamedQuery(name="LatestPostsDao.Nested", query="" +
		"SELECT post FROM Post post WHERE post.threadReplyDate IS NOT NULL AND " +
		"post.root.postId NOT IN (:threadIds)" +
		"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
		"ORDER BY post.threadReplyDate DESC"),
		
	@NamedQuery(name="LatestPostsDao.NestedCount", query="" +
		"SELECT count(post.postId) FROM Post post WHERE post.threadReplyDate IS NOT NULL " +
		"AND post.root.postId NOT IN (:threadIds)" +
		"AND bitwise_and( post.metaMask, :filterMask ) = 0 "),	

	@NamedQuery(name="LatestPostsDao.RepliesInThreads", query="" +
		"SELECT post FROM Post post WHERE post.thread.postId IN(:postIds) " +
		"AND postId <> post.thread.postId " +
		"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
		"ORDER BY post.path"),	
	
	@NamedQuery(name="LatestPostsDao.RepliesInThreadsByDate", query="" +
			"SELECT post FROM Post post WHERE post.thread.postId IN(:postIds) " +
			"AND postId <> post.thread.postId " +
			"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
			"ORDER BY post.date"),		
		
		

	@NamedQuery(name="LatestPostsDao.Flat", query="" +
		"SELECT post FROM Post post " +
		"WHERE post.root.postId NOT IN (:threadIds) " +
		"AND bitwise_and( post.metaMask, :filterMask ) = 0  " +
		"AND post.parent is not null "+
		"ORDER BY post.date DESC"),
		
	@NamedQuery(name="LatestPostsDao.FlatCount", query="" +
		"SELECT count(post.postId) FROM Post post " +
		"WHERE post.root.postId NOT IN (:threadIds) AND " +
		"bitwise_and( post.metaMask, :filterMask ) = 0" ),	
			
	@NamedQuery(name="LatestPostsDao.Threads", query="" +
		"SELECT post FROM Post post " +
		"WHERE post.root.postId NOT IN (:threadIds) " +
		"AND post.parent.postId IS NULL " +
		"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
		"ORDER BY post.date DESC"),
		
	@NamedQuery(name="LatestPostsDao.ThreadsCount", query="" +
		"SELECT count(post.postId) FROM Post post " +
		"WHERE post.root.postId NOT IN (:threadIds) AND " +
		"post.parent.postId IS NULL " +
		"AND bitwise_and( post.metaMask, :filterMask ) = 0 "),	
			
	@NamedQuery(name="LatestPostsDao.Conversations", query="" +
		"SELECT post FROM Post post " +
		"WHERE post.root.postId NOT IN (:threadIds) " +
		"AND post.thread.postId = post.postId " +
		"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
		"ORDER BY post.date DESC"),
		
	@NamedQuery(name="LatestPostsDao.ConversationsCount", query="" +
		"SELECT count(post.postId) FROM Post post " +
		"WHERE post.root.postId NOT IN (:threadIds) " +
		"AND post.thread.postId = post.postId " +
		"AND bitwise_and( post.metaMask, :filterMask ) = 0 "),
			
		
	//Inbox
		
		
	@NamedQuery(name="InboxDao.Flat", query="" +
				"SELECT post FROM Post post " +
				"WHERE post.root.postId NOT IN (:threadIds) " +
				"AND post.date > :startDate " +
				"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
				"ORDER BY post.date DESC"),
				
	@NamedQuery(name="InboxDao.FlatCount", query="" +
				"SELECT count(post.postId) FROM Post post " +
				"WHERE post.root.postId NOT IN (:threadIds) " +
				"AND post.date > :startDate " +
				"AND bitwise_and( post.metaMask, :filterMask ) = 0 "+
				"ORDER BY post.date DESC"),

	//Movies	 
	@NamedQuery(name="MovieDao.peopleWithMovieRatings", query="SELECT distinct ass.creator.personId, count(ass.creator.personId) " +
			"FROM Post p, AssociationPostTag ass INNER JOIN ass.tag "+
			"WHERE ass.tag.type='RATING' " +
			"AND bitwise_and( ass.post.metaMask, "+PostFlagBitmasks.BITMASK_MOVIE+" ) = " +PostFlagBitmasks.BITMASK_MOVIE + 
			"AND p.postId = ass.post.postId " +
			"GROUP BY ass.creator.personId"),
			
	@NamedQuery(name="MovieDao.moviesSortedByTitle", query="SELECT p FROM Post p " +
			"WHERE " +
			"bitwise_and( p.metaMask, "+PostFlagBitmasks.BITMASK_MOVIE+" ) = " +PostFlagBitmasks.BITMASK_MOVIE +
			"ORDER BY p.titleTag.sortValue"),
			
	@NamedQuery(name="MovieDao.moviesSortedByTitleDesc", query="SELECT p FROM Post p " +
			"WHERE " +
			"bitwise_and( p.metaMask, "+PostFlagBitmasks.BITMASK_MOVIE+" ) = " +PostFlagBitmasks.BITMASK_MOVIE +
			"ORDER BY p.titleTag.sortValue DESC"),
			
	@NamedQuery(name="MovieDao.moviesCount", query="SELECT count(p.postId) FROM Post p " +
			"WHERE " +
			"bitwise_and( p.metaMask, "+PostFlagBitmasks.BITMASK_MOVIE+" ) = " +PostFlagBitmasks.BITMASK_MOVIE),		

	@NamedQuery(name="MovieDao.moviesSortedByTitleForPerson", query="SELECT ass.post " +
			"FROM AssociationPostTag ass INNER JOIN ass.post "+
			"WHERE ass.tag.type='RATING' AND ass.creator.personId = :guid " +
			"AND bitwise_and( ass.post.metaMask, "+PostFlagBitmasks.BITMASK_MOVIE+" ) = " +PostFlagBitmasks.BITMASK_MOVIE + 
			"ORDER BY ass.post.titleTag.sortValue"),
			
	@NamedQuery(name="MovieDao.moviesSortedByTitleForPersonDesc", query="SELECT ass.post " +
			"FROM AssociationPostTag ass INNER JOIN ass.post "+
			"WHERE ass.tag.type='RATING' AND ass.creator.personId = :guid " +
			"AND bitwise_and( ass.post.metaMask, "+PostFlagBitmasks.BITMASK_MOVIE+" ) = " +PostFlagBitmasks.BITMASK_MOVIE + 
			"ORDER BY ass.post.titleTag.sortValue DESC"),				
			
	@NamedQuery(name="MovieDao.moviesSortedByAverageRating", query="SELECT p FROM Post p " +
			"WHERE " +
			"bitwise_and( p.metaMask, "+PostFlagBitmasks.BITMASK_MOVIE+" ) = " +PostFlagBitmasks.BITMASK_MOVIE +
			"ORDER BY p.avgRatingTag.sortValue, p.titleTag.sortValue"),
			
	@NamedQuery(name="MovieDao.moviesSortedByAverageRatingDesc", query="SELECT p FROM Post p " +
			"WHERE " +
			"bitwise_and( p.metaMask, "+PostFlagBitmasks.BITMASK_MOVIE+" ) = " +PostFlagBitmasks.BITMASK_MOVIE +
			"ORDER BY p.avgRatingTag.sortValue DESC, p.titleTag.sortValue"),		

			
	@NamedQuery(name="MovieDao.moviesSortedByRatingForPerson", query="SELECT ass.post " +
			"FROM AssociationPostTag ass INNER JOIN ass.post "+
			"WHERE ass.tag.type='RATING' AND ass.creator.personId = :guid " +
			"AND bitwise_and( ass.post.metaMask, "+PostFlagBitmasks.BITMASK_MOVIE+" ) = " +PostFlagBitmasks.BITMASK_MOVIE + 
			"ORDER BY ass.tag.value, ass.post.titleTag.sortValue"),		
			
	@NamedQuery(name="MovieDao.moviesSortedByRatingForPersonDesc", query="SELECT ass.post " +
			"FROM AssociationPostTag ass INNER JOIN ass.post "+
			"WHERE ass.tag.type='RATING' AND ass.creator.personId = :guid " +
			"AND bitwise_and( ass.post.metaMask, "+PostFlagBitmasks.BITMASK_MOVIE+" ) = " +PostFlagBitmasks.BITMASK_MOVIE + 
			"ORDER BY ass.tag.value DESC, ass.post.titleTag.sortValue"),		

	@NamedQuery(name="MovieDao.moviesRatedByPersonCount", query="SELECT count(ass.guid) " +
			"FROM AssociationPostTag ass INNER JOIN ass.post "+
			"WHERE ass.tag.type='RATING' AND ass.creator.personId = :guid " +
			"AND bitwise_and( ass.post.metaMask, "+PostFlagBitmasks.BITMASK_MOVIE+" ) = " +PostFlagBitmasks.BITMASK_MOVIE ),
					
	@NamedQuery(name="MovieDao.moviesSortedByYear", query="SELECT p FROM Post p " +
			"WHERE " +
			"bitwise_and( p.metaMask, "+PostFlagBitmasks.BITMASK_MOVIE+" ) = " +PostFlagBitmasks.BITMASK_MOVIE +
			"ORDER BY p.publishYear, p.titleTag.sortValue"),
			
	@NamedQuery(name="MovieDao.moviesSortedByYearDesc", query="SELECT p FROM Post p " +
			"WHERE " +
			"bitwise_and( p.metaMask, "+PostFlagBitmasks.BITMASK_MOVIE+" ) = " +PostFlagBitmasks.BITMASK_MOVIE +
			"ORDER BY p.publishYear DESC, p.titleTag.sortValue"),		
			
	@NamedQuery(name="MovieDao.moviesSortedByYearForPerson", query="SELECT ass.post " +
			"FROM AssociationPostTag ass INNER JOIN ass.post "+
			"WHERE ass.tag.type='RATING' AND ass.creator.personId = :guid " +
			"AND bitwise_and( ass.post.metaMask, "+PostFlagBitmasks.BITMASK_MOVIE+" ) = " +PostFlagBitmasks.BITMASK_MOVIE + 
			"ORDER BY ass.post.publishYear, ass.post.titleTag.sortValue"),
			
	@NamedQuery(name="MovieDao.moviesSortedByYearForPersonDesc", query="SELECT ass.post " +
			"FROM AssociationPostTag ass INNER JOIN ass.post "+
			"WHERE ass.tag.type='RATING' AND ass.creator.personId = :guid " +
			"AND bitwise_and( ass.post.metaMask, "+PostFlagBitmasks.BITMASK_MOVIE+" ) = " +PostFlagBitmasks.BITMASK_MOVIE + 
			"ORDER BY ass.post.publishYear DESC, ass.post.titleTag.sortValue"),
			
	@NamedQuery(name="PersonDao.loadPersonList", query="SELECT person FROM Person person WHERE person.personId IN (:personIds) ORDER BY person.login")
			
})

@NamedNativeQueries({
	//Trevis, once every thing is working try to use the day/month + thread guid as the daily unique id for cross db compatibilityness
	@NamedNativeQuery(
		name="CalendarDao.fetchMonth", query=""+
			"select count(p.guid) ct, Year(p.date) as yr, Month(p.date) as mo, Day(p.date) as dd, "+
			"titleTag.value as title, p.root_guid as rootId, newid() as uniqueId "+ // //uuid() as uniqueId
			"from POST p "+
			"inner join TAG as titleTag on titleTag.guid = tag_guid_title "+
			"inner join ENTRY as e on e.guid = p.latest_entry_guid "+
			"where Year(p.date) = :year AND Month(p.date) = :month  "+
			"AND p.meta_mask & :filterMask = 0 " +
			"group by Year(p.date), Month(p.date), Day(p.date), titleTag.value, p.root_guid "+
			"order by Year(p.date), Month(p.date), Day(p.date) ",
			resultSetMapping="threadSummaryMapping"
	),
	@NamedNativeQuery(
		name="CalendarDao.fetchHourly", query="" +
			"select p.date, p.guid as postId, p.root_guid as rootId, titleTag.value as title, c.login, " +
			"c.guid as creator_guid, '' as summary "+
			"from POST p "+
			"inner join TAG as titleTag on titleTag.guid = tag_guid_title "+
			"inner join PERSON as c on c.guid = p.person_guid_creator "+
			"where p.date between :startDate and :endDate " +
			"AND p.meta_mask & :filterMask  = 0 " +
			"order by p.date ",
			resultSetMapping="simplePostMapping"
	),
	@NamedNativeQuery(
		name="CalendarDao.fetchHourlyWithSummary", query="" +
			"select p.date, p.guid as postId, p.root_guid as rootId, titleTag.value as title, c.login, " +
			"c.guid as creator_guid, e.summary as summary "+
			"from POST p "+
			"inner join TAG as titleTag on titleTag.guid = tag_guid_title "+
			"inner join ENTRY as e on e.guid = p.latest_entry_guid "+
			"inner join PERSON as c on c.guid = p.person_guid_creator "+
			"where p.date between :startDate and :endDate " +
			"AND p.meta_mask & :filterMask = 0 " +
			"order by p.date ",
			resultSetMapping="simplePostMapping"
	),
	
	@NamedNativeQuery(name="CalendarDao.fetchYear", query=
		"select count(p.guid) ct, Year(p.date) as yr, Month(p.date) as mo, Day(p.date) as dd, newid() as uniqueId "+ //uuid() as uniqueId 
		"from POST p "+
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
		"select count(p.guid) ct, Year(p.date) as yr, Month(p.date) as mo, Day(p.date) as dd, newid() as uniqueId "+ //uuid() as uniqueId
		"from POST p "+
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
            @FieldResult(name="year", column="yr"),
            @FieldResult(name="month", column="mo"),
            @FieldResult(name="day", column="dd"),
            @FieldResult(name="count", column="ct"),
            @FieldResult(name="rootId", column="rootId"),
            @FieldResult(name="title", column="title"),
            //@FieldResult(name="date", column="date"),
            @FieldResult(name="uniqueId", column="uniqueId")
        })),    

    @SqlResultSetMapping(name="daySummaryMapping", entities=
    	@EntityResult(entityClass=org.ttdc.persistence.objects.DaySummaryEntity.class, fields = {
            @FieldResult(name="year", column="yr"),
            @FieldResult(name="month", column="mo"),
            @FieldResult(name="day", column="dd"),
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
	}
	@Field(index=Index.TOKENIZED, store=Store.NO)
	public String getBody() {
		return body;
	}
	
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
