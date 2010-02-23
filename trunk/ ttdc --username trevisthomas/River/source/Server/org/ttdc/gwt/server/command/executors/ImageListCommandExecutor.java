package org.ttdc.gwt.server.command.executors;

import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.executors.utils.PaginatedResultConverters;
import org.ttdc.gwt.server.dao.ImageDao;
import org.ttdc.gwt.shared.commands.ImageListCommand;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Image;

public class ImageListCommandExecutor extends CommandExecutor<PaginatedListCommandResult<GImage>>{

	@Override
	protected CommandResult execute() {
		try{
			Persistence.beginSession(); 
			ImageListCommand cmd = (ImageListCommand)getCommand();
			ImageDao dao = new ImageDao();
			dao.setCurrentPage(cmd.getCurrentPage());
			PaginatedList<Image> results = dao.loadAll();
			
			PaginatedList<GImage> gResuts = PaginatedResultConverters.convertImageList(results);
			
			PaginatedListCommandResult<GImage> cmdResult  = new PaginatedListCommandResult<GImage>(gResuts);
			return cmdResult;
		}
		finally{
			Persistence.commit();
		}
	}

}
