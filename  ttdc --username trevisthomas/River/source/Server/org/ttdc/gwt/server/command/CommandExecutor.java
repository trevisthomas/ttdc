package org.ttdc.gwt.server.command;

import org.apache.log4j.Logger;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.dao.InitConstants;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;

abstract public class CommandExecutor<T extends CommandResult> {
	private final static Logger log = Logger.getLogger(CommandExecutor.class);
	private Command<T> command;
	private Person person;
	
	final public void initialize(String personId, Command<T> command){
		Persistence.beginSession();
		if(personId != null)
			person = PersonDao.loadPerson(personId);
		else
			person = InitConstants.ANONYMOUS;
		Persistence.commit();
		
		this.command = command; 
	}
	
	final public Person getPerson(){
		return person;
	}
	final protected Command<T> getCommand(){
		return command;
	}
	final public CommandResult executeCommand(){
		//log.debug("Executing: " + getCommand().getClass() + " for " +getPerson().getLogin());
		return execute();
	}
	abstract protected CommandResult execute();
}
