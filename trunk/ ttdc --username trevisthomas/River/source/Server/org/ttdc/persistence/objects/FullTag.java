package org.ttdc.persistence.objects;

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
//		name="FullTag.loadList", query=""+
//		"SELECT t.GUID tagId, t.VALUE value, t.TYPE type, a.POST_GUID postId, a.CREATOR_GUID creatorId, a.GUID assId, " +
//		" p.LOGIN creatorLogin " +
//		" FROM TAG t "+
//		"INNER JOIN ASSOCIATION_POST_TAG a on a.TAG_GUID = t.GUID " +
//		"INNER JOIN PERSON p on p.GUID = a.CREATOR_GUID "+
//		"WHERE a.POST_GUID IN (:postIds) ",
//			resultSetMapping="fullTag"
		name="FullTag.loadList", query=""+
		"SELECT t.GUID tagId, t.VALUE value, t.TYPE type, a.POST_GUID postId, a.CREATOR_GUID creatorId, a.GUID assId, " +
		" p.LOGIN creatorLogin " +
		" FROM ASSOCIATION_POST_TAG a "+
		"INNER JOIN TAG t on a.TAG_GUID = t.GUID " +
		"INNER JOIN PERSON p on p.GUID = a.CREATOR_GUID "+
		"WHERE a.POST_GUID IN (:postIds) ",
			resultSetMapping="fullTag"
	),
})

@SqlResultSetMappings({
	@SqlResultSetMapping(name="fullTag", entities=
		@EntityResult(entityClass=org.ttdc.persistence.objects.FullTag.class, fields = {
	        @FieldResult(name="postId", column="postId"),
	        @FieldResult(name="tagId", column="tagId"),
	        @FieldResult(name="value", column="value"),
	        @FieldResult(name="type", column="type"),
	        @FieldResult(name="creatorId", column="creatorId"),
	        @FieldResult(name="assId", column="assId"),
	        @FieldResult(name="creatorLogin", column="creatorLogin")
    }))
})


@Entity
public class FullTag {
	private String tagId;
	private String value;
	private String type;
	private String postId;
	private String creatorId;
	private String assId;
	private String creatorLogin;
	
	
	public String getTagId() {
		return tagId;
	}
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public String getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	
	@Id
	public String getAssId() {
		return assId;
	}
	public void setAssId(String assId) {
		this.assId = assId;
	}
	public String getCreatorLogin() {
		return creatorLogin;
	}
	public void setCreatorLogin(String creatorLogin) {
		this.creatorLogin = creatorLogin;
	}
	
}
