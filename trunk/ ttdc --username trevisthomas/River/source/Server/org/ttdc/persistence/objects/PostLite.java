package org.ttdc.persistence.objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;

/**
 * Tag browser... it envelopes all. 
 * 
 * @author Trevis
 *
 */
@Entity
@NamedQueries({
	
	@NamedQuery(name="postId.Thread", query="SELECT post.postId FROM Post post WHERE post.root.postId = :threadId"),
	@NamedQuery(name="postId.PostsTagUnion", query="SELECT ass.post.postId FROM AssociationPostTag ass " +
			"INNER JOIN ass.post " +
			"WHERE ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.post.postId	" +
			"HAVING count(ass.post.postId) = :count)"),
	@NamedQuery(name="postId.PostsTagUnionThread", query="SELECT ass.post.postId FROM AssociationPostTag ass " +
					"INNER JOIN ass.post " +
					"WHERE ass.post.root.postId = :threadId AND ass.tag.tagId IN (:tagIds)" +
					"GROUP BY ass.post.postId	" +
					"HAVING count(ass.post.postId) = :count)"),			
	@NamedQuery(name="postId.replyPostsTagUnion", query="SELECT ass.post.postId FROM AssociationPostTag ass " +
			"INNER JOIN ass.post " +
			"WHERE ass.post.parent.postId IS NULL AND ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.post.postId	" +
			"HAVING count(ass.post.postId) = :count)"),
	@NamedQuery(name="postId.rootPostsTagUnion", query="SELECT ass.post.postId FROM AssociationPostTag ass " +
			"INNER JOIN ass.post " +
			"WHERE ass.post.parent.postId IS NOT NULL AND ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.post.postId	" +
			"HAVING count(ass.post.postId) = :count)"),		
	@NamedQuery(name="postId.replyPostsTagUnionByDate", query="" +
					"SELECT post.postId FROM Post post WHERE post.postId IN (" +
					"SELECT ass.post.postId FROM AssociationPostTag ass " +
					"INNER JOIN ass.post " +
					"WHERE ass.post.parent.postId IS NOT NULL AND ass.tag.tagId IN (:tagIds)" +
					"GROUP BY ass.post.postId	" +
					"HAVING count(ass.post.postId) = :count) ORDER BY post.date DESC"),
					
	@NamedQuery(name="postId.replyPostsTagUnionByDateThread", query="" +
							"SELECT post.postId FROM Post post WHERE post.root.postId = :threadId AND post.postId IN (" +
							"SELECT ass.post.postId FROM AssociationPostTag ass " +
							"INNER JOIN ass.post " +
							"WHERE ass.post.parent.postId IS NOT NULL AND ass.tag.tagId IN (:tagIds)" +
							"GROUP BY ass.post.postId	" +
							"HAVING count(ass.post.postId) = :count) ORDER BY post.date DESC"),					
					
	@NamedQuery(name="postId.rootPostsTagUnionByMass", query="" +
			"SELECT post.root.postId as postId FROM Post post WHERE post.postId IN (" +
			"SELECT ass.post.postId FROM AssociationPostTag ass " +
			"INNER JOIN ass.post " +
			"WHERE ass.post.parent.postId IS NULL AND ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.post.postId	" +
			"HAVING count(ass.post.postId) = :count) " +
			"GROUP BY post.root.postId ORDER BY COUNT(post.postId) DESC"),
			
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
	
})
@NamedNativeQueries({
	/*Trevis this native query was created because you needed to do a type conversion to calculate the average rating in the db*/
			
	@NamedNativeQuery(name="native.postId.moviesByRating", query="select post0_.GUID as postId, sum(CAST (tag2_.value AS FLOAT ))/ count(tag2_.value) as average, tag6_.value as col_2_0_, tag4_.type as col_3_0_ " +
			"from Post post0_, " +
			"ASSOCIATION_POST_TAG associatio1_ inner join Tag tag2_ on associatio1_.TAG_GUID=tag2_.GUID," +
			"ASSOCIATION_POST_TAG associatio3_ inner join Tag tag4_ on associatio3_.TAG_GUID=tag4_.GUID," +
			"ASSOCIATION_POST_TAG associatio5_ inner join Tag tag6_ on associatio5_.TAG_GUID=tag6_.GUID	" +
			"where tag2_.type='RATING' and tag4_.type='MOVIE' and tag6_.type='SORT_TITLE' " +
			"and post0_.GUID=associatio1_.POST_GUID and post0_.GUID=associatio3_.POST_GUID and post0_.GUID=associatio5_.POST_GUID " +
			"group by post0_.GUID,tag6_.value,tag4_.type " +
			"order by average DESC, tag6_.value", resultSetMapping="postLiteMapping"),
			
	@NamedNativeQuery(name="native.postId.moviesByRatingReverse", query="select post0_.GUID as postId, sum(CAST (tag2_.value AS FLOAT ))/ count(tag2_.value) as average, tag6_.value as col_2_0_, tag4_.type as col_3_0_ " +
					"from Post post0_, " +
					"ASSOCIATION_POST_TAG associatio1_ inner join Tag tag2_ on associatio1_.TAG_GUID=tag2_.GUID," +
					"ASSOCIATION_POST_TAG associatio3_ inner join Tag tag4_ on associatio3_.TAG_GUID=tag4_.GUID," +
					"ASSOCIATION_POST_TAG associatio5_ inner join Tag tag6_ on associatio5_.TAG_GUID=tag6_.GUID	" +
					"where tag2_.type='RATING' and tag4_.type='MOVIE' and tag6_.type='SORT_TITLE' " +
					"and post0_.GUID=associatio1_.POST_GUID and post0_.GUID=associatio3_.POST_GUID and post0_.GUID=associatio5_.POST_GUID " +
					"group by post0_.GUID,tag6_.value,tag4_.type " +
					"order by average, tag6_.value", resultSetMapping="postLiteMapping"),			
	
	/*		
	@NamedNativeQuery(name="native.postId.moviesByRating", query="select distinct associatio0_.POST_GUID as postId , sum(CAST (tag1_.value AS FLOAT ))/ count(tag1_.value) as average " +
			"from ASSOCIATION_POST_TAG associatio0_ inner join Tag tag1_ on associatio0_.TAG_GUID=tag1_.GUID " +
			"where tag1_.type='RATING' and (associatio0_.POST_GUID in (select associatio0_.POST_GUID from ASSOCIATION_POST_TAG associatio3_, Tag tag4_ where associatio3_.TAG_GUID=tag4_.GUID and tag4_.type='MOVIE') ) " +
			"group by associatio0_.POST_GUID " +
			"order by average desc", resultSetMapping="postLiteMapping"),
			
	@NamedNativeQuery(name="native.postId.moviesByRatingReverse", query="select distinct associatio0_.POST_GUID as postId , sum(CAST (tag1_.value AS FLOAT ))/ count(tag1_.value) as average " +
					"from ASSOCIATION_POST_TAG associatio0_ inner join Tag tag1_ on associatio0_.TAG_GUID=tag1_.GUID " +
					"where tag1_.type='RATING' and (associatio0_.POST_GUID in (select associatio0_.POST_GUID from ASSOCIATION_POST_TAG associatio3_, Tag tag4_ where associatio3_.TAG_GUID=tag4_.GUID and tag4_.type='MOVIE') ) " +
					"group by associatio0_.POST_GUID " +
					"order by average", resultSetMapping="postLiteMapping"),		
			
	*/
			
	@NamedNativeQuery(name="postLite.postsTagUnion", query="select ass.post_guid as postId from association_post_tag ass " +
			"where ass.tag_guid in (:tagIds) " +
			"group by ass.post_guid " +
			"having count(ass.post_guid) = :count", resultSetMapping="postLiteMapping"),
		/*	
	@NamedNativeQuery(name="postLite.postsTagUnionByDate", query="" +
					"select guid as postId from post where guid in (" +
					"select ass.post_guid as postId from association_post_tag ass " +
					"where ass.tag_guid in (:tagIds) " +
					"group by ass.post_guid " +
					"having count(ass.post_guid) = :count)" +
					"  order by date desc", resultSetMapping="postLiteMapping"),		
			*/
	@NamedNativeQuery(name="postLite.replyPostsTagUnion", query="select ass.post_guid as postId from association_post_tag ass " +
			"inner join Post as p on p.guid = ass.post_guid " +
			"where p.parent_guid is not null AND ass.tag_guid in (:tagIds) " +
			"group by ass.post_guid " +
			"having count(ass.post_guid) = :count", resultSetMapping="postLiteMapping"),
			
	@NamedNativeQuery(name="postLite.rootPostsTagUnion", query="select ass.post_guid as postId, from association_post_tag ass " +
			"inner join Post as p on p.guid = ass.post_guid " +
			"where p.parent_guid is null AND ass.tag_guid in (:tagIds) " +
			"group by ass.post_guid " +
			"having count(ass.post_guid) = :count", resultSetMapping="postLiteMapping"),
			
	@NamedNativeQuery(name="postLite.replyPostsTagUnionByDate", query=
			"select guid as postId from post where guid in( select ass.post_guid from association_post_tag ass " +
			"inner join Post as p on p.guid = ass.post_guid " +
			"where p.parent_guid is not null AND ass.tag_guid in (:tagIds) " +
			"group by ass.post_guid " +
			"having count(ass.post_guid) = :count) order by date desc ", resultSetMapping="postLiteMapping"),

	@NamedNativeQuery(name="postLite.replyPostsTagUnionByDateThread", query=
				"select guid as postId from post where guid in (select guid from post where post.root_guid = :threadId) " +
				"AND guid in( select ass.post_guid from association_post_tag ass " +
				"inner join Post as p on p.guid = ass.post_guid " +
				"where p.parent_guid is not null AND ass.tag_guid in (:tagIds) " +
				"group by ass.post_guid " +
				"having count(ass.post_guid) = :count) order by date desc ", resultSetMapping="postLiteMapping"),
				
	@NamedNativeQuery(name="postLite.rootPostsTagUnionByMass", query=
			"select root_guid as postId from post where root_guid in ( " +
			"select ass.post_guid from association_post_tag ass " +
			"inner join Post as p on p.guid = ass.post_guid " +
			"where p.parent_guid is null AND ass.tag_guid in (:tagIds) " +
			"group by ass.post_guid " +
			"having count(ass.post_guid) = :count) group by root_guid order by count(guid) desc", resultSetMapping="postLiteMapping"),
			
	@NamedNativeQuery(name="postLite.getMostPopular", query="SELECT root_guid as postId " +
			"FROM post GROUP BY post.root_guid ORDER BY COUNT (post.root_guid) DESC", resultSetMapping="postLiteMapping"),

	@NamedNativeQuery(name="postLite.getLatestThreads", query="SELECT guid as postId " +
					"FROM post WHERE post.parent_guid IS NULL ORDER BY date DESC", resultSetMapping="postLiteMapping")	
})

@SqlResultSetMappings({
@SqlResultSetMapping(name="postLiteMapping", entities=
	@EntityResult(entityClass=org.ttdc.persistence.objects.PostLite.class, fields = {
        @FieldResult(name="postId", column="postId")
    }))
})
public class PostLite{
	private String postId;
	@Id 
	@Column(name="GUID")
	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}
	
	/**
	 * Initially created for widgetService, for the hotlist implementation.
	 */
	@Override
	public boolean equals(Object that) {
		if(that == null) return false;
		if(that instanceof Post)
			return this.getPostId().equals(((Post)that).getPostId());
		else
			return super.equals(that);
	}
	
	@Override
	public int hashCode() {
		return this.getPostId().hashCode();
	}
	
	@Override
	public String toString() {
		return postId;
	}
}
