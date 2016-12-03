package org.ttdc.servlets.wrappers;

import java.util.Date;

import org.ttdc.gwt.client.beans.GAssociationPostTag;

public class TagAssociationWrapper {
	private GAssociationPostTag delegate;

	public TagAssociationWrapper(GAssociationPostTag ass) {
		delegate = ass;
	}

	// final String guid;
	// final Date date;
	// final PersonWrapper creator;
	// final TagWrapper tag;

	public String getGuid() {
		return delegate.getGuid();
	}

	public Date getDate() {
		return delegate.getDate();
	}

	public PersonWrapper getCreator() {
		return new PersonWrapper(delegate.getCreator());
	}

	public TagWrapper getTag() {
		return new TagWrapper(delegate.getTag());
	}

	// let post : Post? //Trevis, you added this for like... is it really needed?

}
