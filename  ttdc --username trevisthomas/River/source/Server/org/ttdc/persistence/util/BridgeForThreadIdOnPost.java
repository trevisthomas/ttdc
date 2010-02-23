package org.ttdc.persistence.util;

import org.hibernate.search.bridge.StringBridge;
import org.ttdc.persistence.objects.Post;

public class BridgeForThreadIdOnPost implements StringBridge{
	public String objectToString(Object object) {
		Post post = (Post) object;
		if(post.getThread() != null)
			return post.getThread().getPostId().toString();
		else
			return "";
	}
}
