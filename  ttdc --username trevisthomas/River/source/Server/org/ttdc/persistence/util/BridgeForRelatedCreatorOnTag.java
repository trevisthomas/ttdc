package org.ttdc.persistence.util;

import java.util.List;

import org.hibernate.search.bridge.StringBridge;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Tag;

public class BridgeForRelatedCreatorOnTag implements StringBridge{
	public String objectToString(Object object) {
		Tag tag = (Tag) object;
		
		List<Person> list = Persistence.session().createQuery("SELECT ass.creator FROM AssociationPostTag ass " +
										  "WHERE ass.post.postId IN " +
										  " (SELECT ass2.post.postId FROM AssociationPostTag ass2 WHERE ass2.tag.tagId = :tagId)")
			.setString("tagId", tag.getTagId())
			.list();
		
		
		StringBuilder sb = new StringBuilder();
		for(Person p : list){
			sb.append(p.getLogin());
			sb.append(" ");
		}
		//System.out.println(sb.toString());
		return sb.toString();
	}
}
