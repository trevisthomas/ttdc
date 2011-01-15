package org.ttdc.persistence.objects;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;

@NamedNativeQueries({
	@NamedNativeQuery(
		name="InitConstants.loadPostDateCache", query="select guid as postId, date from post order by DATE desc",
			resultSetMapping="postDate"
	),
	
})

@SqlResultSetMappings({
	@SqlResultSetMapping(name="postDate", entities=
		@EntityResult(entityClass=org.ttdc.persistence.objects.PostDate.class, fields = {
	        @FieldResult(name="postId", column="postId"),
	        @FieldResult(name="date", column="date"),
	}))
})


@Entity
public class PostDate {
	private String postId;
	private Date date;
	
	@Id
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
//	@Transient
//	public float getScore(Date seedTime){
//		seedTime
//		getDate().getTime()
//	}
	
	
}
