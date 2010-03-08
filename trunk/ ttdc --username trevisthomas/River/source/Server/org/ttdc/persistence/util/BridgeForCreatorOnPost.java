package org.ttdc.persistence.util;

import org.hibernate.search.bridge.StringBridge;
import org.ttdc.persistence.objects.Post;



public class BridgeForCreatorOnPost  implements StringBridge{
	@Override
	public String objectToString(Object object) {
		Post p = (Post) object;
		return p.getCreator().getLogin();
	}
	
}
