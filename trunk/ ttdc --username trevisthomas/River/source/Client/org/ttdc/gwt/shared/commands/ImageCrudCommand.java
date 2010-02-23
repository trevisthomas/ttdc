package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;

public class ImageCrudCommand extends Command<GenericCommandResult<GImage>>{
	private ActionType action;
	private String name;
	private String imageId;
	private String url; //for crud scraping
	
	public ImageCrudCommand() {}
	
	public ImageCrudCommand(ActionType action) {
		this.action = action;
	}

	public ActionType getAction() {
		return action;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
	
}
