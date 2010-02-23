package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.beans.GUserObjectTemplate;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;

public class UserObjectTemplateListCommand extends Command<GenericListCommandResult<GUserObjectTemplate>>{
	public enum UserObjectTemplateListAction {GET_ALL_OF_TYPE, GET_AVAILABLE_FOR_USER}
	private String templateType;
	private UserObjectTemplateListAction action;
	
	public UserObjectTemplateListAction getAction() {
		return action;
	}
	public void setAction(UserObjectTemplateListAction action) {
		this.action = action;
	}
	public String getTemplateType() {
		return templateType;
	}
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
}
