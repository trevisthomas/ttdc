package org.ttdc.persistence.util;

import org.hibernate.search.bridge.StringBridge;
import org.ttdc.persistence.objects.Post;

public class BridgeForBodyOnPost implements StringBridge{
	public String objectToString(Object object) {
		Post p = (Post) object;
		return p.getEntry().getBody();
	}
}
