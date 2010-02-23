package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import org.ttdc.gwt.client.beans.GStyle;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.InitConstants;
import org.ttdc.gwt.server.dao.StyleDao;
import org.ttdc.gwt.shared.commands.StyleCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;
import org.ttdc.persistence.objects.Style;

public class StyleCommandExecutor extends CommandExecutor<GenericCommandResult<GStyle>>{

	@Override
	protected CommandResult execute() {
		GenericCommandResult<GStyle> result = null;
		try{
			beginSession();
			StyleCommand cmd = (StyleCommand)getCommand();
			if(cmd.getAction() == null)
				throw new RuntimeException("No action provided.  So, what do you want?");
			switch(cmd.getAction()){
				case CREATE:
					result = create(cmd);
					break;
				case DELETE:
					result = delete(cmd);
					break;
				case READ:
					result = read(cmd);
					break;
				case UPDATE:
					result = update(cmd);
					break;
				default:
					throw new RuntimeException("StyleCommandExecutor doesnt know that action");
			}
			commit();
			if(ActionType.UPDATE.equals(cmd.getAction()))
				InitConstants.refresh();//Just in case the default style changed.
		}
		catch(RuntimeException e){
			rollback();
			throw(e);
		}
		return result;
	}

	private GenericCommandResult<GStyle> update(StyleCommand cmd) {
		StyleDao dao = new StyleDao();
		dao.setCreatorId(getPerson().getPersonId());
		dao.setStyleId(cmd.getStyleId());
		dao.setCssFileName(cmd.getCssFileName());
		dao.setDescription(cmd.getDescription());
		dao.setDisplayName(cmd.getDisplayName());
		dao.setDefaultStyle(cmd.isDefaultStyle());
		
		Style style = dao.update();		
		
		GStyle gStyle = FastPostBeanConverter.convertStyle(style);
		return new GenericCommandResult<GStyle>(gStyle, gStyle.getDescription()+" has been updated.");
	}

	private GenericCommandResult<GStyle> read(StyleCommand cmd) {
		Style style = StyleDao.load(cmd.getStyleId());
		GStyle gStyle = FastPostBeanConverter.convertStyle(style);
		return new GenericCommandResult<GStyle>(gStyle, "");
	}

	private GenericCommandResult<GStyle> delete(StyleCommand cmd) {
		StyleDao.delete(cmd.getStyleId());
		return new GenericCommandResult<GStyle>(null, "Style has been updated.");
	}

	private GenericCommandResult<GStyle> create(StyleCommand cmd) {
		StyleDao dao = new StyleDao();
		dao.setCreatorId(getPerson().getPersonId());
		dao.setCssFileName(cmd.getCssFileName());
		dao.setDescription(cmd.getDescription());
		dao.setDisplayName(cmd.getDisplayName());
		dao.setDefaultStyle(false);
		Style style = dao.create();		
		GStyle gStyle = FastPostBeanConverter.convertStyle(style);
		return new GenericCommandResult<GStyle>(gStyle, gStyle.getDescription()+" has been created.");
	}
	
}
