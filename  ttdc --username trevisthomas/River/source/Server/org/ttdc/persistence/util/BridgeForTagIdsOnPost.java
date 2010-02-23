package org.ttdc.persistence.util;

import java.util.List;

import org.hibernate.search.bridge.StringBridge;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

public class BridgeForTagIdsOnPost implements StringBridge{
	public String objectToString(Object object) {
		Post post = (Post) object;
		List<AssociationPostTag> asses = post.getTagAssociations();
		StringBuilder sb = new StringBuilder();
		for(AssociationPostTag ass : asses){
			sb.append(ass.getTag().getTagId());
			sb.append(" ");
		}
		return sb.toString();
	}
}
