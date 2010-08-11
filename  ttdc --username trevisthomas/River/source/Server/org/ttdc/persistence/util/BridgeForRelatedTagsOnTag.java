package org.ttdc.persistence.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		
		//I didnt understand why this had duplicates... i added this set to take them out.  This was
		//discovered 8/10/2010 while i was adding the reindixing logic for when tag associations were created.
		Set<String> dedup = new HashSet<String>();
		for(Tag t : list){
			dedup.add(t.getTagId());
		}
		
		
		for(String s : dedup){
			sb.append(s);
			sb.append(" ");
		}
		//System.out.println(sb.toString());
		return sb.toString();
	}
}
