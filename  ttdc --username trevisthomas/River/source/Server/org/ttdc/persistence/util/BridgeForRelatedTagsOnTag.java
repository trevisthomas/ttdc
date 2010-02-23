package org.ttdc.persistence.util;

import java.util.List;

import org.hibernate.search.bridge.StringBridge;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Tag;

public class BridgeForRelatedTagsOnTag  implements StringBridge{
	@Override
	public String objectToString(Object object) {
		Tag tag = (Tag) object;
		
		List<Tag> list = Persistence.session().createQuery("SELECT ass.tag FROM AssociationPostTag ass " +
										  "WHERE ass.post.postId IN " +
										  " (SELECT ass2.post.postId FROM AssociationPostTag ass2 WHERE ass2.tag.tagId = :tagId)")
			.setString("tagId", tag.getTagId())
			.list();
		
		
		StringBuilder sb = new StringBuilder();
		for(Tag t : list){
			sb.append(t.getTagId());
			sb.append(" ");
		}
		//System.out.println(sb.toString());
		return sb.toString();
	}
}
