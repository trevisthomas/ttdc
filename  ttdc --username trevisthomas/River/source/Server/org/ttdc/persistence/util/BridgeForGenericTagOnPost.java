package org.ttdc.persistence.util;

import org.ttdc.persistence.objects.Tag;

public class BridgeForGenericTagOnPost extends BridgeBaseForTagOnPost{
	@Override
	 boolean isIndexedTagType(String tagType) {
		return tagType.equals(Tag.TYPE_TOPIC) ||
		   tagType.equals(Tag.TYPE_MOVIE) ||
		   tagType.equals(Tag.TYPE_REVIEW) ||
		   tagType.equals(Tag.TYPE_RATING);
	}
	
}