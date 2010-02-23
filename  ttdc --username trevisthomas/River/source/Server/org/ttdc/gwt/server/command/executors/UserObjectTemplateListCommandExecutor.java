package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;

import java.util.List;

import org.apache.log4j.Logger;
import org.ttdc.gwt.client.beans.GUserObjectTemplate;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.UserObjectTemplateDao;
import org.ttdc.gwt.shared.commands.UserObjectTemplateListCommand;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.UserObjectTemplate;

public class UserObjectTemplateListCommandExecutor extends CommandExecutor<GenericListCommandResult<GUserObjectTemplate>>{
	private final static Logger log = Logger.getLogger(UserObjectTemplateListCommandExecutor.class);

	@Override
	protected CommandResult execute() {
		UserObjectTemplateListCommand cmd = (UserObjectTemplateListCommand)getCommand();
		GenericListCommandResult<GUserObjectTemplate> result = null;
		try{
			beginSession();
			switch(cmd.getAction()){
			case GET_ALL_OF_TYPE:
				result = getAllOfType(cmd.getTemplateType());	
				break;
			case GET_AVAILABLE_FOR_USER:
				result = getAvailableForUser(getPerson(),cmd.getTemplateType());
				break;
			default:
				throw new RuntimeException("UserObjectTemplateListCommandExecutor doesnt know what to do with this action type.");
			}
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

	private GenericListCommandResult<GUserObjectTemplate> getAvailableForUser(Person person, String type) {
		List<UserObjectTemplate> list = UserObjectTemplateDao.loadAvailableForUser(person.getPersonId(), type);
		List<GUserObjectTemplate> gList = FastPostBeanConverter.convertUserObjectTemplateList(list);
		return new GenericListCommandResult<GUserObjectTemplate>(gList);
	}

	private GenericListCommandResult<GUserObjectTemplate> getAllOfType(String type) {
		List<UserObjectTemplate> list = UserObjectTemplateDao.loadTemplatesOfType(type);
		List<GUserObjectTemplate> gList = FastPostBeanConverter.convertUserObjectTemplateList(list);
		return new GenericListCommandResult<GUserObjectTemplate>(gList);
	}

}
