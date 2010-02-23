package org.ttdc.gwt.server.command.executors;

import org.ttdc.biz.network.services.UserService;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.beanconverters.GenericBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.shared.commands.GetPersonDetailsCommand;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;

@Deprecated 
public class PersonDetailsCommandExecutor extends CommandExecutor<PersonCommandResult>{
	@Override
	protected CommandResult execute() {
		
		throw new RuntimeException("deprecated - use PersonCommand");
//		try{
//			GetPersonDetailsCommand command = (GetPersonDetailsCommand)getCommand();
//			Persistence.beginSession();
//			Person p = PersonDao.loadPerson(command.getPersonId());
//			GPerson gPerson = FastPostBeanConverter.convertPerson(p);
//			return new PersonCommandResult(gPerson);
//		}
//		catch(RuntimeException e){
//			Persistence.commit();
//			throw e;
//		}
		
			
	}
//			try {
//			UserService service = UserService.getInstance();
//			Person person = null;
//			GetPersonDetailsCommand command = (GetPersonDetailsCommand)getCommand();
//			person = service.readPerson(command.getPersonId());
//			GPerson gPerson = GenericBeanConverter.convertPerson(person);
//			return new PersonCommandResult(gPerson);
//		} catch (Throwable t) {
//			throw new RuntimeException(t);
//		}
//	}

}
