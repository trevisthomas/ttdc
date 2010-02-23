package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.beans.GUserObjectTemplate;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;

public class UserObjectTemplateCommand extends Command<GenericCommandResult<GUserObjectTemplate>>{
	private ActionType action;
	private String templateId;
	private String imageId;
	private String displayName;
	private String templateType;
	private String templateValue;
	
	public ActionType getAction() {
		return action;
	}
	public void setAction(ActionType action) {
		this.action = action;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getTemplateType() {
		return templateType;
	}
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
	public String getTemplateValue() {
		return templateValue;
	}
	public void setTemplateValue(String templateValue) {
		this.templateValue = templateValue;
	}
}
