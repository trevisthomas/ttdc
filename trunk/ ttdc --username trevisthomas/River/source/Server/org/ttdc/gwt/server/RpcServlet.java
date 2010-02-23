package org.ttdc.gwt.server;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.client.services.RemoteServiceException;
import org.ttdc.gwt.client.services.RpcService;
import org.ttdc.gwt.server.activity.ServerEventBroadcaster;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.CommandExecutorFactory;
import org.ttdc.gwt.server.dao.AccountDao;
import org.ttdc.gwt.server.dao.InitConstants;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;

public class RpcServlet extends RemoteServiceSessionServlet implements RpcService{
	private static final long serialVersionUID = 1L;
	private final static Logger log = Logger.getLogger(RpcServlet.class);
	
	
	@SuppressWarnings("unchecked")
	public <T extends CommandResult> T execute(Command<T> command) throws RemoteServiceException {
		try{
			CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(getPersonIdFromSession(), command);
			CommandResult result = cmdexec.executeCommand();
			return (T)result;
		}
		catch (Exception e) {
			throw buildRemoteServiceException(e);
		}
	}
	
	public <T extends CommandResult> ArrayList<T> execute(ArrayList<Command<T>> actionList) throws RemoteServiceException{
		log.debug("Executing batch : " + actionList);
		ArrayList<CommandResult> results = new ArrayList<CommandResult>();
			
		for(Command<T> command : actionList){
			CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(getPersonIdFromSession(), command);
			CommandResult result = cmdexec.executeCommand();
			results.add(result);
		}
		return (ArrayList<T>)results;
	}
	
	public <T extends CommandResult> T authenticate(String login, String password) throws RemoteServiceException {
		try {
			Persistence.beginSession();
			Person person = AccountDao.login(login, password);
			GPerson gPerson = processNewlyAuthenticatedUser(person);
			Persistence.commit();
			
			PersonCommandResult result = new PersonCommandResult(gPerson);
			return (T)result;
		} catch (Throwable t) {
			throw buildRemoteServiceException(t);
		}
	}

	
	
	public <T extends CommandResult> T logout() throws RemoteServiceException {
		try {
			forgetActiveUser();
			Person person = InitConstants.ANONYMOUS;
			GPerson gPerson = FastPostBeanConverter.convertPerson(person);
			PersonCommandResult result = new PersonCommandResult(gPerson);
			
			return (T)result;
		} catch (Throwable t) {
			throw buildRemoteServiceException(t);
		}
	}
	
	public <T extends CommandResult> T login(String personId, String encryptedPassword) throws RemoteServiceException {
		try {
			//TODO: the personId is the one from the users cookie. 
			// if it's blank, try to load the user from session.
			Person person = super.loadOrCreateActiveUser();
			GPerson gPerson;
			if(person.isAnonymous() && StringUtil.notEmpty(personId) && StringUtil.notEmpty(encryptedPassword)){
				Persistence.beginSession();
				person = AccountDao.authenticate(personId, encryptedPassword);
				gPerson = processNewlyAuthenticatedUser(person);
				Persistence.commit();
			}
			else{
				gPerson = FastPostBeanConverter.convertPerson(person);
			}
			PersonCommandResult result = new PersonCommandResult(gPerson);
			return (T)result;
		} catch (Throwable t) {
			throw buildRemoteServiceException(t);
		}	
	}
	
	/**
	 * A wacky method that allows admin to assume someones identity
	 */
	@Override
	public <T extends CommandResult> T identity(String personId) throws RemoteServiceException {
		try {
			Person admin = super.loadOrCreateActiveUser();
			if(!admin.isAdministrator()) throw new RuntimeException("Suck it hacker!");
			GPerson gPerson;
			
			Persistence.beginSession();
			Person person = PersonDao.loadPerson(personId);
			gPerson = processNewlyAuthenticatedUser(person);
			Persistence.commit();
			
			PersonCommandResult result = new PersonCommandResult(gPerson);
			return (T)result;
		} catch (Throwable t) {
			throw buildRemoteServiceException(t);
		}	
	}
	
	private GPerson processNewlyAuthenticatedUser(Person person) {
		rememberActiveUser(person);
		AccountDao.userHit(person.getPersonId());
		GPerson gPerson = FastPostBeanConverter.convertPerson(person);
		PersonEvent event = new PersonEvent(PersonEventType.TRAFFIC,gPerson);
		ServerEventBroadcaster.getInstance().broadcastEvent(event);
		return gPerson;
	}
	
	@Override
	protected void doUnexpectedFailure(Throwable e) {
		getLogger().error(e);
	}

	
}
