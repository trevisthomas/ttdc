package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GUserObject;
import org.ttdc.gwt.client.constants.UserObjectConstants;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.server.dao.UserObjectDao;
import org.ttdc.gwt.server.dao.UserObjectTemplateDao;
import org.ttdc.gwt.shared.commands.UserObjectCrudCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.util.StringUtil;
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
				result = removeUserObject(cmd);
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

	private GenericCommandResult<GUserObject> removeUserObject(UserObjectCrudCommand cmd) {
		validateNonAnonUser();
		
		if(UserObjectConstants.TYPE_WEBPAGE.equals(cmd.getType())){
			UserObjectDao.delete(cmd.getObjectId());
		}
		else if(UserObjectConstants.TYPE_FILTER_THREAD.equals(cmd.getType())){
			UserObjectDao.removeThreadFilter(getPerson(), cmd.getValue());
			
			//Fire an event notifying interested users that things have changed with the underlying post
		}
		else{
			UserObjectDao.removeUserObject(getPerson(), cmd.getType(), cmd.getValue());
		}
//		else{
//			throw new RuntimeException("Command is not properly formed.");
//		}
		
		GPerson gPerson = FastPostBeanConverter.convertPerson(getPerson());
		GUserObject uo = new GUserObject();
		uo.setOwner(gPerson);
		return new GenericCommandResult<GUserObject>(uo,"User object deleted.");
	}

	private void validateNonAnonUser() {
		if(getPerson().isAnonymous()){
			throw new RuntimeException("You are not logged in.");
		}
	}

	private GenericCommandResult<GUserObject> readUserObject(UserObjectCrudCommand cmd) {
		UserObject uo = UserObjectDao.loadUserObject(cmd.getObjectId());
		return createResultsObject(uo);
	}

	/*
	 * A word of warning, this method is exclusively for creating userobject template based user 
	 * objects. You'll need to make changes to create other kinds of uo's
	 * 
	 * 5/18 - modifying to handle other types
	 * 
	 * @param cmd
	 * @param person
	 * @return
	 */
	private GenericCommandResult<GUserObject> createUserObject(UserObjectCrudCommand cmd, Person person) {
		validateNonAnonUser();
		UserObject uo = null;
		if(UserObjectConstants.TYPE_WEBPAGE.equals(cmd.getType())){
			UserObjectTemplate template = UserObjectTemplateDao.load(cmd.getTemplateId());
			//validate url pattern here or on client, probably client huh?
			uo = UserObjectDao.createWebLinkFromTemplate(person, template, cmd.getValue());
		}
		else if(UserObjectConstants.TYPE_FILTER_THREAD.equals(cmd.getType())){
			if(!UserObjectDao.loadFilteredThreadIds(person.getPersonId()).contains(cmd.getValue()))
				uo = UserObjectDao.createThreadFilter(person, cmd.getValue());
			else
				uo = null;
		}
		else if(UserObjectConstants.TYPE_FRONTPAGE_MODE.equals(cmd.getType())){
			//If this works, rename this method to create/update
			uo = UserObjectDao.updateUserSetting(person, cmd.getType(), cmd.getValue());
		}
		else if(StringUtil.notEmpty(cmd.getType()) && StringUtil.notEmpty(cmd.getValue()) ){
			uo = UserObjectDao.updateUserSetting(person, cmd.getType(), cmd.getValue());
		}
		if(uo == null)
			throw new RuntimeException("User object was not created.  Code may not be prepared to handle requested type.");
		return createResultsObject(uo);
	}

	private GenericCommandResult<GUserObject> createResultsObject(UserObject uo) {
		GUserObject gUserObject = FastPostBeanConverter.convertUserObject(uo);
		Person owner = PersonDao.loadPerson(getPerson().getPersonId());
		gUserObject.setOwner(FastPostBeanConverter.convertPersonWithBio(owner));
		
		return new GenericCommandResult<GUserObject>(gUserObject,"successfully created user object");
	}

}
