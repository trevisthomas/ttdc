package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.AccountDao;
import org.ttdc.gwt.server.dao.ImageDao;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.server.dao.StyleDao;
import org.ttdc.gwt.server.dao.UserObjectDao;
import org.ttdc.gwt.shared.commands.AccountCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Style;
import org.ttdc.util.ApplicationProperties;

public class AccountCommandExecutor extends CommandExecutor<GenericCommandResult<GPerson>>{

	protected CommandResult execute() {
		GenericCommandResult<GPerson> result = null;
		try{
			beginSession();
			AccountCommand cmd = (AccountCommand)getCommand();
			if(cmd.getAction() == null)
				throw new RuntimeException("No action provided.  So, what do you want?");
			switch(cmd.getAction()){
				case CREATE:
					result = create(cmd);
					break;
				case UPDATE:
					result = update(cmd);
					break;
				case REQUEST_PASSWORD_RESET:
					result = requestPasswordReset(cmd);
					break;
				case RESET_PASSWORD:
					result = ResetPassword(cmd);
					break;
				case ENABLE_NWS:
					result = enableNws(cmd,getPerson().getPersonId());
					break;
				default:
					throw new RuntimeException("StyleCommandExecutor doesnt know that action");
			}
			commit();
		}
		catch(RuntimeException e){
			rollback();
			throw(e);
		}
		return result;
	}

	/*
	 * 
	 * If you want the enableNws method's refresh to work properly then the 
	 * person must have been created within the same session.  Otherwise, refreshing
	 * him wont reflect the delted user object! 
	 * 
	 * NEVER forget this!
	 * 
	 */
	private GenericCommandResult<GPerson> enableNws(AccountCommand cmd, String personId) {
		Person p = PersonDao.loadPerson(personId);  
		UserObjectDao.enableNws(p, cmd.isEnableNws());
		String msg;
		if(p.isNwsEnabled())
			msg = "NWS content is now enabled.";
		else
			msg = "NWS content is disabled.";
		
		GPerson gPerson = FastPostBeanConverter.convertPerson(p);
		return new GenericCommandResult<GPerson>(gPerson,msg);
		
	}

	private GenericCommandResult<GPerson> requestPasswordReset(AccountCommand cmd) {
		Person p = PersonDao.loadPersonByLogin(cmd.getLogin());
		if(p.isAdministrator())
			throw new RuntimeException("Suck it hacker!");
		else if(!p.getEmail().equals(cmd.getEmail())){
			throw new RuntimeException("Email of account doesnt match the one entered. You better email the admin for help.");
		}
		
		try{
			String url = ApplicationProperties.getProperty("URL");
			AccountDao.sendPasswordResetEmail(p,url+"/reset.jsp");
		}
		catch(Exception e){
			return new GenericCommandResult<GPerson>(null, "Instructions could not be sent, contact the admin.");
		}
		
		return new GenericCommandResult<GPerson>(null, "Instructions were sent to your email address.");
	}

	private GenericCommandResult<GPerson> ResetPassword(AccountCommand cmd) {
		Person p = AccountDao.resetPassword(getPerson().getPersonId(), cmd.getPassword());
		GPerson gPerson = FastPostBeanConverter.convertPerson(p);
		return new GenericCommandResult<GPerson>(gPerson,"Password reset successfully.");
	}

	private GenericCommandResult<GPerson> update(AccountCommand cmd) {
		AccountDao dao = new AccountDao();
		dao.setPersonId(getPerson().getPersonId());
		dao.setBio(cmd.getBio());
		dao.setBirthday(cmd.getBirthday());
		dao.setEmail(cmd.getEmail());
		dao.setName(cmd.getName());
		
		if(StringUtil.notEmpty(cmd.getImageId())){
			Image image = ImageDao.loadImage(cmd.getImageId());
			dao.setImage(image);
		}
		
		if(StringUtil.notEmpty(cmd.getStyleId())){
			Style style = StyleDao.load(cmd.getStyleId());
			dao.setStyle(style);
		}
			
		Person p = dao.update();
		GPerson gPerson = FastPostBeanConverter.convertPersonWithBio(p);
		return new GenericCommandResult<GPerson>(gPerson,"Account updated successfully.");
	}

	private GenericCommandResult<GPerson> create(AccountCommand cmd) {
		AccountDao dao = new AccountDao();
		dao.setBio(cmd.getBio());
		dao.setBirthday(cmd.getBirthday());
		dao.setEmail(cmd.getEmail());
		dao.setName(cmd.getName());
		dao.setLogin(cmd.getLogin());
		dao.setPassword(cmd.getPassword());
		Person p = dao.create();
		GPerson gPerson = FastPostBeanConverter.convertPerson(p);
		
		//TODO figure out where to put this...?
		
		try{
			String url = ApplicationProperties.getAppProperties().getProperty("URL");
			
			AccountDao.sendActivateionEmail(p,url+"/activate.jsp");
		}
		catch(Exception e){
			return new GenericCommandResult<GPerson>(gPerson,"Account Created, but email failed to be set. Contact the admin.");
		}
		
		return new GenericCommandResult<GPerson>(gPerson,"Account Created.");
	}
}
