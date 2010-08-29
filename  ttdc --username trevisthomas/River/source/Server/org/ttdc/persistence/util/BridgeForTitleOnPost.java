package org.ttdc.persistence.util;

import org.hibernate.search.bridge.StringBridge;
import org.ttdc.persistence.objects.Post;

public class BridgeForTitleOnPost implements StringBridge{
	public String objectToString(Object object) {
		Post p = (Post) object;
		//I changed my mind.  I only want titles indexed for the root posts...
		/*
		 * I made this choice on 8/28/2010 because i didnt like the way search worked
		 * with all posts being found when you did a search on a title.
		 */
		if(p.isRootPost())
			return p.getTitle();
		else
			return "";
	}
}
