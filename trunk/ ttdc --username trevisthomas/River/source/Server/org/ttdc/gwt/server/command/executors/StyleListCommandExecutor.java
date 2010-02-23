package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;

import java.util.List;

import org.apache.log4j.Logger;
import org.ttdc.gwt.client.beans.GStyle;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.StyleDao;
import org.ttdc.gwt.shared.commands.StyleListCommand;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;
import org.ttdc.persistence.objects.Style;

public class StyleListCommandExecutor extends CommandExecutor<GenericListCommandResult<GStyle>>{
	private final static Logger log = Logger.getLogger(StyleListCommandExecutor.class);

	@Override
	protected CommandResult execute() {
		StyleListCommand cmd = (StyleListCommand)getCommand();
		GenericListCommandResult<GStyle> result = null;
		try{
			beginSession();
			List<Style> list = StyleDao.loadAll();
			List<GStyle> gList = FastPostBeanConverter.convertStyles(list); 
			result = new GenericListCommandResult<GStyle>(gList); 
		}
		catch(RuntimeException e){
			log.error(e);
			throw(e);
		}
		finally{
			commit();
		}
		return result;
	}

}
