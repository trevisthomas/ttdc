package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.beans.GStyle;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;

public class StyleCommand extends Command<GenericCommandResult<GStyle>>{
	private ActionType action;
	private String styleId;
	private String displayName;
	private String cssFileName;
	private String description;
	private boolean defaultStyle = false;
	
	public boolean isDefaultStyle() {
		return defaultStyle;
	}
	public void setDefaultStyle(boolean defaultStyle) {
		this.defaultStyle = defaultStyle;
	}
	public ActionType getAction() {
		return action;
	}
	public void setAction(ActionType action) {
		this.action = action;
	}
	public String getStyleId() {
		return styleId;
	}
	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getCssFileName() {
		return cssFileName;
	}
	public void setCssFileName(String cssFileName) {
		this.cssFileName = cssFileName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
