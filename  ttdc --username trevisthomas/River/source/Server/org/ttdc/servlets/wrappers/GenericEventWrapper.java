package org.ttdc.servlets.wrappers;

import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.Event;

/*
 * Trevis, this horrible class is trying to compensate for the even more horrible Event<?,?> class which seemed like
 * way too big of a deal to be worth fixing.  What a horrible use of generics it is :-(
 */
public class GenericEventWrapper {
	private Event<?,?> delegate;
	
	public GenericEventWrapper(Event<?, ?> event) {
		delegate = event;
	}

	public String getType(){
		return delegate.getType().toString();
	}
	
	public PostWrapper getSourcePost(){
		if (delegate.getSource() instanceof GPost){
			return new PostWrapper((GPost)delegate.getSource());
		}
		else {
			return null;
		}
	}
	
	public PersonWrapper getSourcePerson() {
		if (delegate.getSource() instanceof GPerson) {
			return new PersonWrapper((GPerson) delegate.getSource());
		} else {
			return null;
		}
	}

	public TagAssociationWrapper getSourceTagAssociation() {
		if (delegate.getSource() instanceof GAssociationPostTag) {
			return new TagAssociationWrapper((GAssociationPostTag) delegate.getSource());
		} else {
			return null;
		}
	}
}
