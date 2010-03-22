package org.ttdc.persistence.util;

import org.apache.log4j.Logger;
import org.hibernate.search.bridge.StringBridge;
import org.ttdc.gwt.shared.util.PostFlag;
import org.ttdc.persistence.objects.Post;

public class BridgeForPostFlag implements StringBridge{
	private final static Logger log = Logger.getLogger(BridgeForPostFlag.class);
	
	public String objectToString(Object object) {
		Post p = (Post) object;
		
		StringBuilder sb = new StringBuilder();
		if(p.isDeleted())
			sb.append(PostFlag.DELETED.name()).append(" ");
		if(p.isINF())
			sb.append(PostFlag.INF.name()).append(" ");
		if(p.isLegacyThreadHolder())
			sb.append(PostFlag.LEGACY.name()).append(" ");
		if(p.isLocked())
			sb.append(PostFlag.LOCKED.name()).append(" ");
		if(p.isMovie())
			sb.append(PostFlag.MOVIE.name()).append(" ");
		if(p.isNWS())
			sb.append(PostFlag.NWS.name()).append(" ");
		if(p.isPrivate())
			sb.append(PostFlag.PRIVATE.name()).append(" ");
		if(p.isRatable())
			sb.append(PostFlag.RATABLE.name()).append(" ");
		if(p.isReview())
			sb.append(PostFlag.REVIEW.name()).append(" ");
		if(p.isLinkContained())
			sb.append(PostFlag.LINK.name()).append(" ");
		
		log.debug("PostFlagBridge: "+sb);
		return sb.toString();
		
	}
}
