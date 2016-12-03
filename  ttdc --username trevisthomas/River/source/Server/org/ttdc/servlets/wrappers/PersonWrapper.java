package org.ttdc.servlets.wrappers;

import org.ttdc.gwt.client.beans.GPerson;

public class PersonWrapper {
	private GPerson delegate;

	public PersonWrapper(GPerson person) {
		this.delegate = person;
	}

	public String getPersonId() {
		return delegate.getPersonId();
	}

	public String getLogin() {
		return delegate.getLogin();
	}

	public ImageWrapper getImage() {
		if (delegate.getImage() != null) {
			return new ImageWrapper(delegate.getImage());
		} else {
			return null;
		}
	}

	// public let image: Image?
}
