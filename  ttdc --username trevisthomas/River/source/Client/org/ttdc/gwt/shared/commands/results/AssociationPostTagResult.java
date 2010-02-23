package org.ttdc.gwt.shared.commands.results;

import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.services.CommandResult;

/**
 * Result class for creating or removing a tag association. 
 * 
 */
public class AssociationPostTagResult implements CommandResult {
	public enum Status {CREATE, REMOVE, ERROR};
	private Status status;
	private String message; 
	private GAssociationPostTag associationPostTag;
	
	public AssociationPostTagResult() { status = Status.ERROR; }
	
	public AssociationPostTagResult(Status status) {
		this.status = status;
	}
	
	public boolean isPassed() {
		return !status.equals(Status.ERROR) ? true : false;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public GAssociationPostTag getAssociationPostTag() {
		return associationPostTag;
	}

	public void setAssociationPostTag(GAssociationPostTag associationPostTag) {
		this.associationPostTag = associationPostTag;
	}

	public boolean isRemove() {
		return status.equals(Status.REMOVE) ? true : false;
	}

	public boolean isCreate() {
		return status.equals(Status.CREATE) ? true : false;
	}
}	
