package org.ttdc.persistence.util;

import org.ttdc.persistence.objects.Tag;


public class BridgeForCreatorTagOnPost extends BridgeBaseForTagOnPost{
	@Override
	 boolean isIndexedTagType(String tagType) {
		return tagType.equals(Tag.TYPE_CREATOR);
	}
	
}
