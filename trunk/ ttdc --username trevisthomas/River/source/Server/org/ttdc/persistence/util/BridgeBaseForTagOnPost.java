package org.ttdc.persistence.util;

import java.util.List;

import org.hibernate.search.bridge.StringBridge;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

/**
 * 
 * To create a bridge on a tag type just subclass this and implement the abstract method
 *
 */
public abstract class BridgeBaseForTagOnPost implements StringBridge{
	public String objectToString(Object object) {
		Post post = (Post) object;
		List<AssociationPostTag> asses = post.getTagAssociations();
		StringBuilder sb = new StringBuilder();
		for(AssociationPostTag ass : asses){
			if(isIndexedTagType(ass.getTag().getType())){
				sb.append(ass.getTag().getValue());
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	
	abstract boolean isIndexedTagType(String tagType);
}
