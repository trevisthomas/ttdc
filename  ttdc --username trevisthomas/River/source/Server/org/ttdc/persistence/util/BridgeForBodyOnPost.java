package org.ttdc.persistence.util;

import org.apache.log4j.Logger;
import org.hibernate.search.bridge.StringBridge;
import org.ttdc.persistence.objects.Post;

public class BridgeForBodyOnPost implements StringBridge{
	private final static Logger log = Logger.getLogger(BridgeForBodyOnPost.class);
	public String objectToString(Object object) {
		try{
			Post p = (Post) object;
			return p.getEntry().getBody();
		}
		catch (Exception e){
			log.error(e);
			return "";
		}
	}
}
