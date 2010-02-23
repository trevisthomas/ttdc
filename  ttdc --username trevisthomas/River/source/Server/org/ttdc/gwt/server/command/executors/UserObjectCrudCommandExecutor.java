package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import org.ttdc.gwt.client.beans.GUserObject;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.server.dao.UserObjectDao;
import org.ttdc.gwt.server.dao.UserObjectTemplateDao;
import org.ttdc.gwt.shared.commands.UserObjectCrudCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.persistence.objects.UserObjectTemplate;

public class UserObjectCrudCommandExecutor extends CommandExecutor<GenericCommandResult<GUserObject>>{
	@Override
	protected CommandResult execute() {
		UserObjectCrudCommand cmd = (UserObjectCrudCommand)getCommand();
		GenericCommandResult<GUserObject> result = null;
		try{
			beginSession();
			switch(cmd.getAction()){
			case DELETE:
				result = deletePrivilege(cmd);
				break;
			case CREATE:
				result = createUserObject(cmd,getPerson());
				break;
			case READ:
				result = readUserObject(cmd);
				break;
			default:
				throw new RuntimeException("I cant do that action. Feel free to teach me though.");
			}
			return result;
		}
		catch(RuntimeException e){
			rollback();
			throw e;
		}
		finally{
			commit();
		}
	}

	private GenericCommandResult<GUserObject> deletePrivilege(UserObjectCrudCommand cmd) {
		UserObjectDao.delete(cmd.getObjectId());
		return new GenericCommandResult<GUserObject>(null,"User object deleted.");
	}

	private GenericCommandResult<GUserObject> readUserObject(UserObjectCrudCommand cmd) {
		UserObject uo = UserObjectDao.loadUserObject(cmd.getObjectId());
		return createResultsObject(uo);
	}

	/*
	 * A word of warning, this method is exclusively for creating userobject template based user 
	 * objects. You'll need to make changes to create other kinds of uo's
	 * 
	 * @param cmd
	 * @param person
	 * @return
	 */
	private GenericCommandResult<GUserObject> createUserObject(UserObjectCrudCommand cmd, Person person) {
		UserObjectTemplate template = UserObjectTemplateDao.load(cmd.getTemplateId());
		//validate url pattern here or on client, probably client huh?
		UserObject uo = UserObjectDao.createWebLinkFromTemplate(person, template, cmd.getValue());
		return createResultsObject(uo);
	}

	private GenericCommandResult<GUserObject> createResultsObject(UserObject uo) {
		GUserObject gUserObject = FastPostBeanConverter.convertUserObject(uo);
		Person owner = PersonDao.loadPerson(getPerson().getPersonId());
		gUserObject.setOwner(FastPostBeanConverter.convertPersonWithBio(owner));
		
		return new GenericCommandResult<GUserObject>(gUserObject,"successfully created user object");
	}

}
