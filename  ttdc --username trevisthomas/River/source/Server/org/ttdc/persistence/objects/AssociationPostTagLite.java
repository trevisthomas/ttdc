package org.ttdc.persistence.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;

@Entity
@NamedNativeQueries({
	@NamedNativeQuery(name="assLite.assesForTagIds_broken", query="select ass.guid as guid, ass.post_guid as postId, ass.tag_guid as tagId " +
			"from association_post_tag ass " +
			"where ass.tag_guid in (:tagIds)", resultSetMapping="assLiteMapping"),

	@NamedNativeQuery(name="assLite.assesForTagIds", query="select ass.guid as guid, ass.post_guid as postId, ass.tag_guid as tagId " +
			"from association_post_tag ass inner join Post p on p.guid = ass.post_guid " +
			"where ass.tag_guid in (:tagIds) " +
			"group by ass.post_guid,ass.guid,ass.tag_guid " +
			"having count(ass.post_guid) = :count", resultSetMapping="assLiteMapping")		
			/*
			select ass.post_guid from association_post_tag ass 
			where ass.tag_guid in ('2B994A54-59BF-4043-8F19-19A6AA599566', '3DA00CA6-4C48-4AEF-B2C9-F13B05DBE0A7') 
			group by ass.post_guid
			having count(ass.post_guid) = 2*/
			
})


@SqlResultSetMappings({
@SqlResultSetMapping(name="assLiteMapping", entities=
	@EntityResult(entityClass=org.ttdc.persistence.objects.AssociationPostTagLite.class, fields = {
        @FieldResult(name="guid", column="guid"),
        @FieldResult(name="postId", column="postId"),
        @FieldResult(name="tagId", column="tagId")
    }))
})
public class AssociationPostTagLite {
	//private static Map<String,AssociationPostTagLite> lookup = new HashMap<String,AssociationPostTagLite>();
	//private static Set<String> postIdSet = null;
	private String guid;
	private String tagId;
	private String postId;
	
	@Id
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getTagId() {
		return tagId;
	}
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
		/*
		if(postIdSet != null)
			postIdSet.add(postId);
			*/
	}
	
	
}
