package org.ttdc.persistence.util;

import java.util.List;

import org.hibernate.search.bridge.StringBridge;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

public class BridgeForTagOnPost implements StringBridge {
	public String objectToString(Object object) {
		Post post = (Post) object;
		List<AssociationPostTag> asses = post.getTagAssociations();
		StringBuilder sb = new StringBuilder();
		for (AssociationPostTag ass : asses) {
			if (isIndexedTagType(ass.getTag().getType())) {
				sb.append(ass.getTag().getValue());
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	boolean isIndexedTagType(String tagType) {
		return tagType.equals(Tag.TYPE_TOPIC) || tagType.equals(Tag.TYPE_RATING);
	}

}