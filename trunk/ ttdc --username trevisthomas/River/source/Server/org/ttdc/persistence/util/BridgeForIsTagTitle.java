package org.ttdc.persistence.util;

import java.util.List;

import org.hibernate.search.bridge.StringBridge;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Tag;

public class BridgeForIsTagTitle implements StringBridge{
	public String objectToString(Object object) {
		Tag tag = (Tag) object;
		
		List<Person> list = Persistence.session().createQuery("SELECT ass.creator FROM AssociationPostTag ass " +
										  					  "WHERE ass.tag.tagId = :tagId AND ass.title=1")
															   .setString("tagId", tag.getTagId())
															   .list();
		
		
		StringBuilder sb = new StringBuilder();
		if(list.size() > 0){
			return "yes";
		}
		else{
			return "no";
		}
	}

}
