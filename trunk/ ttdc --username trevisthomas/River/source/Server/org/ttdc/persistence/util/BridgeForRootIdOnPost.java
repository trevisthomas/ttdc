package org.ttdc.persistence.util;

import org.hibernate.search.bridge.StringBridge;
import org.ttdc.persistence.objects.Post;

public class BridgeForRootIdOnPost implements StringBridge{
	public String objectToString(Object object) {
		Post post = (Post) object;
		return post.getRoot().getPostId().toString();
	}
}
