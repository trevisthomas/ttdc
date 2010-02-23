package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;

public class ImageListCommand extends Command<PaginatedListCommandResult<GImage>>{
	private int currentPage = 1;

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
}
