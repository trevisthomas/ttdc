package org.ttdc.persistence.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.search.bridge.StringBridge;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Tag;

public class BridgeForRelatedDatesOnTag implements StringBridge{
	private final SimpleDateFormat luceneDateFormater = new SimpleDateFormat("yyyyMMdd");
	@Override
	public String objectToString(Object object) {
		Tag tag = (Tag) object;
		
		List<Date> list = Persistence.session().createQuery("SELECT ass.date FROM AssociationPostTag ass " +
										  "WHERE ass.post.postId IN " +
										  " (SELECT ass2.post.postId FROM AssociationPostTag ass2 WHERE ass2.tag.tagId = :tagId)")
			.setString("tagId", tag.getTagId())
			.list();
		
		
		StringBuilder sb = new StringBuilder();
		for(Date d : list){
			sb.append(luceneDateFormater.format(d));
			sb.append(" ");
		}
		return sb.toString();
	}

}
