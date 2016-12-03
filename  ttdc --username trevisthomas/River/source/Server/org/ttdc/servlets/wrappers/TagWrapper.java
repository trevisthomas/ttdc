package org.ttdc.servlets.wrappers;

import org.ttdc.gwt.client.beans.GTag;

public class TagWrapper {
	private GTag delegate;

	public TagWrapper(GTag tag) {
		delegate = tag;
	}

	public String getTagId() {
		return delegate.getTagId();
	}

	public String getType() {
		return delegate.getType();
	}

}
