package org.ttdc.persistence.util;

import org.hibernate.search.bridge.StringBridge;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

public class BridgeForSortTitleOnPost implements StringBridge{
	public String objectToString(Object object) {
		Post p = (Post) object;
		
		AssociationPostTag ass = p.loadTitleTagAssociation();
		if(ass != null){
			return ass.getTag().getSortValue();
		}
		else
			return "";
	}
}
