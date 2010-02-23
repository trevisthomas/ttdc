package org.ttdc.persistence.util;

import org.hibernate.search.bridge.StringBridge;
import org.ttdc.persistence.objects.Post;

public class BridgeForPostType implements StringBridge{
	public final static String TOPIC="TOPIC";
	public final static String CONVERSATION="CONVERSATION";
	public final static String REPLY="REPLY";
	
	public String objectToString(Object object) {
		Post p = (Post) object;
		if(p.getRoot().getPostId().equals(p.getPostId())){
			return TOPIC;
		}
		else if(p.getParent().isRootPost()){
			return CONVERSATION;
		}
		else{
			return REPLY;
		}
	}
}
