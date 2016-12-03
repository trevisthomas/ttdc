package org.ttdc.servlets.wrappers;

import org.ttdc.gwt.client.beans.GImage;

public class ImageWrapper {
	private final GImage delegate;

	public ImageWrapper(GImage image) {
		this.delegate = image;
	}

	public String getName() {
		return delegate.getName();
	}

	public String getThumbnailName() {
		return delegate.getThumbnailName();
	}

}
