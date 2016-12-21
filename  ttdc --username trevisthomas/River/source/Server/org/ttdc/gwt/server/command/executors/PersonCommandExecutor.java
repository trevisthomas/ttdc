package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.executors.utils.ExecutorHelpers;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.server.dao.PrivilegeDao;
import org.ttdc.gwt.shared.commands.PersonCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Privilege;

public class PersonCommandExecutor extends CommandExecutor<GenericCommandResult<GPerson>>{
	@Override
	protected CommandResult execute() {
		PersonCommand cmd = (PersonCommand)getCommand();
		
		GenericCommandResult<GPerson> result = null;
		try{
			String personId = cmd.getPersonId();
			beginSession();
			
			switch(cmd.getType()){
				case ACTIVATE:
					PersonDao.activate(personId);
					break;
				case DEACTIVATE:
					PersonDao.deactivate(personId);
					break;
				case LOCK:
					PersonDao.lock(personId);
					break;
				case UNLOCK:
					PersonDao.unlock(personId);
					break;
				case GRANT_PRIVILEGE:
					grantPrivilege(personId, cmd.getPrivilegeId());
					break;
				case REVOKE_PRIVILEGE:
					revokePrivilege(personId, cmd.getPrivilegeId());
					break;	
				case MARK_SITE_READ:
					markSiteRead(personId);
					break;
				case LOAD:
					break;
				default:
					throw new RuntimeException("PersonUpdateCommandExecutor doesnt know what to do.");
			}
			
			Person person = PersonDao.loadPerson(personId);
			//GPerson gPerson = FastPostBeanConverter.convertPerson(person);
			GPerson gPerson = FastPostBeanConverter.convertPersonWithBio(person);
			
			result = new GenericCommandResult<GPerson>(gPerson, gPerson.getLogin() + "'s access was successful.");
			
		}
		catch(RuntimeException e){
			rollback();
			throw e;
		}
		finally{
			commit();
		}
		
		return result;
	}

	private void markSiteRead(String personId) {
		Person person = getPerson();//New style! (remember, older versions were not created in session
		if(person.isAnonymous())
			throw new RuntimeException("Anonymous users cant mark the site read.");
		
		PersonDao.markSiteRead(getPerson().getPersonId());
		ExecutorHelpers.broadcastMarkSiteRead(getPerson(), getCommand().getConnectionId());
	}

	private void grantPrivilege(String personId, String privilegeId) {
		Person person = PersonDao.loadPerson(personId); 
		Privilege privilege = PrivilegeDao.loadPrivilege(privilegeId);
		PersonDao.grantPrivilege(person.getPersonId(), privilege);
	}
	
	private void revokePrivilege(String personId, String privilegeId) {
		Person person = PersonDao.loadPerson(personId); 
		Privilege privilege = PrivilegeDao.loadPrivilege(privilegeId);
		PersonDao.revokePrivilege(person.getPersonId(), privilege);
	}
}
