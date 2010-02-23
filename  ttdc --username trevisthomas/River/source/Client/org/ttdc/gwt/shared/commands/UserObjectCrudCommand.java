package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.beans.GUserObject;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;

public class UserObjectCrudCommand extends Command<GenericCommandResult<GUserObject>>{
	private ActionType action;
	private String value;
	private String type;
	private String templateId;
	private String tagId; //For muted / filtered stuff...
	private String objectId;
	
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public ActionType getAction() {
		return action;
	}
	public void setAction(ActionType action) {
		this.action = action;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getTagId() {
		return tagId;
	}
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
	
}
