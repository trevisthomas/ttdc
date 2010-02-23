package org.ttdc.gwt.server.command.executors;

import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.ImageDao;
import org.ttdc.gwt.server.dao.ImageDataDao;
import org.ttdc.gwt.shared.commands.ImageCrudCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.ImageFull;

import static org.ttdc.persistence.Persistence.*;

public class ImageCrudCommandExecutor extends CommandExecutor<GenericCommandResult<GImage>>{
	@Override
	protected CommandResult execute() {
		ImageCrudCommand cmd = (ImageCrudCommand)getCommand();
		GenericCommandResult<GImage> result = null;
		try{
			beginSession();
			switch(cmd.getAction()){
			case DELETE:
				result = deleteImage(cmd);
				break;
			case UPDATE:
				result = renameImage(cmd);
				break;
			case CREATE:
				result = createImage(cmd);
				break;
			default:
				throw new RuntimeException("I cant do that action. Feel free to teach me though.");
			}
			commit();
			return result;
		}
		catch(RuntimeException e){
			rollback();
			throw e;
		}
	}

	private GenericCommandResult<GImage> createImage(ImageCrudCommand cmd) {
		GenericCommandResult<GImage> result;
		ImageDataDao dao = new ImageDataDao(getPerson());
		ImageFull image = dao.createImage(cmd.getUrl(), cmd.getName());
		session().save(image);
		GImage gImage = FastPostBeanConverter.convertImage(image);
		result = new GenericCommandResult<GImage>(gImage, "Successfully grabbed image from URL.");
		return result;
	}

	private GenericCommandResult<GImage> deleteImage(ImageCrudCommand cmd) {
		GenericCommandResult<GImage> result;
		ImageDao dao = new ImageDao();
		dao.setImageId(cmd.getImageId());
		dao.delete();
		result = new GenericCommandResult<GImage>(null, "Successfully deleted image.");
		return result;
	}

	private GenericCommandResult<GImage> renameImage(ImageCrudCommand cmd) {
		GenericCommandResult<GImage> result;
		ImageDao dao = new ImageDao();
		dao.setImageId(cmd.getImageId());
		Image image = dao.rename(cmd.getName());
		GImage gImage = FastPostBeanConverter.convertImage(image);
		
		result = new GenericCommandResult<GImage>(gImage, "Successfully renamed image.");
		return result;
	}
}
