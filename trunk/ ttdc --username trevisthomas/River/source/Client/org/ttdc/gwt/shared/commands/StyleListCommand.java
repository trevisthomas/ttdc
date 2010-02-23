package org.ttdc.gwt.shared.commands;

import java.util.List;

import org.ttdc.gwt.client.beans.GStyle;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;

public class StyleListCommand extends Command<GenericListCommandResult<GStyle>>{
	private List<GStyle> styleList;
	
	public StyleListCommand() {
	}
	
	public StyleListCommand(List<GStyle> styleList) {
		this.styleList = styleList; 
	}
	
	public List<GStyle> getStyleList() {
		return styleList;
	}
}
